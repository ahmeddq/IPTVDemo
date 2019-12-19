package com.example.asadabbas.iptvdemo.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.asadabbas.iptvdemo.R;
import com.example.asadabbas.iptvdemo.activities.IPTVPlayer;
import com.example.asadabbas.iptvdemo.activities.MediaPlayer;
import com.example.asadabbas.iptvdemo.model.Favourites;
import com.example.asadabbas.iptvdemo.model.IPTV;
import com.example.asadabbas.iptvdemo.util.TinyDB;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rajat Gupta on 18/05/16.
 */
public class StreamAdapter extends RecyclerView.Adapter<StreamAdapter.MyViewHolder> {

    private Activity mContext;
    private List<IPTV> albumList;
    onItemClick onItemClick;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, name;
        public ImageView thumbnail, favourite, share, play, edit, delete;

        public MyViewHolder(View view) {
            super(view);
            share = view.findViewById(R.id.share);
            play = view.findViewById(R.id.play);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            favourite = (ImageView) view.findViewById(R.id.favourite);
            name = view.findViewById(R.id.name);
            edit = view.findViewById(R.id.edit);
            delete = view.findViewById(R.id.delete);
        }
    }

    public StreamAdapter(Activity mContext, List<IPTV> albumList) {
        this.mContext = mContext;
        this.albumList = albumList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.stream_card, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {


        Drawable album = mContext.getDrawable(R.drawable.image);

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.error(R.drawable.audio2);

        Glide.with(mContext).setDefaultRequestOptions(requestOptions).load(album).into(holder.thumbnail);

        holder.share.setOnClickListener(
                new ImageView.OnClickListener() {
                    public void onClick(View v) {

                        String path = albumList.get(position).getPath();
                        Intent shareIntent = new Intent();
                        shareIntent.setAction(Intent.ACTION_SEND);
                        shareIntent.putExtra(Intent.EXTRA_TEXT, path);
                        shareIntent.setType("text/plain");
                        mContext.startActivity(Intent.createChooser(shareIntent, "Share URL"));

                    }
                }
        );

        holder.play.setOnClickListener(
                new ImageView.OnClickListener() {
                    public void onClick(View v) {

                        String uriPath = albumList.get(position).getPath();
                        Intent shareIntent = new Intent(mContext, IPTVPlayer.class);
                        shareIntent.putExtra("uri", uriPath);
                        mContext.startActivity(shareIntent);
                        new TinyDB(mContext).putBoolean("isFullScreen", false);


                    }
                }
        );

        String path = albumList.get(position).getPath();

        holder.favourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isFavourite(path)) {
                    unFavourite(path);
                } else {
                    favourite(path);
                }

                boolean isFav = isFavourite(path);

                if (isFav) {
                    holder.favourite.setImageDrawable(mContext.getDrawable(R.drawable.ic_favorites));
                } else {
                    holder.favourite.setImageDrawable(mContext.getDrawable(R.drawable.heart_outline_black));
                }
            }
        });

        String nameIs = albumList.get(position).getName();
        holder.name.setText(nameIs);

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {

                boolean isFav = isFavourite(path);
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (isFav) {
                            holder.favourite.setImageDrawable(mContext.getDrawable(R.drawable.ic_favorites));
                        } else {
                            holder.favourite.setImageDrawable(mContext.getDrawable(R.drawable.heart_outline_black));
                        }

                    }
                });
            }
        });

        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClick.click(false, albumList.get(position));
            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClick.click(true, albumList.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }

    boolean isFavourite(String str) {

        boolean fav = false;

        Favourites isFav = new Select().from(Favourites.class).where("Path=?", str).executeSingle();

        if (isFav != null)
            fav = isFav.isFav();

        return fav;
    }

    void unFavourite(String str) {
        new Delete().from(Favourites.class).where("Path=?", str).execute();
    }

    void favourite(String album) {
        Favourites favourites = new Favourites();
        favourites.setFav(true);
        favourites.setPath(album);
        favourites.setStream(true);
        favourites.save();
    }

    public interface onItemClick {
        void click(boolean isDelete, IPTV iptvData);
    }

    public void setOnItemClick(onItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }

}
