package com.example.saleepepe;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.core.app.NavUtils;
import androidx.navigation.Navigation;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class SignUp extends AppCompatActivity {

    TextInputEditText username, email, password, ripetiPassword;
    TextInputLayout usernameL, emailL, passwordL, ripetiPasswordL;
    Button b;
    TextView login;
    ProgressBar progressBar;
    FirebaseAuth mAuth;
    FirebaseFirestore firebaseFirestore;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        username = findViewById(R.id.editTextTextUser);
        usernameL = findViewById(R.id.editTextTextUsernameL);
        email = findViewById(R.id.editTextTextEmailAddress);
        emailL = findViewById(R.id.editTextTextEmailAddressL);
        password = findViewById(R.id.editTextTextPassword);
        passwordL = findViewById(R.id.editTextTextPasswordL);
        ripetiPassword = findViewById(R.id.editTextTextPassword2);
        ripetiPasswordL = findViewById(R.id.editTextTextPassword2L);
        b = findViewById(R.id.buttonR);
        login = findViewById(R.id.textView);
        progressBar = findViewById(R.id.progressBar2);

        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();


        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String emails = email.getText().toString().trim();
                String passwords = password.getText().toString().trim();
                String ripetipasswords = ripetiPassword.getText().toString().trim();
                final String usernames = username.getText().toString();


                if (TextUtils.isEmpty(usernames)) {
                    if (TextUtils.isEmpty(emails)) {
                        if (TextUtils.isEmpty(passwords)) {
                            if (TextUtils.isEmpty(ripetipasswords)) {
                                ripetiPasswordL.setError("E' necessario ripetere la password");
                            }
                            passwordL.setError("E' richiesta la password");
                        }
                        emailL.setError("E' richiesta l'email");
                    }
                    usernameL.setError("E' richiesto l'username");
                    return;
                }

                if (!TextUtils.equals(passwords, ripetipasswords)) {
                    ripetiPasswordL.setError("La password ripetuta e' errata");
                    return;
                }

                if (passwords.length() < 6) {
                    passwordL.setError("La password deve contenere almeno 6 caratteri");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                b.setVisibility(View.INVISIBLE);
                login.setVisibility(View.INVISIBLE);

                mAuth.createUserWithEmailAndPassword(emails, passwords).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            Map<String, Object> user = new HashMap<>();

                            user.put("username", usernames);
                            firebaseFirestore.collection("users")
                                    .document(emails)
                                    .set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(SignUp.this, "Utente creato", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("Message2", "OnFailure :"+e.toString());
                                }
                            });
                            startActivity(new Intent(getApplicationContext(), Navigazione.class));
                        }else{
                            Toast.makeText(SignUp.this, "Impossibile creare l'utente", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                            b.setVisibility(View.VISIBLE);
                            login.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }
        });


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Login.class));
            }
        });
    }

    @Override
    public void onBackPressed() {
    }
}