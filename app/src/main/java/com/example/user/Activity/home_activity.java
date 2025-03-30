package com.example.user.Activity;

import static com.example.user.Activity.account_activity.LANGUAGE_PREF;
import static com.example.user.Activity.account_activity.SELECTED_LANGUAGE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.example.user.PaddingItemDecoration;
import com.example.user.R;
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

        // Lista de imágenes de prueba
        List<Integer> imageList = new ArrayList<>();
        imageList.add(R.drawable.woman);
        imageList.add(R.drawable.logo);
        imageList.add(R.drawable.bebida_img);

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
        bebidaAdapter = new BebidaAdapter(this, getBebidasDePrueba());
        recyclerViewBebidas.setAdapter(bebidaAdapter);

        recyclerViewOfertas.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        ofertaAdapter = new OfertaAdapter(this, getOfertasDePrueba());
        recyclerViewOfertas.setAdapter(ofertaAdapter);

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
            Toast.makeText(this, "Siguiente", Toast.LENGTH_SHORT).show();

        });

        findViewById(R.id.btn_catalogo).setOnClickListener(v -> {
            // Acción para siguiente nombre
            startActivity(new Intent(this, catalogo_producto_activity.class));
            Toast.makeText(this, "Siguiente", Toast.LENGTH_SHORT).show();

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

    private List<BebidaDestacada> getBebidasDePrueba() {
        List<BebidaDestacada> bebidas = new ArrayList<>();
        bebidas.add(new BebidaDestacada("Bebida 1", "Chocolate", R.drawable.bebida_img));
        bebidas.add(new BebidaDestacada("Bebida 2", "Vainilla", R.drawable.bebida_img));
        bebidas.add(new BebidaDestacada("Bebida 3", "Fresa", R.drawable.bebida_img));
        bebidas.add(new BebidaDestacada("Bebida 4", "Café", R.drawable.bebida_img));
        return bebidas;
    }

    private List<Oferta> getOfertasDePrueba() {
        List<Oferta> ofertas = new ArrayList<>();
        ofertas.add(new Oferta(R.drawable.bebida_img));
        ofertas.add(new Oferta(R.drawable.bebida_img));
        ofertas.add(new Oferta(R.drawable.bebida_img));
        ofertas.add(new Oferta(R.drawable.bebida_img));
        return ofertas;
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