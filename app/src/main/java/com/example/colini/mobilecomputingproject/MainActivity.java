package com.example.colini.mobilecomputingproject;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.jar.Manifest;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    //finished the view with fragment and set up a navi
    private DrawerLayout drawerLayout;
    private ListView listView;
    private String[] navi_list;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private int shortToast_time = 500;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    String code;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportFragmentManager().beginTransaction().replace(R.id.mainContainer,new mainFragment()).commit();

        Intent i = getIntent();
        code = i.getStringExtra("code");
        if(code != null){
            TextView tv = (TextView) findViewById(R.id.textView);
            tv.setText(code);
        }

        int permission = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }

        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        listView = (ListView) findViewById(R.id.drawerList);
        navi_list = getResources().getStringArray(R.array.navi_list);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,R.string.on,R.string.on){
            @Override
            public void onDrawerOpened(View drawerView) {
                Toast.makeText(getApplicationContext(),"Navi is On",Toast.LENGTH_SHORT).show();
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                Toast.makeText(getApplicationContext(),"Navi is Off",Toast.LENGTH_SHORT).show();
                super.onDrawerClosed(drawerView);
            }
        };
        listView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, navi_list));
        listView.setOnItemClickListener(this);

        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        System.out.println(navi_list[position]);
        /*
        * You can load your view with the code here,
        * add xml and java class
        * go to string.xml file to check out the string-array entry.
        *
        * */
        if (navi_list[position].equalsIgnoreCase("Detail View")){
            getSupportFragmentManager().beginTransaction().replace(R.id.mainContainer, new View_Detial()).commit();
        }
        if(navi_list[position].equalsIgnoreCase("Shopping View")){
            Intent i = new Intent(MainActivity.this, ShoppingViewActivity.class);
            startActivity(i);
        }
        if (navi_list[position].equalsIgnoreCase("History View")){
            getSupportFragmentManager().beginTransaction().replace(R.id.mainContainer,new HistoryFragment()).commit();
        }
        if (navi_list[position].equalsIgnoreCase("List View")){
            getSupportFragmentManager().beginTransaction().replace(R.id.mainContainer, new ListFragment()).commit();
        }

        drawerLayout.closeDrawer(findViewById(R.id.drawerList));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
