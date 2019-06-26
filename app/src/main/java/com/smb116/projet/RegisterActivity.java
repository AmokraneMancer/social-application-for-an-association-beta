package com.smb116.projet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextClock;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private EditText nameField;
    private EditText passwordField;
    private EditText emailField;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        nameField = findViewById(R.id.name);
        passwordField = findViewById(R.id.password);
        emailField = findViewById(R.id.email);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");

        progressDialog = new ProgressDialog(this);
    }

    public void onClickRegister(View v){
        final String name = nameField.getText().toString().trim();
        final String password = passwordField.getText().toString().trim();
        final String email = emailField.getText().toString().trim();

        if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(email)){
            progressDialog.setMessage("Signing up...");
            progressDialog.show();
            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        String userId = firebaseAuth.getCurrentUser().getUid();
                        DatabaseReference currentUserRef = databaseReference.child(userId);
                        currentUserRef.child("name").setValue(name);
                        currentUserRef.child("image").setValue("default");
                        progressDialog.dismiss();
                        finish();
                    }
                }
            });
        }
    }

    public void onClicklogin(View v){
        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
    }
}
