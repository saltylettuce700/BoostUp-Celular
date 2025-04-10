package com.example.user.Activity;


import static com.example.user.Activity.account_activity.LANGUAGE_PREF;
import static com.example.user.Activity.account_activity.SELECTED_LANGUAGE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.EditText;
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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Crea objeto de la BD
        BD bd = new BD();
        EditText email, password;


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
            startActivity(new Intent(this, home_activity.class));
            Toast.makeText(this, "Iniciar Sesion", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.TV_registarte).setOnClickListener(v -> {
            // Acción para Inventario
            startActivity(new Intent(this, registro_mail_activity.class));
            Toast.makeText(this, "Registrate", Toast.LENGTH_SHORT).show();
        });

        email = findViewById(R.id.ET_correo);
        password = findViewById(R.id.ET_password);



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