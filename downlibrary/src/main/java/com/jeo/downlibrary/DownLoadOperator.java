package com.jeo.downlibrary;


import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by 志文 on 2015/11/2 0002.
 */
public class DownLoadOperator implements Runnable {
    private static final String TAG = DownLoadOperator.class.getName();
    private static final int CONNECTION_TIME_OUT = 30000;
    private static final int READ_TIME_OUT = 30000;
    private static final int READ_BUFFER_SIZE = 1024 * 8;//8k
    private static final int REFRESH_INTEVAL_SIZE = 1024 * 100;//100k
    private DownLoadTask task;
    private DownLoadManager manager;

    private int retryTimes;
    private String filePath;

    private boolean pauseFlg;
    private boolean stopFlg;

    public DownLoadOperator(DownLoadManager manager, DownLoadTask task) {
        this.manager = manager;
        this.task = task;
        this.retryTimes = 0;
    }

    void pauseDownLoad() {
        pauseFlg = true;

    }

    void resumeDownLoad() {
        if (!pauseFlg) {
            return;
        }
        pauseFlg = false;
        synchronized (this) {
            notify();
        }
    }

    void cancelDownLoad() {
        stopFlg = true;
        resumeDownLoad();
    }

    private HttpURLConnection initConnection() throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(task.getUrl()).openConnection();
        conn.setConnectTimeout(CONNECTION_TIME_OUT);
        conn.setReadTimeout(READ_TIME_OUT);
        conn.setUseCaches(true);
        //断点续传
        if (task.getFinishSize() != 0) {
            conn.setRequestProperty("Range", "bytes=" + task.getFinishSize() + "-");
        }
        return conn;
    }

    private RandomAccessFile getDownLoadFile() throws IOException {
        String fileName = System.currentTimeMillis() + "_file";
        File file = new File(manager.getConfig().getDownLoadSavePath(), fileName);
        if (!file.getParentFile().isDirectory() && !file.getParentFile().mkdirs()) {
            throw new IOException("can not create downLoad folder");
        }

        filePath = file.getAbsolutePath();
        RandomAccessFile raf = new RandomAccessFile(filePath, "rw");
        //断点续传
        long finishSize = task.getFinishSize();
        if (finishSize != 0) {
            raf.seek(finishSize);
        }
        return raf;
    }

    @Override
    public void run() {
        do {
            RandomAccessFile raf = null;
            HttpURLConnection conn = null;
            InputStream is = null;
            try {
                Log.e(TAG, "run...");
                raf = getDownLoadFile();
                conn = initConnection();
                Log.e(TAG, "initConnection...");

                task.setPath(filePath);
                task.setAllSize(conn.getContentLength());
                task.setStatus(DownLoadTask.STATUS_RUNNING);

                manager.onStartDownLoadTask(task);

                is = conn.getInputStream();

                byte[] buffer = new byte[READ_BUFFER_SIZE];
                int count = 0;
                long total = task.getFinishSize();
                long prevTime = System.currentTimeMillis();
                long achieveSize = total;
                long speed = 0;
                while (!stopFlg && (count = is.read(buffer)) != -1) {
                    while (pauseFlg) {
                        manager.onPauseDownLoadTask(task);
                        synchronized (this) {
                            try {
                                wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                                manager.onResumeDownLoadTask(task);
                            }
                        }
                    }
//                    Log.e(TAG, "total:" + total);
                    raf.write(buffer, 0, count);
                    total += count;

                    long tmpSize = total - achieveSize;
                    if (tmpSize > REFRESH_INTEVAL_SIZE) {
                        long tmpTime = System.currentTimeMillis() - prevTime;
                        if (tmpTime != 0) {
                            speed = tmpSize * 1000 / tmpTime;
                            achieveSize = total;
                            prevTime = System.currentTimeMillis();

                            task.setFinishSize(total);
                            task.setSpeed(speed);
                            manager.onUpdateDownLoadTask(task, total, speed);
                        }
                    }
                }
                is.close();
                is = null;
                raf.close();
                raf = null;


                //downLoad finish
                long tmpSize = total - achieveSize;
                long tmpTime = System.currentTimeMillis() - prevTime;
                if (tmpTime != 0) {
                    speed = tmpSize * 1000 / tmpTime;
                }
                task.setFinishSize(total);
                task.setSpeed(speed);
                manager.onUpdateDownLoadTask(task, total, speed);

                if (stopFlg) {
                    manager.onCancelDownLoadTask(task);
                } else {
                    String fileMd5 = task.getMd5();
                    if (!TextUtils.isEmpty(fileMd5)) {
//                        String md5 = MD5Util.getFileMD5String(new File(filePath));
//                        if (!fileMd5.equals(md5)) {
//                            Log.e(TAG, "md5 not right:" + fileMd5 + "--" + md5);
//                            manager.onFailedDownLoadTask(task);
//                            break;
//                        } else {
//                            Log.e(TAG, "md5 is right:" + fileMd5 + "--" + md5);
//                        }
                        //不知为何如果不用异步线程计算Md5值,会导致内存奔溃
                        new Md5AsyncTask().execute(new String[]{task.getMd5(),filePath});
                    }

//                    manager.onSuccessDownLoadTask(task);
                }
                break;
            } catch (IOException e) {
                e.printStackTrace();

                if (retryTimes > manager.getConfig().getRetryTime()) {
                    manager.onFailedDownLoadTask(task);
                    stopFlg = true;
                    break;
                } else {
                    retryTimes++;
                    continue;
                }

            } finally {
                try {
                    if (is != null) {
                        is.close();
                        is = null;
                    }
                    if (raf != null) {
                        raf.close();
                        raf = null;
                    }
                } catch (Exception e) {
                }
            }
        } while (true);
    }

    class Md5AsyncTask extends AsyncTask<String, Integer, Integer> {
        @Override
        protected Integer doInBackground(String... params) {
            try {
                String fileMd5 = params[0];
                String filePath = params[1];
                if (!TextUtils.isEmpty(fileMd5)) {
                    String md5 = MD5Util.getFileMD5String(new File(filePath));
                    if (!fileMd5.equals(md5)) {
                        Log.e(TAG, "md5 not right:" + fileMd5 + "--" + md5);
                        return -1;
                    } else {
                        Log.e(TAG, "md5 is right:" + fileMd5 + "--" + md5);
                        return 1;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return 0;


        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);

            if (result == -1) {
                manager.onFailedDownLoadTask(task);
            } else if (result == 1) {
                manager.onSuccessDownLoadTask(task);
            }

        }
    }
}
