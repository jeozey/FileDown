package com.jeo.downlibrary;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by 志文 on 2015/11/2 0002.
 */
public class DownLoadManager {
    private static final String TAG = DownLoadManager.class.getName();

    private static DownLoadManager instance = new DownLoadManager();
    private HashMap<DownLoadTask, DownLoadOperator> taskOperators = new HashMap<>();
    private HashMap<DownLoadTask, DownLoadListener> taskListeners = new HashMap<>();
    private DownLoadConfig config;
    private DownLoadProvider provider;
    private ExecutorService pool;
    //by jeo
    private Handler handler = new Handler();
    private DownLoadManager() {

    }

    public static DownLoadManager getInstance() {
        if (instance == null) {
            instance = new DownLoadManager();
        }
        return instance;
    }

    public void init(Context context) {
        config = DownLoadConfig.getDefaultDownLoadConfig(context);
        provider = config.getProvider(context);
        pool = Executors.newFixedThreadPool(config.getMaxDownLoadThread());
    }

    public void init(DownLoadConfig config, Context context) {
        if (config == null) {
            init(context);
            return;
        }
        this.config = config;
        provider = config.getProvider(context);
        pool = Executors.newFixedThreadPool(config.getMaxDownLoadThread());
    }

    public void clearAllData() {
        provider.clearAllData();
    }

    public void addDownLoadTask(DownLoadTask task) {
        addDownLoadTask(task, null);
    }

    public void addDownLoadTask(DownLoadTask task, DownLoadListener listener) {
        if (TextUtils.isEmpty(task.getUrl())) {
            throw new IllegalArgumentException("task's url can not be null");
        }
        if (taskOperators.containsKey(task)) {
            return;
        }

        DownLoadOperator operator = new DownLoadOperator(task);
        taskOperators.put(task, operator);

        if (listener != null) {
            taskListeners.put(task, listener);
        }
        task.setStatus(DownLoadTask.STATUS_PENDDING);

        DownLoadTask historyTask = provider.findDownLoadTaskById(task.getId());
        if (historyTask == null) {
            provider.saveDownTask(task);
        } else {
            provider.updateDownTask(task);
        }

        pool.submit(operator);
    }

    public void addDownLoadTaskNoStart(DownLoadTask task, DownLoadListener listener) {
        if (TextUtils.isEmpty(task.getUrl())) {
            throw new IllegalArgumentException("task's url can not be null");
        }
        if (taskOperators.containsKey(task)) {
            return;
        }

        DownLoadOperator operator = new DownLoadOperator(task);
        taskOperators.put(task, operator);

        if (listener != null) {
            taskListeners.put(task, listener);
        }
        task.setStatus(DownLoadTask.STATUS_PENDDING);

        DownLoadTask historyTask = provider.findDownLoadTaskById(task.getId());
        if (historyTask == null) {
            provider.saveDownTask(task);
        } else {
            provider.updateDownTask(task);
        }

    }

    public DownLoadListener getDownLoadTaskListener(DownLoadTask task) {
        if (task == null) {
            return null;
        }
        return taskListeners.get(task);
    }

    public void removeDownLoadTaskListener(DownLoadTask task) {
        if (task != null && taskListeners.containsKey(task)) {
            taskListeners.remove(task);
        }
    }

    public void updateDownLoadListener(DownLoadTask task, DownLoadListener listener) {
        if (task != null && taskListeners.containsKey(task)) {
            taskListeners.put(task, listener);
        }
    }

    public void pauseDownLoad(DownLoadTask task) {
        if (task == null) {
            return;
        }

        DownLoadOperator operator = taskOperators.get(task);
        if (operator != null) {
            operator.pauseDownLoad(task);
        }
    }

    public void resumeDownLoad(DownLoadTask task) {
        if (task == null) {
            return;
        }

        DownLoadOperator operator = taskOperators.get(task);
        if (operator != null) {
            operator.resumeDownLoad(task);
        }
    }

    public void cancelDownLoad(DownLoadTask task) {
        if (task == null) {
            return;
        }

        DownLoadOperator operator = taskOperators.get(task);
        if (operator != null) {
            operator.cancelDownLoad(task);
        }
    }

    public DownLoadTask findDownLoadTaskById(int id) {
        Iterator<DownLoadTask> iterator = taskOperators.keySet().iterator();
        while (iterator.hasNext()) {
            DownLoadTask task = iterator.next();
            if (id == task.getId()) {
                return task;

            }
        }
        return provider.findDownLoadTaskById(id);
    }

    public List<DownLoadTask> getAllDownLoadTask(){
        return provider.getAllDownLoadTask();
    }


    private void removeTask(DownLoadTask task){
        try {
            taskOperators.remove(task);
            taskListeners.remove(task);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /************************************************/
    void onUpdateDownLoadTask(final DownLoadTask task,final long finishSize,final long speed){
        task.setStatus(DownLoadTask.STATUS_RUNNING);
        final DownLoadListener listener = taskListeners.get(task);
        handler.post(new Runnable() {
            @Override
            public void run() {
                provider.updateDownTask(task);
                if (listener!=null){
                    listener.onUpdate(task);
                }
            }
        });
    }
    void onStartDownLoadTask(final DownLoadTask task,final long finishSize,final long speed){
        task.setStatus(DownLoadTask.STATUS_PENDDING);
        final DownLoadListener listener = taskListeners.get(task);
        handler.post(new Runnable() {
            @Override
            public void run() {
                provider.updateDownTask(task);
                if (listener!=null){
                    listener.onStart(task);
                }
            }
        });
    }
    void onPauseDownLoadTask(final DownLoadTask task,final long finishSize,final long speed){
        task.setStatus(DownLoadTask.STATUS_PAUSED);
        final DownLoadListener listener = taskListeners.get(task);
        handler.post(new Runnable() {
            @Override
            public void run() {
                provider.updateDownTask(task);
                if (listener!=null){
                    listener.onPause(task);
                }
            }
        });
    }
    void onResumeDownLoadTask(final DownLoadTask task,final long finishSize,final long speed){
        task.setStatus(DownLoadTask.STATUS_RUNNING);
        final DownLoadListener listener = taskListeners.get(task);
        handler.post(new Runnable() {
            @Override
            public void run() {
                provider.updateDownTask(task);
                if (listener!=null){
                    listener.onResume(task);
                }
            }
        });
    }
    void onCancelDownLoadTask(final DownLoadTask task,final long finishSize,final long speed){
        task.setStatus(DownLoadTask.STATUS_CANCEL);
        final DownLoadListener listener = taskListeners.get(task);
        handler.post(new Runnable() {
            @Override
            public void run() {
                provider.updateDownTask(task);
                if (listener!=null){
                    listener.onResume(task);
                }
            }
        });
    }
    void onSuccessDownLoadTask(final DownLoadTask task,final long finishSize,final long speed){
        task.setStatus(DownLoadTask.STATUS_FINISH);
        final DownLoadListener listener = taskListeners.get(task);
        handler.post(new Runnable() {
            @Override
            public void run() {
                provider.updateDownTask(task);
                if (listener!=null){
                    listener.onSuccess(task);
                }
            }
        });
    }
    void onFailedDownLoadTask(final DownLoadTask task,final long finishSize,final long speed){
        task.setStatus(DownLoadTask.STATUS_ERROR);
        final DownLoadListener listener = taskListeners.get(task);
        handler.post(new Runnable() {
            @Override
            public void run() {
                provider.updateDownTask(task);
                if (listener!=null){
                    listener.onFailed(task);
                }
            }
        });
    }
    void onRetryDownLoadTask(final DownLoadTask task,final long finishSize,final long speed){
        final DownLoadListener listener = taskListeners.get(task);
        if (listener!=null){
            handler.post(new Runnable() {
                @Override
                public void run() {
                    listener.onRetry(task);
                }
            });
        }
    }



}
