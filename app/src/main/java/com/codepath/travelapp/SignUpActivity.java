package com.codepath.travelapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class SignUpActivity extends AppCompatActivity {

    private static final int PICK_PHOTO_FOR_AVATAR = 2;
    public final String APP_TAG = "SignUpActivity";

    private String username;
    private String password;
    private String email;
    private String state;
    private String bio;
    private File image;

    private Button signUpBtn;
    private EditText etUsername;
    private EditText etPassword;
    private EditText etEmail;
    private Spinner spState;
    private ImageView ivBackBtn;
    private EditText etBio;
    private Button signUpProfileImageBtn;
    private ImageView ivProfileImage;
    public String photoFileName = "photo.jpg";
    private File photoFile;
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        signUpBtn = findViewById(R.id.signUpBtn);
        etUsername =  findViewById(R.id.etSignUpUsername);
        etPassword = findViewById(R.id.etSignUpPassword);
        etEmail = findViewById(R.id.etSignUpEmail);
        spState = findViewById(R.id.spSelectState);
        ivBackBtn = findViewById(R.id.ivBackBtn);
        etBio = findViewById(R.id.etSignUpBio);
        signUpProfileImageBtn = findViewById(R.id.signUpProfileImageBtn);
        ivProfileImage = findViewById(R.id.ivProfileImage);

        // Retrieves intent from login activity
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        password = intent.getStringExtra("password");

        // Username and password autofill from the login activity
        etUsername.setText(username);
        etPassword.setText(password);

        addOnClickListeners();
    }

    private void addOnClickListeners() {
        ivBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Sends intent back to login activity
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username = etUsername.getText().toString();
                password = etPassword.getText().toString();
                email = etEmail.getText().toString();
                state = spState.getSelectedItem().toString();
                bio = etBio.getText().toString();


                // User can only sign up if all fields are complete
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
                } else {
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
    public void onLaunchGallery(View view) {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference to access to future access
        photoFile = getPhotoFileUri(photoFileName);
        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        Uri fileProvider = FileProvider.getUriForFile(this, "com.codepath.fileprovider.travelApp", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }
    //TODO edit this to work for this app
    private File getPhotoFileUri(String photoFileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(this.getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG);

        // Create the storage director y if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(APP_TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + photoFileName);

        return file;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
//                // by this point we have the camera photo on disk
//                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
//                // RESIZE BITMAP, see section below
//                // Load the taken image into a preview
//                ivProfileImage.setImageBitmap(takenImage);
                if (data != null && data.getExtras() != null) {
                    Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
                   ivProfileImage.setImageBitmap(imageBitmap);
                }
            } else { // Result was a failure
                Toast.makeText(context, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void signUp() {
        // Create the ParseUser
        ParseUser user = new ParseUser();
        // Set core properties
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        user.put("bio", bio);
        user.put("homeState", state);
        user.put("profileImage", image);

        // Invoke signUpInBackground
        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    // Hooray! Let them use the app now.
                    Log.d("SignUpActivity", "SignUp successful!");
                    final Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Log.d("SignUpActivity", "SignUp failure");
                    Toast.makeText(SignUpActivity.this, e.toString(), Toast.LENGTH_LONG).show(); // TODO cut down e.toString()
                    e.printStackTrace();
                }
            }
        });
    }
}
