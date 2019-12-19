package com.example.asadabbas.iptvdemo.activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.MimeTypeMap;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.example.asadabbas.iptvdemo.R;
import com.example.asadabbas.iptvdemo.model.Favourites;
import com.example.asadabbas.iptvdemo.util.Constants;
import com.example.asadabbas.iptvdemo.util.TinyDB;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.decoder.DecoderCounters;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.LoopingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.video.VideoRendererEventListener;
import com.verticalseekbar.VerticalSeekBar;



/*
Created by: Asad Abbas
Date: 8/2017

Description:
This Example app was created to show a simple example of ExoPlayer Version 2.8.4.
There is an option to play mp4 files or live stream content.
Exoplayer provides options to play many different formats, so the code can easily be tweaked to play the requested format.
Scroll down to "ADJUST HERE:" I & II to change between sources.
Keep in mind that m3u8 files might be stale and you would need new sources.
 */

public class MediaPlayer extends AppCompatActivity implements VideoRendererEventListener {


    private static final String TAG = "MainActivity";
    private PlayerView simpleExoPlayerView;
    private SimpleExoPlayer player;
    ImageView audio_place;
    ImageView lockView;
    ImageView fullscreen;
    ImageView favourite;
    ImageView settings;
    ImageView rotateScreen;
    VerticalSeekBar brightness;
    VerticalSeekBar volume;
    TinyDB tinyDB = null;
    LinearLayout brightness_linear, volume_linear;
    String ur = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player_main);

        tinyDB = new TinyDB(this);

        Intent intent = getIntent();

        if (intent != null) {

            ur = intent.getStringExtra("uri");

        }
        brightness_linear = findViewById(R.id.brightness_linear);
        volume_linear = findViewById(R.id.volume_linear);
        brightness = findViewById(R.id.brightness);
        volume = findViewById(R.id.volume);
        favourite = findViewById(R.id.favourite);
        settings = findViewById(R.id.settings);
        rotateScreen = findViewById(R.id.rotateScreen);
        lockView = findViewById(R.id.lock);
        fullscreen = findViewById(R.id.fullscreen);
        fullscreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fullscreen();
            }
        });
        brightness.setMax(225);


        VolumeControl();
        BrightnessControl();


//// I. ADJUST HERE:
////CHOOSE CONTENT: LiveStream / SdCard
//
////LIVE STREAM SOURCE: * Livestream links may be out of date so find any m3u8 files online and replace:
//
////        Uri mp4VideoUri =Uri.parse("http://81.7.13.162/hls/ss1/index.m3u8"); //random 720p source
////        Uri mp4VideoUri =Uri.parse("http://54.255.155.24:1935//Live/_definst_/amlst:sweetbcha1novD235L240P/playlist.m3u8"); //Radnom 540p indian channel
//        Uri mp4VideoUri =Uri.parse("http://cbsnewshd-lh.akamaihd.net/i/CBSNHD_7@199302/index_700_av-p.m3u8"); //CNBC
        Uri mp4VideoUri = Uri.parse(ur); //ABC NEWS
////        Uri mp4VideoUri =Uri.parse("FIND A WORKING LINK ABD PLUg INTO HERE"); //PLUG INTO HERE<------------------------------------------
//
//
////VIDEO FROM SD CARD: (2 steps. set up file and path, then change videoSource to get the file)
////        String urimp4 = "path/FileName.mp4"; //upload file to device and add path/name.mp4
////        Uri mp4VideoUri = Uri.parse(Environment.getExternalStorageDirectory().getAbsolutePath()+urimp4);


        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter(); //test

        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector =
                new DefaultTrackSelector(videoTrackSelectionFactory);

        // 2. Create the player
        player = ExoPlayerFactory.newSimpleInstance(this, trackSelector);

        simpleExoPlayerView = new SimpleExoPlayerView(this);
        simpleExoPlayerView = (SimpleExoPlayerView) findViewById(R.id.player_view);

        audio_place = findViewById(R.id.audio_place);

/*        String ext = ur.substring(ur.indexOf(".") + 1);

        if (ext.equalsIgnoreCase("mp3") || ext.equalsIgnoreCase("aac")
                || ext.equalsIgnoreCase("wma") || ext.equalsIgnoreCase("wav")
                || ext.equalsIgnoreCase("wma")) {
            audio_place.setVisibility(View.VISIBLE);
        } else
            audio_place.setVisibility(View.GONE);*/

        int h = simpleExoPlayerView.getResources().getConfiguration().screenHeightDp;
        int w = simpleExoPlayerView.getResources().getConfiguration().screenWidthDp;
        Log.v(TAG, "height : " + h + " weight: " + w);
        ////Set media controller
        simpleExoPlayerView.setUseController(true);//set to true or false to see controllers
        simpleExoPlayerView.requestFocus();
        // Bind the player to the view.
        simpleExoPlayerView.setPlayer(player);

        // Measures bandwidth during playback. Can be null if not required.
        // Produces DataSource instances through which media data is loaded.
        //  DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "exoplayer2example"), bandwidthMeter);
        // This is the MediaSource representing the media to be played.
//        MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(liveStreamUri);

        //// II. ADJUST HERE:

        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "exoplayer2example"), bandwidthMeter);
        ////Produces Extractor instances for parsing the media data.
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();

        //This is the MediaSource representing the media to be played:
        //FOR SD CARD SOURCE:
        MediaSource videoSource = new ExtractorMediaSource(mp4VideoUri, dataSourceFactory, extractorsFactory, null, null);

        //FOR LIVESTREAM LINK:
        //MediaSource videoSource = new HlsMediaSource(mp4VideoUri, dataSourceFactory, 1, null, null);
        final LoopingMediaSource loopingSource = new LoopingMediaSource(videoSource);
        // Prepare the player with the source.
        player.prepare(videoSource);

        player.addListener(new ExoPlayer.EventListener() {

            @Override
            public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
                Log.v(TAG, "Listener-onTracksChanged... ");
            }

            @Override
            public void onLoadingChanged(boolean isLoading) {

            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                Log.v(TAG, "Listener-onPlayerStateChanged..." + playbackState + "|||isDrawingCacheEnabled():" + simpleExoPlayerView.isDrawingCacheEnabled());
            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {

            }

            @Override
            public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {
                Log.v(TAG, "Listener-onPlayerError...");
                player.stop();
                player.prepare(loopingSource);
                player.setPlayWhenReady(true);
            }

            @Override
            public void onPositionDiscontinuity(int reason) {

            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

            }

            @Override
            public void onSeekProcessed() {

            }
        });
        player.setPlayWhenReady(true); //run file/link when ready to play.
        player.setVideoDebugListener(this);

        showViews();

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
                checkFavourite(ur, true);
            }
        });

        checkFavourite(ur, false);
    }

    @Override
    public void onVideoEnabled(DecoderCounters counters) {

    }

    @Override
    public void onVideoDecoderInitialized(String decoderName, long initializedTimestampMs, long initializationDurationMs) {

    }

    @Override
    public void onVideoInputFormatChanged(Format format) {

    }

    @Override
    public void onDroppedFrames(int count, long elapsedMs) {

    }

    @Override
    public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
        Log.v(TAG, "onVideoSizeChanged [" + " width: " + width + " height: " + height + "]");
        //resolutionTextView.setText("RES:(WxH):" + width + "X" + height + "\n           " + height + "p");//shows video info
    }

    @Override
    public void onRenderedFirstFrame(Surface surface) {

    }

    @Override
    public void onVideoDisabled(DecoderCounters counters) {

    }
//-------------------------------------------------------ANDROID LIFECYCLE---------------------------------------------------------------------------------------------

    @Override
    protected void onStop() {
        super.onStop();
        Log.v(TAG, "onStop()...");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.v(TAG, "onStart()...");
    }

    @Override
    protected void onResume() {
        super.onResume();

        player.seekTo(Constants.POSITION);


    }

    @Override
    protected void onPause() {

        super.onPause();

        if (player != null) {
            Constants.POSITION = player.getCurrentPosition();
            player.stop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "onDestroy()...");
        player.release();
    }

    private static String getMimeType(String fileUrl) {
        String extension = MimeTypeMap.getFileExtensionFromUrl(fileUrl);
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
    }

    void fullscreen() {

        boolean isFullScreen = tinyDB.getBoolean("isFullScreen");
        if (isFullScreen) {
            tinyDB.putBoolean("isFullScreen", false);
            simpleExoPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
            fullscreen.setImageDrawable(getResources().getDrawable(R.drawable.arrow_expanded_all));
        } else {
            tinyDB.putBoolean("isFullScreen", true);
            simpleExoPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
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

                    if (!checkSystemWritePermission(progress))
                        return;

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

    public void RotateScreen(View view) {
        changeScreenOrientation();
    }

    void hideViews() {

        brightness.setVisibility(View.GONE);
        volume.setVisibility(View.GONE);
        lockView.setVisibility(View.GONE);
        fullscreen.setVisibility(View.GONE);
        favourite.setVisibility(View.GONE);
        settings.setVisibility(View.GONE);
        brightness_linear.setVisibility(View.GONE);
        volume_linear.setVisibility(View.GONE);
        rotateScreen.setVisibility(View.GONE);

        Animation animLeftToRight;
        animLeftToRight = AnimationUtils.loadAnimation(this, R.anim.fade_out);

        volume.setAnimation(animLeftToRight);
        brightness.setAnimation(animLeftToRight);

        simpleExoPlayerView.hideController();
    }

    void showViews() {

        brightness.setVisibility(View.VISIBLE);
        volume.setVisibility(View.VISIBLE);
        lockView.setVisibility(View.VISIBLE);
        fullscreen.setVisibility(View.VISIBLE);
        favourite.setVisibility(View.VISIBLE);
        settings.setVisibility(View.VISIBLE);
        rotateScreen.setVisibility(View.VISIBLE);
        brightness_linear.setVisibility(View.VISIBLE);
        volume_linear.setVisibility(View.VISIBLE);
        Animation animRightToLeft;
        animRightToLeft = AnimationUtils.loadAnimation(this, R.anim.fade_in);

        volume.setAnimation(animRightToLeft);
        brightness.setAnimation(animRightToLeft);

        simpleExoPlayerView.showController();
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


}
