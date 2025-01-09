package com.au.wifireconnector;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Schedule the worker to run every 15 minutes
        PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(
                InternetCheckWorker.class, 15, TimeUnit.MINUTES)
                .build();

        WorkManager.getInstance(this).enqueue(workRequest);
    }
}
