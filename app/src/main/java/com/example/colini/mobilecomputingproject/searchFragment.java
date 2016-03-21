package com.example.colini.mobilecomputingproject;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
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
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.support.v4.app.Fragment;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import java.util.ArrayList;


/**
 * Created by Colini on 16-03-20.
 */
public class searchFragment extends Fragment {
    Button searchButton;
    EditText searchfield;
    ArrayList<String> List=new ArrayList<String>();
    ArrayList<String> currentList=new ArrayList<String>();
    ArrayList<Button> addButtons=new ArrayList<Button>();
    SQLiteDatabase mydatabase;

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
                        for (String e : result)
                            ResultLayout.addView(createRow(e, i++));
                    }
                });


        return rootView;
    }

    public ArrayList<String> retrieveSearchResult(String query)
    {
        ArrayList<String> results= new ArrayList<>();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        try {

            org.jsoup.nodes.Document document = Jsoup.connect("http://upcdatabase.org/meta/instantsearch.php?payload="+query).get();
            Elements result=document.select(".result_title");
            addButtons.clear();

            for (org.jsoup.nodes.Element e:result)
            {
                if(e.text().equals("No title available")) continue;
                currentList.add(e.text());
                Button tempButton = new Button(getActivity());
                tempButton.setText("Add");
                tempButton.setHeight(LinearLayout.LayoutParams.MATCH_PARENT);
                tempButton.setBackgroundColor(Color.rgb(255, 204, 204));
                //tempButton.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
                results.add(e.text());
                addButtons.add(tempButton);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }

    public void createSearchBar(View view)
    {
        LinearLayout L = (LinearLayout) view.findViewById(R.id.searchContainer);
        searchButton = new Button(getActivity());
        LinearLayout.LayoutParams param=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        param.setMargins(0,0,0,10);
        searchButton.setText("Search");
        searchfield = new EditText(getActivity());
        searchfield.setWidth(600);
        searchfield.setHint("Type an item name ..");
        searchfield.setSingleLine(true);
        searchfield.setHeight(45);
        searchfield.setPadding(0, 0, 0, 0);
        searchButton.setPadding(0, 0, 0, 0);
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

    public LinearLayout createRow(final String productName, int i)
    {
        LinearLayout Row= new LinearLayout(getActivity());
        Row.setOrientation(LinearLayout.HORIZONTAL);
        TextView product= new TextView(getActivity());
        LinearLayout.LayoutParams prams =new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        prams.setMargins(0, 10, 0, 10);

        product.setText(productName);
        product.setTextSize(16);
        product.setWidth(600);
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
                        mydatabase.execSQL("insert into list (product_name, scanned) values('"+productName+"',0)");
                        //List.add(currentList.get(ii));
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
