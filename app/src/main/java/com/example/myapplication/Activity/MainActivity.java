package com.example.myapplication.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.hardware.SensorManager;
import android.os.Bundle;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.example.myapplication.Json.Command;
import com.example.myapplication.Json.ProtocalMsg;
import com.example.myapplication.R;
import com.example.myapplication.Utils.MyTcpClient;
import com.example.myapplication.Utils.Wheel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


//下一个activity可以引入fragment 因为不需要重复创造activity
public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager sensorManager = null;
    private Sensor gyroSensor = null;
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

    private double getNormalize(double val,double old_low,double old_high,double new_low,double new_high){
        return (val-old_low)/(old_high-old_low)*(new_high-new_low) + new_low;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        a.interrupt();
        MyTcpClient.closeConnection();
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        sensorManager.registerListener(this,gyroSensor,SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        wheel=(Wheel)findViewById(R.id.myWheel);
        seekbar = (SeekBar)findViewById(R.id.seekBar);
        aSwitch = (Switch)findViewById(R.id.showbrake);
        gson = new Gson();
        showSpeed = findViewById(R.id.showSpeed);
        showSteer = findViewById(R.id.showSteer);
        measureOne();

        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        gyroSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        wheel.setOnMyWheelMoveListener(new Wheel.OnMyWheelMoveListener() {
            @Override
            public void onValueChanged(int xDistance, int yDistance) {
                steer = getNormalize(wheel.getRotate_degree(),-90,90,-30.0,30.0);
            }
        });
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                speed = getNormalize(seekbar.getProgress(),0,100,0,7.2  );
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

    }

    @Override
    protected void onStart() {
        super.onStart();
        a = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    while (MyTcpClient.socket != null && MyTcpClient.socket.isConnected()){
                        //TODO 添加一些提示字段
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
                        runOnUiThread(new Runnable() {
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

                Log.e("onPreDraw", "````````````````````````````````````````");
                Log.e("Left", "=" + wheel.getLeft());
                Log.e("Top", "=" + wheel.getTop());
                Log.e("Right", "=" + wheel.getRight());
                Log.e("Bottom", "=" + wheel.getBottom());
                Log.e("x", "=" + wheel.getX());
                Log.e("y", "=" + wheel.getY());
                return true;
            }
        });
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_GYROSCOPE){
        }
    }

    private void showInfo(String info){
        Log.e("gyro ",info);
    }
}
