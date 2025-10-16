package com.example.yogarena;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class Options extends AppCompatActivity {

    public ImageButton backButton;
    public MaterialButton sounds, notifications, account, camera;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.options);
        hideSystemUI();

        backButton = findViewById(R.id.back_button_dark);
        backButton.setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });

        sounds = findViewById(R.id.sounds_and_display);
        notifications = findViewById(R.id.notifications);
        account = findViewById(R.id.account);
        camera = findViewById(R.id.camera);

        camera.setOnClickListener(v -> {
            openCameraSettings();
        });

        notifications.setOnClickListener(v ->{
            openNotificationsSettings();
        });

        account.setOnClickListener(v -> {
            openLogoutSettings();
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

    private void openCameraSettings(){
        Intent cameraSettings = new Intent(this, OptionsCamera.class);
        startActivity(cameraSettings);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void openNotificationsSettings(){
        Intent notificationSettings = new Intent(this, OptionsNotifications.class);
        startActivity(notificationSettings);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void openLogoutSettings(){
        Intent logoutSettings = new Intent(this, OptionsLogout.class);
        startActivity(logoutSettings);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}
