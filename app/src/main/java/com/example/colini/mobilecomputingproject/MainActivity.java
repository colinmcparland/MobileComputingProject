package com.example.colini.mobilecomputingproject;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private DrawerLayout drawerLayout;
    private ListView listView;
    private String[] navi_list;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        listView = (ListView) findViewById(R.id.drawerList);
        navi_list = getResources().getStringArray(R.array.navi_list);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,R.string.on,R.string.on){
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };

        listView.setAdapter(new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,navi_list));

        listView.setOnItemClickListener(this);
        //getSupportActionBar().setHomeButtonEnabled(true);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        System.out.println(navi_list[position]);

        if (navi_list[position].equalsIgnoreCase("Detail")){
            getSupportFragmentManager().beginTransaction().replace(R.id.mainContainer,new View_Detial()).commit();
        }
    }
}
