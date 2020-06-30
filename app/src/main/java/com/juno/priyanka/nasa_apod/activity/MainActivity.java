package com.juno.priyanka.nasa_apod.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.juno.priyanka.nasa_apod.R;
import com.juno.priyanka.nasa_apod.client.ApiClient;
import com.juno.priyanka.nasa_apod.model.PlanetaryAPOD;
import com.juno.priyanka.nasa_apod.utils.SharedPrefController;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.appcompat.app.AppCompatActivity;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    PlanetaryAPOD planetaryAPODList;
    TextView tv_title, tv_description, tv_limit;
    ImageView iv_apod, iv_calendar, iv_zoomPlay;
    Calendar calendar;
    int year, month, dayOfMonth;
    String date, YOUTUBE_VIDEO_ID, url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        planetaryAPODList = (PlanetaryAPOD) SharedPrefController.getSharedPreferencesController(MainActivity.this).getObject("planetaryAPODList", PlanetaryAPOD.class);

        initUi();
    }

    private void initUi() {
        tv_title = findViewById(R.id.tv_title);
        tv_description = findViewById(R.id.tv_description);

        iv_zoomPlay = findViewById(R.id.iv_zoomPlay);
        iv_zoomPlay.setOnClickListener(this);

        iv_apod = findViewById(R.id.iv_apod);

        iv_calendar = findViewById(R.id.iv_calendar);
        iv_calendar.setOnClickListener(this);

        tv_limit = findViewById(R.id.tv_limit);
        tv_limit.setVisibility(View.GONE);

        setData();
    }

    private void setData() {
        tv_title.setText(planetaryAPODList.getTitle());
        tv_description.setText(planetaryAPODList.getExplanation());

        url = planetaryAPODList.getUrl();
        String pattern = "(?<=watch\\?v=|/videos/|embed\\/)[^#\\&\\?]*";

        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(url);

        if (matcher.find()) {
            YOUTUBE_VIDEO_ID = matcher.group();
        }

        if (planetaryAPODList.getMediaType().equals("image")) {

            iv_zoomPlay.setImageDrawable(getResources().getDrawable(R.drawable.ic_zoom_in_out));

            Glide.with(MainActivity.this)
                    .load(planetaryAPODList.getHdurl())
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.ic_loading)
                            .optionalCenterCrop()
                    )
                    .into(iv_apod);

            SharedPrefController.getSharedPreferencesController(MainActivity.this).setString("media", "image");

        } else if (planetaryAPODList.getMediaType().equals("video")) {
            iv_zoomPlay.setImageDrawable(getResources().getDrawable(R.drawable.ic_play));
            String url = "https://img.youtube.com/vi/" + YOUTUBE_VIDEO_ID + "/0.jpg";
            Glide.with(MainActivity.this).load(url).into(iv_apod);

            SharedPrefController.getSharedPreferencesController(MainActivity.this).setString("media", "video");

        } else {
            tv_limit.setVisibility(View.VISIBLE);
            Toast.makeText(MainActivity.this, (MainActivity.this).getResources().getString(R.string.limit_exceeded), Toast.LENGTH_SHORT).show();
        }

    }

    public void onShakeImage() {
        Animation shake;
        shake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);
        iv_calendar.startAnimation(shake);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.iv_calendar) {
            openDatePicker();

        } else if (id == R.id.iv_zoomPlay) {
            String media = SharedPrefController.getSharedPreferencesController(MainActivity.this).getStringValue("media");

            if (media.equals("image")) {
                Intent i = new Intent(MainActivity.this, FullScreenActivity.class);
                i.putExtra("strUrl", planetaryAPODList.getHdurl());
                startActivity(i);

            } else if (media.equals("video")) {
                Intent i = new Intent(MainActivity.this, FullScreenActivity.class);
                i.putExtra("strUrl", planetaryAPODList.getUrl());
                startActivity(i);
            }
        }
    }

    private void openDatePicker() {
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        int correctMonth = month + 1;
                        date = year + "-" + correctMonth + "-" + day;
                        SharedPrefController.getSharedPreferencesController(MainActivity.this).setString("date", date);
                        loadPODdate(date);
                    }
                }, year, month, dayOfMonth);

        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void loadPODdate(String date) {

        ApiClient.ApiInterface apiInterface = ApiClient.get(ApiClient.ApiInterface.BASE_URL).create(ApiClient.ApiInterface.class);
        apiInterface.getPODdatewise(date).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<PlanetaryAPOD>>() {

                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Response<PlanetaryAPOD> response) {
                        try {
                            if (response.code() == 200 && response.body() != null) {
                                planetaryAPODList = response.body();

                                tv_title.setText(planetaryAPODList.getTitle());
                                tv_description.setText(planetaryAPODList.getExplanation());

                                url = planetaryAPODList.getUrl();
                                String pattern = "(?<=watch\\?v=|/videos/|embed\\/)[^#\\&\\?]*";

                                Pattern compiledPattern = Pattern.compile(pattern);
                                Matcher matcher = compiledPattern.matcher(url);

                                if (matcher.find()) {
                                    YOUTUBE_VIDEO_ID = matcher.group();
                                }

                                if (planetaryAPODList.getMediaType().equals("image")) {

                                    iv_zoomPlay.setImageDrawable(getResources().getDrawable(R.drawable.ic_zoom_in_out));

                                    Glide.with(MainActivity.this)
                                            .load(planetaryAPODList.getHdurl())
                                            .apply(new RequestOptions()
                                                    .placeholder(R.drawable.ic_loading)
                                                    .optionalCenterCrop()
                                            )
                                            .into(iv_apod);
                                    SharedPrefController.getSharedPreferencesController(MainActivity.this).setString("media", "image");

                                } else if (planetaryAPODList.getMediaType().equals("video")) {
                                    iv_zoomPlay.setImageDrawable(getResources().getDrawable(R.drawable.ic_play));

                                    String url = "https://img.youtube.com/vi/" + YOUTUBE_VIDEO_ID + "/0.jpg";
                                    Glide.with(MainActivity.this).load(url).into(iv_apod);
                                    SharedPrefController.getSharedPreferencesController(MainActivity.this).setString("media", "video");

                                } else {
                                    tv_limit.setVisibility(View.VISIBLE);
                                    Toast.makeText(MainActivity.this, (MainActivity.this).getResources().getString(R.string.limit_exceeded), Toast.LENGTH_SHORT).show();
                                }

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("err", "" + e);

                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    @Override
    protected void onStart() {
        super.onStart();
        onShakeImage();
    }
}
