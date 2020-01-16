package com.example.myapplication.Utils;

public class MathUtils {
    //将一个范围内[old_low,old_high]的值规约到[new_low,new_high]当中
    public static double getNormalize(double val,double old_low,double old_high,double new_low,double new_high){
        return (val-old_low)/(old_high-old_low)*(new_high-new_low) + new_low;
    }
}
