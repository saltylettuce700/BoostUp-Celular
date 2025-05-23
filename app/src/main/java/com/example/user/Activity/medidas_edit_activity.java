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

import java.io.IOException;
import java.util.Calendar;

import POJO.Persona;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

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

        findViewById(R.id.btnBack).setOnClickListener(v -> {
            finish();
            startActivity(new Intent(this, datos_perfil_activity.class));

        });

        ETpeso = findViewById(R.id.ET_peso);
        ETtalla = findViewById(R.id.ET_talla);
        ETcintura = findViewById(R.id.ET_cintura);
        ETcadera = findViewById(R.id.ET_cadera);
        ETcbrazo = findViewById(R.id.ET_cbrazo);

        findViewById(R.id.btn_guardarmedidas).setOnClickListener(v -> {
//            // Acción para Cerrar Sesión
//            finish();
//            //startActivity(new Intent(this, datos_perfil_activity.class));
//            Toast.makeText(this, "ABRIR METODOS DE PAGO", Toast.LENGTH_SHORT).show();

            BD bd = new BD(medidas_edit_activity.this);

            String pesoSt = ETpeso.getText().toString();
            String tallaSt = ETtalla.getText().toString();
            String cinturaSt = ETcintura.getText().toString();
            String caderaSt = ETcadera.getText().toString();
            String brazoSt = ETcbrazo.getText().toString();

            if (pesoSt.isEmpty()) {
                Toast.makeText(this, "El campo peso no puede estar vacío", Toast.LENGTH_SHORT).show();
                return;
            }
            if (tallaSt.isEmpty()) {
                Toast.makeText(this, "El campo talla no puede estar vacío", Toast.LENGTH_SHORT).show();
                return;
            }
            if (cinturaSt.isEmpty()) {
                Toast.makeText(this, "El campo cintura no puede estar vacío", Toast.LENGTH_SHORT).show();
                return;
            }
            if (caderaSt.isEmpty()) {
                Toast.makeText(this, "El campo cadera no puede estar vacío", Toast.LENGTH_SHORT).show();
                return;
            }
            if (brazoSt.isEmpty()) {
                Toast.makeText(this, "El campo brazo no puede estar vacío", Toast.LENGTH_SHORT).show();
                return;
            }

            int peso, talla, cintura, cadera, brazo;

            try {
                peso = Integer.parseInt(pesoSt);
                talla = Integer.parseInt(tallaSt);
                cintura = Integer.parseInt(cinturaSt);
                cadera = Integer.parseInt(caderaSt);
                brazo = Integer.parseInt(brazoSt);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Todos los campos deben ser números válidos", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validar rangos
            if (peso < 30 || peso > 200) {
                Toast.makeText(medidas_edit_activity.this, "El peso debe estar entre 30 y 200 kg", Toast.LENGTH_SHORT).show();
                return;
            }
            if (talla < 130 || talla > 230) {
                Toast.makeText(medidas_edit_activity.this, "La talla debe estar entre 130 y 230 cm", Toast.LENGTH_SHORT).show();
                return;
            }
            if (cintura < 40 || cintura > 150) {
                Toast.makeText(medidas_edit_activity.this, "La cintura debe estar entre 40 y 150 cm", Toast.LENGTH_SHORT).show();
                return;
            }
            if (cadera < 40 || cadera > 170) {
                Toast.makeText(medidas_edit_activity.this, "La cadera debe estar entre 40 y 170 cm", Toast.LENGTH_SHORT).show();
                return;
            }
            if (brazo < 15 || brazo > 50) {
                Toast.makeText(medidas_edit_activity.this, "La circunferencia de brazo debe estar entre 15 y 50 cm", Toast.LENGTH_SHORT).show();
                return;
            }

            bd.ActualizarMedidasUser(peso, talla, cintura, cadera, brazo, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(()->{
                        Toast.makeText(medidas_edit_activity.this, "Error al actualizar: " + e, Toast.LENGTH_SHORT).show();
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    runOnUiThread(() -> {
                        Toast.makeText(medidas_edit_activity.this,"Datos actualizados", Toast.LENGTH_LONG).show();
                    });
                }
            });
        });

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