package com.example.user.Activity.Registro;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.user.Activity.account_activity;
import com.example.user.Activity.historial_activity;
import com.example.user.ConexionBD.BD;
import com.example.user.R;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class ver_detalles_activity extends AppCompatActivity {

    TextView nombretxt, fechatxt, marcatxt, tipotxt, cont_nutritxt, precauciontxt, textoPrecauciones, textoTipo, textoDescNutri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
            startActivity(new Intent(this, historial_activity.class));
            Toast.makeText(this, "PROGRESO", Toast.LENGTH_SHORT).show();

        });
    }
}