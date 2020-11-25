package com.example.geologger;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Administrator on 2016/7/8.
 */
public class FragmentModule extends Fragment {
    private String name;
    private String version_info;
    private Button button_share;

    public FragmentModule(String fName){
        this.name = fName;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.framement_module,container,false);

        button_share = (Button) view.findViewById(R.id.fragment_share);
        button_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initShare();
            }
        });

        return view;
    }



    private void initShare() {
        Intent textIntent = new Intent(Intent.ACTION_SEND);
        textIntent.setType("text/plain");
        textIntent.putExtra(Intent.EXTRA_TEXT,
                "The software is a smart tool for Mobile phone raw data logging and displaying.\n\n" +
                        "Contact us: Hi-target international group.\n" +
                        "Website: http://bd.hi-target.com.cn\n" +
                        "Technical support: Nander@hitargetgroup.com");
        startActivity(Intent.createChooser(textIntent, "Share"));
    }
}







