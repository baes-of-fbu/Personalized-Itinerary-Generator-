package com.codepath.travelapp.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.codepath.travelapp.Models.User;
import com.codepath.travelapp.R;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class SignUpActivity extends AppCompatActivity {

    public final String APP_TAG = "SignUpActivity";
    private final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;

    private String username;
    private String password;
    private String fullName;
    private String email;
    private String state;
    private String bio;
    private ParseFile image;

    private Button signUpBtn;
    private EditText etUsername;
    private EditText etPassword;
    private EditText etFullName;
    private EditText etEmail;
    private Spinner spState;
    private ImageView ivBackBtn;
    private EditText etBio;
    private Button signUpProfileImageBtn;
    private ImageView ivProfileImage;
    private ProgressDialog progressDialog;
    private Bitmap selectedImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        signUpBtn = findViewById(R.id.signUpBtn);
        etUsername = findViewById(R.id.etSignUpUsername);
        etPassword = findViewById(R.id.etSignUpPassword);
        etFullName = findViewById(R.id.etSignUpFullName);
        etEmail = findViewById(R.id.etSignUpEmail);
        spState = findViewById(R.id.spSelectState);
        ivBackBtn = findViewById(R.id.ivBackBtn);
        etBio = findViewById(R.id.etSignUpBio);
        signUpProfileImageBtn = findViewById(R.id.signUpProfileImageBtn);
        ivProfileImage = findViewById(R.id.ivSignUpProfileImage);

        // Retrieves intent from login activity
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        password = intent.getStringExtra("password");

        // Username and password auto-fill from the login activity
        etUsername.setText(username);
        etPassword.setText(password);
        if (selectedImage == null) {
            ivProfileImage.setVisibility(View.GONE);
        } else {
            ivProfileImage.setVisibility(View.VISIBLE);
        }
        addOnClickListeners();
    }

    private void addOnClickListeners() {
        ivBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUpActivity.this, OpeningActivity.class);
                startActivity(intent);
                finish();
            }
        });

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username = etUsername.getText().toString();
                password = etPassword.getText().toString();
                fullName = etFullName.getText().toString();
                email = etEmail.getText().toString();
                state = spState.getSelectedItem().toString();
                bio = etBio.getText().toString();

                // User can only sign up if all inputs are valid
                if (username.length() == 0) {
                    Log.e(APP_TAG, "No username");
                    Toast.makeText(getApplicationContext(), "Please make a username", Toast.LENGTH_LONG).show();
                } else if (password.length() == 0) {
                    Log.e(APP_TAG, "No password");
                    Toast.makeText(getApplicationContext(), "Please make a password", Toast.LENGTH_LONG).show();
                } else if (email.length() == 0) {
                    Log.e(APP_TAG, "No email");
                    Toast.makeText(getApplicationContext(), "Please specify your email", Toast.LENGTH_LONG).show();
                } else if (state.length() > 2) {
                    Log.e(APP_TAG, "No state");
                    Toast.makeText(getApplicationContext(), "Please select your home state", Toast.LENGTH_LONG).show();
                } else if (selectedImage == null) {
                    Log.e(APP_TAG, "no profile picture");
                    Toast.makeText(getApplicationContext(), "Please select a profile picture", Toast.LENGTH_SHORT).show();
                } else if (bio.length() == 0) {
                    Log.e(APP_TAG, "No bio");
                    Toast.makeText(getApplicationContext(), "Please enter a bio", Toast.LENGTH_SHORT).show();
                } else if (fullName.length() == 0) {
                    Log.e(APP_TAG, "no fullName");
                    Toast.makeText(getApplicationContext(), "Please enter your fullName", Toast.LENGTH_SHORT).show();
                } else {
                    image = convertBitmapToParseFile(selectedImage);
                    progressDialog = new ProgressDialog(SignUpActivity.this);
                    progressDialog.setMessage("Logging in...");
                    progressDialog.setTitle("Please wait");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    signUp();
                }
            }
        });

        signUpProfileImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onLaunchGallery(view);
            }
        });

    }

    private void signUp() {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        user.setBio(bio);
        user.setHomeState(state);
        user.setFullName(fullName);
        Toast.makeText(SignUpActivity.this, "Signing up. Please wait.", Toast.LENGTH_SHORT).show();
        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                progressDialog.hide();
                if (e == null) {
                    Log.d(APP_TAG, "SignUp successful!");
                    User newUser = (User) User.getCurrentUser();
                    newUser.setProfileImage(image);
                    newUser.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            final Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                    });
                } else {
                    Log.d(APP_TAG, "SignUp failure");
                    e.printStackTrace();
                    Toast.makeText(SignUpActivity.this, "Error signing up", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void onLaunchGallery(View view) {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (photoPickerIntent.resolveActivity(this.getPackageManager()) != null) {
            startActivityForResult(photoPickerIntent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                final Uri imageUri = data.getData();
                try {
                    selectedImage = MediaStore.Images.Media.getBitmap(SignUpActivity.this.getContentResolver(), imageUri);
                    if (selectedImage == null) {
                        ivProfileImage.setVisibility(View.GONE);
                    } else {
                        Glide.with(this)
                                .load(selectedImage)
                                .apply(RequestOptions.circleCropTransform())
                                .into(ivProfileImage);
                        ivProfileImage.setVisibility(View.VISIBLE);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this, "You haven't picked Image", Toast.LENGTH_LONG).show();
            }
        }
    }

    public static ParseFile convertBitmapToParseFile(Bitmap imageBitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] imageByte = byteArrayOutputStream.toByteArray();
        return new ParseFile("image_file.png", imageByte);
    }
}

