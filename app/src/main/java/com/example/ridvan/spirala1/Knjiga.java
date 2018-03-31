package com.example.ridvan.spirala1;

import android.graphics.Bitmap;

import java.io.Serializable;

public class Knjiga implements Serializable {

    String imeAutora, nazivKnjige, kategorija, slika;
    Boolean oznacena;

    public Knjiga(String imeAutora, String nazivKnjige, String kategorija, String slika) {
        this.imeAutora = imeAutora;
        this.nazivKnjige = nazivKnjige;
        this.kategorija = kategorija;
        this.slika = slika;
        this.oznacena=false;
    }

    public Knjiga(String imeAutora, String nazivKnjige, String kategorija, String slika, Boolean oznac) {
        this.imeAutora = imeAutora;
        this.nazivKnjige = nazivKnjige;
        this.kategorija = kategorija;
        this.slika = slika;
        this.oznacena=oznac;
    }

    public String getImeAutora() {
        return imeAutora;
    }

    public String getNazivKnjige() {
        return nazivKnjige;
    }

    public String getKategorija() {
        return kategorija;
    }

    public String getSlika() {
        return slika;
    }

    public Boolean getOznacena() {
        return oznacena;
    }
}
