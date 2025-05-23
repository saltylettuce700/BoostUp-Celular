package com.example.user.Activity;

import static androidx.core.content.ContextCompat.startActivity;
import static com.example.user.Activity.account_activity.LANGUAGE_PREF;
import static com.example.user.Activity.account_activity.SELECTED_LANGUAGE;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
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
import com.example.user.ConexionBD.Preferences;
import com.example.user.R;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class datos_perfil_activity extends AppCompatActivity {

    private LinearLayout accountDetails;
    private ImageView arrowIcon;

    EditText ETnombre, ETapellido, ETusername;
    TextView txtcorreo, TV_reestablecer;
    Button btGuardarCambios;

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

        TV_reestablecer = findViewById(R.id.TV_reestablecer);

        btGuardarCambios = findViewById(R.id.btn_guardarCambios);

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

                    btGuardarCambios.setOnClickListener(view -> {
                        BD bd = new BD(datos_perfil_activity.this);

                        String name = ETnombre.getText().toString();
                        String ape = ETapellido.getText().toString();
                        String user = ETusername.getText().toString();

                        if (name.isEmpty()) {
                            Toast.makeText(datos_perfil_activity.this, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (ape.isEmpty()) {
                            Toast.makeText(datos_perfil_activity.this, "El apellido no puede estar vacío", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (user.isEmpty()) {
                            Toast.makeText(datos_perfil_activity.this, "El nombre de usuario no puede estar vacío", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        bd.ActualizarDatosUser(name, ape, user, new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                runOnUiThread(()->{
                                    //Toast.makeText(datos_perfil_activity.this, "Error al actualizar: " + e, Toast.LENGTH_SHORT).show();
                                });
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                runOnUiThread(() -> {
                                    Toast.makeText(datos_perfil_activity.this,"Datos actualizados", Toast.LENGTH_LONG).show();
                                });
                            }
                        });
                    });
                }
                isExpanded = !isExpanded;
            }
        });

        findViewById(R.id.btnBack).setOnClickListener(v -> {
            finish();
            startActivity(new Intent(this, account_activity.class));


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
        View dialogView = LayoutInflater.from(this).inflate(R.layout.custom_dialog_restablecer, null);
        dialog.setContentView(dialogView);

        // Configurar botones
        Button btnRespass = dialogView.findViewById(R.id.btn_respass);
        Button btnCancel = dialogView.findViewById(R.id.btn_cancel);

        EditText pass1 = dialogView.findViewById(R.id.ET_pass1);
        EditText pass2 = dialogView.findViewById(R.id.ET_pass2);

        btnRespass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nuevaPass = pass1.getText().toString();
                String confirmarPass = pass2.getText().toString();

                // Verifica que sean iguales
                if (!nuevaPass.equals(confirmarPass)) {
                    Toast.makeText(datos_perfil_activity.this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                    pass2.setText("");
                    return;
                }

                if (nuevaPass.length() < 8) {
                    Toast.makeText(datos_perfil_activity.this, "La contraseña debe tener al menos 8 caracteres", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!nuevaPass.matches(".*[A-Z].*")) {
                    Toast.makeText(datos_perfil_activity.this, "La contraseña debe contener al menos una letra mayúscula", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!nuevaPass.matches(".*\\d.*")) {
                    Toast.makeText(datos_perfil_activity.this, "La contraseña debe contener al menos un número", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Si pasa todas las validaciones, actualiza la contraseña
                BD bd = new BD(datos_perfil_activity.this);
                bd.ActualizarPass(nuevaPass, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        runOnUiThread(() -> {
                            //Toast.makeText(datos_perfil_activity.this, "Fallo: " + e, Toast.LENGTH_SHORT).show();
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        runOnUiThread(() -> {
                            if (response.isSuccessful()) {
                                Preferences preferences = new Preferences(datos_perfil_activity.this);
                                preferences.guardarPassword(nuevaPass);
                                Toast.makeText(datos_perfil_activity.this, "Contraseña actualizada correctamente", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(datos_perfil_activity.this, "Algo salió mal", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                dialog.dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                BD bd = new BD(datos_perfil_activity.this);

                bd.eliminarCuenta(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        runOnUiThread(() ->
                                Toast.makeText(datos_perfil_activity.this, "Error al conectar con el servidor", Toast.LENGTH_LONG).show()
                        );
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()) {
                            runOnUiThread(() -> {
                                // Eliminar token y redirigir
                                Preferences preferences = new Preferences(datos_perfil_activity.this);
                                preferences.borrarCredenciales();

                                Toast.makeText(datos_perfil_activity.this, "Cuenta eliminada correctamente", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(datos_perfil_activity.this, login_activity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            });
                        } else {
                            String errorBody = response.body() != null ? response.body().string() : "sin cuerpo";
                            Log.e("ELIMINAR_CUENTA", "Código: " + response.code() + " - Cuerpo: " + errorBody);
                            runOnUiThread(() ->
                                    Toast.makeText(datos_perfil_activity.this, "No se pudo eliminar la cuenta. Intenta de nuevo.", Toast.LENGTH_LONG).show()
                            );
                        }
                    }
                });


                //Toast.makeText(datos_perfil_activity.this, "Borraste cuenta", Toast.LENGTH_SHORT).show();

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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