package com.example.colini.mobilecomputingproject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;

/**
 * Created by Yang on 16-03-06.
 */
public class View_Detial extends Fragment {

    private Button button;
    private ImageView imageView;
    private File imageFile;

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

        // Trying to show the pic just take in the shopping view
//        imageFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) +"test.jpg");
//        imageView = (ImageView) rootView.findViewById(R.id.DetailimageView);
//        Bitmap bmp = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
//        imageView.setImageBitmap(bmp);

        return rootView;
    }
}
