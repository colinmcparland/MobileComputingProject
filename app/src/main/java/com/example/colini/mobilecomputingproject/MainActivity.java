package com.example.colini.mobilecomputingproject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
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
import java.util.List;

import se.walkercrou.places.GooglePlaces;
import se.walkercrou.places.Place;

public class MainActivity extends ActionBarActivity implements AdapterView.OnItemClickListener, LocationListener {
    //finished the view with fragment and set up a navi
    private DrawerLayout drawerLayout;
    private ListView listView;
    private String[] navi_list;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    SQLiteDatabase mydatabase;
    public static String currentView="";

    String locationName; //used to save the locations name in history
    boolean isInStore; //flag for if a user is in a store
    boolean gpsEnabled; //flag for if the gps provider is enabled
    boolean networkEnabled; //flag for if the network provider is enabled
    Location myLocation;
    LocationManager locationManager;
    double lat;
    double lon;

    boolean closedFromScan; //flag for if the scanner has been closed. The main activity will restart after the scanner, so we don't want to display the scanner again if were in a store
    GooglePlaces client; //Google Places client

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        getSupportFragmentManager().beginTransaction().replace(R.id.mainContainer,new SplashFragment()).addToBackStack("HistoryFragment").commit(); //start on this screen while we get the location
        client = new GooglePlaces("AIzaSyCSUGPn5OAK26WX5x9IbnnNoajQL2tn44w"); //create new Google Places client with given key
        getLocation(); // get the users location


        mydatabase = openOrCreateDatabase("scanAndShop", Context.MODE_PRIVATE,null);
        initDB();
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
        listView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, navi_list));
        listView.setOnItemClickListener(this);

        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


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
        if(navi_list[position].equalsIgnoreCase("Scan")){
            launchScanner();
        }
        if (navi_list[position].equalsIgnoreCase("History")){
            getSupportFragmentManager().beginTransaction().replace(R.id.mainContainer,new HistoryFragment()).addToBackStack("HistoryFragment").commit();
        }
        if (navi_list[position].equalsIgnoreCase("Create List")){
            getSupportFragmentManager().beginTransaction().replace(R.id.mainContainer, new ListFragment()).addToBackStack("ListFragment").commit();
        }
        if(navi_list[position].equalsIgnoreCase("Checkout")){
            Bundle b = new Bundle();
            b.putString("locationName", locationName);
            if(locationName == null){
                System.out.println("Location Name is null");
            }
            CheckoutFragment cof = new CheckoutFragment();
            cof.setArguments(b);
            getSupportFragmentManager().beginTransaction().replace(R.id.mainContainer, cof).addToBackStack("CheckoutFragment").commit();
        }



        drawerLayout.closeDrawer(findViewById(R.id.drawerList));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)){
            View view = getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
            return true;
        }
        if (item.getItemId() == R.id.action_settings){
            //Do things here
            //You can delete the print and toast message.

            Toast.makeText(getApplicationContext(), "Search button pressed", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }

    public void initDB()
    {
        mydatabase.execSQL("create table if not exists list (" +
                "id INTEGER PRIMARY KEY   AUTOINCREMENT ," +
                "barcode text," +
                "product_name char(255)," +
                "scanned int" +
                ")");
        mydatabase.execSQL("create table if not exists history (" +
                "id INTEGER PRIMARY KEY   AUTOINCREMENT ," +
                "barcode text," +
                "product_name char(255)," +
                "transaction_id text" +
                ")");
        mydatabase.execSQL("create table if not exists payment (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "store_name text," +
                "time datetime" +
                ")");
    }

    public void addNotification(final String barcode) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.additem_dialog, null);
        dialogBuilder.setView(dialogView);

        final EditText edt = (EditText) dialogView.findViewById(R.id.edit1);

        dialogBuilder.setTitle("Item Not Found");
        dialogBuilder.setMessage("The scanned item is not on our databases, do you want to add it?");
        dialogBuilder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                mydatabase.execSQL("insert into list (product_name,barcode, scanned) values('" + edt.getText() + "','" + barcode + "',1)");
                getSupportFragmentManager().beginTransaction().replace(R.id.mainContainer, new ListFragment()).commit();
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent){
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null) {
            final String result = scanResult.getContents(); //this is the UPC code of the item scanned.
            if (result != null) {
                try {
                    JSONObject jsonResult = queryUPC(result);
                    String itemName;
                    boolean valid = jsonResult.getBoolean("valid"); //if true, then item is in UPC Database
                    System.out.println("First try valid = " + valid);
                    if (!valid) {
                        //many products have an extra 0 in the upcAPI, lets try that....
                        String zeroCode = "0" + result;
                        JSONObject jsonZero = queryUPC(zeroCode);
                        valid = jsonZero.getBoolean("valid");
                        System.out.println("Zero code valid = " + valid);
                        if (!valid) {
                            //it really isn't in the UPC DB.. we need to ask for it!
                            addNotification(result);
                        } else {
                            itemName = jsonZero.getString("itemname");
                            Toast.makeText(getApplicationContext(), itemName, Toast.LENGTH_LONG).show();
                            processItem(itemName, zeroCode);
                        }
                    } else {
                        itemName = jsonResult.getString("itemname");
                        Toast.makeText(getApplicationContext(), itemName, Toast.LENGTH_LONG).show();
                        processItem(itemName, result);
                    }
                } catch (JSONException e) {
                    System.out.println(e);
                }

            }
            // else continue with any other code you need in the method
            //...
            closedFromScan = true;
        }
    }

    public void processItem(String itemName, final String upcCode){
        final String item = itemName;
        //check to see if this item is on the list and not scanned
        Cursor c = mydatabase.rawQuery("select * from list where product_name='" + item + "' and scanned = 0;", null);
        if (c.getCount() == 0) { //if its not on the list
            // ASK THE USER IF HE WANTS TO ADD THIS ITEM TO THE LIST
            //removed the addNotification from here. We only enter this if the item is in the UPC Database.
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("New Item Found");

            builder.setMessage("The scanned item is not on your list, do you want to add it?");
            //Yes Button
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mydatabase.execSQL("insert into list (product_name, barcode, scanned) values('" + item + "','" + upcCode + "',1)");
                    getSupportFragmentManager().beginTransaction().replace(R.id.mainContainer, new ListFragment()).commit();

                }
            });

            //No Button
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog msg = builder.create();
            msg.show();
        } else {
            // NOTIFY THE USER THAT THE ITEMS WITH THAT BARCODE HAVE BEEN SCANNED
            mydatabase.execSQL("update list set scanned=1 where product_name='" + item + "';");

            //getSupportFragmentManager().beginTransaction().replace(R.id.mainContainer, new ListFragment()).commit();
        }
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
            while ( (line = br.readLine()) != null) {
                K += line;
            }
            br.close();
            is.close();

        } catch (Exception e) {
            //e.printStackTrace();
//            Cursor c = mydatabase.rawQuery("select * from history where barcode = '"+barcode+"'", null);
//            if (c.getCount()==0)
//            {
//                c.moveToFirst();
//                K="{" +
//                        "\"valid\":\"true\"," +
//                        "\"number\":\""+barcode+"\"," +
//                        "\"itemname\":\""+c.getString(2)+"\"," +
//                        "\"alias\":\"\"," +
//                        "\"description\":\"\"," +
//                        "\"avg_price\":\"\"," +
//                        "\"rate_up\":0," +
//                        "\"rate_down\":0" +
//                        "}";
//            }

        }

        if (K.equals(""))
        {
            Cursor c = mydatabase.rawQuery("select * from history where barcode = '"+barcode+"'", null);
            Cursor d = mydatabase.rawQuery("select * from list where  barcode = '"+barcode+"'",null);
            if (c.getCount()>0)
            {
                c.moveToFirst();
                K="{" +
                        "\"valid\":\"true\"," +
                        "\"number\":\""+barcode+"\"," +
                        "\"itemname\":\""+c.getString(2)+"\"," +
                        "\"alias\":\"\"," +
                        "\"description\":\"\"," +
                        "\"avg_price\":\"\"," +
                        "\"rate_up\":0," +
                        "\"rate_down\":0" +
                        "}";
            }
            else
            {
               if (d.getCount()>0)
               {
                   d.moveToFirst();
                   K="{" +
                           "\"valid\":\"true\"," +
                           "\"number\":\""+barcode+"\"," +
                           "\"itemname\":\""+d.getString(2)+"\"," +
                           "\"alias\":\"\"," +
                           "\"description\":\"\"," +
                           "\"avg_price\":\"\"," +
                           "\"rate_up\":0," +
                           "\"rate_down\":0" +
                           "}";
               }
            }
        }


        json = new JSONObject(K);
        return json;
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

    @Override
    public void onBackPressed() {
        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
        if (fm.getBackStackEntryCount() >0){
            fm.popBackStack();
            // getSupportFragmentManager().beginTransaction().replace(R.id.mainContainer, fm.popBackStack()).addToBackStack("DetailFragment").commit();
            System.out.println("POP UP");


        }else{
            super.onBackPressed();
        }
    }

    public void getLocation(){
        try{
            locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE); //create the LocationManager
            gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER); //check to see if GPS Provider is enabled
            networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER); //check to see if Network provider is enabled

            int checkPermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION); //check to see if we can have permission
            if(checkPermission != PackageManager.PERMISSION_GRANTED){
                //ask for user's permission
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1); //ask for it
            }

            if(!networkEnabled && !gpsEnabled){
                //cannot get location view network or gps..
                //need to handle
                determineInitialView();
            }
            else {
                if (networkEnabled) { //first check for location via network
                    locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, this, null);
                    if (locationManager != null) {
                        myLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (myLocation != null) {
                            lat = myLocation.getLatitude();
                            lon = myLocation.getLongitude();
                        }
                    }
                }
                if (gpsEnabled) { //then check with GPS
                    locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, this, null);
                    if (locationManager != null) {
                        myLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (myLocation != null) {
                            lat = myLocation.getLatitude();
                            lon = myLocation.getLongitude();
                        }
                    }
                }
                getGooglePlacesResult();
                locationManager.removeUpdates(this); //stop GPS Updates to save battery

            }

        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public void determineInitialView(){
        System.out.println(isInStore);
        if(isInStore){
            //shopping view
            launchScanner();
        }
        else{
            //list view
            getSupportFragmentManager().beginTransaction().replace(R.id.mainContainer, new ListFragment()).addToBackStack("ListFragment").commit();

        }
    }

    /*
        These functions are here to comply with LocationListener. We do not actually use them.
         */
    @Override
    public void onLocationChanged(Location location) {
        myLocation = location;
        lat = myLocation.getLatitude();
        lon = myLocation.getLongitude();
        getGooglePlacesResult();
        determineInitialView();
    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!closedFromScan) {
            getLocation();
            closedFromScan = true;
        }
        else{
            getSupportFragmentManager().beginTransaction().replace(R.id.mainContainer, new ListFragment()).addToBackStack("ListFragment").commit();

        }
    }

    public void getGooglePlacesResult(){
        new AsyncTask<Void, Void, Void>() { //need to do this in the background
            @Override
            protected Void doInBackground(Void... params) {
                List<Place> places = client.getNearbyPlaces(lat, lon, 25); //get all the places within 25 meters of our location
                int placesCount = places.size();
                for (int i = 0; i < placesCount; i++) { //for each place
                    Place currentPlace = places.get(i);
                    String name = currentPlace.getName();
                    List<String> types = currentPlace.getTypes(); //get the types of the place, there may be multiple
                    int typeCount = types.size();
                    for (int j = 0; j < typeCount; j++) { //for each type
                        String temp = types.get(j); //get the type
                        if (temp.equalsIgnoreCase("store") || temp.equalsIgnoreCase("store")) { //check to see if Google thinks the Place is a store
                            isInStore = true;
                            locationName = name;
                            return null;
                        }
                    }
                }
                isInStore = false;
                locationName = "";
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) { //called after the Async Task completes
                determineInitialView();
                super.onPostExecute(aVoid);
            }
        }.execute();
    }

    public void launchScanner(){
        int checkPermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA); //check to see if we can have permission
        if(checkPermission != PackageManager.PERMISSION_GRANTED){
            //ask for user's permission
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, 1); //ask for it
        }
        IntentIntegrator i = new IntentIntegrator(this); //from ZXing, launches and Intent with the scanner
        i.setDesiredBarcodeFormats(IntentIntegrator.PRODUCT_CODE_TYPES); //we're only looking for products
        i.setCaptureLayout(R.layout.scanner_layout); //custom layout to add cancle button
        i.addExtra("closedFromScan", true); //flag so we don't enter a loop
        i.initiateScan(); //start scan
    }
}
