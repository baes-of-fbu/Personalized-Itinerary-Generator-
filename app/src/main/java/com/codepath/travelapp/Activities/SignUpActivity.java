package com.codepath.travelapp.Activities;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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
import android.widget.LinearLayout;
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
import java.io.File;
import java.io.IOException;

public class SignUpActivity extends AppCompatActivity {

    public final String APP_TAG = "SignUpActivity";
    private String username;
    private String password;
    private String email;
    private String state;
    private String bio;
    private ParseFile image;

    private LinearLayout llSignUp;
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
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    private Context context;
    private Bitmap selectedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        llSignUp = findViewById(R.id.llSignUp);
        signUpBtn = findViewById(R.id.signUpBtn);
        etUsername =  findViewById(R.id.etSignUpUsername);
        etPassword = findViewById(R.id.etSignUpPassword);
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

        // Username and password autofill from the login activity
        etUsername.setText(username);
        etPassword.setText(password);
        if (selectedImage == null) {
            ivProfileImage.setVisibility(View.GONE);
        }else {
            ivProfileImage.setVisibility(View.VISIBLE);
        }
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
                image = conversionBitmapParseFile(selectedImage);


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
                }else if (image == null) {
                    Log.e(APP_TAG, "no profile picture");
                    Toast.makeText(getApplicationContext(), "Please select a profile picture", Toast.LENGTH_SHORT).show();
                } else if (bio.length() == 0) {
                   Log.e(APP_TAG, "No bio");
                    Toast.makeText(getApplicationContext(), "Please enter a bio", Toast.LENGTH_SHORT).show();
                }  else{
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
                File photoFile = new File(getRealPathFromURI(this, imageUri));
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
                Toast.makeText(this, "You haven't picked Image",Toast.LENGTH_LONG).show();
            }
        }
    }

    private void signUp() {
        // Create the ParseUser
        User user = new User();

        // Set core properties
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        user.setBio(bio);
        user.setHomeState(state);
        user.setFollowers(0);
        user.setFollowing(0);
        user.setFavorites(0);

        // Invoke signUpInBackground
        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    // Hooray! Let them use the app now.
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
                    Toast.makeText(SignUpActivity.this, e.toString(), Toast.LENGTH_LONG).show(); // TODO cut down e.toString()
                    e.printStackTrace();
                }
            }
        });
    }

    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public static ParseFile conversionBitmapParseFile(Bitmap imageBitmap){
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.PNG,100,byteArrayOutputStream);
        byte[] imageByte = byteArrayOutputStream.toByteArray();
        ParseFile parseFile = new ParseFile("image_file.png",imageByte);
        return parseFile;
    }

    // TODO user for camera
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

    // TODO user for camera
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
}

