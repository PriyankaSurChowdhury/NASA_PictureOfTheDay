package com.juno.priyanka.nasa_apod.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.juno.priyanka.nasa_apod.R;
import com.juno.priyanka.nasa_apod.utils.SharedPrefController;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FullScreenActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {
    ImageView image;
    String strUrl;

    String YOUTUBE_VIDEO_ID;
    YouTubePlayerView youTubePlayerView;
    private String GOOGLE_API_KEY = "AIzaSyAyMYw7oW0T-uycehw0e6owYjmg6WOll7Q";
    String media;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen);

        initUi();

        Intent intent = getIntent();
        if(intent != null)
        strUrl = String.valueOf(intent.getStringExtra("strUrl"));

        loadDataFullScreen();
    }

    private void initUi() {
        image = (ImageView) findViewById(R.id.image);
        youTubePlayerView = (YouTubePlayerView) findViewById(R.id.youtube_player);
        media = SharedPrefController.getSharedPreferencesController(FullScreenActivity.this).getStringValue("media");

    }

    private void loadDataFullScreen() {
        if (media.equals("image")) {
            loadImage();

        } else if (media.equals("video")) {
            loadVideo();
        }
    }

    private void loadImage() {
        image.setVisibility(View.VISIBLE);
        youTubePlayerView.setVisibility(View.GONE);

        Glide.with(FullScreenActivity.this)
                .load(strUrl)
                .apply(new RequestOptions()
                        .placeholder(R.drawable.ic_loading)
                        .optionalCenterCrop()
                )
                .into(image);
    }

    private void loadVideo() {
        image.setVisibility(View.GONE);
        youTubePlayerView.setVisibility(View.VISIBLE);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        youTubePlayerView.initialize(GOOGLE_API_KEY, this);

        String pattern = "(?<=watch\\?v=|/videos/|embed\\/)[^#\\&\\?]*";

        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(strUrl);

        if (matcher.find()) {
            YOUTUBE_VIDEO_ID = matcher.group();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean b) {
        if (strUrl != null) {
            player.setPlayerStateChangeListener(playerStateChangeListener);
            player.setPlaybackEventListener(playbackEventListener);
            player.loadVideo(YOUTUBE_VIDEO_ID);
        }
    }

    private YouTubePlayer.PlaybackEventListener playbackEventListener = new YouTubePlayer.PlaybackEventListener() {
        @Override
        public void onPlaying() {
        }

        @Override
        public void onPaused() {
        }

        @Override
        public void onStopped() {

        }

        @Override
        public void onBuffering(boolean b) {

        }

        @Override
        public void onSeekTo(int i) {

        }
    };

    YouTubePlayer.PlayerStateChangeListener playerStateChangeListener = new YouTubePlayer.PlayerStateChangeListener() {
        @Override
        public void onLoading() {

        }

        @Override
        public void onLoaded(String s) {

        }

        @Override
        public void onAdStarted() {
        }

        @Override
        public void onVideoStarted() {
        }

        @Override
        public void onVideoEnded() {
        }

        @Override
        public void onError(YouTubePlayer.ErrorReason errorReason) {

        }
    };

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

    }
}
