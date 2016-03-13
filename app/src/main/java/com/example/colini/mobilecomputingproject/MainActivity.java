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

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.jar.Manifest;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    //finished the view with fragment and set up a navi
    private DrawerLayout drawerLayout;
    private ListView listView;
    private String[] navi_list;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private int shortToast_time = 500;
    private Bundle bundle;
    private int codesLength = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bundle = new Bundle();
        getSupportFragmentManager().beginTransaction().replace(R.id.mainContainer,new mainFragment()).commit();

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
            IntentIntegrator i = new IntentIntegrator(this); //between this line and i.initiateScan() we can edit the Scanner
            i.initiateScan();
        }
        if (navi_list[position].equalsIgnoreCase("History View")){
            getSupportFragmentManager().beginTransaction().replace(R.id.mainContainer,new HistoryFragment()).commit();
        }
        if (navi_list[position].equalsIgnoreCase("List View")){
            getSupportFragmentManager().beginTransaction().replace(R.id.mainContainer, new ListFragment()).commit();
        }
        if(navi_list[position].equalsIgnoreCase("Cart View")){
            bundle.putInt("codesLength", codesLength); //incase there are no scans...
            CartViewFragment cvf = new CartViewFragment();
            cvf.setArguments(bundle);

            getSupportFragmentManager().beginTransaction().replace(R.id.mainContainer, cvf).commit();
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

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null) {
            String result = scanResult.getContents();
            System.out.println(result);

            String key = "code" + codesLength;
            bundle.putString(key, result);
            codesLength++;
        }
        // else continue with any other code you need in the method
        //...
    }


}
