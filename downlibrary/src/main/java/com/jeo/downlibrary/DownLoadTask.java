package com.jeo.downlibrary;

/**
 * Created by 志文 on 2015/11/2 0002.
 */
public class DownLoadTask {
//    public static final String ID="_id";
//    public static final String NAME="NAME";
//    public static final String UEL="URL";
//    public static final String PATH="PATH";
//    public static final String MD5="MD5";
//    public static final String FINISH_SIZE="FINISH_SIZE";
//    public static final String ALL_SIZE="ALL_SIZE";
//    public static final String STATUS="STATUS";

    public static final int STATUS_PENDDING = 1;
    public static final int STATUS_RUNNING = 2;
    public static final int STATUS_PAUSED = 3;
    public static final int STATUS_CANCEL = 4;
    public static final int STATUS_FINISH=5;
    public static final int STATUS_ERROR=6;

    private int id;
    private String name;
    private String url;
    private String path;
    private String md5;
    private long finishSize;
    private long allSize;
    private int status;

    public DownLoadTask(){
        finishSize = 0;
        allSize=0;
        status = STATUS_PENDDING;
    }

    @Override
    public boolean equals(Object o) {
        if(o==null){
            return false;
        }

        if(!(o instanceof DownLoadTask)){
            return false;
        }
        DownLoadTask task = (DownLoadTask)o;
        return this.url.equals(task.getUrl());
    }

    @Override
    public int hashCode() {
        return url.hashCode();
    }

    @Override
    public String toString() {
        return "url:"+url+" name:"+name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public long getFinishSize() {
        return finishSize;
    }

    public void setFinishSize(long finishSize) {
        this.finishSize = finishSize;
    }

    public long getAllSize() {
        return allSize;
    }

    public void setAllSize(long allSize) {
        this.allSize = allSize;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
