package com.example.myapplication.Activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.myapplication.Fragment.FirstFragment;
import com.example.myapplication.R;
import com.example.myapplication.Utils.MyTcpClient;

import java.io.IOException;

public class FirstActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new FirstFragment();
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
