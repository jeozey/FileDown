package com.jeo.filedown;

import android.util.Log;

import com.jeo.downlibrary.DownLoadTask;

/**
 * Created by 志文 on 2015/10/30 0030.
 */
public class ViewHolderUtil {
    public static void updateViewHolder(DownLoadTask item,DownAdapter.ViewHolder holder){
        try{
            Log.e("test", "updateViewHolder:" + item);
            holder.downTitle.setText(item.getUrl());
            holder.downProgressBar.setMax(100);
            holder.downProgressBar.setProgress(0);
            if(item.getAllSize()!=0){
                int percent = (int) ((100 * item.getFinishSize()) / item.getAllSize());
                holder.downProgressBar.setProgress(percent);
                holder.downPercent.setText(percent + "%");
            }else {
                holder.downPercent.setText(0 + "%");
            }
            if(item.getAllSize()!=0&&holder.downProgressBar.getProgress()==holder.downProgressBar.getMax()){
                holder.downBtn.setText("完成");
                holder.downBtn.setEnabled(false);
            }else{
                holder.downBtn.setText("开始");
                holder.downBtn.setEnabled(true);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
