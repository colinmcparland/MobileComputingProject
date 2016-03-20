package com.example.colini.mobilecomputingproject;

/**
 * Created by Zaland Khan on 2016-03-06.
 */

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ListActivity extends AppCompatActivity {
    ListView a;
    String[] list = {"Eggs", "Milk", "Bread", "Chicken", "Lettuce", "Orange Juice", "Apple Pie"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_main);

        //a = (ListView) findViewById(R.id.listview);
        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
        //a.setAdapter(adapter);
    }
}
