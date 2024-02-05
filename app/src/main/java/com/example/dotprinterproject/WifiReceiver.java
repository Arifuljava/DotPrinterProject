package com.example.dotprinterproject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.LinkAddress;
import android.net.LinkProperties;
import android.net.Network;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

class WifiReceiver extends BroadcastReceiver {
    WifiManager wifiManager;
    StringBuilder sb;
    ListView wifiDeviceList;
    Context context;

    public WifiReceiver(WifiManager wifiManager, ListView wifiDeviceList,Context context) {
        this.wifiManager = wifiManager;
        this.wifiDeviceList = wifiDeviceList;
        this.context =context;

        wifiDeviceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedItem = (String) adapterView.getItemAtPosition(i);
                Log.e("Selected",""+selectedItem);
                String[] parts = selectedItem.split(" - ");
                String selectedSSID = parts[0];

                // Get the IP Address and port number based on the selected SSID
                String ipAddress = getIpAddress(selectedSSID);
                Log.e("getIP",""+ipAddress);


                int timeout = 2000; // Timeout in milliseconds

                int portNumber = getPortNumber(ipAddress, timeout);

                if (portNumber != -1) {
                    System.out.println("Port number: " + portNumber);
                } else {
                    System.out.println("Error getting port number.");
                }

            }
        });
    }
    private String getIpAddress(String ssid) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                Network network = connectivityManager.getActiveNetwork();
                LinkProperties linkProperties = connectivityManager.getLinkProperties(network);
                List<LinkAddress> linkAddresses = linkProperties.getLinkAddresses();
                for (LinkAddress linkAddress : linkAddresses) {
                    // Assuming you are interested in IPv4 addresses
                    if (linkAddress.getAddress() instanceof Inet4Address) {
                        return linkAddress.getAddress().getHostAddress();
                    }
                }
            }
        } else {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int ipAddress = wifiInfo.getIpAddress();
            String ip = Formatter.formatIpAddress(ipAddress);
            return ip;
        }

        return "192.168.1.1";
    }
    public static int getPortNumber(String ipAddress, int timeout) {
        try {

            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(ipAddress, 80), timeout);
            int portNumber = socket.getPort();
            socket.close();
            return portNumber;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Errrrr",""+e.getMessage());
            return -1;
        }
    }

    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(action)) {
            sb = new StringBuilder();
            if (ActivityCompat.checkSelfPermission(context,
                    android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                List<ScanResult> wifiList = wifiManager.getScanResults();
                ArrayList<String> deviceList = new ArrayList<>();
                for (ScanResult scanResult : wifiList) {
                    sb.append(" ").append(scanResult.SSID).append(" - ").append(scanResult.capabilities);
                    deviceList.add(scanResult.SSID + " - " + scanResult.capabilities);
                }
                //Toast.makeText(context, sb, Toast.LENGTH_SHORT).show();
                ArrayAdapter arrayAdapter = new ArrayAdapter(context, android.R.layout.simple_list_item_1, deviceList.toArray());
                wifiDeviceList.setAdapter(arrayAdapter);
            }
            else{
                List<ScanResult> wifiList = wifiManager.getScanResults();
                ArrayList<String> deviceList = new ArrayList<>();
                for (ScanResult scanResult : wifiList) {
                    sb.append(" ").append(scanResult.SSID).append(" - ").append(scanResult.capabilities);
                    deviceList.add(scanResult.SSID + " - " + scanResult.capabilities);
                }
              //  Toast.makeText(context, sb, Toast.LENGTH_SHORT).show();
                ArrayAdapter arrayAdapter = new ArrayAdapter(context, android.R.layout.simple_list_item_1, deviceList.toArray());
                wifiDeviceList.setAdapter(arrayAdapter);
            }

        }
    }
}