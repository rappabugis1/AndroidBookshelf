package com.example.ridvan.spirala1;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class KnjigeFragment extends Fragment {

    ArrayList<Knjiga> knjige, temp;
    String kategorija, autor;
    ListView lista;
    TextView lblkat;
    Button nazad;
    Boolean opc;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle= this.getArguments();

        if (bundle != null) {
            temp = (ArrayList<Knjiga>) bundle.getSerializable("knjig");
            knjige = new ArrayList<Knjiga>(temp);

            if(bundle.getBoolean("opc")){
                opc=bundle.getBoolean("opc");
                autor=bundle.getString("autor");
            }
            else {
                opc=bundle.getBoolean("opc");
                kategorija=bundle.getString("kat");
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.lista_knjiga_akt, container, false);

        nazad= view.findViewById(R.id.dPovratak);
        lblkat = view.findViewById(R.id.lblNazivKategorije);
        lista= view.findViewById(R.id.listaKnjiga);

        lblkat.setText(kategorija);

        KnjigaAdapter adapter = new KnjigaAdapter(getContext(), knjige, opc);
        lista.setAdapter(adapter);

        if(opc)
            adapter.getFilter().filter(autor);
        else
            adapter.getFilter().filter(kategorija);

        nazad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStackImmediate();
            }
        });

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Knjiga knjiga= (Knjiga) lista.getItemAtPosition(position);
                temp.set(position, new Knjiga(knjiga, true));
                lista.getChildAt(position).setBackgroundResource(R.color.colorLightBlue);
            }
        });

        return view;
    }



}
