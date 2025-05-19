package com.example.user.Activity;

import static androidx.constraintlayout.widget.ConstraintSet.GONE;
import static com.example.user.Activity.account_activity.LANGUAGE_PREF;
import static com.example.user.Activity.account_activity.SELECTED_LANGUAGE;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.google.gson.JsonObject;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class ver_pedido_activity extends AppCompatActivity {

    TextView nombreBebida, precio, fechatxt, estado_pedido, proteinatxt, marca_proteina, gr_proteina, tipo_proteina;
    TextView sabortxt, marca_sabor, tipo_sabor, curcuma, curcuma_marca, curcuma_gr;

    ImageView img1, img2, img3;

    String idpedidoqr;

    Button ver_qr;

    ImageButton btnBack;

    private Translator translator;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Cargar el idioma guardado y aplicarlo
        String languageCode = loadLanguagePreference();

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ver_pedido);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        nombreBebida = findViewById(R.id.tvProductName);
        precio = findViewById(R.id.tvProductPrice);
        fechatxt = findViewById(R.id.tvOrderDate);
        estado_pedido = findViewById(R.id.tvOrderStatus);
        proteinatxt = findViewById(R.id.tvNombreProteina);
        marca_proteina = findViewById(R.id.tvDescripcionProteina);
        gr_proteina = findViewById(R.id.tvGramosProteina);
        tipo_proteina = findViewById(R.id.tvTipoProteina);
        sabortxt = findViewById(R.id.tvNombreSaborizante);
        marca_sabor = findViewById(R.id.tvDescripcionSabroizante);
        tipo_sabor = findViewById(R.id.tvTipoSaborizante);
        curcuma = findViewById(R.id.tvNombreCurcuma);
        curcuma_marca = findViewById(R.id.tvTipoCurcuma);
        curcuma_gr = findViewById(R.id.tvDescripcionCurcuma);

        img1 = findViewById(R.id.imgProducto);
        img2 = findViewById(R.id.imgProducto2);
        img3 = findViewById(R.id.imgProducto3);

        ver_qr = findViewById(R.id.btn_qr);


        // Configura el traductor
        setupTranslator();

        String idPedido = getIntent().getStringExtra("id_pedido");
        String tipoProteina = getIntent().getStringExtra("proteina");

        idpedidoqr = idPedido;


        findViewById(R.id.btn_qr).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showQRDialog();
            }
        });



        BD bd = new BD(this);

        bd.getDetallesPedido(idPedido, new BD.JsonCallback() {
            @Override
            public void onSuccess(JsonObject obj) {
                runOnUiThread(() -> {
                    String proteina = obj.get("proteina").getAsString();
                    double monto = obj.get("monto_total").getAsDouble();
                    String fechaCompleta = obj.get("fec_hora_compra").getAsString();
                    String fecha = fechaCompleta.split("T")[0]; // Solo la fecha, sin la hora

                    String estado = obj.get("estado_canje").getAsString();
                    int proteinaGr = obj.get("proteina_gr").getAsInt();
                    //String curcumaGr = obj.get("curcuma_gr").isJsonNull() ? "N/A" : obj.get("curcuma_gr").getAsString();
                    String sabor = obj.get("sabor").getAsString();

                    ImageView imageBackground = findViewById(R.id.imageBackground);
                    switch (sabor.toLowerCase()) {
                        case "chocolate":
                            imageBackground.setImageResource(R.drawable.choco_cafe);
                            break;
                        case "vainilla":
                            imageBackground.setImageResource(R.drawable.vanilla_yellow);
                            break;
                        case "fresa":
                            imageBackground.setImageResource(R.drawable.strawberry_pink);
                            break;
                        default:
                            imageBackground.setImageResource(R.drawable.bebida_img); // Imagen por defecto
                            break;
                    }

                    if (estado.equals("canjeado")){
                        ver_qr.setVisibility(View.GONE);
                    }else{
                        ver_qr.setVisibility(View.VISIBLE);
                    }


                    String tipoSaborizante = obj.get("tipo_saborizante").getAsString();
                    String marcaProteina = obj.get("proteina_marca").getAsString();
                    //String marcaCurcuma = obj.get("curcuma_marca").isJsonNull() ? "N/A" : obj.get("curcuma_marca").getAsString();
                    String marcaSaborizante = obj.get("saborizante_marca").getAsString();

                    int curcumaGr = 0;
                    String curcumaGrTexto = "N/A";
                    String marcaCurcuma = "N/A";

                    try {
                        curcumaGr = obj.get("curcuma_gr").getAsInt();
                        curcumaGrTexto = curcumaGr + " gr";
                        marcaCurcuma = obj.get("curcuma_marca").getAsString();
                    } catch (Exception e) {
                        Log.e("Curcuma", e.getMessage());
                    }

                    nombreBebida.setText(proteina);
                    precio.setText("$"+monto);
                    fechatxt.setText("Fecha de creación del pedido: "+ fecha);
                    estado_pedido.setText("Estado del pedido: " + estado);
                    proteinatxt.setText(proteina);
                    marca_proteina.setText(marcaProteina);
                    gr_proteina.setText(proteinaGr + " gr");
                    tipo_proteina.setText("Proteina: "+ tipoProteina);
                    int totalGramos = proteinaGr + curcumaGr;
                    gr_proteina.setText(totalGramos + " gr");
                    sabortxt.setText(sabor);
                    marca_sabor.setText(marcaSaborizante);
                    tipo_sabor.setText("Saborizante: " + tipoSaborizante);
                    curcuma.setText("Cúrcuma");
                    curcuma_marca.setText(marcaCurcuma);
                    curcuma_gr.setText(curcumaGrTexto);

                    int resID1 = obtenerImagenPorProducto("Proteínas", proteina);
                    int resID2 = obtenerImagenPorProducto("Saborizantes",sabor);
                    int resID3 = obtenerImagenPorProducto("Cúrcuma y Jengibre",marcaCurcuma);

                    img1.setImageResource(resID1);
                    img2.setImageResource(resID2);
                    img3.setImageResource(resID3);

                    if(loadLanguagePreference().equals("en")){
                        translateSelectedTextViews();
                    }

                });
            }

            @Override
               public void onError(String mensaje) {
                runOnUiThread(() -> {


                    Toast.makeText(ver_pedido_activity.this, mensaje, Toast.LENGTH_SHORT).show();
                });
                }
            });


        findViewById(R.id.imageButton).setOnClickListener(v -> {
            finish();
            startActivity(new Intent(this, historial_activity.class));
            Toast.makeText(this, "PROGRESO", Toast.LENGTH_SHORT).show();

        });


    }

    private void showQRDialog() {
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();

        Dialog dialog = new Dialog(this, R.style.BlurBackgroundDialog);

        // Inflar el diseño personalizado
        View dialogView = LayoutInflater.from(this).inflate(R.layout.custom_dialog_qr, null);
        dialog.setContentView(dialogView);

        // Configurar botones
        ImageView btn_close = dialogView.findViewById(R.id.btn_close);
        ImageView qr = dialogView.findViewById(R.id.generated_qr);


        try {

            BitMatrix bitMatrix = multiFormatWriter.encode(idpedidoqr, BarcodeFormat.QR_CODE,600,600);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);

            qr.setImageBitmap(bitmap);

        }catch (WriterException e) {

            throw new RuntimeException(e);

        }


        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ver_pedido_activity.this, "Cerrar qr", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        // Mostrar el diálogo
        dialog.show();


    }

    // Cargar el idioma guardado
    private String loadLanguagePreference() {
        SharedPreferences preferences = getSharedPreferences(LANGUAGE_PREF, MODE_PRIVATE);

        Toast.makeText(this, "Idioma actual: " + preferences.getString(SELECTED_LANGUAGE, "es"), Toast.LENGTH_SHORT).show(); // Verifica el idioma

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
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Modelo descargado", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Error al descargar el modelo: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }


    private void translateSelectedTextViews() {
        translateTextView(fechatxt);
        translateTextView(estado_pedido);
        translateTextView(tipo_proteina);
        translateTextView(sabortxt);
        translateTextView(tipo_sabor);
        translateTextView(curcuma);
        translateTextView(ver_qr);
    }

    private void translateTextView(TextView textView) {
        String originalText = textView.getText().toString();
        translator.translate(originalText)
                .addOnSuccessListener(translatedText -> textView.setText(translatedText))
                .addOnFailureListener(e -> Toast.makeText(this, "Error al traducir: " + e.getMessage(), Toast.LENGTH_SHORT).show());
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

}