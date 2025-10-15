package com.example.yogarena;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterDialog extends DialogFragment {



    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private EditText createdUsername, inputedPassword, inputtedConfirmPassword;

    private static final String TAG = "RegisterDialog";


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_register, null);


        //Firebase Init
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        //views sa layout
        createdUsername = view.findViewById(R.id.CreatedUsername);
        inputedPassword = view.findViewById(R.id.InputedPassword);
        inputtedConfirmPassword = view.findViewById(R.id.Inputted_Confirm_Password);
        Button cancelButton = view.findViewById(R.id.Cancel_Button);
        Button nextButton = view.findViewById(R.id.Next_Button);


        cancelButton.setOnClickListener(v -> dismiss());

        nextButton.setOnClickListener(v -> {
            validateAndRegisterUser();
        });

        return new AlertDialog.Builder(requireContext())
                .setView(view)
                .create();
    }

    private void validateAndRegisterUser(){
        String username = createdUsername.getText().toString().trim();
        String password = inputedPassword.getText().toString();
        String confirmPassword = inputtedConfirmPassword.getText().toString().trim();

        if(username.isEmpty()){
            createdUsername.setError("Username is required");
            createdUsername.requestFocus();
            return;
        }
        if(password.isEmpty()){
            inputedPassword.setError("Password is required");
            inputedPassword.requestFocus();
            return;
        }
        if(password.length() < 6){
         inputedPassword.setError("Password must be at least 6 characters");
         inputedPassword.requestFocus();
         return;
        }
        if(!password.equals(confirmPassword)){
            inputtedConfirmPassword.setError("Passwords do not match");
            inputtedConfirmPassword.requestFocus();
            return;
        }

        db.collection("users")
                .whereEqualTo("username", username)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().isEmpty()) {
                            registerUserInFirebaseAuth(username, password);
                        }
                        else {
                            createdUsername.setError("Username already exists");
                            createdUsername.requestFocus();
                        }
                    }else {
                        Toast.makeText(getContext(), "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void registerUserInFirebaseAuth(final String username, String password){

        //Placeholder string para sa dummy email
        String dummyEmail = username + "@yogarena.app";

        mAuth.createUserWithEmailAndPassword(dummyEmail,password)
                .addOnCompleteListener(requireActivity(), task -> {
                    if(task.isSuccessful()){
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if(firebaseUser != null){
                            String userID = firebaseUser.getUid();
                            saveUserDataToFirestore(userID, username);
                        }

                    }else{
                        Toast.makeText(getContext(), "Registration Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "FirebaseAuth registration failed", task.getException());
                    }

                });
    }

    private void saveUserDataToFirestore(String userID, String username){
        Map<String, Object> userData = new HashMap<>();
        userData.put("username", username);
        userData.put("createdAt", com.google.firebase.firestore.FieldValue.serverTimestamp());

        //add data sa db
        db.collection("users").document(userID).set(userData)
                .addOnCompleteListener(aVoid -> {
                    Toast.makeText(getContext(), "Registration successful!", Toast.LENGTH_SHORT).show();

                    dismiss();
                    requireActivity().getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                            .replace(R.id.loadingFragment, new health_questions())
                            .addToBackStack(null)
                            .commit();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error saving user data bobo ka kase: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
