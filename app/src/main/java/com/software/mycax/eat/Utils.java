package com.software.mycax.eat;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    /**
     * method is used for checking valid email id format.
     *
     * @param email * email input *
     * @return boolean true for valid false for invalid
     */
    public static boolean isEmailValid(final String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    /**
     * method is used for checking valid password format.
     *
     * @param password * password input *
     * @return boolean true for valid false for invalid
     */
    public static boolean isValidPassword(final String password) {
        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$";
        Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }
}
