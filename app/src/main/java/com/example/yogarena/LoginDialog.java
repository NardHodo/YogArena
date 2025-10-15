package com.example.yogarena;

import static android.content.ContentValues.TAG;

import android.app.Dialog;
import android.content.Intent;
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

import com.google.firebase.auth.FirebaseAuth;

public class LoginDialog extends DialogFragment {

    private FirebaseAuth mAuth;
    private EditText usernameInput, passwordInput;
    private Button loginButton, cancelButton;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_login, null);

        mAuth = FirebaseAuth.getInstance();

        usernameInput = view.findViewById(R.id.UsernameInput);
        passwordInput = view.findViewById(R.id.InputPassword);
        loginButton = view.findViewById(R.id.Login_Button);
        cancelButton = view.findViewById(R.id.Cancel_Button);

        loginButton.setOnClickListener(v -> loginUser());
        cancelButton.setOnClickListener(v -> dismiss());

        return new AlertDialog.Builder(requireContext())
                .setView(view)
                .create();
    }

    private void loginUser() {
        String username = usernameInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (username.isEmpty()) {
            usernameInput.setError("Username is required");
            usernameInput.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            passwordInput.setError("Password is required");
            passwordInput.requestFocus();
            return;
        }

        String dummyEmail = username + "@yogarena.app";

        mAuth.signInWithEmailAndPassword(dummyEmail, password)
                .addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "Login successful!", Toast.LENGTH_SHORT).show();
                        dismiss();
                        Intent intent = new Intent(getActivity(), Main_Menu.class);
                        startActivity(intent);
                        if (getActivity() != null) {
                            getActivity().finish();
                        }
                    } else {
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        Toast.makeText(getContext(), "Invalid username or password.", Toast.LENGTH_SHORT).show();
                    }
                });

    }
}
