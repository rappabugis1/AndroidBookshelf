package com.example.ridvan.spirala1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class ListaKnjigaAkt extends AppCompatActivity {

    ArrayList<Knjiga> knjige, temp;
    String kategorija;
    ListView lista;
    TextView lblkat;
    Button nazad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_knjiga_akt);

        nazad=(Button) findViewById(R.id.dPovratak);
        lblkat = (TextView) findViewById(R.id.lblNazivKategorije);
        lista=(ListView) findViewById(R.id.listaKnjiga);

        knjige= new ArrayList<Knjiga> ((ArrayList<Knjiga>) getIntent().getSerializableExtra("knjige"));
        temp=(ArrayList<Knjiga>) getIntent().getSerializableExtra("knjige");

        kategorija = (String) getIntent().getSerializableExtra("kategorija");
        lblkat.setText(kategorija);

        final KnjigaAdapter adapter = new KnjigaAdapter(this, knjige);
        lista.setAdapter(adapter);
        adapter.getFilter().filter(kategorija);

        nazad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("knjige", temp);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Knjiga knjiga= (Knjiga) lista.getItemAtPosition(position);
                temp.set(position, new Knjiga(knjiga.imeAutora,knjiga.nazivKnjige, knjiga.getKategorija(), knjiga.getSlika(), true));
                view.setBackgroundResource(R.color.colorPrimaryDark);
                adapter.notifyDataSetChanged();
            }
        });
    }

}
