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
import com.example.user.R;

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
            finish();
            startActivity(new Intent(this, home_activity.class));
            Toast.makeText(this, "Registro Completado", Toast.LENGTH_SHORT).show();
        });
    }
}