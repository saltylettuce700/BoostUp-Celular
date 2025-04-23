package com.example.user.Activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.user.ConexionBD.BD;
import com.example.user.ConexionBD.Preferences;
import com.example.user.R;
import com.google.gson.JsonObject;

public class MainActivity extends AppCompatActivity {
    Preferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        prefs = new Preferences(this);
        String token = prefs.obtenerToken();
        String email = prefs.obtenerUser();
        String pass = prefs.obtenerPassword();

        if (token != null && email != null && pass != null) {
            probarToken(token);
        } else {
            irAlLogin();
        }
    }

    private void probarToken(String token) {
        BD bd = new BD(this);
        bd.probarToken(new BD.JsonCallback() {
            @Override
            public void onSuccess(JsonObject obj) {
                irAlHome();
            }

            @Override
            public void onError(String mensaje) {
                intentarLogin();
            }
        });
    }

    private void intentarLogin() {
        String email = prefs.obtenerUser();
        String pass = prefs.obtenerPassword();

        BD bd = new BD(this);
        bd.getAuthToken(email, pass, new BD.AuthCallback() {
            @Override
            public void onSuccess(String nuevoToken) {
                prefs.guardarCredenciales(nuevoToken, email, pass);
                irAlHome();
            }

            @Override
            public void onFailure(String error) {
                irAlLogin();
            }
        });
    }

    private void irAlLogin() {
        Intent intent = new Intent(MainActivity.this, login_activity.class);
        startActivity(intent);
        finish();
    }

    private void irAlHome() {
        Intent intent = new Intent(MainActivity.this, home_activity.class);
        startActivity(intent);
        finish();
    }

}