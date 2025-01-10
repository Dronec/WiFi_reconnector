package com.au.wifireconnector;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class InternetCheckWorker extends Worker {

    private static final String PREFS_NAME = "WiFiReconnecterPrefs";
    private static final String LOG_KEY = "WiFiLog";
    public InternetCheckWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        logEvent("Checking internet...");
        if (!isInternetAvailable()) {
            // Disable WiFi for 10 seconds
            if (wifiManager != null) {
                Log.w("InternetCheckWorker", "Turning off WiFi...");
                logEvent("Turning off WiFi...");
                wifiManager.setWifiEnabled(false);
                try {
                    Thread.sleep(10000); // Sleep for 10 seconds
                } catch (InterruptedException e) {
                    Log.e("InternetCheckWorker", e.getMessage());
                }
                Log.w("InternetCheckWorker", "Turning on WiFi...");
                logEvent("Turning on WiFi...");
                wifiManager.setWifiEnabled(true);
            }
        }

        return Result.success();
    }

    private boolean isInternetAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                Network activeNetwork = connectivityManager.getActiveNetwork();
                if (activeNetwork != null) {
                    NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(activeNetwork);
                    if (capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)) {
                        // Make a test HTTP request to confirm connectivity
                        return checkHttpConnection();
                    }
                }
            } else {
                NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
                    return checkHttpConnection();
                }
            }
        }
        return false;
    }

    private boolean checkHttpConnection() {
        try {
            Log.i("checkHttpConnection", "Checking internet connection");
            URL url = new URL("https://www.microsoft.com/en-au/");
            //URL url = new URL("https://www.microsoft.co/");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setConnectTimeout(5000); // 5 seconds timeout
            urlConnection.connect();

            int responseCode = urlConnection.getResponseCode();
            return responseCode == 200;
        } catch (IOException e) {
            Log.e("checkHttpConnection", e.getMessage());
        }
        return false;
    }

    private void sendLogUpdateBroadcast() {
        Intent intent = new Intent("com.au.wifireconnector.LOG_UPDATED");
        getApplicationContext().sendBroadcast(intent);
    }

    private void logEvent(String message) {
        SharedPreferences prefs = getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        String existingLog = prefs.getString(LOG_KEY, "");
        String newLog = existingLog + timestamp + " - " + message + "\n";

        editor.putString(LOG_KEY, newLog);
        editor.apply();
        sendLogUpdateBroadcast();
    }
}

