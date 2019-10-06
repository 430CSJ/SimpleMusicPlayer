package com.zyh.example.simplemusicplayer;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

public class PlayService extends Service {
    public static final int ACTION_PLAY = 0, ACTION_START = 1, ACTION_PAUSE = 2, ACTION_STOP = 99;
    private static final String PLAY_NOTI = "Playing Notification", NOTI_ID = "Playing Noti ID.", NOTI_NAME = PLAY_NOTI;
    private final String prefix = "musicnotiact";
    private final String MUSIC_NOTI_CHANNEL_ID = "Play Notification";
    private final String MUSIC_NOTI_ACTION_START = prefix + ".START", MUSIC_NOTI_ACTION_PAUSE = prefix + ".PAUSE";
    private static MusicUtil musicUtil;
    private NotificationCompat.Builder notiBuilder;
    private PendingIntent ppIntent;
    private NotificationManager notificationManager;
    private MusicNotification musicNotification = null;
    private MusicBroadCast musicBroadCast = null;

    public PlayService() {}

    @Override
    public void onCreate() {
        musicUtil = MusicUtil.instance(this);/*
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTI_ID, NOTI_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        musicNotification = MusicNotification.getMusicNotification(getApplicationContext());
        musicNotification.setContext(getBaseContext());
        musicNotification.setManager(notificationManager);*/
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //unregisterReceiver(musicBroadCast);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //musicNotification.onCreateMusicNoti();
        if (intent != null) {
            int iconint = R.drawable.ic_pause_circle_filled_24px;
            int iconintr = R.drawable.ic_play_circle_filled_24px;
            String iconstr = "Pause";
            switch (intent.getIntExtra("Action", ACTION_STOP)) {
                case ACTION_PLAY:
                    musicUtil.play();
                    break;
                case ACTION_START:
                    musicUtil.start();
                    break;
                case ACTION_PAUSE:
                    musicUtil.pause();
                    iconint = R.drawable.ic_play_circle_filled_24px;
                    iconintr = R.drawable.ic_pause_circle_filled_24px;
                    iconstr = "Start";
                    break;
                case ACTION_STOP:
                    musicUtil.release();
                    iconint = R.drawable.ic_play_circle_filled_24px;
                    iconintr = R.drawable.ic_pause_circle_filled_24px;
                    iconstr = "Play";
                    break;
                default:
                    break;
            }
            //musicNotification.onUpdateMusicNoti();

            //startForeground(1, musicNotification.getNoti());
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public class MusicBinder extends Binder {
        public PlayService getService() {
            return PlayService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
        //musicNotification.onUpdateMusicNoti();
        //return new MusicBinder();
    }

    public class MusicBroadCast extends BroadcastReceiver {
        private final String prefix = "musicnotiact";
        private final String MUSIC_NOTI_CHANNEL_ID = "Play Notification";
        private final String MUSIC_NOTI_ACTION_START = prefix + ".START", MUSIC_NOTI_ACTION_PAUSE = prefix + ".PAUSE";

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case MUSIC_NOTI_ACTION_START:
                    Intent startIntent = new Intent(getApplicationContext(), PlayService.class).putExtra("Action", ACTION_START);
                    startService(startIntent);
                    break;
                case MUSIC_NOTI_ACTION_PAUSE:
                    Intent pauseIntent = new Intent(getApplicationContext(), PlayService.class).putExtra("Action", ACTION_PAUSE);
                    startService(pauseIntent);
                    break;
                default:
                    break;
            }
        }
    }
}
