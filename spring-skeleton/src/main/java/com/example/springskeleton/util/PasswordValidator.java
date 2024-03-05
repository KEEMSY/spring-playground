package com.example.springskeleton.util;

import java.util.regex.Pattern;

/*
- ^(?=.*[0-9]): 문자열에 최소 한 개의 숫자가 포함되어야 함
- (?=.*[a-zA-Z]): 문자열에 최소 한 개의 알파벳(대문자 또는 소문자)이 포함되어야 함
- (?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]): 문자열에 최소 한 개의 특수 문자가 포함되어야 함
- .{8,15}$: 문자열의 길이가 최소 8자 이상, 최대 15자 이하여야 함
 */
public class PasswordValidator {
    private static final String PASSWORD_PATTERN =
            "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{8,15}$";

    private static final Pattern pattern = Pattern.compile(PASSWORD_PATTERN);

    public static boolean isValid(String password) {
        if (password == null) {
            return false;
        }
        return pattern.matcher(password).matches();
    }
}
