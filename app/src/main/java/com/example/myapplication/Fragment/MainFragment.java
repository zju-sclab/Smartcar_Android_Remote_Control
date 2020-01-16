package com.example.myapplication.Fragment;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.Json.Command;
import com.example.myapplication.Json.ProtocalMsg;
import com.example.myapplication.R;
import com.example.myapplication.Utils.MathUtils;
import com.example.myapplication.Utils.MyTcpClient;
import com.example.myapplication.Utils.Wheel;
import com.google.gson.Gson;

public class MainFragment extends Fragment {
    private Wheel wheel;
    private SeekBar seekbar;
    private Switch aSwitch;
    private Gson gson;
    private TextView showSpeed;
    private TextView showSteer;
    private double speed;
    private double steer = 0.0;
    private double pre_steer = 0.0;
    private double final_steer = 0.0;
    private boolean isBrake = false;
    private int brake = 1;
    private Thread a;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_main,container,false);
        wheel=(Wheel)v.findViewById(R.id.myWheel);
        seekbar = (SeekBar)v.findViewById(R.id.seekBar);
        aSwitch = (Switch)v.findViewById(R.id.showbrake);
        showSpeed = v.findViewById(R.id.showSpeed);
        showSteer = v.findViewById(R.id.showSteer);
        gson = new Gson();
        measureOne();
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        wheel.setOnMyWheelMoveListener(new Wheel.OnMyWheelMoveListener() {
            @Override
            public void onValueChanged(int xDistance, int yDistance) {
                steer = MathUtils.getNormalize(wheel.getRotate_degree(),-90,90,-30.0,30.0);
            }
        });
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                speed = MathUtils.getNormalize(seekbar.getProgress(),0,100,0,7.2  );
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    //打开的回调函数
                    isBrake = true;
                }else{
                    //关闭的回调函数
                    isBrake = false;
                }
            }
        });
        //创建新的线程
        a = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    while (MyTcpClient.socket != null && MyTcpClient.socket.isConnected()){
                        Command c = null;
                        //添加了通过插值法来达到目标角度的功能
                        final_steer = (pre_steer+steer)/2;
                        //如果最后的转角的值过小 则直接设为0
                        if(final_steer<0.01&&final_steer>-0.01)
                            final_steer = 0.0;
                        //brake为1的时候是刹车
                        if(isBrake)
                            brake = 1;
                        else
                            brake = 0;
                        if(speed<1)
                            speed = 0;
                        c = new Command(speed,final_steer,brake);
                        pre_steer = final_steer;
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showSpeed.setText(String.format("%.2f", speed));
                                showSteer.setText(String.format("%.2f", steer));
                            }
                        });
                        Log.e("speed",Double.toString(final_steer));
                        Log.e("steer",Double.toString(steer));
                        ProtocalMsg msg = new ProtocalMsg(0x23,254,0,"123",c);
                        //a line end with "\r\n"
                        MyTcpClient.syncSendTcpMessage(gson.toJson(msg)+"\r\n");
                        Thread.sleep(50);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        a.start();
    }

    private void measureOne() {
        ViewTreeObserver vto = wheel.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                //防止二次调用
                wheel.getViewTreeObserver().removeOnPreDrawListener(this);
                return true;
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        a.interrupt();
    }
}
