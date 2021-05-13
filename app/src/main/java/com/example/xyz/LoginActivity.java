package com.example.xyz;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    EditText email, password;
    Button login, register;

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        login = findViewById(R.id.login);
        register = findViewById(R.id.register);

        auth = FirebaseAuth.getInstance();

        login.setOnClickListener(v -> {
            String Email = email.getText().toString();
            String Password = password.getText().toString();

            if (TextUtils.isEmpty(Email) || TextUtils.isEmpty(Password)) {
                Toast.makeText(LoginActivity.this, "All field are required", Toast.LENGTH_LONG).show();
            } else {
                auth.signInWithEmailAndPassword(Email, Password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                finish();
                            } else {
                                Toast.makeText(LoginActivity.this, "email not register", Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });

        register.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));

    }
}