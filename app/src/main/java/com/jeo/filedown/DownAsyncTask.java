package com.jeo.filedown;

import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.RecoverySystem;
import android.util.Log;

import com.jeo.filedown.com.jeo.filedown.util.OkHttpUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import okio.Buffer;

/**
 * Created by 志文 on 2015/10/30 0030.
 */
public class DownAsyncTask extends AsyncTask<FileItem, FileItem, Integer> {
    private final static String FOLDER = Environment.getExternalStorageDirectory() + File.separator + "downFile" + File.separator;

    static {
        File parent = new File(FOLDER);
        if (!parent.exists()) {
            parent.mkdirs();
        }
    }

    private final static String TAG = DownAsyncTask.class.getName();
    private Handler mHandler;
    private int index;//listView item index

    public DownAsyncTask(Handler handler, int index) {
        this.mHandler = handler;
        this.index = index;
    }

    @Override
    protected Integer doInBackground(FileItem... params) {
        try {
            final FileItem item = params[0];
            String path = FOLDER + System.currentTimeMillis();
            File file = new File(path);
            if (!file.exists()) {
                file.createNewFile();
            }

            item.setPath(path);

            long currentLen = 0;
            Log.e(TAG,"file:"+file.getAbsolutePath());
//            final FileOutputStream fileOutputStream = new FileOutputStream(file);


            final OkHttpUtil.ProgressListener progressListener = new OkHttpUtil.ProgressListener() {
                @Override public void update(Buffer sink,long bytesRead, long contentLength, boolean done) {
//                    try {
                        System.out.println(bytesRead);
                        System.out.println(sink.size());
                        System.out.println(contentLength);
                        System.out.println(done);
                        System.out.format("%d%% done\n", (100 * bytesRead) / contentLength);
                        if(bytesRead>0){
//                            fileOutputStream.write(sink.getByte(bytesRead));
//                            fileOutputStream.write("1".getBytes());
                        }
                        item.setCurrentLength(bytesRead);
                        item.setAllLength(contentLength);
                        publishProgress(item);
//                    }catch (IOException e){
//                        e.printStackTrace();
//                    }


                }
            };
            OkHttpUtil.downFile(item.getUrl(),progressListener);
//            URL url = new URL(item.getUrl());
//            Log.e(TAG, "ready to openConnection");
//            URLConnection conn = url.openConnection();
//            // 设置通用的请求属性
//            conn.setRequestProperty("accept", "*/*");
//            conn.setRequestProperty("connection", "Keep-Alive");
//            conn.setRequestProperty("user-agent",
//                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
//            // 建立实际的连接
//            conn.connect();
//            Log.e(TAG, "has openConnection");
//            InputStream is = conn.getInputStream();
//            Log.e(TAG, "has get inputStream");
//
//            byte[] data = new byte[10240];
//            int len = 0;
//            String path = FOLDER + System.currentTimeMillis();
//            File file = new File(path);
//            int allLength = conn.getContentLength();
//            Log.e(TAG, "file length:" + allLength);
//            if (!file.exists()) {
//                file.createNewFile();
//            }
//
//            item.setPath(path);
//            item.setAllLength(allLength);
//
//            FileOutputStream fileOutputStream = new FileOutputStream(file);
//            int currentLen = 0;
//            while ((len = is.read(data)) != -1) {
//                fileOutputStream.write(data, 0, len);
//                currentLen += len;
//                item.setCurrentLength(currentLen);
////                Log.e(TAG, "current:" + currentLen + " all:" + allLength + " -->" + currentLen / allLength);
//                publishProgress(item);
//            }
//            fileOutputStream.close();
//            is.close();

            return 0;
        } catch (IOException e) {
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    protected void onProgressUpdate(FileItem... values) {
        if (mHandler != null) {
            Message msg = mHandler.obtainMessage();
            msg.what = Constants.MSG_DOWN_FILE_PROGRESS;
            msg.obj = values[0];
            mHandler.sendMessage(msg);
        }
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
    }

}
