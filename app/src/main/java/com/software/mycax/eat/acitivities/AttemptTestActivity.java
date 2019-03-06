package com.software.mycax.eat.acitivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.software.mycax.eat.R;
import com.software.mycax.eat.Utils;
import com.software.mycax.eat.models.TestQuestion;
import com.software.mycax.eat.models.TestResults;
import com.software.mycax.eat.models.TestSet;

import java.util.ArrayList;
import java.util.List;

import mehdi.sakout.fancybuttons.FancyButton;

public class AttemptTestActivity extends AppCompatActivity implements View.OnClickListener {
    private String testUid;
    private TextView tTestTitle;
    private List<EditText> inputAnswerList;
    private List<TextView> inputQuestionList, correctAnswerstList;
    private List<LinearLayout> questionLayoutList;
    private Animation fabOpen;
    private int testSize, correctAnswers, adapterPosition;
    private FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        setContentView(R.layout.activity_attempt_test);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            testUid = extras.getString("testUid");
            adapterPosition = extras.getInt("adapterPosition");
        }

        fabOpen = AnimationUtils.loadAnimation(this,R.anim.fab_open);

        tTestTitle = findViewById(R.id.textView_test_title);
        TextView tQuestion1 = findViewById(R.id.textView_inputQ1);
        TextView tQuestion2 = findViewById(R.id.textView_inputQ2);
        TextView tQuestion3 = findViewById(R.id.textView_inputQ3);
        TextView tQuestion4 = findViewById(R.id.textView_inputQ4);
        TextView tQuestion5 = findViewById(R.id.textView_inputQ5);
        inputQuestionList = new ArrayList<>();
        inputQuestionList.add(tQuestion1);
        inputQuestionList.add(tQuestion2);
        inputQuestionList.add(tQuestion3);
        inputQuestionList.add(tQuestion4);
        inputQuestionList.add(tQuestion5);

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

        TextView tCorrectAnswer1 = findViewById(R.id.textView_correctAnswer1);
        TextView tCorrectAnswer2 = findViewById(R.id.textView_correctAnswer2);
        TextView tCorrectAnswer3 = findViewById(R.id.textView_correctAnswer3);
        TextView tCorrectAnswer4 = findViewById(R.id.textView_correctAnswer4);
        TextView tCorrectAnswer5 = findViewById(R.id.textView_correctAnswer5);
        correctAnswerstList =  new ArrayList<>();
        correctAnswerstList.add(tCorrectAnswer1);
        correctAnswerstList.add(tCorrectAnswer2);
        correctAnswerstList.add(tCorrectAnswer3);
        correctAnswerstList.add(tCorrectAnswer4);
        correctAnswerstList.add(tCorrectAnswer5);

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

        if (mUser != null) {
            getTestData();
            FancyButton bFinishTest = findViewById(R.id.finish_test_button);
            bFinishTest.setOnClickListener(this);
        }
    }

    private void getTestData() {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("tests").child(testUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                TestSet testSet = dataSnapshot.getValue(TestSet.class);
                if (testSet != null) {
                    tTestTitle.setText(testSet.getTestTopic());
                    Log.d(Utils.getTag(), "testSize:" + testSet.getTestSize());
                    for (int i = 0; i < 5; i++) {
                        if(i < testSet.getTestSize()) {
                            testSize++;
                            questionLayoutList.get(i).setVisibility(View.VISIBLE);
                            List<TestQuestion> testQuestions = testSet.getQuestionList();
                            inputQuestionList.get(i).setText(testQuestions.get(i).getQuestion());
                            correctAnswerstList.get(i).setText(testQuestions.get(i).getAnswer());
                            correctAnswerstList.get(i).setVisibility(View.GONE);
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
        if (v.getId() == R.id.finish_test_button) {
            if (Utils.isTestInputValid(v, testSize, inputAnswerList, null, null)) {
                new AlertDialog.Builder(v.getContext())
                        .setTitle(R.string.action_finish_test)
                        .setMessage(R.string.text_finish_test)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                for (int i = 0; i < testSize; i++) {
                                    if (inputAnswerList.get(i).getText().toString().equals(correctAnswerstList.get(i).getText().toString())) {
                                        correctAnswers++;
                                    } else {
                                        correctAnswerstList.get(i).setVisibility(View.VISIBLE);
                                        correctAnswerstList.get(i).setTextColor(getResources().getColor(android.R.color.holo_red_light));
                                        correctAnswerstList.get(i).startAnimation(fabOpen);
                                    }
                                }
                                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                                TestResults testResults = new TestResults(testUid, correctAnswers, testSize, true);
                                mDatabase.child("testResults").child(mUser.getUid()).child(testUid).setValue(testResults).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d(Utils.getTag(), "testResultsUpload:success");
                                            Snackbar.make(v, R.string.test_result_success, Snackbar.LENGTH_LONG).show();
                                            Intent resultIntent = new Intent();
                                            resultIntent.putExtra("isCompleted", true);
                                            resultIntent.putExtra("adapterPosition", adapterPosition);
                                            setResult(RESULT_OK, resultIntent);
                                        } else {
                                            Log.w(Utils.getTag(),"testResultsUpload:failure", task.getException());
                                            Snackbar.make(v, R.string.test_result_failure, Snackbar.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            }

                        })
                        .setNegativeButton(android.R.string.no, null)
                        .show();
            }
        }
    }
}
