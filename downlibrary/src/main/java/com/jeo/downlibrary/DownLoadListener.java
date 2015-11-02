package com.jeo.downlibrary;

/**
 * Created by 志文 on 2015/11/2 0002.
 */
public interface DownLoadListener {

    public void onStart(DownLoadTask task);
    public void onUpdate(DownLoadTask task);
    public void onPause(DownLoadTask task);
    public void onResume(DownLoadTask task);
    public void onSuccess(DownLoadTask task);
    public void onFailed(DownLoadTask task);
    public void onRetry(DownLoadTask task);
}
