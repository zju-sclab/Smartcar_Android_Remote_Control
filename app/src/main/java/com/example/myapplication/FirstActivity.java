package com.example.myapplication;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.Utils.MyTcpClient;

import java.io.IOException;

public class FirstActivity extends AppCompatActivity {
    private Button button;
    private String ip;
    private int port;
    private EditText ip_text,port_text;
    private final double time_out = 2.5;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        button =(Button)findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener(){
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                ip_text = (EditText)findViewById(R.id.ip);
                port_text = (EditText)findViewById(R.id.port);
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
                AlertDialog alertDialog1 = new AlertDialog.Builder(this)
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
          startActivity(new Intent(this,MainActivity.class));
    }
}
