package com.jeo.filedown;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.jeo.downlibrary.DownLoadManager;
import com.jeo.downlibrary.DownLoadTask;
import com.jeo.filedown.eu.erikw.PullToRefreshListView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * A simple {@link Fragment} subclass.
 */
public class HasDownFragment extends Fragment implements View.OnClickListener {


    private final static String TAG = UnDownFragment.class.getName();
    private PullToRefreshListView listView;
    private DownAdapter adapter;
    private List<DownLoadTask> tasks;
    private Button delAllBtn;

    public HasDownFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, "onCreate:" + (listView == null));
        super.onCreate(savedInstanceState);

        //注册EventBus
        EventBus.getDefault().register(this);

    }

    public void onEvent(MessageEvent event) {
        Log.e(TAG, "onEvent");
        if (adapter != null) {
            tasks.add(event.getTask());
            adapter.notifyDataSetChanged();
        }
    }

    ;

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onResume() {
        Log.e(TAG, "onResume:" + (listView == null));
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.delAllBtn:
                deleteAllDownTask();
                break;
            default:
                break;
        }
    }

    private void deleteAllDownTask() {
        new AlertDialog.Builder(getActivity()).setTitle("是否清空所有已下载文件?").setNegativeButton("否", null).setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    for (DownLoadTask task : tasks) {
                        File file = new File(task.getPath());
                        if (file.exists()) {
                            file.delete();
                            DownLoadManager.getInstance().removeTaskAndDel(task);

                        }
                    }
                    tasks.clear();
                    adapter.notifyDataSetChanged();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).show();
    }

    class GetFinishTasks extends AsyncTask<Void, Integer, Integer> {
        @Override
        protected Integer doInBackground(Void... params) {
            List<DownLoadTask> tmpTasks = historyDownLoadTask();
            for (DownLoadTask t :
                    tmpTasks) {
                if (!tasks.contains(t)) {
                    tasks.add(t);
                }
            }

            return 0;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            listView.onRefreshComplete();

            adapter.notifyDataSetChanged();

            super.onPostExecute(integer);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e(TAG, "onCreateView:" + (listView == null));
        View parent = inflater.inflate(R.layout.fragment_hasdown, null);
        if (listView == null) {
            delAllBtn = (Button) parent.findViewById(R.id.delAllBtn);
            delAllBtn.setOnClickListener(this);
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

    private void initView() {
        // Set a listener to be invoked when the list should be refreshed.
        listView.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {

            @Override
            public void onRefresh() {
                // Your code to refresh the list contents
                new GetFinishTasks().execute();
            }
        });
        tasks = new ArrayList<>();
        adapter = new DownAdapter(tasks, getActivity(), null);
        listView.setAdapter(adapter);

        new GetFinishTasks().execute();
    }

    private List<DownLoadTask> historyDownLoadTask() {
        List<DownLoadTask> tasks = new ArrayList<>();
        List<DownLoadTask> tmpTasks = null;
        tmpTasks = DownLoadManager.getInstance().getAllDownLoadTask();

        for (DownLoadTask task : tmpTasks
                ) {
            if (DownLoadTask.STATUS_FINISH == task.getStatus()) {
                tasks.add(task);
            }
        }
        return tasks;
    }


}
