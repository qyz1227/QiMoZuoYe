package com.example.qimozuoye.bean;

import android.provider.MediaStore;

import java.io.Serializable;

public class MyMedia implements Serializable {
    public static final int MEDIA_VIDEO = 0;
    public static final int MEDIA_MUSIC = 1;
    public static final int MEDIA_IMAGE = 2;
    private String name;
    private int type;
    private String coverPath;
    private String dataPath;

    public MyMedia(){ }

    public MyMedia(String name, int type, String coverpath, String datapath) {
        this.name = name;
        this.type = type;
        this.coverPath = coverpath;
        this.dataPath = datapath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getCoverPath() {
        return coverPath;
    }

    public void setCoverPath(String coverPath) {
        this.coverPath = coverPath;
    }

    public String getDataPath() {
        return dataPath;
    }

    public void setDataPath(String dataPath) {
        this.dataPath = dataPath;
    }
}
