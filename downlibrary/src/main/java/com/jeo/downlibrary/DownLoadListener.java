package com.jeo.downlibrary;

/**
 * Created by 志文 on 2015/11/2 0002.
 */
public interface DownLoadListener {

    void onStart(DownLoadTask task);

    void onUpdate(DownLoadTask task);

    void onPause(DownLoadTask task);

    void onResume(DownLoadTask task);

    void onSuccess(DownLoadTask task);

    void onFailed(DownLoadTask task);

    void onRetry(DownLoadTask task);
}
