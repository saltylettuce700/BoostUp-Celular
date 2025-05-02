package com.example.user.Activity;

import static com.example.user.Activity.account_activity.LANGUAGE_PREF;
import static com.example.user.Activity.account_activity.SELECTED_LANGUAGE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.user.ConexionBD.BD;
import com.example.user.R;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.TranslatorOptions;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import com.google.mlkit.nl.translate.Translator;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class crear_pedido_activity extends AppCompatActivity {

    private ListView listProteinas, listSaborizantes, listCurcuma;
    private String selectedProteina, selectedSaborizante, selectedCurcuma, orderString;

    String[] proteinas;
    String[] saborizantes;
    String[] curcuma;

    TextView txtBebidasUsername;

    private Translator translator;


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
        ImageView qr = findViewById(R.id.generated_qr);
        txtBebidasUsername = findViewById(R.id.tvProductName);

        // Configura el traductor
        setupTranslator();

        // Datos de prueba
        //String[] proteinas = {"Proteína Genérica Vegetal", "Proteína Genérica Animal", "Proteína Premium"};
        //String[] saborizantes = {"Vainilla", "Chocolate", "Fresa"};
        //String[] curcuma = {"Con cúrcuma de la chida", "Con cúrcuma de la chafa"};

        llenarUsername();
        llenarCadenas();

        // Traducir los elementos antes de configurar los ListView
//        translateListItems(proteinas, translatedProteinas -> {
//            translateListItems(saborizantes, translatedSaborizantes -> {
//                translateListItems(curcuma, translatedCurcuma -> {
//                    // Configurar los ListView con los elementos traducidos
//                    configureListView(listProteinas, translatedProteinas, selected -> selectedProteina = selected);
//                    configureListView(listSaborizantes, translatedSaborizantes, selected -> selectedSaborizante = selected);
//                    configureListView(listCurcuma, translatedCurcuma, selected -> selectedCurcuma = selected);
//                });
//            });
//        });


        btn_pagar.setOnClickListener(v -> {
            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
            String orderString = formatOrder();

            Toast.makeText(this, "ver_pedidoAfterPago", Toast.LENGTH_SHORT).show();

            if (selectedProteina != null && selectedSaborizante != null && selectedCurcuma != null) {
                Toast.makeText(this, "Pedido: \n" +
                        "Proteína: " + selectedProteina + "\n" +
                        "Saborizante: " + selectedSaborizante + "\n" +
                        "Cúrcuma: " + selectedCurcuma, Toast.LENGTH_LONG).show();

                try {
                    BitMatrix bitMatrix = multiFormatWriter.encode(orderString, BarcodeFormat.QR_CODE,300,300);
                    BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                    Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);

                    qr.setImageBitmap(bitmap);

                }catch (WriterException e) {

                    throw new RuntimeException(e);

                }

                Intent intent = new Intent(crear_pedido_activity.this, ver_pedidoAfterPago.class);
                intent.putExtra("pedidoDetalles", orderString);
                startActivity(intent);

            } else {
                Toast.makeText(this, "Selecciona todas las opciones", Toast.LENGTH_SHORT).show();
            }




        });

        findViewById(R.id.imageButton).setOnClickListener(v -> {
            finish();
            startActivity(new Intent(this, home_activity.class));
            Toast.makeText(this, "PROGRESO", Toast.LENGTH_SHORT).show();

        });


    }

    private void llenarUsername() {
        BD bd = new BD(this);

        bd.getInfoUser(new BD.JsonCallback() {
            @Override
            public void onSuccess(JsonObject obj) {

                runOnUiThread(() ->{
                    String username = obj.get("username").getAsString();
                    txtBebidasUsername.setText("Bebidas "+ username);
                });
            }

            @Override
            public void onError(String mensaje) {
                runOnUiThread(() -> {
                    Toast.makeText(crear_pedido_activity.this, mensaje, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void llenarCadenas() {
        BD bd = new BD(this);

        bd.getOpcionesProteinas(new BD.JsonArrayCallback() {
            @Override
            public void onSuccess(JsonArray array) {
                proteinas = new String[array.size()];
                int i = 0;
                for (JsonElement elemento : array) {
                    JsonObject obj = elemento.getAsJsonObject();
                    String nombre = obj.get("nombre").getAsString();
                    proteinas[i++] = nombre;
                }

                // Luego de cargar proteínas, cargar saborizantes
                new Thread(() -> {
                    List<Map<String, String>> lista = bd.getSaborizantes();
                    if (lista != null) {
                        saborizantes = new String[lista.size()];
                        for (int j = 0; j < lista.size(); j++) {
                            saborizantes[j] = lista.get(j).get("sabor");
                        }

                        new Thread(() -> {
                            int idcurcuma = 1;
                            double precio = bd.getPrecioCurcuma(idcurcuma);

                            if (precio != -1) {
                                curcuma = new String[]{("Con cúrcuma +$" + precio), "Sin cúrcuma"};
                            } else {
                                curcuma = new String[]{"Con cúrcuma", "Sin cúrcuma"};
                            }

                            // Aquí obtenemos el idioma
                            String languageCode = loadLanguagePreference();


                            runOnUiThread(() -> {
                                if (languageCode.equals("en")) {
                                    translateListItems(proteinas, translatedProteinas -> {
                                        translateListItems(saborizantes, translatedSaborizantes -> {
                                            translateListItems(curcuma, translatedCurcuma -> {
                                                configureListView(listProteinas, translatedProteinas, selected -> selectedProteina = selected);
                                                configureListView(listSaborizantes, translatedSaborizantes, selected -> selectedSaborizante = selected);
                                                configureListView(listCurcuma, translatedCurcuma, selected -> selectedCurcuma = selected);
                                            });
                                        });
                                    });
                                } else {
                                    // Si no es inglés, carga sin traducir
                                    configureListView(listProteinas, proteinas, selected -> selectedProteina = selected);
                                    configureListView(listSaborizantes, saborizantes, selected -> selectedSaborizante = selected);
                                    configureListView(listCurcuma, curcuma, selected -> selectedCurcuma = selected);
                                }
                            });

                        }).start();

                    } else {
                        runOnUiThread(() ->
                                Toast.makeText(crear_pedido_activity.this, "Error al cargar saborizantes", Toast.LENGTH_SHORT).show()
                        );
                    }
                }).start();
            }

            @Override
            public void onError(String mensaje) {
                Toast.makeText(crear_pedido_activity.this, mensaje, Toast.LENGTH_SHORT).show();
            }
        });
    }


    // Configurar el traductor ML Kit
    private void setupTranslator() {
        TranslatorOptions options = new TranslatorOptions.Builder()
                .setSourceLanguage(TranslateLanguage.SPANISH)
                .setTargetLanguage(TranslateLanguage.ENGLISH)
                .build();

        translator = Translation.getClient(options);

        translator.downloadModelIfNeeded()
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Modelo descargado", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Error al descargar el modelo: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    // Traducir los elementos de la lista
    private void translateListItems(String[] items, OnItemsTranslatedListener listener) {
        List<String> translatedItems = new ArrayList<>();
        for (String item : items) {
            translator.translate(item)
                    .addOnSuccessListener(translatedText -> {
                        translatedItems.add(translatedText);
                        if (translatedItems.size() == items.length) {
                            listener.onItemsTranslated(translatedItems.toArray(new String[0]));
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error al traducir: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    // Formatea el pedido en un string estructurado
    private String formatOrder() {
        try {
            JSONObject orderJson = new JSONObject();
            orderJson.put("proteina", selectedProteina);
            orderJson.put("saborizante", selectedSaborizante);
            orderJson.put("curcuma", selectedCurcuma);

            return orderJson.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return "{}"; // Devuelve un JSON vacío en caso de error
        }
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

    // Interfaz para manejar los elementos traducidos
    interface OnItemsTranslatedListener {
        void onItemsTranslated(String[] translatedItems);
    }

    // Cargar el idioma guardado
    private String loadLanguagePreference() {
        SharedPreferences preferences = getSharedPreferences(LANGUAGE_PREF, MODE_PRIVATE);


        return preferences.getString(SELECTED_LANGUAGE, "es"); // Default is Spanish

    }
}