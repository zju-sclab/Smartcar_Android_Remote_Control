package com.example.myapplication;

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
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.myapplication.Json.Command;
import com.example.myapplication.Json.ProtocalMsg;
import com.example.myapplication.Utils.MyTcpClient;
import com.example.myapplication.Utils.Wheel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager sensorManager = null;
    private Sensor gyroSensor = null;
//    private TextView vX;
//    private TextView vY;
//    private TextView vZ;
//    private TextView v;
    private Wheel wheel;
    private SeekBar seekbar;
    private Gson gson;
    private double speed;
    private double steer;
    private Lock lock = new ReentrantLock();
    private Lock lock1 = new ReentrantLock();
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
//        vX = (TextView)findViewById(R.id.vx);
//        vY = (TextView)findViewById(R.id.vy);
//        vZ = (TextView)findViewById(R.id.vz);
//        v = (TextView)findViewById(R.id.v);
        wheel=(Wheel)findViewById(R.id.myWheel);
        seekbar = (SeekBar)findViewById(R.id.seekBar);
        gson = new Gson();
        measureOne();

        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        gyroSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        wheel.setOnMyWheelMoveListener(new Wheel.OnMyWheelMoveListener() {
            @Override
            public void onValueChanged(int xDistance, int yDistance) {
//                v.setText("rotate:" + wheel.getRotate_degree() +", speed:" + seekbar.getProgress());
            }
        });
        ViewTreeObserver vto = seekbar.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                lock.lock();
                speed = getNormalize(seekbar.getProgress(),0,100,0,7.2  );
//                Log.e("speed1",Double.toString(speed));
                lock.unlock();
            }
        });
        ViewTreeObserver vto1 = wheel.getViewTreeObserver();
        vto1.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                lock1.lock();
                steer = getNormalize(wheel.getRotate_degree(),-90,90,-30.0,30.0);
//                Log.e("steer1",Double.toString(steer));
                lock1.unlock();

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
                        //TODO 需要用插值法来缓慢达到目标的值
//                        double steer = getNormalize(wheel.getRotate_degree(),-90,90,-30.0,30.0);
//                        double speed = getNormalize(seekbar.getProgress(),0,100,0,10);
                        Command c = null;
                        lock.lock();
                        lock1.lock();
                        c = new Command(speed,steer,0);
                        Log.e("speed",Double.toString(speed));
                        Log.e("steer",Double.toString(steer));
                        lock1.unlock();
                        lock.unlock();

                        ProtocalMsg msg = new ProtocalMsg(0x23,254,0,"123",c);
                        //a line end with "\r\n"
                        MyTcpClient.syncSendTcpMessage(gson.toJson(msg)+"\r\n");
                        Thread.sleep(100);
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
//                wheel.setCenterX(wheel.getLeft() + wheel.getWidth()/2);
//                wheel.setCenterY(wheel.getTop() + wheel.getHeight()/2);
//                button.setX(wheel.getLeft() + wheel.getWidth()/2);
//                button.setY(wheel.getTop() + wheel.getHeight()/2);
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
//            showInfo("事件：" + " x:" + event.values[0] + " y:" + event.values[1] + " z:" + event.values[2]);

//            vX.setText("X: " + event.values[0]);
//            vY.setText("Gyroscope Y: " + event.values[1]);
//            vZ.setText("Gyroscope Z: " + event.values[2]);
        }
    }

    private void showInfo(String info){
        Log.e("gyro ",info);
    }
}
