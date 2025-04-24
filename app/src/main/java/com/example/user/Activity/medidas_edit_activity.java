package com.example.user.Activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.user.Activity.Registro.registro_medidas_activity;
import com.example.user.ConexionBD.BD;
import com.example.user.R;
import com.google.gson.JsonObject;

import java.util.Calendar;

import POJO.Persona;

public class medidas_edit_activity extends AppCompatActivity {

    EditText ET_bday, ETpeso, ETtalla, ETcintura, ETcadera, ETcbrazo;
    POJO.Persona Persona= new Persona();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_medidas_edit);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        findViewById(R.id.btn_guardarmedidas).setOnClickListener(v -> {
            // Acción para Cerrar Sesión
            finish();
            //startActivity(new Intent(this, datos_perfil_activity.class));
            Toast.makeText(this, "ABRIR METODOS DE PAGO", Toast.LENGTH_SHORT).show();
        });

        ETpeso = findViewById(R.id.ET_peso);
        ETtalla = findViewById(R.id.ET_talla);
        ETcintura = findViewById(R.id.ET_cintura);
        ETcadera = findViewById(R.id.ET_cadera);
        ETcbrazo = findViewById(R.id.ET_cbrazo);


        mostrarMedidas();
    }

    private void mostrarMedidas() {
        BD bd = new BD(this);

        bd.getMedidasUser(new BD.JsonCallback() {
            @Override
            public void onSuccess(JsonObject obj) {
                runOnUiThread(() ->{
                    double peso = obj.get("peso_kg").getAsDouble();
                    int talla = obj.get("talla_cm").getAsInt();
                    int cintura = obj.get("cintura_cm").getAsInt();
                    int cadera = obj.get("cadera_cm").getAsInt();
                    int brazo = obj.get("circ_brazo_cm").getAsInt();

                    ETpeso.setText("" + peso);
                    ETtalla.setText(""+talla);
                    ETcintura.setText(""+cintura);
                    ETcadera.setText(""+cadera);
                    ETcbrazo.setText(""+brazo);
                });
            }

            @Override
            public void onError(String mensaje) {
                runOnUiThread(() -> {
                    Toast.makeText(medidas_edit_activity.this, mensaje, Toast.LENGTH_SHORT).show();
                });
            }
        });

    }
}