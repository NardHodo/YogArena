package com.example.yogarena;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Profile_Setup#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Profile_Setup extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private EditText inputName;
    private RadioGroup grpGender;
    private Button Confirm_button;


    public Profile_Setup() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Profile_Setup.
     */
    // TODO: Rename and change types and number of parameters
    public static Profile_Setup newInstance(String param1, String param2) {
        Profile_Setup fragment = new Profile_Setup();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile__setup, container, false);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        inputName = view.findViewById(R.id.Input_Name);
        grpGender = view.findViewById(R.id.grpGender);
        Confirm_button = view.findViewById(R.id.Confirm_button);

        Confirm_button.setOnClickListener(v -> {
            saveProfileData();
        });

        return view;
    }

    private void saveProfileData() {
        String fullname = inputName.getText().toString().trim();
        if (fullname.isEmpty()) {
            inputName.setError("Please enter your name");
            inputName.requestFocus();
            return;
        }
        int selectedID = grpGender.getCheckedRadioButtonId();
        if (selectedID == -1) {
            Toast.makeText(requireContext(), "Please select a gender", Toast.LENGTH_SHORT).show();
            return;
        }
        RadioButton selectedRadioButton = getView().findViewById(selectedID);
        String gender = selectedRadioButton.getText().toString();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null){
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }
        String userID = currentUser.getUid();

        Map<String, Object> profileData = new HashMap<>();
        profileData.put("fullName", fullname);
        profileData.put("gender", gender);

        db.collection("users").document(userID)
                .update(profileData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(requireContext(), "Profile data saved successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getActivity(), Main_Menu.class);
                    startActivity(intent);
                    if (getActivity() != null) {
                      getActivity().finish();
                    }
                })
        .addOnFailureListener(e -> {
            Toast.makeText(requireContext(), "Failed to save profile data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });

    }
}