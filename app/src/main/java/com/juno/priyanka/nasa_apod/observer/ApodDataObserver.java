package com.juno.priyanka.nasa_apod.observer;

import android.content.Context;

import com.juno.priyanka.nasa_apod.model.PlanetaryAPOD;
import com.juno.priyanka.nasa_apod.utils.SharedPrefController;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import retrofit2.Response;

public class ApodDataObserver implements Observer<Response<PlanetaryAPOD>> {
    PlanetaryAPOD planetaryAPODList;
    private Context context;

    public ApodDataObserver(Context context) {
        this.context = context;
    }

    @Override
    public void onSubscribe(Disposable d) {

    }

    @Override
    public void onNext(Response<PlanetaryAPOD> planetaryAPODResponse) {
        try {
            if (planetaryAPODResponse.code() == 200 && planetaryAPODResponse.body() != null) {
                planetaryAPODList = planetaryAPODResponse.body();

                SharedPrefController.getSharedPreferencesController(context).putObject("planetaryAPODList", planetaryAPODList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(Throwable e) {

    }

    @Override
    public void onComplete() {

    }
}
