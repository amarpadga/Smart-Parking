package com.example.akhil.smartparking;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * The LoginRequest class essentially makes a request to the database to see if the user that is attempting
 * to login is already registered in the database.
 */
public class LoginRequest extends StringRequest {

    private static final String LOGIN_REQUEST_URL = "https://smart-parking-bruck.c9users.io:8081/auth/sign_in";
    private Map<String, String> params;

        public LoginRequest(String username, String password, Response.Listener<String> listener, Response.ErrorListener error) {
            super(Method.POST, LOGIN_REQUEST_URL, listener, error);
        params = new HashMap<>();
        params.put("email", username);
        params.put("password", password);
    }


    @Override
    public Map<String, String> getParams() {
        return params;
    }
}