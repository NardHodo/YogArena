package com.example.yogarena;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.button.MaterialButton;

public class LogoutDialog extends DialogFragment {

    private MaterialButton cancelLogout, confirmLogout;

    //Apply full screen dialog style
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogTheme);
        setCancelable(false);
    }

    //Declare dialog properties and button functions
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_logout, container, false);

        cancelLogout = view.findViewById(R.id.button_cancel);
        confirmLogout = view.findViewById(R.id.button_confirm);

        cancelLogout.setOnClickListener(v -> dismiss());

        return view;
    }

    //idk
    @Override
    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();
        if (dialog != null && dialog.getWindow() != null) {
            dialog.getWindow().setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            );
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
    }
}
