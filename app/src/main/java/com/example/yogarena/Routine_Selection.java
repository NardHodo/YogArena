package com.example.yogarena;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class Routine_Selection extends AppCompatActivity {

    public ImageButton backButton, nextLeft, nextRight;
    private Fragment[] routines;
    private int currentIndex = 0;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.routine_selection);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        hideSystemUI();
        nextLeft = findViewById(R.id.next_button_left);
        nextRight = findViewById(R.id.next_button_right);

        if(savedInstanceState == null){
            Fragment defaultRoutine = new YogArena_Routine_1();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.yoga_art_container, defaultRoutine);
            transaction.commit();
            nextLeft.setVisibility(View.GONE);
        }

        backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> {
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        routines = new Fragment[]{
                new YogArena_Routine_1(),
                new YogArena_Routine_2(),
                new YogArena_Routine_3()
                };

        nextRight.setOnClickListener(v ->{
            if(currentIndex < routines.length){
                currentIndex++;
                showRoutine(currentIndex);
                updateButtons();
            }
        });

        nextLeft.setOnClickListener(v ->{
            if(currentIndex > 0){
                currentIndex--;
                showRoutine(currentIndex);
                updateButtons();
            }
        });
    }

    private void showRoutine(int index){
        FragmentManager fragment = getSupportFragmentManager();
        FragmentTransaction transaction = fragment.beginTransaction();

        transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        transaction.replace(R.id.yoga_art_container, routines[index]);
        transaction.commit();

    }

    private void updateButtons() {
        nextLeft.setVisibility(currentIndex == 0 ? View.GONE : View.VISIBLE);
        nextRight.setVisibility(currentIndex == routines.length - 1 ? View.GONE : View.VISIBLE);
    }


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