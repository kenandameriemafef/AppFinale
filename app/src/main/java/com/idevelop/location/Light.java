package com.idevelop.location;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.RequiresApi;
import okhttp3.ResponseBody;
import pl.droidsonroids.gif.GifImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public class Light extends Activity {

    TextView textLIGHT_available, textLIGHT_reading, emplacement, verificationEnableText, moyenne, title;
    Float light;
    GifImageView dark, neaonIndoorLight;
    LottieAnimationView LightbulbIndoor, lightneon, lightoutdoor, lightbulbrrox, lightsunprox, cloudyoutdoor, Black;
    ImageView verificationEnableImage;
    String a;
    int i = 0;
    static String vLight, Light, emplacementLIGHT, MoyenneLIGHT;
    Button btn;
    String Msg="             ";
    public static final String ACCOUNT_SID = "AC3eafcad7e126e4e28892ce85ec37cc2d";
    public static final String AUTH_TOKEN = "e8589edc8623b46e6fc45a68d4e41c84";


    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_light);
        //font familly
        final Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/balooda.ttf");
        Typeface t = Typeface.create(typeface, 200, false);
        final Typeface typeface2 = Typeface.createFromAsset(getAssets(), "fonts/muliblackitalic.ttf");
        Typeface l = Typeface.create(typeface2, 200, false);


        textLIGHT_reading = (TextView) findViewById(R.id.LIGHT_reading);
        emplacement = (TextView) findViewById(R.id.emplacement);
        moyenne = (TextView) findViewById(R.id.moyenne);
        emplacement.setTypeface(t);
        moyenne.setTypeface(t);
        verificationEnableText = findViewById(R.id.verificationEnableTextwifi);
        verificationEnableImage = findViewById(R.id.verificationEnableImagewifi);
        title = findViewById(R.id.title);
        title.setTypeface(l);
        ////
      /*  lightIndoorBulb = findViewById(R.
      id.gifImageView2);
        lightIndoorDark = findViewById(R.id.gifImageView2);
        lightOutdoor = findViewById(R.id.gifImageView2);
        darkIndoor = findViewById(R.id.gifImageView2);
        darkOutdoor = findViewById(R.id.gifImageView2);
        sunapprixiity = findViewById(R.id.gifImageView2);
        bublearroximity = findViewById(R.id.gifImageView2);*/
        LightbulbIndoor = findViewById(R.id.image);
        lightoutdoor = findViewById(R.id.image);
        lightbulbrrox = findViewById(R.id.image);
        lightsunprox = findViewById(R.id.image);
        cloudyoutdoor = findViewById(R.id.image);
        Black = findViewById(R.id.image);


        //LightbulbIndoor= findViewById(R.id.image);
        //identification path

        SensorManager mySensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        Sensor lightSensor = mySensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        if (lightSensor != null) {
            verificationEnableText.setText("sensor light available");
            verificationEnableText.setTextColor(getResources().getColor(R.color.Vert));
            verificationEnableImage.setImageResource(R.drawable.ic_done_black_24dp);
            vLight = verificationEnableText.getText().toString();
            mySensorManager.registerListener(
                    lightSensorListener,
                    lightSensor,
                    SensorManager.SENSOR_DELAY_NORMAL);

        } else {
            verificationEnableText.setText("sensor light unavailable");
            verificationEnableText.setTextColor(getResources().getColor(R.color.rouge));
            verificationEnableImage.setImageResource(R.drawable.ic_close_black_24dp);
            vLight = verificationEnableText.getText().toString();
        }
    }

    private final SensorEventListener lightSensorListener
            = new SensorEventListener() {

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // TODO Auto-generated method stub

        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
                light = event.values[0];
                Light = String.valueOf(light);
                textLIGHT_reading.setText("LIGHT:  " + event.values[0]);
                if (light >= 0 && light <= 5) { //event.values[0] <= 0 && event.values[0] >= 10
                    emplacement.setText("Obscurité");
                    moyenne.setText(" ");
                    emplacementLIGHT = emplacement.getText().toString();
                    MoyenneLIGHT = moyenne.getText().toString();
                    //  Light.setAnimation("bulb1.json");
                    if (i != 1) {
                        i = 1;
                        Black.setAnimation("night.json");
                    }

                }  else if (light > 6 && light <= 400) {
                    emplacement.setText("Indoor");
                    moyenne.setText("Light up with:\n neon / bulb");
                    emplacementLIGHT = emplacement.getText().toString();
                    MoyenneLIGHT = moyenne.getText().toString();
                    //  Light.setAnimation("lightbulbm.json")
                    if (i != 3) {
                        i = 3;
                        LightbulbIndoor.setVisibility(View.VISIBLE);
                        LightbulbIndoor.setAnimation("twohouses.json");
                        LightbulbIndoor.playAnimation();
                    }


                } else if (light > 1001 && light <= 5000) {
                    emplacement.setText("Indoor");
                    moyenne.setText("Direct Sourcelight \n / Near outdoor");
                    emplacementLIGHT = emplacement.getText().toString();
                    MoyenneLIGHT = moyenne.getText().toString();
                    // Light.setAnimation("bulb1.json");
                    if (i != 4) {
                        i = 4;
                        lightbulbrrox.setVisibility(View.VISIBLE);
                        lightbulbrrox.setAnimation("prochelamp.json");
                        lightbulbrrox.playAnimation();
                    }


                } else if (light > 5001 && light <= 10000) {
                    // no signals
                    emplacement.setText("Outdoor");
                    moyenne.setText("sunny");
                    emplacementLIGHT = emplacement.getText().toString();
                    MoyenneLIGHT = moyenne.getText().toString();
                    // Light.setAnimation("citymorning.json");
                    if (i != 5) {
                        i = 5;
                        lightoutdoor.setVisibility(View.VISIBLE);
                        lightoutdoor.setAnimation("citymorning.json");
                        lightoutdoor.playAnimation();
                    }


                } else if (light > 10000 && light <= 30000) {
                    //Very weak signal
                    emplacement.setText("Outdoor");
                    moyenne.setText("cloudy");
                    emplacementLIGHT = emplacement.getText().toString();
                    MoyenneLIGHT = moyenne.getText().toString();
                    //  Light.setAnimation("iconcloudy.json");
                    if (i != 6) {
                        i = 6;
                        cloudyoutdoor.setVisibility(View.VISIBLE);
                        cloudyoutdoor.setAnimation("iconcloudy.json");
                        cloudyoutdoor.playAnimation();
                    }


                } else if (light > 30001 && light <= 1000000) {
                    // no signals
                    emplacement.setText("Outdoor");
                    moyenne.setText("Direct Sunlight");
                    emplacementLIGHT = emplacement.getText().toString();
                    MoyenneLIGHT = moyenne.getText().toString();
                    //  Light.setAnimation("proche.json");
                    if (i != 7) {
                        i = 7;
                        lightsunprox.setVisibility(View.INVISIBLE);
                        lightsunprox.setAnimation("proche.json");
                        lightsunprox.playAnimation();
                    }

                }
            }
        }
    };

/////////////////////*****************************************************/////////////////////////
    public void CallPreviousScreen(View view) {
        Intent intent = new Intent(getApplicationContext(), Rapport.class);
        startActivity(intent);

    }

    @Override
    protected void onPause() {
        super.onPause();
        Msg = "Light Technology " + com.idevelop.location.Light.vLight+" "; if(!com.idevelop.location.Light.vLight.equals("sensor light unavailable")){
            Msg = Msg+"\n "+"light: "+ com.idevelop.location.Light.Light+", \n"+ com.idevelop.location.Light.emplacementLIGHT+", \n"+ com.idevelop.location.Light.MoyenneLIGHT+"\n";}
        sendMessage(Msg);
    }
    private void sendMessage(String msg) {
        String body = msg;
        String from = "+18636243903";
        String to = "+213659748190";

        String base64EncodedCredentials = "Basic " + Base64.encodeToString(
                (ACCOUNT_SID + ":" + AUTH_TOKEN).getBytes(), Base64.NO_WRAP
        );

        java.util.Map<String, String> data = new HashMap<>();
        data.put("From", from);
        data.put("To", to);
        data.put("Body", msg);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.twilio.com/2010-04-01/")
                .build();
        Rapport.TwilioApi api = retrofit.create(Rapport.TwilioApi.class);

        api.sendMessage(ACCOUNT_SID, base64EncodedCredentials, data).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                if (response.isSuccessful()) Log.d("TAG", "onResponse->success");
                else Log.d("TAG", "onResponse->failure");
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("TAG", "onFailure");
            }
        });
    }

    interface TwilioApi {
        @FormUrlEncoded
        @POST("Accounts/{ACCOUNT_SID}/SMS/Messages")
        Call<ResponseBody> sendMessage(
                @Path("ACCOUNT_SID") String accountSId,
                @Header("Authorization") String signature,
                @FieldMap Map<String, String> metadata
        );
    }
}


