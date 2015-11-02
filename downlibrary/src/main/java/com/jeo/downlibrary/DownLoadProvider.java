package com.jeo.downlibrary;

import java.util.List;

/**
 * Created by 志文 on 2015/11/2 0002.
 */
public interface DownLoadProvider {
    void saveDownTask(DownLoadTask task);

    void updateDownTask(DownLoadTask task);

    DownLoadTask findDownLoadTaskById(int id);

    DownLoadTask findDownLoadTask(String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy);

    List<DownLoadTask> getAllDownLoadTask();

    void notifyDownLoadStatusChanged(DownLoadTask task);

    void clearAllData();

    void delete(int id);

}
