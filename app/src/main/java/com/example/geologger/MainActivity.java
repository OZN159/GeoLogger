package com.example.geologger;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private FragmentSatellite f1;
    private FragmentRawData f2;
    private FragmentModule f3;

    private GNSSContainer mGnssContainer;
    private Rinex mRinex;

    //底部三个按钮
    private Button foot1;
    private Button foot2;
    private Button foot3;

    private static final int PERMISSION_REQUEST = 100;
    List<String> mPermissionList = new ArrayList();
    String[] permissions = new String[]{"android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_LOCATION_EXTRA_COMMANDS", "android.permission.READ_PHONE_STATE"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //requestMyPermissions();
        initPermission();

        mRinex = new Rinex(getApplicationContext());
        mGnssContainer = new GNSSContainer(getApplicationContext(), mRinex);

        foot1 = (Button) findViewById(R.id.bt1);
        foot2 = (Button) findViewById(R.id.bt2);
        foot3 = (Button) findViewById(R.id.bt3);
        foot1.setOnClickListener(this);
        foot2.setOnClickListener(this);
        foot3.setOnClickListener(this);

        //第一次初始化首页默认显示第一个fragment
        initFragment2();
        initFragment3();
        initFragment1();
    }

    //显示第一个fragment
    private void initFragment1(){
        //开启事务，fragment的控制是由事务来实现的
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        //第一种方式（add），初始化fragment并添加到事务中，如果为null就new一个
        if(f1 == null){
            f1 = new FragmentSatellite("Correction Data", mGnssContainer);
            transaction.add(R.id.main_frame_layout, f1);
        }
        //隐藏所有fragment
        hideFragment(transaction);
        //显示需要显示的fragment
        transaction.show(f1);

        //第二种方式(replace)，初始化fragment
//        if(f1 == null){
//            f1 = new MyFragment("Correction Data");
//        }
//        transaction.replace(R.id.main_frame_layout, f1);

        //提交事务
        transaction.commit();
    }

    //显示第二个fragment
    private void initFragment2(){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if(f2 == null){
            f2 = new FragmentRawData("Raw Data", mGnssContainer, mRinex);
            transaction.add(R.id.main_frame_layout,f2);
        }
        hideFragment(transaction);
        transaction.show(f2);

//        if(f2 == null) {
//            f2 = new MyFragment("Raw Data");
//        }
//        transaction.replace(R.id.main_frame_layout, f2);

        transaction.commit();
    }

    //显示第三个fragment
    private void initFragment3(){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if(f3 == null){
            f3 = new FragmentModule("Setting");
            transaction.add(R.id.main_frame_layout,f3);
        }
        hideFragment(transaction);
        transaction.show(f3);

//        if(f3 == null) {
//            f3 = new FragmentModule("Setting");
//        }
//        transaction.replace(R.id.main_frame_layout, f3);

        transaction.commit();
    }

    //隐藏所有的fragment
    private void hideFragment(FragmentTransaction transaction){
        if(f1 != null){
            transaction.hide(f1);
        }
        if(f2 != null){
            transaction.hide(f2);
        }
        if(f3 != null){
            transaction.hide(f3);
        }
    }

    @Override
    public void onClick(View v) {
        if(v == foot1){
            initFragment1();
        }else if(v == foot2){
            initFragment2();
        }else if(v == foot3){
            initFragment3();
        }
    }

    private void requestMyPermissions() {
        //the permission of write and read
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //没有授权，编写申请权限代码
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
        } else {
            //Log.d(TAG, "requestMyPermissions: Location permission");
        }

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            //没有授权，编写申请权限代码
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
        } else {
            //Log.d(TAG, "requestMyPermissions: Location permission");
        }
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS)
                != PackageManager.PERMISSION_GRANTED) {
            //没有授权，编写申请权限代码
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS}, 100);
        } else {
            //Log.d(TAG, "requestMyPermissions: location_extra permission");
        }

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            //没有授权，编写申请权限代码
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, 100);
        } else {
            //Log.d(TAG, "requestMyPermissions: location_extra permission");
        }

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
        }

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            //没有授权，编写申请权限代码
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE}, 100);
        } else {
            //Log.d(TAG, "requestMyPermissions: location_extra permission");
        }
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

                //restart the application
//                exitApplication();

                return true;
            } else {
                return true;
            }
        }
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
        }
    }

    //exit APP
    private void exitApplication(){

        Toast.makeText(this, "The application will shut down", Toast.LENGTH_LONG).show();

        finish();
        System.exit(0);
    }
}
