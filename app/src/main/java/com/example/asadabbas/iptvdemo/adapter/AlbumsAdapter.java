package com.example.asadabbas.iptvdemo.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
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
import com.activeandroid.query.Update;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.asadabbas.iptvdemo.activities.MediaPlayer;
import com.example.asadabbas.iptvdemo.R;
import com.example.asadabbas.iptvdemo.model.Favourites;
import com.example.asadabbas.iptvdemo.model.IPTV;
import com.example.asadabbas.iptvdemo.util.TinyDB;

import java.util.ArrayList;

import static com.example.asadabbas.iptvdemo.util.Constants.POSITION;

/**
 * Created by Rajat Gupta on 18/05/16.
 */
public class AlbumsAdapter extends RecyclerView.Adapter<AlbumsAdapter.MyViewHolder> {

    private Activity mContext;
    private ArrayList<String> albumList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, timeTxt, name;
        public ImageView thumbnail, share, play, favourite;

        public MyViewHolder(View view) {
            super(view);
            share = view.findViewById(R.id.share);
            play = view.findViewById(R.id.play);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            favourite = (ImageView) view.findViewById(R.id.favourite);
            timeTxt = (TextView) view.findViewById(R.id.timeTxt);
            name = (TextView) view.findViewById(R.id.name);
        }
    }

    public AlbumsAdapter(Activity mContext, ArrayList<String> albumList) {
        this.mContext = mContext;
        this.albumList = albumList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.album_card, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        String album = albumList.get(position);

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.error(R.drawable.audio2);

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                String duration = getDuration(album);
                boolean isFav = isFavourite(album);
                // String fileName = getfileName(album);
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!TextUtils.isEmpty(duration) && duration != null) {

                            holder.timeTxt.setText(duration);

                            if (isFav) {
                                holder.favourite.setImageDrawable(mContext.getDrawable(R.drawable.ic_favorites));
                            } else {
                                holder.favourite.setImageDrawable(mContext.getDrawable(R.drawable.heart_outline_black));
                            }
                            //    holder.name.setText(fileName);
                        }
                    }
                });
            }
        });

        Glide.with(mContext).setDefaultRequestOptions(requestOptions).load(album).into(holder.thumbnail);

        holder.share.setOnClickListener(
                new ImageView.OnClickListener() {
                    public void onClick(View v) {

                        Uri uriPath = Uri.parse(albumList.get(position));
                        Intent shareIntent = new Intent();
                        shareIntent.setAction(Intent.ACTION_SEND);
                        //shareIntent.putExtra(Intent.EXTRA_TEXT, "Text");
                        shareIntent.putExtra(Intent.EXTRA_STREAM, uriPath);
                        shareIntent.setType("video/*");
                        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        mContext.startActivity(Intent.createChooser(shareIntent, "Share Video"));

                    }
                }
        );

        holder.play.setOnClickListener(
                new ImageView.OnClickListener() {
                    public void onClick(View v) {

                        String uriPath = albumList.get(position);
                        Intent shareIntent = new Intent(mContext, MediaPlayer.class);
                        shareIntent.putExtra("uri", uriPath);
                        new TinyDB(mContext).putBoolean("isFullScreen",false);
                        POSITION =0;
                        mContext.startActivity(shareIntent);

                    }
                }
        );


        holder.favourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isFavourite(album)) {
                    unFavourite(album);
                } else {
                    favourite(album);
                }

                boolean isFav = isFavourite(album);

                if (isFav) {
                    holder.favourite.setImageDrawable(mContext.getDrawable(R.drawable.ic_favorites));
                } else {
                    holder.favourite.setImageDrawable(mContext.getDrawable(R.drawable.heart_outline_black));
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }

    String getDuration(String uriOfFile) {

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uriOfFile);
        long duration = Long.parseLong(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));

        int seconds = (int) (duration / 1000) % 60;
        int minutes = (int) ((duration / (1000 * 60)) % 60);
        int hours = (int) ((duration / (1000 * 60 * 60)) % 24);


        String timeDuration = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        retriever.release();

        return timeDuration;

    }

    String getfileName(String str) {

        String filename = str.substring(str.lastIndexOf("/") + 1);

        return filename;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
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
        favourites.save();
    }

}
