package com.example.colini.mobilecomputingproject;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
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
//        if (navi_list[position].equalsIgnoreCase("Detail View")){
//            getSupportFragmentManager().beginTransaction().replace(R.id.mainContainer, new View_Detial()).addToBackStack("DetailFragment").commit();
//        }
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
                "barcode text," +
                "product_name char(255)," +
                "scanned int" +
                ")");
    }

    public void addNotification(final String barcode) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.additem_dialog, null);
        dialogBuilder.setView(dialogView);

        final EditText edt = (EditText) dialogView.findViewById(R.id.edit1);

        dialogBuilder.setTitle("Item Not Found");
        dialogBuilder.setMessage("The scanned item is not in our databases, do you want to add it?");
        dialogBuilder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Toast.makeText(MainActivity.this, "Added", Toast.LENGTH_LONG).show();
                mydatabase.execSQL("insert into list (product_name,barcode, scanned) values('" + edt.getText() + "','" + barcode + "',1)");
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Toast.makeText(MainActivity.this, "Canceled", Toast.LENGTH_LONG).show();
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent){
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null) {
            final String result = scanResult.getContents(); //this is the UPC code of the item scanned.
            upcCodes.add(result); //it's added to an ArrayList, instead we should add it to a database
            if(result != null){
                try {
                    JSONObject jsonResult = queryUPC(result);
                    String itemName;
                    boolean valid = jsonResult.getBoolean("valid"); //if true, then item is in UPC Database
                    System.out.println("First try valid = " + valid);
                    if(!valid){
                        //many products have an extra 0 in the upcAPI, lets try that....
                        String zeroCode = "0"+result;
                        JSONObject jsonZero = queryUPC(zeroCode);
                        valid = jsonZero.getBoolean("valid");
                        System.out.println("Zero code valid = "+valid);
                        if(!valid){
                        //it really isn't in the UPC DB.. we need to ask for it!
                            addNotification(result);
                        }
                        else{
                            itemName = jsonZero.getString("itemname");
                            Toast.makeText(getApplicationContext(), itemName, Toast.LENGTH_LONG).show();
                            processItem(itemName, zeroCode);
                        }
                    }
                    else {
                        itemName = jsonResult.getString("itemname");
                        Toast.makeText(getApplicationContext(), itemName, Toast.LENGTH_LONG).show();
                        processItem(itemName, result);
                    }
                }
                catch(JSONException e) {
                    System.out.println(e);
                }

            }
        // else continue with any other code you need in the method
        //...
         }
    }

    public void processItem(String itemName, String upcCode){
        final String item = itemName;

        Cursor c = mydatabase.rawQuery("select * from list where product_name='" + item + "' and scanned = 0;", null);
        System.out.println("Processing item... " + itemName + " " + upcCode);
        if (c.getCount() == 0) {
            // ASK THE USER IF HE WANTS TO ADD THIS ITEM TO THE LIST
            //removed the addNotification from here. We only enter this if the item is in the UPC Database.
           System.out.println("Count is zero, new item!");
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("New Item Found");

            builder.setMessage("The scanned item is not on your list, do you want to add it?");
            //Yes Button
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mydatabase.execSQL("insert into list (product_name, scanned) values('" + item + "',1)");
                    Toast.makeText(getApplicationContext(), "Item added!", Toast.LENGTH_LONG).show();
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
            Toast.makeText(getApplicationContext(), "Need to add to database", Toast.LENGTH_LONG).show();
        } else {
            // NOTIFY THE USER THAT THE ITEMS WITH THAT BARCODE HAVE BEEN SCANNED
            mydatabase.execSQL("update list set scanned=1 where product_name='" + item + "';");
            Toast.makeText(getApplicationContext(), "Item(s) scanned!", Toast.LENGTH_LONG).show();


        }
        //after this we should also refresh the list view...
        //getSupportFragmentManager().beginTransaction().replace(R.id.mainContainer, new ListFragment()).addToBackStack("ListFragment").commit();




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
            e.printStackTrace();
        }

        json = new JSONObject(K);
        return json;
    }

    public void refreshListView(){
        Fragment currentFragement = getSupportFragmentManager().findFragmentByTag("listTag");
        android.support.v4.app.FragmentTransaction fragTrans = getSupportFragmentManager().beginTransaction().addToBackStack("ListRefresh");
        fragTrans.detach(currentFragement);
        fragTrans.attach(currentFragement);
        fragTrans.commit();


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
}
