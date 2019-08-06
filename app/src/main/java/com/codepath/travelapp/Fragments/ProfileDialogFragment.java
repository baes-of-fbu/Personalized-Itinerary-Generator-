package com.codepath.travelapp.Fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.codepath.travelapp.Activities.MainActivity;
import com.codepath.travelapp.Models.User;
import com.codepath.travelapp.R;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;

public class ProfileDialogFragment extends DialogFragment {

    private User user;
    private ImageView ivProfileImage;
    private EditText etBio;
    private Spinner spHomeState;
    private String newBio;
    private String newState;
    private ParseFile newImage;
    private final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;

    public ProfileDialogFragment() { }

    static ProfileDialogFragment newInstance() {
       return new ProfileDialogFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_profile, container);
    }

    public void onResume() {
        // Store access variables for window and blank point
        Window window = Objects.requireNonNull(getDialog()).getWindow();
        Point size = new Point();
        if (window != null) {
            Display display = window.getWindowManager().getDefaultDisplay();
            display.getSize(size);
            // Set the width of the dialog proportional to 75% of the screen width
            window.setLayout((size.x), WindowManager.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.CENTER);
            super.onResume();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button imageBtn = view.findViewById(R.id.imageBtn);
        ivProfileImage = view.findViewById(R.id.ivProfileImage);
        etBio = view.findViewById(R.id.etBio);
        spHomeState = view.findViewById(R.id.spHomeState);
        Button applyBtn = view.findViewById(R.id.applyBtn);

        imageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onLaunchGallery();
            }
        });

        applyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newBio = etBio.getText().toString();
                newState = spHomeState.getSelectedItem().toString();

                // Check to see all fields are entered
                if (newBio.length() == 0) {
                    Toast.makeText(getContext(), "Please enter a bio", Toast.LENGTH_SHORT)
                            .show();
                }else if(newState.length() > 2) {
                    Toast.makeText(getContext(), "Please select a State", Toast.LENGTH_SHORT)
                            .show();
                }else if(newImage == null) {
                    Toast.makeText(getContext(), "Please select a Profile picture",
                            Toast.LENGTH_SHORT).show();
                }else{
                    applyChanges(newBio, newState, newImage);
                }
            }
        });
        setViews();
    }

    private void onLaunchGallery() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media
                .EXTERNAL_CONTENT_URI);
        if (photoPickerIntent.resolveActivity(Objects.requireNonNull(getContext())
                .getPackageManager()) != null) {
            startActivityForResult(photoPickerIntent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                final Uri imageUri = data.getData();
                if (imageUri != null) {
                    Bitmap rotatedImage = rotateBitmapOrientation(imageUri.getPath());
                    Glide.with(this)
                            .load(rotatedImage)
                            .apply(RequestOptions.circleCropTransform())
                            .into(ivProfileImage);
                    ivProfileImage.setVisibility(View.VISIBLE);
                    if (rotatedImage != null) {
                        newImage = conversionBitmapParseFile(rotatedImage);
                    }
                }
            } else {
                Toast.makeText(getContext(), "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    private static ParseFile conversionBitmapParseFile(Bitmap imageBitmap){
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.PNG,100,byteArrayOutputStream);
        byte[] imageByte = byteArrayOutputStream.toByteArray();
        return new ParseFile("image_file.png",imageByte);
    }


    private void setViews() {
        user = (User) User.getCurrentUser();
        etBio.setText(user.getBio());
        if (user.getProfileImage() != null) {
            ParseFile image = user.getProfileImage();
            Glide.with(Objects.requireNonNull(getContext()))
                    .load(image.getUrl())
                    .apply(RequestOptions.circleCropTransform())
                    .into(ivProfileImage);
            newImage = user.getProfileImage();
        }
    }

    private void applyChanges(String newBio, String newState, ParseFile newImage) {
        user.setBio(newBio);
        user.setHomeState(newState);
        user.setProfileImage(newImage);

        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                SidebarFragment fragment = new SidebarFragment();
                MainActivity.fragmentManager.beginTransaction()
                        .replace(R.id.flContainer, fragment)
                        .addToBackStack(null)
                        .commit();
                dismiss();
            }
        });
    }

    private Bitmap rotateBitmapOrientation(String photoFilePath) {
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
        if (exif != null) {
            String orientString = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
            int orientation = orientString != null ? Integer.parseInt(orientString) : ExifInterface
                    .ORIENTATION_NORMAL;
            int rotationAngle = 0;
            if (orientation == ExifInterface.ORIENTATION_ROTATE_90) rotationAngle = 90;
            if (orientation == ExifInterface.ORIENTATION_ROTATE_180) rotationAngle = 180;
            if (orientation == ExifInterface.ORIENTATION_ROTATE_270) rotationAngle = 270;

            // Rotate Bitmap
            Matrix matrix = new Matrix();
            matrix.setRotate(rotationAngle, (float) bm.getWidth() / 2,
                    (float) bm.getHeight() / 2);
            return Bitmap.createBitmap(bm, 0, 0, bounds.outWidth, bounds.outHeight, matrix,
                    true);
        } else {
            return null;
        }
    }
}
