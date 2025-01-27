package com.example.user.Activity;

import android.os.Bundle;

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

        pedidoAdapter = new PedidoAdapter(pedidos);
        recyclerView.setAdapter(pedidoAdapter);
    }
}