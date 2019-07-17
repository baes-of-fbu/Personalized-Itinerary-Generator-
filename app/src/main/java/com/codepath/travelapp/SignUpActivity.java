package com.codepath.travelapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class SignUpActivity extends AppCompatActivity {

    //initialize views on signup screen
    private Button setsignupbtn;
    private EditText setusername;
    private EditText setpassword;
    private EditText setemail;
    private Spinner setstate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        setsignupbtn = findViewById(R.id.signup_btn);
        setusername = findViewById(R.id.etSignUpUsername);
        setpassword = findViewById(R.id.etSignUpPassword);
        setemail = findViewById(R.id.etSignupEmail);

        setsignupbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String newusername = setusername.getText().toString();
                final String newpassword = setpassword.getText().toString();
                final  String newemail = setemail.getText().toString();
                final String newstate = setstate.getSelectedItem().toString();
                setSignUp(newusername,newpassword,newemail, newstate);
                finish();
            }
        });
    }

    private void setSignUp(String username, String password, String email, String state) {
        // Create the ParseUser
        ParseUser user = new ParseUser();
        // Set core properties
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        //TODO add a getter and setter for state
        // user.setState(state);

        // Invoke signUpInBackground
        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    // Hooray! Let them use the app now.
                    final Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    // Sign up didn't succeed. Look at the ParseException
                    // to figure out what went wrong
                }
            }
        });
    }


}
