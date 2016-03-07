package com.example.colini.mobilecomputingproject;


import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by JoeyFarrell on 2016-03-07.
 */
public class ShoppingViewFragment extends android.support.v4.app.Fragment implements SurfaceHolder.Callback
{
    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;
    Camera camera;
    DrawFrame frame;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View myView = inflater.inflate(R.layout.shoppingview_layout, container, false);


        surfaceView = (SurfaceView) myView.findViewById(R.id.mySurface); //view for the camera
        surfaceHolder = surfaceView.getHolder();

        surfaceHolder.addCallback(this); //to display the camera

        frame = (DrawFrame) myView.findViewById(R.id.myView); //draws a rectangle on the screen. frame to find the barcode in
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
