package com.example.colini.mobilecomputingproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

/**
 * Created by JoeyFarrell on 2016-03-12.
 */
public class ShoppingViewActivity extends Activity {

    IntentIntegrator i;

    TextView tv;
    Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        i = new IntentIntegrator(this);
        i.initiateScan();
        setContentView(R.layout.shoppingview_layout);

        tv = (TextView) findViewById(R.id.myTextView);
        btn = (Button) findViewById(R.id.myButton);

    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null) {
            System.out.println("Here");
            String result = scanResult.getContents();
            tv.setText(result);
        }
        // else continue with any other code you need in the method
        //...
    }

    public void scanAgain(){
        i.initiateScan();
    }

    public void home(){
        Intent intent = new Intent(ShoppingViewActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
