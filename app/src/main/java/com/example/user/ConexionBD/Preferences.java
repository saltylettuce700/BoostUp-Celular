package com.example.user.ConexionBD;

import android.content.Context;
import android.content.SharedPreferences;

public class Preferences {
    private static final String NOMBRE_PREF = "AuthPrefs";
    private SharedPreferences prefs;

    public Preferences(Context context) {
        prefs = context.getSharedPreferences(NOMBRE_PREF, Context.MODE_PRIVATE);
    }

    public void guardarCredenciales(String token, String user, String password) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("access_token", token);
        editor.putString("user", user);
        editor.putString("password", password);
        editor.apply();
    }

    public void guardarToken(String token) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("access_token", token);
        editor.apply();
    }

    public void guardarUser(String user) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("user", user);
        editor.apply();
    }

    public void guardarPassword(String password) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("password", password);
        editor.apply();
    }

    public String obtenerToken() {
        return prefs.getString("access_token", null);
    }

    public String obtenerPassword() {return prefs.getString("password", null);}

    public String obtenerUser() {return prefs.getString("user", null);}

    public void borrarCredenciales() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove("access_token");
        editor.remove("user");
        editor.remove("password");
        editor.apply();
    }


}

