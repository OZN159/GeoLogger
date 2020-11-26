package com.example.geologger;

import android.content.pm.PackageManager;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

public class SplashScreen extends AppCompatActivity {
    private static final int PERMISSION_REQUEST = 100;
    List<String> mPermissionList = new ArrayList();
    String[] permissions = new String[]{"android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_LOCATION_EXTRA_COMMANDS", "android.permission.READ_PHONE_STATE"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splashscreen);
        initPermission();
    }


    private boolean initPermission() {
        if (VERSION.SDK_INT >= 23) {
            this.mPermissionList.clear();
            for (int i = 0; i < this.permissions.length; i++) {
                if (checkSelfPermission(this.permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                    this.mPermissionList.add(this.permissions[i]);
                }
            }
            if (this.mPermissionList.size() > 0) {
                requestPermissions(this.permissions, 100);

                return true;
            } else {
                startApp();
                return true;
            }
        }
        startApp();
        return true;
    }

    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        super.onRequestPermissionsResult(i, strArr, iArr);
        Object obj = null;
        if (VERSION.SDK_INT >= 23 && 100 == i) {
            for (int i2 : iArr) {
                if (i2 == -1) {
                    obj = 1;
                    break;
                }
            }
        }
        if (obj != null) {
            initPermission();
        } else {
            startApp();
        }
    }

    private void startApp() {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                SplashScreen.this.startActivity(new Intent(SplashScreen.this, MainActivity.class));
                SplashScreen.this.finish();
            }
        }, 2000);
    }
}
