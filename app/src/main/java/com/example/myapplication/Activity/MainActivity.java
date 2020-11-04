package com.example.myapplication.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

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

import com.example.myapplication.Fragment.MainFragment;
import com.example.myapplication.Json.Command;
import com.example.myapplication.Json.ProtocalMsg;
import com.example.myapplication.R;
import com.example.myapplication.Utils.MyTcpClient;
import com.example.myapplication.Utils.Wheel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 *  通过重载的createFragment来配置对应activity的页面
 *  下一个activity可以引入fragment 因为不需要重复创造activity
 */
public class MainActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new MainFragment();
    }

    @Override
    protected void startActivityFunc() {

    }

    @Override
    protected void pauseActivityFunc() {

    }

    @Override
    protected void destroyActivityFunc() {

    }
}
