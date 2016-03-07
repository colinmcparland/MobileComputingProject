package com.example.colini.mobilecomputingproject;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

/**
 * Created by JoeyFarrell on 2016-03-06.?
 */
public class DrawFrame extends View{
    Paint paint = new Paint();
    Context c;

    public DrawFrame(Context context){
        super(context);
        c = context;

    }
    public DrawFrame(Context context, AttributeSet attrs) {
        super(context, attrs);
        c = context;
    }

    public DrawFrame(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        c = context;
    }

    @Override
    public void onDraw(Canvas canvas){ //draws a rectangle on the screen, used to find the barcode!
        super.onDraw(canvas);
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(7);
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(dm);
        int height = dm.heightPixels;
        int width = dm.widthPixels;
        int left = width/4; //left side of rectangle is 1/4 of the way
        int top = 3*height/8; //top is 3/8ths down
        int right = width - left; //right is 3/4 of the screen
        int bottom = height - top; //bottom is 3/8ths from the bottom, the rectangle will be 1/4 of the screen high
        paint.setStyle(Paint.Style.STROKE); //set to stroke
        canvas.drawRect(left, top, right, bottom, paint); //draw the rectangle
        setBackgroundColor(Color.TRANSPARENT); //background transparent to see the camera underneath
    }

}
