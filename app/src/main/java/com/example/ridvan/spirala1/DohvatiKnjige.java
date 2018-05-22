package com.example.ridvan.spirala1;

import android.os.AsyncTask;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class DohvatiKnjige extends AsyncTask<String, Integer, Void> {


    public interface  IDohvatiKnjigeDone {
        void onDohvatiDone(ArrayList<Knjiga> filtriraneKnjige);
    }

    ArrayList<Knjiga> filtriraneKnjige;
    IDohvatiKnjigeDone sender;

    public DohvatiKnjige(IDohvatiKnjigeDone org_send ) {
        sender= org_send;
    }

    @Override
    protected Void doInBackground(String... params) {
        filtriraneKnjige= new ArrayList<Knjiga>();

        for(int i=0 ; i<params.length; i++){
            String query= null;
            try{
                query = URLEncoder.encode(params[i], "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            String url1= "https://www.googleapis.com/books/v1/volumes?q=intitle:" + query + "&maxResults=5";

            try{
                URL url = new URL(url1);
                HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();

                if (urlConnection.getResponseCode() == HttpsURLConnection.HTTP_OK){
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                    String rezultat = convertStreamToString(in);

                    JSONObject jo=new JSONObject(rezultat);
                    JSONArray items = jo.getJSONArray("items");
                    String id ="",naziv = "", opis = "", datumObjavljivanja = "";

                    for(int iter=0; iter<items.length(); iter++){
                        URL slika= null;
                        int brStranica=0;
                        ArrayList<Autor> autori = new ArrayList<>();

                        JSONObject book = items.getJSONObject(iter);
                        if(book.has("id")){
                            id=book.getString("id");

                            JSONObject info= book.getJSONObject("volumeInfo");

                            if(info.has("title")) naziv=info.getString("title");
                            if(info.has("publishedDate")) datumObjavljivanja=info.getString("publishedDate");
                            if(info.has("pageCount")) brStranica=info.getInt("pageCount");
                            if(info.has("description")) opis=info.getString("description");

                            JSONArray bookAuthors = new JSONArray();
                            if (info.has("authors")) bookAuthors = info.getJSONArray("authors");

                            for (int j = 0; j < bookAuthors.length(); j++) {
                                Autor a = new Autor(bookAuthors.getString(j));
                                Boolean nijePronadjen = true;
                                for (Autor p : autori) {
                                    if (p.getImeiPrezime().toLowerCase().equals(a.getImeiPrezime())) {
                                        nijePronadjen = false;
                                        p.dodajKnjigu(id);
                                    }
                                }
                                if (nijePronadjen) {
                                    a.dodajKnjigu(id);
                                    autori.add(a);
                                }
                            }

                            if(info.has("imageLinks")){
                                JSONObject slike = info.getJSONObject("imageLinks");
                                slika= new URL( slike.getString("thumbnail"));
                            }

                        }
                        filtriraneKnjige.add(new Knjiga(id, naziv, autori, opis, datumObjavljivanja, slika, brStranica));
                    }

                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public void onPostExecute(Void aVoid){
        super.onPostExecute(aVoid);
        sender.onDohvatiDone(filtriraneKnjige);
    }


    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
        } finally {
            try {
                is.close();
            } catch (IOException e) {
            }
        }
        return sb.toString();
    }
}
