package com.example.womenssafety;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.Map;

public class ThirdAcitivity extends AppCompatActivity {

    Button signout;
    TextView textView;
    GoogleSignInClient mGoogleSignInClient;

    public static final String Login = "" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third_acitivity);

        textView = findViewById(R.id.third);

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {
            String personName = acct.getDisplayName();
            textView.setText(personName + " has signed in");
        }

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        signout = findViewById(R.id.signoutlast);

        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signoutla();
            }
        });
    }

    private void signoutla() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(ThirdAcitivity.this, "Signed Out", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(ThirdAcitivity.this, MainActivity.class);
                        startActivity(intent);

                        SharedPreferences sharedPreferences = getSharedPreferences(Login,MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(Login,"0");
                        editor.apply();

                    }
                });
    }

}