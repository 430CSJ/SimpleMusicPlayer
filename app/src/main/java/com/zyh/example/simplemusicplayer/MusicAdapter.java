package com.zyh.example.simplemusicplayer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.ViewHolder> {
    private List<MusicListLoader.MusicInfo> mMusicList;
    private Context mContext;
    public Activity mActivity;
    public static final int REQUEST_PLAY_ACTIVITY = 1;

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        View musicView;
        ImageView iv_albumbmp;
        TextView tv_title, tv_album, tv_artist, tv_duration, tv_path;
        Activity a;
        MusicListLoader.GetAlbumBMPTask task;

        public ViewHolder(View view) {
            super(view);
            musicView = view;
            iv_albumbmp = view.findViewById(R.id.iv_albumbmp);
            tv_title = view.findViewById(R.id.tv_title);
            tv_album = view.findViewById(R.id.tv_album);
            tv_artist = view.findViewById(R.id.tv_artist);
            tv_duration = view.findViewById(R.id.tv_duration);
            tv_path = view.findViewById(R.id.tv_path);
            task = new MusicListLoader.GetAlbumBMPTask(this);
        }

        public void startTask(int position) {
            if (task.getStatus() == AsyncTask.Status.RUNNING) {
                task.cancel(true);
                task = new MusicListLoader.GetAlbumBMPTask(this);
            }
            if (task.getStatus() == AsyncTask.Status.FINISHED)
                task = new MusicListLoader.GetAlbumBMPTask(this);
            task.execute(position, 160, 160);
        }

        public void setActivity(Activity activity) {
            a = activity;
        }

        public void onClick(View v) {
            mActivity.startActivityForResult(new Intent(mActivity, PlayActivity.class).putExtra("mi", getAdapterPosition()), REQUEST_PLAY_ACTIVITY);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_listview_music, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.setActivity(mActivity);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        MusicListLoader.MusicInfo musicInfo = mMusicList.get(position);
        holder.iv_albumbmp.setImageBitmap(null);
        holder.startTask(position);
        holder.tv_title.setText(musicInfo.title);
        holder.tv_album.setText(musicInfo.album);
        holder.tv_artist.setText(musicInfo.artist);
        holder.tv_duration.setText(String.valueOf(musicInfo.duration));
        holder.tv_path.setText(musicInfo.data);
        holder.musicView.setOnClickListener(holder);
    }

    @Override
    public int getItemCount() {
        return mMusicList.size();
    }

    public MusicAdapter(List<MusicListLoader.MusicInfo> musicInfos) {
        mMusicList = musicInfos;
    }
}
