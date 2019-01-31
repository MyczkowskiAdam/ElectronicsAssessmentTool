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
public class ManageStudentsFragment extends Fragment {

    public ManageStudentsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle(R.string.menu_manage_students);
        return inflater.inflate(R.layout.fragment_manage_students, container, false);
    }
}
