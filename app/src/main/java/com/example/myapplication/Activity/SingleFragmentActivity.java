package com.example.myapplication.Activity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.example.myapplication.R;

public abstract class SingleFragmentActivity extends FragmentActivity {
    //  TODO 自动驾驶模式界面的实现 自动驾驶模式下可以显示地图上面的点
    //  TODO 可以添加重力驾驶模块
    //  TODO 选择模式的界面来管理tcpconnection 实际的页面来管理thread
    //  TODO 可以传输建成的地图
    // 使用了设计模式 通过fragment来管理页面中的布局变化
    protected abstract Fragment createFragment();
    protected abstract void startActivityFunc();
    protected abstract void pauseActivityFunc();
    protected abstract void destroyActivityFunc();
//
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT >= 21){
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_fragment);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);

        if(fragment == null){
            fragment = createFragment();
            fm.beginTransaction().add(R.id.fragment_container,fragment).commit();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroyActivityFunc();
    }

    @Override
    protected void onStart() {
        super.onStart();
        startActivityFunc();
    }

    @Override
    protected void onPause() {
        super.onPause();
        pauseActivityFunc();
    }
}
