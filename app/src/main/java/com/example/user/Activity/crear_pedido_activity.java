package com.example.user.Activity;

import static com.example.user.Activity.account_activity.LANGUAGE_PREF;
import static com.example.user.Activity.account_activity.SELECTED_LANGUAGE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class crear_pedido_activity extends AppCompatActivity {

    private ListView listProteinas, listSaborizantes, listCurcuma;
    private String selectedProteina, selectedSaborizante, selectedCurcuma, orderString;

    String[] proteinas;
    String[] saborizantes;
    String[] curcuma;

    //Stripe
    private String paymentIntentClientSecret;
    private PaymentSheet paymentSheet;


    TextView txtBebidasUsername;

    private Translator translator;
    private List<Integer> alergiasUsuario = new ArrayList<>();

    private TextView txtPrecioTotal;
    private double precioBase = 70.00;
    private double precioCurcuma = 10.0;

    private Map<String, Integer> proteinaNombreToId = new HashMap<>();
    private Map<String, Integer> saborizanteNombreToId = new HashMap<>();
    private boolean tieneCurcuma = false;


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

        txtPrecioTotal = findViewById(R.id.tvProductPrice);

        //Stripe
        paymentSheet = new PaymentSheet(this, this::onPaymentSheetResult);

        // Configura el traductor
        setupTranslator();

        // Datos de prueba
        //String[] proteinas = {"Proteína Genérica Vegetal", "Proteína Genérica Animal", "Proteína Premium"};
        //String[] saborizantes = {"Vainilla", "Chocolate", "Fresa"};
        //String[] curcuma = {"Con cúrcuma de la chida", "Con cúrcuma de la chafa"};

        llenarUsername();

        obtenerAlergiasUsuario();
        //llenarCadenas();

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
                    crearPedido();
                    BitMatrix bitMatrix = multiFormatWriter.encode(orderString, BarcodeFormat.QR_CODE,300,300);
                    BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                    Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);

                    qr.setImageBitmap(bitmap);

                }catch (WriterException e) {

                    throw new RuntimeException(e);

                }

//                Intent intent = new Intent(crear_pedido_activity.this, ver_pedidoAfterPago.class);
//                intent.putExtra("pedidoDetalles", orderString);
//                startActivity(intent);

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

    private void obtenerAlergiasUsuario() {
        BD bd = new BD(this);

        bd.getAlergiasUser(new BD.JsonArrayCallback() {
            @Override
            public void onSuccess(JsonArray array) {
                runOnUiThread(()-> {
                    alergiasUsuario.clear();
                    for (JsonElement element : array){
                        JsonObject obj = element.getAsJsonObject();
                        int tipoAlergeno = obj.get("tipo_alergeno").getAsInt();
                        alergiasUsuario.add(tipoAlergeno);
                    }

                    llenarCadenas();
                });
            }

            @Override
            public void onError(String mensaje) {
                runOnUiThread(() ->{
                    llenarCadenas();
                    Toast.makeText(crear_pedido_activity.this, "Error al obtener alergias", Toast.LENGTH_SHORT).show();
                });
            }
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

//    private void llenarCadenas() {
//
//        BD bd = new BD(this);
//
//        bd.getOpcionesProteinas(new BD.JsonArrayCallback() {
//            @Override
//            public void onSuccess(JsonArray array) {
//                proteinas = new String[array.size()];
//                List<String> listaProteinasWarning = new ArrayList<>();
//                List<Integer> idsProteina = new ArrayList<>();
//                List<String> listaProteinas = new ArrayList<>();
//
//                //int i = 0;
//                for (JsonElement elemento : array) {
//                    JsonObject obj = elemento.getAsJsonObject();
//                    int idProteina = obj.get("id_proteina").getAsInt();
//                    String nombre = obj.get("nombre").getAsString();
//                    //proteinas[i++] = nombre;
//                    listaProteinas.add(nombre);
//                    idsProteina.add(idProteina);
//                }
//
//                proteinas = new String[listaProteinas.size()];
//
//                int totalProteinas = listaProteinas.size();
//                int[] completadas = {0};
//
//                // Luego de cargar proteínas, cargar saborizantes
//                new Thread(() -> {
//                    List<Map<String, String>> lista = bd.getSaborizantes();
//                    if (lista != null) {
//                        saborizantes = new String[lista.size()];
//                        for (int j = 0; j < lista.size(); j++) {
//                            saborizantes[j] = lista.get(j).get("sabor");
//                        }
//
//                        new Thread(() -> {
//                            int idcurcuma = 1;
//                            double precio = bd.getPrecioCurcuma(idcurcuma);
//
//                            if (precio != -1) {
//                                curcuma = new String[]{("Con cúrcuma +$" + precio), "Sin cúrcuma"};
//                            } else {
//                                curcuma = new String[]{"Con cúrcuma", "Sin cúrcuma"};
//                            }
//
//                            // Aquí obtenemos el idioma
//                            String languageCode = loadLanguagePreference();
//
//
//                            runOnUiThread(() -> {
//                                if (languageCode.equals("en")) {
//                                    translateListItems(proteinas, translatedProteinas -> {
//                                        translateListItems(saborizantes, translatedSaborizantes -> {
//                                            translateListItems(curcuma, translatedCurcuma -> {
//                                                configureListView(listProteinas, translatedProteinas, selected -> selectedProteina = selected);
//                                                configureListView(listSaborizantes, translatedSaborizantes, selected -> selectedSaborizante = selected);
//                                                configureListView(listCurcuma, translatedCurcuma, selected -> selectedCurcuma = selected);
//                                            });
//                                        });
//                                    });
//                                } else {
//                                    // Si no es inglés, carga sin traducir
//                                    configureListView(listProteinas, proteinas, selected -> selectedProteina = selected);
//                                    configureListView(listSaborizantes, saborizantes, selected -> selectedSaborizante = selected);
//                                    configureListView(listCurcuma, curcuma, selected -> selectedCurcuma = selected);
//                                }
//                            });
//
//                        }).start();
//
//                    } else {
//                        runOnUiThread(() ->
//                                Toast.makeText(crear_pedido_activity.this, "Error al cargar saborizantes", Toast.LENGTH_SHORT).show()
//                        );
//                    }
//                }).start();
//            }
//
//            @Override
//            public void onError(String mensaje) {
//                Toast.makeText(crear_pedido_activity.this, mensaje, Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

    private void llenarCadenas() {
        BD bd = new BD(this);

        bd.getOpcionesProteinas(new BD.JsonArrayCallback() {
            @Override
            public void onSuccess(JsonArray array) {
                List<String> listaProteinas = new ArrayList<>();
                List<String> listaProteinasAmarillo = new ArrayList<>();
                List<Integer> idsProteina = new ArrayList<>();

                for (JsonElement elemento : array) {
                    JsonObject obj = elemento.getAsJsonObject();
                    int idProteina = obj.get("id_proteina").getAsInt();
                    String nombre = obj.get("nombre").getAsString();
                    listaProteinas.add(nombre);
                    idsProteina.add(idProteina);
                    proteinaNombreToId.put(nombre, idProteina);

                }

                proteinas = new String[listaProteinas.size()];

                // Para manejar respuestas asíncronas
                int totalProteinas = listaProteinas.size();
                int[] completadas = {0};

                for (int i = 0; i < totalProteinas; i++) {
                    int index = i;
                    int idProteina = idsProteina.get(i);
                    String nombre = listaProteinas.get(i);

                    bd.getAlergenosProteina(idProteina, new BD.JsonArrayCallback() {
                        @Override
                        public void onSuccess(JsonArray alergenosArray) {
                            boolean tieneAlergeno = false;

                            for (JsonElement e : alergenosArray) {
                                JsonObject aler = e.getAsJsonObject();
                                int tipo = aler.get("tipo_alergeno").getAsInt();

                                if (alergiasUsuario.contains(tipo)) {
                                    tieneAlergeno = true;
                                    break;
                                }
                            }

                            if (tieneAlergeno) {
                                listaProteinasAmarillo.add(nombre); // Puedes guardar este para luego colorear
                            }

                            proteinas[index] = nombre;

                            completadas[0]++;
                            if (completadas[0] == totalProteinas) {
                                cargarSaborizantesYCurcuma(bd, listaProteinasAmarillo);
                            }
                        }

                        @Override
                        public void onError(String mensaje) {
                            proteinas[index] = nombre;
                            completadas[0]++;
                            if (completadas[0] == totalProteinas) {
                                cargarSaborizantesYCurcuma(bd, listaProteinasAmarillo);
                            }
                        }
                    });
                }
            }

            @Override
            public void onError(String mensaje) {
                Toast.makeText(crear_pedido_activity.this, mensaje, Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void cargarSaborizantesYCurcuma(BD bd, List<String> proteinasAmarillo) {
        new Thread(() -> {
            List<Map<String, String>> lista = bd.getSaborizantes();
            if (lista != null) {
                saborizantes = new String[lista.size()];
                for (int j = 0; j < lista.size(); j++) {
                    String nombreSabor = lista.get(j).get("sabor");
                    String id = lista.get(j).get("id");

                    int id_sabor = Integer.parseInt(id);
                    saborizanteNombreToId.put(nombreSabor, id_sabor);
                    saborizantes[j] = nombreSabor;

                }



                new Thread(() -> {
                    int idcurcuma = 1;
                    precioCurcuma = bd.getPrecioCurcuma(idcurcuma);
                    if (precioCurcuma != -1) {
                        curcuma = new String[]{("Con cúrcuma +$" + precioCurcuma), "No agregar cúrcuma"};
                    } else {
                        precioCurcuma = 0.0;
                        curcuma = new String[]{"Con cúrcuma", "No agregar cúrcuma"};
                    }

                    String languageCode = loadLanguagePreference();

                    runOnUiThread(() -> {
                        if (languageCode.equals("en")) {
                            translateListItems(proteinas, translatedProteinas -> {
                                translateListItems(proteinasAmarillo.toArray(new String[0]), translatedAmarillos -> {
                                    colorearProteinas(translatedProteinas, Arrays.asList(translatedAmarillos));
                                    translateListItems(saborizantes, translatedSaborizantes -> {
                                        translateListItems(curcuma, translatedCurcuma -> {
                                            configureListView(listProteinas, translatedProteinas, selected -> selectedProteina = selected);
                                            configureListView(listSaborizantes, translatedSaborizantes, selected -> selectedSaborizante = selected);
                                            configureListView(listCurcuma, translatedCurcuma, selected -> {
                                                selectedCurcuma = selected;
                                                int index = Arrays.asList(translatedCurcuma).indexOf(selected);
                                                tieneCurcuma = (index == 0); // Sigue siendo la primera opción
                                            });
                                        });
                                    });
                                });
                            });
                        } else {
                            colorearProteinas(proteinas, proteinasAmarillo);
                            configureListView(listProteinas, proteinas, selected -> selectedProteina = selected);
                            configureListView(listSaborizantes, saborizantes, selected -> selectedSaborizante = selected);
                            configureListView(listCurcuma, curcuma, selected -> {
                                selectedCurcuma = selected;
                                int index = Arrays.asList(curcuma).indexOf(selected);
                                tieneCurcuma = (index == 0); // La opción "Con cúrcuma" siempre es la primera
                            });
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

    private void colorearProteinas(String[] proteinas, List<String> proteinasAmarillo) {
        for (int i = 0; i < proteinas.length; i++) {
            if (proteinasAmarillo.contains(proteinas[i])) {
                proteinas[i] = "⚠️ " + proteinas[i]; // Marca visual
            }
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
        listView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedItem = adapter.getItem(position);
            listener.onItemSelected(selectedItem);



            if (listView == listCurcuma) {
                if (selectedItem.contains("Con cúrcuma")||selectedItem.contains("With turmeric")|| selectedItem.contains("With turmeric +$")) {
                    actualizarPrecio(precioBase + precioCurcuma);
                } else {
                    actualizarPrecio(precioBase);
                }
            }
        });
    }

    private void actualizarPrecio(double nuevoPrecio) {
        txtPrecioTotal.setText(String.format("$%.2f", nuevoPrecio));
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


    /*-----------------Stripe------------------*/
    private void showToast(String message) {
        runOnUiThread(() -> Toast.makeText(this, message, Toast.LENGTH_LONG).show());
    }

    private void showAlert(String title, @Nullable String message) {
        runOnUiThread(() -> {
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton("Ok", null)
                    .create();
            dialog.show();
        });
    }

    private void onPaymentSheetResult(final PaymentSheetResult paymentSheetResult) {
        if (paymentSheetResult instanceof PaymentSheetResult.Completed) {
            showToast("Payment complete!");
        } else if (paymentSheetResult instanceof PaymentSheetResult.Canceled) {
            Log.i("CheckoutActivity", "Payment canceled!");
        } else if (paymentSheetResult instanceof PaymentSheetResult.Failed) {
            Throwable error = ((PaymentSheetResult.Failed) paymentSheetResult).getError();
            showAlert("Payment failed", error != null ? error.getLocalizedMessage() : "Unknown error");
        }
    }

    private void crearPedido() {

        int idProteina = proteinaNombreToId.getOrDefault(selectedProteina, -1);
        int idSaborizante = saborizanteNombreToId.getOrDefault(selectedSaborizante, -1);
        int idCurcuma = tieneCurcuma ? 1 : -1;

        BD bd = new BD(this);

        bd.crearPedido(idProteina, idCurcuma, idSaborizante, new BD.PedidoCallback() {
            @Override
            public void onSuccess(String clientSecret, String id_pedido) {
                runOnUiThread(() -> {
                    Toast.makeText(crear_pedido_activity.this, "Pedido creado con exito", Toast.LENGTH_SHORT).show();
                    PaymentSheet.Configuration configuration = new PaymentSheet.Configuration.Builder("BoostUp Inc.").build();
                    paymentIntentClientSecret = clientSecret;
                    paymentSheet.presentWithPaymentIntent(paymentIntentClientSecret, configuration);
                    String QR = id_pedido;
                    Toast.makeText(crear_pedido_activity.this, QR, Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onFailure() {

            }
        });
    }
}