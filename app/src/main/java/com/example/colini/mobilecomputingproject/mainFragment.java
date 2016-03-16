package com.example.colini.mobilecomputingproject;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Yang on 16-03-07.
 */
public class mainFragment extends Fragment {

    private Button button;
    private TextView textView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.content_main,container,false);

        button = (Button) rootView.findViewById(R.id.buttoninMain);
        textView = (TextView) rootView.findViewById(R.id.MaintextView);
        button.setText("Send a sample to Detail fragment");
        textView.setText("Not send");

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Detail_Data x = new Detail_Data("T-Love","Pets Food","$25","This is a good food!","233333333");
                MainActivity myaty = (MainActivity) getActivity();
                myaty.setDetail(x);
                textView.setText("Done!");
            }
        });

        return rootView;
    }
}
