package com.example.user.Activity;

import static com.example.user.Activity.account_activity.LANGUAGE_PREF;
import static com.example.user.Activity.account_activity.SELECTED_LANGUAGE;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.user.Activity.Registro.registro_terminos_activity;
import com.example.user.ConexionBD.BD;
import com.example.user.R;
import com.google.gson.JsonObject;

import java.util.Locale;

public class datos_perfil_activity extends AppCompatActivity {

    private LinearLayout accountDetails;
    private ImageView arrowIcon;

    EditText ETnombre, ETapellido, ETusername;
    TextView txtcorreo;

    private boolean isExpanded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Cargar el idioma guardado y aplicarlo
        String languageCode = loadLanguagePreference();
        changeLanguage(languageCode); // Aplicar idioma guardado

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_datos_perfil);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Referencias
        LinearLayout cuentaSection = findViewById(R.id.cuenta_section);
        accountDetails = findViewById(R.id.account_details);
        arrowIcon = findViewById(R.id.arrow_icon);

        //Espacios de la seccion cuenta
        ETnombre = findViewById(R.id.ET_nombre);
        ETapellido = findViewById(R.id.ET_apellido);
        ETusername = findViewById(R.id.ET_username);
        txtcorreo = findViewById(R.id.textView18);

        // Manejar el clic en la sección "Cuenta"
        cuentaSection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isExpanded) {
                    // Ocultar con animación
                    //Animation slideUp = AnimationUtils.loadAnimation(datos_perfil_activity.this, R.anim.slide_up);
                    //accountDetails.startAnimation(slideUp);
                    accountDetails.setVisibility(View.GONE);
                    arrowIcon.setRotation(0f); // Rotar flecha hacia abajo
                } else {
                    // Mostrar con animación
                    Animation slideDown = AnimationUtils.loadAnimation(datos_perfil_activity.this, R.anim.slide_down);
                    accountDetails.startAnimation(slideDown);
                    accountDetails.setVisibility(View.VISIBLE);
                    arrowIcon.setRotation(180f); // Rotar flecha hacia arriba

                    //llenar los acmpos con la info del usuario
                    cargarDatosUsuario();
                }
                isExpanded = !isExpanded;
            }
        });

        findViewById(R.id.btnBack).setOnClickListener(v -> {
            finish();
            startActivity(new Intent(this, account_activity.class));
            Toast.makeText(this, "PROGRESO", Toast.LENGTH_SHORT).show();

        });


        // Botón "Restablecer"
        findViewById(R.id.TV_reestablecer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showResetPasswordDialog();
            }
        });

        findViewById(R.id.medidas_section).setOnClickListener(v -> {
            // Acción para Cerrar Sesión
            startActivity(new Intent(this, medidas_edit_activity.class));
            Toast.makeText(this, "ABRIR METODOS DE PAGO", Toast.LENGTH_SHORT).show();
        });

        // Botón "Borrar cuenta"
        findViewById(R.id.TV_eliminar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteAccountDialog();
            }
        });
    }

    private void cargarDatosUsuario() {
        BD bd = new BD(this);

        bd.getInfoUser(new BD.JsonCallback() {
            @Override
            public void onSuccess(JsonObject obj) {
                runOnUiThread(() ->{
                    String nombre = obj.get("nombre").getAsString();
                    String apellido = obj.get("apellido").getAsString();
                    String username = obj.get("username").getAsString();
                    String email = obj.get("email").getAsString();

                    ETnombre.setText(nombre);
                    ETapellido.setText(apellido);
                    ETusername.setText(username);
                    txtcorreo.setText(email);
                });
            }

            @Override
            public void onError(String mensaje) {
                runOnUiThread(() -> {
                    Toast.makeText(datos_perfil_activity.this, mensaje, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void showResetPasswordDialog() {

        Dialog dialog = new Dialog(this, R.style.BlurBackgroundDialog);

        // Inflar el diseño personalizado
        View dialogView = LayoutInflater.from(this).inflate(R.layout.custom_dialog_reset_password, null);
        dialog.setContentView(dialogView);

        // Configurar botones
        Button btnContinue = dialogView.findViewById(R.id.btn_continue);
        Button btnCancel = dialogView.findViewById(R.id.btn_cancel);

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Acción de continuar
                dialog.dismiss();
                Toast.makeText(datos_perfil_activity.this, "Se enviará un correo para restablecer tu contraseña", Toast.LENGTH_SHORT).show();

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(datos_perfil_activity.this, "Cancelar reestablecer", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        // Mostrar el diálogo
        dialog.show();


    }


    private void showDeleteAccountDialog() {

        Dialog dialog = new Dialog(this, R.style.BlurBackgroundDialog);

        // Inflar el diseño personalizado
        View dialogView = LayoutInflater.from(this).inflate(R.layout.custom_dialog_delete_account, null);
        dialog.setContentView(dialogView);

        // Configurar botones
        Button btnContinue = dialogView.findViewById(R.id.btn_delete);
        Button btnCancel = dialogView.findViewById(R.id.btn_cancel);

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Acción de continuar
                dialog.dismiss();
                Toast.makeText(datos_perfil_activity.this, "Borraste cuenta", Toast.LENGTH_SHORT).show();

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(datos_perfil_activity.this, "Cancelar BorraR", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        // Mostrar el diálogo
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