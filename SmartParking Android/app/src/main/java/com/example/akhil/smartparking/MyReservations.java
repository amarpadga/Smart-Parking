package com.example.akhil.smartparking;

import android.os.Bundle;
import android.widget.*;

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
import java.util.List;
import java.util.ArrayList;

/*
    This activity is used to display the parking reservations a user has made.
    An HTTP GET request is made to the server requesting all the reservations, which is then parsed into a
    dictionary. This dictionary is then iterated over to populate a list for the user to view
 */
public class MyReservations extends BaseActivity {

    private static final String URL_DATA_PATH = "http://smart-parking-bruck.c9users.io:8081/";
    private static final String RESERVATION_PATH = "reservations/";
    private static final String PARKING_SPOTS_PATH = "parking_spots/";

    private static final String RESERVATION_FIELD_FROM = "from";
    private static final String RESERVATION_FIELD_TO = "to";
    private static final String RESERVATION_FIELD_PARKING_SPOT_ID = "parking_spot_id";
    private static final String RESERVTION_FIELD_OID = "$oid";

    private static final String PARKING_SPACE_FIELD_NAME = "name";

    private static final String KEY_TIME_FROM = "from";
    private static final String KEY_TIME_TO = "to";
    private static final String KEY_PARKING_SPACE_NAME = "parking_space";
    private static final String KEY_PARKING_SPACE_ID = "parking_space_id";

    private static final String TEXT_PARKING_SPACE = "Parking Space: ";
    private static final String TEXT_FROM = "From: ";
    private static final String TEXT_TO = "To: ";
    private static final String TEXT_NEW_LINE = "\n";

    private List<Dictionary<String, Object>> reservations;

    private List<String> userReservationsList;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_reservations);

        reservations = new ArrayList<>();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_DATA_PATH + RESERVATION_PATH,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String s) {
                        try {
                            JSONArray jsonArray = new JSONArray(s);
                            for(int i = 0; i<jsonArray.length(); i++){
                                JSONObject obj = jsonArray.getJSONObject(i);
                                String beginTime = obj.getString(RESERVATION_FIELD_FROM);
                                String endTime = obj.getString(RESERVATION_FIELD_TO);
                                String parkingSpaceId = obj.getJSONObject(RESERVATION_FIELD_PARKING_SPOT_ID).getString(RESERVTION_FIELD_OID);

                                Dictionary<String, Object> reservation = new Hashtable<>();
                                reservation.put(KEY_TIME_FROM, beginTime);
                                reservation.put(KEY_TIME_TO, endTime);
                                reservation.put(KEY_PARKING_SPACE_ID, parkingSpaceId);

                                reservations.add(reservation);
                            }
                            addReservationsToList();
                            
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        System.out.println("Point A");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                    }
                });


        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void addReservationsToList(){
        userReservationsList = new ArrayList<>();
        adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, userReservationsList);

        ListView reservationsContainer = (ListView) findViewById(R.id.list);
        reservationsContainer.setAdapter(adapter);

        for(final Dictionary reservationObject : reservations) {
            if(reservationObject.get(KEY_PARKING_SPACE_ID) != null) {
                System.out.println("Path: " + URL_DATA_PATH + PARKING_SPOTS_PATH + reservationObject.get(KEY_PARKING_SPACE_ID));
                StringRequest parkingSpotRequest = new StringRequest(Request.Method.GET, URL_DATA_PATH + PARKING_SPOTS_PATH + reservationObject.get(KEY_PARKING_SPACE_ID),
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String s) {
                                try {
                                        JSONObject obj = new JSONObject(s);
                                        String parkingName = obj.getString(PARKING_SPACE_FIELD_NAME);

                                        reservationObject.put(KEY_PARKING_SPACE_NAME, parkingName);
                                    String reservationItem = TEXT_PARKING_SPACE + reservationObject.get(KEY_PARKING_SPACE_NAME) + TEXT_NEW_LINE
                                            + TEXT_FROM + reservationObject.get(KEY_TIME_FROM) + TEXT_NEW_LINE
                                            + TEXT_TO + reservationObject.get(KEY_TIME_TO) + TEXT_NEW_LINE;

                                    adapter.add(reservationItem);
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
                requestQueue.add(parkingSpotRequest);
            }
        }
    }
}
