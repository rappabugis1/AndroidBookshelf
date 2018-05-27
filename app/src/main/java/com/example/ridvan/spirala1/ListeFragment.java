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
import java.util.Arrays;


public class ListeFragment extends Fragment {

    EditText tekstPretraga;
    Button dDodKat, dDodKnjig, dAutori, dKat, dPretraga, dDodajOnline;
    ListView listaKat ;
    Boolean opcija;
    ArrayAdapter<String> adapter;

    ArrayList<String> kategorije;
    ArrayList<Autor> autori;
    ArrayList<Knjiga> knjige;

    BazaOpenHelper db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle= this.getArguments();

        db= new BazaOpenHelper(getActivity());
        kategorije=db.dajImenaKategorija();


        if (bundle != null) {
            autori = (ArrayList<Autor>) bundle.getSerializable("aut");
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
        dDodajOnline = view.findViewById(R.id.dDodajOnline);

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
                if(db.dodajKategoriju(tekstPretraga.getText().toString())!=-1) {
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
                dDodajOnline.setVisibility(View.VISIBLE);
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
                dDodajOnline.setVisibility(View.GONE);

                AutorAdapter adapter1 = new AutorAdapter(getActivity(), autori);
                listaKat.setAdapter(adapter1);
            }
        });

        dDodKnjig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DodavanjeKnjigeFragment frag = new DodavanjeKnjigeFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();

                Bundle bundle = new Bundle();
                bundle.putStringArrayList("kat", kategorije);
                bundle.putSerializable("aut", autori);
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
                    Autor autorTemp=(Autor) parent.getAdapter().getItem(position);
                    bundle.putString("autor", autorTemp.getImeiPrezime());
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

        dDodajOnline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentOnline frag = new FragmentOnline();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();

                Bundle bundle = new Bundle();
                bundle.putStringArrayList("kat", kategorije);
                bundle.putSerializable("aut", autori);
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
