package com.example.akhil.smartparking;

import android.content.Intent;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.app.AlertDialog;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A login screen that offers login via username/password.
 */
public class LoginActivity extends BaseActivity {
    private SharedPreferences mPreferences;

    /**
     * A login screen that offers login via username/password.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final EditText username1 = (EditText) findViewById(R.id.username);
        final EditText password1 = (EditText) findViewById(R.id.password);
        final Button email_sign_in_button = (Button) findViewById(R.id.email_sign_in_button);
        final Button signup = (Button) findViewById(R.id.signup);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(LoginActivity.this, Register.class);
                LoginActivity.this.startActivity(registerIntent);
            }
        });

        email_sign_in_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = username1.getText().toString();
                final String password = password1.getText().toString();

                // Response received from the server
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            /*
                               Make a HTTP GET request for all the parking spots
                            */
                            JSONObject jsonResponse = new JSONObject(response);
                            JSONObject data = jsonResponse.getJSONObject("data");

                            /* Shared preference editor notifies all the activity that a user has
                             * successfully logged in and saves the user session
                             */
                            SharedPreferences.Editor editor = mPreferences.edit();
                            editor.putString("uid", data.getString("uid"));
                            editor.putString("username", data.getString("username"));
                            editor.putString("_id.$oid", data.getJSONObject("_id").getString("$oid"));
                            editor.commit();

                            if (data != null) {
                                String email  = data.getString("email");
                                Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                                LoginActivity.this.startActivity(mainIntent);
                                invalidateOptionsMenu();

                            } else if (data == null) {
                                onErrorResponse("Failed");
                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                builder.setMessage("Login Failed")
                                        .setNegativeButton("Retry", null)
                                        .create()
                                        .show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    public void onErrorResponse(String message) {
                       System.out.println(message);
                    }
                };

                //Login Request made through the LoginRequest class
                LoginRequest loginRequest = new LoginRequest(username, password, responseListener, new FailError());
                RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
                queue.add(loginRequest);
            }
        });
        mPreferences = getSharedPreferences("CurrentUser", MODE_PRIVATE);
    }

    /**
     * If the wrong password has been entered, a dialogue box appears showing the login failed with
     * a "Retry" button that takes back to the login screen.
     */
    public class FailError implements Response.ErrorListener {

        public void onErrorResponse(VolleyError volleyError) {
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
            builder.setMessage("Login Failed. Wrong username or password")
                    .setNegativeButton("Retry", null)
                    .create()
                    .show();
        }
    }
}