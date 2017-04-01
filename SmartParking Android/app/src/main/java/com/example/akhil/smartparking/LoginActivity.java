package com.example.akhil.smartparking;

import android.content.Intent;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.app.AlertDialog;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLConnection;

/**
 * A login screen that offers login via username/password.
 */
public class LoginActivity extends BaseActivity {
    //private SharedPreferences mPreferences;

    //private final static String LOGIN_API_ENDPOINT_URL = "https://smart-parking-bruck.c9users.io:8081/auth/sign_in";
    private SharedPreferences mPreferences;
    private String mUserEmail;
    private String mUserPassword;

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
                            JSONObject jsonResponse = new JSONObject(response);
                            System.out.println("Object: " + jsonResponse.toString());
                           // boolean success = true;//jsonResponse.getBoolean("success");
                            JSONObject data = jsonResponse.getJSONObject("data");
                            SharedPreferences.Editor editor = mPreferences.edit();
                            editor.putString("uid", data.getString("uid"));
                            System.out.println("Testtttttttttt"+data.getString("username"));
                            editor.putString("username", data.getString("username"));
                            editor.putString("_id.$oid", data.getJSONObject("_id").getString("$oid"));
                            editor.commit();

                            if (data != null) {
                                System.out.println("success");
                                String email  = data.getString("email");
                                System.out.println(email);
                                Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                                LoginActivity.this.startActivity(mainIntent);

                                String username = data.getString("username");
                                String password = jsonResponse.getString("password");



                            } else if (data == null) {
                                onErrorResponse("Failed");
                                System.out.println("failed");
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

                LoginRequest loginRequest = new LoginRequest(username, password, responseListener, new FailError());
                RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
                queue.add(loginRequest);
            }
        });
        mPreferences = getSharedPreferences("CurrentUser", MODE_PRIVATE);
    }


    public class FailError implements Response.ErrorListener {

        public void onErrorResponse(VolleyError volleyError) {
            System.out.println("failed");

            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
            builder.setMessage("Login Failed")
                    .setNegativeButton("Retry", null)
                    .create()
                    .show();
        }
    }
}