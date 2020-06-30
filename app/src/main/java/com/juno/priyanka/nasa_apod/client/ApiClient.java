package com.juno.priyanka.nasa_apod.client;


import com.juno.priyanka.nasa_apod.BuildConfig;
import com.juno.priyanka.nasa_apod.model.PlanetaryAPOD;
import com.juno.priyanka.nasa_apod.utils.AppConstant;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class ApiClient {
    public static final int CONNECTION_TIME_OUT = 20;
    private static final int READ_TIME_OUT = 20;
    private static final int WRITE_TIME_OUT = 20;

    //get client with fixed header
    public static Retrofit get(String baseUrl) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
        if (BuildConfig.DEBUG)
            okHttpClientBuilder.addInterceptor(interceptor);

        okHttpClientBuilder.connectTimeout(CONNECTION_TIME_OUT, TimeUnit.SECONDS);
        okHttpClientBuilder.readTimeout(READ_TIME_OUT, TimeUnit.SECONDS);
        okHttpClientBuilder.writeTimeout(WRITE_TIME_OUT, TimeUnit.SECONDS);

        if (BuildConfig.DEBUG)
            okHttpClientBuilder.addInterceptor(new Interceptor() {
                @Override
                public okhttp3.Response intercept(Chain chain) throws IOException {
                    Request.Builder builder = chain.request().newBuilder();

                    return chain.proceed(builder.build());
                }
            });

        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClientBuilder.build())
                .addConverterFactory(new NullOnEmptyConverterFactory())
                .addConverterFactory(ScalarsConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public interface ApiInterface {

        String BASE_URL = "https://api.nasa.gov/planetary/";

        @GET(AppConstant.ApiUrl.APOD)
        Observable<Response<PlanetaryAPOD>> getPOD();

        @GET(AppConstant.ApiUrl.APOD)
        Observable<Response<PlanetaryAPOD>> getPODdatewise(@Query("date") String date);

    }

}
