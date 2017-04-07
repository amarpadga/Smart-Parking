package com.example.akhil.smartparking;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Dictionary;
import java.util.Hashtable;

/*
    This activity is the first activity that is displayed when the app is opened. It displays the
    current available parking spots denoted by the buttons "P1", "P2", etc. If the spot is not occupied,
    the button the button turns green, else the button turns red.
    An HTTP request is made to the server requesting all the parking spots' information, which is then
    parsed to update the information about the parking spots that are stored in a dictionary.
 */
public class MainActivity extends BaseActivity {
    private Dictionary<String, Integer> spacesMap;
    private static final String URL_DATA = "http://smart-parking-bruck.c9users.io:8081/parking_spots";

    private SharedPreferences mPreferences;

    /*
    This function shows the parking spots in action
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spacesMap = new Hashtable<>();
        spacesMap.put("P1", R.id.p1);
        spacesMap.put("P2", R.id.p2);
        spacesMap.put("P3", R.id.p3);
        spacesMap.put("P4", R.id.p4);
        spacesMap.put("P5", R.id.p5);
        spacesMap.put("P6", R.id.p6);
        spacesMap.put("P7", R.id.p7);
        spacesMap.put("P8", R.id.p8);
        spacesMap.put("R1", R.id.r1);
        spacesMap.put("R2", R.id.r2);
        spacesMap.put("R3", R.id.r3);
        spacesMap.put("R4", R.id.r4);
        spacesMap.put("R5", R.id.r5);
        spacesMap.put("R6", R.id.r6);

        /*
            Make a HTTP GET request for all the parking spots
         */
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_DATA,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String s) {
                        try {
                            // Iterate over the JSON array, parse, and get the key:value pairs
                            JSONArray jsonArray = new JSONArray(s);
                            for(int i = 0; i<jsonArray.length(); i++){
                                JSONObject obj = jsonArray.getJSONObject(i);
                                String name1 = obj.getString("name");
                                Boolean occupied = obj.getBoolean("occupied");

                                //If the spot is not occupied, the button the button turns green, else the button turns red.
                                if (!occupied) {
                                    final Button test = (Button) findViewById(spacesMap.get(name1));
                                    test.setBackgroundColor(Color.GREEN);
                                } else {
                                    final Button test = (Button) findViewById(spacesMap.get(name1));
                                    test.setBackgroundColor(Color.RED);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

        mPreferences = getSharedPreferences("CurrentUser", MODE_PRIVATE);

        final Button refresh = (Button) findViewById(R.id.refresh);

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent refreshIntent = new Intent(MainActivity.this, MainActivity.class);
                MainActivity.this.startActivity(refreshIntent);
            }
        });
    }

    /*
    * This function checks if the user is logged in.
    * If logged in, text appears on the screen showing the user is currently logged in to the session
    */
    @Override
    public void onResume() {
        super.onResume();
        if (mPreferences.contains("uid")) {
            final TextView textToDisplay = (TextView) findViewById(R.id.userText);
            textToDisplay.setText("Logged in as " +(mPreferences.getString("username","asdasd")));
        } else {
            final TextView textToDisplay = (TextView) findViewById(R.id.userText);
            textToDisplay.setText("No user is currently logged in");
        }
    }
}