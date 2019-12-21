package com.example.qimozuoye.util;

import android.app.ProgressDialog;

import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

public class User {
    public static String name;
    public static String imagePath;
    public static ProgressDialog progressDialog;
    private User(){}

    public static String getName() {
        return name;
    }

    public static void setName(String name) {
        User.name = name;
    }

    public static String getImagePath() {
        return imagePath;
    }

    public static void setImagePath(String imagePath) {
        User.imagePath = imagePath;
    }

    public static ProgressDialog getProgressDialog() {
        return progressDialog;
    }

    public static void setProgressDialog(ProgressDialog progressDialog) {
        User.progressDialog = progressDialog;
    }
}
