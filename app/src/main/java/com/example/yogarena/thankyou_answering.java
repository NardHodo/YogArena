package com.example.yogarena;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link thankyou_answering#newInstance} factory method to
 * create an instance of this fragment.
 */
public class thankyou_answering extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public thankyou_answering() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment thankyou_answering.
     */
    // TODO: Rename and change types and number of parameters
    public static thankyou_answering newInstance(String param1, String param2) {
        thankyou_answering fragment = new thankyou_answering();
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
        View view = inflater.inflate(R.layout.fragment_thankyou_answering, container, false);

        Button Finish_button = view.findViewById(R.id.Finish_button);

        Finish_button.setOnClickListener(v -> {
            Profile_Setup profileSetup = new Profile_Setup();
            getActivity().getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.fade_in,R.anim.fade_out)
                    .replace(R.id.thank_you_mess, profileSetup)
                    .addToBackStack(null)
                    .commit();
        });


        return view;
    }
}