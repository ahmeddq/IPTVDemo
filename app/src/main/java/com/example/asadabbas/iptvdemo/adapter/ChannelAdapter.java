package com.example.asadabbas.iptvdemo.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.asadabbas.iptvdemo.R;
import com.example.asadabbas.iptvdemo.activities.IPTVPlayer;
import com.example.asadabbas.iptvdemo.model.Favourites;
import com.example.asadabbas.iptvdemo.model.IPTV;
import com.example.asadabbas.iptvdemo.parse.data.ExtInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rajat Gupta on 18/05/16.
 */
public class ChannelAdapter extends RecyclerView.Adapter<ChannelAdapter.MyViewHolder> {

    private Activity mContext;
    private List<ExtInfo> albumList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, name;
        public ImageView thumbnail, share, play;

        public MyViewHolder(View view) {
            super(view);
            share = view.findViewById(R.id.share);
            play = view.findViewById(R.id.play);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            name = view.findViewById(R.id.name);
        }
    }

    public ChannelAdapter(Activity mContext, List<ExtInfo> albumList) {
        this.mContext = mContext;
        this.albumList = albumList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.channel_list_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {


        String album = albumList.get(position).getTvgLogoUrl();

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.error(R.drawable.image);

        Glide.with(mContext).setDefaultRequestOptions(requestOptions).load(album).into(holder.thumbnail);
        holder.share.setOnClickListener(
                new ImageView.OnClickListener() {
                    public void onClick(View v) {

                        String path = albumList.get(position).getTvUrl();
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

                        String uriPath = albumList.get(position).getTvUrl();
                        Intent intent = new Intent();
                        intent.putExtra("url", uriPath);
                        mContext.setResult(Activity.RESULT_OK, intent);
                        mContext.finish();

                    }
                }
        );

        String nameIs = albumList.get(position).getTvgName();
        holder.name.setText(nameIs);

    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }


}
