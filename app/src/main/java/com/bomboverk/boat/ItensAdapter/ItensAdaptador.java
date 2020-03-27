package com.bomboverk.boat.ItensAdapter;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bomboverk.boat.R;
import com.bomboverk.boat.StorageHelpers.StorageHelper;

import java.util.ArrayList;

public class ItensAdaptador extends RecyclerView.Adapter{

    private StorageHelper storageHelper;

    private ArrayList<Itens> itens;
    private Context context;
    private OnItemClickListener listener;

    public SparseBooleanArray selectedItems;

    public ItensAdaptador(ArrayList<Itens> itens, Context context, OnItemClickListener listener) {
        this.itens = itens;
        this.context = context;
        this.listener = listener;
        storageHelper = new StorageHelper(null,context);
        selectedItems = new SparseBooleanArray();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.celula_itens, parent, false);
        return new ItensViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        ItensViewHolder meuViewHolder = (ItensViewHolder) holder;
        Itens item = itens.get(position);

        meuViewHolder.nome.setText(item.getNome());
        meuViewHolder.icon.setImageResource(storageHelper.getFileIcon(item.getExtension()));

        meuViewHolder.background.setSelected(selectedItems.get(position, false));

        if (item.getExtension().equals("folder")){
            meuViewHolder.info.setText(item.getItensCount() + " " +context.getString(R.string.class_itensadaptador_items));
        }else{
            meuViewHolder.info.setText(item.getTamanho());
        }

        meuViewHolder.bind(itens.get(position), listener);
    }

    public interface OnItemClickListener{
        void onItemClick(Itens item, ItensViewHolder vholder, int position);
        void OnLongPress(Itens item, ItensViewHolder vholder, int position);
    }

    /*@Override
    public int getItemViewType(int position) {
        return position;
    }*/

    @Override
    public int getItemCount() {
        return itens.size();
    }

    public void updateList(ArrayList<Itens> itens){
        this.itens = new ArrayList<Itens>();
        this.itens.addAll(itens);
        notifyDataSetChanged();
    }

    /*public void updateOne(Itens item){
        itens.add(item);
    }*/
}
