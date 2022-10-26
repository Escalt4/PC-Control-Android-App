package com.example.pccontrol;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.Nullable;

public class SocketService extends IntentService {
    static final String LOG_TAG = "MyApp";

    public SocketService() {
        super("MyApp");
    }

    @Override
    public void onCreate() {
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.e(LOG_TAG, "заработало");
    }
}

