package com.zyh.example.simplemusicplayer;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
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

    static class ViewHolder extends RecyclerView.ViewHolder {
        View musicView;
        ImageView iv_albumbmp;
        TextView tv_title, tv_album, tv_artist, tv_duration;

        public ViewHolder(View view) {
            super(view);
            musicView = view;
            iv_albumbmp = view.findViewById(R.id.iv_albumbmp);
            tv_title = view.findViewById(R.id.tv_title);
            tv_album = view.findViewById(R.id.tv_album);
            tv_artist = view.findViewById(R.id.tv_artist);
            tv_duration = view.findViewById(R.id.tv_duration);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_listview_music, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        MusicListLoader.MusicInfo musicInfo = mMusicList.get(position);
        MusicListLoader.GetAlbumBMPThread myThread = new MusicListLoader.GetAlbumBMPThread();
        myThread.mAlbumID = musicInfo.albumID;
        myThread.mReqW = 160;
        myThread.mReqH = 160;
        myThread.handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                holder.iv_albumbmp.setImageBitmap((Bitmap) msg.obj);
            }
        };
        MusicListLoader.executeThreadTask(myThread);
        holder.tv_title.setText(musicInfo.title);
        holder.tv_album.setText(musicInfo.album);
        holder.tv_artist.setText(musicInfo.artist);
        holder.tv_duration.setText(String.valueOf(musicInfo.duration));
    }

    @Override
    public int getItemCount() {
        return mMusicList.size();
    }

    public MusicAdapter(List<MusicListLoader.MusicInfo> musicInfos) {
        mMusicList = musicInfos;
    }
}
