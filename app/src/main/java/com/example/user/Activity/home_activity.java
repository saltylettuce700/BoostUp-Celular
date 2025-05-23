package com.example.user.Activity;

import static com.example.user.Activity.account_activity.LANGUAGE_PREF;
import static com.example.user.Activity.account_activity.SELECTED_LANGUAGE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.ImageView;
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
import androidx.viewpager2.widget.ViewPager2;

import com.example.user.Activity.Registro.registro_terminos_activity;
import com.example.user.Adapter.BebidaAdapter;
import com.example.user.Adapter.CarouselAdapter;
import com.example.user.Adapter.OfertaAdapter;
import com.example.user.ConexionBD.BD;
import com.example.user.PaddingItemDecoration;
import com.example.user.R;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import POJO.BebidaDestacada;
import POJO.Oferta;


public class home_activity extends AppCompatActivity {

    private ViewPager2 viewPagerCarousel;
    private CarouselAdapter carouselAdapter;

    private LinearLayout carouselIndicators;
    private List<Integer> imageList;

    private RecyclerView recyclerViewBebidas, recyclerViewOfertas;
    private BebidaAdapter bebidaAdapter;

    private OfertaAdapter ofertaAdapter;

    private Translator translator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Cargar el idioma guardado y aplicarlo
        String languageCode = loadLanguagePreference();
        changeLanguage(languageCode); // Aplicar idioma guardado


        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        viewPagerCarousel = findViewById(R.id.viewPagerCarousel);
        carouselIndicators = findViewById(R.id.carouselIndicators);

        recyclerViewBebidas = findViewById(R.id.recyclerViewBebidas);
        recyclerViewOfertas = findViewById(R.id.recyclerViewOferta);

        // Configura el traductor
        setupTranslator();

        // Lista de imágenes de prueba
        List<Integer> imageList = new ArrayList<>();
        imageList.add(R.drawable.anuncio);
        imageList.add(R.drawable.anuncio2);
        imageList.add(R.drawable.anuncio3);

        // Configurar el adaptador
        carouselAdapter = new CarouselAdapter(this, imageList);
        viewPagerCarousel.setAdapter(carouselAdapter);

        // Configurar el scroll infinito
        viewPagerCarousel.setOffscreenPageLimit(3);

        // Configurar indicadores
        setupIndicators(imageList.size());
        setCurrentIndicator(0);

        // Listener para cambiar indicadores al deslizar
        viewPagerCarousel.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setCurrentIndicator(position);
            }
        });

        // Configurar el RecyclerView
        recyclerViewBebidas.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        /*bebidaAdapter = new BebidaAdapter(this, getBebidasDePrueba());
        recyclerViewBebidas.setAdapter(bebidaAdapter);*/
        cargarTopBebidas();

        recyclerViewOfertas.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        getOfertasDePruebaConTraduccion(ofertas -> {
            ofertaAdapter = new OfertaAdapter(this, ofertas);
            recyclerViewOfertas.setAdapter(ofertaAdapter);
        });


        // Agregar ItemDecoration para el padding
        int paddingStart = (int) getResources().getDimension(R.dimen.padding_start); // 20dp
        int paddingEnd = (int) getResources().getDimension(R.dimen.padding_end);     // 20dp
        recyclerViewBebidas.addItemDecoration(new PaddingItemDecoration(paddingStart, paddingEnd));

        recyclerViewOfertas.addItemDecoration(new PaddingItemDecoration(paddingStart, paddingEnd));

        findViewById(R.id.btn_account).setOnClickListener(v -> {
            // Acción para siguiente nombre
            startActivity(new Intent(this, account_activity.class));


        });

        findViewById(R.id.btn_comprar).setOnClickListener(v -> {
            // Acción para siguiente nombre
            startActivity(new Intent(this, crear_pedido_activity.class));

        });

        findViewById(R.id.btn_catalogo).setOnClickListener(v -> {
            // Acción para siguiente nombre
            startActivity(new Intent(this, catalogo_producto_activity.class));

        });


    }

    private void setupIndicators(int count) {
        ImageView[] indicators = new ImageView[count];
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(8, 0, 8, 0);

        for (int i = 0; i < indicators.length; i++) {
            indicators[i] = new ImageView(this);
            indicators[i].setImageResource(R.drawable.indicator_inactive); // Puntos inactivos
            indicators[i].setLayoutParams(layoutParams);
            carouselIndicators.addView(indicators[i]);
        }
    }

    private void setCurrentIndicator(int position) {
        int childCount = carouselIndicators.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ImageView imageView = (ImageView) carouselIndicators.getChildAt(i);
            if (i == position) {
                imageView.setImageResource(R.drawable.indicator_active); // Punto activo
            } else {
                imageView.setImageResource(R.drawable.indicator_inactive); // Puntos inactivos
            }
        }
    }

    /*private List<BebidaDestacada> getBebidasDePrueba() {
        List<BebidaDestacada> bebidas = new ArrayList<>();
        bebidas.add(new BebidaDestacada("Bebida 1", "Chocolate", R.drawable.bebida_img));
        bebidas.add(new BebidaDestacada("Bebida 2", "Vainilla", R.drawable.bebida_img));
        bebidas.add(new BebidaDestacada("Bebida 3", "Fresa", R.drawable.bebida_img));
        bebidas.add(new BebidaDestacada("Bebida 4", "Café", R.drawable.bebida_img));
        return bebidas;
    }*/

    private void cargarTopBebidas() {
        BD bd = new BD(this);
        List<BebidaDestacada> bebidas = new ArrayList<>();

        bd.getTopProteina(new BD.JsonArrayCallback() {
            @Override
            public void onSuccess(JsonArray proteinasArray) {
                // Añadir proteínas directamente (sin traducción)
                for (JsonElement element : proteinasArray) {
                    String nombre = element.getAsJsonObject().get("nombre").getAsString();
                    int imagen = obtenerImagenParaBebida(nombre);
                    bebidas.add(new BebidaDestacada(nombre, "", imagen));
                }

                int totalProteinas = proteinasArray.size();

                bd.getTopSabores(new BD.JsonArrayCallback() {
                    @Override
                    public void onSuccess(JsonArray saboresArray) {
                        int totalSabores = saboresArray.size();

                        if (totalSabores == 0) {
                            runOnUiThread(() -> {
                                bebidaAdapter = new BebidaAdapter(home_activity.this, bebidas);
                                recyclerViewBebidas.setAdapter(bebidaAdapter);
                            });
                            return;
                        }

                        // Traducir cada sabor
                        for (JsonElement element : saboresArray) {
                            String saborOriginal = element.getAsJsonObject().get("sabor").getAsString();
                            int imagen = obtenerImagenParaBebida(saborOriginal);

                            translator.translate(saborOriginal)
                                    .addOnSuccessListener(saborTraducido -> {
                                        bebidas.add(new BebidaDestacada(saborTraducido, "", imagen));

                                        // Solo actualizar cuando todas las bebidas estén listas
                                        if (bebidas.size() == totalProteinas + totalSabores) {
                                            runOnUiThread(() -> {
                                                bebidaAdapter = new BebidaAdapter(home_activity.this, bebidas);
                                                recyclerViewBebidas.setAdapter(bebidaAdapter);
                                            });
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        // Si falla, usar el texto original
                                        bebidas.add(new BebidaDestacada(saborOriginal, "", imagen));

                                        if (bebidas.size() == totalProteinas + totalSabores) {
                                            runOnUiThread(() -> {
                                                bebidaAdapter = new BebidaAdapter(home_activity.this, bebidas);
                                                recyclerViewBebidas.setAdapter(bebidaAdapter);
                                            });
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onError(String mensaje) {
                        runOnUiThread(() ->
                                Toast.makeText(home_activity.this, "Error sabores: " + mensaje, Toast.LENGTH_SHORT));
                    }
                });
            }

            @Override
            public void onError(String mensaje) {
                runOnUiThread(() ->
                        Toast.makeText(home_activity.this, "Error proteínas: " + mensaje, Toast.LENGTH_SHORT));
            }
        });
    }



    private void getOfertasDePruebaConTraduccion(OfertaCallback callback) {
        List<Oferta> ofertas = new ArrayList<>();

        String idioma = loadLanguagePreference(); // "es" o "en"

        String[] textos = new String[]{
                "Conóce nuestras máquinas en tu gym SmartFit más cercano",
                "🎉 ¡Tu primera bebida es gratis!",
                "💳 10% OFF pagando con tarjeta X o Stripe"
        };

        int[] imagenes = new int[]{
                R.drawable.promocionsmart,
                R.drawable.bebida_img,
                R.drawable.bebida_img
        };

        // Si el idioma es español, no traducimos
        if (idioma.equals("es")) {
            for (int i = 0; i < textos.length; i++) {
                ofertas.add(new Oferta(imagenes[i], textos[i]));
            }
            callback.onComplete(ofertas);
        } else {
            // Traducimos al inglés
            List<String> textosTraducidos = new ArrayList<>();

            for (int i = 0; i < textos.length; i++) {
                int finalI = i;
                translator.translate(textos[i])
                        .addOnSuccessListener(traducido -> {
                            ofertas.add(new Oferta(imagenes[finalI], traducido));
                            if (ofertas.size() == textos.length) {
                                callback.onComplete(ofertas);
                            }
                        })
                        .addOnFailureListener(e -> {
                            ofertas.add(new Oferta(imagenes[finalI], textos[finalI])); // usar original si falla
                            if (ofertas.size() == textos.length) {
                                callback.onComplete(ofertas);
                            }
                        });
            }
        }
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

    private int obtenerImagenParaBebida(String nombre) {
        switch (nombre.trim().toLowerCase()) {
            case "falcon protein":
                return R.drawable.falcon;
            case "pure and natural":
                return R.drawable.pure_natural_img;
            case "chocolate":
                return R.drawable.chocolate_icon;
            case "vainilla":
                return R.drawable.vanilla_icon;
            case "fresa":
                return R.drawable.strawberry_icon;
            default:
                return R.drawable.bebida_img;
        }
    }



    // Configurar el traductor ML Kit
    private void setupTranslator() {
        TranslatorOptions options = new TranslatorOptions.Builder()
                .setSourceLanguage(TranslateLanguage.SPANISH)
                .setTargetLanguage(TranslateLanguage.ENGLISH)
                .build();

        translator = Translation.getClient(options);

        translator.downloadModelIfNeeded()

                .addOnFailureListener(e -> Toast.makeText(this, "Error al descargar el modelo: " + e.getMessage(), Toast.LENGTH_SHORT));
    }

    interface OfertaCallback {
        void onComplete(List<Oferta> ofertas);
    }






}