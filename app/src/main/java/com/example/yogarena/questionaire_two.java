package com.example.yogarena;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link questionaire_two#newInstance} factory method to
 * create an instance of this fragment.
 */
public class questionaire_two extends Fragment {



    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;

    private QuestionaireViewModel viewModel;
    private RadioGroup radioGroup;

    private Button nextButton;
    private Button backButton;

    public questionaire_two() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment questionaire_two.
     */
    // TODO: Rename and change types and number of parameters
    public static questionaire_two newInstance(String param1, String param2) {
        questionaire_two fragment = new questionaire_two();
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
        View view = inflater.inflate(R.layout.fragment_questionaire_two, container, false);

        viewModel = new ViewModelProvider(requireActivity()).get(QuestionaireViewModel.class);
        RadioGroup radioGroup = view.findViewById(R.id.radioGroup);

         nextButton = view.findViewById(R.id.Next_Button);
         backButton = view.findViewById(R.id.Back_Button);




        nextButton.setOnClickListener(v -> {
            int selectedID = radioGroup.getCheckedRadioButtonId();
            if(selectedID == -1){
                Toast.makeText(requireContext(), "Please select an option", Toast.LENGTH_SHORT).show();
                return;
            }
            RadioButton selectedRadioButton = view.findViewById(selectedID);
            String reason = selectedRadioButton.getText().toString();

            viewModel.setYogaReasons(reason);

            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.questionaire_two, new questionaire_three())
                    .addToBackStack(null)
                    .commit();
        });

        backButton.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        return view;
    }
}