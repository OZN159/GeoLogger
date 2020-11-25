package com.example.geologger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/7/8.
 */
public class FragmentSatellite extends Fragment {
    //Constellation types
    private final int CONSTELLATION_GPS = 1;
    private final int CONSTELLATION_SBAS = 2;
    private final int CONSTELLATION_GLONASS = 3;
    private final int CONSTELLATION_QZSS = 4;
    private final int CONSTELLATION_BEIDOU = 5;
    private final int CONSTELLATION_GAL = 6;
    private final int CONSTELLATION_UNKNOWN = 0;

    private LocalBroadcastManager broadcastManager;
    private StarMapView sv_view;
    private GridView gv;
    private TextView tv_bds, tv_gps, tv_gln, tv_gal, tv_qzss, tv_all;
    private GNSSContainer mGnssContainer;
    private String name;
    private satelliteListAdapter mAdapter;
    private List item_list = new ArrayList();
    private List item = new ArrayList();

    // 高度角，方位角数组，卫星编号数组
    private double[] Altitude = new double[70];
    private double[] Azimuth = new double[70];
    private int[] PRN = new int[70];
    private float[] SNR = new float[70];
    private int[] Constellation = new int[70];

    private int[] sat_cnt = new int[10];
    private int sat_cnt_all;

    public FragmentSatellite(String fName, GNSSContainer mGnssContainer){
        this.name = fName;
        this.mGnssContainer = mGnssContainer;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.framement_satellite,container,false);

        sv_view = view.findViewById(R.id.fragment_satellite);

        //register the broadcastManager
        broadcastManager = LocalBroadcastManager.getInstance(getContext());
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.Gnss.CUSTOM_INTENT_SV");
        broadcastManager.registerReceiver(new FragmentSatellite.svReceiver(), intentFilter);

        //set satellite list adapter
//        gv = view.findViewById(R.id.fragment_satellite_list);
//        mAdapter = new satelliteListAdapter(getContext(), item);
//        gv.setAdapter(mAdapter);

        tv_bds = (TextView) view.findViewById(R.id.num_beidou);
        tv_bds.setText(" 0 ");
        Drawable drawable = ContextCompat.getDrawable(getActivity(), R.drawable.bds);
        drawable.setBounds(0, 0, 50, 50);
        tv_bds.setCompoundDrawables(drawable, null, null, null);

        tv_gps = (TextView) view.findViewById(R.id.num_gps);
        tv_gps.setText(" 0 ");
        drawable = ContextCompat.getDrawable(getActivity(), R.drawable.gps);
        drawable.setBounds(0, 0, 50, 50);
        tv_gps.setCompoundDrawables(drawable, null, null, null);

        tv_gln = (TextView) view.findViewById(R.id.num_glonass);
        tv_gln.setText(" 0 ");
        drawable = ContextCompat.getDrawable(getActivity(), R.drawable.glo);
        drawable.setBounds(0, 0, 50, 50);
        tv_gln.setCompoundDrawables(drawable, null, null, null);

        tv_gal = (TextView) view.findViewById(R.id.num_galileo);
        tv_gal.setText(" 0 ");
        drawable = ContextCompat.getDrawable(getActivity(), R.drawable.gal);
        drawable.setBounds(0, 0, 50, 50);
        tv_gal.setCompoundDrawables(drawable, null, null, null);

        tv_qzss = (TextView) view.findViewById(R.id.num_qzss);
        tv_qzss.setText(" 0 ");
        drawable = ContextCompat.getDrawable(getActivity(), R.drawable.qzs);
        drawable.setBounds(0, 0, 50, 50);
        tv_qzss.setCompoundDrawables(drawable, null, null, null);

        tv_all = (TextView) view.findViewById(R.id.num_all);
        tv_all.setText(" 0 ");
        drawable = ContextCompat.getDrawable(getActivity(), R.drawable.satellite);
        drawable.setBounds(0, 0, 50, 50);
        tv_all.setCompoundDrawables(drawable, null, null, null);

        return view;
    }

    private class svReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            PRN = mGnssContainer.getGnssPRN();
            Azimuth = mGnssContainer.getGnssAzimuth();
            Altitude = mGnssContainer.getGnssAltitude();
            SNR = mGnssContainer.getGnssSNR();
            sat_cnt_all = mGnssContainer.getGnssSatelliteCnt();
            Constellation = mGnssContainer.getGnssConstellation();

//            item.clear();
//            item.add("GPS:");
//            item.add("BD2:");
//            item.add("BD3:");
//            item.add("GLN:");
//            item.add("GAL:");
//            item.add("QZSS:");
//            mAdapter.notifyDataSetChanged();
            sat_cnt[CONSTELLATION_GPS] = 0;
            sat_cnt[CONSTELLATION_SBAS] = 0;
            sat_cnt[CONSTELLATION_GLONASS] = 0;
            sat_cnt[CONSTELLATION_BEIDOU] = 0;
            sat_cnt[CONSTELLATION_GAL] = 0;
            sat_cnt[CONSTELLATION_QZSS] = 0;
            sat_cnt[CONSTELLATION_UNKNOWN] = 0;
            for (int i = 0; i < sat_cnt_all; i++) {
//                if (Constellation[i]) {
//                    continue;
//                }
                switch (Constellation[i]) {
                    case CONSTELLATION_GPS: sat_cnt[CONSTELLATION_GPS]++; break;
                    case CONSTELLATION_SBAS: sat_cnt[CONSTELLATION_SBAS]++; break;
                    case CONSTELLATION_GLONASS: sat_cnt[CONSTELLATION_GLONASS]++; break;
                    case CONSTELLATION_BEIDOU: sat_cnt[CONSTELLATION_BEIDOU]++; break;
                    case CONSTELLATION_GAL: sat_cnt[CONSTELLATION_GAL]++; break;
                    case CONSTELLATION_QZSS: sat_cnt[CONSTELLATION_QZSS]++; break;
                    case CONSTELLATION_UNKNOWN: sat_cnt[CONSTELLATION_UNKNOWN]++; break;
                    default: break;

                }
            }

            sv_view.setMarker(Altitude, Azimuth, PRN, SNR);

            //satellite number
            tv_gps.setText(Integer.toString(sat_cnt[CONSTELLATION_GPS]));
            tv_gln.setText(Integer.toString(sat_cnt[CONSTELLATION_GLONASS]));
            tv_bds.setText(Integer.toString(sat_cnt[CONSTELLATION_BEIDOU]));
            tv_gal.setText(Integer.toString(sat_cnt[CONSTELLATION_GAL]));
            tv_qzss.setText(Integer.toString(sat_cnt[CONSTELLATION_QZSS]));
            tv_all.setText(Integer.toString(sat_cnt_all));
        }
    }

    public class satelliteListAdapter extends BaseAdapter {
        private LayoutInflater layoutInflater;
        private List text;

        public satelliteListAdapter(Context context, List text){
            this.text = text;
            layoutInflater = LayoutInflater.from(context);
        }
        @Override
        public int getCount() {
            return text.size();
        }

        @Override
        public Object getItem(int position) {
            return text.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = layoutInflater.inflate(R.layout.gridview_satellite_list,null);
            TextView tv = (TextView) v.findViewById(R.id.gridView_satellite_list);
            tv.setText((CharSequence) text.get(position));
            return v;
        }
    }
}