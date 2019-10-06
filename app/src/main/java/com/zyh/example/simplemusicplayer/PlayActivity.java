package com.zyh.example.simplemusicplayer;

import android.annotation.SuppressLint;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class PlayActivity extends AppCompatActivity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
            mMusicInfoView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    private Integer mMusicI;
    private ImageView mAlbumIV;
    private TextView titleTV;
    private TextView albumTV;
    private TextView artistTV;
    private TextView pathTV;
    public AppCompatSeekBar seekBar;
    private TextView currentTV;
    public Button startPauseButton;
    private TextView durationTV;
    private HandlerThread handlerThread;
    private Handler getAlbumArt;
    private Handler workHandler;
    private Message message0;
    private static MusicListLoader.MusicInfo musicInfo;
    private View mMusicInfoView;
    private boolean isSeekBarChanging;
    private Timer updatePosTimer;
    private TimerTask updatePosTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_play);

        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.fullscreen_content);

        mMusicInfoView = findViewById(R.id.music_info);

        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        findViewById(R.id.play_button).setOnTouchListener(mDelayHideTouchListener);

        mMusicI = (Integer) getIntent().getSerializableExtra("mi");
        if (mMusicI == null)
            mMusicI = 0;
        if (mMusicI < 0)
            musicInfo = MusicUtil.instance().getMusicInfo();
        else if (mMusicI < MusicListLoader.getMusicList().size())
            musicInfo = MusicListLoader.getMusicList().get(mMusicI);
        if (musicInfo == null) {
            mMusicI = 0;
            musicInfo = MusicListLoader.getMusicList().get(0);
        }
        if (musicInfo != MusicUtil.instance().getMusicInfo()) {
            MusicUtil.instance().release();
            MusicUtil.instance().setMusicInfo(mMusicI);
        }
        startPauseButton = findViewById(R.id.play_button);
        startPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent playIntent = new Intent(v.getContext(), PlayService.class);
                if (MusicUtil.instance().isPlaying()) {
                    playIntent.putExtra("Action", PlayService.ACTION_PAUSE);
                    startService(playIntent);
                    startPauseButton.setText("Start");
                } else if (seekBar != null && seekBar.getProgress() == 0) {
                    playIntent.putExtra("Action", PlayService.ACTION_PLAY);
                    startService(playIntent);
                    startPauseButton.setText("Pause");
                } else {
                    playIntent.putExtra("Action", PlayService.ACTION_START);
                    startService(playIntent);
                    startPauseButton.setText("Pause");
                }
            }
        });
        if (MusicUtil.instance().isPlaying() && startPauseButton != null)
            startPauseButton.setText("Pause");
        mAlbumIV = findViewById(R.id.fullscreen_content);
        titleTV = findViewById(R.id.play_tv_title);
        albumTV = findViewById(R.id.play_tv_album);
        artistTV = findViewById(R.id.play_tv_artist);
        pathTV = findViewById(R.id.play_tv_path);
        seekBar = findViewById(R.id.seekbar);
        isSeekBarChanging = false;
        currentTV = findViewById(R.id.play_tv_current);
        durationTV = findViewById(R.id.play_tv_duration);
        getAlbumArt = new Handler();
        handlerThread = new HandlerThread("handlerThread");
        handlerThread.start();
        workHandler = new Handler(handlerThread.getLooper()) {
            Bitmap albumbm;
            @Override
            public void handleMessage(Message msg)
            {
                try {
                    albumbm = MusicListLoader.getAlbumBMP(msg.what, 1080, 1080);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                getAlbumArt.post(new Runnable() {
                    @Override
                    public void run() {
                        mAlbumIV.setImageBitmap(albumbm);
                    }
                });
            }
        };
        message0 = Message.obtain();
        message0.what = musicInfo.albumID;
        workHandler.sendMessage(message0);
        titleTV.setText(musicInfo.title);
        albumTV.setText(musicInfo.album);
        artistTV.setText(musicInfo.artist);
        pathTV.setText(musicInfo.data);
        seekBar.setMax(musicInfo.duration);
        currentTV.setText("0");
        durationTV.setText(String.valueOf(musicInfo.duration));
        updatePosTask = new TimerTask() {
            boolean playing;
            int curpos;
            @Override
            public void run() {
                playing = MusicUtil.instance().isPlaying();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (startPauseButton != null) {
                            if (playing)
                                startPauseButton.setText("Pause");
                            else if (startPauseButton.getText() != "Play")
                                startPauseButton.setText("Start");
                        }
                    }
                });
                if (isSeekBarChanging)
                    return;
                curpos = MusicUtil.instance().getPos();
                if (curpos < 0)
                    curpos = 0;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (seekBar != null)
                            seekBar.setProgress(curpos);
                        if (currentTV != null)
                            currentTV.setText(String.valueOf(curpos));
                    }
                });
            }
        };
        updatePosTimer = new Timer();
        updatePosTimer.schedule(updatePosTask, 0, 1000);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser)
                    currentTV.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isSeekBarChanging = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                MusicUtil.instance().seekTo(seekBar.getProgress());
                isSeekBarChanging = false;
            }
        });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mMusicInfoView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    @Override
    public void finish() {
        handlerThread.quit();
        if (mMusicI != null && mMusicI >= 0)
            setResult(mMusicI);
        else
            setResult(-1);
        super.finish();
    }
}
