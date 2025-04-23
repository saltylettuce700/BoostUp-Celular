package com.example.user.Activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.user.Activity.Registro.registro_terminos_activity;
import com.example.user.ConexionBD.Preferences;
import com.example.user.R;

import java.util.Locale;

public class account_activity extends AppCompatActivity {

    public static final String LANGUAGE_PREF = "language_pref";
    public static final String SELECTED_LANGUAGE = "selected_language";


    TextView TV_idioma;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //String languageCode = loadLanguagePreference();
        //changeLanguage(languageCode); // Aplicar idioma guardado

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_account);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView TV_idioma = findViewById(R.id.TV_idioma);



        findViewById(R.id.idioma_section).setOnClickListener(new View.OnClickListener() {
            // Acción para idioma (crear un dialog con 2 radios entre ingles y espanol)

            @Override
            public void onClick(View v) {
                showLanguageDialog();
            }

        });

        findViewById(R.id.progreso_section).setOnClickListener(v -> {
            // Acción para progreso
            //startActivity(new Intent(this, ReportarFalloActivity.class));
            Toast.makeText(this, "PROGRESO", Toast.LENGTH_SHORT).show();

        });

        findViewById(R.id.terminos_section).setOnClickListener(v -> {
            //terminos y condiciones solo leer activity
            startActivity(new Intent(this, registro_terminos_activity.class));
            Toast.makeText(this, "TERMINOS Y COND", Toast.LENGTH_SHORT).show();
        });

        // Botón "Restablecer"
        findViewById(R.id.ayuda_cliente_section).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCallDialog();
            }
        });

        findViewById(R.id.cerrar_sesion_section).setOnClickListener(v -> {

            // Acción para Cerrar Sesión
            Preferences pref = new Preferences(this);
            pref.borrarCredenciales();
            //startActivity(new Intent(this, login_activity.class));
            Intent intent = new Intent(this, login_activity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show();
        });

        //Botones de arriba

        findViewById(R.id.perfil_section).setOnClickListener(v -> {
            startActivity(new Intent(this, datos_perfil_activity.class));
            Toast.makeText(this, "IR A PERFIL", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.historial_section).setOnClickListener(v -> {
            // Acción para Cerrar Sesión
            startActivity(new Intent(this, historial_activity.class));
            Toast.makeText(this, "HISTORIAL", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.pago_section).setOnClickListener(v -> {
            // Acción para Cerrar Sesión
            //startActivity(new Intent(this, registro_terminos_activity.class));
            Toast.makeText(this, "ABRIR METODOS DE PAGO", Toast.LENGTH_SHORT).show();
        });
    }

    private void showCallDialog() {

        Dialog dialog = new Dialog(this, R.style.BlurBackgroundDialog);

        // Inflar el diseño personalizado
        View dialogView = LayoutInflater.from(this).inflate(R.layout.custom_dialog_call, null);
        dialog.setContentView(dialogView);

        // Configurar botones
        Button btn_call = dialogView.findViewById(R.id.btn_call);
        Button btnCancel = dialogView.findViewById(R.id.btn_cancel);
        TextView telefono = dialogView.findViewById(R.id.TV_numero);

        btn_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Acción de continuar
                dialog.dismiss();
                Toast.makeText(account_activity.this, "llamando", Toast.LENGTH_SHORT).show();

                Intent llamar = new Intent((Intent.ACTION_CALL));

                llamar.setData(Uri.parse("tel:" + telefono.getText().toString()));
                if (ActivityCompat.checkSelfPermission(account_activity.this, Manifest.permission.CALL_PHONE)
                        != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(account_activity.this, new String[] {Manifest.permission.CALL_PHONE},10);
                    return;
                }
                startActivity(llamar);

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(account_activity.this, "Cancelar llamar", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        // Mostrar el diálogo
        dialog.show();


    }

    private void showLanguageDialog() {

        Dialog dialog = new Dialog(this, R.style.BlurBackgroundDialog);

        // Inflar el diseño personalizado
        View dialogView = LayoutInflater.from(this).inflate(R.layout.custom_dialog_language, null);
        dialog.setContentView(dialogView);

        // Configurar botones
        Button btnCambiar = dialogView.findViewById(R.id.btn_guardarCambios);
        Button btnCancel = dialogView.findViewById(R.id.btn_cancel);
        RadioGroup radioGroup = dialogView.findViewById(R.id.radioGroupIdioma);

        btnCambiar.setOnClickListener(v -> {
            int selectedId = radioGroup.getCheckedRadioButtonId();

            if (selectedId == R.id.rb_espanol) {

                changeLanguage("es");
                saveLanguagePreference("es");

                Toast.makeText(this, "Idioma cambiado a Español", Toast.LENGTH_SHORT).show();

            } else if (selectedId == R.id.rb_ingles) {

                changeLanguage("en");
                saveLanguagePreference("en");

                Toast.makeText(this, "Language changed to English", Toast.LENGTH_SHORT).show();

            }
            dialog.dismiss();
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(account_activity.this, "Cancelar", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });


        dialog.show();

    }

    public void changeLanguage(String languageCode) {
        Locale locale = new Locale(languageCode); // Idioma elegido
        Locale.setDefault(locale);

        // Configuración para cambiar la configuración regional
        Configuration config = getResources().getConfiguration();
        config.setLocale(locale);  // Establecer el idioma en la configuración

        // Actualizar los recursos
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());

        // Reiniciar la actividad para que los cambios surtan efecto
        //recreate();  // Vuelve a recrear la actividad con los nuevos recursos

        // Reiniciar la aplicación para aplicar el cambio
        //restartApplication();


    }

    // Cargar el idioma guardado
    public String loadLanguagePreference() {
        SharedPreferences preferences = getSharedPreferences(LANGUAGE_PREF, MODE_PRIVATE);
        return preferences.getString(SELECTED_LANGUAGE, "es"); // Default is Spanish
    }

    // Guardar el idioma seleccionado
    private void saveLanguagePreference(String languageCode) {
        SharedPreferences preferences = getSharedPreferences(LANGUAGE_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(SELECTED_LANGUAGE, languageCode);
        editor.apply();
    }


    public void restartApplication() {
        Intent intent = getPackageManager().getLaunchIntentForPackage(getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Limpiar el back stack
        startActivity(intent);
        finish(); // Finalizar la actividad actual
        System.exit(0); // Cerrar la aplicación completamente
    }

}