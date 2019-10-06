package com.zyh.example.simplemusicplayer;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

import java.io.IOException;

public class MyPlayer implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {
    private MediaPlayer mediaPlayer;
    private boolean hasPrepared;

    private void init() {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setOnErrorListener(this);
            mediaPlayer.setOnCompletionListener(this);
            mediaPlayer.setOnPreparedListener(this);
        }
    }

    public void play(Context context, Uri datasource) {
        hasPrepared = false;
        init();
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(context, datasource);
            mediaPlayer.prepareAsync();
        }catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void play(String datapath) {
        hasPrepared = false;
        init();
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(datapath);
            mediaPlayer.prepareAsync();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void start() {
        if (mediaPlayer != null && hasPrepared)
            mediaPlayer.start();
    }

    public boolean isPlaying() {
        if (mediaPlayer != null && hasPrepared)
            return mediaPlayer.isPlaying();
        return false;
    }

    public void pause() {
        if (mediaPlayer !=null && hasPrepared)
            mediaPlayer.pause();
    }

    public void seekTo(int position) {
        if (mediaPlayer != null && hasPrepared)
            mediaPlayer.seekTo(position);
    }

    public int getCurrPos() {
        if (mediaPlayer != null && hasPrepared)
            return mediaPlayer.getCurrentPosition();
        return -1;
    }

    public void release() {
        hasPrepared = false;
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        hasPrepared = true;
        start();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        hasPrepared = true;
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        hasPrepared = false;
        return false;
    }
}
