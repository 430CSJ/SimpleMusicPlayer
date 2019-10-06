package com.zyh.example.simplemusicplayer;

import android.Manifest;
import android.app.Application;
import android.content.pm.PackageManager;
import android.os.Build;

import java.util.List;

public class SMPApp extends Application {
    private MusicListLoader appMusicListLoader;
    private MusicUtil appMusicUtil;

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            appMusicListLoader = MusicListLoader.instance(getApplicationContext().getContentResolver(), getApplicationContext());
            appMusicUtil = MusicUtil.instance();
        }
    }
}
