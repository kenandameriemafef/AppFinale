package com.idevelop.location;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.List;

public class GetGPSPosition extends AppCompatActivity {
    FusedLocationProviderClient fusedLocationProviderClient;
    SwipeRefreshLayout swipeRefreshLayout;
    TextView log, lat, Address,verificationEnableText,Text1,Text2,Text3;
    ImageView verificationEnableImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_g_p_s_position);
        swipeRefreshLayout = findViewById(R.id.swipelayout);
        lat = findViewById(R.id.latitudeValue);
        log = findViewById(R.id.longitudeValue);
        Address = findViewById(R.id.AddressValue);
        Text1 = findViewById(R.id.Text1);
        Text2 = findViewById(R.id.Text2);
        Text3 = findViewById(R.id.Text3);
        verificationEnableText = findViewById(R.id.verificationEnableText);
        verificationEnableImage = findViewById(R.id.verificationEnableImage);

        if(isGPSEnable ()){

        //Check permission
        if (ActivityCompat.checkSelfPermission(GetGPSPosition.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //when permission grated
            //Call method
            //getCurrentLocation();
        } else {
            //Where permission denied
            //Request permission
            ActivityCompat.requestPermissions(GetGPSPosition.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }
        UpdateGPS();
        isGPSEnable ();}
        else{ ifGPSDisable();}
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(isGPSEnable ()){
                    isGPSEnable ();
                    UpdateGPS(); }
                else { ifGPSDisable();
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 44) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //When permission grated
                //Call method
                UpdateGPS();
            }
        }
    }
    private void UpdateGPS(){
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(GetGPSPosition.this);
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                UpdateUIValue(location);
            }
        });
    }
    private void UpdateUIValue(Location location){
          try {
              Text1.setText("Latitude"); Text2.setText("Longitude"); Text3.setText("Address");
              lat.setText(String.valueOf(location.getLatitude()));
              log.setText(String.valueOf(location.getLongitude()));
          }catch(Exception ex){
              //lat = null;
              //log = null;
        }

        Geocoder geocoder = new Geocoder(GetGPSPosition.this);

        try{
            List<android.location.Address> address = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
            Address.setText(address.get(0).getAddressLine(0));
        }catch (Exception ex){
            Address.setText(" ");
        }
    }

    public void CallMapScreen(View view) {
        Intent intent = new Intent(getApplicationContext(), Map.class);
        startActivity(intent);
    }
    public void CallPreviousScreen(View view) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }
    //Verification GPS
    private boolean isGPSEnable (){
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean providerEnable = locationManager.isProviderEnabled((LocationManager.GPS_PROVIDER));
        if(providerEnable){
            verificationEnableText.setText("GPS Enable");
            verificationEnableText.setTextColor(getResources().getColor(R.color.Vert));
            verificationEnableImage.setImageResource(R.drawable.ic_done_black_24dp);

            return true;
        }else{ verificationEnableText.setText("GPS Is Disable");
               verificationEnableText.setTextColor(getResources().getColor(R.color.rouge));
            verificationEnableImage.setImageResource(R.drawable.ic_close_black_24dp);
            }return false;
    }
    public void ifGPSDisable(){
        lat.setText(" "); log.setText(" "); Address.setText(" ");
        Text1.setText(" "); Text2.setText(" "); Text3.setText(" ");
    }
    public void callsuiteScreen(View view) {
        Intent intent = new Intent(getApplicationContext(), Wifi.class);
        startActivity(intent);
    }


}
