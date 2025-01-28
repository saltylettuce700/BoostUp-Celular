package com.example.user;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PaddingItemDecoration extends RecyclerView.ItemDecoration{

    private final int paddingStart;
    private final int paddingEnd;

    public PaddingItemDecoration(int paddingStart, int paddingEnd) {
        this.paddingStart = paddingStart;
        this.paddingEnd = paddingEnd;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        int itemCount = state.getItemCount();

        // Padding al primer elemento
        if (position == 0) {
            outRect.left = paddingStart;
        } else {
            outRect.left = 0;
        }

        // Padding al último elemento
        if (position == itemCount - 1) {
            outRect.right = paddingEnd;
        } else {
            outRect.right = 0;
        }

        // No hay cambios para top/bottom
        outRect.top = 0;
        outRect.bottom = 0;
    }
}
