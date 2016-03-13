package com.example.colini.mobilecomputingproject;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;


/**
 * Created by JoeyFarrell on 2016-03-07.
 */
public class ShoppingViewFragment extends android.app.Fragment{


    TextView tv; //display results of the scan
    Button btn; //button for another scan. Will not use in final product
    IntentIntegrator integrator;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View myView = inflater.inflate(R.layout.shoppingview_layout, container, false);
        integrator = IntentIntegrator.forFragment(this);
        return myView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);

        if(scanResult != null){
            System.out.println("we have a result");
            String result = scanResult.getContents();
            System.out.println(result);

        }
    }




}
