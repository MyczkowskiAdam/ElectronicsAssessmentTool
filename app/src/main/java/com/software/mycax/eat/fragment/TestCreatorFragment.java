package com.software.mycax.eat.fragment;


import android.app.Fragment;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.software.mycax.eat.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class TestCreatorFragment extends Fragment {


    public TestCreatorFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle(R.string.menu_testcreator);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_test_creator, container, false);
    }

}
