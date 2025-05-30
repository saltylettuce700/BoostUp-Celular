package com.example.user.Activity;


import static com.example.user.Activity.account_activity.LANGUAGE_PREF;
import static com.example.user.Activity.account_activity.SELECTED_LANGUAGE;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
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

import java.io.IOException;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

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

        ProgressBar progressBar = findViewById(R.id.progressBar);
        ImageButton btnNext = findViewById(R.id.btn_next);



        findViewById(R.id.btn_next).setOnClickListener(v -> {

            // Acción para Inventario
            //startActivity(new Intent(this, home_activity.class));
            //Toast.makeText(this, "Iniciar Sesion", Toast.LENGTH_SHORT).show();

            String correo = email.getText().toString();
            String pass = password.getText().toString();

            if (correo.isEmpty()) {
                Toast.makeText(this, "Email vacío favor de llenarlo", Toast.LENGTH_SHORT).show();
            }else if(pass.isEmpty()){
                Toast.makeText(this, "Contraseña vacía favor de llenarlo", Toast.LENGTH_SHORT).show();
            }else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
                Toast.makeText(this, "El formato del correo no es válido", Toast.LENGTH_SHORT).show();
            } else {
                // Oculta el botón y muestra la barra de progreso
                btnNext.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);

                bd.iniciaSesion(correo, pass, new BD.LoginCallback() {
                    @Override
                    public void onLoginSuccess() {
                        // Nada más que hacer, ya te lleva a home_activity
                    }

                    @Override
                    public void onLoginFailed() {
                        runOnUiThread(()->{
                            progressBar.setVisibility(View.GONE);
                            btnNext.setVisibility(View.VISIBLE);

                            bd.comprobarEmailExiste(correo, new BD.BooleanCallback() {
                                @Override
                                public void onSuccess(boolean existe) {
                                    runOnUiThread(()->{
                                        if (existe){
                                            Toast.makeText(login_activity.this, "Contraseña incorrecta", Toast.LENGTH_SHORT).show();
                                        }else{
                                            Toast.makeText(login_activity.this, "Usuario incorrecto", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }

                                @Override
                                public void onFailure() {
                                    runOnUiThread(() ->
                                            Toast.makeText(login_activity.this, "Algo falló", Toast.LENGTH_SHORT)
                                    );
                                }
                            });
                        });
                    }
                });
            }
        });

        findViewById(R.id.TV_registarte).setOnClickListener(v -> {
            // Acción para Inventario
            startActivity(new Intent(this, registro_mail_activity.class));
        });

        email = findViewById(R.id.ET_correo);
        password = findViewById(R.id.ET_password);

        TextView tv = findViewById(R.id.TV_lostPass);
        tv.setPaintFlags(tv.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        tv.setOnClickListener(v -> {
            // Aquí abres una actividad, fragmento, diálogo, etc.
        });

        // Botón "Restablecer"
        findViewById(R.id.TV_lostPass).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showResetPasswordDialog();
            }
        });

    }

    private void showResetPasswordDialog() {
        Dialog dialog = new Dialog(this, R.style.BlurBackgroundDialog);

        // Inflar diseño personalizado
        View dialogView = LayoutInflater.from(this).inflate(R.layout.custom_dialog_reset_password, null);
        dialog.setContentView(dialogView);


        Button btnRespass = dialogView.findViewById(R.id.btn_continue);
        Button btnCancel = dialogView.findViewById(R.id.btn_back);
        EditText mailEditText = dialogView.findViewById(R.id.mail);

        btnRespass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mailEditText.getText().toString().trim();

                if (email.isEmpty()) {
                    Toast.makeText(login_activity.this, "Por favor ingresa tu correo", Toast.LENGTH_SHORT).show();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(login_activity.this, "Correo inválido", Toast.LENGTH_SHORT).show();
                } else {
                    bd.comprobarEmailExiste(email, new BD.BooleanCallback() {
                        @Override
                        public void onSuccess(boolean existe) {
                            runOnUiThread(()->{
                                if (existe){
                                    runOnUiThread(()->{
                                        bd.olvideMiContraseña(email, new Callback() {
                                            @Override
                                            public void onFailure(Call call, IOException e) {
                                                runOnUiThread(() ->
                                                        Toast.makeText(login_activity.this, "Algo falló", Toast.LENGTH_SHORT).show()
                                                );
                                            }

                                            @Override
                                            public void onResponse(Call call, Response response) throws IOException {
                                                //Nada ya lo hace solito
                                            }
                                        });
                                    });
                                }else{
                                    Toast.makeText(login_activity.this, "Correo no registrado", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onFailure() {
                            runOnUiThread(() ->
                                    Toast.makeText(login_activity.this, "Algo falló", Toast.LENGTH_SHORT).show()
                            );
                        }
                    });
                }

                dialog.dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
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