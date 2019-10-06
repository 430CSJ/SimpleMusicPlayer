package com.zyh.example.simplemusicplayer;

import android.content.Context;
import android.os.PowerManager;

public class MusicUtil {
    private int currentPos;
    private boolean isPrepare = false;
    private static final MyPlayer myPlayer = new MyPlayer();
    private static MusicUtil musicUtil;
    private MusicListLoader.MusicInfo musicInfo;
    private static PowerManager.WakeLock wakeLock;
    private static Context actContext;

    public static MusicUtil instance(Context pContext) {
        if (musicUtil == null)
            musicUtil = new MusicUtil(pContext);
        actContext = pContext;
        return musicUtil;
    }

    public static MusicUtil instance() {
        if (musicUtil == null)
            musicUtil = new MusicUtil(actContext);
        return musicUtil;
    }

    private MusicUtil(Context pContext) {
        actContext = pContext;
    }

    public void setMusicInfo(int i) {
        musicInfo = MusicListLoader.getMusicList().get(i);
    }

    public MusicListLoader.MusicInfo getMusicInfo() {
        return musicInfo;
    }

    public boolean isPlaying() {
        return myPlayer.isPlaying();
    }

    public void play() {
        if (musicInfo != null) {
            acquireWakeLock();
            myPlayer.play(musicInfo.data);
        }
    }

    public void pause() {
        if (musicInfo != null) {
            myPlayer.pause();
            releaseWakeLock();
        }
    }

    public void start() {
        if (musicInfo != null) {
            acquireWakeLock();
            myPlayer.start();
        }
    }

    public void seekTo(int position) {
        myPlayer.seekTo(position);
    }

    public int getPos() {
        return myPlayer.getCurrPos();
    }

    public void release() {
        myPlayer.release();
        releaseWakeLock();
    }

    private void acquireWakeLock() {
        if (wakeLock == null) {
            PowerManager powerManager = (PowerManager)actContext.getSystemService(Context.POWER_SERVICE);
            wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK|PowerManager.ON_AFTER_RELEASE, "SMP:SMPwakelocktag");
            if (wakeLock !=null)
                wakeLock.acquire();
        }
    }
    private void releaseWakeLock() {
        if (wakeLock != null) {
            wakeLock.release();
            wakeLock = null;
        }
    }
}
