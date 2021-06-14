package com.idevelop.location;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.telephony.CellIdentityGsm;
import android.telephony.CellIdentityLte;
import android.telephony.CellIdentityWcdma;
import android.telephony.CellInfo;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.CellSignalStrengthWcdma;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Gsm extends AppCompatActivity {
    final private int REQUEST_CODE_ASK_PERMISSIONS = 13;
    TextView tvCellID,PC,MCC,MNC,CellID,RSSIC,Area,technology;
    ImageView ivGsmLevel,ivGeneration;
    Button btnCellId,plus;
    List<CellInfo> cellInfos;
    private ProgressBar progress;
    CardView PCell, cardMCC, cardMNC;
    private int mInterval = 2000; // 5 seconds by default, can be changed later
    private Handler mHandler;
    int count=0;
//    MyPhoneStateListener mPhoneStatelistener;


//    class MyPhoneStateListener extends PhoneStateListener {
//
//        public void onCellLocationChanged(CellLocation location)
//        {
//            GsmCellLocation gsmCellLocation = (GsmCellLocation) location;
//            gsmCellLocation.getCid();
//            gsmCellLocation.getLac();
//            gsmCellLocation.getPsc();
//
//            Log.e("cellp -------------", "registered: "+gsmCellLocation.toString());
//
//        }
//
//        public void onCellInfoChanged(List<CellInfo> cellInfo) {
//            super.onCellInfoChanged(cellInfo);
//            Log.e("-----------cell Info---", "value " +  cellInfo);
//
////            CellInfo cellInfo = cellInfoList.get(0);
////
////            CellInfoGsm cellInfoGsm = (CellInfoGsm) cellInfo;
////            Log.e("------------gsm cell ID", "value " +  cellInfoGsm.getCellIdentity());
////            super.onCellInfoChanged(cellInfoList);
////
////            Log.e("", "value " + cellInfoList);
////            CellInfo cellInfo = cellInfoList.get(0);
////            String additional_info = null;
////            int strength = 0;
////
////            if (cellInfo instanceof CellInfoGsm) {
////                CellInfoGsm cellInfoGsm = (CellInfoGsm) cellInfo;
////                CellIdentityGsm cellIdentityGsm = cellInfoGsm.getCellIdentity();
////                CellSignalStrengthGsm cellSignalStrengthWcdma = cellInfoGsm.getCellSignalStrength();
////                strength = cellSignalStrengthWcdma.getDbm();
////                additional_info = "cell identity GSM " + cellIdentityGsm.getCid() + "\n"
////                        + "Mobile country code " + cellIdentityGsm.getMcc() + "\n"
////                        + "Mobile network code " + cellIdentityGsm.getMnc() + "\n"
////                        + "local area " + cellIdentityGsm.getLac() + "\n"
////                        + "RSSI " + strength + "\n";
////                levelOfSignal(strength, ivGsmLevel);
////                Log.e("", "value " +  additional_info);
////            } else if (cellInfo instanceof CellInfoLte) {
////                CellInfoLte cellInfoLte = (CellInfoLte) cellInfo;
////                CellIdentityLte cellIdentityLte = cellInfoLte.getCellIdentity();
////                CellSignalStrengthLte cellSignalStrengthWcdma = cellInfoLte.getCellSignalStrength();
////                strength = cellSignalStrengthWcdma.getDbm();
////                additional_info = "cell identity LTE " + cellIdentityLte.getCi() + "\n"
////                        + "Mobile country code " + cellIdentityLte.getMcc() + "\n"
////                        + "Mobile network code " + cellIdentityLte.getMnc() + "\n"
////                        + "physical cell " + cellIdentityLte.getPci() + "\n"
////                        + "Tracking area code " + cellIdentityLte.getTac() + "\n"
////                        + "RSSI " + strength + "\n";
////                levelOfSignal(strength, ivGsmLevel);
////                Log.e("", "value " +  additional_info);
////            } else if (cellInfo instanceof CellInfoWcdma){
////                CellInfoWcdma cellInfoWcdma = (CellInfoWcdma) cellInfo;
////                CellIdentityWcdma cellIdentityWcdma = cellInfoWcdma.getCellIdentity();
////                CellSignalStrengthWcdma cellSignalStrengthWcdma = cellInfoWcdma.getCellSignalStrength();
////                strength = cellSignalStrengthWcdma.getDbm();
////                additional_info = "cell identity WCDMA " + cellIdentityWcdma.getCid() + "\n"
////                        + "Mobile country code " + cellIdentityWcdma.getMcc() + "\n"
////                        + "Mobile network code " + cellIdentityWcdma.getMnc() + "\n"
////                        + "local area " + cellIdentityWcdma.getLac() + "\n"
////                        +  "RSSI " + strength + "\n";
////                levelOfSignal(strength, ivGsmLevel);
////                Log.e("", "value " +  additional_info);
////            }
//////            return additional_info;
//        }
//
//        @Override
//        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
//
//            super.onSignalStrengthsChanged(signalStrength);
//            Log.e("gsm signal", "value " + signalStrength.getLevel());
//
//        }
//    }


    public class MobileInfoRecognizer {
        public void getCellInfo(CellInfo cellInfo) {
            String additional_info = null;
            int strength = 0;
            if (cellInfo instanceof CellInfoGsm) {
                CellInfoGsm cellInfoGsm = (CellInfoGsm) cellInfo;
                CellIdentityGsm cellIdentityGsm = cellInfoGsm.getCellIdentity();
                CellSignalStrengthGsm cellSignalStrengthWcdma = cellInfoGsm.getCellSignalStrength();
                strength = cellSignalStrengthWcdma.getDbm();
               /* additional_info = "cell identity GSM " + cellIdentityGsm.getCid() + "\n"
                        + "Mobile country code " + cellIdentityGsm.getMcc() + "\n"
                        + "Mobile network code " + cellIdentityGsm.getMnc() + "\n"
                        + "local area " + cellIdentityGsm.getLac() + "\n"
                        + "RSSI " + strength + "\n";
                tvCellID.setText(additional_info);*/
                CellID.setText(""+cellIdentityGsm.getCid());
                Area.setText(""+cellIdentityGsm.getLac());
                RSSIC.setText(""+strength);
                MCC.setText(""+cellIdentityGsm.getMcc());
                MNC.setText(""+cellIdentityGsm.getMnc());
                technology.setText("GSM");
                ivGeneration.setImageResource(R.drawable.g2);
                levelOfSignal(strength, ivGsmLevel);
                if(count == 1){
                    getGsmInfoPlus();
                }

            } else if (cellInfo instanceof CellInfoLte) {
                CellInfoLte cellInfoLte = (CellInfoLte) cellInfo;
                CellIdentityLte cellIdentityLte = cellInfoLte.getCellIdentity();
                CellSignalStrengthLte cellSignalStrengthWcdma = cellInfoLte.getCellSignalStrength();
                strength = cellSignalStrengthWcdma.getDbm();
                /*additional_info = "cell identity LTE " + cellIdentityLte.getCi() + "\n"
                        + "Mobile country code " + cellIdentityLte.getMcc() + "\n"
                        + "Mobile network code " + cellIdentityLte.getMnc() + "\n"
                        + "physical cell " + cellIdentityLte.getPci() + "\n"
                        + "Tracking area code " + cellIdentityLte.getTac() + "\n"
                        + "RSSI " + strength + "\n";
                tvCellID.setText(additional_info);*/
                CellID.setText(""+cellIdentityLte.getCi());
                Area.setText(""+cellIdentityLte.getTac());
                RSSIC.setText(""+strength);
                MCC.setText(""+cellIdentityLte.getMcc());
                MNC.setText(""+cellIdentityLte.getMnc());
                technology.setText("LTE");
                PC.setText(""+cellIdentityLte.getPci());
                ivGeneration.setImageResource(R.drawable.g4);
                levelOfSignal(strength, ivGsmLevel);
                if(count == 1){
                    getLteInfoPlus();
                }

            } else if (cellInfo instanceof CellInfoWcdma) {
                CellInfoWcdma cellInfoWcdma = (CellInfoWcdma) cellInfo;
                CellIdentityWcdma cellIdentityWcdma = cellInfoWcdma.getCellIdentity();
                CellSignalStrengthWcdma cellSignalStrengthWcdma = cellInfoWcdma.getCellSignalStrength();
                strength = cellSignalStrengthWcdma.getDbm();
               /* additional_info = "cell identity WCDMA " + cellIdentityWcdma.getCid() + "\n"
                        + "Mobile country code " + cellIdentityWcdma.getMcc() + "\n"
                        + "Mobile network code " + cellIdentityWcdma.getMnc() + "\n"
                        + "local area " + cellIdentityWcdma.getLac() + "\n"
                        + "RSSI " + strength + "\n";
                levelOfSignal(strength, ivGsmLevel);*/
                CellID.setText(""+cellIdentityWcdma.getCid());
                Area.setText(""+cellIdentityWcdma.getLac());
                RSSIC.setText(""+strength);
                MCC.setText(""+cellIdentityWcdma.getMcc());
                MNC.setText(""+cellIdentityWcdma.getMnc());
                technology.setText("Wcdma");
                ivGeneration.setImageResource(R.drawable.g3);
                levelOfSignal(strength, ivGsmLevel);
                if(count == 1){
                    getGsmInfoPlus();
                }

            }
//            return additional_info;
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gsm);
        ivGsmLevel =  findViewById(R.id.ivGSMLevel);
        ivGeneration = findViewById(R.id.ivGeneration);
        RSSIC =  findViewById(R.id.RSSICell);
        CellID =  findViewById(R.id.CellID);
        Area =  findViewById(R.id.Area);
        MNC =  findViewById(R.id.MNC);
        MCC =  findViewById(R.id.MCC);
        technology =  findViewById(R.id.technology);
        PCell = findViewById(R.id.PCell);
        progress = findViewById(R.id.progress1);
        cardMCC = findViewById(R.id.cardMCC);
        cardMNC = findViewById(R.id.cardMNC);
        plus = findViewById(R.id.wifi1);


        cardMCC.setVisibility(View.INVISIBLE);
        cardMNC.setVisibility(View.INVISIBLE);
        PCell.setVisibility(View.INVISIBLE);

//        btnCellId = (Button) findViewById(R.id.btnCellID);

        mHandler = new Handler();
        startRepeatingTask();
//
//        btnCellId.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                // request permission in run time
//                requestPermissions(new String[]{
//                                Manifest.permission.READ_PHONE_STATE,
//                                Manifest.permission.ACCESS_FINE_LOCATION},
//                        REQUEST_CODE_ASK_PERMISSIONS);
//
//                if (ActivityCompat.checkSelfPermission(CellularActivity.this,
//                        Manifest.permission.ACCESS_FINE_LOCATION) !=
//                        PackageManager.PERMISSION_GRANTED
//                        && ActivityCompat.checkSelfPermission(CellularActivity.this,
//                        Manifest.permission.ACCESS_COARSE_LOCATION) !=
//                        PackageManager.PERMISSION_GRANTED
//                        && ActivityCompat.checkSelfPermission(CellularActivity.this,
//                        Manifest.permission.READ_PHONE_STATE) !=
//                        PackageManager.PERMISSION_GRANTED) {
//                    return;
//                }
//
//                // get information about connection (GSM / LTE / WCDMA--> technolgie of multiplixing used by 3G)
//                final TelephonyManager telephony = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
//                MobileInfoRecognizer mobileInfoRecognizer = new MobileInfoRecognizer();
//                cellInfos = telephony.getAllCellInfo();
//                String additional_info = mobileInfoRecognizer.getCellInfo(cellInfos.get(0));
//                Log.e("connection information" , " value " + additional_info);


//                mPhoneStatelistener = new MyPhoneStateListener();
//                telephony.listen(mPhoneStatelistener, PhoneStateListener.LISTEN_CELL_LOCATION);
//                telephony.listen(mPhoneStatelistener, PhoneStateListener.LISTEN_CELL_INFO);
//                telephony.listen(mPhoneStatelistener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);


//                this.telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//                this.phoneStateListener = setupPhoneStateListener();
//                this.telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CELL_LOCATION);
//                this.telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_DATA_CONNECTION_STATE);
//                this.telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);


        //TelephonyManager is a system service
//                TelephonyManager tm = (TelephonyManager)getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        //Listener, if the system status changes, it will call back this listener's function
//                PhoneStateListener pl = new PhoneStateListener(){
//                    public void onServiceStateChanged(ServiceState serviceState) {
//                        super.onServiceStateChanged(serviceState);
//                        Log.v("", serviceState.toString());
//                    }
//
//                    @Override
//                    public void onCallStateChanged(int state, String incomingNumber) {
//                        super.onCallStateChanged(state, incomingNumber);
//                        Log.v("", "state = "+state+"  incomingNumber = "+incomingNumber);
//                    }
//
//                    @Override
//                    public void onDataConnectionStateChanged(int state, int networkType) {
//                        super.onDataConnectionStateChanged(state, networkType);
//                        Log.v("", "state = " + state + "  networkType = " + networkType);
//
//                    }
//
//                    @Override
//                    public void onSignalStrengthsChanged(SignalStrength signalStrength) {
//                        super.onSignalStrengthsChanged(signalStrength);
//                        Log.v("", signalStrength.toString());
//
//                    }
//
//                    @Override
//                    public void onCellInfoChanged(List<CellInfo> cellInfo) {
//                        super.onCellInfoChanged(cellInfo);
//
////                        if (cellInfo == null) return;
//                        for(CellInfo Info: cellInfo) {
//                            Log.e("cell info ===========", Info.toString());
//                        }
//                    }
//
////                    @Override
////                    public void onCellInfoChanged(List<CellInfo> cellInfo) {
////                        super.onCellInfoChanged(cellInfo);
////                        for(CellInfo Info: cellInfo) {
////                            Log.v("", Info.toString());
////                        }
////                    }
//
//                    @Override
//                    public void onDataActivity(int direction) {
//                        super.onDataActivity(direction);
//                        Log.v("", "direction = " + direction);
//
//                    }
//
//                    @Override
//                    public void onCallForwardingIndicatorChanged(boolean cfi) {
//                        super.onCallForwardingIndicatorChanged(cfi);
//                        Log.v("", "cfi = " + cfi);
//
//                    }
//
//                    @Override
//                    public void onMessageWaitingIndicatorChanged(boolean mwi) {
//                        super.onMessageWaitingIndicatorChanged(mwi);
//                        Log.v("", "mwi = " + mwi);
//
//                    }
//                };
//                //Register the listener to the corresponding listener through TelephonyManager. The second parameter of listen() indicates the specific state that needs to be monitored
//                tm.listen(pl,PhoneStateListener.LISTEN_CALL_FORWARDING_INDICATOR);
//                tm.listen(pl,PhoneStateListener.LISTEN_CALL_STATE);
//                tm.listen(pl,PhoneStateListener.LISTEN_CELL_INFO);
//                tm.listen(pl,PhoneStateListener.LISTEN_CELL_LOCATION);
//                tm.listen(pl,PhoneStateListener.LISTEN_DATA_ACTIVITY);
//                tm.listen(pl,PhoneStateListener.LISTEN_DATA_CONNECTION_STATE);
//                tm.listen(pl,PhoneStateListener.LISTEN_MESSAGE_WAITING_INDICATOR);
//                tm.listen(pl, PhoneStateListener.LISTEN_SERVICE_STATE);
//                tm.listen(pl,PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);


//            }
//        });
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
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void run() {
            try {
                final TelephonyManager telephony = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                MobileInfoRecognizer mobileInfoRecognizer = new MobileInfoRecognizer();
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    Activity#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for Activity#requestPermissions for more details.
                    return;
                }
                cellInfos = telephony.getAllCellInfo();
                mobileInfoRecognizer.getCellInfo(cellInfos.get(0));
            } finally {
                // 100% guarantee that this always happens, even if
                // your update method throws an exception
                mHandler.postDelayed(mStatusChecker, mInterval);
            }
        }
    };

    private void levelOfSignal(int level, ImageView ivSignalLevel) {
        if (level <= 0 && level >= -50) {
            //Best signal
            ivSignalLevel.setImageResource(R.drawable.gsm4);
        } else if (level < -50 && level >= -70) {
            //Good signal
            ivSignalLevel.setImageResource(R.drawable.gsm3);
        } else if (level < -70 && level >= -80) {
            //Low signal
            ivSignalLevel.setImageResource(R.drawable.gsm2);
        } else if (level < -80 && level >= -100) {
            //Very weak signal
            ivSignalLevel.setImageResource(R.drawable.gsm1);
        } else {
            // no signals
            ivSignalLevel.setImageResource(R.drawable.gsmsearch);
        }
    }
    public void CallPreviousScreen(View view) {
        Intent intent = new Intent(getApplicationContext(), Menu.class);
        startActivity(intent);
    }

    public void onClick (View view){
        count=1;
        prog();
    }

    private void prog() {
       progress.setVisibility(View.VISIBLE);
       plus.setVisibility(View.INVISIBLE);
    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
        @Override
        public void run() {
            progress.setVisibility(View.INVISIBLE);
            if(technology.getText()=="GSM"|| technology.getText()=="Wcdma"){
                getGsmInfoPlus();
            }else{getLteInfoPlus();}
        }
    },1000);}

        public void getGsmInfoPlus(){
            PCell.setVisibility(View.INVISIBLE);
            cardMCC.setVisibility(View.VISIBLE);
            cardMNC.setVisibility(View.VISIBLE);
        }
    public void getLteInfoPlus(){
        PCell.setVisibility(View.VISIBLE);
        cardMCC.setVisibility(View.VISIBLE);
        cardMNC.setVisibility(View.VISIBLE);
    }
}


