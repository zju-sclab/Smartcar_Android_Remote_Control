package com.example.myapplication.Activity;

import androidx.fragment.app.Fragment;

import com.example.myapplication.Fragment.AutoFragment;

public class AutoActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new AutoFragment();
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
