package com.example.asadabbas.iptvdemo.activities;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.widget.Toast;

import com.example.asadabbas.iptvdemo.R;
import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.github.paolorotolo.appintro.model.SliderPage;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;
import java.util.List;

import rebus.permissionutils.PermissionEnum;
import rebus.permissionutils.PermissionUtils;

public class IntroActivity extends AppIntro {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Note here that we DO NOT use setContentView();

        // Add your slide fragments here.
        // AppIntro will automatically generate the dots indicator and buttons.
     /*   addSlide(firstFragment);
        addSlide(secondFragment);
        addSlide(thirdFragment);
        addSlide(fourthFragment);*/

        // Instead of fragments, you can also use our default slide.
        // Just create a `SliderPage` and provide title, description, background and image.
        // AppIntro will do the rest.
        SliderPage sliderPage = new SliderPage();
        sliderPage.setTitle("Welcome to IPTV Player");
        sliderPage.setDescription("IPTV Player made simple and fun");
        sliderPage.setImageDrawable(R.drawable.plasyer);
        sliderPage.setBgColor(getResources().getColor(R.color.black));
        addSlide(AppIntroFragment.newInstance(sliderPage));

        SliderPage sliderPage1 = new SliderPage();
        sliderPage1.setTitle("Access Media");
        sliderPage1.setDescription("Make your life simpler by accessing video and audio by one click.");
        sliderPage1.setImageDrawable(R.drawable.media);
        sliderPage1.setBgColor(getResources().getColor(R.color.color1));

        addSlide(AppIntroFragment.newInstance(sliderPage1));


        SliderPage sliderPage2 = new SliderPage();
        sliderPage2.setTitle("Access File");
        sliderPage2.setDescription("Access the media by simply exploring the mobile folders");
        sliderPage2.setImageDrawable(R.drawable.file);
        sliderPage2.setBgColor(getResources().getColor(R.color.color2));

        addSlide(AppIntroFragment.newInstance(sliderPage2));

        SliderPage sliderPage3 = new SliderPage();
        sliderPage3.setTitle("Live Channel");
        sliderPage3.setDescription("See your favourite channels");
        sliderPage3.setImageDrawable(R.drawable.stream_1);
        sliderPage3.setBgColor(getResources().getColor(R.color.color3));

        addSlide(AppIntroFragment.newInstance(sliderPage3));

        // OPTIONAL METHODS
        // Override bar/separator color.
        setBarColor(Color.parseColor("#3F51B5"));
        setSeparatorColor(Color.parseColor("#2196F3"));

        // Hide Skip/Done button.
        showSkipButton(true);
        setProgressButtonEnabled(true);

        // Turn vibration on and set intensity.
        // NOTE: you will probably need to ask VIBRATE permission in Manifest.
        setVibrate(true);
        setVibrateIntensity(30);
        askForPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 3); // OR

    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);

        permissions();

    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);

        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        // Do something when the slide changes.
    }

    PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {

            try {
                Intent intent = new Intent(IntroActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onPermissionDenied(List<String> deniedPermissions) {
            Toast.makeText(IntroActivity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
        }
    };

    void permissions() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            boolean readGranted = PermissionUtils.isGranted(this, PermissionEnum.READ_EXTERNAL_STORAGE);

            if (!readGranted) {
                TedPermission.with(this)
                        .setPermissionListener(permissionlistener)
                        .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                        .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
                        )
                        .check();
            } else {
                Intent intent = new Intent(IntroActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();

            }
        } else {
            Intent intent = new Intent(IntroActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }
    }


}
