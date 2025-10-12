package com.example.yogarena;

import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Random;

public class Loading extends AppCompatActivity {

    private ProgressBar loading;
    private TextView triviaHeader;
    private TextView triviaDescription;
    private int progress = 0;
    private final Handler handler = new Handler();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blank_screen_transition);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        hideSystemUI();

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
           setContentView(R.layout.loading_screen);
           overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
           loading = findViewById(R.id.progressLinear);
           triviaHeader = findViewById(R.id.trivia_header);
           triviaDescription = findViewById(R.id.trivia);

           String[] triviaTitles = {
                   getString(R.string.trivia_header_1),
                   getString(R.string.trivia_header_2),
                   getString(R.string.trivia_header_3),
                   getString(R.string.trivia_header_4),
                   getString(R.string.trivia_header_5)
           };

           String[] triviaDescriptions = {
                   getString(R.string.trivia_desc_1),
                   getString(R.string.trivia_desc_2),
                   getString(R.string.trivia_desc_3),
                   getString(R.string.trivia_desc_4),
                   getString(R.string.trivia_desc_5)
           };

           Random random = new Random();
           int triviaIndex = random.nextInt(triviaTitles.length);

           triviaHeader.setText(triviaTitles[triviaIndex]);
           triviaDescription.setText(triviaDescriptions[triviaIndex]);
           startLoading();
        }, 800);
    }

    private void startLoading(){
        new Thread(() -> {
            while (progress < 100){
                progress += 2;
                handler.post(() -> {
                    if(loading != null){
                        loading.setProgress(progress,true);
                    }
                    });
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            handler.post(()-> {
               Intent intent = new Intent(Loading.this, Main_Menu.class);
               startActivity(intent);
               overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
               finish();
            });
        }).start();
    }

    //Para Sakop Buong Screen
    @SuppressWarnings("deprecation")
    private void hideSystemUI(){

        if(getSupportActionBar()!= null){
            getSupportActionBar().hide();
        }

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
    }

}
