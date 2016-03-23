package com.example.colini.mobilecomputingproject;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.jar.Attributes;

/**
 * Created by JoeyFarrell on 2016-03-21.
 */
public class BarcodeView extends View {

    ImageView barcodeImage;
    TextView upcCode;
    TextView productTitle;


    public BarcodeView(Context context){
        super(context);

    }

    public BarcodeView(Context context, AttributeSet attr){
        super(context, attr);
    }
}
