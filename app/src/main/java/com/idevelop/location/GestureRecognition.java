package com.idevelop.location;

import androidx.annotation.LongDef;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import pl.droidsonroids.gif.GifImageView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.opencsv.CSVWriter;
import com.studioidan.httpagent.HttpAgent;
import com.studioidan.httpagent.JsonCallback;

import org.jetbrains.annotations.Contract;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.BreakIterator;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GestureRecognition extends AppCompatActivity implements SensorEventListener{

    SensorManager mSensorManager;
    //String
    String x_accMean,y_accMean,z_accMean,x_gyroMean,y_gyroMean,z_gyroMean,x_magMean,y_magMean,z_magMean;

    LottieAnimationView image, sitting;
    GifImageView giffile;

    // sensors
    private Sensor mSensorAccelerometer;
    private Sensor mSensorMagnetometer;
    private Sensor mSensorGyroscope;
    private Sensor StepsAccount;
    private Sensor StepsDetector;

    //valueStepSensor

    private int totalSteps = 0;
    private float previousTotalSteps = 0f;

    int  stepDetct = 0, delay = 20000;
    public int  StepsSave= -1;
    File file;
    double[] resultAcc;
    double[] resultGyro;
    double[] resultMagn;

    //Rapport value
    static  String Geste;

    LinearLayout meanLayout, accMeanField, magnMeanField, gyroMeanField;
    CheckBox checkbox_Acc, checkbox_Magn, checkbox_gyro, chechbox_steps;

    // TextViews to display current sensor values
    EditText mTextAccSensor_X, mTextAccSensor_Y, mTextAccSensor_Z;
    EditText mTextMagSensor_X, mTextMagSensor_Y, mTextMagSensor_Z;
    EditText mTextGyroSensor_X, mTextGyroSensor_Y, mTextGyroSensor_Z;

    Button btnStartColl, btnTest;
    TextView vStepsAccount, vStepsDetector, textPrediction;

    // texView to display mean value of each axis of each sensor

    EditText XSteps, XStepsD,Distance;

    //Spinner
    Spinner spinnertime, spinnergeste;
    String GesteSelected = null;


    boolean accChecked, magnChecked, gyroChecked, stepsCheck = false;
    int nmbrOfCheck = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gesture_recognition);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensorAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mSensorGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        StepsAccount = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        StepsDetector = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);

        //lottieFiles
        image = findViewById(R.id.image);
        giffile = findViewById(R.id.giffile);



        // make the layout that have the values of mean invisible till the process finish (after 20s)
        meanLayout = (LinearLayout) findViewById(R.id.meanLayout);
        accMeanField = (LinearLayout) findViewById(R.id.accMeanField);
        magnMeanField = (LinearLayout) findViewById(R.id.magnMeanField);
        gyroMeanField = (LinearLayout) findViewById(R.id.gyroMeanField);

        checkbox_Acc = (CheckBox) findViewById(R.id.checkbox_Acc);
        checkbox_Magn = (CheckBox) findViewById(R.id.checkbox_Magn);
        checkbox_gyro = (CheckBox) findViewById(R.id.checkbox_gyro);
        chechbox_steps = (CheckBox) findViewById(R.id.checkbox_steps);

        // display sensors data --------------------------------------------------------------------
        mTextAccSensor_X = (EditText) findViewById(R.id.acc_x);
        mTextAccSensor_Y = (EditText) findViewById(R.id.acc_y);
        mTextAccSensor_Z = (EditText) findViewById(R.id.acc_z);

        mTextMagSensor_X = (EditText) findViewById(R.id.mag_x);
        mTextMagSensor_Y = (EditText) findViewById(R.id.mag_y);
        mTextMagSensor_Z = (EditText) findViewById(R.id.mag_z);

        mTextGyroSensor_X = (EditText) findViewById(R.id.gyro_x);
        mTextGyroSensor_Y = (EditText) findViewById(R.id.gyro_y);
        mTextGyroSensor_Z = (EditText) findViewById(R.id.gyro_z);

        XSteps = (EditText) findViewById(R.id.XSteps);
        //XStepsD = (EditText) findViewById(R.id.StepDetector);

        btnStartColl = (Button) findViewById(R.id.btnStartColl);
       // btnTest = (Button) findViewById(R.id.btnTest);

        textPrediction = (TextView) findViewById(R.id.textPrediction);
        // for mean function  ----------------------------------------------------------------------

        disableEditText(mTextAccSensor_X);disableEditText(mTextAccSensor_Y);disableEditText(mTextAccSensor_Z);
        disableEditText(mTextMagSensor_X);disableEditText(mTextMagSensor_Y);disableEditText(mTextMagSensor_Z);
        disableEditText(mTextGyroSensor_X);disableEditText(mTextGyroSensor_Y);disableEditText(mTextGyroSensor_Z);



       // spinnertime = findViewById(R.id.spinnerTime);
        spinnergeste = findViewById(R.id.spinnerGeste);
        //populateSpinnerTime();


        final DecimalFormat form = new DecimalFormat("0.000");

        //Step Sensor
        //Step Acount
        vStepsAccount = (TextView) findViewById(R.id.XSteps);

//permision access to storage
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1000);
        }



        final Handler handler = new Handler();
        btnStartColl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                    for(int i=0; i<3; i++ ){
//                        accl_mean[i] =  0;
//                    }
                clearForm((ViewGroup) findViewById(R.id.testFather));
                clearForm((ViewGroup) findViewById(R.id.meanLayout));


                if (accChecked) {
                    nmbrOfCheck++;
                    if (mSensorAccelerometer != null) {
                        mSensorManager.registerListener(GestureRecognition.this, mSensorAccelerometer,
                                SensorManager.SENSOR_DELAY_NORMAL);
                    }
                }
                if (magnChecked) {
                    nmbrOfCheck++;
                    if (mSensorMagnetometer != null) {
                        mSensorManager.registerListener(GestureRecognition.this, mSensorMagnetometer,
                                SensorManager.SENSOR_DELAY_NORMAL);
                    }
                }
                if (gyroChecked) {
                    nmbrOfCheck++;
                    if (mSensorGyroscope != null) {
                        mSensorManager.registerListener(GestureRecognition.this, mSensorGyroscope,
                                SensorManager.SENSOR_DELAY_NORMAL);
                    }
                }
                //stepsCounter
                if (stepsCheck) {
                    nmbrOfCheck++;
                    if (StepsAccount != null) {
                        mSensorManager.registerListener(GestureRecognition.this, StepsAccount,
                                SensorManager.SENSOR_DELAY_NORMAL);
                        mSensorManager.registerListener(GestureRecognition.this, StepsDetector,
                                SensorManager.SENSOR_DELAY_NORMAL);
                    }
                }

                if(nmbrOfCheck != 0) {
                    // disable the checkboxes when the collecting of data begin (that means one/all  sensor/s is/are checked)
                    btnStartColl.setEnabled(false);
                    checkbox_Acc.setEnabled(false);
                    checkbox_Magn.setEnabled(false);
                    checkbox_gyro.setEnabled(false);
                    chechbox_steps.setEnabled(false);

                    handler.postDelayed(new Runnable() {
                        @SuppressLint("StringFormatInvalid")
                        @Override
                        public void run() {

                            nmbrOfCheck = 0;
                            // enable the checkboxes when 20s of collecting done
                            btnStartColl.setEnabled(true);
                            checkbox_Acc.setEnabled(true);
                            checkbox_Magn.setEnabled(true);
                            checkbox_gyro.setEnabled(true);
                            chechbox_steps.setEnabled(true);

                            // stop listening for all sensors after 20s
                            mSensorManager.unregisterListener(GestureRecognition.this);


                            Log.e("listSize after collect" , " value : " + acclDataCollection.size());
                            // calculate the mean after the collection of data done
                            double mean[] = new double[] {0,0,0};
                            for (ValuesOf_x_y_z d : acclDataCollection ){
                                mean[0] += d.x;
                                mean[1] += d.y;
                                mean[2] += d.z;
                            }
                            Log.e("sum ", "value" + mean[0] + " " + mean[1] + " " + mean[2] );
                            mean[0] /= acclDataCollection.size();
                            mean[1] /= acclDataCollection.size();
                            mean[2] /= acclDataCollection.size();
                            Log.e("division ", "value" + mean[0] + " " + mean[1] + " " + mean[2] );
                            String resultx, resulty, resultz;
                            if(stepsCheck){
                                StepsSave = 0;
                                StepsSave = StepCounter;
                                StepCounter = -1;
                                StepDetector = 0;
                            }
                            if(acclDataCollection.size() != 0){
                                resultAcc = meanFun(acclDataCollection, acclDataCollection.size());
                                x_accMean = " "+ resultAcc[0];
                                y_accMean = " "+ resultAcc[1];
                                z_accMean = " "+ resultAcc[2];

                                Log.e("affichage ", " Means:  " +resultAcc[0] + " " +resultAcc[1]+ " "+ resultAcc[2]);
                                // before clear data i should upload it to the dataBase
                                acclDataCollection.clear();
                            }
                            Log.e("listSize after clean" , " value : " + acclDataCollection.size());

                            if(magnDataCollection.size() != 0){
                                resultGyro = meanFun(magnDataCollection, magnDataCollection.size());
                                x_magMean = " "+ resultGyro[0];
                                y_magMean = " "+ resultGyro[1];
                                z_magMean = " "+ resultGyro[2];

                                // before clear data i should upload it to the dataBase
                                magnDataCollection.clear();
                            }
                            if(gyroDataCollection.size() != 0){
                                resultMagn = meanFun(gyroDataCollection, gyroDataCollection.size());
                                x_gyroMean = " "+ resultMagn[0];
                                y_gyroMean = " "+ resultMagn[1];
                                z_gyroMean = " "+ resultMagn[2];

                                // before clear data i should upload it to the dataBase
                                gyroDataCollection.clear();
                            }
                            HttpAgent.post("http://95.111.237.128:8080/WebServiceTest/Greeting")
                                    .queryParams("acc_x",resultAcc[0]+"","acc_y",resultAcc[1]+"","acc_z",resultAcc[2]+""
                                            ,"gyro_x",resultGyro[0]+"","gyro_y",resultGyro[1]+"","gyro_z",resultGyro[2]+"",
                                            "magn_x",resultGyro[0]+"","magn_y",resultGyro[1]+"","magn_z",resultGyro[2]+"",
                                            "steps",StepsSave + "")
                                    .goJson(new JsonCallback() {
                                        @Override
                                        protected void onDone(boolean success, JSONObject jsonObject) {
                                            Log.e("msg", "value" + jsonObject);
                                            try {
                                                Log.e("msg", "value " + jsonObject.get("Greeting"));
                                                textPrediction.setText(jsonObject.get("Greeting").toString());
                                                if(textPrediction.getText().toString().equals("running")){giffile.setVisibility(View.INVISIBLE);image.setVisibility(View.VISIBLE);image.setAnimation("running.json");image.playAnimation();}
                                                if(textPrediction.getText().toString().equals("Walking")){giffile.setVisibility(View.INVISIBLE);image.setVisibility(View.VISIBLE);image.setAnimation("walking.json");image.playAnimation();}
                                                if(textPrediction.getText().toString().equals("Standing")){giffile.setVisibility(View.INVISIBLE);image.setVisibility(View.VISIBLE);image.setAnimation("standing1.json");image.playAnimation();}
                                                if(textPrediction.getText().toString().equals("Sitting")){giffile.setVisibility(View.INVISIBLE);image.setVisibility(View.VISIBLE);image.setAnimation("sitting.json");image.playAnimation();}
                                                if(textPrediction.getText().toString().equals("Upstairs")){image.setVisibility(View.INVISIBLE);giffile.setVisibility(View.VISIBLE);giffile.setImageResource(R.drawable.stairup);}
                                                if(textPrediction.getText().toString().equals("Downstairs")){image.setVisibility(View.INVISIBLE);giffile.setVisibility(View.VISIBLE);giffile.setImageResource(R.drawable.downstairs);}
                                                if(textPrediction.getText().toString().equals("InElevator")){giffile.setVisibility(View.VISIBLE);image.setVisibility(View.INVISIBLE);image.setAnimation("ascensore.json");image.playAnimation();}

                                                Geste=textPrediction.getText().toString();
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
//                                                    JSONArray jsonArray = jsonObject.getJSONArray("data");
//                                                    Toast.makeText(TestActivity.this, jsonArray.toString(), Toast.LENGTH_LONG).show();

                                        }
                                    });
                        }
                    }, 20000);
                }
            }
        });

        // till now doing nothing
        checkbox_gyro.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
            }
        });
    }
    //Permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1000 :
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "Permission granted!",Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permission not granted!",Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }
    /////


    @Override
    protected void onStart() {
        super.onStart();
       // accMeanField.setVisibility(View.INVISIBLE);
      //  magnMeanField.setVisibility(View.INVISIBLE);
      //  gyroMeanField.setVisibility(View.INVISIBLE);
        clearForm((ViewGroup) findViewById(R.id.meanLayout));
        clearForm((ViewGroup) findViewById(R.id.testFather));
    }
    @Override
    protected void onStop() {
        super.onStop();
        mSensorManager.unregisterListener(this);

    }

    // arrayList for collecting data and calculate the mean function
    final ArrayList<ValuesOf_x_y_z> acclDataCollection = new ArrayList<>();
    final ArrayList<ValuesOf_x_y_z> magnDataCollection = new ArrayList<>();
    final ArrayList<ValuesOf_x_y_z> gyroDataCollection = new ArrayList<>();

    // tables for calculate the mean of each sensor (sum and then division)
    double accl_mean[] = new double [] {0,0,0};


    private int StepCounter = -1;
    private int StepDetector = 0;
    @SuppressLint("StringFormatInvalid")
    @Override
    public void onSensorChanged(SensorEvent event) {
        int sensorType = event.sensor.getType();
        Sensor sensor = event.sensor;

        final double alpha = 0.8;
        double gravity[] = new double[3];
        // tables for displaying data of sensors instead of using event.values directly
        double linear_acceleration_acc[] = new double[3];
        double linear_acceleration_gyro[] = new double[3];
        double linear_acceleration_magn[] = new double[3];

        if (sensorType ==  Sensor.TYPE_ACCELEROMETER){
            // Isolate the force of gravity with the low-pass filter.
            gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
            gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
            gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

            // Remove the gravity contribution with the high-pass filter.
            linear_acceleration_acc[0] = event.values[0] - gravity[0];
            linear_acceleration_acc[1] = event.values[1] - gravity[1];
            linear_acceleration_acc[2] = event.values[2] - gravity[2];

            // i used two methods for acce arrayList and array
//            for (int i=0; i<3; i++){
//                accl_mean[i] = accl_mean[i] + linear_acceleration_acc[i];
//            }

            // collected the data for 20s to set it in dataBase so i used arrayList
            acclDataCollection.add(new ValuesOf_x_y_z(linear_acceleration_acc[0],linear_acceleration_acc[1],linear_acceleration_acc[2]));
        }

        if (sensorType ==  Sensor.TYPE_MAGNETIC_FIELD){
            linear_acceleration_magn[0] = event.values[0];
            linear_acceleration_magn[1] = event.values[1];
            linear_acceleration_magn[2] = event.values[2];

            // collected the data for 20s to set it in dataBase so i used arrayList
            magnDataCollection.add(new ValuesOf_x_y_z(linear_acceleration_magn[0],linear_acceleration_magn[1],linear_acceleration_magn[2]));
        }

        if (sensorType ==  Sensor.TYPE_GYROSCOPE){
            linear_acceleration_gyro[0] = event.values[0];
            linear_acceleration_gyro[1] = event.values[1];
            linear_acceleration_gyro[2] = event.values[2];

            // collected the data for 20s to set it in dataBase so i used arrayList
            gyroDataCollection.add(new ValuesOf_x_y_z(linear_acceleration_gyro[0],linear_acceleration_gyro[1],linear_acceleration_gyro[2]));
        }
        if (sensorType == Sensor.TYPE_STEP_COUNTER){
            totalSteps = (int) event.values[0];
            for (float val : event.values){
                StepCounter++;
            }
            XSteps.setText(String.valueOf(StepCounter));
        }



        String textAccX;
        String textAccY;
        String textAccZ;
        String textMagX;
        String textMagY;
        String textMagZ;
        String textGyrX;
        String textGyrY;
        String textGyrZ;
        switch (sensorType) {
            // Event came from the sensors.
            case Sensor.TYPE_ACCELEROMETER: {
                textAccX = " "+ linear_acceleration_acc[0];
                mTextAccSensor_X.setText(textAccX);
                textAccY = " "+ linear_acceleration_acc[1];
                mTextAccSensor_Y.setText(textAccY);
                textAccZ = " "+ linear_acceleration_acc[2];
                mTextAccSensor_Z.setText(textAccZ);

//                x_accMean.setText(getResources().getString(R.string.x_axis, accl_mean[0]));
//                y_accMean.setText(getResources().getString(R.string.y_axis, accl_mean[1]));
//                z_accMean.setText(getResources().getString(R.string.z_axis, accl_mean[2]));
                break;
            }
            case Sensor.TYPE_MAGNETIC_FIELD: {
                textMagX = " " + linear_acceleration_magn[0];
                mTextMagSensor_X.setText(textMagX);
                textMagY = " " + linear_acceleration_magn[1];
                mTextMagSensor_Y.setText(textMagY);
                textMagZ = " " + linear_acceleration_magn[2];
                mTextMagSensor_Z.setText(textMagZ);
                break;
            }
            case Sensor.TYPE_GYROSCOPE: {
                textGyrX = " " + linear_acceleration_gyro[0];
                mTextGyroSensor_X.setText(textGyrX);
                textGyrY = " " + linear_acceleration_gyro[1];
                mTextGyroSensor_Y.setText(textGyrY);
                textGyrZ = " " + linear_acceleration_gyro[2];
                mTextGyroSensor_Z.setText(textGyrZ);
                break;
            }
            default:
                // do nothing
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }



    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
    }

    public void onCheckboxAcc(View view) {
        if(accChecked){
            checkbox_Acc.setTextColor(Color.parseColor("#000000"));
        }else{
            checkbox_Acc.setTextColor(Color.parseColor("#0099CC"));
        }
        accChecked = !accChecked;
    }

    public void onCheckboxMagn(View view) {
        if(magnChecked){
            checkbox_Magn.setTextColor(Color.parseColor("#000000"));
        }else{
            checkbox_Magn.setTextColor(Color.parseColor("#0099CC"));
        }
        magnChecked = !magnChecked;
    }
    public void onCheckboxStep(View view) {
        if(stepsCheck){
            chechbox_steps.setTextColor(Color.parseColor("#000000"));
        }else{
            chechbox_steps.setTextColor(Color.parseColor("#0099CC"));
        }
        stepsCheck = !stepsCheck;
    }

    public void onCheckboxGyro(View view) {
        if(gyroChecked){
            checkbox_gyro.setTextColor(Color.parseColor("#000000"));
        }else{
            checkbox_gyro.setTextColor(Color.parseColor("#0099CC"));
        }
        gyroChecked = !gyroChecked;
    }

    private void clearForm(ViewGroup group) {
        for (int i = 0, count = group.getChildCount(); i < count; ++i) {
            View view = group.getChildAt(i);
            if (view instanceof EditText) {
                ((EditText)view).setText("");
            }

            if(view instanceof ViewGroup && (((ViewGroup)view).getChildCount() > 0))
                clearForm((ViewGroup)view);
        }
    }

    private void disableEditText(EditText editText) {
        editText.setFocusable(false);
        editText.setCursorVisible(false);
    }

    private double[] meanFun (ArrayList<ValuesOf_x_y_z> dataCollection, int size) {
        double mean[] = new double[] {0,0,0};
        for (ValuesOf_x_y_z d : dataCollection ){
            mean[0] += d.x;
            mean[1] += d.y;
            mean[2] += d.z;
        }
        mean[0] /= size;
        mean[1] /= size;
        mean[2] /= size;
        return mean;
    }
    private void populateSpinnerGeste() {
       /* ArrayAdapter<String> gesteAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.spenner_geste));
        gesteAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnergeste.setPrompt("Select a Gesture");
        spinnergeste.setAdapter(gesteAdapter);*/
        ArrayAdapter<String> adapter = new ArrayAdapter<String>( GestureRecognition.this, android.R.layout.simple_spinner_dropdown_item) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                View v = super.getView(position, convertView, parent);
                if (position == getCount()) {
                    ((TextView)v.findViewById(android.R.id.text1)).setText("");
                    ((TextView)v.findViewById(android.R.id.text1)).setHint(getItem(getCount())); //"Hint to be displayed"
                }

                return v;
            }

            @Override
            public int getCount() {
                return super.getCount()-1; // you dont display last item. It is used as hint.
            }

        };

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter.add("Walking");
        adapter.add("Sitting");
        adapter.add("Standing");
        adapter.add("running");
        adapter.add("Up Stairs");
        adapter.add("Down Stairs");
        adapter.add("In Elevator");
        adapter.add("Select Gesture");


    }




      /* public int GetDelay(){
        String itemSelected = spinnertime.getSelectedItem().toString();
        switch (itemSelected){
            case ("5 s"): delay = 5000; break;
            case ("10 s"): delay = 10000; break;
            case ("15 s"): delay = 15000; break;
            case ("20 s"): delay = 20000; break;
            default:
        }

        Toast.makeText(GestureRecognition.this," Durée: "+delay,Toast.LENGTH_SHORT).show();
        return delay;}*/

    //StepsCounter


    private void saveData(){

        SharedPreferences sharedPreferences  = getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        Float saveNumber = sharedPreferences.getFloat("key1",0f);
        Log.d(String.valueOf(GestureRecognition.this),"§savedNumber");
        previousTotalSteps = saveNumber;
        previousTotalSteps = totalSteps;
        Distance.setText(0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat("Key1", previousTotalSteps);
        editor.apply();
    }

    private void loadData(){
    }

//    public void predictionFunction() {
//        HttpAgent.post("http://192.168.137.1:8080/WebServiceTest/Greeting")
//            .queryParams("acc_x",resultAcc[0]+"","acc_y",resultAcc[1]+"","acc_z",resultAcc[2]+""
//                    ,"gyro_x",resultGyro[0]+"","gyro_y",resultGyro[1]+"","gyro_z",resultGyro[2]+"",
//                    "magn_x",resultGyro[0]+"","magn_y",resultGyro[1]+"","magn_z",resultGyro[2]+"")
//            .goJson(new JsonCallback() {
//                @Override
//                protected void onDone(boolean success, JSONObject jsonObject) {
//                    Log.e("msg", "value" + jsonObject);
//                    try {
//                        Log.e("msg", "value " + jsonObject.get("Greeting"));
//                        textPrediction.setText(jsonObject.get("Greeting").toString());
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
////                                                    JSONArray jsonArray = jsonObject.getJSONArray("data");
////                                                    Toast.makeText(TestActivity.this, jsonArray.toString(), Toast.LENGTH_LONG).show();
//
//            }
//        });
//    }


    public void CallPreviousScreen(View view) {
        Intent intent = new Intent(getApplicationContext(), Menu.class);
        startActivity(intent);
    }


}

class ValuesOf_x_y_z{
    double x,y,z;

    ValuesOf_x_y_z(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

}
