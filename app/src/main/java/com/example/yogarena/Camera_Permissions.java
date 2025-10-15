package com.example.yogarena;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;

public class Camera_Permissions extends AppCompatActivity {

    public MaterialButton exitGame;
    public MaterialButton allowCamera;


    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    Toast.makeText(this, "Welcome to YogArena!", Toast.LENGTH_SHORT).show();
                    transitionToLogin();
                } else {
                    Toast.makeText(this, "Camera Access is Required to Proceed", Toast.LENGTH_LONG).show();
                }
            });

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_permissions);
        hideSystemUI();

        exitGame = findViewById(R.id.exit_button);
        exitGame.setOnClickListener(v -> finish());

        allowCamera = findViewById(R.id.allow_button);
        allowCamera.setOnClickListener(v -> checkAndRequestCameraPermission());
    }

    private void checkAndRequestCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            // If permission is already granted, just proceed.
            Toast.makeText(this, "Permission was already granted.", Toast.LENGTH_SHORT).show();
        } else {
            // Directly ask for the permission.
            // The registered ActivityResultCallback gets the result of this request.
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void transitionToLogin(){
        LogIn loginFragment = new LogIn();

        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                .replace(R.id.loadingFragment, loginFragment)
                .addToBackStack(null)
                .commit();
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
