package com.example.myapplication.Utils;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.util.Printer;

import com.example.myapplication.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class MyTcpClient {
    public static Socket socket = null;

    public static void startClient(final String ipAddress, final int port){
        if(ipAddress == null){
            return;
        }
        if(socket == null){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try{
                        Log.i("tcp","start client");
                        socket = new Socket(ipAddress,port);
                        Log.i("tcp","tcp connect success!");
                        PrintWriter pw = new PrintWriter(socket.getOutputStream());
                        InputStream ins = socket.getInputStream();
                        byte[] buffer = new byte[1024];
                        int len = -1;
                        while ( (len = ins.read(buffer) )!=-1){
                            String data = new String(buffer,0,len);
                            Log.i("tcp","receive msg from server: "+data);
                        }
                        Log.i("tcp","client disconnect");
                        pw.close();
                    }catch (Exception EE){
                        EE.printStackTrace();
                        Log.i("tcp","unable to connect server!");
                    }finally {
                        try{
                            if(socket!=null)
                                socket.close();
                        }catch (IOException ie){
                            ie.printStackTrace();
                        }
                        socket = null;
                    }
                }
            }).start();
        }
    }

    public static void sendTcpMessage(final String msg){
        if(socket != null && socket.isConnected()){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try{
                        socket.getOutputStream().write(msg.getBytes());
                        socket.getOutputStream().flush();
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    public static void syncSendTcpMessage(final String msg){
        if(socket != null && socket.isConnected()){
            try{
                socket.getOutputStream().write(msg.getBytes());
                socket.getOutputStream().flush();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    public static void eventLoop(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    while (socket != null && socket.isConnected()){

                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static void closeConnection(){
        try{
            if(socket!=null)
                socket.close();
            socket = null;
        }catch (IOException E){
            E.printStackTrace();
        }
    }
}
