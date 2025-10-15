package com.example.yogarena;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class Main_Menu extends AppCompatActivity{

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private TextView playerNameTextView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        playerNameTextView = findViewById(R.id.player_name);

        loadUserData();
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

}
