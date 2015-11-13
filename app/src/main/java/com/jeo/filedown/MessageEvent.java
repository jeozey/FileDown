package com.jeo.filedown;

import com.jeo.downlibrary.DownLoadTask;

/**
 * Created by 志文 on 2015/11/13 0013.
 */
public class MessageEvent {
    /* Additional fields if needed */
    private DownLoadTask task;

    public MessageEvent(DownLoadTask task) {
        this.task = task;
    }

    public DownLoadTask getTask() {
        return task;
    }
}
