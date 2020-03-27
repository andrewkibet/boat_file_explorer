package com.bomboverk.boat.ItensAdapter;

import androidx.documentfile.provider.DocumentFile;

public class Itens {

    String nome, url, extension, tamanho;
    int itensCount;
    DocumentFile doc;

    public Itens(String nome, String url, String extension, String tamanho, int itensCount, DocumentFile doc) {
        this.nome = nome;
        this.url = url;
        this.extension = extension;
        this.tamanho = tamanho;
        this.itensCount = itensCount;
        this.doc = doc;
    }

    public DocumentFile getDoc() {
        return doc;
    }

    public String getNome() {
        return nome;
    }

    public String getUrl() {
        return url;
    }

    public String getExtension() {
        return extension;
    }

    public String getTamanho() {
        return tamanho;
    }

    public int getItensCount() {
        return itensCount;
    }
}
