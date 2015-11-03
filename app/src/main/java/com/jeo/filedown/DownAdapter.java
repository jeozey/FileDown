package com.jeo.filedown;

import android.content.Context;
import android.content.res.Resources;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jeo.downlibrary.DownLoadListener;
import com.jeo.downlibrary.DownLoadManager;
import com.jeo.downlibrary.DownLoadTask;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 志文 on 2015/10/30 0030.
 */
public class DownAdapter extends BaseAdapter {
    private final static String TAG = DownAdapter.class.getName();
    private List<DownLoadTask> tasks;
    private Context mContext;
    private Handler mHandler;
    private Resources res;


    public DownAdapter(List<DownLoadTask> tasks, Context contex, Handler handler) {
        if (tasks == null) {
            tasks = new ArrayList<>();
        }
        this.tasks = tasks;
        this.mContext = contex;
        this.mHandler = handler;
        res = contex.getResources();
    }

    public void setFiles(List<DownLoadTask> files) {
        this.tasks = files;
    }

    public List<DownLoadTask> getFiles() {
        return this.tasks;
    }

    @Override
    public int getCount() {
        return tasks.size();
    }

    @Override
    public Object getItem(int position) {
        return tasks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.file_down_item, null);
            holder = new ViewHolder();
            holder.downTitle = (TextView) convertView.findViewById(R.id.downTitle);
            holder.downPercent = (TextView) convertView.findViewById(R.id.downPercent);
            holder.downProgressBar = (ProgressBar) convertView.findViewById(R.id.downProgressBar);
            holder.downBtn = (Button) convertView.findViewById(R.id.downBtn);


            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }



        final DownLoadListener listener = new DownLoadListener() {
            @Override
            public void onStart(DownLoadTask task) {

            }

            @Override
            public void onUpdate(DownLoadTask task) {
                if (mHandler != null) {
                    Message msg = mHandler.obtainMessage();
                    msg.what = Constants.MSG_DOWN_FILE_PROGRESS;
                    msg.obj = task;
                    mHandler.sendMessage(msg);
                }
//                Log.e(TAG, "onUpdate:holder" + holder);
//
//                holder.downProgressBar.setMax(100);
//                if (task.getAllSize() != 0) {
//                    int percent = (int) ((100 * task.getFinishSize()) / task.getAllSize());
//                    holder.downProgressBar.setProgress(percent);
//                    holder.downPercent.setText(percent + "%");
//                }
            }

            @Override
            public void onPause(DownLoadTask task) {

            }

            @Override
            public void onResume(DownLoadTask task) {

            }

            @Override
            public void onSuccess(DownLoadTask task) {
                Log.e(TAG, "onSuccess");
                tasks.remove(task);
//
                notifyDataSetChanged();
            }

            @Override
            public void onFailed(DownLoadTask task) {
                Log.e(TAG, "onFailed...");
            }

            @Override
            public void onRetry(DownLoadTask task) {

            }
        };
        final DownLoadTask task = tasks.get(position);
        holder.downBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (v.getId() == R.id.downBtn) {
                        if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                            Toast.makeText(mContext, "没有找到存储卡", Toast.LENGTH_LONG).show();
                            return;
                        }

                        if (task.getStatus() == DownLoadTask.STATUS_PAUSED) {
                            boolean flg = DownLoadManager.getInstance().resumeDownLoad(task);
                            if (!flg) {
                                DownLoadManager.getInstance().addDownLoadTask(task, listener);

                                ((Button) v).setText(res.getText(R.string.pause));
                            }

                        } else if (task.getStatus() == DownLoadTask.STATUS_RUNNING) {
                            DownLoadManager.getInstance().pauseDownLoad(task);

                            ((Button) v).setText(res.getText(R.string.resume));
                        } else if (task.getStatus() == DownLoadTask.STATUS_FINISH) {
                            tasks.remove(task);
                        } else {
                            task.setStatus(DownLoadTask.STATUS_RUNNING);
                            DownLoadManager.getInstance().addDownLoadTask(task, listener);

                            ((Button) v).setText(res.getText(R.string.pause));
                        }


                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(mContext, "出错", Toast.LENGTH_LONG).show();
                }
            }
        });

        ViewHolderUtil.updateViewHolder(task, holder);

        return convertView;
    }

    //更新过程有可能刷新列表，导致位置变化
    private int getRightPosition(DownLoadTask task) {
        int i = 0;
        for (DownLoadTask t : tasks) {
            if (task.getUrl().equals(t.getUrl())) {
                return i;
            }
            i++;

        }
        return -1;
    }

    public class ViewHolder {
        public TextView downTitle;
        public TextView downPercent;
        public ProgressBar downProgressBar;
        public Button downBtn;
    }


}
