package com.example.saleepepe;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class Login extends AppCompatActivity {
    EditText email, password;
    TextInputLayout emailL, passwordL;
    TextView registrati, passwordDimenicata;
    Button bLogin;
    ProgressBar progressBar;

    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);


        password = findViewById(R.id.editTextTextPassword);
        email = findViewById(R.id.editTextTextEmailAddress);
        emailL = findViewById(R.id.editTextTextEmailAddressL);
        passwordL = findViewById(R.id.editTextTextPasswordL);
        registrati = findViewById(R.id.textView3);
        passwordDimenicata = findViewById(R.id.textView2);
        bLogin = findViewById(R.id.buttonL);
        progressBar = findViewById(R.id.progressBar);

        mAuth = FirebaseAuth.getInstance();


        bLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String emails = email.getText().toString().trim();
                final String passwords = password.getText().toString().trim();

                if (TextUtils.isEmpty(emails)) {
                    if (TextUtils.isEmpty(passwords)) {
                        passwordL.setError("E' richiesta la password");

                    }
                    emailL.setError("E' richiesta l'email");
                    return;
                }

                if (passwords.length() < 6) {
                    passwordL.setError("La password deve contenere almeno 6 caratteri");
                    return;
                }

                passwordDimenicata.setVisibility(View.INVISIBLE);
                registrati.setVisibility(View.INVISIBLE);
                bLogin.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);


                mAuth.signInWithEmailAndPassword(emails, passwords).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(Login.this, "Utente loggato", Toast.LENGTH_SHORT).show();
                            Intent homePage = new Intent(Login.this, Navigazione.class);
                            homePage.putExtra("userId", emails);
                            homePage.putExtra("psw", passwords);
                            startActivity(homePage);
                        } else {
                            Toast.makeText(Login.this, "Utente non loggato", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                            passwordDimenicata.setVisibility(View.VISIBLE);
                            registrati.setVisibility(View.VISIBLE);
                            bLogin.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }
        });


        registrati.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SignUp.class));
            }
        });

        passwordDimenicata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText resetEmail = new EditText(v.getContext());
                AlertDialog.Builder passwordreset = new AlertDialog.Builder(v.getContext());
                passwordreset.setTitle("Desideri reimpostare la password?");
                passwordreset.setMessage("Inserisci la tua mail per ottenere il link di reimpostazione");
                passwordreset.setView(resetEmail);
                passwordreset.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String mail = resetEmail.getText().toString();
                        mAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(Login.this, "Link di reimpostazione inviato alla tua mail", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Login.this, "Errore. Link di reimpostazione non inviato ", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

                passwordreset.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                passwordreset.create().show();
            }
        });
    }
    @Override
    public void onBackPressed() {
    }
}