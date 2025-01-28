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

import POJO.BebidaDestacada;

public class BebidaAdapter extends RecyclerView.Adapter<BebidaAdapter.BebidaViewHolder>{

    private final Context context;
    private final List<BebidaDestacada> bebidas;

    public BebidaAdapter(Context context, List<BebidaDestacada> bebidas) {
        this.context = context;
        this.bebidas = bebidas;
    }

    @NonNull
    @Override
    public BebidaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_bebida_destacada, parent, false);
        return new BebidaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BebidaViewHolder holder, int position) {
        BebidaDestacada bebida = bebidas.get(position);
        holder.nombre.setText(bebida.getNombre());
        holder.sabor.setText(bebida.getSabor());
        holder.imagen.setImageResource(bebida.getImagenResId());
    }

    @Override
    public int getItemCount() {
        return bebidas.size();
    }

    public static class BebidaViewHolder extends RecyclerView.ViewHolder {
        TextView nombre, sabor;
        ImageView imagen;

        public BebidaViewHolder(@NonNull View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.itemTitle);
            sabor = itemView.findViewById(R.id.itemSubtitle);
            imagen = itemView.findViewById(R.id.itemImage);
        }
    }


}
