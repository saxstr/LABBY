package com.example.gp_test;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

public class LogInSc extends AppCompatActivity {


    private FirebaseAuth fauth;
    private EditText loginEmail, loginPass;
    private Button loginButton;
    private Button toregButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.login_sc);

        fauth = FirebaseAuth.getInstance();
        loginEmail = findViewById(R.id.loginEmail);
        loginPass = findViewById(R.id.loginPass);
        loginButton = findViewById(R.id.loginButton);


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = loginEmail.getText().toString();
                String pass = loginPass.getText().toString();

                if (!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    if (!pass.isEmpty()){
                        fauth.signInWithEmailAndPassword(email, pass)
                                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                    @Override
                                    public void onSuccess(AuthResult authResult) {
                                        Toast.makeText(LogInSc.this, "Login Successfully", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(LogInSc.this, TutorialPageSc.class));
                                        finish();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(LogInSc.this, "login failed", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        loginPass.setError("password cannot be empty");
                    }
                } else if(email.isEmpty()){
                    loginEmail.setError("email cannot be empty");
                } else {
                    loginEmail.setError("please enter valid email");
                }

            }
        });
        Button toregButton = findViewById(R.id.toregButton);

        toregButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LogInSc.this, RegSc.class);
                startActivity(intent);
            }
        });
    }
}