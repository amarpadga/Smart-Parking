package com.example.akhil.smartparking;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Register class currently does send information to the database.
 */
public class Register extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final Button regSubmit = (Button) findViewById(R.id.regSubmit);

        regSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(Register.this, LoginActivity.class);
                Register.this.startActivity(registerIntent);
            }

        });
    }
}
