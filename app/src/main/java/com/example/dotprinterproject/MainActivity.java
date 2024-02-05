package com.example.dotprinterproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.thanosfisherman.wifiutils.WifiUtils;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private WifiManager wifiManager;
    private BroadcastReceiver wifiScanReceiver;
    TextView mainText;
    WifiManager mainWifi;
    WifiReceiver1 receiverWifi;
    List<ScanResult> wifiList;
    StringBuilder sb = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //
        mainText = (TextView) findViewById(R.id.tv1);
        mainWifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        if (mainWifi.isWifiEnabled() == false)
        {
            // If wifi disabled then enable it
            Toast.makeText(getApplicationContext(), "wifi is disabled..making it enabled",
                    Toast.LENGTH_LONG).show();
            mainWifi.setWifiEnabled(true);
        }

        receiverWifi = new WifiReceiver1();
        registerReceiver(receiverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        mainWifi.startScan();
        mainText.setText("Starting Scan...");

    }
    private void getScanResults(@NonNull final List<ScanResult> results)
    {
        if (results.isEmpty())
        {
            Log.i("Messss", "SCAN RESULTS IT'S EMPTY");
            return;
        }
        Log.i("Messss", "GOT SCAN RESULTS " + results);
    }
    public void wificlicked(View view) {
       /* mainWifi.startScan();
        mainText.setText("Starting Scan");

        */
        WifiUtils.withContext(getApplicationContext()).scanWifi(this::getScanResults).start();
     /*
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        if (!wifiManager.isWifiEnabled()) {
            // Ensure that Wi-Fi is enabled
            wifiManager.setWifiEnabled(true);
            Log.e("enable","Enable");
        }
        Log.e("ggg","Enable");
        wifiScanReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(intent.getAction())) {
                    List<ScanResult> scanResults = wifiManager.getScanResults();
                    Log.e("aaaaaaa","Enable"+scanResults);
                    displayWifiNetworks(scanResults);
                }
            }
        };

        // Register the BroadcastReceiver to receive scan results
        registerReceiver(wifiScanReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        // Start a Wi-Fi scan
        wifiManager.startScan();
      */
    }
    private void displayWifiNetworks(List<ScanResult> scanResults) {
        for (ScanResult result : scanResults) {
            // Access information about each Wi-Fi network
            String ssid = result.SSID;
            String bssid = result.BSSID;
            int signalStrength = result.level;

            // Process the information as needed
            // For example, log it or display it in a TextView
            // ...

            // Here, let's just show a toast with SSID and signal strength
            String toastMessage = "SSID: " + ssid + "\nBSSID: " + bssid + "\nSignal Strength: " + signalStrength + " dBm";
            Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        // Unregister the BroadcastReceiver when the activity is destroyed
        unregisterReceiver(wifiScanReceiver);
        super.onDestroy();
    }



    protected void onPause() {
        unregisterReceiver(receiverWifi);
        super.onPause();
    }

    protected void onResume() {
        registerReceiver(receiverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        super.onResume();
    }

    // Broadcast receiver class called its receive method
    // when number of wifi connections changed

    class WifiReceiver1 extends BroadcastReceiver {


        // This method call when number of wifi connections changed
        public void onReceive(Context c, Intent intent) {
            Log.e("tttttt","IIII");

            sb = new StringBuilder();
            wifiList = mainWifi.getScanResults();
            sb.append("\n        Number Of Wifi connections :"+wifiList.size()+"\n\n");

            for(int i = 0; i < wifiList.size(); i++){

                sb.append(new Integer(i+1).toString() + ". ");
                sb.append((wifiList.get(i)).toString());
                sb.append("\n\n");
            }

            mainText.setText(sb);
        }

    }
}