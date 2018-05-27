package com.example.ridvan.spirala1;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.FrameLayout;
import android.widget.ListView;
import java.util.ArrayList;

public class KategorijeAkt extends AppCompatActivity{


    ArrayList<Autor> autori;
    ArrayList<Knjiga> knjige;
    //ArrayList<String> kategorije;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_layout);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS},100);
        }

        BazaOpenHelper db = new BazaOpenHelper(getApplicationContext());

        Configuration config = getResources().getConfiguration();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();


        db.dodajKategoriju("Fantazija");
        db.dodajKategoriju("Drama");
        db.dodajKategoriju("Akcija");
        db.dodajKategoriju("Romantika");

        //onsite temp
        knjige = new ArrayList<Knjiga>();
        //kategorije= new ArrayList<String>();
        autori = new ArrayList<Autor>();

        //kategorije.add("Fantazija"); kategorije.add("Drama"); kategorije.add("Akcija"); kategorije.add("Romantika"); kategorije.add("Komedija");

        ListeFragment fr_poc = new ListeFragment();

        Bundle bundle = new Bundle();
        //bundle.putStringArrayList("kat", kategorije);
        bundle.putSerializable("aut", autori);
        bundle.putSerializable("knjig", knjige);

        fr_poc.setArguments(bundle);
        fragmentTransaction.replace(R.id.fragment_container, fr_poc);
        fragmentTransaction.commit();

    }

}
