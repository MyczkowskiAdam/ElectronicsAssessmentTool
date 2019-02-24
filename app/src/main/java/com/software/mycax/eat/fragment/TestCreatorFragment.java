package com.software.mycax.eat.fragment;


import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.software.mycax.eat.R;
import com.software.mycax.eat.Utils;
import com.software.mycax.eat.models.TestQuestion;
import com.software.mycax.eat.models.TestSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class TestCreatorFragment extends Fragment implements View.OnClickListener {
    private ListView listView;
    private Context context;

    public TestCreatorFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle(R.string.menu_testcreator);
        View v = inflater.inflate(R.layout.fragment_test_creator, container, false);
        context = v.getContext();
        Button button_create = v.findViewById(R.id.button_create);
        button_create.setOnClickListener(this);
        Button button_display = v.findViewById(R.id.button_display);
        button_display.setOnClickListener(this);
        listView = v.findViewById(R.id.list);
        return v;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button_create) {
            FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
            String key = mDatabase.getReference("tests").push().getKey();
            TestSet testSet = new TestSet("picUrl", key, "SCG-FS", "Potential dividers");
            testSet.addQuestion(new TestQuestion("Question 1", "Answer 1"));
            testSet.addQuestion(new TestQuestion("Question 2", "Answer 2"));
            testSet.addQuestion(new TestQuestion("Question 3", "Answer 3"));
            mDatabase.getReference().child("tests").child(Objects.requireNonNull(key)).setValue(testSet).addOnSuccessListener(new OnSuccessListener<Void>() {
                // Upload new user's data to Firebase database
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(Utils.getTag(), "createTestDatabaseEntry:success");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w(Utils.getTag(), "createTestDatabaseEntry:failure; deleting user", e.getCause());
                }
            });
        } else if (v.getId() == R.id.button_display) {
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
            mDatabase.child("tests").orderByChild("teacherCode").equalTo("SCG-FS").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //create a list of ManageStudent items
                    ArrayList<String> questionStringList= new ArrayList<>();
                    Log.d(Utils.getTag(), "onDataChange: read value success");
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        TestSet testSet = userSnapshot.getValue(TestSet.class);
                        Log.d(Utils.getTag(), "userSnapshot: added");
                        if (testSet != null) {
                            List<TestQuestion> questions = testSet.getQuestionList();
                            for (TestQuestion question : questions) {
                                questionStringList.add("testUid:" + testSet.getTestUid() + "Q:" + question.getQuestion() + "A:" + question.getAnswer());
                                Log.d(Utils.getTag(), "testUid:" + testSet.getTestUid() + "Q:" + question.getQuestion() + "A:" + question.getAnswer());
                            }
                        }
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(context,
                            android.R.layout.simple_list_item_1, questionStringList);
                    listView.setAdapter(adapter);
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
