package com.idevelop.location;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class WifiSearch extends AppCompatActivity {

    private int mInterval = 2000; // 5 seconds by default, can be changed later
    private Handler mHandler;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 13;
    IntentFilter intentFilter = new IntentFilter();
    WifiManager wifiManager;
    List<ScanResult> wifiList;
    ListView listView;
    String[] textString;
    String[] textMac;
    CustomAdapter adapter;

    private BroadcastReceiver networkChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("app","Network connectivity change  ----------------------------- available network");

            wifiManager.startScan();
            wifiList = wifiManager.getScanResults();
            textString = new String[wifiList.size()];
            textMac = new String[wifiList.size()];
            int[] drawableIds = new int[wifiList.size()];

            for(int i = 0; i < wifiList.size(); i++) {
                textString[i] = wifiList.get(i).SSID;
                textMac[i] = wifiList.get(i).BSSID;
                if (wifiList.get(i).level <= 0 && wifiList.get(i).level >= -50) {
                    drawableIds[i] = R.drawable.wifi4;
                    //Best signal
                } else if (wifiList.get(i).level < -50 && wifiList.get(i).level >= -70) {
                    drawableIds[i] = R.drawable.wifi3;
                    //Good signal
                } else if (wifiList.get(i).level < -70 && wifiList.get(i).level >= -80) {
                    drawableIds[i] = R.drawable.wifi2;
                    //Low signal
                } else if (wifiList.get(i).level < -80 && wifiList.get(i).level >= -100) {
                    drawableIds[i] = R.drawable.wifi1;
                    //Very weak signal
                } else {
                    drawableIds[i] = R.drawable.wifi0;
                    // no signals
                }
            }
            adapter = new CustomAdapter(WifiSearch.this,  textString,textMac, drawableIds);
            listView.setAdapter(adapter);
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_search);

        mHandler = new Handler();
        startRepeatingTask();

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        registerReceiver(networkChangeReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        listView = (ListView) findViewById(R.id.menuList);
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

    // class of adapter to display each element of listView with icon
    public class CustomAdapter extends BaseAdapter {

        private Context mContext;
        private String[]  Title;
        private String[]  MACAdd;
        private int[] imge;

        public CustomAdapter(Context context, String[] text1,String[] text2,int[] imageIds) {
            mContext = context;
            Title = text1;
            MACAdd = text2;
            imge = imageIds;
        }

        public int getCount() {
            // TODO Auto-generated method stub
            return Title.length;

        }

        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            return null;
        }

        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = getLayoutInflater();
            View row;
            row = inflater.inflate(R.layout.row, parent, false);
            TextView title;
            TextView MAC;
            ImageView i1;
            i1 = (ImageView) row.findViewById(R.id.imgIcon);
            title = (TextView) row.findViewById(R.id.txtTitle);
            MAC = (TextView) row.findViewById(R.id.MAC);
            title.setText(Title[position]);
            MAC.setText(MACAdd[position]);
            i1.setImageResource(imge[position]);
            return (row);
        }
    }
    //Retourne
    public void CallPreviousScreen(View view) {
        Intent intent = new Intent(getApplicationContext(), Wifi.class);
        startActivity(intent);
    }

}
