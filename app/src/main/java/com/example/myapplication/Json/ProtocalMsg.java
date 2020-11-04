package com.example.myapplication.Json;

import com.google.gson.Gson;

public class ProtocalMsg {
    private float version;
    private int type;
    private int ack;
    private int requestId;
    private String vin;
    private Command data;

    public ProtocalMsg(float version,int type,int ack,int requestId,String vin,Command data){
        this.version = version;
        this.type = type;
        this.ack = ack;
        this.requestId = requestId;
        this.vin = vin;
        this.data = data;
    }

    public ProtocalMsg(int type,int ack,int requestId,String vin,Command data){
        this.type = type;
        this.ack = ack;
        this.requestId = requestId;
        this.vin = vin;
        this.data = data;
    }
}
