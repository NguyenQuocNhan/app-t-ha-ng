package com.example.xyz;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    private EditText username, email, password;

    FirebaseAuth firebaseAuth;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Button register = findViewById(R.id.register);
        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        register.setOnClickListener(v -> registerAccount(username.getText().toString(), email.getText().toString(), password.getText().toString()));
    }

    private void registerAccount(String username, String email, String password) {
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(RegisterActivity.this, "All field are required", Toast.LENGTH_SHORT).show();
        } else if (password.length() < 6) {
            Toast.makeText(RegisterActivity.this, "password < 6 character", Toast.LENGTH_SHORT).show();
        } else {
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                            assert firebaseUser != null;
                            String userID = firebaseUser.getUid();

                            reference = FirebaseDatabase.getInstance().getReference("Users").child(userID);

                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("ID", userID);
                            hashMap.put("username", username);
                            hashMap.put("avatar", "default");
                            hashMap.put("gender", "male");
                            hashMap.put("number", "default");
                            hashMap.put("address", "default");
                            hashMap.put("actor", "customer");

                            reference.setValue(hashMap).addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                    finish();
                                }
                            });
                        } else {
                            Toast.makeText(RegisterActivity.this, "not register", Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }
}