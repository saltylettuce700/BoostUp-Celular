package com.example.user.Adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import POJO.Pedido;

import com.example.user.Activity.Registro.ver_detalles_activity;
import com.example.user.R;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.TranslatorOptions;
import com.google.mlkit.nl.translate.Translator;

import android.content.Context;


import java.util.List;

public class PedidoAdapter extends RecyclerView.Adapter<PedidoAdapter.PedidoViewHolder> {

    // Lista de datos que se mostrarán
    private final List<Pedido> pedidos;

    private Context context;
    private Translator translator;
    private String languageCode;

    // Constructor que recibe la lista de pedidos
    public PedidoAdapter(Context context, List<Pedido> pedidos,  String languageCode) {
        this.pedidos = pedidos;
        this.context = context;
        this.languageCode = languageCode;  // Asigna el idioma aqu
        setupTranslator(languageCode);
    }

    @NonNull
    @Override
    public PedidoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pedido, parent, false);
        return new PedidoViewHolder(view);
    }

    // Enlaza los datos con las vistas dentro del ViewHolder
    @Override
    public void onBindViewHolder(@NonNull PedidoViewHolder holder, int position) {
        Pedido pedido = pedidos.get(position); // Obtener el pedido actual
        holder.tvBebidaUsername.setText(pedido.getNombreBebida());
        holder.tvSabor.setText("Sabor: " + pedido.getSabor());
        holder.tvProteina.setText("Proteína: " + pedido.getProteina());
        // Puedes cargar imágenes en imgPedido si tienes URLs o recursos

        // Traducir los textos
        translateText(holder.tvBebidaUsername);
        translateText(holder.tvSabor);
        translateText(holder.tvProteina);

        holder.tvVerDetalles.setOnClickListener(v -> {
            Intent intent = new Intent(context, ver_detalles_activity.class);
            intent.putExtra("nombreBebida", pedido.getNombreBebida());
            intent.putExtra("sabor", pedido.getSabor());
            intent.putExtra("proteina", pedido.getProteina());
            // Puedes agregar más extras si los necesitas
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // importante si estás usando contexto de Adapter
            context.startActivity(intent);
        });

    }

    // Devuelve el número de elementos en la lista
    @Override
    public int getItemCount() {
        return pedidos.size();
    }



    static class PedidoViewHolder extends RecyclerView.ViewHolder {

        TextView tvBebidaUsername, tvSabor, tvProteina, tvVerDetalles;
        ImageView imgPedido;

        public PedidoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBebidaUsername = itemView.findViewById(R.id.tvBebidaUsername);
            tvSabor = itemView.findViewById(R.id.tvSabor);
            tvProteina = itemView.findViewById(R.id.tvProteina);
            tvVerDetalles = itemView.findViewById(R.id.tvVerDetalles);
            imgPedido = itemView.findViewById(R.id.imgProducto);
        }
    }

    // Configurar el Traductor ML Kit
    private void setupTranslator(String targetLang) {
        String sourceLang = TranslateLanguage.SPANISH;
        String targetLangCode = targetLang.equals("en") ? TranslateLanguage.ENGLISH : TranslateLanguage.SPANISH;

        TranslatorOptions options = new TranslatorOptions.Builder()
                .setSourceLanguage(sourceLang)
                .setTargetLanguage(targetLangCode)
                .build();

        translator = Translation.getClient(options);

        translator.downloadModelIfNeeded()
                .addOnSuccessListener(aVoid -> Toast.makeText(context, "Modelo descargado", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(context, "Error al descargar el modelo: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    // Traducir texto con ML Kit
    private void translateText(TextView textView) {
        // Usa el languageCode que ya está disponible
        String sourceText = textView.getText().toString().trim();

        if (sourceText.isEmpty() || !languageCode.equals("en")) {
            return; // No traducir si no hay texto o no es inglés
        }

        // Asegúrate de que el modelo esté listo
        translator.translate(sourceText)
                .addOnSuccessListener(translatedText -> {
                    textView.setText(translatedText);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Error al traducir: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }



}
