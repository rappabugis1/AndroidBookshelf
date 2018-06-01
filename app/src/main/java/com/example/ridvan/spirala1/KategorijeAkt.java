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

import com.facebook.stetho.Stetho;

import java.util.ArrayList;

public class KategorijeAkt extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Stetho.initializeWithDefaults(this);
        setContentView(R.layout.base_layout);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS},100);
        }

        BazaOpenHelper db = new BazaOpenHelper(getApplicationContext());

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        db.dodajKategoriju("Fantazija");
        db.dodajKategoriju("Drama");
        db.dodajKategoriju("Akcija");
        db.dodajKategoriju("Romantika");

        ListeFragment fr_poc = new ListeFragment();

        fragmentTransaction.replace(R.id.fragment_container, fr_poc);
        fragmentTransaction.commit();
    }

}
