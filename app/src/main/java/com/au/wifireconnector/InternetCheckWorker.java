package com.au.wifireconnector;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.wifi.WifiManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class InternetCheckWorker extends Worker {

    public InternetCheckWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        if (!isInternetAvailable()) {
            // Disable WiFi for 10 seconds
            if (wifiManager != null) {
                Log.w("InternetCheckWorker","Turning off WiFi...");
                wifiManager.setWifiEnabled(false);
                try {
                    Thread.sleep(10000); // Sleep for 10 seconds
                } catch (InterruptedException e) {
                    Log.e("InternetCheckWorker", e.getMessage());
                }
                Log.w("InternetCheckWorker","Turning on WiFi...");
                wifiManager.setWifiEnabled(true);
            }
        }

        return Result.success();
    }

    private boolean isInternetAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            Network activeNetwork = connectivityManager.getActiveNetwork();
            if (activeNetwork != null) {
                NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(activeNetwork);
                if (capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)) {
                    // Make a test HTTP request to confirm connectivity
                    return checkHttpConnection();
                }
            }
        }
        return false;
    }

    private boolean checkHttpConnection() {
        try {
            Log.i("checkHttpConnection","Checking internet connection");
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
}

