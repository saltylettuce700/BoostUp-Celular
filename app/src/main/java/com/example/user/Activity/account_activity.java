package com.example.user.Activity;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.user.R;

public class account_activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_account);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        findViewById(R.id.idioma_section).setOnClickListener(v -> {
            // Acción para idioma
            //startActivity(new Intent(this, InventarioActivity.class));
            Toast.makeText(this, "IDIOMA", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.progreso_section).setOnClickListener(v -> {
            // Acción para progreso
            //startActivity(new Intent(this, ReportarFalloActivity.class));
            Toast.makeText(this, "PROGRESO", Toast.LENGTH_SHORT).show();

        });

        findViewById(R.id.terminos_section).setOnClickListener(v -> {

            //startActivity(new Intent(this, HistorialFallosActivity.class));
            Toast.makeText(this, "TERMINOS Y COND", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.ayuda_cliente_section).setOnClickListener(v -> {

            Toast.makeText(this, "AYUDA AL CLIENTE", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.cerrar_sesion_section).setOnClickListener(v -> {
            // Acción para Cerrar Sesión
            Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show();
        });

        //Botones de arriba

        findViewById(R.id.perfil_section).setOnClickListener(v -> {

            Toast.makeText(this, "IR A PERFIL", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.historial_section).setOnClickListener(v -> {
            // Acción para Cerrar Sesión
            Toast.makeText(this, "HISTORIAL", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.pago_section).setOnClickListener(v -> {
            // Acción para Cerrar Sesión
            Toast.makeText(this, "ABRIR METODOS DE PAGO", Toast.LENGTH_SHORT).show();
        });
    }
}