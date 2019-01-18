package com.yomko.romawallpapers.Utility;

import android.text.TextUtils;
import android.widget.EditText;

public class Validation {
    public static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean validateEmail(EditText inputEmail) {
        String email = inputEmail.getText().toString().trim();
        if (email.isEmpty() || !isValidEmail(email)) {
            return false;
        }
        return true;
    }

    public static boolean validatePassword(EditText inputPassword) {
        if (inputPassword.getText().toString().trim().isEmpty() || inputPassword.getText().length() < 8) {
            return false;
        }
        return true;
    }

    public static boolean validateConfirmPassword(String password1, String password2) {
        if (password1.equals(password2)) {
            return true;
        }
        return false;
    }

    public static boolean validatePhone(EditText inputPhone) {
        if (inputPhone.getText().toString().trim().length() < 11) {
            return false;
        }
        return true;
    }

    public static boolean validateName(EditText inputName) {
        String word = inputName.getText().toString();
        if (!inputName.getText().toString().trim().isEmpty()) {
            if (word.matches("[a-zA-Z][a-zA-Z ]*")) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
}
