package com.example.ridvan.spirala1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ListView;
import java.util.ArrayList;

public class KategorijeAkt extends AppCompatActivity {

    ArrayList<String> kategorije;
    ArrayAdapter<String> adapter;
    Button dPretraga;
    EditText tekstPretraga;
    Button dDodKat;
    Button dDodKnjig ;
    ListView listaKat ;
    ArrayList<Knjiga> knjige;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                knjige = (ArrayList<Knjiga>) data.getSerializableExtra("knjige");
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kategorije_akt);

        listaKat = (ListView) findViewById(R.id.listaKategorija);
        dPretraga = (Button) findViewById(R.id.dPretraga);
        tekstPretraga = (EditText) findViewById(R.id.tekstPretraga);
        dDodKat = (Button) findViewById(R.id.dDodajKategoriju);
        dDodKnjig = (Button) findViewById(R.id.dDodajKnjigu);
        listaKat = (ListView) findViewById(R.id.listaKategorija);

        knjige = new ArrayList<Knjiga>();
        kategorije= new ArrayList<String>();
        kategorije.add("Fantazija"); kategorije.add("Drama"); kategorije.add("Akcija"); kategorije.add("Romantika"); kategorije.add("Komedija");

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, kategorije);
        listaKat.setAdapter(adapter);

        dPretraga.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.getFilter().filter(tekstPretraga.getText().toString(), new Filter.FilterListener() {
                    @Override
                    public void onFilterComplete(int count) {
                        if (adapter.getCount()==0)
                            dDodKat.setEnabled(true);
                        else dDodKat.setEnabled(false);
                    }
                });
            }
        });

        dDodKat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!kategorije.contains(tekstPretraga.getText().toString())) {
                    kategorije.add(0, tekstPretraga.getText().toString());
                    adapter.clear();
                    adapter.addAll(kategorije);
                    adapter.getFilter().filter(tekstPretraga.getText().toString());
                }
            }
        });

        dDodKnjig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), DodavanjeKnjigeAkt.class);
                i.putExtra("kategorije", kategorije);
                i.putExtra("knjige", knjige);
                startActivityForResult(i,1);
            }
        });

        listaKat.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getApplicationContext(), ListaKnjigaAkt.class);
                i.putExtra("kategorija", listaKat.getItemAtPosition(position).toString());
                i.putExtra("knjige", knjige);
                startActivityForResult(i,1);
            }
        });
    }


}
