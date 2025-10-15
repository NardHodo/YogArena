package com.example.yogarena;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link questionaire_one#newInstance} factory method to
 * create an instance of this fragment.
 */
public class questionaire_one extends Fragment {

    private QuestionaireViewModel viewModel;
    private EditText inputedAge;
    private RadioGroup radioGroupExperience;
    private Button nextButton;
    private Button backButton;


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public questionaire_one() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment questionaire_one.
     */
    // TODO: Rename and change types and number of parameters
    public static questionaire_one newInstance(String param1, String param2) {
        questionaire_one fragment = new questionaire_one();
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
        View view = inflater.inflate(R.layout.fragment_questionaire_one, container, false);

        viewModel = new ViewModelProvider(requireActivity()).get(QuestionaireViewModel.class);

        inputedAge = view.findViewById(R.id.Inputed_Age);
        radioGroupExperience = view.findViewById(R.id.radioGroup);
        nextButton = view.findViewById(R.id.Next_Button);
        backButton = view.findViewById(R.id.Back_Button);






        nextButton.setOnClickListener(v -> {
            String ageStr = inputedAge.getText().toString().trim();
            if (ageStr.isEmpty()){
                inputedAge.setError("Please enter your age");
                return;
            }
            int age = Integer.parseInt(ageStr);

            int selectedID = radioGroupExperience.getCheckedRadioButtonId();
            if(selectedID == -1){
                Toast.makeText(requireContext(), "Please select an option", Toast.LENGTH_SHORT).show();
                return;
            }

            RadioButton selectedRadioButton = view.findViewById(selectedID);
            String experience = selectedRadioButton.getText().toString();

            viewModel.setAge(age);
            viewModel.setYogaExperience(experience);

            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.questionaire_one, new questionaire_two())
                    .addToBackStack(null)
                    .commit();
        });



        backButton.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        return view;
    }
}