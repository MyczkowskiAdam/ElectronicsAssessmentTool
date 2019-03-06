package com.software.mycax.eat;

import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    public static final int ACCOUNT_STUDENT = 0;
    public static final int ACCOUNT_TEACHER = 1;
    public static final int GALLERY_INTENT = 1;
    public static final int ATTEMPT_TEST_INTENT = 3;
    public static final int EDIT_TEST_INTENT = 4;
    public static final int PERMISSION_READ_EXTERNAL_STORAGE = 2;
    public static final String PREF_PROFILE_UPDATE = "pref_profile_update";
    public static final String DASHBOARD_TAG = "dashboard_fragment";
    public static final String ANALYTICS_TAG = "analytics_fragment";
    public static final String MANAGE_STUDENTS_TAG = "manage_students_fragment";
    public static final String SETTINGS_TAG = "settings_fragment";
    public static final String TEST_CREATOR_TAG = "test_creator_fragment";
    public static final String CHILD_REF_USERS = "users";
    public static final String CHILD_REF_TEACHER_CODE = "teacherCode";
    public static final String CHILD_REF_ACCOUNT_KEY = "accountKey";
    public static final String CHILD_REF_ACCOUNT_TYPE = "accountType";
    public static final String CHILD_REF_TESTS = "tests";
    public static final String CHILD_REF_TEST_RESULTS = "testResults";
    public static final String CHILD_REF_COMPLETED = "completed";
    public static final String EXTRA_INT_ADAPTER_POSITION = "adapterPoition";
    public static final String EXTRA_STRING_TESTUID = "testUid";
    public static final String EXTRA_BOOLEAN_IS_COMPLETED = "isCompleted";
    public static final String EXTRA_BOOLEAN_IS_EDITED = "isEdited";

    /**
     * method is used for checking valid email id format.
     *
     * @param email * email input *
     * @return boolean true for valid false for invalid
     */
    public static boolean isEmailValid(final String email) {
        String expression = "^[\\w.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    /**
     * method is used for checking valid password format.
     *
     * @param password * password input *
     * @return boolean true for valid false for invalid
     *
     * ^                 # start-of-string
     * (?=.*[0-9])       # a digit must occur at least once
     * (?=.*[a-z])       # a lower case letter must occur at least once
     * (?=.*[A-Z])       # an upper case letter must occur at least once
     * (?=.*[@#$%^&+=])  # a special character must occur at least once
     * (?=\S+$)          # no whitespace allowed in the entire string
     * .{8,}             # anything, at least eight places though
     * $                 # end-of-string
     *
     * Password rules are bad and shall not be enforced because it's limiting
     * the number of potential passwords and removing password permutations
     * that don't match your rules.
     */
    @SuppressWarnings("unused")
    private static boolean isPasswordValid(final String password) {
        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";
        Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

    /**
     * method is used getting log tag with line number.
     *
     * @return String tag in (filename.java:XX) format
     */
    public static String getTag() {
        String tag = "";
        final StackTraceElement[] ste = Thread.currentThread().getStackTrace();
        for (int i = 0; i < ste.length; i++) {
            if (ste[i].getMethodName().equals("getTag")) {
                tag = "("+ste[i + 1].getFileName() + ":" + ste[i + 1].getLineNumber()+")";
            }
        }
        return tag;
    }

    public static boolean isTestInputValid(View view, int questionCount, List<EditText> inputAnswerList, @Nullable List<EditText> inputQuestionList, @Nullable EditText eTestTtle) {
        if (eTestTtle != null && TextUtils.isEmpty(eTestTtle.getText())) {
            Snackbar.make(view, R.string.invalid_empty, Snackbar.LENGTH_LONG).show();
            return false;
        }
        for (int i = 0; i < questionCount; i++) {
            if (inputQuestionList != null && TextUtils.isEmpty(inputQuestionList.get(i).getText())) {
                Snackbar.make(view, R.string.invalid_empty, Snackbar.LENGTH_LONG).show();
                return false;
            }
            if (TextUtils.isEmpty(inputAnswerList.get(i).getText())) {
                Snackbar.make(view, R.string.invalid_empty, Snackbar.LENGTH_LONG).show();
                return false;
            }
        }
        return true;
    }
}
