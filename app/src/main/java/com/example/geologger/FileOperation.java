package com.example.geologger;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class FileOperation {
    private Context mContext;

    //file storage path
    private String FILEPATH = "/sdcard/GeoLogger/";
    private String FILENAME = "Rinex.20o";

    public FileOperation(Context context) {
        mContext = context;
    }

    public FileOperation(Context context, String file_path, String file_name) {
        mContext = context;
        FILEPATH = file_path;
        FILENAME = file_name;
    }

    public RandomAccessFile openNewFile() {
        //check if file is exist
        if (isFileExist(0, FILEPATH, FILENAME) == false) {
            return null;
        }

        String file_str = FILEPATH + FILENAME;
        try {
            File file = new File(file_str);

            RandomAccessFile mFile = new RandomAccessFile(file, "rw");
            return mFile;

        } catch (Exception E) {
            Log.e("openNewFile", "ERROR happen in open the file:" + E);
            return null;
        }
    }

    public RandomAccessFile openFile() {
        //check if file is exist
        if (isFileExist(1, FILEPATH, FILENAME) == false) {
            return null;
        }

        String file_str = FILEPATH + FILENAME;
        try {
            File file = new File(file_str);

            RandomAccessFile mFile = new RandomAccessFile(file, "rw");
            return mFile;

        } catch (Exception E) {
            Log.e("openFile", "ERROR happen in open the file:" + E);
            return null;
        }
    }

    public RandomAccessFile openSuffixFile() {
        //obtain the current time
        SimpleDateFormat  simpleDateFormat = new SimpleDateFormat("yyMMddHHmm");
        Date date = new Date();
        String STime = simpleDateFormat.format(date);

        String[] temp = FILENAME.split("\\.");
        String file_str = FILEPATH + temp[0] + STime  + "." + temp[1];

        //check if file is exist
        if (isFileExist(0, FILEPATH, (temp[0] + STime  + "." + temp[1])) == false) {
            return null;
        }

        try {
            File file = new File(file_str);

            RandomAccessFile mFile = new RandomAccessFile(file, "rw");
            return mFile;

        } catch (Exception E) {
            Log.e("openFile", "ERROR happen in open the file:" + E);
            return null;
        }
    }

    public boolean writeToFileAppend(RandomAccessFile mFile, byte[] str, int cnt) {
        try {
            long file_len = mFile.length();
            mFile.seek(file_len);
            mFile.write(str,0, cnt);
            return true;
        } catch (Exception E) {
            Log.e("writeToFile", "ERROR happen in write the file:" + E);
            return false;
        }
    }

    public boolean writeToFileOverwrite(RandomAccessFile mFile, byte[] str, int cnt) {
        try {
            mFile.write(str,0, cnt);
            return true;
        } catch (Exception E) {
            Log.e("writeToFile", "ERROR happen in write the file:" + E);
            return false;
        }
    }

    public Map<String, String> readOneLineFromFile(RandomAccessFile mFile) {
        Map<String, String> map = new HashMap<String, String>();
        String str;

        try {
            str = mFile.readLine();
            map.put("length", String.valueOf(str.length()));
            map.put("content", str);
        } catch (Exception E) {
            Log.e("readFromFile", "ERROR happen in read the file:" + E);
            map.put("length", "-1");
            map.put("content", "");
        }

        return map;
    }

    public Map<String, String> readAllFromFile(RandomAccessFile mFile) {
        Map<String, String> map = new HashMap<String, String>();
        byte[] str = new byte[1024];
        int cnt;

        try {
            cnt = mFile.read(str);
            String showContent = new String(str);
            map.put("length", String.valueOf(cnt));
            map.put("content", showContent);
        } catch (Exception E) {
            Log.e("readFromFile", "ERROR happen in read the file:" + E);
            map.put("length", "-1");
            map.put("content", "");
        }

        return map;
    }


    /******************************************Private Function*************************************/
    //if state == 0, delete the old file and create a new one;
    //if state != 0, just check if it exists;
    private boolean isFileExist(int state, String file_path, String file_name) {
        File file = null;

        isFileDirExist(file_path);

        try{
            file = new File(file_path + file_name);
            if (!file.exists()) {
                if (state == 0) {
                    file.createNewFile();
                } else {
                    return false;
                }
            } else {
                if (state == 0) {
                    file.delete();
                    file.createNewFile();
                }
            }
        } catch (Exception E) {
            E.printStackTrace();
        }
        return true;
    }

    private void isFileDirExist(String file_path) {
        File file = null;

        try {
            file = new File(file_path);
            if (!file.exists()) {
                file.mkdir();
            }
        } catch (Exception E) {
            Log.i("error:", E+"");
        }
    }

}
