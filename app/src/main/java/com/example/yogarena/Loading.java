package com.example.yogarena;

import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;

public class Loading extends AppCompatActivity {

    private ProgressBar loading;
    private int progress = 0;
    private final Handler handler = new Handler();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_screen);
        lo

    }

    private void loadGame(){
        new Thread() -> {
            while(progress < 100){
                progress++;
                handler.post(() -> {
                    loading.setProgress(progress);
                });
        }
    }
}
