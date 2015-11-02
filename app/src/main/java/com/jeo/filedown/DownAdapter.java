package com.jeo.filedown;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jeo.filedown.com.jeo.filedown.util.OkHttpUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 志文 on 2015/10/30 0030.
 */
public class DownAdapter extends BaseAdapter {
    private final static String TAG = DownAdapter.class.getName();
    private List<FileItem> files;
    private Context mContext;
    private Handler handler;
    private Resources res;

    public DownAdapter(List<FileItem> files, Context contex, Handler handler) {
        if (files == null) {
            files = new ArrayList<>();
        }
        this.files = files;
        this.mContext = contex;
        this.handler = handler;
        res = contex.getResources();
    }

    public void setFiles(List<FileItem> files){
        this.files = files;
    }
    public List<FileItem>  getFiles(){
        return this.files;
    }

    @Override
    public int getCount() {
        return files.size();
    }

    @Override
    public Object getItem(int position) {
        return files.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
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

        FileItem item = files.get(position);
        holder.downBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (v.getId() == R.id.downBtn) {
                        if (res.getText(R.string.start).equals(((Button) v).getText().toString())) {
                            FileItem item = files.get(position);
                            item.setPosition(position);
                            new DownAsyncTask(handler, position).execute(item);
                            ((Button) v).setText(res.getText(R.string.pause));

                        } else {

                        }
                        Log.e(TAG,"position:"+position);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(mContext, "出错", Toast.LENGTH_LONG).show();
                }
            }
        });

        ViewHolderUtil.updateViewHolder(item, holder);

        return convertView;
    }


    public class ViewHolder {
        public TextView downTitle;
        public TextView downPercent;
        public ProgressBar downProgressBar;
        public Button downBtn;
    }


}
