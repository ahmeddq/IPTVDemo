package com.example.asadabbas.iptvdemo.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.example.asadabbas.iptvdemo.R;
import com.example.asadabbas.iptvdemo.adapter.ChannelAdapter;
import com.example.asadabbas.iptvdemo.parse.data.ExtInfo;

import java.util.ArrayList;

public class ChannelActivity extends AppCompatActivity {
    RecyclerView list;
    EditText searchView;
    ChannelAdapter channelAdapter = null;
    ArrayList<ExtInfo> channelList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_channel);

        list = findViewById(R.id.list_of_channel);

        searchView = findViewById(R.id.searchView);
        list.setLayoutManager(new GridLayoutManager(this, 2));

        Intent intent = getIntent();

        if (intent != null) {
            channelList = intent.getParcelableArrayListExtra("list");
            setAdapter1(channelList);
        }


        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                ArrayList<ExtInfo> list = new ArrayList<>();

                for (ExtInfo extInfo : channelList) {

                    if (extInfo.getTvgName().toLowerCase().contains(s.toString().toLowerCase())) {
                        list.add(extInfo);
                    }
                }

                setAdapter1(list);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    void setAdapter1(ArrayList<ExtInfo> list1) {

        channelAdapter = new ChannelAdapter(this, list1);
        list.setAdapter(channelAdapter);
    }
}
