package com.idevelop.location;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.DhcpInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.CellIdentityLte;
import android.telephony.CellInfo;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigInteger;
import java.net.InetAddress;
import java.nio.ByteOrder;
import java.util.List;

public class Information extends AppCompatActivity {

    final private int REQUEST_CODE_ASK_PERMISSIONS = 13;
    TextView CID,LAC, SSID,RSSI, tvRouterIpAddress, tvRouterMacAddress;
    ImageView ivWifiSignal;
    Button btnCellId, btnWifi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);

        CID = (TextView) findViewById(R.id.CID);
        LAC = (TextView) findViewById(R.id.LAC);
        SSID = (TextView) findViewById(R.id.SSID);
        RSSI = (TextView) findViewById(R.id.RSSI);
       // tvRouterIpAddress = (TextView) findViewById(R.id.tvRouterIpAddress);
       // tvRouterMacAddress = (TextView) findViewById(R.id.tvRouterMacAddress);
        ivWifiSignal = (ImageView) findViewById(R.id.ivWifiSignal);
        btnCellId = (Button) findViewById(R.id.gsm);
        btnWifi = (Button) findViewById(R.id.wifi);


        btnCellId.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                Log.e("CID " , "from button");

                requestPermissions(new String[]{
                                android.Manifest.permission.ACCESS_FINE_LOCATION,
                                android.Manifest.permission.ACCESS_COARSE_LOCATION},
                        REQUEST_CODE_ASK_PERMISSIONS);

                if (ActivityCompat.checkSelfPermission(Information.this,
                        Manifest.permission.ACCESS_FINE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(Information.this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
                    return;
                }

                // get the cell ID & Location area code
                final TelephonyManager telephony = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                final GsmCellLocation location = (GsmCellLocation) telephony.getCellLocation();
//                Toast.makeText(ConnectivityActivity.this,"cellID " + location.getCid()
//                        + " LAC " + location.getLac(), Toast.LENGTH_LONG).show();
                if (location != null) {
                    String cid = " "+ location.getCid();
                    String lac = " "+ location.getLac();
                    CID.setText(cid);
                    LAC.setText(lac);
//                    msg.setText("LAC: " + location.getLac() + " CID: " + location.getCid());
                }else {
                    CID.setText("NULL");
                    LAC.setText("NULL");
                }
            }
        });

        btnWifi.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                // get the SSID & RSSI & ip_@ of wireless network wifi
                WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();

                String ssid = " "+ wifiInfo.getSSID();
                String rssi = " "+ wifiInfo.getRssi();
               SSID.setText(ssid);
                RSSI.setText(rssi);
//                Toast.makeText(ConnectivityActivity.this,"SSID " + ssid + " RSSI " + rssi, Toast.LENGTH_LONG).show();

                //éliminer pour le moment **********************************************************
                // get the Mac @ of router
             /*   String BSSID = wifiInfo.getBSSID();
                Toast.makeText(Information.this,"BSSID " + BSSID, Toast.LENGTH_LONG).show();
                tvRouterMacAddress.setText(getResources().getString(R.string.mac_address, BSSID));

                // get the ip_@ of wifi
                final DhcpInfo dhcp = wifiManager.getDhcpInfo();
                int ipAddress = dhcp.gateway;
                ipAddress = (ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN)) ?
                        Integer.reverseBytes(ipAddress) : ipAddress;
                byte[] ipAddressByte = BigInteger.valueOf(ipAddress).toByteArray();
                try {
                    InetAddress myAddr = InetAddress.getByAddress(ipAddressByte);
                    tvRouterIpAddress.setText(getResources().getString(R.string.ip_address, myAddr.getHostAddress()));
//                    Toast.makeText(ConnectivityActivity.this,"ip address " + myAddr.getHostAddress(), Toast.LENGTH_LONG).show();
                }catch (Exception e){
                } */

                //**********************************************************************************

                // get the signal level and display it as icon
                int numberOfLevels = 5;
                int level = WifiManager.compareSignalLevel(wifiInfo.getRssi(), numberOfLevels);
//                Toast.makeText(ConnectivityActivity.this,"level " + level, Toast.LENGTH_LONG).show();

                if (level <= 0 && level >= -50) {
                    //Best signal
                    ivWifiSignal.setImageResource(R.drawable.ic_signal_wifi_4_bar_black_24dp);
                } else if (level < -50 && level >= -70) {
                    //Good signal
                    ivWifiSignal.setImageResource(R.drawable.ic_signal_wifi_3_bar_black_24dp);
                } else if (level < -70 && level >= -80) {
                    //Low signal
                    ivWifiSignal.setImageResource(R.drawable.ic_signal_wifi_2_bar_black_24dp);
                } else if (level < -80 && level >= -100) {
                    //Very weak signal
                    ivWifiSignal.setImageResource(R.drawable.ic_signal_wifi_1_bar_black_24dp);
                } else {
                    // no signals
                    ivWifiSignal.setImageResource(R.drawable.ic_signal_wifi_0_bar_black_24dp);
                }

//                Excellent >-50 dBm
//                Good -50 to -60 dBm
//                Fair -60 to -70 dBm
//                Weak < -70 dBm
            }
        });
    }

    public void CallPreviousScreen(View view) {
        Intent intent = new Intent(getApplicationContext(), Menu.class);
        startActivity(intent);
    }



//    public String getMacId() {
//        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
//        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
//        return wifiInfo.getBSSID();
//    }
}
