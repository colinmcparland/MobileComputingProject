package com.example.colini.mobilecomputingproject;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentIntegrator;


/**
 * Created by JoeyFarrell on 2016-03-07.
 */
public class CartViewFragment extends Fragment{


    TextView tv; //display results of the scan
    String tvText = "";
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View myView = inflater.inflate(R.layout.cartview_layout, container, false);
        int codesLength = getArguments().getInt("codesLength");
        tv = (TextView) myView.findViewById(R.id.myTextView);
        for(int i = 0; i < codesLength; i++){
            String key = "code" + i;
            String code = getArguments().getString(key);
            if(tvText == ""){
                tvText = code;
            }
            else{
            tvText = tvText + "\n" + code;
            }
            tv.setText(tvText);
        }
        return myView;
    }





}
