package com.jeo.filedown.util;

import android.util.Log;

import com.jeo.downlibrary.DownLoadTask;
import com.jeo.filedown.DownAdapter;

/**
 * Created by 志文 on 2015/10/30 0030.
 */
public class ViewHolderUtil {
    private static final String TAG = ViewHolderUtil.class.getName();

    public static void updateViewHolder(DownLoadTask item, DownAdapter.ViewHolder holder) {
        try {
            Log.e("test", "updateViewHolder:" + item);
            holder.downTitle.setText(item.getName());
            holder.downProgressBar.setMax(100);
            holder.downProgressBar.setProgress(0);
            if (item.getAllSize() != 0) {
                int percent = (int) ((100 * item.getFinishSize()) / item.getAllSize());
                holder.downProgressBar.setProgress(percent);
                holder.downPercent.setText(percent + "%");

                holder.downSpeed.setText(SpeedUtil.getSpeed(item.getSpeed()));
            } else {
                holder.downPercent.setText(0 + "%");
                holder.downSpeed.setText("0kb/s");
            }
            if (DownLoadTask.STATUS_FINISH == item.getStatus()) {
                holder.downBtn.setText("删除");
                holder.downBtn.setEnabled(true);
            } else if (DownLoadTask.STATUS_RUNNING == item.getStatus()) {
                holder.downBtn.setText("暂停");
                holder.downBtn.setEnabled(true);
            } else if (DownLoadTask.STATUS_PAUSED == item.getStatus()) {
                holder.downBtn.setText("继续");
                holder.downBtn.setEnabled(true);
            } else if (DownLoadTask.STATUS_ERROR == item.getStatus()) {
                holder.downBtn.setText("失败");
                holder.downBtn.setEnabled(true);
            } else {
                Log.e(TAG, "here:" + item);
                holder.downBtn.setText("开始");
                holder.downBtn.setEnabled(true);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
