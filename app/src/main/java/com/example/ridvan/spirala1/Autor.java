package com.example.ridvan.spirala1;

import java.io.Serializable;
import java.util.ArrayList;

public class Autor implements Serializable{
    public Autor(String imeiPrezime) {
        this.imeiPrezime = imeiPrezime;
        knjige=new ArrayList<>();
    }

    private String imeiPrezime;
    private ArrayList<String> knjige;

    public String getImeiPrezime() {
        return imeiPrezime;
    }

    public Autor(String imeiPrezime, ArrayList<String> knjige) {
        this.imeiPrezime = imeiPrezime;
        this.knjige = knjige;
    }

    public Autor(String imeiPrezime, String knjiga) {
        this.imeiPrezime = imeiPrezime;
        if(knjige==null)
            knjige=new ArrayList<>();
        knjige.add(knjiga);
    }

    public void setImeiPrezime(String imeiPrezime) {
        this.imeiPrezime = imeiPrezime;
    }

    public ArrayList<String> getKnjige() {
        return knjige;
    }

    public void setKnjige(ArrayList<String> knjige) {
        this.knjige = knjige;
    }

    public void dodajKnjigu(String id){
        this.knjige.add(id);
    }
}
