package com.example.user.Adapter;


import static com.example.user.Activity.account_activity.LANGUAGE_PREF;
import static com.example.user.Activity.account_activity.SELECTED_LANGUAGE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import POJO.CatalogItem;

import com.example.user.Activity.Registro.ver_detalles_activity;
import com.example.user.R;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;

import com.google.mlkit.nl.translate.TranslatorOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class CatalogAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{


    private Context context;
    private List<CatalogItem> items;

    private Translator translator;

    private String languageCode;

    public CatalogAdapter(Context context, List<CatalogItem> items, String languageCode) {
        this.context = context;
        this.items = items;


        this.languageCode = languageCode;  // Asigna el idioma aqu
        setupTranslator(languageCode);
    }

    public int getItemViewType(int position) {
        return items.get(position).getType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == CatalogItem.TYPE_CATEGORY) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_category_header, parent, false);
            return new CategoryViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_producto, parent, false);
            return new ProductViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        CatalogItem item = items.get(position);

        if (holder instanceof CategoryViewHolder) {
            CategoryViewHolder categoryHolder = (CategoryViewHolder) holder;
            categoryHolder.categoryTitle.setText(item.getTitle());

            translateText(categoryHolder.categoryTitle);
        } else if (holder instanceof ProductViewHolder) {
            ProductViewHolder productHolder = (ProductViewHolder) holder;
            productHolder.productName.setText(item.getTitle());
            productHolder.productDescription.setText(item.getDescription());
            productHolder.productTypeProtein.setText(item.getTipoProteinaSaborizante());

            translateText(productHolder.productName);
            translateText(productHolder.productDescription);
            translateText(productHolder.productTypeProtein);

            productHolder.tvVerDetalles.setOnClickListener(view -> {
                Intent intent = new Intent(context, ver_detalles_activity.class);
                intent.putExtra("id", item.getId());
                intent.putExtra("tipoProducto", item.getTipoProducto());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            });
        }

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView categoryTitle;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryTitle = itemView.findViewById(R.id.tvCategoryTitle);
        }
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView productName, productDescription, productTypeProtein, tvVerDetalles;
        //ImageView productImage;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.tvNombreProducto);
            productDescription = itemView.findViewById(R.id.tvDescripcionProducto);
            productTypeProtein = itemView.findViewById(R.id.tvProteina);
            tvVerDetalles = itemView.findViewById(R.id.tvVerDetalles);
            //productImage = itemView.findViewById(R.id.imgProducto);
        }
    }


    // Configurar el Traductor ML Kit
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



    // Cargar idioma guardado
   /* private String loadLanguagePreference() {
        SharedPreferences preferences = context.getSharedPreferences(LANGUAGE_PREF, Context.MODE_PRIVATE);
        String language = preferences.getString(SELECTED_LANGUAGE, "es"); // Default es Español
        Toast.makeText(context, "Idioma actual: " + language, Toast.LENGTH_SHORT).show(); // Verifica el idioma
        return language;
    }*/





}
