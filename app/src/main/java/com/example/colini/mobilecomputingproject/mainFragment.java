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

import com.google.zxing.integration.android.IntentIntegrator;

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
        button.setText("Scan An Item");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity myaty = (MainActivity) getActivity();


                textView.setText("Scanned Amount: " + myaty.gettotal());
                IntentIntegrator i = new IntentIntegrator(getActivity()); //between this line and i.initiateScan() we can edit the Scanner
                i.initiateScan();
            }
        });

        return rootView;
    }
}
