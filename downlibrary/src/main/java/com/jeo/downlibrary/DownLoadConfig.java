package com.jeo.downlibrary;

import android.content.Context;
import android.os.Environment;

import java.io.File;

/**
 * Created by 志文 on 2015/11/2 0002.
 */
public class DownLoadConfig {
    private String downLoadSavePath;
    private int maxDownLoadThread;
    private int retryTime;
    private DownLoadProvider provider;

    public static String ROOT_DIR = Environment.getExternalStorageDirectory().getPath() + File.separator + "downFile";

    private DownLoadConfig() {
        downLoadSavePath = ROOT_DIR + File.separator + "file";
        maxDownLoadThread = 5;
        retryTime = 2;
    }

    public DownLoadProvider getProvider(Context context) {
        if (provider == null) {
            provider = SqlLiteDownLoadProvider.getInstance(context);
        }
        return provider;
    }

    public static DownLoadConfig getDefaultDownLoadConfig(Context context) {
        DownLoadConfig config = new DownLoadConfig();
        config.provider = SqlLiteDownLoadProvider.getInstance(context);
        return config;
    }

    public String getDownLoadSavePath() {
        return downLoadSavePath;
    }

    public void setDownLoadSavePath(String downLoadSavePath) {
        this.downLoadSavePath = downLoadSavePath;
    }

    public int getMaxDownLoadThread() {
        return maxDownLoadThread;
    }

    public void setMaxDownLoadThread(int maxDownLoadThread) {
        this.maxDownLoadThread = maxDownLoadThread;
    }

    public int getRetryTime() {
        return retryTime;
    }

    public void setRetryTime(int retryTime) {
        this.retryTime = retryTime;
    }
}
