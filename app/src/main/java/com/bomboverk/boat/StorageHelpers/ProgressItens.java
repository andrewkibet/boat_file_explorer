package com.bomboverk.boat.StorageHelpers;

import android.util.SparseBooleanArray;
import com.bomboverk.boat.ItensAdapter.Itens;
import java.util.ArrayList;

public class ProgressItens {

    private int type = 0;
    private String zipName;
    private SparseBooleanArray selectedItens;
    private ArrayList<Itens> item;

    public ProgressItens(SparseBooleanArray selectedItens, ArrayList<Itens> item) {
        this.selectedItens = selectedItens;
        this.item = item;
    }

    public SparseBooleanArray getSelectedItens() {
        return selectedItens;
    }

    public ArrayList<Itens> getItem() {
        return item;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getZipName() {
        return zipName;
    }

    public void setZipName(String zipName) {
        this.zipName = zipName;
    }
}
