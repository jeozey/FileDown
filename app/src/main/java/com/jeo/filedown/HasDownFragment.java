package com.jeo.filedown;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jeo.downlibrary.DownLoadManager;
import com.jeo.downlibrary.DownLoadTask;
import com.jeo.filedown.eu.erikw.PullToRefreshListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class HasDownFragment extends Fragment {


    private final static String TAG = UnDownFragment.class.getName();
    private PullToRefreshListView listView;
    private DownAdapter adapter;
    private List<DownLoadTask> tasks;

    public HasDownFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, "onCreate:" + (listView == null));
        super.onCreate(savedInstanceState);
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
