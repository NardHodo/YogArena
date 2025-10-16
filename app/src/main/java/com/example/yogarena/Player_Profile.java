package com.example.yogarena;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.button.MaterialButton;

public class Player_Profile extends AppCompatActivity {

    MaterialButton profileOverview, profilePerformance, profileBadges, profileLeaderboards;
    ImageButton backButton;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player_profile);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        hideSystemUI();

        if(savedInstanceState == null){
            Fragment defaultFragment = new PlayerOverviewFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.main_content, defaultFragment);
            transaction.commit();

        }

        profileOverview = findViewById(R.id.profile_overview);
        profilePerformance = findViewById(R.id.profile_performance);
        profileBadges = findViewById(R.id.profile_badges);
        profileLeaderboards = findViewById(R.id.profile_leaderboards);
        backButton = findViewById(R.id.back_button);

        profileOverview.setOnClickListener(v -> loadFragment(new PlayerOverviewFragment()));
        profilePerformance.setOnClickListener(v -> loadFragment(new PlayerPerformanceFragment()));
        profileBadges.setOnClickListener(v -> loadFragment(new PlayerBadgesFragment()));
        profileLeaderboards.setOnClickListener(v -> loadFragment(new PlayerLeaderboards()));

        backButton.setOnClickListener(v ->{
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });

    }


    private void loadFragment(Fragment fragment){
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_content, fragment)
                .commit();
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
