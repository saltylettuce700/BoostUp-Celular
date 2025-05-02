package com.example.user.Activity.Registro;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.user.Activity.account_activity;
import com.example.user.Activity.historial_activity;
import com.example.user.R;

public class ver_detalles_activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ver_detalles);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //cuando sea un producto de saborizante, la seccion de precauciones desaparece

        findViewById(R.id.imageButton).setOnClickListener(v -> {
            finish();
            startActivity(new Intent(this, historial_activity.class));
            Toast.makeText(this, "PROGRESO", Toast.LENGTH_SHORT).show();

        });
    }
}