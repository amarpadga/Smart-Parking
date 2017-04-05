package com.example.akhil.smartparking;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
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
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;

public class MainActivity extends BaseActivity {
    private Dictionary<String, Integer> spacesMap;
    private static final String URL_DATA = "http://smart-parking-bruck.c9users.io:8081/parking_spots";

    private SharedPreferences mPreferences;

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

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_DATA,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String s) {
                        try {
                            JSONArray jsonArray = new JSONArray(s);
                            for(int i = 0; i<jsonArray.length(); i++){
                                JSONObject obj = jsonArray.getJSONObject(i);
                                String name1 = obj.getString("name");
                                Boolean occupied = obj.getBoolean("occupied");
                                Boolean reservable = obj.getBoolean("reservable");
                                Boolean reserved = obj.getBoolean("reserved");

                                System.out.println(name1);
                                if (occupied == false) {
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
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mPreferences.contains("uid")) {
            System.out.println("User login is successful");
            final TextView test = (TextView) findViewById(R.id.userText);
            test.setText("Logged in as " +(mPreferences.getString("username","asdasd")));
        } else {
            System.out.println("User not logged in");
        }
    }
}