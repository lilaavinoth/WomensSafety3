package com.example.womenssafety;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class SecondActivity extends AppCompatActivity {

    Button signout;
    GoogleSignInClient mGoogleSignInClient;
    DatabaseReference mDatabase;
    Button getotp,verify;
    EditText phoneno,enterotp;
    FirebaseAuth firebaseAuth;
    String Codesent,personId,phone;

    public static final String Login = "" ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        firebaseAuth = FirebaseAuth.getInstance();

        signout = findViewById(R.id.signout);
        getotp = findViewById(R.id.getotp);
        verify = findViewById(R.id.verify);
        phoneno = findViewById(R.id.phoneno);
        enterotp = findViewById(R.id.enterotp);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {
            String personName = acct.getDisplayName();
            String personGivenName = acct.getGivenName();
            String personFamilyName = acct.getFamilyName();
            String personEmail = acct.getEmail();
            personId = acct.getId();
            Uri personPhoto = acct.getPhotoUrl();

            Log.d("personEmail",personEmail);
            Log.d("personFamilyName",personFamilyName);
            Log.d("personGivenName",personGivenName);
            Log.d("personId",personId);
            Log.d("personName",personName);
//            Log.d(personPhoto,personPhoto);


            mDatabase = FirebaseDatabase.getInstance().getReference().child("Google Sign-in Users");
            Map<String,Object>users = new HashMap<>();
            users.put("Name",personName);
            users.put("ID",personId);
            users.put("e-Mail",personEmail);


            mDatabase.child(personId).updateChildren(users);

        }


        getotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getotpp();
            }
        });

        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verifier();
            }
        });

        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signoout();
            }
        });

    }

    private void verifier() {

        String code = enterotp.getText().toString();

        if (code.isEmpty()){
            Toast.makeText(this, "OTP is Empty", Toast.LENGTH_SHORT).show();
        }else {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(Codesent, code);
            signInWithPhoneAuthCredential(credential);
        }


    }

    private void getotpp() {

        phone = phoneno.getText().toString();

        if (phone.isEmpty())
        {
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(400);
            Toast.makeText(this, "Enter a Valid Phone Number", Toast.LENGTH_SHORT).show();
        }
        else {
            PhoneAuthOptions options =
                    PhoneAuthOptions.newBuilder(firebaseAuth)
                            .setPhoneNumber(phone)       // Phone number to verify
                            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                            .setActivity(this)                 // Activity (for callback binding)
                            .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                            .build();
            PhoneAuthProvider.verifyPhoneNumber(options);
        }


    }

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {

        }

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            Codesent = s;
        }
    };

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();
                            mDatabase = FirebaseDatabase.getInstance().getReference().child("Google Sign-in Users");
                            Map<String,Object>users = new HashMap<>();
                            users.put("Phone",phone);

                            mDatabase.child(personId).updateChildren(users);

                            Intent intent = new Intent(SecondActivity.this,ThirdAcitivity.class);
                            startActivity(intent);
                            finish();

                            SharedPreferences sharedPreferences = getSharedPreferences(Login,MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(Login,"2");
                            editor.apply();

                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w("TAG", "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Toast.makeText(SecondActivity.this, "Incorrect OTP", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    private void signoout() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(SecondActivity.this, "Signed Out", Toast.LENGTH_SHORT).show();
                        SharedPreferences sharedPreferences = getSharedPreferences(Login,MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(Login,"0");
                        editor.apply();

                    }
                });
    }
}