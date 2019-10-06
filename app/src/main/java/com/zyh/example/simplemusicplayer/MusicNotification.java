package com.zyh.example.simplemusicplayer;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class MusicNotification extends NotificationCompat {
    private static MusicNotification notifiInstance = null;
    private final int NOTIFICATION_ID = 0, REQUEST_CODE = 12345;
    private Context context;

    private NotificationManager manager = null;
    private NotificationCompat.Builder builder = null;
    private android.app.Notification musicNotification;
    private final String prefix = "musicnotiact";
    private final String MUSIC_NOTI_CHANNEL_ID = "Play Notification";
    private final String MUSIC_NOTI_ACTION_START = prefix + ".START", MUSIC_NOTI_ACTION_PAUSE = prefix + ".PAUSE";
    private Intent start = null, pause = null;
    private PendingIntent musicSPI = null, musicPPI = null, musicOPI = null;
    private int iconint, iconintr;
    private String iconstr, iconstrr;

    public void setContext(Context context) {
        this.context = context;
    }
    public void setManager(NotificationManager manager) {
        this.manager = manager;
    }

    private MusicNotification(Context context) {
        this.context = context;
        builder = new NotificationCompat.Builder(context, MUSIC_NOTI_CHANNEL_ID);
        start = new Intent();
        start.setAction(MUSIC_NOTI_ACTION_START);
        pause = new Intent();
        pause.setAction(MUSIC_NOTI_ACTION_PAUSE);
    }

    public static MusicNotification getMusicNotification(Context context) {
        if (notifiInstance == null) {
            notifiInstance = new MusicNotification(context);
        }
        return notifiInstance;
    }

    public void onCreateMusicNoti() {
        musicSPI = PendingIntent.getBroadcast(context, REQUEST_CODE, start, 0);
        musicPPI = PendingIntent.getBroadcast(context, REQUEST_CODE, pause, 0);
        musicOPI = PendingIntent.getActivity(context, MusicAdapter.REQUEST_PLAY_ACTIVITY, new Intent(context, PlayActivity.class).putExtra("mi", -1), 0);
        iconint = R.drawable.ic_pause_circle_filled_24px;
        iconstr = "Pause";
        iconintr = R.drawable.ic_play_circle_filled_24px;;
        iconstrr = "Start";
        int iconi = iconintr;
        int iconil = iconint;
        String icons = iconstrr;
        PendingIntent musicOP = musicSPI;
        if (MusicUtil.instance().isPlaying()) {
            iconi = iconint;
            iconil = iconintr;
            icons = iconstr;
            musicOP = musicPPI;
        }
        MusicListLoader.MusicInfo musicInfo = MusicUtil.instance().getMusicInfo();
        builder.setContentTitle(musicInfo.title)
                .setContentText(musicInfo.album)
                .setSubText(musicInfo.artist)
                .setLargeIcon(MusicListLoader.getAlbumBMP(musicInfo.albumID, 360, 360))
                .setContentIntent(musicOPI)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setSmallIcon(iconil)
                .addAction(new NotificationCompat.Action(iconi, icons, musicOP));
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN)
            musicNotification = builder.getNotification();
        else
            musicNotification = builder.build();
    }

    public void onUpdateMusicNoti() {
        onCreateMusicNoti();
    }

    public Notification getNoti() {
        return musicNotification;
    }
}
