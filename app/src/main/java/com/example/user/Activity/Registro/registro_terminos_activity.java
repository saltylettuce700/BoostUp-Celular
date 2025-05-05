package com.example.user.Activity.Registro;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.user.Activity.account_activity;
import com.example.user.Activity.home_activity;
import com.example.user.ConexionBD.BD;
import com.example.user.R;

import java.util.ArrayList;

public class registro_terminos_activity extends AppCompatActivity {

    private CheckBox checkboxTerms;
    private Button buttonFinish;
    private ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registro_terminos);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent1 = getIntent();
        String setemail = intent1.getStringExtra("email");
        String setpass =intent1.getStringExtra("pass");
        String setNombre = intent1.getStringExtra("nombre");
        String setApellido = intent1.getStringExtra("apellido");
        String setBirthday = intent1.getStringExtra("birthday");
        String setusername = intent1.getStringExtra("username");
        String setsexo = intent1.getStringExtra("sexo");
        int setpeso = intent1.getIntExtra("peso",0);
        int settalla = intent1.getIntExtra("talla",0);
        int setcintura = intent1.getIntExtra("cintura", 0);
        int setcadera = intent1.getIntExtra("cadera", 0);
        int setbrazo = intent1.getIntExtra("brazo", 0);
        ArrayList<Integer> setalergiasSeleccionadas = intent1.getIntegerArrayListExtra("alergiasSeleccionadas");

        checkboxTerms = findViewById(R.id.checkbox_terms);
        buttonFinish = findViewById(R.id.button_finish);
        btnBack = findViewById(R.id.btnBack);



        boolean desdeAccount = getIntent().getBooleanExtra("desde_account", false);

        if (desdeAccount) {
            checkboxTerms.setVisibility(View.GONE);
            buttonFinish.setVisibility(View.GONE);
            btnBack.setVisibility(View.VISIBLE);

            btnBack.setOnClickListener(v -> {
                finish();
                startActivity(new Intent(this, account_activity.class));
                Toast.makeText(this, "PROGRESO", Toast.LENGTH_SHORT).show();
            });
        } else {
            btnBack.setVisibility(View.GONE);
        }




        // Habilitar/deshabilitar el botón dependiendo del estado del checkbox
        checkboxTerms.setOnCheckedChangeListener((buttonView, isChecked) -> {
            buttonFinish.setEnabled(isChecked);
        });

        // Acción cuando se presiona el botón
        buttonFinish.setOnClickListener(v -> {
            new Thread(() -> {
                BD bd = new BD(registro_terminos_activity.this);

                boolean exito = bd.registrarUsuario(setemail, setpass, setusername, setNombre, setApellido, setsexo, setBirthday, setpeso, settalla, setcintura, setcadera, setbrazo);

                if (exito) {
                    bd.iniciaSesion(setemail, setpass);

                    if (setalergiasSeleccionadas != null) {
                        for (int idAlergia : setalergiasSeleccionadas) {
                            bd.registrarAlergiaUsuario(idAlergia);
                        }
                    }

                    // Volver al hilo principal para la UI
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(this, home_activity.class);
                        startActivity(intent);
                        finish();
                    });
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Error al registrar", Toast.LENGTH_SHORT).show();
                    });
                }
            }).start(); // <- importante: inicia el hilo
        });
    }
}