package com.example.puzzlio;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class UserSettings extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.usersettings);
        context = getApplicationContext();
        mAuth = FirebaseAuth.getInstance();

        //close activity
        ImageButton close = findViewById(R.id.usersettingsclose);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        //sign out of firebase

        Button signout = findViewById(R.id.signout);
        Button delete = findViewById(R.id.deleteaccount);

        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                GoogleSignIn.getClient(
                        getApplicationContext(),
                        new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
                ).signOut();
                startActivity(new Intent(UserSettings.this, MainLoginRegistry.class));
                finish();
            }
        });


        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mAuth.getCurrentUser() == null){
                    exitToLogin();
                }

                AlertDialog alertDialog = new AlertDialog.Builder(UserSettings.this).create();
                alertDialog.setTitle("Delete Account?");
                alertDialog.setMessage("Are you sure?");
                alertDialog.setIcon(R.drawable.ic_delete_red_24dp);
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                mAuth.getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Toast.makeText(context, "Account deleted", Toast.LENGTH_SHORT).show();
                                            exitToLogin();
                                        }
                                    }
                                });
                            }
                        });


                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "no",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
        });





    }


    public void exitToLogin(){
        startActivity(new Intent(UserSettings.this, MainLoginRegistry.class));
        finish();
    }
}
