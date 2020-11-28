package com.example.puzzlio;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;

import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SignUpLogInActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signuploginactivity);

        Button signIn = findViewById(R.id.signinnavbutton);
        Button create = findViewById(R.id.createaccountnavbutton);

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUpLogInActivity.this, SignIn.class);
                startActivity(intent);

            }
        });


        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUpLogInActivity.this, EmailPasswordActivity.class);
                startActivity(intent);
            }
        });
    }
}
