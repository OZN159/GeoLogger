package com.example.geologger;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.GnssClock;
import android.location.GnssMeasurement;
import android.location.GnssMeasurementsEvent;
import android.location.GnssStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

/**
 * Created by Administrator on 2016/7/8.
 */
public class GNSSContainer {
    private static final int LeapSecond = 0;
    private static final int TimeNanos = 1;
    private static final int TimeUncertaintyNanos = 2;
    private static final int FullBiasNanos = 3;
    private static final int BiasNanos = 4;
    private static final int BiasUncertaintyNanos = 5;
    private static final int DriftNanosPerSecond = 6;
    private static final int DriftUncertaintyNanosPerSecond = 7;
    private static final int HardwareClockDiscontinuityCount = 8;

    private static final int Svid = 0;
    private static final int ConstellationType = 1;
    private static final int TimeOffsetNanos = 2;
    private static final int State = 3;
    private static final int ReceivedSvTimeNanos = 4;
    private static final int ReceivedSvTimeUncertaintyNanos = 5;
    private static final int Cn0DbHz = 6;
    private static final int PseudorangeRateMetersPerSecond = 7;
    private static final int PseudorangeRateUncertaintyMetersPerSeconds = 8;
    private static final int AccumulatedDeltaRangeState = 9;
    private static final int AccumulatedDeltaRangeMeters = 10;
    private static final int AccumulatedDeltaRangeUncertaintyMeters = 11;
    private static final int CarrierFrequencyHz = 12;
    private static final int CarrierCycles = 13;
    private static final int CarrierPhase = 14;
    private static final int CarrierPhaseUncertainty = 15;
    private static final int MultipathIndicator = 16;
    private static final int SnrInDb = 17;
    private static final int AgcDb = 18;
    private static final int CarrierFreqHz = 19;
    private static final int CodeType = 20;

    private static final int SVID = 0;
    private static final int C_1 = 1;
    private static final int S_1 = 2;
    private static final int L_1 = 3;
    private static final int D_1 = 4;
    private static final int C_2 = 5;
    private static final int S_2 = 6;
    private static final int L_2 = 7;
    private static final int D_2 = 8;

    private static final int CODETYPE = 5;
    private static final int STATUS = 6;

    //Constellation types
    final int CONSTELLATION_GPS = 1;
    final int CONSTELLATION_SBAS = 2;
    final int CONSTELLATION_GLONASS = 3;
    final int CONSTELLATION_QZSS = 4;
    final int CONSTELLATION_BEIDOU = 5;
    final int CONSTELLATION_GAL = 6;
    final int CONSTELLATION_UNKNOWN = 0;

    private Context mContext;
    private Rinex mRinex;

    private Intent intent;
    private Intent intent_sv;
    private LocationManager mLocationManager;
    private StringBuilder mBuilder;
    private List text = new ArrayList();
    private String[] gnss_values = new String[10];
    private String[] raw_values = new String[25];
    private String[][] raw_values_list = new String[80][25];
    private String[] rinex_values = new String[10];

    private double[] Altitude = new double[70];
    private double[] Azimuth = new double[70];
    private int[] PRN = new int[70];
    private int[] Constellation = new int[70];
    private float[] SNR = new float[70];
    private int satellite_cnt;

    private LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location mlocal) {
            if (mlocal == null) return;
//            String strResult = "getAccuracy:" + mlocal.getAccuracy() + "\r\n"
//                    + "getAltitude:" + mlocal.getAltitude() + "\r\n"
//                    + "getBearing:" + mlocal.getBearing() + "\r\n"
//                    + "getElapsedRealtimeNanos:" + mlocal.getElapsedRealtimeNanos() + "\r\n"
//                    + "getLatitude:" + mlocal.getLatitude() + "\r\n"
//                    + "getLongitude:" + mlocal.getLongitude() + "\r\n"
//                    + "getProvider:" + mlocal.getProvider() + "\r\n"
//                    + "getSpeed:" + mlocal.getSpeed() + "\r\n"
//                    + "getTime:" + mlocal.getTime() + "\r\n";
//            Log.i("Show", strResult);
//            Toast.makeText(mContext, strResult,Toast.LENGTH_SHORT).show();

            LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent_sv);
        }

        @Override
        public void onProviderDisabled(String arg0) {
        }

        @Override
        public void onProviderEnabled(String arg0) {
        }

        @Override
        public void onStatusChanged(String provider, int event, Bundle extras) {

        }
    };

    public GNSSContainer(Context context, Rinex rinex) {
        mContext = context;
        mRinex = rinex;

        if(ContextCompat.checkSelfPermission(context,
                android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, mLocationListener);
                //            mLocationManager.addNmeaListener(mNmeaListener);
                mLocationManager.registerGnssStatusCallback(mGnssStatusListener);
                mLocationManager.registerGnssMeasurementsCallback(gnssMeasurementEventListener);
            }
        }

        //register the raw data update signal
        intent = new Intent("com.Gnss.CUSTOM_INTENT");
        intent.setComponent(new ComponentName("com.example.geologger", "com.example.geologger.rawReceiver"));
        //register the SV data update signal
        intent_sv = new Intent("com.Gnss.CUSTOM_INTENT_SV");
        intent_sv.setComponent(new ComponentName("com.example.geologger", "com.example.geologger.svReceiver"));
    }

    private GnssStatus.Callback mGnssStatusListener =
            new GnssStatus.Callback() {
                @Override
                public void onSatelliteStatusChanged(GnssStatus status) {
                    //obtain the satellite numbers
                    satellite_cnt = status.getSatelliteCount();

                    for (int i = 0;i < satellite_cnt;i++) {
                        Constellation[i] = status.getConstellationType(i);
                        PRN[i] = status.getSvid(i);
                        Azimuth[i] = status.getAzimuthDegrees(i);
                        Altitude[i] = status.getElevationDegrees(i);
                        SNR[i] = status.getCn0DbHz(i);

                        switch (Constellation[i]) {
                            case CONSTELLATION_BEIDOU: PRN[i] = PRN[i] + 200; break;
                            case CONSTELLATION_GAL: PRN[i] = PRN[i] + 300; break;
                            case CONSTELLATION_GLONASS: PRN[i] = PRN[i] + 65; break;
                            default: break;
                        }
                    }
                }
            };

    //implement interface by anonymous internal classes
    private GnssMeasurementsEvent.Callback gnssMeasurementEventListener =
            new GnssMeasurementsEvent.Callback() {
                @Override
                public void onGnssMeasurementsReceived(GnssMeasurementsEvent eventArgs) {
                    super.onGnssMeasurementsReceived(eventArgs);
                    mBuilder = new StringBuilder("GNSS Raw Data：\n\n");

                    text.clear();
                    text.add("Svid");
                    text.add("CodeType");
                    text.add("SNR");
                    text.add("C1");
                    text.add("L1");

                    toStringClock(eventArgs.getClock());
                    mBuilder.append(displayGnssClock(gnss_values));//写入gnss时钟的数据
                    mBuilder.append("\n");

                    //the location for offer the time information
//                    mBuilder.append("sv_id  CodeType  SNR        C1                        L1                    \n");
//                    mBuilder.append("--------------------------------------------------------------------------\n");
                    int i = 0;
                    raw_values_list = new String[80][25];
                    //raw_values_list = null;

                    //each epoch's information
                    for (GnssMeasurement measurement : eventArgs.getMeasurements()) {
                        toStringMeasurement(measurement);

                        //raw_values_list
                        if (raw_values != null) {
                            //raw_values_list[i] = raw_values;
                            System.arraycopy(raw_values, 0, raw_values_list[i], 0, 25);
                            i++;
                        }

                        //mBuilder.append(displayGnssMeasurement(raw_values));//写入gnss测量数据
                        if (!displayGnssRinex(gnss_values, raw_values)) {
                            continue;
                        } else {
                            // display to the textview
                            addArrayList(text, rinex_values[SVID], rinex_values[CODETYPE], rinex_values[S_1], rinex_values[C_1], rinex_values[L_1]);
                        }
                    }

                    LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                }

                @Override
                public void onStatusChanged(int status) {
                    super.onStatusChanged(status);
                }
            };

    private void toStringClock(GnssClock gnssClock){
        //如果闰秒存在则显示闰秒
        if (gnssClock.hasLeapSecond()) {
            gnss_values[LeapSecond] = String.valueOf(gnssClock.getLeapSecond());
        } else {
            gnss_values[LeapSecond] = "NULL";
        }

        //获取以毫秒为单位的GNSS接收器内部硬件时钟值
        gnss_values[TimeNanos] = String.valueOf(gnssClock.getTimeNanos());

        //获取硬件时钟的误差估计（不确定度）
        if (gnssClock.hasTimeUncertaintyNanos()) {
            gnss_values[TimeUncertaintyNanos] = String.valueOf(gnssClock.getTimeUncertaintyNanos());
        } else {
            gnss_values[TimeUncertaintyNanos] = "NULL";
        }

        //如果存在接收机本地时钟总偏差，则显示
        if (gnssClock.hasFullBiasNanos()) {
            gnss_values[FullBiasNanos] = String.valueOf(gnssClock.getFullBiasNanos());
        } else {
            gnss_values[FullBiasNanos] = "NULL";
        }

        //亚纳秒偏差
        if (gnssClock.hasBiasNanos()) {
            gnss_values[BiasNanos] = String.valueOf(gnssClock.getBiasNanos());
        }

        //FullBiasNanos和BiasNanos的误差估计
        if (gnssClock.hasBiasUncertaintyNanos()) {
            gnss_values[BiasUncertaintyNanos] = String.valueOf(gnssClock.getBiasUncertaintyNanos());
        }
        /*
         * 注意：以上五个数据用于计算GPS时钟
         * 具体计算方法为：local estimate of GPS time = TimeNanos - (FullBiasNanos + BiasNanos)
         *     世界标准时：UtcTimeNanos = TimeNanos - (FullBiasNanos + BiasNanos) - LeapSecond * 1,000,000,000
         */
        //以每秒纳秒为单位获取时钟的漂移
        if (gnssClock.hasDriftNanosPerSecond()) {
            gnss_values[DriftNanosPerSecond] = String.valueOf(gnssClock.getDriftNanosPerSecond());
        }
        //时钟偏差的估计
        if (gnssClock.hasDriftUncertaintyNanosPerSecond()) {
            gnss_values[DriftUncertaintyNanosPerSecond] = String.valueOf(gnssClock.getDriftUncertaintyNanosPerSecond());
        }
        //获取硬件时钟不连续的计数,即：每当gnssclock中断时，该值+1
        gnss_values[HardwareClockDiscontinuityCount] = String.valueOf(gnssClock.getHardwareClockDiscontinuityCount());
    }

    private void toStringMeasurement(GnssMeasurement measurement){
        //获取卫星ID
        /*
         * 取决于卫星类型
         * GPS：1-32
         * SBAS：120-151、183-192
         * GLONASS：OSN或FCN + 100之一
         * 1-24作为轨道槽号（OSN）（首选，如果知道）
         * 93-106作为频道号（FCN）（-7至+6）加100。即将-7的FCN编码为93，0编码为100，+ 6编码为106
         * QZSS：193-200
         * 伽利略：1-36
         * 北斗：1-37
         */
        raw_values[Svid] = String.valueOf(measurement.getSvid());

        //获取卫星类型
        /*
         *  1:CONSTELLATION_GPS 使用GPS定位
         *  2:CONSTELLATION_SBAS 使用SBAS定位
         *  3：CONSTELLATION_GLONASS 使用格洛纳斯定位
         *  4：CONSTELLATION_QZSS 使用QZSS定位
         *  5：CONSTELLATION_BEIDOU 使用北斗定位 （^-^）!
         *  6：CONSTELLATION_GALILEO 使用伽利略定位
         *  7：CONSTELLATION_IRNSS 使用印度区域卫星定位
         */
        raw_values[ConstellationType] = String.valueOf(measurement.getConstellationType());

        //Gets the GNSS measurement's code type.
        raw_values[CodeType] = measurement.getCodeType();

        //获取进行测量的时间偏移量（以纳秒为单位）
        raw_values[TimeOffsetNanos] = String.valueOf(measurement.getTimeOffsetNanos());

        //获取每个卫星的同步状态
        //具体数值含义请查表
        raw_values[State] = String.valueOf(measurement.getState());

        raw_values[ReceivedSvTimeNanos] = String.valueOf(measurement.getReceivedSvTimeNanos());
        raw_values[ReceivedSvTimeUncertaintyNanos] = String.valueOf(measurement.getReceivedSvTimeUncertaintyNanos());
        raw_values[Cn0DbHz] = String.valueOf(measurement.getCn0DbHz());

        //获取时间戳的伪距速率，以m/s为单位
        raw_values[PseudorangeRateMetersPerSecond] = String.valueOf(measurement.getPseudorangeRateMetersPerSecond());

        //获取伪距的速率不确定性（1-Sigma），以m/s为单位
        raw_values[PseudorangeRateUncertaintyMetersPerSeconds] = String.valueOf(measurement.getPseudorangeRateUncertaintyMetersPerSecond());
        //
        if (measurement.getAccumulatedDeltaRangeState() != 0) {
            // 获取“累积增量范围”状态
            // 返回：MULTIPATH_INDICATOR_UNKNOWN（指示器不可用）=0
            // notice 即：指示器可用时，收集数据
            raw_values[AccumulatedDeltaRangeState] = String.valueOf(measurement.getAccumulatedDeltaRangeState());
            //获取自上次重置通道以来的累积增量范围，以米为单位.
            //该值仅在上面的state值为“可用”时有效
            //notice 累积增量范围= -k * 载波相位（其中k为常数）
            raw_values[AccumulatedDeltaRangeMeters] = String.valueOf(measurement.getAccumulatedDeltaRangeMeters());
            //获取以米为单位的累积增量范围的不确定性（1-Sigma）
            raw_values[AccumulatedDeltaRangeUncertaintyMeters] = String.valueOf(measurement.getAccumulatedDeltaRangeUncertaintyMeters());
        } else {
            raw_values[AccumulatedDeltaRangeState] = "NULL";
            raw_values[AccumulatedDeltaRangeMeters] = "NULL";
            raw_values[AccumulatedDeltaRangeUncertaintyMeters] = "NULL";
        }

        if (measurement.hasCarrierFrequencyHz()) {
            //获取被跟踪信号的载波频率
            raw_values[CarrierFrequencyHz] = String.valueOf(measurement.getCarrierFrequencyHz());
        } else {
            raw_values[CarrierFrequencyHz] = "NULL";
        }

        /* hasCarrierCycles, hasCarrierPhase, hasCarrierPhaseUncertainty was deprecated in API 28.
        use getAccumulatedDeltaRangeState() instead */
        if (measurement.hasCarrierCycles()) {
            //卫星和接收器之间的完整载波周期数
            raw_values[CarrierCycles] = String.valueOf(measurement.getCarrierCycles());
        } else {
            raw_values[CarrierCycles] = "NULL";
        }
        if (measurement.hasCarrierPhase()) {
            //获取接收器检测到的RF相位
            raw_values[CarrierPhase] = String.valueOf(measurement.getCarrierPhase());
        } else {
            raw_values[CarrierPhase] = "NULL";
        }
        if (measurement.hasCarrierPhaseUncertainty()) {
            //误差估计
            raw_values[CarrierPhaseUncertainty] = String.valueOf(measurement.getCarrierPhaseUncertainty());
        } else {
            raw_values[CarrierPhaseUncertainty] = "NULL";
        }
        //获取一个值，该值指示事件的“多路径”状态,返回0或1或2
        //MULTIPATH_INDICATOR_DETECTED = 1 测量显示有“多路径效应”迹象
        // MULTIPATH_INDICATOR_NOT_DETECTED = 2 测量结果显示没有“多路径效应”迹象
        raw_values[MultipathIndicator] = String.valueOf(measurement.getMultipathIndicator());

        if (measurement.hasSnrInDb()) {
            //获取信噪比（SNR），以dB为单位
            raw_values[SnrInDb] = String.valueOf(measurement.getSnrInDb());
        } else {
            raw_values[SnrInDb] = "NULL";
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            if (measurement.hasAutomaticGainControlLevelDb()) {
                //获取以dB为单位的自动增益控制级别
                raw_values[AgcDb] = String.valueOf(measurement.getAutomaticGainControlLevelDb());
            } else {
                raw_values[AgcDb] = "NULL";
            }
            if (measurement.hasCarrierFrequencyHz()) {
                raw_values[CarrierFreqHz] = String.valueOf(measurement.getCarrierFrequencyHz());
            } else {
                raw_values[CarrierFreqHz] = "NULL";
            }
        }
    }

    private String displayGnssClock(String[] gnss_values){
        //将GPS接收器时钟的值转换为字符串
        final String format = "   %-4s = %s\n";//定义数据显示格式，“%-4”表示左对齐、不足四位补足四位
        StringBuilder builder=new StringBuilder("GNSS时钟:\n");

        builder.append(String.format(format, "跳秒（LeapSecond）", gnss_values[LeapSecond]));
        builder.append(String.format(format, "硬件时钟（TimeNanos）", gnss_values[TimeNanos]));
        builder.append(String.format(format, "时钟误差估计（TimeUncertaintyNanos）", gnss_values[TimeUncertaintyNanos]));
        builder.append(String.format(format, "总时钟偏差（FullBiasNanos）", gnss_values[FullBiasNanos]));
        builder.append(String.format(format, "亚偏差（BiasNanos）", gnss_values[BiasNanos]));
        builder.append(String.format(format, "时钟偏差估计（BiasUncertaintyNanos）", gnss_values[BiasUncertaintyNanos]));
        builder.append(String.format(format, "时钟漂移（DriftNanosPerSecond）", gnss_values[DriftNanosPerSecond]));
        builder.append(String.format(format, "时钟漂移估计（DriftUncertaintyNanosPerSecond）", gnss_values[DriftUncertaintyNanosPerSecond]));
        builder.append(String.format(format, "中断计数（HardwareClockDiscontinuityCount）", gnss_values[HardwareClockDiscontinuityCount]));

        return builder.toString();
    }

    private String displayGnssMeasurement(String[] raw_values){
        //将GNSS测量结果转换为字符串
        //定义显示格式
        final String format = "   %-4s = %s\n";
        StringBuilder builder = new StringBuilder("Satellite Data:\n");

        builder.append(String.format(format, "卫星ID", raw_values[Svid]));
        builder.append(String.format(format, "卫星类型", raw_values[ConstellationType]));
        builder.append(String.format(format, "测量时间偏移量", raw_values[TimeOffsetNanos]));
        builder.append(String.format(format, "同步状态", raw_values[State]));
        builder.append(String.format(format, "ReceivedSvTimeNanos", raw_values[ReceivedSvTimeNanos]));
        builder.append(String.format(format, "ReceivedSvTimeUncertaintyNanos", raw_values[ReceivedSvTimeUncertaintyNanos]));
        builder.append(String.format(format, "Cn0DbHz", raw_values[Cn0DbHz]));
        builder.append(String.format(format, "伪距速率", raw_values[PseudorangeRateMetersPerSecond]));
        builder.append(String.format(format, "伪距速率不确定度", raw_values[PseudorangeRateUncertaintyMetersPerSeconds]));
        builder.append(String.format(format, "累积增量范围状态", raw_values[AccumulatedDeltaRangeState]));
        builder.append(String.format(format, "累积增量范围", raw_values[AccumulatedDeltaRangeMeters]));
        builder.append(String.format(format, "累积增量范围不确定度", raw_values[AccumulatedDeltaRangeUncertaintyMeters]));
        builder.append(String.format(format, "信号载波频率", raw_values[CarrierFrequencyHz]));
        builder.append(String.format(format, "载波周期数", raw_values[CarrierCycles]));
        builder.append(String.format(format, "RF相位", raw_values[CarrierPhase]));
        builder.append(String.format(format, "RF相位不确定度", raw_values[CarrierPhaseUncertainty]));
        builder.append(String.format(format, "多路经效应指示器", raw_values[MultipathIndicator]));
        builder.append(String.format(format, "信噪比", raw_values[SnrInDb]));
        builder.append(String.format(format, "自动增益控制级别", raw_values[AgcDb]));
        builder.append(String.format(format, "载波频率", raw_values[CarrierFreqHz]));

        return builder.toString();
    }

    public void RegisterMeasurements(){
        @SuppressLint("MissingPermission")
        boolean is_register_success=
                mLocationManager.registerGnssMeasurementsCallback(gnssMeasurementEventListener);
    }

    public void unRegisterMeasurements(){
        mLocationManager.unregisterGnssMeasurementsCallback(gnssMeasurementEventListener);
    }

    public StringBuilder getBuilder() {
        return mBuilder;
    }

    public String[] getGnssClock() {
        return gnss_values;
    }

    public List getGnssValues() {
        return text;
    }

    public String[][] getGnssValuesList() {
        return raw_values_list;
    }

    public int getGnssSatelliteCnt() {
        return satellite_cnt;
    }

    public int[] getGnssConstellation() {
        return Constellation;
    }

    public int[] getGnssPRN() {
        return PRN;
    }

    public double[] getGnssAzimuth() {
        return Azimuth;
    }

    public double[] getGnssAltitude() {
        return Altitude;
    }

    public float[] getGnssSNR() {
        return SNR;
    }

    private void addArrayList(List item, String svid, String codeType, String snr, String C1, String L1) {
        String[] str_c1;
        String[] str_l1;

        double s1 = Double.valueOf(snr);
        str_c1 = C1.split("\\.");
        str_l1 = L1.split("\\.");

        item.add(svid);
        item.add(codeType);
        item.add(String.format("%.1f", s1));
        item.add(str_c1[0]);
        item.add(str_l1[0]);
    }

    private boolean displayGnssRinex(String[] gnss_values, String[] raw_values) {
        rinex_values = mRinex.calculateRinexData(gnss_values, raw_values);

        //String notice =  rinex_values[SVID] + "," + rinex_values[CODETYPE] + "," + rinex_values[S_1] + "," + rinex_values[C_1] + "," + rinex_values[L_1];
        //Log.d("rinex_values", notice);

        if((rinex_values[S_1] != null) && (Double.parseDouble(rinex_values[S_1]) > 0)) {      // && (C1 >10e6) && (C1 <40e6)
            return true;
        } else {
            return false;
        }
    }

//    private String getFreType(int constellation_type, String code_type) {
//        String fre_code_type = "";
//
//        switch (code_type) {
//            case "A":
//                switch (constellation_type) {
//                    case CONSTELLATION_GAL:
//
//                        break;
//                    case CONSTELLATION_GLONASS:
//                        prn = 'R' + String.valueOf(sv_id);
//                        L1 = Double.parseDouble(raw_values[AccumulatedDeltaRangeMeters]) / GLN_L1_WAVELENGTH;
//                        break;
//                    case CONSTELLATION_BEIDOU:
//                        prn = 'B' + String.valueOf(sv_id);
//                        L1 = Double.parseDouble(raw_values[AccumulatedDeltaRangeMeters]) / BDS_L1_WAVELENGTH;
//                        break;
//                    case CONSTELLATION_QZSS:
//                        prn = 'Q' + String.valueOf(sv_id);
//                        L1 = Double.parseDouble(raw_values[AccumulatedDeltaRangeMeters]) / QZS_L1_WAVELENGTH;
//                        break;
//                    case CONSTELLATION_SBAS:
//                        prn = 'S' + String.valueOf(sv_id);
//                        L1 = Double.parseDouble(raw_values[AccumulatedDeltaRangeMeters]) / QZS_L1_WAVELENGTH;
//                        break;
//                    case CONSTELLATION_GAL:
//                        prn = 'E' + String.valueOf(sv_id);
//                        L1 = Double.parseDouble(raw_values[AccumulatedDeltaRangeMeters]) / GAL_L1_WAVELENGTH;
//                        break;
//                    case CONSTELLATION_UNKNOWN:
//                        prn = ' ' + String.valueOf(sv_id);
//                        L1 = 0.0;
//                    default: break;
//                }
//                break;
//            default: break;
//        }
//
//        return fre_code_type;
//    }
}