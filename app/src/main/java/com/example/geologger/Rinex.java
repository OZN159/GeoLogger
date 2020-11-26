package com.example.geologger;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class Rinex {
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

    //Define constants
    private static final double SPEED_OF_LIGHT = 299792458.0;
    private static final double GPS_WEEKSECS = 604800;
    private static final double NS_TO_S = 1.0e-9;
    private static final double S_TO_NS = 1.0e9;
    private static final double NS_TO_M = NS_TO_S * SPEED_OF_LIGHT;
    //1
    private static final double GPS_L1_FREQ = 154.0 * 10.23e6;
    private static final double GPS_L1_WAVELENGTH = SPEED_OF_LIGHT / GPS_L1_FREQ;
    private static final double GPS_L2_FREQ = 1227.6e6;
    private static final double GPS_L2_WAVELENGTH = SPEED_OF_LIGHT / GPS_L2_FREQ;
    private static final double GPS_L5_FREQ = 1176.45e6;
    private static final double GPS_L5_WAVELENGTH = SPEED_OF_LIGHT / GPS_L5_FREQ;

    //2
    private static final double BDS_L1_FREQ = 1561.098e6;
    private static final double BDS_L1_WAVELENGTH = SPEED_OF_LIGHT / BDS_L1_FREQ;
    private static final double BDS_L2_FREQ = 1207.14e6;
    private static final double BDS_L2_WAVELENGTH = SPEED_OF_LIGHT / BDS_L2_FREQ;
    private static final double BDS_L3_FREQ = 1268.52e6;
    private static final double BDS_L3_WAVELENGTH = (SPEED_OF_LIGHT / BDS_L3_FREQ);

    //3
    private static final double GAL_E1_FREQ = 1575.42e6;
    private static final double GAL_E1_WAVELENGTH = SPEED_OF_LIGHT / GAL_E1_FREQ;
    private static final double GAL_E5A_FREQ = 1176.45e6;
    private static final double GAL_E5A_WAVELENGTH = SPEED_OF_LIGHT / GAL_E5A_FREQ;
    private static final double GAL_E5B_FREQ = 1207.14e6;
    private static final double GAL_E5B_WAVELENGTH = (SPEED_OF_LIGHT / GAL_E5B_FREQ);
    private static final double GAL_E6_FREQ = 1278.75e6;
    private static final double GAL_E6_WAVELENGTH = (SPEED_OF_LIGHT / GAL_E6_FREQ);

    //4
    private static final double GLN_L1_FREQ = 1598.065e6;
    private static final double GLN_L1_WAVELENGTH = SPEED_OF_LIGHT / GLN_L1_FREQ;
    private static final double GLN_L2_FREQ = 1242.9375e6;
    private static final double GLN_L2_WAVELENGTH = SPEED_OF_LIGHT / GLN_L2_FREQ;

    private static final double GLN_L1_FREQ_BASE = 1602000000.0;
    private static final double GLN_L2_FREQ_BASE = 1246000000.0;
    private static final double GLN_L1_FREQ_STEP = 562500.0;
    private static final double GLN_L2_FREQ_STEP = 437500.0;

    //5
    private static final double QZS_L1_FREQ = 1575.42e6;
    private static final double QZS_L1_WAVELENGTH = SPEED_OF_LIGHT / QZS_L1_FREQ;
    private static final double QZS_L2_FREQ = 1227.6e6;
    private static final double QZS_L2_WAVELENGTH = SPEED_OF_LIGHT / QZS_L2_FREQ;

    //Constant
    private static final double NUM_NANOSEC_WEEK = 604800e9;
    private static final double NUM_NANOSEC_DAY = 86400e9;
    private static final double NUM_NANOSEC_100MILL = 1e8;
    private static final int MAX_VALUE = 8388608;
    private static final int[] GLN_LIST = {1, -4, 5, 6, 1, -4, 5, 6, -2, -7, 0, -1, -2, -7, 0, -1, -6, -3, 3, 2, 4, -3, 3, 2};

    //Rinex Header
    private static final String VERSION = "3.03";
    private static final String TYPE = "OBSERVATION DATA";
    private static final String SATSYS = "M: Mixed";
    private static final String PGM = "ANDROID_RINEX";
    private static final String RUNBY = "UNKN";
    private static final String MARKERNAME = "UNKN";
    private static final String MARKERTYPE = "UNKN";
    private static final String OBSERVER = "UNKN";
    private static final String AGENCY = "UNKN";
    private static final String RECEIVERNUM = "UNKN";
    private static final String RECEIVERTYPE = "UNKN";
    private static final String RECEIVERVERSION = "AndroidOS >7.0";
    private static final String ANTENNANUM = "UNKN";
    private static final String ANTENNATYPE = "internal";

    //file storage path
    private static final String FILEPATH = "/sdcard/GeoCaster/";
    private static final String FILENAME = "Rinex3.20o";

    //variable
    private Context mContext;
    private String[] gnss_values = new String[10];
    private String[][] raw_values_list = new String[80][25];

    private GregorianCalendar mCalendar = new GregorianCalendar();
    private Date mDate = new Date();
    private SimpleDateFormat calendar_format = new SimpleDateFormat("> yyyy MM dd HH mm ss.000000  0 ");

    public Rinex(Context context) {
        mContext = context;
    }

    public void getRawData(String[] gnss_clock, String[][] list) {
        gnss_values = gnss_clock;
        raw_values_list = list;
    }

    public void setRinexHeader(FileOperation mFileOperation, RandomAccessFile mFile) {
        String rinex_header;
        String date_str;

        // Version line
        rinex_header = String.format("%-9s           %-20s%-20sRINEX VERSION / TYPE\n", VERSION, TYPE, SATSYS);

        // Pgm line
        Date date_time = new Date();
        SimpleDateFormat date_format = new SimpleDateFormat("yyyyMMdd HHmmss");
        date_str = date_format.format(date_time);

        rinex_header += String.format("%-20s%-20s%-20sPGM / RUN BY / DATE\n", PGM, RUNBY, date_str);
        // Additional comment line for the program
        rinex_header += String.format("%-60sCOMMENT\n", "Generated by Hi-target android_rinex program");
        rinex_header += String.format("%-60sCOMMENT\n", "Contact us at Nander@hitargetgroup.com.cn");
        // Marker name
        rinex_header += String.format("%-60sMARKER NAME\n", MARKERNAME);
        // Marker type
        rinex_header += String.format("%-60sMARKER TYPE\n", MARKERTYPE);
        // Observer and agency
        rinex_header += String.format("%-20s%-40sOBSERVER / AGENCY\n", OBSERVER, AGENCY);
        // Receiver line
        rinex_header += String.format("%-20s%-20s%-20sREC # / TYPE / VERS\n", RECEIVERNUM, RECEIVERTYPE, RECEIVERVERSION);
        // Antenna type
        rinex_header += String.format("%-20s%-40sANT # / TYPE\n", ANTENNANUM, ANTENNATYPE);
        // Approximate position
        rinex_header += String.format("% 14f% 14f% 14f                  APPROX POSITION XYZ\n", 0.0, 0.0, 0.0);
        // Antenna offset
        rinex_header += String.format("% 14f% 14f% 14f                  ANTENNA: DELTA H/E/N\n", 0.0, 0.0, 0.0);

        // Observables
        rinex_header += "G    8 C1C L1C D1C S1C C5Q L5Q D5Q S5Q                      SYS / # / OBS TYPES\n" +
                        "R    4 C1C L1C D1C S1C                                      SYS / # / OBS TYPES\n" +
                        "E   12 C1C L1C D1C S1C C5I L5I D5I S5I C7I L7I D7I S7I      SYS / # / OBS TYPES\n" +
                        "C    4 C2I L2I D2I S2I                                      SYS / # / OBS TYPES\n" +
                        "J    8 C1C L1C D1C S1C C5Q L5Q D5Q S5Q                      SYS / # / OBS TYPES\n";

        // End header
        //rinex_header += first_epoch.strftime("  %Y    %m    %d    %H    %M    %S.%f                 TIME OF FIRST OBS\n")
        rinex_header += " 24 R01  1 R02 -4 R03  5 R04  6 R05  1 R06 -4 R07  5 R08  6 GLONASS SLOT / FRQ #\n" +
                        "    R09 -2 R10 -5 R11  0 R12 -1 R13 -2 R14 -7 R15  0 R16 -1 GLONASS SLOT / FRQ #\n" +
                        "    R17  4 R18 -3 R19  3 R20  2 R21  4 R22 -3 R23  3 R24  2 GLONASS SLOT / FRQ #\n" +
                        "G L1C                                                       SYS / PHASE SHIFT\n" +
                        "G L5Q -0.25000                                              SYS / PHASE SHIFT\n" +
                        "R L1C                                                       SYS / PHASE SHIFT\n" +
                        "E L1B                                                       SYS / PHASE SHIFT\n" +
                        "E L1C +0.50000                                              SYS / PHASE SHIFT\n" +
                        "E L5Q -0.25000                                              SYS / PHASE SHIFT\n" +
                        "C L2I                                                       SYS / PHASE SHIFT\n" +
                        "J L1C                                                       SYS / PHASE SHIFT\n" +
                        "J L5Q -0.25000                                              SYS / PHASE SHIFT\n" +
                        " C1C    0.000 C1P    0.000 C2C    0.000 C2P    0.000        GLONASS COD/PHS/BIS\n" +
                        "                                                            END OF HEADER\n";

        mFileOperation.writeToFileOverwrite(mFile, rinex_header.getBytes(), rinex_header.length());
        //Toast.makeText(mContext, rinex_header, Toast.LENGTH_SHORT).show();
    }

    public void setRinexEpoch(FileOperation mFileOperation, RandomAccessFile mFile) {
        final int EXIST_L1 = 1;
        final int EXIST_L2 = 2;
        final int NOT_EXIST_L1 = 3;
        final int NOT_EXIST_L2 = 4;

        String epoch_str;
        String[] rinex_values = new String[10];
        int[] state = new int[2];
        //Rinex data format: c1, s1, l1, d1, c2, s2, l2, d2
        String[][] epoch_value = new String[80][9];

        epoch_str = getFirstEpochTime();

        //calculate each Epoch
        for(int i = 0; i < raw_values_list.length; i++) {
            if(raw_values_list[i] == null) {
                continue;
            }
            rinex_values = calculateRinexData(gnss_values, raw_values_list[i]);
            if (rinex_values[STATUS].contains("False")) {
                continue;
            }

            state = isRinexValuesState(epoch_value, rinex_values);
            if (state[0] == EXIST_L1) {
                epoch_value[state[1]][C_1] = rinex_values[C_1];
                epoch_value[state[1]][S_1] = rinex_values[S_1];
                epoch_value[state[1]][L_1] = rinex_values[L_1];
                epoch_value[state[1]][D_1] = rinex_values[D_1];
            } else if (state[0] == EXIST_L2) {
                epoch_value[state[1]][C_2] = rinex_values[C_1];
                epoch_value[state[1]][S_2] = rinex_values[S_1];
                epoch_value[state[1]][L_2] = rinex_values[L_1];
                epoch_value[state[1]][D_2] = rinex_values[D_1];
            } else if (state[0] == NOT_EXIST_L1) {
                epoch_value[i][SVID] = rinex_values[SVID];
                epoch_value[i][C_1] = rinex_values[C_1];
                epoch_value[i][S_1] = rinex_values[S_1];
                epoch_value[i][L_1] = rinex_values[L_1];
                epoch_value[i][D_1] = rinex_values[D_1];
                epoch_value[i][C_2] = " ";
                epoch_value[i][S_2] = " ";
                epoch_value[i][L_2] = " ";
                epoch_value[i][D_2] = " ";
            } else if (state[0] == NOT_EXIST_L2) {
                epoch_value[i][SVID] = rinex_values[SVID];
                epoch_value[i][C_1] = " ";
                epoch_value[i][S_1] = " ";
                epoch_value[i][L_1] = " ";
                epoch_value[i][D_1] = " ";
                epoch_value[i][C_2] = rinex_values[C_1];
                epoch_value[i][S_2] = rinex_values[S_1];
                epoch_value[i][L_2] = rinex_values[L_1];
                epoch_value[i][D_2] = rinex_values[D_1];
            } else {
                //ignore, nothing need to do.
            }
        }

        //write into the Rinex file
        writeRinexEpochToFile(mFileOperation, mFile, epoch_str, epoch_value);


    }

    public String[] calculateRinexData(String[] gnss_values, String[] raw_values) {
        String[] rinex_values = new String[10];

        double full_bias_nanos = (gnss_values[FullBiasNanos] != null)? Double.parseDouble(gnss_values[FullBiasNanos]):0.0;
        double Time_Nanos = (gnss_values[TimeNanos] != null)? Double.parseDouble(gnss_values[TimeNanos]):0.0;
        double Bias_Nanos = (gnss_values[BiasNanos] != null)? Double.parseDouble(gnss_values[BiasNanos]):0.0;
        double leap_sec = (gnss_values[LeapSecond] != null)? Double.parseDouble(gnss_values[LeapSecond]):0.0;

        double Time_Offset_Nanos = (raw_values[TimeOffsetNanos] != null)? Double.parseDouble(raw_values[TimeOffsetNanos]):0.0;
        double Received_Sv_Time_Nanos = (raw_values[ReceivedSvTimeNanos] != null)? Double.parseDouble(raw_values[ReceivedSvTimeNanos]):0.0;
        int constellation_Type = (raw_values[ConstellationType] != null)? Integer.parseInt(raw_values[ConstellationType]):1;
        double Carrier_FrequencyHz = (raw_values[CarrierFrequencyHz] != null)? Double.parseDouble(raw_values[CarrierFrequencyHz]):0.0;
        int sv_id = (raw_values[Svid] != null)? Integer.parseInt(raw_values[Svid]):0;
        double Accumulated_Delta_Range_M = (raw_values[AccumulatedDeltaRangeMeters] != null)? Double.parseDouble(raw_values[AccumulatedDeltaRangeMeters]):0.0;
        double Ps_Rate_MPerSecond = (raw_values[PseudorangeRateMetersPerSecond] != null)? Double.parseDouble(raw_values[PseudorangeRateMetersPerSecond]):0.0;


        // Compute the GPS week number as well as the time within the week of
        // the reception time (i.e. clock epoch)
        double gpsweek = Math.floor(-full_bias_nanos * NS_TO_S / GPS_WEEKSECS);
        double local_est_GPS_time = Time_Nanos - (full_bias_nanos + Bias_Nanos);
        double gpssow = local_est_GPS_time * NS_TO_S - gpsweek * GPS_WEEKSECS;

        //Fractional part of the integer seconds
        //frac = 0.0
        //if args.integerize:
        //frac = gpssow - int(gpssow+0.5)

        // Convert the epoch to Python's buiit-in datetime class
        // epoch = gpstime_to_epoch(gpsweek, gpssow);

        // pseudorange
        // 𝑤𝑒𝑒𝑘𝑁𝑢𝑚𝑏𝑒𝑟𝑁𝑎𝑛𝑜𝑠 is the number of nanoseconds that have occurred from the beginning of GPS time to the current WN.
        // 𝐷𝑎𝑦𝑁𝑢𝑚𝑏𝑒𝑟𝑁𝑎𝑛𝑜𝑠 is the number of nanoseconds that have occurred from the beginning of GPS time to the current day.
        // 𝑚𝑖𝑙𝑙𝑖𝑆𝑒𝑐𝑜𝑛𝑑𝑠𝑁𝑢𝑚𝑏𝑒𝑟𝑁𝑎𝑛𝑜𝑠 is the number of milliseconds that have occurred from the beginning of the GPS time.
        double week_number_nanos = Math.floor(-full_bias_nanos / NUM_NANOSEC_WEEK) * NUM_NANOSEC_WEEK;
        double day_number_nanos = Math.floor(-full_bias_nanos / NUM_NANOSEC_DAY) * NUM_NANOSEC_DAY;
        double millSec_number_nanos = Math.floor(-full_bias_nanos / NUM_NANOSEC_100MILL) * NUM_NANOSEC_100MILL;

        //tRx_GNSS = TimeNanos + TimeOffsetNanos - FullBiasNanos - BiasNanos;
        double tRx_GNSS = Time_Nanos + Time_Offset_Nanos - full_bias_nanos - Bias_Nanos;
        double tRx_nanos = 0.0;

        if (constellation_Type == CONSTELLATION_GPS) {
            tRx_nanos = tRx_GNSS - week_number_nanos;
        } else if (constellation_Type == CONSTELLATION_GLONASS){
            tRx_nanos = tRx_GNSS - day_number_nanos + (3*3600 - leap_sec) * S_TO_NS;
        } else if (constellation_Type == CONSTELLATION_GAL){
            if (Carrier_FrequencyHz > 1500e6) {
                tRx_nanos = tRx_GNSS - millSec_number_nanos;
            } else {
                tRx_nanos = tRx_GNSS - week_number_nanos;
            }
        } else if (constellation_Type == CONSTELLATION_BEIDOU){
            tRx_nanos = tRx_GNSS - week_number_nanos - 14 * S_TO_NS;
        } else {
            tRx_nanos = tRx_GNSS - week_number_nanos;
        }

        // Compute the reception and transmission times
        double tRxSeconds = tRx_nanos * NS_TO_S;
        double tTxSeconds = Received_Sv_Time_Nanos * NS_TO_S;

        // Compute the travel time, which will be eventually the pseudorange
        double tau = tRxSeconds - tTxSeconds;

        // Check the week rollover, for measurements near the week transition
        if (tau < 0) {
            tau += GPS_WEEKSECS;
        }

        // Compute the range as the difference between the received time and
        // the transmitted time
        double C1 = tau * SPEED_OF_LIGHT;
        double SNR;
        String prn = "", fre_code_type="";
        double L1 = 0.0, D1 = 0.0;

        switch (constellation_Type) {
            case CONSTELLATION_GPS:
                //prn = 'G' + String.valueOf(sv_id);
                prn = String.format("G%02d", sv_id);
                //Fre_code_type
                if (Carrier_FrequencyHz > 1500e6) {
                    fre_code_type = "L1";
                    L1 = Accumulated_Delta_Range_M / GPS_L1_WAVELENGTH;
                    //corrected adr (cycles)
                    L1 = get_correction_adr(C1, L1, GPS_L1_WAVELENGTH);
                    D1 = -Ps_Rate_MPerSecond / GPS_L1_WAVELENGTH;
                } else if(Carrier_FrequencyHz > 1200e6) {
                    fre_code_type = "L2";
                    L1 = Accumulated_Delta_Range_M / GPS_L2_WAVELENGTH;
                    L1 = get_correction_adr(C1, L1, GPS_L2_WAVELENGTH);
                    D1 = -Ps_Rate_MPerSecond / GPS_L2_WAVELENGTH;
                } else if (Carrier_FrequencyHz > 1100e6) {
                    fre_code_type = "L5";
                    L1 = Accumulated_Delta_Range_M / GPS_L5_WAVELENGTH;
                    L1 = get_correction_adr(C1, L1, GPS_L5_WAVELENGTH);
                    D1 = -Ps_Rate_MPerSecond / GPS_L5_WAVELENGTH;
                }

                break;
            case CONSTELLATION_GLONASS:
                if (sv_id >= 93) {
                    //Receiver is giving Frequency slot number (FSN) instead of Orbital Slot Number (OSN).
                    // Since I have no means of converting between them,I am going to skip this measurement
                    break;
                } else {
                    //prn = 'R' + String.valueOf(sv_id);
                    prn = String.format("R%02d", sv_id);
                }
                //Fre_code_type
                if (Carrier_FrequencyHz > 1500e6) {
                    fre_code_type = "L1";
                    //adr (cycles)
                    L1 = Accumulated_Delta_Range_M / get_glo_L1(GLN_LIST[sv_id-1]);
                    //corrected adr (cycles)
                    L1 = get_correction_adr(C1, L1, get_glo_L1(GLN_LIST[sv_id-1]));
                    D1 = -Ps_Rate_MPerSecond / get_glo_L1(GLN_LIST[sv_id-1]);
                } else if(Carrier_FrequencyHz > 1200e6) {
                    fre_code_type = "L2";
                    L1 = Accumulated_Delta_Range_M / get_glo_L2(GLN_LIST[sv_id-1]);
                    L1 = get_correction_adr(C1, L1, get_glo_L2(GLN_LIST[sv_id-1]));
                    D1 = -Ps_Rate_MPerSecond / get_glo_L2(GLN_LIST[sv_id-1]);
                }
                break;
            case CONSTELLATION_BEIDOU:
                //prn = 'C' + String.valueOf(sv_id);
                prn = String.format("C%02d", sv_id);

                //Fre_code_type
                if (Carrier_FrequencyHz > 1500e6) {
                    fre_code_type = "C1";
                    L1 = Accumulated_Delta_Range_M / BDS_L1_WAVELENGTH;
                    L1 = get_correction_adr(C1, L1, BDS_L1_WAVELENGTH);
                    D1 = -Ps_Rate_MPerSecond / BDS_L1_WAVELENGTH;
                } else if(Carrier_FrequencyHz > 1250e6) {
                    fre_code_type = "C3";
                    L1 = Accumulated_Delta_Range_M / BDS_L3_WAVELENGTH;
                    L1 = get_correction_adr(C1, L1, BDS_L3_WAVELENGTH);
                    D1 = -Ps_Rate_MPerSecond / BDS_L3_WAVELENGTH;
                } else if (Carrier_FrequencyHz > 1100e6) {
                    fre_code_type = "C2";
                    L1 = Accumulated_Delta_Range_M / BDS_L2_WAVELENGTH;
                    L1 = get_correction_adr(C1, L1, BDS_L2_WAVELENGTH);
                    D1 = -Ps_Rate_MPerSecond / BDS_L2_WAVELENGTH;
                }
                break;
            case CONSTELLATION_QZSS:
                //prn = 'Q' + String.valueOf(sv_id);
                prn = String.format("J%02d", (sv_id-192));

                L1 = Accumulated_Delta_Range_M / QZS_L1_WAVELENGTH;
                //Fre_code_type
                if (Carrier_FrequencyHz > 1500e6) {
                    fre_code_type = "L1";
                } else if(Carrier_FrequencyHz > 1200e6) {
                    fre_code_type = "L2";
                } else if (Carrier_FrequencyHz > 1100e6) {
                    fre_code_type = "L5";
                }
                break;
            case CONSTELLATION_SBAS:
                //prn = 'S' + String.valueOf(sv_id);
                prn = String.format("S%02d", (sv_id-100));

                L1 = Accumulated_Delta_Range_M / QZS_L1_WAVELENGTH;
                //Fre_code_type
                if (Carrier_FrequencyHz > 1500e6) {
                    fre_code_type = "L1";
                }
                break;
            case CONSTELLATION_GAL:
                //prn = 'E' + String.valueOf(sv_id);
                prn = String.format("E%02d", sv_id);

                //Fre_code_type
                if (Carrier_FrequencyHz > 1500e6) {
                    fre_code_type = "E1";
                    L1 = Accumulated_Delta_Range_M / GAL_E1_WAVELENGTH;
                    L1 = get_correction_adr(C1, L1, GAL_E1_WAVELENGTH);
                    D1 = -Ps_Rate_MPerSecond / GAL_E1_WAVELENGTH;
                } else if(Carrier_FrequencyHz > 1250e6) {
                    fre_code_type = "E6";
                    L1 = Accumulated_Delta_Range_M / GAL_E6_WAVELENGTH;
                    L1 = get_correction_adr(C1, L1, GAL_E6_WAVELENGTH);
                    D1 = -Ps_Rate_MPerSecond / GAL_E6_WAVELENGTH;
                } else if (Carrier_FrequencyHz > 1200e6) {
                    fre_code_type = "E5b";
                    L1 = Accumulated_Delta_Range_M / GAL_E5B_WAVELENGTH;
                    L1 = get_correction_adr(C1, L1, GAL_E5B_WAVELENGTH);
                    D1 = -Ps_Rate_MPerSecond / GAL_E5B_WAVELENGTH;
                } else if (Carrier_FrequencyHz > 1100e6) {
                    fre_code_type = "E5a";
                    L1 = Accumulated_Delta_Range_M / GAL_E5A_WAVELENGTH;
                    L1 = get_correction_adr(C1, L1, GAL_E5A_WAVELENGTH);
                    D1 = -Ps_Rate_MPerSecond / GAL_E5A_WAVELENGTH;
                }
                break;
            case CONSTELLATION_UNKNOWN:
                prn = ' ' + String.valueOf(sv_id);
                L1 = 0.0;
                D1 = 0.0;
            default: break;
        }

        //Rinex : SNR
        SNR = (raw_values[Cn0DbHz] != null)? Double.parseDouble(raw_values[Cn0DbHz]):0.0;

        if (C1 > 10e6 && C1 < 40e6) {
            rinex_values[SVID] = prn;
            rinex_values[L_1] = String.format("%14.3f", L1);
            rinex_values[C_1] = String.format("%12.3f", C1);
            rinex_values[S_1] = String.format("%14.3f", SNR);
            rinex_values[D_1] = String.format("%14.3f", D1);
            rinex_values[CODETYPE] = fre_code_type;
            rinex_values[STATUS] = "True";
        } else {
            rinex_values[STATUS] = "False";
        }

        return rinex_values;
    }

    public String getFirstEpochTime() {
        String calendar_str;
        double local_est_gps_time;

        Calendar gps_time = Calendar.getInstance();
        gps_time.set(1980, 0, 6);

        local_est_gps_time = Double.parseDouble(gnss_values[TimeNanos]) - (Double.parseDouble(gnss_values[FullBiasNanos]) + Double.parseDouble(gnss_values[BiasNanos]));

        mCalendar.set(1980, 0, 6, 0, 0, 0);
        mCalendar.add(Calendar.SECOND, (int)(local_est_gps_time * NS_TO_S));

        calendar_str = calendar_format.format(mCalendar.getTime());

        return calendar_str;

    }

    private int[] isRinexValuesState(String[][] epoch_value, String[] rinex_values) {
        int[] state = new int[2];

        for(int j = 0; j < epoch_value.length; j++) {
            if (rinex_values[SVID] != null && rinex_values[SVID].equals(epoch_value[j][SVID])) {
                if (rinex_values[CODETYPE] != null && (rinex_values[CODETYPE].equals("L1") || rinex_values[CODETYPE].equals("E1") ||
                        rinex_values[CODETYPE].equals("C1"))) {
                    state[0] = 1;
                    state[1] = j;
                    return state;
                } else {
                    state[0] = 2;
                    state[1] = j;
                    return state;
                }
            }
        }

        // the GO satellite is not exist, will ignore it.
        if (rinex_values[SVID].contains("G0")) {
            state[0] = 0;
            state[1] = 0;
            return state;
        }

        //maybe will skip some arrayList because the same satellite prn was found.
        if (rinex_values[CODETYPE] != null && (rinex_values[CODETYPE].equals("L1") || rinex_values[CODETYPE].equals("E1") ||
                rinex_values[CODETYPE].equals("C1"))) {
            state[0] = 3;
            state[1] = 0;
            return state;
        } else {
            state[0] = 4;
            state[1] = 0;
            return state;
        }
    }

    private double get_correction_adr(double psr, double adr_cycles, double wave_length) {
        double adr_rolls = (psr / wave_length + adr_cycles) / MAX_VALUE;
        if (adr_rolls <= 0) {
            adr_rolls = adr_rolls - 0.5;
        } else {
            adr_rolls = adr_rolls + 0.5;
        }

        return (-(adr_cycles - (MAX_VALUE * adr_rolls)));
    }

    private double get_glo_L1(int chel) {
        return SPEED_OF_LIGHT / (GLN_L1_FREQ_BASE + chel * GLN_L1_FREQ_STEP);
    }

    private double get_glo_L2(int chel) {
        return SPEED_OF_LIGHT / (GLN_L2_FREQ_BASE + chel * GLN_L2_FREQ_STEP);
    }

    /* ******************************************Rinex file storage****************************** */
    private boolean writeRinexEpochToFile(FileOperation mFileOperation, RandomAccessFile mFile, String epoch_date, String[][] values) {
        String epoch_str;
        int epoch_cnt = 0;

        // count the number of satellite
        for(int i = 0; i < values.length; i++) {
            if (values[i][SVID] != null && (values[i][SVID].contains("G") || values[i][SVID].contains("C") ||
                    values[i][SVID].contains("E") || values[i][SVID].contains("R") || values[i][SVID].contains("J"))) {
                epoch_cnt++;
            }
        }

        //epoch time
        epoch_str = epoch_date + epoch_cnt + "\n";
        //Log.d("epoch_str", epoch_str);


        for(int i = 0; i < values.length; i++) {
            if (values[i][SVID] != null && values[i][SVID].contains("G")) {
                epoch_str += String.format("%-5s%12s  %14s  %14s  %14s  %14s  %14s  %14s  %14s\n",
                        values[i][SVID], values[i][C_1], values[i][L_1], values[i][D_1], values[i][S_1],
                        values[i][C_2], values[i][L_2], values[i][D_2], values[i][S_2]);
            }
        }
        for(int i = 0; i < values.length; i++) {
            if (values[i][SVID] != null && values[i][SVID].contains("C")) {
                epoch_str += String.format("%-5s%12s  %14s  %14s  %14s  %14s  %14s  %14s  %14s\n",
                        values[i][SVID], values[i][C_1], values[i][L_1], values[i][D_1], values[i][S_1],
                        values[i][C_2], values[i][L_2], values[i][D_2], values[i][S_2]);
            }
        }
        for(int i = 0; i < values.length; i++) {
            if (values[i][SVID] != null && values[i][SVID].contains("E")) {
                epoch_str += String.format("%-5s%12s  %14s  %14s  %14s  %14s  %14s  %14s  %14s\n",
                        values[i][SVID], values[i][C_1], values[i][L_1], values[i][D_1], values[i][S_1],
                        values[i][C_2], values[i][L_2], values[i][D_2], values[i][S_2]);
            }
        }
        for(int i = 0; i < values.length; i++) {
            if (values[i][SVID] != null && values[i][SVID].contains("R")) {
                epoch_str += String.format("%-5s%12s  %14s  %14s  %14s  %14s  %14s  %14s  %14s\n",
                        values[i][SVID], values[i][C_1], values[i][L_1], values[i][D_1], values[i][S_1],
                        values[i][C_2], values[i][L_2], values[i][D_2], values[i][S_2]);
            }
        }
        for(int i = 0; i < values.length; i++) {
            if (values[i][SVID] != null && values[i][SVID].contains("J")) {
                epoch_str += String.format("%-5s%12s  %14s  %14s  %14s  %14s  %14s  %14s  %14s\n",
                        values[i][SVID], values[i][C_1], values[i][L_1], values[i][D_1], values[i][S_1],
                        values[i][C_2], values[i][L_2], values[i][D_2], values[i][S_2]);
            }
        }

        //write
        boolean status = mFileOperation.writeToFileAppend(mFile, epoch_str.getBytes(), epoch_str.length());
        //Log.d("epoch_str", epoch_str);

        return status;
    }
}



/* ******************************************** backup ********************************************* */
//    private String[] calculateRinexData_bak(String[] raw_values) {
//        String[] rinex_values = new String[10];
//        double tau, tRxSeconds, tTxSeconds, tRx_GNSS, fullbiasnanos, week_number_nanos, tRx_nanos, tTx_nanos, day_number_nanos, millSec_number_nanos;
//        double carrier_frequency, full_bias_nanos, time_nanos, time_offset_nanos, bias_nanos, leap_sec;
//
//        int sv_id = Integer.parseInt(raw_values[Svid]);
//        int constellation_type = Integer.parseInt(raw_values[ConstellationType]);
//        String prn = "", SNR = "", fre_code_type="";
//        double C1, L1 = 0.0;
//        final String format = "%-5s %-5s %-10s %-20.2f %-20.5f\n";
//
//        //Rinex : prn
//        carrier_frequency = Double.parseDouble(raw_values[CarrierFrequencyHz]);
//        switch (constellation_type) {
//            case CONSTELLATION_GPS:
//                prn = 'G' + String.valueOf(sv_id);
//                L1 = Double.parseDouble(raw_values[AccumulatedDeltaRangeMeters]) / GPS_L1_WAVELENGTH;
//                //Fre_code_type
//                if (carrier_frequency > 1500e6) {
//                    fre_code_type = "L1";
//                } else if(carrier_frequency > 1200e6) {
//                    fre_code_type = "L2";
//                } else if (carrier_frequency > 1100e6) {
//                    fre_code_type = "L5";
//                }
//                break;
//            case CONSTELLATION_GLONASS:
//                prn = 'R' + String.valueOf(sv_id);
//                L1 = Double.parseDouble(raw_values[AccumulatedDeltaRangeMeters]) / GLN_L1_WAVELENGTH;
//                //Fre_code_type
//                if (carrier_frequency > 1500e6) {
//                    fre_code_type = "L1";
//                } else if(carrier_frequency > 1200e6) {
//                    fre_code_type = "L2";
//                }
//                break;
//            case CONSTELLATION_BEIDOU:
//                prn = 'B' + String.valueOf(sv_id);
//                L1 = Double.parseDouble(raw_values[AccumulatedDeltaRangeMeters]) / BDS_L1_WAVELENGTH;
//                //Fre_code_type
//                if (carrier_frequency > 1500e6) {
//                    fre_code_type = "B1";
//                } else if(carrier_frequency > 1250e6) {
//                    fre_code_type = "B3";
//                } else if (carrier_frequency > 1100e6) {
//                    fre_code_type = "B2";
//                }
//                break;
//            case CONSTELLATION_QZSS:
//                prn = 'Q' + String.valueOf(sv_id);
//                L1 = Double.parseDouble(raw_values[AccumulatedDeltaRangeMeters]) / QZS_L1_WAVELENGTH;
//                //Fre_code_type
//                if (carrier_frequency > 1500e6) {
//                    fre_code_type = "L1";
//                } else if(carrier_frequency > 1200e6) {
//                    fre_code_type = "L2";
//                } else if (carrier_frequency > 1100e6) {
//                    fre_code_type = "L5";
//                }
//                break;
//            case CONSTELLATION_SBAS:
//                prn = 'S' + String.valueOf(sv_id);
//                L1 = Double.parseDouble(raw_values[AccumulatedDeltaRangeMeters]) / QZS_L1_WAVELENGTH;
//                //Fre_code_type
//                if (carrier_frequency > 1500e6) {
//                    fre_code_type = "L1";
//                }
//                break;
//            case CONSTELLATION_GAL:
//                prn = 'E' + String.valueOf(sv_id);
//                L1 = Double.parseDouble(raw_values[AccumulatedDeltaRangeMeters]) / GAL_E1_WAVELENGTH;
//                //Fre_code_type
//                if (carrier_frequency > 1500e6) {
//                    fre_code_type = "E1";
//                } else if(carrier_frequency > 1250e6) {
//                    fre_code_type = "E6";
//                } else if (carrier_frequency > 1200e6) {
//                    fre_code_type = "E5b";
//                } else if (carrier_frequency > 1100e6) {
//                    fre_code_type = "E5a";
//                }
//                break;
//            case CONSTELLATION_UNKNOWN:
//                prn = ' ' + String.valueOf(sv_id);
//                L1 = 0.0;
//            default: break;
//        }
//
//        /* **************************one of the GPS pseudorange compute method ********************
//        //Rinex : C1
//        //Set the fullbiasnanos if not set or if we need to update the full bias
//        //nanos at each epoch
//        fullbiasnanos = Double.parseDouble(gnss_values[FullBiasNanos]);
//        // Compute the GPS week number as well as the time within the week of
//        //the reception time (i.e. clock epoch)
//        gpsweek = Math.floor(-fullbiasnanos * NS_TO_S / GPS_WEEKSECS);
//        local_est_GPS_time = Double.parseDouble(gnss_values[TimeNanos]) - (fullbiasnanos + Double.parseDouble(gnss_values[BiasNanos]));
//        gpssow = local_est_GPS_time * NS_TO_S - gpsweek * GPS_WEEKSECS;
//
//        //Compute the reception and transmission times
//        tRxSeconds = gpssow - Double.parseDouble(raw_values[TimeOffsetNanos]) * NS_TO_S;
//        tTxSeconds = Double.parseDouble(raw_values[ReceivedSvTimeNanos]) * NS_TO_S;
//        ************************************************************************************** */
//
//        //Pseudorange
//        full_bias_nanos = Double.parseDouble(gnss_values[FullBiasNanos]);
//        time_nanos = Double.parseDouble(gnss_values[TimeNanos]);
//        time_offset_nanos = Double.parseDouble(gnss_values[TimeOffsetNanos]);
//        bias_nanos = Double.parseDouble(gnss_values[BiasNanos]);
//        leap_sec = Double.parseDouble(gnss_values[LeapSecond]);
//
//        //𝑤𝑒𝑒𝑘𝑁𝑢𝑚𝑏𝑒𝑟𝑁𝑎𝑛𝑜𝑠 is the number of nanoseconds that have occurred from the beginning of GPS time to the current WN.
//        //𝐷𝑎𝑦𝑁𝑢𝑚𝑏𝑒𝑟𝑁𝑎𝑛𝑜𝑠 is the number of nanoseconds that have occurred from the beginning of GPS time to the current day.
//        //𝑚𝑖𝑙𝑙𝑖𝑆𝑒𝑐𝑜𝑛𝑑𝑠𝑁𝑢𝑚𝑏𝑒𝑟𝑁𝑎𝑛𝑜𝑠 is the number of milliseconds that have occurred from the beginning of the GPS time.
//        week_number_nanos = Math.floor(-full_bias_nanos / NUM_NANOSEC_WEEK) * NUM_NANOSEC_WEEK;
//        day_number_nanos = Math.floor(-full_bias_nanos / NUM_NANOSEC_DAY) * NUM_NANOSEC_DAY;
//        millSec_number_nanos = Math.floor(-full_bias_nanos / NUM_NANOSEC_100MILL) * NUM_NANOSEC_100MILL;
//
//        //tRx_GNSS = TimeNanos + TimeOffsetNanos - FullBiasNanos - BiasNanos;
//        tRx_GNSS = time_nanos + time_offset_nanos - full_bias_nanos - bias_nanos;
//
//        //compute the tRx from tRx_GNSS
//        switch (constellation_type) {
//            case CONSTELLATION_GPS:
//                tRx_nanos = tRx_GNSS - week_number_nanos;
//                break;
//            case CONSTELLATION_GLONASS:
//                tRx_nanos = tRx_GNSS - day_number_nanos + (3*3600 - leap_sec) * S_TO_NS;
//                break;
//            case CONSTELLATION_BEIDOU:
//                tRx_nanos = tRx_GNSS - week_number_nanos - 14 * S_TO_NS;
//                break;
//            case CONSTELLATION_QZSS:
//                tRx_nanos = tRx_GNSS - week_number_nanos;
//                break;
//            case CONSTELLATION_SBAS:
//                tRx_nanos = tRx_GNSS - week_number_nanos;
//                break;
//            case CONSTELLATION_GAL:
//                if (carrier_frequency > 1500e6) {
//                    tRx_nanos = tRx_GNSS - millSec_number_nanos;
//                } else {
//                    tRx_nanos = tRx_GNSS - week_number_nanos;
//                }
//                break;
//            case CONSTELLATION_UNKNOWN:
//                tRx_nanos = tRx_GNSS - week_number_nanos;
//                break;
//            default:
//                tRx_nanos = 0;
//                break;
//
//        }
//
//        //Compute the reception and transmission times
//        tRxSeconds = tRx_nanos * NS_TO_S;
//        tTxSeconds = Double.parseDouble(raw_values[ReceivedSvTimeNanos]) * NS_TO_S;
//
//        //Compute the travel time, which will be eventually the pseudorange
//        tau = tRxSeconds - tTxSeconds;
//
//        //Check the week rollover, for measurements near the week transition
//        if(tau < 0) {
//            tau += GPS_WEEKSECS;
//        }
//
//        //Compute the range as the difference between the received time and
//        //the transmitted time
//        C1 = tau * SPEED_OF_LIGHT;
//
//        //Check if the range needs to be modified with the range rate in
//        //order to make it consistent with the timestamp
////        if args.integerize:
////            c1 -= frac * values['PseudorangeRateMetersPerSecond']
//
//        //Minimum data quality edition
////        if not args.skip_edit and (c1 > 30e6 or c1 < 10e6):
////          sys.stderr.write("Measurement [ {0} ] for svid [ {1} ] rejected. Out of bounds\n".format(svid, c1))
////        continue
//
//        //Rinex : SNR
//        SNR = raw_values[Cn0DbHz];
//
//        rinex_values[SVID] = prn;
//        rinex_values[L_1] = String.format("%.3f", L1);
//        rinex_values[C_1] = String.format("%.3f", C1);
//        rinex_values[S_1] = SNR;
//        rinex_values[D_1] = String.format("%.3f", C1);
//        rinex_values[CODETYPE] = fre_code_type;
//
//        return rinex_values;
//    }