package com.example.colini.mobilecomputingproject;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.inputmethodservice.KeyboardView;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.support.v4.app.Fragment;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import java.util.ArrayList;


/**
 * Created by Colini on 16-03-20.
 */
public class searchFragment extends Fragment implements View.OnFocusChangeListener {
    Button searchButton;
    EditText searchfield;
    ArrayList<String> List=new ArrayList<String>();
    ArrayList<String> currentList=new ArrayList<String>();
    ArrayList<String> currentBarcodes=new ArrayList<String>();
    ArrayList<ImageView> addButtons=new ArrayList<ImageView>();
    SQLiteDatabase mydatabase;




    public void onFocusChange(View v, boolean hasFocus){

        if(!hasFocus) {

            InputMethodManager imm =  (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.search_layout,container,false);

        createSearchBar(rootView);
        mydatabase = getActivity().openOrCreateDatabase("scanAndShop", Context.MODE_PRIVATE,null);


        searchButton.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        LinearLayout ResultLayout = (LinearLayout) rootView.findViewById(R.id.results);
                        ResultLayout.removeAllViews();
                        ArrayList<String> result = retrieveSearchResult(searchfield.getText().toString());
                        int i = 0;
                        if (result.size()==0)
                        {
                            TextView temp= new TextView(getActivity());
                            temp.setText("Could not find the item");
                            ResultLayout.addView(temp);
                        }
                        for (String e : result)
                            ResultLayout.addView(createRow(e, currentBarcodes.get(i), i++));
                        View view = getActivity().getCurrentFocus();
                        if (view != null) {
                            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }
                        searchfield.clearFocus();
                    }
                });
        searchfield.setId(View.NO_ID);
        return rootView;
    }





    public ArrayList<String> retrieveSearchResult(String query)
    {

        ArrayList<String> results= new ArrayList<>();

        addButtons.clear();
        currentBarcodes.clear();



        // Force the Internet traffic to run on the same Main Thread..
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {

            org.jsoup.nodes.Document document = Jsoup.connect("http://upcdatabase.org/meta/instantsearch.php?payload="+query).get();
            Elements result=document.select(".result_title");
            Elements barcodes=document.select(".result_code");


            int i=0;
            for (org.jsoup.nodes.Element e:result)
            {
                if(e.text().equals("No title available")) continue;
                currentList.add(e.text());
                currentBarcodes.add(barcodes.get(i++).text());
                ImageView tempButton = new ImageView(getActivity());
                tempButton.setImageResource(R.drawable.add);
                //tempButton.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
                results.add(e.text());
                addButtons.add(tempButton);

            }

        } catch (Exception e) {
            //e.printStackTrace();

            //  TO DO  >>  Implement the history table;
            //  check if the product exists in the cache (HISTORY) first
            //




        }
        return results;
    }

    public void createSearchBar(View view)
    {
        LinearLayout L = (LinearLayout) view.findViewById(R.id.searchContainer);
        searchButton = new Button(getActivity());
        ImageView cancel=new ImageView(getActivity());
        LinearLayout.LayoutParams param=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        param.setMargins(0, 0, 0, 10);

        cancel.setImageResource(R.drawable.back);
        searchButton.setText("Search");
        searchfield = new EditText(getActivity());
        searchfield.setWidth(500);
        searchfield.setHint("Type an item name ..");
        searchfield.setSingleLine(true);
        searchfield.setHeight(45);
        searchfield.setPadding(0, 0, 0, 0);
        searchButton.setPadding(0, 0, 0, 0);



        cancel.setPadding(0, 0, 0, 0);
        cancel.setPadding(0, 0, 0, 0);

        cancel.setOnClickListener(

                new Button.OnClickListener() {
                    public void onClick(View v) {
                        View view = getActivity().getCurrentFocus();
                        if (view != null) {
                            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.mainContainer, new ListFragment()).addToBackStack("SearchFragment").commit();
                    }
                }

        );

        L.addView(cancel, param);
        L.addView(searchfield, param);
        L.addView(searchButton, param);

        GradientDrawable border = new GradientDrawable();
        border.setColor(0xFFFFFFFF); //white background
        border.setStroke(1, Color.GRAY); //black border with full opacity
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            L.setBackgroundDrawable(border);
        } else {
            L.setBackground(border);
        }
        L.setWeightSum(0);
        L.setLayoutParams(param);
    }

    public LinearLayout createRow(final String productName,final String barcode, int i)
    {
        LinearLayout Row= new LinearLayout(getActivity());
        Row.setOrientation(LinearLayout.HORIZONTAL);
        TextView product= new TextView(getActivity());
        LinearLayout.LayoutParams prams =new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        prams.setMargins(0, 10, 0, 10);

        product.setText(productName);
        product.setTextSize(16);
        product.setWidth(650);
        product.setPadding(20, 20, 20, 20);


        GradientDrawable border = new GradientDrawable();
        border.setColor(0xFFFFFFFF); //white background
        border.setStroke(1, Color.GRAY); //black border with full opacity
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            Row.setBackgroundDrawable(border);
        } else {
            Row.setBackground(border);
        }

        final int ii=i;
        addButtons.get(i).setOnClickListener(
                new Button.OnClickListener(){
                    public void onClick(View v)
                    {
                        mydatabase.execSQL("insert into list (product_name, barcode, scanned) values(\""+productName+"\",'"+barcode+"',0)");
                        Snackbar.make(v, productName+" has been added!", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                }
        );


        Row.addView(product);
        Row.addView(addButtons.get(i));
        Row.setLayoutParams(prams);
        Row.setGravity(Gravity.CENTER_VERTICAL);
        return Row;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
