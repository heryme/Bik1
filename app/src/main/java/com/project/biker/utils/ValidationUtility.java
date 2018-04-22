package com.project.biker.utils;

import android.content.Context;
import android.widget.EditText;

import com.project.biker.R;


public class ValidationUtility {

    private static final String TAG = ValidationUtility.class.getSimpleName();

    public static boolean isValidLength(Context context, EditText editText) {
        String pass = editText.getText().toString();
        if (pass.length() < 6) {
            editText.requestFocus();
            editText.setFocusable(true);
            editText.setError(context.getResources().getString(R.string.error_msg_password_must_be_six_digit));
            return false;
        } else {
            return true;
        }
    }

    public static boolean isValidEditText(EditText editText, String errMsg) {
        boolean isValid = true;
        if (editText.getText().toString().trim().length() == 0) {
            editText.requestFocus();
            editText.setFocusable(true);
            editText.setCursorVisible(true);
            editText.setError(errMsg);
            isValid = false;
        }
        return isValid;
    }

    public static boolean isBothPasswordEqual(Context context, EditText edtText1, EditText edtText2) {
        boolean isValid = true;
        String str1 = edtText1.getText().toString().trim();
        String str2 = edtText2.getText().toString().trim();
        if (!str1.equals(str2)) {
            isValid = false;
            edtText2.requestFocus();
            edtText2.setFocusable(true);
            edtText2.setError(context.getString(R.string.err_msg_re_enter_password_dosnt_match));

        }

        return isValid;
    }

    public static boolean matchTwoString(String str1, String str2) {
        return str1.equals(str2);
    }


    public final static boolean isValidEmail(Context context, EditText etEmail) {

        String emailPattern ="^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                +"((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                +"[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                +"([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                +"[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                +"([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$"/* "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"*/;
        String email = etEmail.getText().toString().trim();
        if (email.matches(emailPattern) && email.length() > 0) {
            return true;
        } else {
            etEmail.requestFocus();
            etEmail.setFocusable(true);
            etEmail.setError(context.getResources().getString(R.string.hint_enter_valid_email));
            return false;
        }
    }


    /**
     * Check, if string contains a space or not for password feild
     *
     * @param context
     * @param editText
     * @return
     */
    public static boolean isStringHaveSpace(Context context, EditText editText) {
        if (editText.getText().toString().matches(".*\\s+.*")) {
            editText.requestFocus();
            editText.setFocusable(true);
            editText.setError(context.getResources().getString(R.string.error_msg_space_not_allowed));
            return false;
        } else {
            return true;
        }
    }


}
