package com.example.user.ConexionBD;

import android.content.Context;
import android.content.SharedPreferences;

public class Preferences {
    private static final String NOMBRE_PREF = "AuthPrefs";
    private SharedPreferences prefs;

    public Preferences(Context context) {
        prefs = context.getSharedPreferences(NOMBRE_PREF, Context.MODE_PRIVATE);
    }

    public void guardarToken(String token) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("access_token", token);
        editor.apply();
    }

    public String obtenerToken() {
        return prefs.getString("access_token", null);
    }

    public void borrarToken() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove("access_token");
        editor.apply();
    }
}

