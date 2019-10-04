package com.zyh.example.simplemusicplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static MusicListLoader musicListLoader;
    private static List<MusicListLoader.MusicInfo> musicInfos;
    private static RecyclerView musicRV;

    private void loadMusicRV() {
        musicListLoader = MusicListLoader.instance(getApplicationContext().getContentResolver(), getApplicationContext());
        musicInfos = musicListLoader.getMusicList();
        if (musicRV == null) {
            musicRV = findViewById(R.id.rv_musiclist);
            if (musicRV != null) {
                musicRV.setLayoutManager(new LinearLayoutManager(this));
                musicRV.setAdapter(new MusicAdapter(musicInfos));
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        musicRV = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            TestPermission.getPermission(Manifest.permission.READ_EXTERNAL_STORAGE, this, TestPermission.OPEN_SYSTEM_DIALOG);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            loadMusicRV();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == TestPermission.CODE_REQUEST_READ_EXTERNAL) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED)
                TestPermission.getPermission(permissions[0], this, TestPermission.GOTO_SETTING);
            else {
                loadMusicRV();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TestPermission.REQUEST_READ_EXTERNAL_SETTING)
            TestPermission.getPermission(Manifest.permission.READ_EXTERNAL_STORAGE, this, TestPermission.GOTO_SETTING);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            loadMusicRV();
    }
}
