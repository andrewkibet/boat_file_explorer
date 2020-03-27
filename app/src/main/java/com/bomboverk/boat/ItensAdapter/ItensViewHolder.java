package com.bomboverk.boat.ItensAdapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bomboverk.boat.R;

public class ItensViewHolder extends RecyclerView.ViewHolder {

    ImageView icon;
    TextView nome, info;
    public LinearLayout background;

    public ItensViewHolder(@NonNull View itemView) {
        super(itemView);

        icon = itemView.findViewById(R.id.cel_item_icon);
        nome = itemView.findViewById(R.id.cel_item_name);
        info = itemView.findViewById(R.id.cel_item_info);

        background = itemView.findViewById(R.id.cel_background);
    }

    public void bind(final Itens item, final ItensAdaptador.OnItemClickListener listener) {

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(item, ItensViewHolder.this, getAdapterPosition());
            }
        });

        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                listener.OnLongPress(item, ItensViewHolder.this, getAdapterPosition());
                return true;
            }
        });
    }

}
