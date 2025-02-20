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
import com.example.user.R;

import java.util.Calendar;

import POJO.Persona;

public class medidas_edit_activity extends AppCompatActivity {

    EditText ET_bday;
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
    }
}