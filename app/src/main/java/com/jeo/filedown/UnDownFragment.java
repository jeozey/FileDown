package com.jeo.filedown;


import android.content.Context;
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

import com.jeo.downlibrary.DownLoadManager;
import com.jeo.downlibrary.DownLoadTask;
import com.jeo.filedown.eu.erikw.PullToRefreshListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class UnDownFragment extends Fragment {
    private final static String TAG = UnDownFragment.class.getName();
    private int mScrollState;
    private PullToRefreshListView listView;
    private DownAdapter adapter;

    public UnDownFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View parent = inflater.inflate(R.layout.fragment_undown, null);
        if (listView == null) {
            listView = (PullToRefreshListView) parent.findViewById(R.id.pull_to_refresh_listview);
            initView();

        }
        return parent;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    private void initView() {
        // Set a listener to be invoked when the list should be refreshed.
        listView.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {

            @Override
            public void onRefresh() {
                // Your code to refresh the list contents

                List<String> urls;
                urls = Arrays.asList(Constants.URLS);
                List<DownLoadTask> files = new ArrayList<>();
                for (String url : urls) {
                    DownLoadTask item = new DownLoadTask();
                    item.setUrl(url);
                    files.add(item);
                }
                adapter.setFiles(files);
                adapter.notifyDataSetChanged();

                // Make sure you call listView.onRefreshComplete()
                // when the loading is done. This can be done from here or any
                // other place, like on a broadcast receive from your loading
                // service or the onPostExecute of your AsyncTask.

                listView.onRefreshComplete();
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

        List<DownLoadTask> tasks = historyDownLoadTask();
        adapter = new DownAdapter(tasks, getActivity().getBaseContext(), new MyHandler());
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

        // Update only when we're not scrolling, and only for visible views
//        if (mScrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
//            int start = listView.getFirstVisiblePosition();
//            for(int i = start, j = listView.getLastVisiblePosition(); i<=j; i++) {
//                View view = listView.getChildAt(i-start);
//                listView.getAdapter().getView(i, view, listView); // Tell the adapter to update this view
//            }
//        }
    }

}
