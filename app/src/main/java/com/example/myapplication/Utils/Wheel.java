package com.example.myapplication.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View;


import androidx.annotation.Nullable;

import com.example.myapplication.R;

public class Wheel extends View implements View.OnTouchListener {
    int xPosition;      //x coordinate of icon
    int yPosition;      //y coordinate of icon
    int centerX;        //center of wheel x coordinate
    int centerY;        //center of wheel y coordinate
    int mainRadius;
    int secondRadius;
    boolean isClicked;
    boolean isLocked = false;
    int remainingState = -1;  // 0 is right 1 is left
    int wheel_width;
    int wheel_height;
    double rotate_degree = 0.0;
    OnMyWheelMoveListener myWheelMoveListener;

    public Wheel(Context context){
        super(context);
    }

    public Wheel(Context context, @Nullable AttributeSet attrs){
        super(context,attrs);
    }

    public Wheel(Context context,@Nullable AttributeSet attrs,int defStyleAttr){
        super(context,attrs,defStyleAttr);
        isClicked = false;  // initial state of wheel
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.UNSPECIFIED?100:MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.UNSPECIFIED?100:MeasureSpec.getSize(heightMeasureSpec);
        wheel_width = 600;
        wheel_height = 600;
//        logPos(getWidth(),getHeight());
//        logPos(width,height);
        this.mainRadius = (getWidth()-100)/2;
        this.secondRadius = mainRadius/5*2; // touchable circle area radius
        logPos((int)getLeft(),(int)getRight());
        setMeasuredDimension(wheel_width,wheel_height);

        this.centerX = getWidth()/2;
        this.centerY = getHeight()/2;
        this.xPosition = this.centerX;
        this.yPosition = this.centerY;
//        logPos(getWidth()/2,getHeight()/2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Bitmap bm;

        Paint BackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        BackgroundPaint.setFilterBitmap(true);
        BackgroundPaint.setDither(true);

        Paint circlePaint = new Paint();
        circlePaint.setColor(Color.parseColor("#52c1bd"));
        circlePaint.setStyle(Paint.Style.FILL);
        if(!isClicked){        //set circle1 to bm is clicked otherwise circle
            bm=((BitmapDrawable)getResources().getDrawable(R.mipmap.wheel)).getBitmap();
        }else{
            bm=((BitmapDrawable)getResources().getDrawable(R.mipmap.wheel)).getBitmap();
//            bm = adjustPhotoRotation(bm,(int)this.rotate_degree);
//            Log.e("rotate ",Double.toString(this.rotate_degree));
//            bm = adjustPhotoRotation(bm,(int)(this.rotate_degree*180/3.14159));
        }
        Rect mSrcRect = new Rect(0,0,bm.getWidth(),bm.getHeight()); //设置原始图像中要被画出来的区域
        Rect mDestRect = new Rect(30,30,getWidth()-30,getHeight()-30);                       //设置目标区域中会被画进去图像的区域
        canvas.drawCircle(this.xPosition,this.yPosition,5,circlePaint);
        if(isClicked){
//            if(isLocked){
//                if(remainingState == 0){
//                    this.rotate_degree = 90;
//                }else{
//                    this.rotate_degree = -90;
//                }
//            }
            canvas.rotate((float)this.rotate_degree,getWidth()/2,getHeight()/2);

        }
        canvas.drawBitmap(bm,mSrcRect,mDestRect,BackgroundPaint);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        isClicked = true;
        this.xPosition = (int)event.getX();
        this.yPosition = (int)event.getY();
        if(Math.sqrt((this.xPosition-this.centerX)*(this.xPosition-this.centerX)+(this.yPosition-this.centerY)*(this.yPosition-this.centerY))>mainRadius){
            this.rotate_degree = -Math.atan2(getWidth()/2-this.xPosition,getHeight()/2-this.yPosition);
            this.rotate_degree = this.rotate_degree*180/Math.PI;
            //always locked until you release your hand
            if(this.rotate_degree>90||this.rotate_degree<-90){
                if(!isLocked){
                    this.isLocked = true;
                    if(this.rotate_degree>90)
                        remainingState = 0;
                    if(this.rotate_degree<-90)
                        remainingState = 1;
                }else{
                    if(remainingState == 0){
                        this.rotate_degree = 90;
                    }else if(remainingState == 1){
                        this.rotate_degree = -90;
                    }
                }
            }else{
                isLocked = false;
                remainingState = -1;
            }
        }
        //relative position
        if(this.myWheelMoveListener!=null){
            this.myWheelMoveListener.onValueChanged(this.xPosition,this.yPosition);
        }
        invalidate();

        if(event.getAction()==1){
            isClicked=false;
            isLocked = false;
            remainingState = -1;
            this.yPosition=this.centerY;
            this.xPosition=this.centerX;
            this.rotate_degree = 0;
            if(this.myWheelMoveListener!=null){
                this.myWheelMoveListener.onValueChanged(this.xPosition,this.yPosition);
            }
            invalidate();
        }
        return true;
    }

    public void setOnMyWheelMoveListener(OnMyWheelMoveListener listener){
        this.myWheelMoveListener=listener;
    }

    public static abstract interface OnMyWheelMoveListener {
        public abstract void onValueChanged(int xDistance, int yDistance);
    }

    public double getRotate_degree(){
        return this.rotate_degree;
    }

    private void logPos(int xPosition,int yPosition){
        Log.e("wheel x:",Integer.toString(xPosition));
        Log.e("y: ",Integer.toString(yPosition));
    }

    Bitmap adjustPhotoRotation(Bitmap bm, final int orientationDegree) {

        Matrix m = new Matrix();
        //anchor point to rotate
        m.setRotate(orientationDegree, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);
        try {
            Bitmap bm1 = Bitmap.createBitmap(bm, this.centerX, this.centerY, bm.getWidth(), bm.getHeight(), m, true);
            return bm1;
        } catch (OutOfMemoryError ex) {

        }
        return null;
    }

}
