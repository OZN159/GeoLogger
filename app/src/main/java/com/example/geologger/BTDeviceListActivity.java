package com.example.geologger;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Set;

public class BTDeviceListActivity extends AppCompatActivity {
    private static final String TAG = "BTDeviceListActivity";

    // Return Intent extra
    public static String EXTRA_DEVICE_ADDRESS = "device_address";

    private BluetoothAdapter mBtAdapter;
    private ArrayAdapter<String> mPairedDevicesArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //get the view
        setContentView(R.layout.subactivity_btdevice_list);

        // 初始化阵列适配器。 一个用于已配对的设备，一个用于新发现的设备,一个用于测试专用的设备
        mPairedDevicesArrayAdapter = new ArrayAdapter<String>(this,
                R.layout.textview_bt_devicesname);


        // 为配对的设备查找和设置ListView
        ListView pairedListView = (ListView) findViewById(R.id.paired_devices);
        pairedListView.setAdapter(mPairedDevicesArrayAdapter);
        pairedListView.setOnItemClickListener(mDeviceClickListener);

        // 获取本地蓝牙适配器
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();

        // 获取一组当前配对的设备
        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();

        // 如果有配对的设备，请将每个设备添加到ArrayAdapter
        TextView textView = (TextView) findViewById(R.id.title_paired_devices);
        if (pairedDevices.size() > 0)
        {
            textView.setVisibility(View.VISIBLE);
            for (BluetoothDevice device : pairedDevices)
            {
                mPairedDevicesArrayAdapter.add(device.getName() + "\n"
                        + device.getAddress());

            }
        }
    }

    // ListView中其他设备的点击监听器
    private OnItemClickListener mDeviceClickListener = new OnItemClickListener()
    {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3)
        {
            //取消发现,我们m_about来连接
            mBtAdapter.cancelDiscovery();

            // 获取设备的MAC地址，这是视图中最后17个字符
            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);

            // 创建结果意图并包括MAC地址
            Intent intent = new Intent();
            intent.putExtra(EXTRA_DEVICE_ADDRESS, address);

            // 设置结果并完成此活动
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    };
}
