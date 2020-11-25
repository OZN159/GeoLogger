package com.example.geologger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.GnssMeasurementsEvent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016/7/8.
 */
public class FragmentRawData extends Fragment {
    private static final int SVID = 0;
    private static final int CODETYPE = 1;
    private static final int S_N_R = 2;
    private static final int L_1 = 3;
    private static final int C_1 = 4;
    private static final int D_1 = 5;
    private static final int L_2 = 6;
    private static final int C_2 = 7;
    private static final int D_2 = 8;

    //file storage path
    private static final String FILEPATH = "/sdcard/GeoLogger/";
    private static final String FILENAME_NMEA = "Rinex.20o";

    private TextView tv;
    private GridView gv;
    private Button btn;
    private RawAdapter mRawAdapter;
    private GNSSContainer mGnssContainer;
    private Rinex mRinex;
    private int rinex_status;
    private GnssMeasurementsEvent mGnssEvent;
    private String name;
    private StringBuilder builder;
    private List text = new ArrayList();
    private List item = new ArrayList();
    private String[] gnss_clock = new String[10];
    private String[][] raw_list = new String[80][25];
    private LocalBroadcastManager broadcastManager;
    private FileOperation mFileOperation;
    private RandomAccessFile mFile_rinex = null;

    public FragmentRawData(String fName, GNSSContainer mGnssContainer, Rinex rinex){
        this.name = fName;
        this.mGnssContainer = mGnssContainer;
        this.mRinex = rinex;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.framement_raw, container,false);

        tv = (TextView) view.findViewById(R.id.fragment_tv);
        gv = (GridView) view.findViewById(R.id.fragment_gv);
        btn = (Button) view.findViewById(R.id.fragment_log);

        rinex_status = 0;

        //button onclick function
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (btn.getText().equals("START LOG")) {
                        btn.setText("STOP LOG");

                        //keep the screen light
                        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

                        //create the NMEA data collecting file
                        mFileOperation = new FileOperation(getActivity(), FILEPATH, FILENAME_NMEA);
                        mFile_rinex = mFileOperation.openSuffixFile();

                        //create the Rinex collecting file
                        mRinex.setRinexHeader(mFileOperation, mFile_rinex);

                        //start log
                        rinex_status = 1;

                        String notice = "The screen will keep on while logging";
                        Toast.makeText(getActivity(), notice, Toast.LENGTH_LONG).show();
                    } else {
                        if (mFile_rinex != null) { mFile_rinex.close(); }
                        btn.setText("START LOG");

                        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

                        //stop log
                        rinex_status = 0;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        //register the broadcastManager
        broadcastManager = LocalBroadcastManager.getInstance(getContext());
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.Gnss.CUSTOM_INTENT");
        broadcastManager.registerReceiver(new rawReceiver(), intentFilter);

        mRawAdapter = new RawAdapter(getContext(), item);

        gv.setAdapter(mRawAdapter);
        tv.setMovementMethod(ScrollingMovementMethod.getInstance());
        builder = new StringBuilder(name);

        return view;
    }

    private class rawReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
//            String notice;

            text = mGnssContainer.getGnssValues();
            builder = mGnssContainer.getBuilder();
            tv.setText(builder.toString());
            item.clear();
            item.addAll(text);
            mRawAdapter.notifyDataSetChanged();

            if (rinex_status == 1) {
                raw_list = mGnssContainer.getGnssValuesList();
                gnss_clock = mGnssContainer.getGnssClock();
                //forward the data to the Rinex engine
                mRinex.getRawData(gnss_clock, raw_list);
                //write down each epoch to Rinex file
                mRinex.setRinexEpoch(mFileOperation, mFile_rinex);
            }

//            notice = "receive the signal:" + text.get(3);
//            Toast.makeText(context, notice,Toast.LENGTH_SHORT).show();
        }
    }
}
