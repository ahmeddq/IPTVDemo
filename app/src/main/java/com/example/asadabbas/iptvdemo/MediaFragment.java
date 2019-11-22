package com.example.asadabbas.iptvdemo;


import android.Manifest;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.asadabbas.iptvdemo.adapter.AlbumsAdapter;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import rebus.permissionutils.PermissionEnum;
import rebus.permissionutils.PermissionUtils;


/**
 * A simple {@link Fragment} subclass.
 */
public class MediaFragment extends Fragment {

    RecyclerView list;

    public MediaFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_media, container, false);
        // Inflate the layout for this fragment

        list = view.findViewById(R.id.list);
        init();

        permissions();
        return view;
    }

    private void init() {

        list.setLayoutManager(new GridLayoutManager(getActivity(), 2));
      /*  list.setLayoutManager
                (new LinearLayoutManager(this));*/
    }

    public ArrayList<String> getAllMedia() {
        HashSet<String> videoItemHashSet = new HashSet<>();
        String[] projection = {MediaStore.Video.VideoColumns.DATA, MediaStore.Video.Media.DISPLAY_NAME};
        Cursor cursor = getActivity().getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection, null, null, null);
        try {
            cursor.moveToFirst();
            do {
                videoItemHashSet.add((cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA))));
            } while (cursor.moveToNext());

            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


        String[] projection1 = {MediaStore.Audio.AudioColumns.DATA, MediaStore.Video.Media.DISPLAY_NAME};
        Cursor cursor1 = getActivity().getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection1, null, null, null);
        try {
            cursor1.moveToFirst();
            do {
                videoItemHashSet.add((cursor1.getString(cursor1.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA))));
            } while (cursor1.moveToNext());

            cursor1.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        ArrayList<String> downloadedList = new ArrayList<>(videoItemHashSet);

        return downloadedList;
    }

    PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {

            try {
                ArrayList<String> media = getAllMedia();
                fillList(media);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onPermissionDenied(List<String> deniedPermissions) {
            Toast.makeText(getActivity(), "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
        }
    };

    void permissions() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            boolean readGranted = PermissionUtils.isGranted(getActivity(), PermissionEnum.READ_EXTERNAL_STORAGE);

            if (!readGranted) {
                TedPermission.with(getActivity())
                        .setPermissionListener(permissionlistener)
                        .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                        .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE
                        )
                        .check();
            } else {
                ArrayList<String> media = getAllMedia();
                fillList(media);

            }
        } else {
            ArrayList<String> media = getAllMedia();
            fillList(media);

        }
    }

    void fillList(ArrayList<String> list) {

        AlbumsAdapter albumsAdapter = new AlbumsAdapter(getActivity(), list);
        this.list.setAdapter(albumsAdapter);
    }


}
