package com.example.colini.mobilecomputingproject;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by Yang on 16-03-06.
 */
public class View_Detial extends Fragment {

    private Button button;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.view_detail,container,false);
        button = (Button) rootView.findViewById(R.id.DetailButton);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().replace(R.id.mainContainer,new mainFragment()).commit();
            }
        });

        return rootView;
    }
}
