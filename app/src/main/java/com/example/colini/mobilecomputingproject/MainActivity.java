package com.example.colini.mobilecomputingproject;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.jar.Manifest;

public class MainActivity extends ActionBarActivity implements AdapterView.OnItemClickListener {
    //finished the view with fragment and set up a navi
    private DrawerLayout drawerLayout;
    private ListView listView;
    private String[] navi_list;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private int shortToast_time = 500;
    private Bundle bundle;
    private int codesLength = 0;
    private ArrayList <String> upcCodes;
    SQLiteDatabase mydatabase;
    public static String currentView="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bundle = new Bundle();
        getSupportFragmentManager().beginTransaction().replace(R.id.mainContainer,new mainFragment()).addToBackStack("mainFragment").commit();

        mydatabase = openOrCreateDatabase("scanAndShop", Context.MODE_PRIVATE,null);
        initDB();
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        listView = (ListView) findViewById(R.id.drawerList);
        navi_list = getResources().getStringArray(R.array.navi_list);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,R.string.on,R.string.on){
            @Override
            public void onDrawerOpened(View drawerView) {
                //Toast.makeText(getApplicationContext(),"Navi is On",Toast.LENGTH_SHORT).show();
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                //Toast.makeText(getApplicationContext(),"Navi is Off",Toast.LENGTH_SHORT).show();
                super.onDrawerClosed(drawerView);
            }
        };
        listView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, navi_list));
        listView.setOnItemClickListener(this);




        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        upcCodes = new ArrayList<>();
        mydatabase = openOrCreateDatabase("scanAndShop", MODE_PRIVATE ,null);


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
            getSupportFragmentManager().beginTransaction().replace(R.id.mainContainer, new View_Detial()).addToBackStack("DetailFragment").commit();
        }
        if(navi_list[position].equalsIgnoreCase("Shopping View")){
            IntentIntegrator i = new IntentIntegrator(this); //between this line and i.initiateScan() we can edit the Scanner
            i.setDesiredBarcodeFormats(IntentIntegrator.PRODUCT_CODE_TYPES);
            i.initiateScan();
        }
        if (navi_list[position].equalsIgnoreCase("History View")){
            getSupportFragmentManager().beginTransaction().replace(R.id.mainContainer,new HistoryFragment()).addToBackStack("HistoryFragment").commit();
        }
        if (navi_list[position].equalsIgnoreCase("List View")){
            getSupportFragmentManager().beginTransaction().replace(R.id.mainContainer, new ListFragment()).addToBackStack("ListFragment").commit();
        }
        if(navi_list[position].equalsIgnoreCase("Checkout View")){
            if(upcCodes != null){
            bundle.putStringArrayList("upcCodes", upcCodes);
            CheckoutFragment cof = new CheckoutFragment();
            cof.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.mainContainer, cof).addToBackStack("CheckoutFragment").commit();}
            else{
                System.out.println("No ArrayList");
            }
        }

        drawerLayout.closeDrawer(findViewById(R.id.drawerList));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        if (item.getItemId() == R.id.action_settings){
            //Do things here
            //You can delete the print and toast message.

            Toast.makeText(getApplicationContext(),"Search button pressed",Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }

    public void initDB()
    {
        mydatabase.execSQL("create table if not exists list (" +
                "id INTEGER PRIMARY KEY   AUTOINCREMENT ," +
                "product_name char(255)," +
                "scanned int" +
                ")");
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent){
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null) {
            String result = scanResult.getContents(); //this is the UPC code of the item scanned.
            upcCodes.add(result); //it's added to an ArrayList, instead we should add it to a database

            try {
                JSONObject json = queryUPC(result);
                /* check if valid first, if not valid it's not in the UPC database..
                   if it's not in the UPC database we should save it. Maybe ask the user for
                   information on the product to expand our application?
                */
                Cursor c = mydatabase.rawQuery("select * from list where product_name='"+json.getString("itemname")+" and scanned = 0';",null);
                if (c.getCount()==0)
                {
                    // ASK THE USER IF HE WANTS TO ADD THIS ITEM TO THE LIST

                    // IF (YES){
                    mydatabase.execSQL("insert into list (product_name, scanned) values('"+json.getString("itemname")+"',1)");
                    //}
                }else
                {
                    mydatabase.execSQL("update list set scanned=1 where product_name='"+json.getString("itemname")+"';");
                    // NOTIFY THE USER THAT THE ITEMS WITH THAT BARCODE HAVE BEEN SCANNED

                }

            }catch(JSONException e)
            {

            }
        }
        // else continue with any other code you need in the method
        //...
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

    private Detail_Data detail_data;
    public void setDetail(Detail_Data x){
        detail_data = x;
    }
    public Detail_Data getDetail_data(){
        return detail_data;
    }
    public SQLiteDatabase getDatabase(){return mydatabase;}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        if (currentView.equals("add")) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_main, menu);
            return super.onCreateOptionsMenu(menu);
        }
        else
        {
            return false;
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }
}
