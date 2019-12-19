package com.example.asadabbas.iptvdemo.activities;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.example.asadabbas.iptvdemo.R;
import com.example.asadabbas.iptvdemo.adapter.ChannelAdapter;
import com.example.asadabbas.iptvdemo.model.Favourites;
import com.example.asadabbas.iptvdemo.parse.data.ExtInfo;
import com.example.asadabbas.iptvdemo.parse.data.Playlist;
import com.example.asadabbas.iptvdemo.parse.data.Track;
import com.example.asadabbas.iptvdemo.parse.exception.PlaylistParseException;
import com.example.asadabbas.iptvdemo.parse.merger.PlaylistMerger;
import com.example.asadabbas.iptvdemo.parse.parser.M3U8Parser;
import com.example.asadabbas.iptvdemo.parse.scanner.M3U8ItemScanner;
import com.example.asadabbas.iptvdemo.parse.writer.M3U8PlaylistWriter;
import com.example.asadabbas.iptvdemo.util.TinyDB;
import com.verticalseekbar.VerticalSeekBar;

import org.videolan.libvlc.IVLCVout;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;


public class IPTVPlayer extends AppCompatActivity implements IVLCVout.Callback {

    private static final String TAG = "PlayerFragment";
    String SAMPLE_URL = "http://s1.dailyiptv.net:8080/live/CXHnoViJaA/BcmKHDufr7/3524.ts";

    private SurfaceView mVideoSurface = null;

    private LibVLC mLibVLC = null;
    private MediaPlayer mMediaPlayer = null;

    VerticalSeekBar brightness;
    VerticalSeekBar volume;
    LinearLayout brightness_linear, volume_linear;

    ProgressBar pbProgress;
    ImageView lockView;
    ImageView fullscreen;
    ImageView favourite;
    ImageView settings;
    ImageView rotateScreen;
    TinyDB tinyDB = null;
    TextView numberTxt;
    FloatingActionButton channelFab;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        setContentView(R.layout.activity_reproductor);
        pbProgress = findViewById(R.id.pbProgress);
        lockView = findViewById(R.id.lock);
        fullscreen = findViewById(R.id.fullscreen);
        fullscreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fullscreen();
            }
        });
        init();
        brightness.setMax(225);
        fullscreen();
    }

    public void init() {

        tinyDB = new TinyDB(this);

        final ArrayList<String> args = new ArrayList<>();
        args.add("-vvv");
        mLibVLC = new LibVLC(this, args);

        mMediaPlayer = new MediaPlayer(mLibVLC);
        mVideoSurface = (SurfaceView) findViewById(R.id.video_surface);
        brightness = findViewById(R.id.brightness);
        volume = findViewById(R.id.volume);
        brightness_linear = findViewById(R.id.brightness_linear);
        volume_linear = findViewById(R.id.volume_linear);
        favourite = findViewById(R.id.favourite);
        settings = findViewById(R.id.settings);
        rotateScreen = findViewById(R.id.rotateScreen);
        numberTxt = findViewById(R.id.number);
        channelFab = findViewById(R.id.channel);
        VolumeControl();
        BrightnessControl();

        mMediaPlayer.setEventListener(new MediaPlayer.EventListener() {
            @Override
            public void onEvent(MediaPlayer.Event event) {
                switch (event.type) {
                    case MediaPlayer.Event.Buffering:
                        Log.d(TAG, "onEvent: Buffering");
                        break;
                    case MediaPlayer.Event.EncounteredError:
                        Log.d(TAG, "onEvent: EncounteredError");
                        break;
                    case MediaPlayer.Event.EndReached:
                        Log.d(TAG, "onEvent: EndReached");
                        break;
                    case MediaPlayer.Event.ESAdded:
                        Log.d(TAG, "onEvent: ESAdded");
                        break;
                    case MediaPlayer.Event.ESDeleted:
                        Log.d(TAG, "onEvent: ESDeleted");
                        break;
                    case MediaPlayer.Event.MediaChanged:
                        Log.d(TAG, "onEvent: MediaChanged");
                        break;
                    case MediaPlayer.Event.Opening:
                        pbProgress.setVisibility(View.VISIBLE);
                        Log.d(TAG, "onEvent: Opening");
                        break;
                    case MediaPlayer.Event.PausableChanged:
                        Log.d(TAG, "onEvent: PausableChanged");
                        break;
                    case MediaPlayer.Event.Paused:
                        Log.d(TAG, "onEvent: Paused");
                        break;
                    case MediaPlayer.Event.Playing:
                        pbProgress.setVisibility(View.GONE);
                        break;
                    case MediaPlayer.Event.PositionChanged:
//                        Log.d(TAG, "onEvent: PositionChanged");
                        break;
                    case MediaPlayer.Event.SeekableChanged:
                        Log.d(TAG, "onEvent: SeekableChanged");
                        break;
                    case MediaPlayer.Event.Stopped:
                        Log.d(TAG, "onEvent: Stopped");
                        break;
                    case MediaPlayer.Event.TimeChanged:
//                        Log.d(TAG, "onEvent: TimeChanged");
                        break;
                    case MediaPlayer.Event.Vout:
                        Log.d(TAG, "onEvent: Vout");
                        break;
                }
            }
        });

        boolean lock = tinyDB.getBoolean("lock");

        if (lock) {
            this.lockView.setImageDrawable(getResources().getDrawable(R.drawable.screen_rotation_locked));
        } else {
            this.lockView.setImageDrawable(getResources().getDrawable(R.drawable.screen_rotation_lock));
        }

        this.lockView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean lock = tinyDB.getBoolean("lock");

                if (lock) {
                    lockView.setImageDrawable(getResources().getDrawable(R.drawable.screen_rotation_lock));
                    tinyDB.putBoolean("lock", false);
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

                } else {
                    lockView.setImageDrawable(getResources().getDrawable(R.drawable.screen_rotation_locked));
                    tinyDB.putBoolean("lock", true);
                    int orientation = getResources().getConfiguration().orientation;
                    if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    } else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    }
                    // setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

                }
            }
        });

        favourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkFavourite(SAMPLE_URL, true);
            }
        });

        checkFavourite(SAMPLE_URL, false);

        showViews();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMediaPlayer.release();

        mLibVLC.release();
    }

    @Override
    public void onStart() {
        super.onStart();
        final IVLCVout vlcVout = mMediaPlayer.getVLCVout();
        vlcVout.setVideoView(mVideoSurface);
        vlcVout.attachViews();
        mMediaPlayer.getVLCVout().addCallback(this);
        Intent intent = getIntent();
        if (intent != null) {
            SAMPLE_URL = intent.getStringExtra("uri");
            channelList = new ArrayList<>();
            channelList = intent.getParcelableArrayListExtra("list");
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    if (SAMPLE_URL.contains("m3u") || SAMPLE_URL.contains("m3u_plus")) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                pbProgress.setVisibility(View.VISIBLE);
                            }
                        });
                        parseMu3(SAMPLE_URL);
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                playUrl(SAMPLE_URL);

                                if (channelList != null && channelList.size() > 0) {
                                    numberTxt.setText("" + channelList.size());
                                }
                            }
                        });
                    }
                }
            });
        }
    }

    void playUrl(String url) {
        Media media = new Media(mLibVLC, Uri.parse(url));
        mMediaPlayer.setMedia(media);
        media.release();
        mMediaPlayer.play();
        mMediaPlayer.setVideoTrackEnabled(true);
    }

    @Override
    public void onStop() {
        super.onStop();
        mMediaPlayer.stop();
        mMediaPlayer.getVLCVout().detachViews();
        mMediaPlayer.getVLCVout().removeCallback(this);
    }

    @Override
    public void onNewLayout(IVLCVout vlcVout, int width, int height, int visibleWidth, int visibleHeight, int sarNum, int sarDen) {

    }

    @Override
    public void onSurfacesCreated(IVLCVout vlcVout) {
    }

    @Override
    public void onSurfacesDestroyed(IVLCVout vlcVout) {
    }

    private boolean checkSystemWritePermission(int progress) {
        boolean retVal = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            retVal = Settings.System.canWrite(this);

            if (retVal) {
                Log.d("brightness", "" + progress);
                //setBrightness(brightness, progress);
            } else {
                openAndroidPermissionsMenu();
            }
        }
        return retVal;
    }

    private void openAndroidPermissionsMenu() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void setBrightness(View view, int brightness) {


        Settings.System.putInt(this.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);

        Settings.System.putInt(this.getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS, brightness);

        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.screenBrightness = brightness;// 100 / 100.0f;
        getWindow().setAttributes(lp);

    }

    public void RotateScreen(View view) {
        changeScreenOrientation();
    }

    private void changeScreenOrientation() {
        int orientation = this.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            tinyDB.putBoolean("lock", true);
            lockView.setImageDrawable(getResources().getDrawable(R.drawable.screen_rotation_locked));
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            tinyDB.putBoolean("lock", true);
            lockView.setImageDrawable(getResources().getDrawable(R.drawable.screen_rotation_locked));
        }

      /*  if (Settings.System.getInt(getContentResolver(),
                Settings.System.ACCELEROMETER_ROTATION, 0) == 1) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
                }
            }, 4000);
        }*/

    }

    void fullscreen() {

        boolean isFullScreen = tinyDB.getBoolean("isFullScreen");
        if (isFullScreen) {
            tinyDB.putBoolean("isFullScreen", false);
            mVideoSurface.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            fullscreen.setImageDrawable(getResources().getDrawable(R.drawable.arrow_expanded_all));
        } else {
            tinyDB.putBoolean("isFullScreen", true);
            mVideoSurface.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 900));
            fullscreen.setImageDrawable(getResources().getDrawable(R.drawable.arrow_expand_all));
        }
    }

    private AudioManager audioManager = null;

    private void VolumeControl() {
        try {

            audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            volume.setMax(audioManager
                    .getStreamMaxVolume(AudioManager.STREAM_MUSIC));
            volume.setProgress(audioManager
                    .getStreamVolume(AudioManager.STREAM_MUSIC));
            volume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onStopTrackingTouch(SeekBar arg0) {
                }

                @Override
                public void onStartTrackingTouch(SeekBar arg0) {
                }

                @Override
                public void onProgressChanged(SeekBar arg0, int progress, boolean arg2) {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                            progress, 0);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private int bright;

    private void BrightnessControl() {
        //Set the seekbar range between 0 and 255
        //seek bar settings//
        //sets the range between 0 and 255


        brightness.setMax(255);
        //set the seek bar progress to 1
        brightness.setKeyProgressIncrement(1);

        try {
            //Get the current system brightness
            bright = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
            //Throw an error case it couldn't be retrieved
            Log.e("Error", "Cannot access system brightness");
            e.printStackTrace();
        }

        //Set the progress of the seek bar based on the system's brightness
        brightness.setProgress(bright);

        //Register OnSeekBarChangeListener, so it can actually change values
        brightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onStopTrackingTouch(SeekBar seekBar) {


            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                //Nothing handled here
            }

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                if (!checkSystemWritePermission(progress))
                    return;

                //Set the minimal brightness level
                //if seek bar is 20 or any value below
                if (progress <= 20) {
                    //Set the brightness to 20
                    bright = 20;
                } else //brightness is greater than 20
                {
                    //Set brightness variable based on the progress bar
                    bright = progress;
                }

                Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);

                //Set the system brightness using the brightness variable value
                Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, bright);
                //Get the current window attributes
                WindowManager.LayoutParams layoutpars = getWindow().getAttributes();
                //Set the brightness of this window
                layoutpars.screenBrightness = bright / (float) 255;
                //Apply attribute changes to this window
                getWindow().setAttributes(layoutpars);

            }
        });
    }

    void hideViews() {

        brightness.setVisibility(View.GONE);
        volume.setVisibility(View.GONE);
        brightness_linear.setVisibility(View.GONE);
        volume_linear.setVisibility(View.GONE);
        lockView.setVisibility(View.GONE);
        fullscreen.setVisibility(View.GONE);
        favourite.setVisibility(View.GONE);
        settings.setVisibility(View.GONE);
        rotateScreen.setVisibility(View.GONE);
        channelFab.setVisibility(View.GONE);
        numberTxt.setVisibility(View.GONE);

        Animation animLeftToRight;
        animLeftToRight = AnimationUtils.loadAnimation(this, R.anim.fade_out);

        volume.setAnimation(animLeftToRight);
        brightness.setAnimation(animLeftToRight);
    }

    void showViews() {

        brightness.setVisibility(View.VISIBLE);
        volume.setVisibility(View.VISIBLE);
        brightness_linear.setVisibility(View.VISIBLE);
        volume_linear.setVisibility(View.VISIBLE);
        lockView.setVisibility(View.VISIBLE);
        fullscreen.setVisibility(View.VISIBLE);
        favourite.setVisibility(View.VISIBLE);
        settings.setVisibility(View.VISIBLE);
        rotateScreen.setVisibility(View.VISIBLE);
        channelFab.setVisibility(View.VISIBLE);
        numberTxt.setVisibility(View.VISIBLE);

        Animation animRightToLeft;
        animRightToLeft = AnimationUtils.loadAnimation(this, R.anim.fade_in);

        volume.setAnimation(animRightToLeft);
        brightness.setAnimation(animRightToLeft);
    }

    public void TouchScreen(View view) {

        boolean isVisible = tinyDB.getBoolean("isViewVisible");
        if (isVisible) {
            tinyDB.putBoolean("isViewVisible", false);
            hideViews();
        } else {
            tinyDB.putBoolean("isViewVisible", true);
            showViews();
        }
    }

    ArrayList<ExtInfo> channelList = null;

    void parseMu3(String url) {
        channelList = new ArrayList<>();

        try {

            if (url.contains("m3u_plus")) {
                url = url.substring(0, url.indexOf("m3u_plus"));
                url = url + "m3u_plus";
            } else if (url.contains("m3u")) {
                url = url.substring(0, url.indexOf("m3u"));
                url = url + "m3u";
            }

            M3U8Parser m3U8Parser = new M3U8Parser(new URL(url).openStream(), M3U8ItemScanner.Encoding.UTF_8);
            try {

                Playlist playlist1 = m3U8Parser.parse();
                Map<String, Set<Track>> map = playlist1.getTrackSetMap();
                // using for-each loop for iteration over Map.entrySet()
                for (Map.Entry<String, Set<Track>> entry : map.entrySet()) {
                    for (Track track : entry.getValue()) {
                        ExtInfo extInfo = track.getExtInfo();
                        String urlTv = track.getUrl();
                        extInfo.setTvUrl(urlTv);
                        channelList.add(extInfo);
                    }
                }

              /*
                PlaylistMerger playlistMerger = new PlaylistMerger();
                Playlist playlist = playlistMerger.mergePlaylist(playlist1);
                M3U8PlaylistWriter m3U8PlaylistWriter = new M3U8PlaylistWriter();
                ByteArrayOutputStream byteArrayOutputStream = m3U8PlaylistWriter.getByteArrayOutputStream(playlist);
*/
            } catch (PlaylistParseException e) {
                e.printStackTrace();
            } finally {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (channelList != null && channelList.size() > 0) {
/*
                            playUrl(channelList.get(0).getTvUrl());
*/
                            numberTxt.setText("" + channelList.size());
                        }

                        Channel(null);

                        pbProgress.setVisibility(View.GONE);

                    }
                });
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    public void Channel(View view) {

        if (channelList != null && channelList.size() != 0) {

            Intent intent = new Intent(this, ChannelActivity.class);
            intent.putParcelableArrayListExtra("list", channelList);
            startActivityForResult(intent, 100);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == RESULT_OK) {

            SAMPLE_URL = data.getStringExtra("url");

            Intent shareIntent = new Intent(this, IPTVPlayer.class);
            shareIntent.putExtra("uri", SAMPLE_URL);
            shareIntent.putParcelableArrayListExtra("list", channelList);
            startActivity(shareIntent);
            new TinyDB(this).putBoolean("isFullScreen", false);

            finish();
        }
    }

    void checkFavourite(String str, boolean isclick) {

        if (isFavourite(str)) {
            if (isclick) {
                favourite.setImageDrawable(getDrawable(R.drawable.heart_outline));
                unFavourite(str);
            } else favourite.setImageDrawable(getDrawable(R.drawable.heart));
        } else {
            if (isclick) {
                favourite(str);
                if (isFavourite(str)) {
                    favourite.setImageDrawable(getDrawable(R.drawable.heart));
                }
            }
        }
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
        favourites.setStream(true);
        favourites.setPath(album);
        favourites.save();
    }

}





