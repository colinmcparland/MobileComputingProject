package com.example.colini.mobilecomputingproject;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by JoeyFarrell on 2016-04-03.
 */
public class SplashFragment extends Fragment {
    ImageView imageView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View myView = inflater.inflate(R.layout.splash_layout, container, false);
        imageView = (ImageView) myView.findViewById(R.id.splashImage);
        imageView.setImageResource(R.drawable.splash_image);
        return myView;

    }
}


