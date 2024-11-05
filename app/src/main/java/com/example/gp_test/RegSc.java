package com.example.gp_test;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class RegSc extends AppCompatActivity {

    private FirebaseAuth fauth;
    private EditText signupEmail, signupPass, sigupName;
    private Button SignupButton;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.reg_sc);



        fauth = FirebaseAuth.getInstance();
        signupEmail = findViewById(R.id.signupEmail);
        signupPass = findViewById(R.id.signupPass);
        SignupButton = findViewById(R.id.signupButton);

        SignupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v ) {
                String user = signupEmail.getText().toString().trim();
                String pass = signupPass.getText().toString().trim();


                if (user.isEmpty()) {
                    signupEmail.setError("Email cannot be empty");
                }
                if (pass.isEmpty()){
                    signupPass.setError("Password cannot be empty");

                } else{
                    fauth.createUserWithEmailAndPassword(user, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                            Toast.makeText(RegSc.this, "signing up succssfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RegSc.this, LogInSc.class));
                            }else {
                                Toast.makeText(RegSc.this, "sign up failed" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        }

    }


