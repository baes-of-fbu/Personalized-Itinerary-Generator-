package com.codepath.travelapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
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
    public static final int PICK_IMAGE = 1;
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
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }
    // Returns the File for a photo stored on disk given the fileName
    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG);
        // Create the storage director y if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(APP_TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);

        return file;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_PHOTO_FOR_AVATAR && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                //Display an error
                return;
            }
            try {
                InputStream inputStream = context.getContentResolver().openInputStream(data.getData());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            //Now you can do whatever you want with your inpustream, save it as file, upload to a server, decode a bitmap...
            String filePath = photoFile.getPath();
            Bitmap bitmap = BitmapFactory.decodeFile(filePath);
            ivProfileImage.setImageBitmap(bitmap);
        }
    }
    public Bitmap rotateBitmapOrientation(String photoFilePath) {
        // Create and configure BitmapFactory
        BitmapFactory.Options bounds = new BitmapFactory.Options();
        bounds.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoFilePath, bounds);
        BitmapFactory.Options opts = new BitmapFactory.Options();
        Bitmap bm = BitmapFactory.decodeFile(photoFilePath, opts);
        // Read EXIF Data
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(photoFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String orientString = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
        int orientation = orientString != null ? Integer.parseInt(orientString) : ExifInterface.ORIENTATION_NORMAL;
        int rotationAngle = 0;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_90) rotationAngle = 90;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_180) rotationAngle = 180;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_270) rotationAngle = 270;
        // Rotate Bitmap
        Matrix matrix = new Matrix();
        matrix.setRotate(rotationAngle, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);
        Bitmap rotatedBitmap = Bitmap.createBitmap(bm, 0, 0, bounds.outWidth, bounds.outHeight, matrix, true);
        // Return result
        return rotatedBitmap;
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
