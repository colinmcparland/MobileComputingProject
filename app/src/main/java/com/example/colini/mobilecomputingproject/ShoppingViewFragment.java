package com.example.colini.mobilecomputingproject;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.jar.Manifest;

/**
 * Created by JoeyFarrell on 2016-03-07.
 */
public class ShoppingViewFragment extends android.support.v4.app.Fragment implements SurfaceHolder.Callback
{
    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;
    Camera camera;
    DrawFrame frame;

    //Button in the view and file for the image
    private Button button;
    private File imageFile;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View myView = inflater.inflate(R.layout.shoppingview_layout, container, false);


        surfaceView = (SurfaceView) myView.findViewById(R.id.mySurface); //view for the camera
        surfaceHolder = surfaceView.getHolder();

        surfaceHolder.addCallback(this); //to display the camera

        frame = (DrawFrame) myView.findViewById(R.id.myView); //draws a rectangle on the screen. frame to find the barcode in

        /*
        * Saving the pic into the Disk
        * Still cannot find the pic
        * */
        button = (Button) myView.findViewById(R.id.scanButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                imageFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) +"test.jpg");
                Uri tempuri = Uri.fromFile(imageFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, tempuri);
                intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);



                if (imageFile.exists()){
                    System.out.println("We have the Pic");
                    System.out.println(imageFile);
                }else {
                    System.out.println("we dont have the pic");
                }
            }
        });

        return myView;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder){
        try{
            camera = Camera.open(); //start the camera
        }

        catch(RuntimeException e){
            System.err.println(e); //hopefully not!
            return;
        }

        try{
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview(); //display whats through the camera
        }

        catch(Exception e){
            System.err.println(e);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder){
        camera.stopPreview(); //stop the preview
        camera.release(); //release the camera
        camera = null; //set the object null
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height){
        refreshCamera();
    }

    public void refreshCamera(){
        if(surfaceHolder.getSurface() == null){ //if there's no surface view...
            return;
        }

        try{
            camera.stopPreview(); //stop the preview
        }

        catch(Exception e){
            System.err.println(e);
            return;
        }

        try{
            camera.setPreviewDisplay(surfaceHolder); //set surfaceHolder
            camera.startPreview(); //start preview
        }
        catch (Exception e){
            System.err.println(e);
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    public void onPause(){
        super.onPause();
        camera.stopPreview();
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshCamera();
    }
}
