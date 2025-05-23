package com.example.user.Activity.Registro;

import static com.example.user.Activity.account_activity.LANGUAGE_PREF;
import static com.example.user.Activity.account_activity.SELECTED_LANGUAGE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.user.Activity.account_activity;
import com.example.user.Activity.catalogo_producto_activity;
import com.example.user.Activity.historial_activity;
import com.example.user.ConexionBD.BD;
import com.example.user.R;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

public class ver_detalles_activity extends AppCompatActivity {

    TextView nombretxt, fechatxt, marcatxt, tipotxt, cont_nutritxt, precauciontxt, textoPrecauciones, textoTipo, textoDescNutri;

    TextView tvNombreProteina, tvTipo;
    private Translator translator;

    ImageView imgProducto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Cargar el idioma guardado y aplicarlo
        String languageCode = loadLanguagePreference();

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ver_detalles);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();
        int id = intent.getIntExtra("id", -1);
        String tipoProducto = intent.getStringExtra("tipoProducto");
        nombretxt = findViewById(R.id.tvProductName);
        fechatxt = findViewById(R.id.tvOrderDate);
        marcatxt = findViewById(R.id.tvDescripcionProteina);
        tipotxt =findViewById(R.id.tvDescripcionSabroizante);
        cont_nutritxt = findViewById(R.id.tvDescripcion);
        precauciontxt = findViewById(R.id.tvListaAlergenos);
        textoPrecauciones = findViewById(R.id.tvPrecauciones);
        textoTipo = findViewById(R.id.tvTipo);
        textoDescNutri = findViewById(R.id.tvDescripcionNutricional);

        imgProducto = findViewById(R.id.imageBackground);

        tvNombreProteina = findViewById(R.id.tvNombreProteina);
        tvTipo = findViewById(R.id.tvTipo);




        // Configura el traductor
        setupTranslator();


        BD bd = new BD(ver_detalles_activity.this);

        if ("proteina".equals(tipoProducto)){
            bd.getDetallesProteina(id, new BD.JsonCallback() {
                @Override
                public void onSuccess(JsonObject obj) {
                    runOnUiThread(()-> {
                        String nombre = obj.get("nombre").getAsString();
                        String fecha = obj.get("fec_actualizacion").getAsString();
                        String marca = obj.get("marca").getAsString();
                        String tipo_proteina = obj.get("tipo_proteina").getAsString();
                        String cont_nutri = obj.get("cont_nutricional").getAsString();

                        bd.getAlergenosProteina(id, new BD.JsonArrayCallback() {
                            @Override
                            public void onSuccess(JsonArray array) {
                                runOnUiThread(()->{
                                    StringBuilder texto = new StringBuilder("Alérgenos:\n");
                                    for (JsonElement element : array){
                                        JsonObject obj2 = element.getAsJsonObject();
                                        String alergeno = obj2.get("alergeno").getAsString();
                                        texto.append("- ").append(alergeno).append("\n");
                                    }

                                    nombretxt.setText(nombre);
                                    fechatxt.setText("Ultima Fecha de Actualizacion:" + fecha);
                                    marcatxt.setText(marca);
                                    tipotxt.setText(tipo_proteina);
                                    cont_nutritxt.setText(cont_nutri);
                                    textoPrecauciones.setText("Alergenos:");
                                    precauciontxt.setText(texto);

                                    if (loadLanguagePreference().equals("en")) {
                                        translateSelectedTextViews();
                                    }

                                    int resID = obtenerImagenPorProducto("Proteínas", marca);

                                    imgProducto.setImageResource(resID);


                                });
                            }

                            @Override
                            public void onError(String mensaje) {
                                runOnUiThread(() ->
                                        Toast.makeText(ver_detalles_activity.this, mensaje, Toast.LENGTH_SHORT).show()
                                );
                            }
                        });
                    });
                }

                @Override
                public void onError(String mensaje) {
                    runOnUiThread(() ->
                            Toast.makeText(ver_detalles_activity.this, mensaje, Toast.LENGTH_SHORT).show()
                    );
                }
            });
        } else if ("saborizante".equals(tipoProducto)) {

            bd.getDetallesSaborizante(id, new BD.JsonCallback() {
                @Override
                public void onSuccess(JsonObject obj) {
                    runOnUiThread(()->{
                        String marca = obj.get("marca").getAsString();
                        String fecha = obj.get("fec_actualizacion").getAsString();
                        String tipo_saborizante = obj.get("tipo_saborizante").getAsString();
                        int cont_nutri = obj.get("cont_calorico").getAsInt();
                        String sabor = obj.get("sabor").getAsString();
                        String texto = "Sabor";

                        nombretxt.setText(marca);
                        fechatxt.setText("Ultima Fecha de Actualizacion:" + fecha);
                        marcatxt.setText(marca);
                        tipotxt.setText(tipo_saborizante);
                        cont_nutritxt.setText("Contenido calorico: "+ cont_nutri);
                        textoPrecauciones.setText(texto);
                        precauciontxt.setText(sabor);

                        if (loadLanguagePreference().equals("en")) {
                            translateSelectedTextViews();
                        }

                        int resID = obtenerImagenPorProducto("Saborizantes", sabor);

                        imgProducto.setImageResource(resID);



                    });
                }

                @Override
                public void onError(String mensaje) {
                    runOnUiThread(() ->
                            Toast.makeText(ver_detalles_activity.this, mensaje, Toast.LENGTH_SHORT).show()
                    );
                }
            });
        } else if ("curcuma".equals(tipoProducto)) {
            bd.getDetallesCurcuma(id, new BD.JsonCallback() {
                @Override
                public void onSuccess(JsonObject obj) {
                    runOnUiThread(()->{
                        String marca = obj.get("marca").getAsString();
                        String fecha = obj.get("fec_actualizacion").getAsString();
                        int cont_curcuminoide = obj.get("cont_curcuminoide").getAsInt();
                        int cont_gingerol = obj.get("cont_gingerol").getAsInt();
                        String precauciones = obj.get("precauciones").getAsString();

                        nombretxt.setText(marca);
                        fechatxt.setText("Ultima Fecha de Actualizacion:" + fecha);
                        marcatxt.setText(marca);
                        textoTipo.setText("Contenido curcuminoide (gr)");
                        tipotxt.setText("" + cont_curcuminoide);
                        textoDescNutri.setText("Contenido gingerol (gr)");
                        cont_nutritxt.setText(""+ cont_gingerol);
                        precauciontxt.setText(precauciones);

                        if (loadLanguagePreference().equals("en")) {
                            translateSelectedTextViews();
                        }


                        int resID  = obtenerImagenPorProducto("Cúrcuma y Jengibre", marca);
                        imgProducto.setImageResource(resID);
                    });
                }

                @Override
                public void onError(String mensaje) {
                    runOnUiThread(() ->
                            Toast.makeText(ver_detalles_activity.this, mensaje, Toast.LENGTH_SHORT).show()
                    );
                }
            });
        }

        //cuando sea un producto de saborizante, la seccion de precauciones desaparece

        findViewById(R.id.imageButton).setOnClickListener(v -> {
            finish();
            startActivity(new Intent(this, catalogo_producto_activity.class));

        });
    }

    // Cargar el idioma guardado
    private String loadLanguagePreference() {
        SharedPreferences preferences = getSharedPreferences(LANGUAGE_PREF, MODE_PRIVATE);


        return preferences.getString(SELECTED_LANGUAGE, "es"); // Default is Spanish

    }

    // Configurar el traductor ML Kit
    private void setupTranslator() {
        TranslatorOptions options = new TranslatorOptions.Builder()
                .setSourceLanguage(TranslateLanguage.SPANISH)
                .setTargetLanguage(TranslateLanguage.ENGLISH)
                .build();

        translator = Translation.getClient(options);

        translator.downloadModelIfNeeded()
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Modelo descargado", Toast.LENGTH_SHORT))
                .addOnFailureListener(e -> Toast.makeText(this, "Error al descargar el modelo: " + e.getMessage(), Toast.LENGTH_SHORT));
    }


    private void translateSelectedTextViews() {
        translateTextView(fechatxt);
        translateTextView(tipotxt);
        translateTextView(cont_nutritxt);
        translateTextView(textoPrecauciones);
        translateTextView(precauciontxt);
        translateTextView(textoDescNutri);

        translateTextView(tvNombreProteina);
        translateTextView(tvTipo);

    }

    private void translateTextView(TextView textView) {
        String originalText = textView.getText().toString();
        translator.translate(originalText)
                .addOnSuccessListener(translatedText -> textView.setText(translatedText))
                .addOnFailureListener(e -> Toast.makeText(this, "Error al traducir: " + e.getMessage(), Toast.LENGTH_SHORT));
    }

    private int obtenerImagenPorProducto(String categoria, String valorClave) {
        if (valorClave == null) return 0;

        valorClave = valorClave.toLowerCase();

        switch (categoria) {
            case "Proteínas":
                if (valorClave.contains("hero sport")) return R.drawable.pure_natural_img;
                if (valorClave.contains("birdman")) return R.drawable.falcon;
                return 0;

            case "Saborizantes":
                if (valorClave.contains("fresa")||valorClave.contains("strawberry")) return R.drawable.strawberry_pink;
                if (valorClave.contains("chocolate")) return R.drawable.choco_cafe;
                if (valorClave.contains("vainilla")|| valorClave.contains("vanilla")) return R.drawable.vanilla_yellow;


                return 0;

            case "Cúrcuma y Jengibre":
                if (valorClave.contains("nature heart")) return R.drawable.nature_heart_turmeric;
                return 0;
        }

        return 0;
    }


}