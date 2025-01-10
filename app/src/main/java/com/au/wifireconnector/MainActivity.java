package com.au.wifireconnector;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "WiFiReconnecterPrefs";
    private static final String LOG_KEY = "WiFiLog";

    private TextView logTextView;
    private final BroadcastReceiver logUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            displayLog(); // Update the log dynamically
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        logTextView = findViewById(R.id.logTextView);

        // Display the log
        displayLog();
        IntentFilter filter = new IntentFilter("com.au.wifireconnector.LOG_UPDATED");
        //registerReceiver(logUpdateReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
        registerReceiver(logUpdateReceiver, filter);
        // Schedule the worker to run every 15 minutes
        PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(InternetCheckWorker.class, 15, TimeUnit.MINUTES).build();

        WorkManager.getInstance(this).enqueue(workRequest);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(logUpdateReceiver);
    }

    private void displayLog() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String log = prefs.getString(LOG_KEY, "No events logged yet.");
        logTextView.setText(log);
    }
}
