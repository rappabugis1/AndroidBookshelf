package com.example.ridvan.spirala1;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;

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

import static android.app.DownloadManager.STATUS_FAILED;
import static android.app.DownloadManager.STATUS_RUNNING;
import static android.app.DownloadManager.STATUS_SUCCESSFUL;
import static android.drm.DrmInfoStatus.STATUS_ERROR;


public class KnjigePoznanika extends IntentService {


    public KnjigePoznanika() {
        super("DajKnjige");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public static final int STATUS_START =0;
    public static final int STATUS_FINISH=1;
    public static final int STATUS_ERROR=2;

    ArrayList<Knjiga> listaKnjiga;

    @Override
    protected void onHandleIntent(Intent intent) {

        final ResultReceiver receiver = intent.getParcelableExtra("receiver");

        String idKorisnika = intent.getStringExtra("id");

        Bundle bundle = new Bundle();
        receiver.send(STATUS_RUNNING, Bundle.EMPTY);

        listaKnjiga = new ArrayList<>();
        String query = null;
        try {
            query = URLEncoder.encode(idKorisnika, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


        String url1 = "https://www.googleapis.com/books/v1/users/" + query + "/bookshelves";

        try {
            URL urlUp = new URL(url1);
            HttpsURLConnection urlConnectionUp = (HttpsURLConnection) urlUp.openConnection();

            if (urlConnectionUp.getResponseCode() == HttpsURLConnection.HTTP_OK) {
                InputStream inUp = new BufferedInputStream(urlConnectionUp.getInputStream());

                String rezultatUp = convertStreamToString(inUp);

                JSONObject joUp = new JSONObject(rezultatUp);

                JSONArray itemsUp = joUp.getJSONArray("items");

                for (int k = 0; k < itemsUp.length(); k++) {
                    JSONObject okviri = itemsUp.getJSONObject(k);
                    String url2 = "https://www.googleapis.com/books/v1/users/" + query + "/bookshelves/" + okviri.getString("id") + "/volumes";

                    try {

                        URL url = new URL(url2);
                        HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();

                        if (urlConnection.getResponseCode() == HttpsURLConnection.HTTP_OK) {
                            InputStream inn = new BufferedInputStream(urlConnection.getInputStream());
                            String rezultatt = convertStreamToString(inn);
                            JSONObject jo=new JSONObject(rezultatt);
                            JSONArray items = jo.getJSONArray("items");

                            String id = "", naziv = "", opis = "", datumObjavljivanja = "";

                            for (int iter = 0; iter < items.length(); iter++) {
                                URL slika = null;
                                int brStranica = 0;
                                ArrayList<Autor> autori = new ArrayList<>();

                                JSONObject book = items.getJSONObject(iter);
                                if (book.has("id")) {
                                    id = book.getString("id");

                                    JSONObject info = book.getJSONObject("volumeInfo");

                                    if (info.has("title")) naziv = info.getString("title");
                                    if (info.has("publishedDate"))
                                        datumObjavljivanja = info.getString("publishedDate");
                                    if (info.has("pageCount"))
                                        brStranica = info.getInt("pageCount");
                                    if (info.has("description"))
                                        opis = info.getString("description");

                                    JSONArray bookAuthors = new JSONArray();
                                    if (info.has("authors"))
                                        bookAuthors = info.getJSONArray("authors");

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

                                    if (info.has("imageLinks")) {
                                        JSONObject slike = info.getJSONObject("imageLinks");
                                        slika = new URL(slike.getString("thumbnail"));
                                    }

                                }
                                listaKnjiga.add(new Knjiga(id, naziv, autori, opis, datumObjavljivanja, slika, brStranica));
                                bundle.putSerializable("lista", listaKnjiga);
                                receiver.send(STATUS_FINISH, bundle);
                            }

                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            receiver.send(STATUS_ERROR, bundle);

        } catch (JSONException e) {
            e.printStackTrace();
            receiver.send(STATUS_ERROR, bundle);
        }
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