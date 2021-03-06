package com.jeo.filedown;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.jeo.downlibrary.DownLoadManager;
import com.jeo.downlibrary.DownLoadTask;
import com.jeo.filedown.eu.erikw.PullToRefreshListView;
import com.jeo.filedown.util.Constants;
import com.jeo.filedown.util.SpeedUtil;
import com.jeo.filedown.util.ViewHolderUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class UnDownFragment extends Fragment implements View.OnClickListener{
    private final static String TAG = UnDownFragment.class.getName();
    private int mScrollState;
    private PullToRefreshListView listView;
    private DownAdapter adapter;
    private List<DownLoadTask> tasks;
    private Button downAllBtn;

    public UnDownFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, "onCreate:" + (listView == null));
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e(TAG, "onCreateView:" + (listView == null));
        View parent = inflater.inflate(R.layout.fragment_undown, null);
        if (listView == null) {
            listView = (PullToRefreshListView) parent.findViewById(R.id.pull_to_refresh_listview);
            downAllBtn = (Button)parent.findViewById(R.id.downAllBtn);
            downAllBtn.setOnClickListener(this);
            initView();

        }
        return parent;
    }

    @Override
    public void onAttach(Context context) {
        Log.e(TAG, "onAttach:" + (listView == null));
        super.onAttach(context);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.downAllBtn:
                for (DownLoadTask task: tasks){
                    task.setStartAll(true);
                }
                adapter.notifyDataSetChanged();
                break;
            default:
                break;
        }
    }

    class GetJsonFromServer extends AsyncTask<String, Integer, String> {
        private HttpURLConnection initConnection(String url) throws IOException {
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            conn.setRequestMethod("GET");
            conn.setUseCaches(true);
            return conn;
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                HttpURLConnection conn = initConnection(params[0]);
                conn.connect();
                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    InputStream is = conn.getInputStream();
                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                    int len = 0;
                    byte[] buffer = new byte[1024];
                    while ((len = is.read(buffer)) != -1) {
                        os.write(buffer, 0, len);
                    }
                    String content = os.toString();
                    return content;
                } else {
                    return null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                listView.onRefreshComplete();


                if (!TextUtils.isEmpty(s)) {
                    JSONArray array = new JSONArray(s);
                    int len = array.length();
                    for (int i = 0; i < len; i++) {
                        JSONObject json = (JSONObject) array.get(i);
                        DownLoadTask item = new DownLoadTask();
                        String name = json.getString("name");
                        item.setName(name);
                        item.setUrl(Constants.SERVER_URL + "PhoneInfo/file/" + name);
                        item.setMd5(json.getString("md5"));
                        if (!tasks.contains(item)) {
                            tasks.add(item);
                        }
                    }
                    adapter.setFiles(tasks);

                    adapter.notifyDataSetChanged();

                }else{
                    Toast.makeText(getActivity(), "没有获取到数据", Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "数据解析出错", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void initView() {

        // Set a listener to be invoked when the list should be refreshed.
        listView.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {

            @Override
            public void onRefresh() {
                // Your code to refresh the list contents

                new GetJsonFromServer().execute(Constants.SERVER_LIST_URL);

            }
        });

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                mScrollState = scrollState;

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }

        });

        tasks = historyDownLoadTask();
        adapter = new DownAdapter(tasks, getActivity(), new MyHandler());
        listView.setAdapter(adapter);
    }

    private List<DownLoadTask> historyDownLoadTask() {
        List<DownLoadTask> tasks = new ArrayList<>();
        List<DownLoadTask> tmpTasks = null;
        tmpTasks = DownLoadManager.getInstance().getAllDownLoadTask();

        for (DownLoadTask task : tmpTasks
                ) {
            if (DownLoadTask.STATUS_FINISH == task.getStatus()) {
                continue;
            }
            if (DownLoadTask.STATUS_RUNNING == task.getStatus()) {
                task.setStatus(DownLoadTask.STATUS_PENDDING);
            }
            //出错的情况,重下
            if (task.getFinishSize() > task.getAllSize()) {
                task.setFinishSize(0);
            }
            tasks.add(task);

        }
        return tasks;
    }

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.MSG_DOWN_FILE_PROGRESS:
                    DownLoadTask item = (DownLoadTask) msg.obj;
                    updateProgressBar(item);
                    break;
            }
        }
    }

    //更新过程有可能刷新列表，导致位置变化
    private int getRightPosition(DownLoadTask task) {
        int i = 0;
        for (DownLoadTask t : adapter.getFiles()) {
            if (task.getUrl().equals(t.getUrl())) {
                return i;
            }
            i++;

        }
        return -1;
    }

    //http://blog.csdn.net/nupt123456789/article/details/39432781  ListView的局部刷新
    private void updateProgressBar(DownLoadTask task) {
        if (task == null) {
            Log.e(TAG, "fileItem is null");
            return;
        }
//        if (mScrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
            int firstVisiablePosition = listView.getFirstVisiblePosition();
            int lastVisiablePosition = listView.getLastVisiblePosition();
//            int position = task.getPosition();
            int position = getRightPosition(task);
//            Log.e(TAG, "first:" + firstVisiablePosition + " last:" + lastVisiablePosition + " pos:" + position);

            if (position >= firstVisiablePosition-1 && position <= lastVisiablePosition+1) {
                View view = listView.getChildAt(position - firstVisiablePosition + 1);
                if (view!=null&&view.getTag() instanceof DownAdapter.ViewHolder) {
                    DownAdapter.ViewHolder holder = (DownAdapter.ViewHolder) view.getTag();
                    ProgressBar progressBar = holder.downProgressBar;
                    progressBar.setMax(100);
                    if (task.getAllSize() != 0) {
                        int percent = (int) ((100 * task.getFinishSize()) / task.getAllSize());
                        progressBar.setProgress(percent);
                        holder.downPercent.setText(percent + "%");

                        holder.downSpeed.setText(SpeedUtil.getSpeed(task.getSpeed()));
                    }
                    Log.e(TAG, "curr:" + progressBar.getProgress() + " max:" + progressBar.getMax() + " all:" + task.getAllSize());
                    DownLoadTask t = (DownLoadTask) adapter.getItem(position);
                    t.setFinishSize(t.getFinishSize());

                    if (progressBar.getProgress() == 100) {
                        //为什么这句没有直接调用getView
//                        listView.getAdapter().getView(position, view, listView); // Tell the adapter to update this view
//                   adapter.notifyDataSetChanged();
                        ViewHolderUtil.updateViewHolder(t, holder);
                    }
                }
            } else {
                Log.e(TAG, "Error position!");
            }
//        }

    }

}
