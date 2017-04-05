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

import java.io.InputStream;
import java.net.URL;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.ArrayList;

import android.view.View;

import android.view.ViewGroup.LayoutParams;
/*
    This activity is used to display the parking reservations a user has made.
    An HTTP GET request is made to the server requesting all the reservations, which is then parsed into a
    dictionary. This dictionary is then iterated over to populate a list for the user to view
 */
public class MyReservations extends BaseActivity{

    private static final String URL_DATA_PATH = "http://smart-parking-bruck.c9users.io:8081/";
    private static final String PATH_USER = "users/";
    private static final String RESERVATION_PATH = "reservations/";
    private static final String PARKING_SPOTS_PATH = "parking_spots/";

    private static final String RESERVATION_FIELD_FROM = "from";
    private static final String RESERVATION_FIELD_TO = "to";
    private static final String RESERVATION_FIELD_PARKING_SPOT_ID = "parking_spot_id";
    private static final String RESERVATION_FIELD_OID = "$oid";
    private static final String RESERVATION_FIELD_QR_CODE_PATH = "qr_code_path";

    private static final String PARKING_SPACE_FIELD_NAME = "name";

    private static final String KEY_TIME_FROM = "from";
    private static final String KEY_TIME_TO = "to";
    private static final String KEY_PARKING_SPACE_NAME = "parking_space";
    private static final String KEY_PARKING_SPACE_ID = "parking_space_id";
    private static final String KEY_QR_CODE_PATH = "qr_code_path";

    private static final String TEXT_PARKING_SPACE = "Parking Space: ";
    private static final String TEXT_FROM = "From: ";
    private static final String TEXT_TO = "To: ";
    private static final String TEXT_NEW_LINE = "\n";

    private List<Dictionary<String, Object>> reservations;

    private List<String> userReservationsList;
    private ArrayAdapter<String> adapter;

    private List<Button> qrButtons;
    private List<String> qrPaths;


    private SharedPreferences mPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_reservations);

        mPreferences = getSharedPreferences("CurrentUser", MODE_PRIVATE);

        if (mPreferences.contains("_id.$oid")) {

            reservations = new ArrayList<>();

            qrButtons = new ArrayList<>();
            qrPaths = new ArrayList<>();
            StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_DATA_PATH +
                                            PATH_USER + mPreferences.getString("_id.$oid","") + "/" + RESERVATION_PATH,
                    new Response.Listener<String>(){
                        @Override
                        public void onResponse(String s) {
                            try {
                                JSONArray jsonArray = new JSONArray(s);
                                for(int i = 0; i<jsonArray.length(); i++){
                                    JSONObject obj = jsonArray.getJSONObject(i);
                                    String beginTime = obj.getString(RESERVATION_FIELD_FROM);
                                    String endTime = obj.getString(RESERVATION_FIELD_TO);
                                    String parkingSpaceId = obj.getJSONObject(RESERVATION_FIELD_PARKING_SPOT_ID).getString(RESERVATION_FIELD_OID);
                                    String qrCodePath = obj.getString(RESERVATION_FIELD_QR_CODE_PATH);

                                    Dictionary<String, Object> reservation = new Hashtable<>();
                                    reservation.put(KEY_TIME_FROM, beginTime);
                                    reservation.put(KEY_TIME_TO, endTime);
                                    reservation.put(KEY_PARKING_SPACE_ID, parkingSpaceId);
                                    reservation.put(KEY_QR_CODE_PATH, qrCodePath);

                                    reservations.add(reservation);
                                }
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

    private void addReservationsToList(){
        userReservationsList = new ArrayList<>();
        adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, userReservationsList);
        //adapter = new ArrayAdapter<>()

        ListView reservationsContainer = (ListView) findViewById(R.id.list);
        //reservationsContainer.setOnItemClickListener(this);
        reservationsContainer.setAdapter(adapter);


        for(final Dictionary reservationObject : reservations) {

            if(reservationObject.get(KEY_PARKING_SPACE_ID) != null) {
                StringRequest parkingSpotRequest = new StringRequest(Request.Method.GET, URL_DATA_PATH + PARKING_SPOTS_PATH + reservationObject.get(KEY_PARKING_SPACE_ID),
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String s) {
                                try {
                                        JSONObject obj = new JSONObject(s);
                                        String parkingName = obj.getString(PARKING_SPACE_FIELD_NAME);

                                        reservationObject.put(KEY_PARKING_SPACE_NAME, parkingName);
                                        String reservationText = TEXT_PARKING_SPACE + reservationObject.get(KEY_PARKING_SPACE_NAME) + TEXT_NEW_LINE
                                            + TEXT_FROM + reservationObject.get(KEY_TIME_FROM) + TEXT_NEW_LINE
                                            + TEXT_TO + reservationObject.get(KEY_TIME_TO) + TEXT_NEW_LINE;

                                    //Button reservationListItem = createReservationButton(reservationText);
                                    //reservationListItem.setText(reservationItem);

                                    //adapter.add(reservationText);
                                    //adapter.add(reservationListItem);

                                    addReservation(reservationObject);

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

    private void addReservation(Dictionary<String, Object> reservationObject){

        LinearLayout frame = (LinearLayout) findViewById(R.id.list_layout);

        TextView test = new TextView(this);
        test.setText(TEXT_PARKING_SPACE + reservationObject.get(KEY_PARKING_SPACE_NAME) + TEXT_NEW_LINE
                + TEXT_FROM + reservationObject.get(KEY_TIME_FROM) + TEXT_NEW_LINE
                + TEXT_TO + reservationObject.get(KEY_TIME_TO) + TEXT_NEW_LINE);
        frame.addView(test);

        Button testButton = new Button(this);
        testButton.setWidth(100);
        qrButtons.add(testButton);
        qrPaths.add(reservationObject.get(KEY_QR_CODE_PATH) + "");
        testButton.setText("View QR Code");
        //testButton.text
        testButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Button testButtonClicked = (Button) v;
                int qrIndex = qrButtons.indexOf(testButtonClicked);
                String qrPath = qrPaths.get(qrIndex);
                String urlPath = URL_DATA_PATH + qrPath;
                AsyncImageDownload imageDownloadTest = new AsyncImageDownload();
                imageDownloadTest.execute(urlPath);
            }
        });


        try {
            String qrFullPath = URL_DATA_PATH + reservationObject.get(KEY_QR_CODE_PATH);

            ImageView testImage = new ImageView(this);

            URL url = new URL(qrFullPath);
            AsyncImageDownload imageDownloadTest = new AsyncImageDownload();
            frame.addView(testImage);

        } catch (Exception e) {
            testButton.setEnabled(false);
            System.err.println(e);
        }
        frame.addView(testButton);
    }

    public class AsyncImageDownload extends AsyncTask<String, Integer, Bitmap> {
        protected void onPreExecute(Long result) {

        }

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