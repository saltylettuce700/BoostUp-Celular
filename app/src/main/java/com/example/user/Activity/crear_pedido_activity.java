package com.example.user.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.user.R;

public class crear_pedido_activity extends AppCompatActivity {

    private ListView listProteinas, listSaborizantes, listCurcuma;
    private String selectedProteina, selectedSaborizante, selectedCurcuma;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_crear_pedido);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        listProteinas = findViewById(R.id.listProteinas);
        listSaborizantes = findViewById(R.id.listSaborizantes);
        listCurcuma = findViewById(R.id.listCurcuma);
        Button btn_pagar = findViewById(R.id.btn_pagar);

        // Datos de prueba
        String[] proteinas = {"Proteína Genérica Vegetal", "Proteína Genérica Animal", "Proteína Premium"};
        String[] saborizantes = {"Vainilla", "Chocolate", "Fresa"};
        String[] curcuma = {"Con cúrcuma de la chida", "Con cúrcuma de la chafa"};

        // Configurar adaptadores
        configureListView(listProteinas, proteinas, selected -> selectedProteina = selected);
        configureListView(listSaborizantes, saborizantes, selected -> selectedSaborizante = selected);
        configureListView(listCurcuma, curcuma, selected -> selectedCurcuma = selected);


        btn_pagar.setOnClickListener(v -> {
            if (selectedProteina != null && selectedSaborizante != null && selectedCurcuma != null) {
                Toast.makeText(this, "Pedido: \n" +
                        "Proteína: " + selectedProteina + "\n" +
                        "Saborizante: " + selectedSaborizante + "\n" +
                        "Cúrcuma: " + selectedCurcuma, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Selecciona todas las opciones", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void configureListView(ListView listView, String[] items, OnItemSelectedListener listener) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_single_choice, items);
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setOnItemClickListener((parent, view, position, id) -> listener.onItemSelected(items[position]));
    }

    // Interfaz para manejar selección
    interface OnItemSelectedListener {
        void onItemSelected(String selected);
    }
}