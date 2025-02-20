package com.example.user.Activity.Registro;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.user.R;

public class registro_alergia_activity extends AppCompatActivity {

    ListView LV_alergenos;
    ArrayAdapter<String> adapter;
    String [] array_alergenos = {"alergeno1", "alergeno2", "alergeno3", "alergeno4", "alergeno5"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registro_alergia);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        LV_alergenos = findViewById(R.id.LV_alergenos);

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice,array_alergenos);
        LV_alergenos.setAdapter(adapter);

        findViewById(R.id.btn_next).setOnClickListener(v -> {
            // Acción para siguiente nombre
            //startActivity(new Intent(this, registro_name_activity.class));
            Toast.makeText(this, "Siguiente nombre", Toast.LENGTH_SHORT).show();


        });

        // Configuración del ListView con soporte para selección múltiple
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, array_alergenos);
        LV_alergenos.setAdapter(adapter);
        LV_alergenos.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE); // Habilitar selección múltiple

        // Configuración del botón para mostrar las opciones seleccionadas
        findViewById(R.id.btn_next).setOnClickListener(v -> {
            StringBuilder selectedItems = new StringBuilder("Selected Items: ");
            for (int i = 0; i < LV_alergenos.getCount(); i++) {
                if (LV_alergenos.isItemChecked(i)) {
                    selectedItems.append(LV_alergenos.getItemAtPosition(i)).append(" ");
                }
            }

            // Mostrar las opciones seleccionadas en un Toast
            Toast.makeText(this, selectedItems.toString(), Toast.LENGTH_SHORT).show();
        });


    }


}