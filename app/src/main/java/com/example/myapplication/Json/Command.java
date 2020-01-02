package com.example.myapplication.Json;

public class Command {
    private double speed;
    private double steer;    //[-0.6,0.6]
    private int brake;
    private int shift;

    public Command(){
        this.brake = 0;
        this.shift = 0;
    }

    public Command(double speed,double steer,int brake,int shift){
        this.speed = speed;
        this.steer = steer;
        this.brake = brake;
        this.shift = shift;
    }

    public Command(double speed,double steer,int brake){
        this.speed = speed;
        this.steer = steer;
        this.brake = brake;
        this.shift = 0;
    }

    public double getSpeed(){
        return speed;
    }

    public void setSpeed(double speed){
        this.speed = speed;
    }

    public void setSteer(double steer){
        this.steer = steer;
    }
}
