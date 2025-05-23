package com.example.user.Activity;

import static com.example.user.Activity.account_activity.LANGUAGE_PREF;
import static com.example.user.Activity.account_activity.SELECTED_LANGUAGE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.SearchView;
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

import com.example.user.ConexionBD.BD;
import com.example.user.R;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class catalogo_producto_activity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CatalogAdapter adapter;
    private List<CatalogItem> catalogItems;


    private List<CatalogItem> todasLasProteinas  = new ArrayList<>();

    // Declarar las listas
    private List<Map<String, String>> saborizantes = new ArrayList<>();
    private List<CatalogItem> todasLasCurcumas = new ArrayList<>();
    private List<CatalogItem> todosLosSaborizantes  = new ArrayList<>();


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


        catalogItems = new ArrayList<>();

        // Llamar a la configuración de la barra de búsqueda
        setupSearchBar();




        // Configura el adaptador
        adapter = new CatalogAdapter(this, catalogItems, languageCode);
        recyclerView.setAdapter(adapter);
        //obtenerProductos();

        TextView btnProteinas = findViewById(R.id.btn_proteinas);
        TextView btnSaborizantes = findViewById(R.id.btn_saborizante);
        TextView btnCurcumas = findViewById(R.id.btn_curcuma);
        // Opcionales: estos se pueden usar si implementas filtros por origen
        TextView btnAnimal = findViewById(R.id.btn_animal);
        TextView btnVegetal = findViewById(R.id.btn_vegetal);




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

        btnProteinas.setTag("Proteínas");
        btnSaborizantes.setTag("Saborizantes");
        btnCurcumas.setTag("Cúrcuma y Jengibre");
        btnAnimal.setTag("animal");
        btnVegetal.setTag("vegetal");

        // Variable para almacenar el botón seleccionado (si lo hay)
        TextView[] selectedButton = {null};

        TextView[] allButtons = {btnProteinas, btnSaborizantes, btnCurcumas, btnAnimal, btnVegetal};

        for (TextView button : allButtons) {
            button.setOnClickListener(v -> {
                if (button.isSelected()) {
                    button.setSelected(false);
                    button.setBackgroundResource(R.drawable.et_holder_gray_rectangle);
                    button.setTextColor(getResources().getColor(R.color.black));
                    selectedButton[0] = null;
                    obtenerProductos(); // Muestra todos
                } else {
                    if (selectedButton[0] != null) {
                        selectedButton[0].setSelected(false);
                        selectedButton[0].setBackgroundResource(R.drawable.et_holder_gray_rectangle);
                        selectedButton[0].setTextColor(getResources().getColor(R.color.black));
                    }

                    button.setSelected(true);
                    button.setBackgroundResource(R.drawable.et_holder_selected_rectangle);
                    button.setTextColor(getResources().getColor(R.color.white));
                    selectedButton[0] = button;


                    // Usa el tag en lugar del texto
                    String categoriaTag = (String) button.getTag();
                    filtrarProductosPorCategoria(categoriaTag);
                }
            });
        }

        // Mostrar todos los productos al inicio
        obtenerProductos();



        findViewById(R.id.btnBack).setOnClickListener(v -> {
            finish();
            startActivity(new Intent(this, home_activity.class));

        });


    }

    



    private void obtenerProteinas() {
        BD bd = new BD(catalogo_producto_activity.this);

        bd.getOpcionesProteinas(new BD.JsonArrayCallback() {
            @Override
            public void onSuccess(JsonArray array) {
                runOnUiThread(() -> {
                    for (JsonElement elemento : array) {
                        JsonObject obj = elemento.getAsJsonObject();
                        int id = obj.get("id_proteina").getAsInt();
                        String nombre = obj.get("nombre").getAsString();
                        String tipo = obj.get("tipo_proteina").getAsString();

                        bd.getDetallesProteina(id, new BD.JsonCallback() {
                            @Override
                            public void onSuccess(JsonObject obj) {
                                runOnUiThread(() -> {
                                    String marca = obj.get("marca").getAsString();
                                    //catalogItems.add(new CatalogItem(CatalogItem.TYPE_PRODUCT, nombre, id, marca, tipo));
                                    //adapter.notifyDataSetChanged();

                                    int imageResId = obtenerImagenPorProducto("Proteínas", nombre);
                                    CatalogItem item = new CatalogItem(CatalogItem.TYPE_PRODUCT, nombre, id, marca, tipo, "proteina" ,imageResId);

                                    todasLasProteinas.add(item); // Guardar en la lista completa



                                    

                                    agregarProductoDebajoDeCategoria(item, "Proteínas");
                                });
                            }

                            @Override
                            public void onError(String mensaje) {
                                runOnUiThread(() -> {
                                    Toast.makeText(catalogo_producto_activity.this, mensaje, Toast.LENGTH_SHORT).show();
                                });
                            }
                        });
                    }
                });
            }

            @Override
            public void onError(String mensaje) {
                runOnUiThread(() -> {
                    Toast.makeText(catalogo_producto_activity.this, mensaje, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void obtenerCatalogoSaborizantes() {
        BD bd = new BD(catalogo_producto_activity.this);
        new Thread(() -> {
            List<Map<String, String>> saborizantes = bd.getSaborizantes();
            if (saborizantes != null) {
                for (Map<String, String> saborizante : saborizantes) {
                    String idStr = saborizante.get("id");


                    //String sabor = saborizante.get("sabor");

                    String saborEs = saborizante.get("sabor");
                    String saborEn = traducirSabor(saborEs); // crea esta función de traducción simple

                    String tipo = saborizante.get("tipo");

                    if (idStr != null && !idStr.isEmpty()) {
                        try {
                            int id = Integer.parseInt(idStr);

                            bd.getDetallesSaborizante(id, new BD.JsonCallback() {
                                @Override
                                public void onSuccess(JsonObject obj) {
                                    runOnUiThread(() -> {
                                        String marca = obj.has("marca") && !obj.get("marca").isJsonNull()
                                                ? obj.get("marca").getAsString()
                                                : "Desconocida";


                                        int imageResId = obtenerImagenPorProducto("Saborizantes", saborEn);
                                        CatalogItem item = new CatalogItem(CatalogItem.TYPE_PRODUCT, marca, id, saborEn, tipo, "saborizante", imageResId);
                                        todosLosSaborizantes.add(item);


                                        


//                                        catalogItems.add(item);
//                                        adapter.notifyDataSetChanged();
                                        agregarProductoDebajoDeCategoria(item, "Saborizantes");
                                    });
                                }

                                @Override
                                public void onError(String mensaje) {
                                    runOnUiThread(() ->
                                            Toast.makeText(catalogo_producto_activity.this, mensaje, Toast.LENGTH_SHORT).show()
                                    );
                                }
                            });
                        } catch (NumberFormatException e) {
                            Log.e("catalogo_producto", "ID inválido: " + idStr, e);
                        }
                    }
                }
            } else {
                runOnUiThread(() ->
                        Toast.makeText(catalogo_producto_activity.this, "No se pudieron obtener los saborizantes.", Toast.LENGTH_SHORT).show()
                );
            }
        }).start();
    }

    private void obeterCurcumas(){
        BD bd = new BD(catalogo_producto_activity.this);
        bd.getDatosCurcuma(new BD.JsonArrayCallback() {
            @Override
            public void onSuccess(JsonArray array) {
                runOnUiThread(() -> {
                    for (JsonElement element : array) {
                        JsonObject obj = element.getAsJsonObject();
                        int id = obj.get("id_curcuma").getAsInt();
                        String marca = obj.get("marca").getAsString();
//                        catalogItems.add(new CatalogItem(CatalogItem.TYPE_PRODUCT, marca, id, null, null));
//                        adapter.notifyDataSetChanged();


                        int imageResId = obtenerImagenPorProducto("Cúrcuma y Jengibre", marca);
                        CatalogItem item = new CatalogItem(CatalogItem.TYPE_PRODUCT, marca, id, null, null, "curcuma", imageResId);

                        todasLasCurcumas.add(item);


                        

                        agregarProductoDebajoDeCategoria(item, "Cúrcuma y Jengibre");
                    }
                });
            }

            @Override
            public void onError(String mensaje) {
                runOnUiThread(() -> {
                    Toast.makeText(catalogo_producto_activity.this, mensaje, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void obtenerProductos() {
        catalogItems.clear(); // Vacía la lista antes de llenarla
        todasLasProteinas.clear();
        todosLosSaborizantes.clear();
        todasLasCurcumas.clear();

        // Proteínas

        catalogItems.add(new CatalogItem(CatalogItem.TYPE_CATEGORY, "Proteínas", 0, null, null, null, 0));
        // Saborizantes
        catalogItems.add(new CatalogItem(CatalogItem.TYPE_CATEGORY, "Saborizantes", 0, null, null, null, 0));
        // Cúrcuma y Jengibre
        catalogItems.add(new CatalogItem(CatalogItem.TYPE_CATEGORY, "Cúrcuma y Jengibre", 0, null, null, null, 0));

      
        adapter.notifyDataSetChanged();
        obtenerProteinas();
        obtenerCatalogoSaborizantes();
        obeterCurcumas();
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

        //Toast.makeText(this, "Idioma actual: " + preferences.getString(SELECTED_LANGUAGE, "es"), Toast.LENGTH_SHORT).show(); // Verifica el idioma

        return preferences.getString(SELECTED_LANGUAGE, "es"); // Default is Spanish

    }

    private void agregarProductoDebajoDeCategoria(CatalogItem producto, String nombreCategoria) {
        int index = -1;
        // Buscar la categoría
        for (int i = 0; i < catalogItems.size(); i++) {
            CatalogItem item = catalogItems.get(i);
            if (item.getType() == CatalogItem.TYPE_CATEGORY && item.getTitle().equals(nombreCategoria)) {
                index = i;
            }
        }

        if (index != -1) {
            // Encontrar el primer producto que no sea de esta categoría
            int insertPos = index + 1;
            while (insertPos < catalogItems.size() && catalogItems.get(insertPos).getType() == CatalogItem.TYPE_PRODUCT) {
                insertPos++;
            }
            catalogItems.add(insertPos, producto);
            adapter.notifyItemInserted(insertPos);
        }
    }


    private int obtenerImagenPorProducto(String categoria, String valorClave) {
        if (valorClave == null) return 0;

        valorClave = valorClave.toLowerCase();

        switch (categoria) {
            case "Proteínas":
                if (valorClave.contains("pure and natural")) return R.drawable.pure_natural_img;
                if (valorClave.contains("falcon")) return R.drawable.falcon;
                return 0;

            case "Saborizantes":
                if (valorClave.contains("fresa")||valorClave.contains("strawberry")) return R.drawable.strawberry_milk;
                if (valorClave.contains("chocolate")) return R.drawable.choco_milk;
                if (valorClave.contains("vainilla")|| valorClave.contains("vanilla")) return R.drawable.vanilla_milk;


                return 0;

            case "Cúrcuma y Jengibre":
                if (valorClave.contains("nature heart")) return R.drawable.nature_heart_turmeric;
                return 0;
        }

        return 0;
    }

    private void filtrarProductosPorCategoria(String categoria) {
        catalogItems.clear();

        catalogItems.add(new CatalogItem(CatalogItem.TYPE_CATEGORY, categoria, 0, null, null, null ,0));
        adapter.notifyDataSetChanged();

        switch (categoria) {
            case "Proteínas":
                obtenerProteinas();
                break;
            case "Saborizantes":
                obtenerCatalogoSaborizantes();
                break;
            case "Cúrcuma y Jengibre":
                obeterCurcumas();
                break;

            case "animal":
            case "vegetal":
                filtrarProteinasPorTipo(categoria); // Tipo de proteína
                return;
        }
    }


    private void filtrarProteinasPorTipo(String tipoFiltrado) {
        catalogItems.clear();

        catalogItems.add(new CatalogItem(CatalogItem.TYPE_CATEGORY, "Proteínas", 0, null, null, null, 0));

        for (CatalogItem item : todasLasProteinas) {
            if (item.getTipoProteinaSaborizante() != null && item.getTipoProteinaSaborizante().equalsIgnoreCase(tipoFiltrado)) {
                catalogItems.add(item);
            }
        }

        adapter.notifyDataSetChanged();
    }

    private void setupSearchBar() {
        EditText searchBar = findViewById(R.id.search_bar);

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                String query = charSequence.toString().toLowerCase();
                filtrarProductos(query);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
    }

    private void filtrarProductos(String query) {
        List<CatalogItem> filteredItems = new ArrayList<>();

        // Filtrar Proteínas
        filteredItems.addAll(filtrarProteinas(query));

        // Filtrar Saborizantes
        filteredItems.addAll(filtrarSaborizantes(query));

        // Filtrar Curcuma
        filteredItems.addAll(filtrarCurcuma(query));

        // Mostrar los productos filtrados en el RecyclerView
        catalogItems.clear();
        catalogItems.addAll(filteredItems);
        adapter.notifyDataSetChanged();
    }

    private List<CatalogItem> filtrarProteinas(String query) {
        List<CatalogItem> proteinasFiltradas = new ArrayList<>();
        catalogItems.clear();
        proteinasFiltradas.clear();
        for (CatalogItem item : todasLasProteinas) {
            if (item.getTitle().toLowerCase().contains(query) ||
                    (item.getTipoProteinaSaborizante() != null && item.getTipoProteinaSaborizante().toLowerCase().contains(query))) {
                proteinasFiltradas.add(item);
            }
        }
        return proteinasFiltradas;
    }

    private List<CatalogItem> filtrarSaborizantes(String query) {
        List<CatalogItem> saborizantesFiltrados = new ArrayList<>();
        String texto = query.toLowerCase();
        catalogItems.clear();


        for (CatalogItem item : todosLosSaborizantes) {
            if (item.getTitle().toLowerCase().contains(texto) || // marca
                    (item.getDescription() != null && item.getDescription().toLowerCase().contains(texto))) { // sabor
                saborizantesFiltrados.add(item);
            }
        }

        adapter.notifyDataSetChanged();

        return saborizantesFiltrados;
    }





    private List<CatalogItem> filtrarCurcuma(String query) {
        List<CatalogItem> curcumaFiltrada = new ArrayList<>();
        catalogItems.clear();
        for (CatalogItem item : todasLasCurcumas) {
            if (item.getTitle().toLowerCase().contains(query)) {
                curcumaFiltrada.add(item);
            }
        }
        return curcumaFiltrada;
    }


    private String traducirSabor(String saborEs) {
        // Esto debe adaptarse según los idiomas soportados
        if (loadLanguagePreference().equals("en")) {
            switch (saborEs.toLowerCase()) {
                case "vainilla": return "Vanilla";
                case "chocolate": return "Chocolate";
                case "fresa": return "Strawberry";
                default: return saborEs; // Si no se encuentra traducción
            }
        }
        return saborEs;
    }










}