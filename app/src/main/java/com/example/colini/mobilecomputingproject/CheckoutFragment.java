package com.example.colini.mobilecomputingproject;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.UPCAWriter;

import java.util.ArrayList;

/**
 * Created by JoeyFarrell on 2016-03-20.
 */
public class CheckoutFragment extends Fragment {

    ImageView barcodeImage;
    TextView upcCode;
    TextView description;
    ArrayList<String> barcodes;
    Bitmap bitmapForBarcode;
    int currPos = 0;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View myView = inflater.inflate(R.layout.checkoutview_layout, container, false);
        barcodeImage = (ImageView) myView.findViewById(R.id.barcode_image);
        upcCode = (TextView) myView.findViewById(R.id.upc_code);
        description = (TextView) myView.findViewById(R.id.item_description);
        barcodes = getArguments().getStringArrayList("upcCodes");
        final int barcodesSize = barcodes.size();
        System.out.println(barcodesSize);
        if(barcodes.isEmpty()){
            upcCode.setText("No Products Scanned");
        }
        else{
            String code = barcodes.get(0);
            changeUPC(code);

        }

        final GestureDetector gesture = new GestureDetector(getActivity(), new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onDown(MotionEvent e){
                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY){ //found on StackOverflow../
                final int SWIPE_MIN_DISTANCE = 120;
                final int SWIPE_MAX_OFF_PATH = 250;
                final int SWIPE_THRESHOLD_VELOCITY = 200;
                try {
                    if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH) //make sure it's not a vertical swipe...
                        return false;
                    if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) { //swiped left
                        if(currPos == 0){
                            currPos = barcodesSize;
                        }
                        else{
                            currPos--;
                        }
                        if(!barcodes.isEmpty()) {
                            changeUPC(barcodes.get(currPos));
                        }
                    } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {//swipe right
                        if(currPos == barcodesSize){
                            currPos = 0;
                        }
                        else{
                            currPos++;
                        }
                        if(!barcodes.isEmpty()) {
                            changeUPC(barcodes.get(currPos));
                        }
                    }
                } catch (Exception e) {
                    System.out.println(e); //hope not!
                }
                return super.onFling(e1, e2, velocityX, velocityY);
            }
        });

        myView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gesture.onTouchEvent(event);
            }
        });

        return myView;
    }

    public void changeUPC(String code){
        upcCode.setText(code);
        description.setText("Need to get description from API!");
        bitmapForBarcode = encodeBarcodeBitmap(code, 600, 300);
        barcodeImage.setImageBitmap(bitmapForBarcode);
    }

    private Bitmap encodeBarcodeBitmap(String code, int width, int height){
        UPCAWriter myWriter = new UPCAWriter(); //from ZXing, used to encode the UPC into a BitMatrix
        BitMatrix myMatrix = null; //from Zxing a 2d matrix representing the colors of the Barcode image.
        try{
            myMatrix = myWriter.encode(code, BarcodeFormat.UPC_A, width, height); //encode
        }
        catch(WriterException e)
        {
            e.printStackTrace();
        }

        Bitmap bm = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        for(int i = 0; i < width; i++){
            for(int j = 0; j < height; j++){
                bm.setPixel(i, j, myMatrix.get(i, j) ? Color.BLACK : Color.WHITE); //either 1 0. If 1 pixel is black, if 0 pixel is white
            }
        }
        return bm; //return Bitmap to use with ImageView
    }
}