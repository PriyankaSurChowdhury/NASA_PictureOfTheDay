package com.juno.priyanka.nasa_apod.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

public class SharedPrefController {
    private final SharedPreferences preferences;
    private static final String PREF_FILE_NAME = "android.content.SharedPreferences";
    final static Object lock = new Object();
    static SharedPrefController sharedPreferencesController;



    private SharedPrefController(Context context) {
        preferences = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
    }

    public static SharedPrefController getSharedPreferencesController(Context context) {
        synchronized (lock) {
            if (sharedPreferencesController == null) {
                sharedPreferencesController = new SharedPrefController(context);
            }
            return sharedPreferencesController;
        }
    }
    public void removeFromSharedPreferences(Context mContext, String key) {
        if (mContext != null) {
            if (preferences != null)
                preferences.edit().remove(key).commit();
        }
    }

    public void checkForNullKey(String key) {
        if (key == null) {
            throw new NullPointerException();
        }
    }

    public void checkForNullValue(String value) {
        if (value == null) {
            throw new NullPointerException();
        }
    }

    public void setString(String key, String value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public String getStringValue(String key) {
        return preferences.getString(key, null);
    }

    public void setLongValue(String key, long value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    public long getLongValue(String key) {
        return preferences.getLong(key, 0);
    }

    public void putObject(String key, Object object) {
        String value = new Gson().toJson(object);
        checkForNullKey(key);
        checkForNullValue(value);
        preferences.edit().putString(key, value).apply();
    }

    public Object getObject(String key, Class<?> clazz) {
        Object obj = null;
        if (clazz != null) {
            obj = new Gson().fromJson(getStringValue(key), clazz);
        }
        return obj;
    }
}
