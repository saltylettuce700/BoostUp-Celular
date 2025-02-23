package com.example.user.Activity.Registro;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.user.Activity.home_activity;
import com.example.user.R;

public class registro_terminos_activity extends AppCompatActivity {

    private CheckBox checkboxTerms;
    private Button buttonFinish;

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

        checkboxTerms = findViewById(R.id.checkbox_terms);
        buttonFinish = findViewById(R.id.button_finish);


        // Habilitar/deshabilitar el botón dependiendo del estado del checkbox
        checkboxTerms.setOnCheckedChangeListener((buttonView, isChecked) -> {
            buttonFinish.setEnabled(isChecked);
        });

        // Acción cuando se presiona el botón
        buttonFinish.setOnClickListener(v -> {
            finish();
            startActivity(new Intent(this, home_activity.class));
            Toast.makeText(this, "Registro Completado", Toast.LENGTH_SHORT).show();
        });
    }
}