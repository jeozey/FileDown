package com.jeo.downlibrary;

import java.util.List;

/**
 * Created by 志文 on 2015/11/2 0002.
 */
public interface DownLoadProvider {
    public void saveDownTask(DownLoadTask task);

    public void updateDownTask(DownLoadTask task);

    public DownLoadTask findDownLoadTaskById(int id);

    public DownLoadTask findDownLoadTask(String[] columns,String selection,String[] selectionArgs,String groupBy,String having,String orderBy);

    public List<DownLoadTask> getAllDownLoadTask();

    public void notifyDownLoadStatusChanged(DownLoadTask task);

    public void clearAllData();

    public void delete(int id);

}
