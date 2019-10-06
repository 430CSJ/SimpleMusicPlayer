package com.zyh.example.simplemusicplayer;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MusicListLoader {
    public static class MusicInfo {
        public String data;
        public int duration;
        public String title;
        public String artist;
        public String album;
        public int albumID;
    }
    private static final String LOG_TAG = "Simple Music Player";
    private static List<MusicInfo> musicList = new ArrayList<MusicInfo>();
    private static MusicListLoader musicListLoader;
    private static ContentResolver contentResolver;
    private static Context appContext;
    private static boolean loaded;
    private static ExecutorService threadPool;

    public static MusicListLoader instance(ContentResolver pContentResolver, Context pAppContext) {
        if (musicListLoader == null) {
            contentResolver = pContentResolver;
            musicListLoader = new MusicListLoader();
        }
        appContext = pAppContext;
        if (threadPool == null)
            threadPool = Executors.newCachedThreadPool();
        if (!loaded && appContext != null)
            loadMusicList();
        return musicListLoader;
    }

    private MusicListLoader() {
        loaded = false;
    }

    private static void loadMusicList() {
        //appContext.grantUriPermission(appContext.getPackageName(), MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        Cursor cursor = contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
        if (cursor == null)
            Log.i(LOG_TAG, "cursor == null");
        else if (!cursor.moveToFirst())
            Log.v(LOG_TAG, "cursor.moveToFirst() == false");
        else {
            do {
                MusicInfo musicInfo = new MusicInfo();
                musicInfo.data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                musicInfo.duration = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                musicInfo.title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                musicInfo.artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                musicInfo.album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                musicInfo.albumID = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
                musicList.add(musicInfo);
            } while (cursor.moveToNext());
        }
        loaded = true;
    }

    public static List<MusicInfo> getMusicList() {
        return musicList;
    }

    public static Bitmap getAlbumBMP(int albumid, int reqw, int reqh) {
        String mUriAlbums = "content://media/external/audio/albums";
        Uri mAlbumUri = Uri.parse(mUriAlbums + "/" + albumid);
        //appContext.grantUriPermission(appContext.getPackageName(), mAlbumUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        Cursor cursor = appContext.getContentResolver().query(mAlbumUri, new String[]{"album_art"}, null, null, null);
        String album_bmp = null;
        if (cursor.getCount() > 0 && cursor.getColumnCount() > 0) {
            cursor.moveToNext();
            album_bmp = cursor.getString(0);
        }
        cursor.close();
        Bitmap bmp = null;
        if (album_bmp != null) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(album_bmp, options);
            int oh = options.outHeight, ow = options.outWidth, sampleSize = 1;
            while (oh > reqh || ow > reqw) {
                oh /= 2;
                ow /= 2;
                sampleSize *= 2;
            }
            options.inSampleSize = sampleSize;
            options.inJustDecodeBounds = false;
            bmp = BitmapFactory.decodeFile(album_bmp, options);
        }
        return bmp;
    }

    public static class GetAlbumBMPTask extends AsyncTask<Integer, Integer, Bitmap> {
        Bitmap album_bmp;
        private MusicAdapter.ViewHolder holder;

        public GetAlbumBMPTask(MusicAdapter.ViewHolder holder) {
            this.holder = holder;
        }

        @Override
        protected Bitmap doInBackground(Integer... integers) {
            album_bmp = getAlbumBMP(musicList.get(integers[0]).albumID, integers[1], integers[2]);
            return album_bmp;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            holder.iv_albumbmp.setImageBitmap(bitmap);
        }
    }
}
