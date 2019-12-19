package com.example.asadabbas.iptvdemo.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
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
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.asadabbas.iptvdemo.R;
import com.example.asadabbas.iptvdemo.activities.IPTVPlayer;
import com.example.asadabbas.iptvdemo.activities.MediaPlayer;
import com.example.asadabbas.iptvdemo.model.Favourites;
import com.example.asadabbas.iptvdemo.util.TinyDB;

import java.util.ArrayList;
import java.util.List;

import static com.example.asadabbas.iptvdemo.util.Constants.POSITION;

/**
 * Created by Rajat Gupta on 18/05/16.
 */
public class FavouriteAdapter extends RecyclerView.Adapter<FavouriteAdapter.MyViewHolder> {

    private Activity mContext;
    private List<Favourites> albumList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, timeTxt, name;
        public ImageView thumbnail, share, play, favourite;

        public MyViewHolder(View view) {
            super(view);
            share = view.findViewById(R.id.share);
            play = view.findViewById(R.id.play);
            thumbnail = view.findViewById(R.id.thumbnail);
            favourite = view.findViewById(R.id.favourite);
            timeTxt = view.findViewById(R.id.timeTxt);
            name = view.findViewById(R.id.name);
        }
    }

    public FavouriteAdapter(Activity mContext, List<Favourites> albumList) {
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

        String album = albumList.get(position).getPath();
        final String[] duration = {""};
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.error(R.drawable.audio2);

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {

                try {
                    if (!albumList.get(position).isStream())
                        duration[0] = getDuration(album);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                boolean isFav = isFavourite(album);
                // String fileName = getfileName(album);
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        holder.timeTxt.setText(duration[0]);

                        if (isFav) {
                            holder.favourite.setImageDrawable(mContext.getDrawable(R.drawable.ic_favorites));
                        } else {
                            holder.favourite.setImageDrawable(mContext.getDrawable(R.drawable.heart_outline_black));
                        }

                    }
                });
            }
        });

        if (!albumList.get(position).isStream())
            Glide.with(mContext).setDefaultRequestOptions(requestOptions).load(album).into(holder.thumbnail);
        else {
            Drawable album1 = mContext.getDrawable(R.drawable.image);
            Glide.with(mContext).setDefaultRequestOptions(requestOptions).load(album1).into(holder.thumbnail);
        }


        holder.share.setOnClickListener(
                new ImageView.OnClickListener() {
                    public void onClick(View v) {


                        if (albumList.get(position).isStream()) {
                            String path = albumList.get(position).getPath();
                            Intent shareIntent = new Intent();
                            shareIntent.setAction(Intent.ACTION_SEND);
                            shareIntent.putExtra(Intent.EXTRA_TEXT, path);
                            shareIntent.setType("text/plain");
                            mContext.startActivity(Intent.createChooser(shareIntent, "Share URL"));

                            return;
                        }


                        Uri uriPath = Uri.parse(albumList.get(position).getPath());
                        Intent shareIntent = new Intent();
                        shareIntent.setAction(Intent.ACTION_SEND);
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

                        if (albumList.get(position).isStream()) {

                            String uriPath = albumList.get(position).getPath();
                            Intent shareIntent = new Intent(mContext, IPTVPlayer.class);
                            shareIntent.putExtra("uri", uriPath);
                            mContext.startActivity(shareIntent);
                            new TinyDB(mContext).putBoolean("isFullScreen",false);


                            return;
                        }

                        String uriPath = albumList.get(position).getPath();
                        Intent shareIntent = new Intent(mContext, MediaPlayer.class);
                        shareIntent.putExtra("uri", uriPath);
                        new TinyDB(mContext).putBoolean("isFullScreen",false);

                        POSITION = 0;
                        mContext.startActivity(shareIntent);

                    }
                }
        );


        holder.favourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isFavourite(album)) {
                    unFavourite(album);
                    albumList.remove(position);
                    notifyDataSetChanged();
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

}
