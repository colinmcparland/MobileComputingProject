package com.example.colini.mobilecomputingproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

/**
 * Created by Yang on 16-03-06.
 */
public class View_Detial extends Fragment {

    private Button button;
    private ImageView imageView;
    private TextView textView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.view_detail,container,false);
        button = (Button) rootView.findViewById(R.id.DetailButton);
        textView = (TextView) rootView.findViewById(R.id.textView2);
        button.setText("Delete");

        MainActivity myaty = (MainActivity) getActivity();
        Detail_Data x = myaty.getDetail_data();

        if (x != null) {
            textView.setText(String.format("Name: %s \nCategory: %s \nPrice: %s \nDescription: %s \nBarcode: %s", x.getName(),
                    x.getCategory(), x.getPrice(), x.getDescription(), x.getBarcode()));
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getFragmentManager().beginTransaction().replace(R.id.mainContainer, new mainFragment()).commit();
            }
        });

        return rootView;
    }
}
