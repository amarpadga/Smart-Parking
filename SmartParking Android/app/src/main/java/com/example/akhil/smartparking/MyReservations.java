package com.example.akhil.smartparking;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
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

import java.net.URL;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.ArrayList;

import android.view.View;
/*
    This activity is used to display the parking reservations a user has made.
    An HTTP GET request is made to the server requesting all the reservations of the user which is logged in, which
    is then parsed into a dictionary. This dictionary is then iterated over to populate a list for the user to view.
    The image of the QR Code associated with each reservation is also retrieved and loaded for the user.
 */
public class MyReservations extends BaseActivity{

    private static final String SERVER_PATH = "http://smart-parking-bruck.c9users.io:8081/";
    private static final String USER_PATH = "users/";
    private static final String RESERVATION_PATH = "reservations/";
    private static final String PARKING_SPOTS_PATH = "parking_spots/";


    // KEY constants are identifiers used to extract items from JSON objects or to store values in a dictionary
    private static final String KEY_FROM = "from";
    private static final String KEY_TO = "to";
    private static final String KEY_PARKING_SPOT_ID = "parking_spot_id";
    private static final String KEY_OID = "$oid";
    private static final String KEY_QR_CODE_PATH = "qr_code_path";

    private static final String KEY_NAME = "name";

    private static final String KEY_PARKING_SPACE_NAME = "parking_space";
    private static final String KEY_PARKING_SPACE_ID = "parking_space_id";

    // FIELD constants are the text identifiers that users will see on the reservations list
    private static final String FIELD_PARKING_SPACE = "Parking Space: ";
    private static final String FIELD_FROM = "From: ";
    private static final String FIELD_TO = "To: ";
    private static final String FIELD_NEW_LINE = "\n";
    private static final String FIELD_BUTTON_LABEL = "View QR Code";

    private List<Dictionary<String, Object>> reservations;

    private List<String> userReservationsList;
    private ArrayAdapter<String> adapter;

    private List<Button> qrButtons;
    private List<String> qrPaths;


    private SharedPreferences mPreferences;

    /*
    This function shows the list of the user's reservations
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_reservations);

        mPreferences = getSharedPreferences("CurrentUser", MODE_PRIVATE);

        // check if the user is logged in by querying the preferences for a user's saved information
        if (mPreferences.contains("_id.$oid")) {

            reservations = new ArrayList<>();

            qrButtons = new ArrayList<>();
            qrPaths = new ArrayList<>();

            /*
            Make a HTTP GET request for all the reservations of the logged in user while defining a subclass
            to handle the response
             */
            StringRequest stringRequest = new StringRequest(Request.Method.GET, SERVER_PATH +
                                            USER_PATH + mPreferences.getString("_id.$oid","") + "/" + RESERVATION_PATH,
                    new Response.Listener<String>(){
                        @Override
                        public void onResponse(String s) {
                            try {
                                /*
                                For each reservation returned, extract the values for reservation begin time, reservation
                                end time, the reserved spot, and the path to the reservation's QR code. These values are then placed
                                into a dictionary object which is added to a list of all the reservations
                                 */

                                JSONArray jsonArray = new JSONArray(s);
                                for(int i = 0; i<jsonArray.length(); i++){
                                    JSONObject obj = jsonArray.getJSONObject(i);
                                    String beginTime = obj.getString(KEY_FROM);
                                    String endTime = obj.getString(KEY_TO);
                                    String parkingSpaceId = obj.getJSONObject(KEY_PARKING_SPOT_ID).getString(KEY_OID);
                                    String qrCodePath = obj.getString(KEY_QR_CODE_PATH);

                                    Dictionary<String, Object> reservation = new Hashtable<>();
                                    reservation.put(KEY_FROM, beginTime);
                                    reservation.put(KEY_TO, endTime);
                                    reservation.put(KEY_PARKING_SPACE_ID, parkingSpaceId);
                                    reservation.put(KEY_QR_CODE_PATH, qrCodePath);

                                    reservations.add(reservation);
                                }

                                // Once all the reservations have been received, they are added to the ListView
                                addReservationsToList();

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
        } else {
            System.out.println("User not logged in");
        }
    }

    /*
    * This function retrieves the parking space name
    */
    private void addReservationsToList(){
        userReservationsList = new ArrayList<>();
        adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, userReservationsList);

        ListView reservationsContainer = (ListView) findViewById(R.id.list);
        reservationsContainer.setAdapter(adapter);

        for(final Dictionary reservationObject : reservations) {

            if(reservationObject.get(KEY_PARKING_SPACE_ID) != null) {
                /*
                An HTTP GET request using the saved reservation parking space id to get a parking space name so that it can be displayed.
                This value is added to the reservation object once it is retrieved
                 */
                StringRequest parkingSpotRequest = new StringRequest(Request.Method.GET, SERVER_PATH + PARKING_SPOTS_PATH + reservationObject.get(KEY_PARKING_SPACE_ID),
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String s) {
                                try {
                                        JSONObject obj = new JSONObject(s);
                                        String parkingName = obj.getString(KEY_NAME);

                                        reservationObject.put(KEY_PARKING_SPACE_NAME, parkingName);

                                    // All required components have been retreieved at this point, so the reservation can now be displayed
                                    showReservation(reservationObject);

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
    /*
    * This function takes a reservation object as a parameter and displays it
    */
    private void showReservation(Dictionary<String, Object> reservationObject){

        LinearLayout frame = (LinearLayout) findViewById(R.id.list_layout);

        TextView reservationInfoText = new TextView(this);
        reservationInfoText.setText(FIELD_PARKING_SPACE + reservationObject.get(KEY_PARKING_SPACE_NAME) + FIELD_NEW_LINE
                + FIELD_FROM + reservationObject.get(KEY_FROM) + FIELD_NEW_LINE
                + FIELD_TO + reservationObject.get(KEY_TO) + FIELD_NEW_LINE);
        frame.addView(reservationInfoText);

        Button qrButton = new Button(this);
        qrButton.setWidth(100);
        qrButtons.add(qrButton);
        qrPaths.add(reservationObject.get(KEY_QR_CODE_PATH) + "");
        qrButton.setText(FIELD_BUTTON_LABEL);

        //Once the button is clicked, it asynchronously gets the QR Code of the reservation
        qrButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Button clickedButton = (Button) v;
                int qrIndex = qrButtons.indexOf(clickedButton);
                String qrPath = qrPaths.get(qrIndex);
                String urlPath = SERVER_PATH + qrPath;
                AsyncImageDownload imageDownload = new AsyncImageDownload();
                imageDownload.execute(urlPath);
            }
        });
        frame.addView(qrButton);
    }

    /*
    * Subclass responsible for the asynchronous download of a QR Code from a URL
    */
    public class AsyncImageDownload extends AsyncTask<String, Integer, Bitmap> {

        /*
        * This function downloads the QR Code and returns it as a Bitmap, which is passed to
        * onPostExecute(Bitmap result)
        */
        protected Bitmap doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);
                Bitmap image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                return image;

            } catch (Exception e) {
                System.out.println("Could not download");
                return null;
            }
        }

        protected void onProgressUpdate(Integer... progress) {

        }
        /*
        * This funtion is called once doInBackground(String... params) is done executing. It takes the returned Bitmap
        * QR code and displays it in a popup
        */
        protected void onPostExecute(Bitmap result) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(MyReservations.this);
            ImageView viewImage = new ImageView(MyReservations.this);
            viewImage.setMinimumHeight(1000);
            viewImage.setMinimumWidth(1000);
            viewImage.setImageBitmap(result);
            dialog.setView(viewImage);
            dialog.create();
            dialog.show();
        }
    }
}