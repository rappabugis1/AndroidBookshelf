package com.example.ridvan.spirala1;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.GradientDrawable;
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

public class KategorijeAkt extends AppCompatActivity {


    ArrayList<String> autori;
    ArrayList<Knjiga> knjige;
    ArrayList<String> kategorije;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_layout);

        Configuration config = getResources().getConfiguration();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        knjige = new ArrayList<Knjiga>();
        kategorije= new ArrayList<String>();
        autori = new ArrayList<String>();

        kategorije.add("Fantazija"); kategorije.add("Drama"); kategorije.add("Akcija"); kategorije.add("Romantika"); kategorije.add("Komedija");
        autori.add("Ridvan");

        ListeFragment fr_poc = new ListeFragment();

        Bundle bundle = new Bundle();
        bundle.putStringArrayList("kat", kategorije);
        bundle.putStringArrayList("aut", autori);
        bundle.putSerializable("knjig", knjige);

        fr_poc.setArguments(bundle);
        fragmentTransaction.replace(R.id.fragment_container, fr_poc);
        fragmentTransaction.commit();

    }


}
