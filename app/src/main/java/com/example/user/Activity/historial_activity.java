package com.example.user.Activity;

import static com.example.user.Activity.account_activity.LANGUAGE_PREF;
import static com.example.user.Activity.account_activity.SELECTED_LANGUAGE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.user.Adapter.PedidoAdapter;
import POJO.Pedido;

import com.example.user.ConexionBD.BD;
import com.example.user.R;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class historial_activity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PedidoAdapter pedidoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String languageCode = loadLanguagePreference();


        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_historial);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerView = findViewById(R.id.recyclerViewPedidos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Simulación de datos
        /*List<Pedido> pedidos = new ArrayList<>();
        pedidos.add(new Pedido("Bebida Usuario", "Chocolate", "Vegetal"));
        pedidos.add(new Pedido("Bebida Usuario", "Vainilla", "Animal"));
        pedidos.add(new Pedido("Bebida Usuario", "Fresa", "Vegetal"));*/

        BD bd = new BD(this);
        bd.getInfoPedidosGeneralUser(new BD.JsonArrayCallback() {
            @Override
            public void onSuccess(JsonArray array) {
                List<Pedido> pedidos = new ArrayList<>();

                for (JsonElement element : array) {
                    JsonObject obj = element.getAsJsonObject();
                    String id = obj.get("id_pedido").getAsString();
                    String proteina = obj.get("nombre_proteina").getAsString();
                    String tipo = obj.get("tipo_proteina").getAsString();
                    String sabor = obj.get("sabor").getAsString();


                    pedidos.add(new Pedido(proteina, sabor, tipo));
                }

                runOnUiThread(() -> {
                    pedidoAdapter = new PedidoAdapter(historial_activity.this, pedidos, languageCode);
                    recyclerView.setAdapter(pedidoAdapter);
                });
            }

            @Override
            public void onError(String mensaje) {
                runOnUiThread(() -> {
                    Toast.makeText(historial_activity.this, "Error al cargar:" + mensaje, Toast.LENGTH_SHORT).show();
                });
            }
        });

        //pedidoAdapter = new PedidoAdapter(this, pedidos, languageCode);
        //recyclerView.setAdapter(pedidoAdapter);

        findViewById(R.id.btnBack).setOnClickListener(v -> {
            finish();
            startActivity(new Intent(this, account_activity.class));
            Toast.makeText(this, "PROGRESO", Toast.LENGTH_SHORT).show();

        });
    }

    // Cargar el idioma guardado
    private String loadLanguagePreference() {
        SharedPreferences preferences = getSharedPreferences(LANGUAGE_PREF, MODE_PRIVATE);

        Toast.makeText(this, "Idioma actual: " + preferences.getString(SELECTED_LANGUAGE, "es"), Toast.LENGTH_SHORT).show(); // Verifica el idioma

        return preferences.getString(SELECTED_LANGUAGE, "es"); // Default is Spanish

    }
}