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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.jeo.downlibrary.DownLoadManager;
import com.jeo.downlibrary.DownLoadTask;
import com.jeo.filedown.eu.erikw.PullToRefreshListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class UnDownFragment extends Fragment {
    private final static String TAG = UnDownFragment.class.getName();
    private static final String SERVER_URL = "http://192.168.155.1:8080/";
    private static final String SERVER_LIST_URL = SERVER_URL+"PhoneInfo/dv_getFileDown.apk";
    private int mScrollState;
    private PullToRefreshListView listView;
    private DownAdapter adapter;
    private List<DownLoadTask> tasks;

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
            initView();

        }
        return parent;
    }

    @Override
    public void onAttach(Context context) {
        Log.e(TAG, "onAttach:" + (listView == null));
        super.onAttach(context);

    }

    class GetJsonFromServer extends AsyncTask<String,Integer,String>{
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
                if(conn.getResponseCode()==HttpURLConnection.HTTP_OK) {
                    InputStream is = conn.getInputStream();
                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                    int len = 0;
                    byte[] buffer = new byte[1024];
                    while ((len=is.read(buffer))!=-1){
                        os.write(buffer,0,len);
                    }
                    String content = os.toString();
                    return content;
                }else{
                    return null;
                }
            }catch (Exception e){
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {



            if(!TextUtils.isEmpty(s)) {
                JSONArray array = new JSONArray(s);
                int len = array.length();
                for (int i = 0;i<len;i++){
                    JSONObject json = (JSONObject)array.get(i);
                    DownLoadTask item = new DownLoadTask();
                    item.setUrl(SERVER_URL+json.getString("name"));
                    item.setMd5(json.getString("md5"));
                    if (!tasks.contains(item)) {
                        tasks.add(item);
                    }
                }
                List<String> urls;
                urls = Arrays.asList(Constants.URLS);
                for (String url : urls) {


                }
                adapter.setFiles(tasks);

                adapter.notifyDataSetChanged();
                listView.onRefreshComplete();
            }
            }catch (JSONException e){
                e.printStackTrace();
                Toast.makeText(getActivity(),"数据解析出错",Toast.LENGTH_LONG).show();
            }
        }
    }

    private void initView() {
        // Set a listener to be invoked when the list should be refreshed.
        listView.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {

            @Override
            public void onRefresh() {
                // Your code to refresh the list contents

                new GetJsonFromServer().execute(SERVER_LIST_URL);

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
        if (mScrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
            int firstVisiablePosition = listView.getFirstVisiblePosition();
            int lastVisiablePosition = listView.getLastVisiblePosition();
//            int position = task.getPosition();
            int position = getRightPosition(task);
            Log.e(TAG, "first:" + firstVisiablePosition + " last:" + lastVisiablePosition + " pos:" + position);

            if (position >= firstVisiablePosition && position <= lastVisiablePosition) {
                View view = listView.getChildAt(position - firstVisiablePosition + 1);
                if (view.getTag() instanceof DownAdapter.ViewHolder) {
                    DownAdapter.ViewHolder holder = (DownAdapter.ViewHolder) view.getTag();
                    ProgressBar progressBar = holder.downProgressBar;
                    progressBar.setMax(100);
                    if (task.getAllSize() != 0) {
                        int percent = (int) ((100 * task.getFinishSize()) / task.getAllSize());
                        progressBar.setProgress(percent);
                        holder.downPercent.setText(percent + "%");
                    }
                    Log.e(TAG, "curr:" + progressBar.getProgress() + " max:" + progressBar.getMax() + " all:" + task.getAllSize());
                    DownLoadTask file = (DownLoadTask) adapter.getItem(position);
                    file.setFinishSize(task.getFinishSize());

                    if (progressBar.getProgress() == 100) {
                        //为什么这句没有直接调用getView
//                        listView.getAdapter().getView(position, view, listView); // Tell the adapter to update this view
//                   adapter.notifyDataSetChanged();
                        ViewHolderUtil.updateViewHolder(task, holder);
                    }
                }
            } else {
                Log.e(TAG, "Error position!");
            }
        }

    }

}
