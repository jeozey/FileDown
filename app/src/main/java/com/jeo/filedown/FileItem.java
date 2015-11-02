package com.jeo.filedown;

import java.sql.Date;

/**
 * Created by 志文 on 2015/10/30 0030.
 */
public class FileItem {
    private int position;//listView item index

    private int id;
    private String name;
    private String url;
    private String path;
    private String md5;
    private long allLength;
    private long currentLength;
    private Date createTime;

    public FileItem(){}
    public FileItem(String url){
        this.url = url;
    }
    public FileItem(int position,String url, String path, long allLength, long currentLength) {
        this.position = position;
        this.url = url;
        this.path = path;
        this.allLength = allLength;
        this.currentLength = currentLength;
    }

    public FileItem(long allLength, long currentLength) {
        this.allLength = allLength;
        this.currentLength = currentLength;
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

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getAllLength() {
        return allLength;
    }

    public void setAllLength(long allLength) {
        this.allLength = allLength;
    }

    public long getCurrentLength() {
        return currentLength;
    }

    public void setCurrentLength(long currentLength) {
        this.currentLength = currentLength;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString() {
        return "hashCode():"+hashCode()+" path:" + path+" url:"+url+ " pos:"+position +" curr:"+currentLength+" all:"+allLength;
    }
}
