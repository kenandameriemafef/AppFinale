package com.idevelop.location;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;


public class Menu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

    }


    public void CallGestureRecScreen(View view) {
        Intent intent = new Intent(getApplicationContext(), GestureRecognition.class);
        startActivity(intent);
    }

    public void CallGetRouterPositionScreen(View view) {
        Intent intent = new Intent(getApplicationContext(), GetRouterPosition.class);
        startActivity(intent);
    }

    public void CallInformationScreen(View view) {
        Intent intent = new Intent(getApplicationContext(), Information.class);
        startActivity(intent);
    }
    public void CallWifiScreen(View view) {
        Intent intent = new Intent(getApplicationContext(), Wifi.class);
        startActivity(intent);
    }
    public void CallGSMScreen(View view) {
        Intent intent = new Intent(getApplicationContext(), Gsm.class);
        startActivity(intent);
    }



    public void CallDataColleScreen(View view) {
        Intent intent = new Intent(getApplicationContext(), DataCollection.class);
        startActivity(intent);
    }
    public void CallPreviousScreen(View view) {
         Intent intent = new Intent(getApplicationContext(), MainActivity.class);
         startActivity(intent);
    }
    public void CallGPSScreen(View view) {
        Intent intent = new Intent(getApplicationContext(), GetGPSPosition.class);
        startActivity(intent);
    }

    public void CalllightScreen(View view) {
        Intent intent = new Intent(getApplicationContext(), Light.class);
        startActivity(intent);
    }
}
