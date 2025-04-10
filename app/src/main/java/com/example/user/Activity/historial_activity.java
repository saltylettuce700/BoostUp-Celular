package com.example.user.Activity;

import static com.example.user.Activity.account_activity.LANGUAGE_PREF;
import static com.example.user.Activity.account_activity.SELECTED_LANGUAGE;

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
import com.example.user.R;

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
        List<Pedido> pedidos = new ArrayList<>();
        pedidos.add(new Pedido("Bebida Usuario", "Chocolate", "Vegetal"));
        pedidos.add(new Pedido("Bebida Usuario", "Vainilla", "Animal"));
        pedidos.add(new Pedido("Bebida Usuario", "Fresa", "Vegetal"));

        pedidoAdapter = new PedidoAdapter(this, pedidos, languageCode);
        recyclerView.setAdapter(pedidoAdapter);
    }

    // Cargar el idioma guardado
    private String loadLanguagePreference() {
        SharedPreferences preferences = getSharedPreferences(LANGUAGE_PREF, MODE_PRIVATE);

        Toast.makeText(this, "Idioma actual: " + preferences.getString(SELECTED_LANGUAGE, "es"), Toast.LENGTH_SHORT).show(); // Verifica el idioma

        return preferences.getString(SELECTED_LANGUAGE, "es"); // Default is Spanish

    }
}