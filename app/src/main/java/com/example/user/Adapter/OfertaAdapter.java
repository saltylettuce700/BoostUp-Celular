package com.example.user.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.user.R;

import java.util.List;
import POJO.Oferta;

public class OfertaAdapter extends RecyclerView.Adapter<OfertaAdapter.OfertaViewHolder>{

    private final Context context;
    private final List<Oferta> ofertas;

    public OfertaAdapter(Context context, List<Oferta> ofertas) {
        this.context = context;
        this.ofertas = ofertas;
    }

    @NonNull
    @Override
    public OfertaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_ofertas, parent, false);
        return new OfertaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OfertaViewHolder  holder, int position) {
        Oferta oferta = ofertas.get(position);

        holder.imagen.setImageResource(oferta.getImagenResIdOferta());
    }

    @Override
    public int getItemCount() {
        return ofertas.size();
    }

    public static class OfertaViewHolder extends RecyclerView.ViewHolder {

        ImageView imagen;

        public OfertaViewHolder(@NonNull View itemView) {
            super(itemView);
            imagen = itemView.findViewById(R.id.mainImage);
        }
    }
}
