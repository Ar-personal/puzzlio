package com.example.puzzlio;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EmailRegister extends AppCompatActivity {

    private static final String TAG = "EmailPassword";
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.emailregister);

        mAuth = FirebaseAuth.getInstance();

        Button create_account = findViewById(R.id.createaccount);

        create_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText email = findViewById(R.id.loginemail);
                EditText password = findViewById(R.id.loginpassword);
                EditText password2 = findViewById(R.id.loginpassword2);

                String emailText = email.getText().toString();
                String passwordText = password.getText().toString();
                String password2Text = password2.getText().toString();



                verifyEmailPassword(emailText, passwordText, password2Text);

            }
        });


        Button back = findViewById(R.id.registerback);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }

    public void verifyEmailPassword(String email, String password, String password2){
        if(!password.trim().equals(password2.trim())){
            if(password.length() == 0 || password2.isEmpty()){
                Toast.makeText(getApplicationContext(), "A Password is Missing or incorrect", Toast.LENGTH_SHORT).show();
            }
            Toast.makeText(getApplicationContext(), "Passwords Do Not Match", Toast.LENGTH_SHORT).show();
            return;
        }

        if(password.length() == 0){
            Toast.makeText(getApplicationContext(), "You have not entered a password", Toast.LENGTH_SHORT).show();
            return;
        }

        if(password2.isEmpty()){
            Toast.makeText(getApplicationContext(), "Please Enter Verification Password", Toast.LENGTH_SHORT).show();
            return;
        }

        if(email.length() == 0){
            Toast.makeText(getApplicationContext(), "Please Enter a Valid Email", Toast.LENGTH_SHORT).show();
        return;
        }

        if(password.length() < 6 || password2.length() <  6){
            Toast.makeText(getApplicationContext(), "Minimum Password Length is 7", Toast.LENGTH_SHORT).show();
            return;
        }

        createUserWithEmailAndPassword(email, password);
    }

    public void createUserWithEmailAndPassword(String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                        } else{
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(EmailRegister.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }

                    }
                });
    }



}
