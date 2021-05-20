package com.example.needthis;


import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {

    private EditText emailSignUp, passSignUp;
    public Button signUpBtn;
    public TextView signINText;
    private FirebaseAuth auth;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        auth = FirebaseAuth.getInstance();

        emailSignUp = findViewById(R.id.sign_up_email);
        passSignUp = findViewById(R.id.sign_up_pass);
        signUpBtn = findViewById(R.id.sign_up_btn);
        signINText = findViewById(R.id.sign_in_text);

        signINText.setOnClickListener(v -> startActivity(new Intent(SignUpActivity.this, SignInActivity.class)));

        signUpBtn.setOnClickListener(v -> {
            String email = emailSignUp.getText().toString();
            String pass = passSignUp.getText().toString();

            if (!email.isEmpty() && !pass.isEmpty()){
                auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        Toast.makeText(SignUpActivity.this, "Registered Successfully!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(SignUpActivity.this, SetUpActivity.class));
                        finish();
                    }else {
                        Toast.makeText(SignUpActivity.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }else {
                Toast.makeText(SignUpActivity.this, "Please Input All Fields!", Toast.LENGTH_SHORT).show();
            }
        });



    }
}