package com.example.myapplication.Fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.Activity.AutoActivity;
import com.example.myapplication.R;
import com.example.myapplication.Utils.MyTcpClient;

import java.io.IOException;

public class FirstFragment extends Fragment {
    private Button button;
    private String ip;
    private int port;
    private EditText ip_text,port_text;
    private final double time_out = 2.5;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_first,container,false);
        button =(Button)v.findViewById(R.id.button);
        ip_text = (EditText)v.findViewById(R.id.ip);
        port_text = (EditText)v.findViewById(R.id.port);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                ip = ip_text.getText().toString();
                port = Integer.parseInt(port_text.getText().toString());
                try {
                    connectTCP();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private void connectTCP() throws IOException {
        MyTcpClient.startClient(ip,port);
        //获取开始时间 ms为单位
        long startTime=System.currentTimeMillis();
        double connectTime;
        boolean flag = false;
        while (MyTcpClient.socket==null){
            connectTime = (System.currentTimeMillis()-startTime)/1000;
            if(connectTime>time_out){
                AlertDialog alertDialog1 = new AlertDialog.Builder(getContext())
                        .setTitle("Timeout")//标题
                        .setMessage("IP/PORT INACCURATE!")//内容
                        .setIcon(R.mipmap.car)//图标
                        .create();
                alertDialog1.show();
                break;
            }
        }
        while (MyTcpClient.socket!=null){
            if(MyTcpClient.socket.isConnected()){
                flag = true;
                break;
            }
        }

        if(flag)
            startActivity(new Intent(getContext(), AutoActivity.class));
    }
}
