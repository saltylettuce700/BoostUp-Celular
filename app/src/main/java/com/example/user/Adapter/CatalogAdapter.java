package com.example.user.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.user.POJO.CatalogItem;
import com.example.user.R;

import java.util.List;


public class CatalogAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Context context;
    private List<CatalogItem> items;

    public CatalogAdapter(Context context, List<CatalogItem> items) {
        this.context = context;
        this.items = items;
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
            ((CategoryViewHolder) holder).categoryTitle.setText(item.getTitle());
        } else if (holder instanceof ProductViewHolder) {
            ProductViewHolder productHolder = (ProductViewHolder) holder;
            productHolder.productName.setText(item.getTitle());
            productHolder.productDescription.setText(item.getDescription());
            //Glide.with(context).load(item.getImageUrl()).into(productHolder.productImage);
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
        TextView productName, productDescription;
        //ImageView productImage;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.tvNombreProducto);
            productDescription = itemView.findViewById(R.id.tvDescripcionProducto);
            //productImage = itemView.findViewById(R.id.imgProducto);
        }
    }

}
