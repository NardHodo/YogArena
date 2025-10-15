package com.example.yogarena;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

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
 * Use the {@link questionaire_three#newInstance} factory method to
 * create an instance of this fragment.
 */
public class questionaire_three extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private QuestionaireViewModel viewModel;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private RadioGroup radioGroupHC;
    private EditText health_condition;

    private Button finishButton;
    private Button backButton;

    public questionaire_three() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment questionaire_three.
     */
    // TODO: Rename and change types and number of parameters
    public static questionaire_three newInstance(String param1, String param2) {
        questionaire_three fragment = new questionaire_three();
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
        View view = inflater.inflate(R.layout.fragment_questionaire_three, container, false);

        viewModel = new ViewModelProvider(requireActivity()).get(QuestionaireViewModel.class);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        RadioGroup radioGroupConditions = view.findViewById(R.id.radioGroupHC);
        EditText healthDetails = view.findViewById(R.id.health_condition);

        finishButton = view.findViewById(R.id.Finish_button);
        backButton = view.findViewById(R.id.Back_Button);

        finishButton.setOnClickListener(v -> {
            int selectedID = radioGroupConditions.getCheckedRadioButtonId();
            if (selectedID == -1) {
                Toast.makeText(requireContext(), "Please select an option", Toast.LENGTH_SHORT).show();
                return;
            }

            RadioButton selectedRadioButton = view.findViewById(selectedID);
            String hasConditions = selectedRadioButton.getText().toString();
            String conditionDetails = healthDetails.getText().toString();

            viewModel.setHasHealthCondition(hasConditions);
            viewModel.setHealthConditionDetails(conditionDetails);

            saveAllDataToFirestore();

        });

        backButton.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });
        return view;
    }

    private void saveAllDataToFirestore() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null){
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }
        String userID = currentUser.getUid();

        Map<String, Object> questionaireData = new HashMap<>();
        questionaireData.put("age", viewModel.getAge());
        questionaireData.put("yogaExperience", viewModel.getYogaExperience());
        questionaireData.put("yoga_reasons", viewModel.getYogaReasons());
        questionaireData.put("has_health_condition", viewModel.getHasHealthCondition());
        questionaireData.put("health_condition_details", viewModel.getHealthConditionDetails());

        db.collection("users").document(userID)
                .update(questionaireData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(requireContext(), "Data saved successfully", Toast.LENGTH_SHORT).show();
                    requireActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.questionaire_three, new Profile_Setup())
                            .addToBackStack(null)
                            .commit();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Failed to save data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });


    }
}