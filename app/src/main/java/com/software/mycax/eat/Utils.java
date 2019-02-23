package com.software.mycax.eat;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    public static final int ACCOUNT_STUDENT = 0;
    public static final int ACCOUNT_TEACHER = 1;
    public static final int GALLERY_INTENT = 1;
    public static final int PERMISSION_READ_EXTERNAL_STORAGE = 2;
    public static final String PREF_PROFILE_UPDATE = "pref_profile_update";

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
}
