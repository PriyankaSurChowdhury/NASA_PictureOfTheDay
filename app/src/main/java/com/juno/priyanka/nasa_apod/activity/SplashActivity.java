package com.juno.priyanka.nasa_apod.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.RelativeLayout;

import com.juno.priyanka.nasa_apod.R;
import com.juno.priyanka.nasa_apod.client.ApiClient;
import com.juno.priyanka.nasa_apod.observer.ApodDataObserver;

import java.io.IOException;

import androidx.appcompat.app.AppCompatActivity;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import pl.droidsonroids.gif.GifDrawable;

public class SplashActivity extends AppCompatActivity {
    private static final int SPLASH_TIME_OUT = 3000;
    Context context;
    RelativeLayout rl_main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        context = this;

        loadPOD();
        initUi();
        splashHandler();
    }

    private void initUi() {
        rl_main = findViewById(R.id.rl_main);
        try {
            GifDrawable gifFromResource = new GifDrawable(getResources(), R.drawable.ic_shootstar);
            rl_main.setBackground(gifFromResource);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void splashHandler() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        }, SPLASH_TIME_OUT);

    }

    public void loadPOD() {
        ApodDataObserver apodDataObserver = new ApodDataObserver(context);
        ApiClient.ApiInterface apiInterface =
                ApiClient.get(ApiClient.ApiInterface.BASE_URL)
                        .create(ApiClient.ApiInterface.class);
        apiInterface.getPOD()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(apodDataObserver);
    }
}
