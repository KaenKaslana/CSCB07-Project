package com.example.b07demosummer2024;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesHelper {
    private static final String PREFS_NAME = "secure_prefs";
    private static final String KEY_PIN = "user_pin";

    private SharedPreferences sharedPreferences;

    public SharedPreferencesHelper(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void savePin(String pin) {
        sharedPreferences.edit().putString(KEY_PIN, pin).apply();
    }

    public String getPin() {
        return sharedPreferences.getString(KEY_PIN, null);
    }

    public boolean isPinSet() {
        return getPin() != null;
    }

    public boolean verifyPin(String inputPin) {
        String storedPin = getPin();
        return storedPin != null && storedPin.equals(inputPin);
    }

    public void clearPin() {
        sharedPreferences.edit().remove(KEY_PIN).apply();
    }
}