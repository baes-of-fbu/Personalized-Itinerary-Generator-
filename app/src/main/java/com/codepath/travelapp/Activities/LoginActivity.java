package com.codepath.travelapp.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.codepath.travelapp.R;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.facebook.ParseFacebookUtils;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private final String APP_TAG = "LoginActivity";

    private EditText etUsername;
    private EditText etPassword;
    private Button loginBtn;
    private TextView signUpBtn;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Allows user to continue to be signed in across sessions
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        etUsername = findViewById(R.id.etLoginUsername);
        etPassword = findViewById(R.id.etLoginPassword);
        loginBtn = findViewById(R.id.loginBtn);
        signUpBtn = findViewById(R.id.signUpBtn);


        addOnClickListeners();
    }

    private void addOnClickListeners() {

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String username = etUsername.getText().toString();
                final String password = etPassword.getText().toString();
                progressDialog = new ProgressDialog(LoginActivity.this);
                progressDialog.setMessage("Logging in...");
                progressDialog.setTitle("Please wait");
                progressDialog.show();
                login(username, password);
            }
        });

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                intent.putExtra("username", etUsername.getText().toString());
                intent.putExtra("password", etPassword.getText().toString());
                startActivity(intent);
                finish();
            }
        });
    }

    // Parse network request sent, logging in user
    private void login(String username, String password) {
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                progressDialog.hide();
                if (e == null) {
                    Log.d(APP_TAG, "Login successful!");
                    final Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Log.d(APP_TAG, "Login failure.");
                    e.printStackTrace();
                    showAlertDialog("Invalid login credentials");
                }
            }
        });
    }

    // TODO remove if Facebook login is removed
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }

    private void showAlertDialog(String message) {
        AlertDialog dialog = new AlertDialog.Builder(Objects.requireNonNull(getApplicationContext()))
                .setTitle(message)
                .setPositiveButton("OK", null)
                .create();
        dialog.show();
    }
}

