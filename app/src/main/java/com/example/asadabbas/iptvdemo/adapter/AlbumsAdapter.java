package com.example.asadabbas.iptvdemo.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.asadabbas.iptvdemo.Player_Main;
import com.example.asadabbas.iptvdemo.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rajat Gupta on 18/05/16.
 */
public class AlbumsAdapter extends RecyclerView.Adapter<AlbumsAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<String> albumList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, count;
        public ImageView thumbnail, overflow, share, play;

        public MyViewHolder(View view) {
            super(view);
            share = view.findViewById(R.id.share);
            play = view.findViewById(R.id.play);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            //overflow = (ImageView) view.findViewById(R.id.overflow);
        }
    }


    public AlbumsAdapter(Context mContext, ArrayList<String> albumList) {
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
                        Intent shareIntent = new Intent(mContext, Player_Main.class);
                        shareIntent.putExtra("uri", uriPath);
                        mContext.startActivity(shareIntent);

                    }
                }
        );

      /*  // loading album cover using Glide library





        holder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(holder.overflow);
                Toast.makeText(this, "Thank you for trying this app, Find out more...", Toast.LENGTH_SHORT).show();
            }
        });*/
    }

    /**
     * Showing popup menu when tapping on 3 dots
     */
   /* private void showPopupMenu(View view) {
        // inflate menu
        PopupMenu popup = new PopupMenu(mContext, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_album, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener());
        popup.show();
        Glide.with(mContext).load(album.getThumbnail()).into(holder.thumbnail);
    }*/

    /**
     * Click listener for popup menu items
     */
/*    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        public MyMenuItemClickListener() {
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.action_add_favourite:
                    Toast.makeText(mContext, "Add to favourite", Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.action_play_next:
                    Toast.makeText(mContext, "Play next", Toast.LENGTH_SHORT).show();
                    return true;
                default:
            }
            return false;
        }
    }*/
    @Override
    public int getItemCount() {
        return albumList.size();
    }
}
