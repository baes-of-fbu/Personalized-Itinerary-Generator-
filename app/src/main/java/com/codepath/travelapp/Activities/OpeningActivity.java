package com.codepath.travelapp.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.codepath.travelapp.R;
import com.parse.ParseUser;

import java.util.Timer;
import java.util.TimerTask;

public class OpeningActivity extends AppCompatActivity {
    private ImageSwitcher imageSwitcher;
    private int[] gallery = {R.drawable.seattle1, R.drawable.seattle2, R.drawable.seattle3, R.drawable.seattle4, R.drawable.seattle5, R.drawable.seattle6, R.drawable.seattle7, R.drawable.seattle8, R.drawable.seattle9, R.drawable.seattle10};
    private int position = 0;
    private Timer timer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opening);
        imageSwitcher = findViewById(R.id.imageSwitcher);
        start();
        imageSwitcher.setFactory(new ViewSwitcher.ViewFactory() {

            public View makeView()
            {
                ImageView imageView = new ImageView(OpeningActivity.this);
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);

//                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//                imageView.setLayoutParams(params);

                return imageView;
            }
        });

        // Set animations http://danielme.com/2013/08/18/diseno-android-transiciones-entre-activities/
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        Animation fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out);
        imageSwitcher.setInAnimation(fadeIn);
        imageSwitcher.setOutAnimation(fadeOut);
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(OpeningActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        Button loginBtn = findViewById(R.id.loginBtn);
        Button signUpBtn = findViewById(R.id.signUpBtn);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OpeningActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OpeningActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
    }
    public void start()
    {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {

            public void run() {
                // avoid exception: "Only the original thread that created a view hierarchy can touch its views"
                runOnUiThread(new Runnable() {
                    public void run() {
                        imageSwitcher.setImageResource(gallery[position]);
                        position++;
                        if (position == 10)
                        {
                            position = 0;
                        }
                    }
                });
            }

        }, 0, 5000);

    }

    public void stop()
    {
        timer.cancel();
    }
}
