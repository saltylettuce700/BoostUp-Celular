package com.example.user.Activity;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
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

public class ver_pedido_activity extends AppCompatActivity {

    TextView nombreBebida, precio, fechatxt, estado_pedido, proteinatxt, marca_proteina, gr_proteina, tipo_proteina;
    TextView sabortxt, marca_sabor, tipo_sabor, curcuma, curcuma_marca, curcuma_gr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        findViewById(R.id.btn_qr).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showQRDialog();
            }
        });

        String idPedido = getIntent().getStringExtra("id_pedido");
        String tipoProteina = getIntent().getStringExtra("proteina");

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
                    String tipoSaborizante = obj.get("tipo_saborizante").getAsString();
                    String marcaProteina = obj.get("proteina_marca").getAsString();
                    //String marcaCurcuma = obj.get("curcuma_marca").isJsonNull() ? "N/A" : obj.get("curcuma_marca").getAsString();
                    String marcaSaborizante = obj.get("saborizante_marca").getAsString();
                    int curcumaGr = 0;
                    String curcumaGrTexto;
                    String marcaCurcuma;

                    if (!obj.get("curcuma_gr").isJsonNull()) {
                        curcumaGr = obj.get("curcuma_gr").getAsInt();
                        curcumaGrTexto = curcumaGr + " gr";
                        marcaCurcuma = obj.get("curcuma_marca").getAsString();
                    } else {
                        curcumaGrTexto = "N/A";
                        marcaCurcuma = "N/A";
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
                    curcuma.setText("Curcuma");
                    curcuma_marca.setText(marcaCurcuma);
                    curcuma_gr.setText(curcumaGrTexto);

                });
            }

            @Override
               public void onError(String mensaje) {
                runOnUiThread(() -> {
                    Toast.makeText(ver_pedido_activity.this, mensaje, Toast.LENGTH_SHORT).show();
                });
                }
            });


    }

    private void showQRDialog() {

        Dialog dialog = new Dialog(this, R.style.BlurBackgroundDialog);

        // Inflar el diseño personalizado
        View dialogView = LayoutInflater.from(this).inflate(R.layout.custom_dialog_qr, null);
        dialog.setContentView(dialogView);

        // Configurar botones
        ImageView btn_close = dialogView.findViewById(R.id.btn_close);


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
}