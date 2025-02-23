package com.example.user.Activity.Registro;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.user.R;

public class registro_name_activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registro_name);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        findViewById(R.id.btn_next).setOnClickListener(v -> {
            // Acción para siguiente medidas
            startActivity(new Intent(this, registro_medidas_activity.class));
            Toast.makeText(this, "Siguiente MEDIDAS", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.btn_back).setOnClickListener(v -> {
            // Acción para atras mail
            startActivity(new Intent(this, registro_mail_activity.class));
            Toast.makeText(this, "Atras MEDIDAS", Toast.LENGTH_SHORT).show();
        });



    }
}