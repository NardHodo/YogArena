package com.example.yogarena;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class Main_Menu extends AppCompatActivity{

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private TextView playerNameTextView;

    public MaterialButton playButton;
    public ImageButton playerProfile, settings;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        hideSystemUI();

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        playerNameTextView = findViewById(R.id.player_name);


        loadUserData();
        playButton = findViewById(R.id.play_button);
        playButton.setOnClickListener(v -> {
            loadRoutineSelection();
        });

        playerProfile = findViewById(R.id.profile_button);
        playerProfile.setOnClickListener(v -> {
            loadUserProfile();
        });

        settings = findViewById(R.id.settings);
        settings.setOnClickListener(v -> {
            loadOptions();
        });
    }

    private void loadRoutineSelection(){
        Intent routine = new Intent(this, Routine_Selection.class);
        startActivity(routine);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void loadOptions(){
        Intent options = new Intent(this, Options.class);
        startActivity(options);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void loadUserProfile(){
        Intent profile = new Intent(this, Player_Profile.class);
        startActivity(profile);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void loadUserData() {

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Log.e(TAG, "No user is logged in. Cannot fetch profile data.");
            playerNameTextView.setText("Guest");
            return;
        }

        String userId = currentUser.getUid();

        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {

                    if (documentSnapshot.exists()) {
                        String fullName = documentSnapshot.getString("fullName");
                        if (fullName != null && !fullName.isEmpty()) {
                            playerNameTextView.setText(fullName);
                        } else {
                            playerNameTextView.setText("Yogi");
                            Log.w(TAG, "User document exists but fullName is missing.");
                        }
                    } else {
                        Log.w(TAG, "User document does not exist for UID: " + userId);
                        playerNameTextView.setText("User");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching user data", e);
                    Toast.makeText(Main_Menu.this, "Failed to load profile.", Toast.LENGTH_SHORT).show();
                });
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
