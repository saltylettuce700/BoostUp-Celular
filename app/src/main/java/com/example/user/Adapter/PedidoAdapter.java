package com.example.user.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import POJO.Pedido;
import com.example.user.R;

import java.util.List;

public class PedidoAdapter extends RecyclerView.Adapter<PedidoAdapter.PedidoViewHolder> {

    // Lista de datos que se mostrarán
    private final List<Pedido> pedidos;

    // Constructor que recibe la lista de pedidos
    public PedidoAdapter(List<Pedido> pedidos) {
        this.pedidos = pedidos;
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
            imgPedido = itemView.findViewById(R.id.imgPedido);
        }
    }


}
