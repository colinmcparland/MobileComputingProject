package com.example.colini.mobilecomputingproject;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by JoeyFarrell on 2016-03-20.
 */
public class CheckoutFragment extends Fragment {

    ImageView barcodeImage;
    TextView upcCode;
    TextView product;
    ArrayList<String> barcodes;
    Bitmap bitmapForBarcode;
    int currPos = 0;
    JSONObject myObject;
    View myView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.checkoutview_layout, container, false);
        barcodeImage = (ImageView) myView.findViewById(R.id.barcode_image);
        upcCode = (TextView) myView.findViewById(R.id.upc_code);
        product = (TextView) myView.findViewById(R.id.item_description);
        barcodes = getArguments().getStringArrayList("upcCodes"); //here we get the ArrayList from the MainActivity, instead we should grab from db...
        final int barcodesSize = barcodes.size();
        myObject = new JSONObject();
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
                final int SWIPE_MIN_DISTANCE = 120; //shortest distance to register a swipe
                final int SWIPE_MAX_OFF_PATH = 250; //maximum change in y value to ensure the swipe is horizontal
                final int SWIPE_THRESHOLD_VELOCITY = 200; //how fast the swipe has to be
                try {
                    if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH) //make sure it's not a vertical swipe...
                        return false;
                    if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY)  {
                        /*
                        If the difference between the x values of the initial position and final position
                        of the swipe is greater than the minimum distance, and the swipe is fast enough register this as
                        a left swipe.

                         */
                        if(currPos == 0){ //if we are showing the first element in barcodes
                            currPos = barcodesSize - 1; //set the position to the last element of barcodes
                        }
                        else{
                            currPos--; //else decrease the current position.
                        }
                        if(!barcodes.isEmpty()) {
                            changeUPC(barcodes.get(currPos)); //update!
                        }
                    } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {//swipe right
                                                /*
                        If the difference between the x values of the final position and initial position
                        of the swipe is greater than the minimum distance, and the swipe is fast enough register this as
                        a right swipe.

                         */

                        if(currPos == barcodesSize - 1){ //if we're displaying the last element
                            currPos = 0; //set to the first
                        }
                        else{
                            currPos++; //else show the next element
                        }
                        if(!barcodes.isEmpty()) {
                            changeUPC(barcodes.get(currPos)); //update!
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
        bitmapForBarcode = encodeBarcodeBitmap(code, 600, 300); //need to change BitMatrix to Bitmap
        barcodeImage.setImageBitmap(bitmapForBarcode);
        try{ //get the name of the item
            myObject = queryUPC(code);
            String productTitle = myObject.getString("itemname");
            product.setText(productTitle);
        }
        catch (JSONException e){
            e.printStackTrace();
        }

    }

    private Bitmap encodeBarcodeBitmap(String code, int width, int height){
        UPCAWriter myWriter = new UPCAWriter(); //from ZXing, used to encode the UPC into a BitMatrix
        BitMatrix myMatrix = null; //from Zxing a 2d matrix representing the colors of the Barcode image (Values are Boolean representing black or white)
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
                Boolean color = myMatrix.get(i, j);
                if(color){ //if true color is black
                    bm.setPixel(i, j, Color.BLACK);
                }
                else{ //else color is white
                    bm.setPixel(i, j, Color.WHITE);
                }

            }
        }
        return bm; //return Bitmap to use with ImageView
    }

    /**
     *
     * @param barcode
     * @return JSON Object
     * @throws JSONException
     */
    public JSONObject queryUPC(String barcode) throws JSONException
    {
        JSONObject json;
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
        String K="";
        try {
            URL url = new URL("http://api.upcdatabase.org/json/72b665bccfa4c65025f18e2be5bd2e65/"+barcode);
            InputStream is = url.openStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            String line;
            while ( (line = br.readLine()) != null)

                K+=line;

            br.close();
            is.close();

        } catch (Exception e) {
            e.printStackTrace();
        }


        json = new JSONObject(K);

        return json;
    }
}