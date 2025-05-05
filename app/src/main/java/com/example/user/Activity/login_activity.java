package com.example.user.Activity;


import static com.example.user.Activity.account_activity.LANGUAGE_PREF;
import static com.example.user.Activity.account_activity.SELECTED_LANGUAGE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Paint;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.user.Activity.Registro.registro_mail_activity;
import com.example.user.ConexionBD.BD;
import com.example.user.R;

import java.util.Locale;

public class login_activity extends AppCompatActivity {

    //Crea objeto de la BD
    BD bd = new BD(this);
    EditText email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Cargar el idioma guardado y aplicarlo
        String languageCode = loadLanguagePreference();
        changeLanguage(languageCode); // Aplicar idioma guardado

        //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        findViewById(R.id.btn_next).setOnClickListener(v -> {

            // Acción para Inventario
            //startActivity(new Intent(this, home_activity.class));
            //Toast.makeText(this, "Iniciar Sesion", Toast.LENGTH_SHORT).show();

            String correo = email.getText().toString();
            String pass = password.getText().toString();

            if (correo.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Campos vacíos, favor de llenarlos", Toast.LENGTH_SHORT).show();
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
                Toast.makeText(this, "El formato del correo no es válido", Toast.LENGTH_SHORT).show();
            } else {
                bd.iniciaSesion(correo, pass);
            }
        });

        findViewById(R.id.TV_registarte).setOnClickListener(v -> {
            // Acción para Inventario
            startActivity(new Intent(this, registro_mail_activity.class));
            Toast.makeText(this, "Registrate", Toast.LENGTH_SHORT).show();
        });

        email = findViewById(R.id.ET_correo);
        password = findViewById(R.id.ET_password);

        TextView tv = findViewById(R.id.TV_lostPass);
        tv.setPaintFlags(tv.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        tv.setOnClickListener(v -> {
            // Aquí abres una actividad, fragmento, diálogo, etc.
            Toast.makeText(this, "Ir a recuperación de contraseña", Toast.LENGTH_SHORT).show();
        });

    }

    private void changeLanguage(String languageCode) {
        // Cambiar el idioma
        Locale locale = new Locale(languageCode); // Idioma elegido
        Locale.setDefault(locale);

        // Configuración para cambiar la configuración regional
        Configuration config = getResources().getConfiguration();
        config.setLocale(locale);  // Establecer el idioma en la configuración

        // Actualizar los recursos
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
    }


    // Cargar el idioma guardado
    private String loadLanguagePreference() {
        SharedPreferences preferences = getSharedPreferences(LANGUAGE_PREF, MODE_PRIVATE);
        return preferences.getString(SELECTED_LANGUAGE, "es"); // Default is Spanish
    }
}