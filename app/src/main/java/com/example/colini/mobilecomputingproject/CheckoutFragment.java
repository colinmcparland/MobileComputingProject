package com.example.colini.mobilecomputingproject;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
    //fooo
    ImageView barcodeImage;
    TextView upcCode;
    TextView product;
    TextView quantity;
    TextView numProducts;
    Bitmap bitmapForBarcode;
    int currPos = 0;
    JSONObject myObject;
    View myView;
    SQLiteDatabase myDb;
    ArrayList <Product> myCart;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myDb = getActivity().openOrCreateDatabase("scanAndShop", Context.MODE_PRIVATE, null);
        myCart = new ArrayList<Product>();
        Cursor c =  myDb.rawQuery("select distinct(barcode), count(barcode) as quant, product_name from list where scanned = 1 group by barcode",null);
        try {
            while (c.moveToNext()) {
                String barcode = c.getString(0);
                int quant = c.getInt(1);
                String name = c.getString(2);
                if(myCart == null){
                    myCart.set(0, new Product(barcode, name, quant));
                }
                else{
                    myCart.add(new Product(barcode, name, quant));
                }
            }
        }
        finally{
            c.close();
        }




        myView = inflater.inflate(R.layout.checkoutview_layout, container, false);
        barcodeImage = (ImageView) myView.findViewById(R.id.barcode_image);
        upcCode = (TextView) myView.findViewById(R.id.upc_code);
        product = (TextView) myView.findViewById(R.id.item_description);
        quantity = (TextView) myView.findViewById(R.id.quant);
        numProducts = (TextView) myView.findViewById(R.id.numProducts);

        myObject = new JSONObject();



        Button finish=(Button) myView.findViewById(R.id.buttonFinish);
        finish.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {

                        myDb.execSQL("insert into payment (store_name, time) values (\""+""+"\",date('now'))");
                        Cursor payment=myDb.rawQuery("select * from payment order by id desc limit 1", null);
                        payment.moveToFirst();
                        Cursor c= myDb.rawQuery("select * from list where scanned =1",null);
                        if (c.getCount()>0)
                        {
                            c.moveToFirst();
                            do {
                                myDb.execSQL("insert into history (barcode, product_name,transaction_id) " +
                                        "values (" +
                                        "\""+c.getString(1)+"\"," +
                                        "\""+c.getString(2)+"\"," +
                                        "\""+payment.getString(0)+"\"" +
                                        ")");
                                myDb.execSQL("delete from list where id='"+c.getString(0)+"'");
                                getFragmentManager().beginTransaction().replace(R.id.mainContainer, new HistoryFragment()).addToBackStack("HistoryFragment").commit();
                                Snackbar.make(v, "Checked Out Successfully !", Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                            }while (c.moveToNext());
                        }

                    }
                }
        );

        final int maxProducts = myCart.size();
        if(maxProducts != 0){
            changeUPC(myCart.get(0));
            String out = "1 / "+maxProducts;
            numProducts.setText(out);
        }
        else{
            numProducts.setText("No Items Scanned");
            barcodeImage.setBackgroundColor(Color.WHITE);
        }




        final GestureDetector gesture = new GestureDetector(getActivity(), new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onDown(MotionEvent e){
                return true;
            } //registers the users first type

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
                        if(currPos == 0){
                            currPos = maxProducts - 1;
                        }
                        else{
                            currPos--;
                        }
                        changeUPC(myCart.get(currPos));

                    } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {//swipe right
                                                /*
                        If the difference between the x values of the final position and initial position
                        of the swipe is greater than the minimum distance, and the swipe is fast enough register this as
                        a right swipe.

                         */
                        if(currPos == maxProducts - 1){
                            currPos = 0;
                        }
                        else{
                            currPos++;
                        }
                        changeUPC(myCart.get(currPos));


                    }
                } catch (Exception e) {
                    System.out.println(e); //hope not!
                }
                int pos = currPos + 1;
                if(pos >= 0 && maxProducts > 0){
                    String progress = pos + " / " +maxProducts;
                    numProducts.setText(progress);
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

    public void changeUPC(Product p){
        String code = p.barcode;
        int quant = p.quantity;
        String title = p.itemTitle;
        String quantity_text = "Number of Products: " + quant;
        quantity.setText(quantity_text);
        upcCode.setText(code);
        bitmapForBarcode = encodeBarcodeBitmap(code, 600, 300); //need to change BitMatrix to Bitmap
        barcodeImage.setImageBitmap(bitmapForBarcode);
        product.setText(title);
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

    /*
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


    protected class Product{ //small inner class to handle the Database results.
        String barcode;
        String itemTitle;
        int quantity;
        Product(String code, String title, int quant){
            barcode = code;
            itemTitle = title;
            quantity = quant;
        }
    }
}