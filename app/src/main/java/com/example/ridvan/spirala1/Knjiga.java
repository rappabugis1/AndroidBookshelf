package com.example.ridvan.spirala1;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;

public class Knjiga implements Serializable {

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    public ArrayList<Autor> getAutori() {
        return autori;
    }

    public void setAutori(ArrayList<Autor> autori) {
        this.autori = autori;
    }

    public String getOpis() {
        return opis;
    }

    public void setOpis(String opis) {
        this.opis = opis;
    }

    public String getDatumObjavljivanja() {
        return datumObjavljivanja;
    }

    public void setDatumObjavljivanja(String datumObjavljivanja) {
        this.datumObjavljivanja = datumObjavljivanja;
    }

    public URL getSlika() {
        return slika;
    }

    public void setSlika(URL slika) {
        this.slika = slika;
    }

    public int getBrojStranica() {
        return brojStranica;
    }

    public void setBrojStranica(int brojStranica) {
        this.brojStranica = brojStranica;
    }

    public String getKategorija() {
        return kategorija;
    }

    public void setKategorija(String kategorija) {
        this.kategorija = kategorija;
    }

    public Boolean getOznacena() {
        return oznacena;
    }

    public void setOznacena(Boolean oznacena) {
        this.oznacena = oznacena;
    }

    public String getsSlika() {
        return sSlika;
    }

    public void setsSlika(String sSlika) {
        this.sSlika = sSlika;
    }

    String id, naziv;
    ArrayList<Autor> autori;
    String opis, datumObjavljivanja;
    URL slika;
    String sSlika;
    int brojStranica;
    String kategorija;
    Boolean oznacena;
    byte[] thumb= new byte[]{};

    public Bitmap getThumb() {
        return BitmapFactory.decodeByteArray(thumb, 0, thumb.length);
    }

    public void setThumb(byte[] thumb) {
        this.thumb = thumb;
    }

    public Knjiga (){

    }

    public Knjiga(String id, String naziv, ArrayList<Autor> autori, String opis, String datumObjavljivanja, URL slika, int brojStranica) {
        this.id = id;
        this.naziv = naziv;
        this.autori = autori;
        this.opis = opis;
        this.datumObjavljivanja = datumObjavljivanja;
        this.slika = slika;
        this.brojStranica = brojStranica;
        this.oznacena= false;
    }

    public Knjiga(String id, String naziv, ArrayList<Autor> autori, String opis, String datumObjavljivanja, String slika, int brojStranica) {
        this.id = id;
        this.naziv = naziv;
        this.autori = autori;
        this.opis = opis;
        this.datumObjavljivanja = datumObjavljivanja;
        this.sSlika = slika;
        this.brojStranica = brojStranica;
        this.oznacena= false;
    }


    public Knjiga (Knjiga temp, Boolean ozn){
        if(temp.getId()!=null)
            this.setId(temp.getId());
        if(temp.getNaziv()!=null)
            this.setNaziv(temp.getNaziv());
        if(temp.getAutori()!=null)
            this.setAutori(temp.getAutori());
        if(temp.getOpis()!=null)
            this.setOpis(temp.getOpis());
        if(temp.getDatumObjavljivanja()!=null)
            this.setDatumObjavljivanja(temp.getDatumObjavljivanja());
        if(temp.getSlika()!=null)
            this.setSlika(temp.getSlika());
        if(temp.getsSlika()!=null)
            this.setsSlika(temp.getsSlika());
        if(temp.getBrojStranica()!=0) {
            this.setBrojStranica(temp.getBrojStranica());
        } else {this.setBrojStranica(0);}
        if(temp.getKategorija()!=null)
            this.setKategorija(temp.getKategorija());
        this.setOznacena(ozn);
    }

    public Knjiga(String imeAutora, String nazivKnjige, String kategorija, String slika) {
        autori=new ArrayList<>();
        this.autori.add(new Autor(imeAutora));
        this.naziv = nazivKnjige;
        this.kategorija = kategorija;
        this.sSlika = slika;
        this.slika=null;
        this.oznacena=false;
        this.opis = "";
        this.datumObjavljivanja = "";
        this.brojStranica = 0;
        this.id=imeAutora+nazivKnjige;
    }

}
