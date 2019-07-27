package com.codepath.travelapp.Fragments;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Point;
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
import java.io.File;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;
import static com.parse.ParseUser.getCurrentUser;

public class ProfileDialogFragment extends DialogFragment {

    private User user;
    private Button imageBtn;
    private ImageView ivProfileImage;
    private EditText etBio;
    private Spinner spHomeState;
    private String newBio;
    private String newState;
    private ParseFile newImage;
    private final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    private Bitmap selectedImage;

    public ProfileDialogFragment() { }

    static ProfileDialogFragment newInstance() {
        ProfileDialogFragment frag = new ProfileDialogFragment();
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_profile, container);
    }

    public void onResume() {
        // Store access variables for window and blank point
        Window window = getDialog().getWindow();
        Point size = new Point();
        // Store dimensions of the screen in `size`
        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(size);
        // Set the width of the dialog proportional to 75% of the screen width
        window.setLayout((size.x), WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
        // Call super onResume after sizing
        super.onResume();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        imageBtn = view.findViewById(R.id.imageBtn);
        ivProfileImage = view.findViewById(R.id.ivProfileImage);
        etBio = view.findViewById(R.id.etBio);
        spHomeState = view.findViewById(R.id.spHomeState);
        Button applyBtn = view.findViewById(R.id.applyBtn);

        imageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onLaunchGallery(view);
            }
        });

        applyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newBio = etBio.getText().toString();
                newState = spHomeState.getSelectedItem().toString();
                newImage = conversionBitmapParseFile(selectedImage);

                // Check to see all fields are entered
                if (newBio.length() == 0) {
                    Toast.makeText(getContext(), "Please enter a bio", Toast.LENGTH_SHORT).show();
                }else if(newState.length() > 2) {
                    Toast.makeText(getContext(), "Please select a State", Toast.LENGTH_SHORT).show();
                }else if(newImage == null) {
                    Toast.makeText(getContext(), "Please select a Profile picture", Toast.LENGTH_SHORT).show();
                }else{
                    ApplyChanges(newBio, newState, newImage);
                }


            }
        });
        SetViews();
    }

    private void onLaunchGallery(View view) {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (photoPickerIntent.resolveActivity(getContext().getPackageManager()) != null) {
            startActivityForResult(photoPickerIntent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                final Uri imageUri = data.getData();
                File photoFile = new File(getRealPathFromURI(getContext(), imageUri));
                try {
                   selectedImage = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), imageUri);
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
                Toast.makeText(getContext(), "You haven't picked Image",Toast.LENGTH_LONG).show();
            }
        }
    }

    private String getRealPathFromURI(Context context, Uri contentUri) {
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

    private static ParseFile conversionBitmapParseFile(Bitmap imageBitmap){
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.PNG,100,byteArrayOutputStream);
        byte[] imageByte = byteArrayOutputStream.toByteArray();
        ParseFile parseFile = new ParseFile("image_file.png",imageByte);
        return parseFile;
    }

    private void SetViews () {
        user = (User) User.getCurrentUser();
        etBio.setText(user.getBio());
        if (user.getProfileImage() != null) {
            ParseFile image = user.getProfileImage();
            Glide.with(getContext())
                    .load(image.getUrl())
                    .apply(RequestOptions.circleCropTransform())
                    .into(ivProfileImage);
        }
    }

    private void ApplyChanges(String newBio, String newState, ParseFile newImage) {
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
}
