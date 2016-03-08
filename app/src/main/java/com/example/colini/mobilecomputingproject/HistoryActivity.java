package com.example.colini.mobilecomputingproject;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class HistoryActivity extends AppCompatActivity {

    SQLiteDatabase mydatabase ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("History");
        setSupportActionBar(toolbar);

        mydatabase = openOrCreateDatabase("scanAndShop",MODE_PRIVATE,null);



        LinearLayout LL = (LinearLayout) findViewById(R.id.mainContainer);

        initDB();
        Cursor dbElements = mydatabase.rawQuery("select * from history", null);
        if (dbElements.getCount()==0)
        {
            LL.addView(creatRow("You haven't purchased any item yet!",""));
            //return;
        }



        LinearLayout hdr=createHeader("1", "SuperStore Halifax", "2016-02-02 03:21", "$28.2");
        LL.addView(hdr);

        for (int i=0; i<7; i++) {
            LinearLayout row = creatRow("Toilet Paper", "$10");
            LL.addView(row);
        }

        LinearLayout hdr2=createHeader("2", "SuperStore Halifax", "2016-02-02 03:21", "$28.2");
        LL.addView(hdr2);
        for (int i=0; i<7; i++) {
            LinearLayout row = creatRow("Toilet Paper", "$10");
            LL.addView(row);
        }
    }

    public LinearLayout creatRow(String productName, String price)
    {
        LinearLayout Row= new LinearLayout(this);
        Row.setOrientation(LinearLayout.VERTICAL);

        TextView product= new TextView(this), Price = new TextView(this);

        product.setText(productName);
        Price.setText(price);

        product.setPadding(20, 10, 0, 0);
        Price.setPadding(30, 10, 0, 10);

        GradientDrawable border = new GradientDrawable();
        border.setColor(0xFFFFFFFF); //white background
        border.setStroke(1, Color.GRAY); //black border with full opacity
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            Row.setBackgroundDrawable(border);
        } else {
            Row.setBackground(border);
        }


        Row.addView(product);
        Row.addView(Price);

        return Row;
    }


    public LinearLayout createHeader(String id, String storeName, String dateAndTime, String total)
    {
        LinearLayout Header=new LinearLayout(this);
        Header.setOrientation(LinearLayout.HORIZONTAL);
        Header.setBackgroundColor(Color.GRAY);


        LinearLayout.LayoutParams headerParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        headerParams.setMargins(0,20,0,0);
        GradientDrawable border = new GradientDrawable();
        border.setColor(Color.GRAY); //white background
        border.setStroke(1, Color.GRAY); //black border with full opacity
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            Header.setBackgroundDrawable(border);
        } else {
            Header.setBackground(border);
        }


        LinearLayout innerHeaderLeft= new LinearLayout(this),
                innerHeaderRight= new LinearLayout(this);

        innerHeaderLeft.setOrientation(LinearLayout.VERTICAL);
        innerHeaderRight.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        params.width=400;
        params.height=LinearLayout.LayoutParams.WRAP_CONTENT;

        TextView ID, store, dAt,Total;
        ID=new TextView(this);
        store=new TextView(this);
        dAt=new TextView(this);
        Total=new TextView(this);

        ID.setText(id);
        store.setText(storeName);
        dAt.setText(dateAndTime);
        Total.setText(total);

        ID.setTextColor(Color.WHITE);
        store.setTextColor(Color.WHITE);
        dAt.setTextColor(Color.WHITE);
        Total.setTextColor(Color.WHITE);

        ID.setTextSize(16);
        store.setTextSize(16);
        dAt.setTextSize(16);
        Total.setTextSize(16);

        innerHeaderLeft.addView(ID);
        innerHeaderLeft.addView(store);
        innerHeaderRight.addView(dAt);
        innerHeaderRight.addView(Total);

        innerHeaderLeft.setPadding(10, 10, 10, 10);
        innerHeaderRight.setPadding(10,10,10,10);

        Header.addView(innerHeaderLeft,params);
        Header.addView(innerHeaderRight,params);
        Header.setLayoutParams(headerParams);

        return Header;


    }


    public void initDB()
    {
        mydatabase.execSQL("CREATE TABLE if not exists`history` (" +
                "`id` int(10) NOT NULL," +
                "  `transactionId` int(20) NOT NULL," +
                "  `pName` char(255) NOT NULL," +
                "  `price` float(4,2) NOT NULL" +
                ")");
    }

}
