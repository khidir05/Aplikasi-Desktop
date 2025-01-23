package com.rewritetext.util;

public class ValidationUtils {
    public static boolean isValidPassword(String password) {
        return password != null && !password.trim().isEmpty();
    }
}