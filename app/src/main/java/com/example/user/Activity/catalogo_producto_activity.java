package com.example.user.Activity;

import static com.example.user.Activity.account_activity.LANGUAGE_PREF;
import static com.example.user.Activity.account_activity.SELECTED_LANGUAGE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.user.Adapter.CatalogAdapter;
import POJO.CatalogItem;
import com.example.user.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class catalogo_producto_activity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CatalogAdapter adapter;
    private List<CatalogItem> catalogItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Cargar el idioma guardado y aplicarlo
        String languageCode = loadLanguagePreference();
        //changeLanguage(languageCode); // Aplicar idioma guardado



        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_catalogo_producto);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        HorizontalScrollView horizontalScrollView = findViewById(R.id.scroll_opciones);
        LinearLayout linearLayoutFilter = findViewById(R.id.linearLayoutFilter);

        recyclerView = findViewById(R.id.recyclerViewCatalog);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // SIMUALCION
        catalogItems = obtenerProductosSimulados();

        // Configura el adaptador
        adapter = new CatalogAdapter(this, catalogItems, languageCode);
        recyclerView.setAdapter(adapter);

        horizontalScrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                int childWidth = linearLayoutFilter.getMeasuredWidth();
                int scrollViewWidth = horizontalScrollView.getMeasuredWidth();

                if (scrollX == 0) {
                    // Si está en el inicio, aplicar padding izquierdo menuda mamamda
                    horizontalScrollView.setPadding(64, 0, 0, 0);
                } else if (scrollX + scrollViewWidth >= childWidth) {
                    // Si está al final, aplicar padding derecho
                    horizontalScrollView.setPadding(0, 0, 54, 0);
                } else {
                    // Mientras se desplaza, quitar padding adicional
                    horizontalScrollView.setPadding(0, 0, 0, 0);
                }
            }
        });

        // Variable para almacenar el botón seleccionado (si lo hay)
        final TextView[] selectedButton = {null};

        for (int i = 0; i < linearLayoutFilter.getChildCount(); i++) {
            View child = linearLayoutFilter.getChildAt(i);
            if (child instanceof TextView) {
                TextView button = (TextView) child;


                button.setOnClickListener(v -> {
                    if (button.isSelected()) {
                        // Situación 1: El botón ya está activo, lo desactiva
                        button.setSelected(false);
                        button.setBackgroundResource(R.drawable.et_holder_gray_rectangle); // Fondo desactivado
                        button.setTextColor(getResources().getColor(R.color.black));
                        selectedButton[0] = null; // Ningún botón está seleccionado
                    } else {
                        // Situación 2: Activar el botón clickeaado y desactivar el anterior
                        if (selectedButton[0] != null) {
                            // Desactiva el botón anteriormente seleccionado
                            selectedButton[0].setSelected(false);
                            selectedButton[0].setBackgroundResource(R.drawable.et_holder_gray_rectangle);
                            selectedButton[0].setTextColor(getResources().getColor(R.color.black));
                        }

                        // Activa el botón actual
                        button.setSelected(true);
                        button.setBackgroundResource(R.drawable.et_holder_selected_rectangle); // Fondo activado
                        selectedButton[0] = button; // Actualiza el botón seleccionado
                    }
                });
            }
        }

        findViewById(R.id.btnBack).setOnClickListener(v -> {
            finish();
            startActivity(new Intent(this, home_activity.class));
            Toast.makeText(this, "PROGRESO", Toast.LENGTH_SHORT).show();

        });
    }

    private List<CatalogItem> obtenerProductosSimulados() {
        List<CatalogItem> items = new ArrayList<>();

        // Categoría: Proteínas
        items.add(new CatalogItem(CatalogItem.TYPE_CATEGORY, "Proteínas", null));
        items.add(new CatalogItem(CatalogItem.TYPE_PRODUCT, "Proteína genérica vegetal", "Descripción breve"));
        items.add(new CatalogItem(CatalogItem.TYPE_PRODUCT, "Proteína genérica animal", "Descripción breve"));

        // Categoría: Saborizantes
        items.add(new CatalogItem(CatalogItem.TYPE_CATEGORY, "Saborizantes", null));
        items.add(new CatalogItem(CatalogItem.TYPE_PRODUCT, "Saborizante genérico", "Descripción breve"));
        items.add(new CatalogItem(CatalogItem.TYPE_PRODUCT, "Saborizante especial", "Descripción breve"));

        // Categoría: Cúrcuma y Jengibre
        items.add(new CatalogItem(CatalogItem.TYPE_CATEGORY, "Cúrcuma y Jengibre", null));
        items.add(new CatalogItem(CatalogItem.TYPE_PRODUCT, "Cúrcuma genérica", "Descripción breve"));
        items.add(new CatalogItem(CatalogItem.TYPE_PRODUCT, "Jengibre genérico", "Descripción breve"));

        return items;
    }

    private void changeLanguage(String languageCode) {
        // Cambiar el idioma
        Locale locale = new Locale(languageCode); // Idioma elegido
        Locale.setDefault(locale);

        // Configuración para cambiar la configuración regional
        Configuration config = getResources().getConfiguration();
        config.setLocale(locale);  // Establecer el idioma en la configuración

        // Actualizar los recursos
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
    }


    // Cargar el idioma guardado
    private String loadLanguagePreference() {
        SharedPreferences preferences = getSharedPreferences(LANGUAGE_PREF, MODE_PRIVATE);

        Toast.makeText(this, "Idioma actual: " + preferences.getString(SELECTED_LANGUAGE, "es"), Toast.LENGTH_SHORT).show(); // Verifica el idioma

        return preferences.getString(SELECTED_LANGUAGE, "es"); // Default is Spanish

    }
}