package com.example.ridvan.spirala1;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ListView;

import java.util.ArrayList;


public class ListeFragment extends Fragment {

    EditText tekstPretraga;
    Button dDodKat, dDodKnjig, dAutori, dKat, dPretraga;
    ListView listaKat ;
    Boolean opcija;
    ArrayAdapter<String> adapter;

    ArrayList<String> kategorije, autori;
    ArrayList<Knjiga> knjige;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle= this.getArguments();


        if (bundle != null) {
            kategorije = bundle.getStringArrayList("kat");
            autori = bundle.getStringArrayList("aut");
            knjige = (ArrayList<Knjiga>) bundle.getSerializable("knjig");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.kategorije_akt, container, false);

        tekstPretraga =  view.findViewById(R.id.tekstPretraga);
        listaKat = view.findViewById(R.id.listaKategorija);

        opcija=false;

        dKat= view.findViewById(R.id.dKategorije);
        dAutori = view.findViewById(R.id.dAutori);
        dPretraga = view.findViewById(R.id.dPretraga);
        dDodKat = view.findViewById(R.id.dDodajKategoriju);
        dDodKnjig = view.findViewById(R.id.dDodajKnjigu);

        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, kategorije);
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

        dKat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                opcija=false;
                dDodKat.setVisibility(View.VISIBLE);
                dPretraga.setVisibility(View.VISIBLE);
                tekstPretraga.setVisibility(View.VISIBLE);
                adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, kategorije);
                listaKat.setAdapter(adapter);
            }
        });
        dAutori.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                opcija=true;
                dDodKat.setVisibility(View.GONE);
                dPretraga.setVisibility(View.GONE);
                tekstPretraga.setVisibility(View.GONE);

                for (Knjiga knj : knjige) {
                    if(!autori.contains(knj.getImeAutora()))
                        autori.add(knj.getImeAutora());
                }

                adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, autori);
                listaKat.setAdapter(adapter);
            }
        });

        dDodKnjig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create new fragment and transaction
                DodavanjeKnjigeFragment frag = new DodavanjeKnjigeFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();

                Bundle bundle = new Bundle();
                bundle.putStringArrayList("kat", kategorije);
                bundle.putStringArrayList("aut", autori);
                bundle.putSerializable("knjig", knjige);

                frag.setArguments(bundle);

                transaction.replace(R.id.fragment_container, frag);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        listaKat.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                KnjigeFragment frag = new KnjigeFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                Bundle bundle = new Bundle();

                if(opcija){
                    bundle.putBoolean("opc", true);
                    bundle.putString("autor", listaKat.getItemAtPosition(position).toString());
                }
                else {
                    bundle.putBoolean("opc", false);
                    bundle.putString("kat", listaKat.getItemAtPosition(position).toString());
                }

                bundle.putSerializable("knjig", knjige);
                frag.setArguments(bundle);

                transaction.replace(R.id.fragment_container, frag);
                transaction.addToBackStack(null);
                transaction.commit();

            }
        });

        return view;
    }

}
