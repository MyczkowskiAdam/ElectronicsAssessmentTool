package com.software.mycax.eat.acitivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
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

import mehdi.sakout.fancybuttons.FancyButton;

public class UpdateTestActivity extends AppCompatActivity implements View.OnClickListener{
    private int prevPosition;
    private EditText eTestTtle;
    private List<EditText> inputQuestionList, inputAnswerList;
    private List<LinearLayout> questionLayoutList;
    private FirebaseAuth mAuth;
    private String testUid;
    private int testSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_update_test);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle extras = getIntent().getExtras();
        if(extras != null)
            testUid = extras.getString("testUid");

        eTestTtle = findViewById(R.id.test_title_edit_text);
        EditText eQuestion1 = findViewById(R.id.q1_edit_text);
        EditText eQuestion2 = findViewById(R.id.q2_edit_text);
        EditText eQuestion3 = findViewById(R.id.q3_edit_text);
        EditText eQuestion4 = findViewById(R.id.q4_edit_text);
        EditText eQuestion5 = findViewById(R.id.q5_edit_text);
        inputQuestionList = new ArrayList<>();
        inputQuestionList.add(eQuestion1);
        inputQuestionList.add(eQuestion2);
        inputQuestionList.add(eQuestion3);
        inputQuestionList.add(eQuestion4);
        inputQuestionList.add(eQuestion5);

        EditText eAnswer1 = findViewById(R.id.a1_edit_text);
        EditText eAnswer2 = findViewById(R.id.a2_edit_text);
        EditText eAnswer3 = findViewById(R.id.a3_edit_text);
        EditText eAnswer4 = findViewById(R.id.a4_edit_text);
        EditText eAnswer5 = findViewById(R.id.a5_edit_text);
        inputAnswerList = new ArrayList<>();
        inputAnswerList.add(eAnswer1);
        inputAnswerList.add(eAnswer2);
        inputAnswerList.add(eAnswer3);
        inputAnswerList.add(eAnswer4);
        inputAnswerList.add(eAnswer5);

        questionLayoutList = new ArrayList<>();
        LinearLayout questionContainer1 = findViewById(R.id.containerQuestion1);
        LinearLayout questionContainer2 = findViewById(R.id.containerQuestion2);
        LinearLayout questionContainer3 = findViewById(R.id.containerQuestion3);
        LinearLayout questionContainer4 = findViewById(R.id.containerQuestion4);
        LinearLayout questionContainer5 = findViewById(R.id.containerQuestion5);
        questionLayoutList.add(questionContainer1);
        questionLayoutList.add(questionContainer2);
        questionLayoutList.add(questionContainer3);
        questionLayoutList.add(questionContainer4);
        questionLayoutList.add(questionContainer5);

        Animation fabOpen = AnimationUtils.loadAnimation(this, R.anim.fab_open);
        Animation fabClose = AnimationUtils.loadAnimation(this, R.anim.fab_close);

        FancyButton bUploadTest = findViewById(R.id.upload_test_button);
        bUploadTest.setOnClickListener(this);

        getTestData();
    }

    private void getTestData() {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("tests").child(testUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                TestSet testSet = dataSnapshot.getValue(TestSet.class);
                if (testSet != null) {
                    eTestTtle.setText(testSet.getTestTopic());
                    Log.d(Utils.getTag(), "testSize:" + testSet.getTestSize());
                    for (int i = 0; i < 5; i++) {
                        if(i < testSet.getTestSize()) {
                            testSize++;
                            questionLayoutList.get(i).setVisibility(View.VISIBLE);
                            List<TestQuestion> testQuestions = testSet.getQuestionList();
                            inputQuestionList.get(i).setText(testQuestions.get(i).getQuestion());
                            inputAnswerList.get(i).setText(testQuestions.get(i).getAnswer());
                        } else {
                            questionLayoutList.get(i).setVisibility(View.GONE);
                        }
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Log.e(Utils.getTag(), "onCancelled: failed to read value", error.toException());
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(final View v) {
        if (v.getId() == R.id.upload_test_button) {
            if (Utils.isTestInputValid(v, testSize, inputAnswerList, inputQuestionList, eTestTtle)) {
                final FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
                mDatabase.getReference().child("users").child(Objects.requireNonNull(mAuth.getUid())).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String teacherCode = dataSnapshot.child("teacherCode").getValue(String.class);
                        Log.d(Utils.getTag(), "onDataChange: read value success");
                        String key = mDatabase.getReference("tests").push().getKey();
                        TestSet testSet = new TestSet("picUrl", key, teacherCode, eTestTtle.getText().toString());
                        for (int i = 0; i < testSize; i++) {
                            testSet.addQuestion(new TestQuestion(inputQuestionList.get(i).getText().toString(), inputAnswerList.get(i).getText().toString()));
                        }
                        mDatabase.getReference().child("tests").child(Objects.requireNonNull(key)).setValue(testSet).addOnSuccessListener(new OnSuccessListener<Void>() {
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
