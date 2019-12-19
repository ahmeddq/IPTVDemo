package com.example.asadabbas.iptvdemo.activities;

import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.asadabbas.iptvdemo.R;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

public class HomeActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        MediaFragment myf = new MediaFragment();
        setFragment(myf);

        FileFragment file = new FileFragment();

        StreamFragment streamFragment = new StreamFragment();
        FavouriteFragment favouriteFragment = new FavouriteFragment();


        BottomBar bottomBar = (BottomBar) findViewById(R.id.bottomBar);
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                if (tabId == R.id.media) {
                    setFragment(myf);

                } else if (tabId == R.id.file) {
                    setFragment(file);

                } else if (tabId == R.id.stream) {
                    setFragment(streamFragment);

                } else if (tabId == R.id.favourite) {
                    setFragment(favouriteFragment);
                }

            }
        });
    }

    protected void setFragment(Fragment fragment) {
        android.support.v4.app.FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(R.id.contentContainer, fragment);
        t.commit();
    }

}


