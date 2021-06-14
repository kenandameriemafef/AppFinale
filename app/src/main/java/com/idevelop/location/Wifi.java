package com.idevelop.location;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.CellInfo;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.skyhookwireless.wps.IWPS;
import com.skyhookwireless.wps.WPSContinuation;
import com.skyhookwireless.wps.WPSLocation;
import com.skyhookwireless.wps.WPSLocationCallback;
import com.skyhookwireless.wps.WPSReturnCode;
import com.skyhookwireless.wps.WPSStreetAddressLookup;
import com.skyhookwireless.wps.XPS;

import java.util.List;
import java.util.Locale;

public class Wifi extends AppCompatActivity {

    final private int REQUEST_CODE_ASK_PERMISSIONS = 13;
    TextView SSID, RSSI, tvRouterIpAddress, tvRouterMacAddress, verificationEnableText, latitude, longitude, country, Address;
    ImageView ivWifiSignal, verificationEnableImage;
    Button btnWifi, wifi;
    SwipeRefreshLayout swipeRefreshLayout;
    private TextView tv;
    private ProgressBar progress;
    private IWPS xps;

    ListView listView;
    WifiManager wifiManager;
    public int mInterval = 3000; // 5 seconds by default, can be changed later
    private Handler mHandler;

    List<WifiInfo> wifiInfos;

    IntentFilter intentFilter = new IntentFilter();

    CardView card1, card2, card3, card4;
    Button wifiRSSIActivity;


    private BroadcastReceiver networkChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("app", "Network connectivity change ----------------------------- conected wifi");

            WifiInfo wifiInfo = wifiManager.getConnectionInfo();

            // get SSID
            String ssid = wifiInfo.getSSID();
            SSID.setText(ssid);

            // get RSSI
            int rssi = wifiInfo.getRssi();
            RSSI.setText(" " +rssi);

            // get @ MAC
            String BSSID = wifiInfo.getBSSID();
            tvRouterMacAddress.setText(BSSID);

            // get RSSI
            int numberOfLevels = 5;
            int level = WifiManager.compareSignalLevel(wifiInfo.getRssi(), numberOfLevels);
            levelOfSignal(level, ivWifiSignal);

            //verify wifi enable or not
            isConnected();
        }
    };


    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi);

        mHandler = new Handler();
        startRepeatingTask();

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        registerReceiver(networkChangeReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        wifiManager.startScan();



        SSID = (TextView) findViewById(R.id.SSID);
        RSSI = (TextView) findViewById(R.id.RSSI);
        tvRouterMacAddress = (TextView) findViewById(R.id.MACAddress);
        // tvRouterIpAddress = (TextView) findViewById(R.id.tvRouterIpAddress);
        // tvRouterMacAddress = (TextView) findViewById(R.id.tvRouterMacAddress);
        ivWifiSignal = (ImageView) findViewById(R.id.ivWifiSignal);
        btnWifi = (Button) findViewById(R.id.wifi);
        swipeRefreshLayout = findViewById(R.id.swipeLayout);
        verificationEnableText = findViewById(R.id.verificationEnableTextwifi);
        verificationEnableImage = findViewById(R.id.verificationEnableImagewifi);

        card1 = findViewById(R.id.card1);
        card2 = findViewById(R.id.card2);
        card3 = findViewById(R.id.card3);
        card4 = findViewById(R.id.card4);

        latitude = findViewById(R.id.latitude);
        longitude = findViewById(R.id.longitude);
        country = findViewById(R.id.country);
        Address = findViewById(R.id.Address);

        wifi = findViewById(R.id.wifi);

        retry();

        //Call Skyhook Dataset
        setTitle(String.format(
                Locale.ROOT,
                "%s (XPS v%s)",
                getString(R.string.app_name),
                XPS.getVersion()));

        tv = findViewById(R.id.tv);
        progress = findViewById(R.id.progress);

        xps = new XPS(this);

        try {
            xps.setKey("eJwNwcENACAIBLC3w5BwISI8EXQp4-7aooE_cFdrBxtik0ElZhSuTFm5KWKJD_WsyPsAESsLVg");
        } catch (IllegalArgumentException e) {
            tv.setText("Put your API key in the source code");
        }

        ActivityCompat.requestPermissions(
                this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);

        //Call position wifi function
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isConnected();
                retry();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

    }

    //Verify Wifi Enable or not
    @SuppressLint("SetTextI18n")
    private boolean isConnected() {
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager.isWifiEnabled()) {
            verificationEnableText.setText("WI-FI Enable");
            verificationEnableText.setTextColor(getResources().getColor(R.color.Vert));
            verificationEnableImage.setImageResource(R.drawable.ic_done_black_24dp);
            return true;
        } else {
            verificationEnableText.setText("WI-FI Is Disable");
            verificationEnableText.setTextColor(getResources().getColor(R.color.rouge));
            verificationEnableImage.setImageResource(R.drawable.ic_close_black_24dp);
            return false;
        }
    }

    //Get Wifi Information
    private void WifiPosition() {

    }

    private void levelOfSignal(int level, ImageView ivSignalLevel) {
        if (level <= 0 && level >= -50) {
            //Best signal
            ivSignalLevel.setImageResource(R.drawable.wifi4);
        } else if (level < -50 && level >= -70) {
            //Good signal
            ivSignalLevel.setImageResource(R.drawable.wifi3);
        } else if (level < -70 && level >= -80) {
            //Low signal
            ivSignalLevel.setImageResource(R.drawable.wifi2);
        } else if (level < -80 && level >= -100) {
            //Very weak signal
            ivSignalLevel.setImageResource(R.drawable.wifi1);
        } else {
            // no signals
            ivSignalLevel.setImageResource(R.drawable.wifi0);
        }
    }

    //Get Wifi Position With Skyhook Dataset
    public void onClick(View view) {
        determineLocation();

        }


    private void determineLocation() {
        if (! hasLocationPermission()) {
            tv.setText("Permission denied");
            return;
        }

        tv.setVisibility(View.INVISIBLE);
        progress.setVisibility(View.VISIBLE);
        wifi.setVisibility(View.INVISIBLE);
        if(!isConnected()){
            card1.setVisibility(View.INVISIBLE);
            card2.setVisibility(View.INVISIBLE);
            card3.setVisibility(View.INVISIBLE);
            card4.setVisibility(View.INVISIBLE);
        }else{
            card1.setVisibility(View.VISIBLE);
            card2.setVisibility(View.VISIBLE);
            card3.setVisibility(View.VISIBLE);
            card4.setVisibility(View.VISIBLE);
        }

        xps.getLocation(
                null,
                WPSStreetAddressLookup.WPS_FULL_STREET_ADDRESS_LOOKUP,
                true,
                new WPSLocationCallback() {
                    @Override
                    public void handleWPSLocation(final WPSLocation location) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                               /* tv.setText(String.format(
                                        Locale.ROOT,
                                        "%.7f %.7f +/-%dm\n\n%s\n\n%s \n\n%s \n\n%s \n\n%s",
                                        location.getLatitude(),
                                        location.getLongitude(),
                                        location.getHPE(),
                                        location.hasTimeZone() ? location.getTimeZone() : "No timezone",
                                        location.hasStreetAddress() ? location.getStreetAddress() : "No address",
                                        location.hasAltitude() ? location.getAltitude() : "No altitude",
                                        location.hasIp() ? location.getIp() : "No Ip",
                                        getCompleteAddressString(location.getLatitude(), location.getLongitude())
                                ));*/
                                latitude.setText(String.format(
                                        Locale.ROOT,
                                        "%.7f ",
                                        location.getLatitude()));
                                longitude.setText(String.format(
                                        Locale.ROOT,
                                        "%.7f ",
                                        location.getLongitude()));
                                country.setText(String.format(
                                        Locale.ROOT,
                                        "%s",
                                        location.hasStreetAddress() ? location.getStreetAddress() : "No address"));
                                Address.setText(String.format(
                                        Locale.ROOT,
                                        "%s ",
                                        getCompleteAddressString(location.getLatitude(), location.getLongitude())
                                ));


                            }
                        });
                    }

                    @Override
                    public void done() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tv.setVisibility(View.VISIBLE);
                                progress.setVisibility(View.INVISIBLE);
                            }
                        });
                    }

                    @Override
                    public WPSContinuation handleError(final WPSReturnCode returnCode) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tv.setText(returnCode.toString());
                            }
                        });

                        return WPSContinuation.WPS_CONTINUE;
                    }
                });
    }

    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                Log.w("My Current loction add", strReturnedAddress.toString());
            } else {
                Log.w("My Current loction add", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("My Current loction add", "Canont get Address!");
        }
        return strAdd;
    }

    private boolean hasLocationPermission() {
        return checkCallingOrSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    //Retourne
    public void CallPreviousScreen(View view) {
        Intent intent = new Intent(getApplicationContext(), Menu.class);
        startActivity(intent);
    }
    //Search networks
    public void CallWifiSearchScreen(View view) {
        Intent intent = new Intent(getApplicationContext(), WifiSearch.class);
        startActivity(intent);
    }
    //Suite
    public void CallGSMScreen(View view) {
        Intent intent = new Intent(getApplicationContext(), Gsm.class);
        startActivity(intent);
    }

    private void retry (){
        //Invisible Card
        card1.setVisibility(View.INVISIBLE);
        card2.setVisibility(View.INVISIBLE);
        card3.setVisibility(View.INVISIBLE);
        card4.setVisibility(View.INVISIBLE);

        //Visibility button
        //Invisible Card
        wifi.setVisibility(View.VISIBLE);

    }
    @Override
    protected void onResume() {
        super.onResume();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(networkChangeReceiver);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopRepeatingTask();
    }

    void startRepeatingTask() {
        mStatusChecker.run();
    }

    void stopRepeatingTask() {
        mHandler.removeCallbacks(mStatusChecker);
    }

    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            try {
                intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
                registerReceiver(networkChangeReceiver, intentFilter);
            } finally {
                // 100% guarantee that this always happens, even if
                // your update method throws an exception
                mHandler.postDelayed(mStatusChecker, mInterval);
            }
        }
    };



}
