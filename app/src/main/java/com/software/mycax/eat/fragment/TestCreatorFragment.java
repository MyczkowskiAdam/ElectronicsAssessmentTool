package com.software.mycax.eat.fragment;


import android.app.Fragment;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.software.mycax.eat.R;
import com.software.mycax.eat.Utils;
import com.software.mycax.eat.models.TestQuestion;
import com.software.mycax.eat.models.TestSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import mehdi.sakout.fancybuttons.FancyButton;

/**
 * A simple {@link Fragment} subclass.
 */
public class TestCreatorFragment extends Fragment implements View.OnClickListener {
    private Spinner questionNumberSpinner;
    private Animation fabOpen, fabClose;
    private int prevPosition;
    private EditText eTestTtle;
    private List<EditText> inputQuestionList, inputAnswerList;
    private FirebaseAuth mAuth;

    public TestCreatorFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle(R.string.menu_testcreator);
        mAuth = FirebaseAuth.getInstance();
        final View v = inflater.inflate(R.layout.fragment_test_creator, container, false);

        eTestTtle = v.findViewById(R.id.test_title_edit_text);
        EditText eQuestion1 = v.findViewById(R.id.q1_edit_text);
        EditText eQuestion2 = v.findViewById(R.id.q2_edit_text);
        EditText eQuestion3 = v.findViewById(R.id.q3_edit_text);
        EditText eQuestion4 = v.findViewById(R.id.q4_edit_text);
        EditText eQuestion5 = v.findViewById(R.id.q5_edit_text);
        inputQuestionList = new ArrayList<>();
        inputQuestionList.add(eQuestion1);
        inputQuestionList.add(eQuestion2);
        inputQuestionList.add(eQuestion3);
        inputQuestionList.add(eQuestion4);
        inputQuestionList.add(eQuestion5);

        EditText eAnswer1 = v.findViewById(R.id.a1_edit_text);
        EditText eAnswer2 = v.findViewById(R.id.a2_edit_text);
        EditText eAnswer3 = v.findViewById(R.id.a3_edit_text);
        EditText eAnswer4 = v.findViewById(R.id.a4_edit_text);
        EditText eAnswer5 = v.findViewById(R.id.a5_edit_text);
        inputAnswerList = new ArrayList<>();
        inputAnswerList.add(eAnswer1);
        inputAnswerList.add(eAnswer2);
        inputAnswerList.add(eAnswer3);
        inputAnswerList.add(eAnswer4);
        inputAnswerList.add(eAnswer5);

        final List<LinearLayout> questionLayoutList = new ArrayList<>();
        LinearLayout questionContainer1 = v.findViewById(R.id.containerQuestion1);
        LinearLayout questionContainer2 = v.findViewById(R.id.containerQuestion2);
        LinearLayout questionContainer3 = v.findViewById(R.id.containerQuestion3);
        LinearLayout questionContainer4 = v.findViewById(R.id.containerQuestion4);
        LinearLayout questionContainer5 = v.findViewById(R.id.containerQuestion5);
        questionLayoutList.add(questionContainer1);
        questionLayoutList.add(questionContainer2);
        questionLayoutList.add(questionContainer3);
        questionLayoutList.add(questionContainer4);
        questionLayoutList.add(questionContainer5);

        fabOpen = AnimationUtils.loadAnimation(getActivity(),R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation(getActivity(),R.anim.fab_close);
        questionNumberSpinner = v.findViewById(R.id.num_of_q_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.number_of_q, R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        questionNumberSpinner.setAdapter(adapter);

        prevPosition = questionNumberSpinner.getSelectedItemPosition();
        questionNumberSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                for (int i = 0; i < 5; i++) {
                    if (position >= i) {
                        questionLayoutList.get(i).setVisibility(View.VISIBLE);
                        if (prevPosition < position) questionLayoutList.get(i).startAnimation(fabOpen);
                    } else {
                        questionLayoutList.get(i).setVisibility(View.GONE);
                        if(prevPosition > position) questionLayoutList.get(i).startAnimation(fabClose);
                    }
                }
                prevPosition = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        FancyButton bUploadTest = v.findViewById(R.id.upload_test_button);
        bUploadTest.setOnClickListener(this);
        return v;
    }



    @Override
    public void onClick(final View v) {
        if (v.getId() == R.id.upload_test_button) {
            if (Utils.isTestInputValid(v, questionNumberSpinner.getSelectedItemPosition()+1, inputAnswerList, inputQuestionList, eTestTtle)) {
                final FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
                mDatabase.getReference().child(Utils.CHILD_REF_USERS).child(Objects.requireNonNull(mAuth.getUid())).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String teacherCode = dataSnapshot.child(Utils.CHILD_REF_TEACHER_CODE).getValue(String.class);
                        Log.d(Utils.getTag(), "onDataChange: read value success");
                        String key = mDatabase.getReference(Utils.CHILD_REF_TESTS).push().getKey();
                        TestSet testSet = new TestSet("picUrl", key, teacherCode, eTestTtle.getText().toString());
                        for (int i = 0; i <= questionNumberSpinner.getSelectedItemPosition(); i++) {
                            testSet.addQuestion(new TestQuestion(inputQuestionList.get(i).getText().toString(), inputAnswerList.get(i).getText().toString()));
                        }
                        mDatabase.getReference().child(Utils.CHILD_REF_TESTS).child(Objects.requireNonNull(key)).setValue(testSet).addOnSuccessListener(new OnSuccessListener<Void>() {
                            // Upload new user's data to Firebase database
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(Utils.getTag(), "createTestDatabaseEntry:success");
                                Snackbar.make(v, R.string.test_upload_success, Snackbar.LENGTH_LONG).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(Utils.getTag(), "createTestDatabaseEntry:failure; deleting user", e.getCause());
                                Snackbar.make(v, R.string.test_upload_failed, Snackbar.LENGTH_LONG).show();
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Failed to read value
                        Log.e(Utils.getTag(), "onCancelled: failed to read value", error.toException());
                    }
                });
            }
        }
    }

}
