package com.example.colini.mobilecomputingproject;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;


/**
 * Created by Yang on 16-03-06.
 */
public class View_Detial extends Fragment {
    SQLiteDatabase mydatabase;
    private Button button;
    private ImageView imageView;
    private TextView textView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.view_detail,container,false);
        button = (Button) rootView.findViewById(R.id.DetailButton);
        textView = (TextView) rootView.findViewById(R.id.textView2);
        button.setText("Delete");

        MainActivity myaty = (MainActivity) getActivity();

        Detail_Data x = myaty.getDetail_data();

        // grab id of element from the bundle that was sent by the previous fragment
        final int id = getArguments().getInt("ID");

        // connect to the database and run the query to get the barcode and name
        mydatabase = getActivity().openOrCreateDatabase("scanAndShop", Context.MODE_PRIVATE,null);
        Cursor cursor = mydatabase.rawQuery("select * from list where id=" + id, null);
        cursor.moveToFirst();

        // store the barcode and then grab the json object, finally grab the description from it
        String barcode = cursor.getString(1);
        String desc = "";
        try {
            JSONObject json = queryUPC(barcode);
            desc = json.getString("description");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // center the text
        textView.setGravity(Gravity.CENTER);

        // prepare the string to print
        String temp = "<b>Barcode Number</b><br>" + barcode +
                "<br><br><b>Name</b><br>" + cursor.getString(2) +
                "<br><br><b>Description</b><br>" + desc;

        // print the information depending on what is in the database
        if(cursor.getCount() != 0)
            textView.setText(Html.fromHtml(temp));
        else
            textView.setText("No value exists in database");

        // delete this element when its clicked on
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mydatabase.execSQL("delete from list where id = " + id);
                getFragmentManager().beginTransaction().replace(R.id.mainContainer, new ListFragment()).commit();
            }
        });

        return rootView;
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
}
