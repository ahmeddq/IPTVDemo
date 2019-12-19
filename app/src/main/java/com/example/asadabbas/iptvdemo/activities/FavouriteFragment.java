package com.example.asadabbas.iptvdemo.activities;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.example.asadabbas.iptvdemo.R;
import com.example.asadabbas.iptvdemo.adapter.AlbumsAdapter;
import com.example.asadabbas.iptvdemo.adapter.FavouriteAdapter;
import com.example.asadabbas.iptvdemo.model.Favourites;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class FavouriteFragment extends Fragment {

    RecyclerView list;
    TextView pplace_holder;

    public FavouriteFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragmen
        View view = inflater.inflate(R.layout.fragment_favourite, container, false);

        list = view.findViewById(R.id.list);
        list.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        pplace_holder = view.findViewById(R.id.pplace_holder);
        fillList();

        return view;
    }

    void fillList() {

        List<Favourites> favouritesArrayList = new Select().from(Favourites.class).execute();

        if (favouritesArrayList != null && favouritesArrayList.size() > 0) {
            FavouriteAdapter albumsAdapter = new FavouriteAdapter(getActivity(), favouritesArrayList);
            this.list.setAdapter(albumsAdapter);
            pplace_holder.setVisibility(View.GONE);
        } else {
            pplace_holder.setVisibility(View.VISIBLE);
        }

    }

}
