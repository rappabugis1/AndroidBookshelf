package com.example.ridvan.spirala1;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.transform.Result;


public class FragmentOnline extends Fragment implements DohvatiKnjige.IDohvatiKnjigeDone, DohvatiNajnovije.IDohvatiNajnovijeDone, BookshelveReceiver.Receiver  {

    ArrayList<String> kategorije;
    ArrayList<Autor> autori;
    ArrayList<Knjiga> knjige, rezKnjige;

    Spinner sRez, sKat;
    String kategorija;
    TextView upit;
    Button dAdd, dPovratak, dRun;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle= this.getArguments();

        if (bundle != null) {
            kategorije = bundle.getStringArrayList("kat");
            autori = (ArrayList<Autor>) bundle.getSerializable("aut");
            knjige = (ArrayList<Knjiga>) bundle.getSerializable("knjig");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_online, container, false);

        dAdd= view.findViewById(R.id.dAdd);
        dRun= view.findViewById(R.id.dRun);
        dPovratak= view.findViewById(R.id.dPovratak);
        sRez = view.findViewById(R.id.sRezultat);
        sKat= view.findViewById(R.id.sKategorije);
        upit= view.findViewById(R.id.tekstUpit);

        ArrayAdapter<String> sadapter ;
        sadapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, kategorije);
        sKat.setAdapter(sadapter);

        dRun.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String query = upit.getText().toString();
                if(!query.startsWith("autor:") && !query.startsWith("korisnik:")&& !(query.contains(";")) && !(query.contains(" ")))
                    new DohvatiKnjige((DohvatiKnjige.IDohvatiKnjigeDone)FragmentOnline.this).execute(query);

                else if(query.contains(";")) {
                    new DohvatiKnjige((DohvatiKnjige.IDohvatiKnjigeDone)FragmentOnline.this).execute(query.split(";"));
                }
                else if(query.startsWith("autor:")) {
                    new DohvatiNajnovije((DohvatiNajnovije.IDohvatiNajnovijeDone)FragmentOnline.this).execute(query.substring(6));
                }
                else if(query.startsWith("korisnik:")) {
                    Intent intent = new Intent(Intent.ACTION_SYNC, null, getActivity(), KnjigePoznanika.class);
                    BookshelveReceiver mReceiver= new BookshelveReceiver(new Handler());
                    mReceiver.setReceiver(FragmentOnline.this);

                    intent.putExtra("id", query.substring(9));
                    intent.putExtra("receiver", mReceiver);

                    getActivity().startService(intent);

                }
            }
        });

        sRez.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                dAdd.setEnabled(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                dAdd.setEnabled(false);
            }
        });

        dAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Knjiga knjig= rezKnjige.get(sRez.getSelectedItemPosition());
                knjig.setKategorija((String)sKat.getSelectedItem());

                if(!knjige.contains(knjig)) {
                    knjige.add(knjig);
                    for(int a=0; a<knjig.getAutori().size(); a++){
                        boolean pronadjen=false;

                        for(int i=0; i<autori.size(); i++){
                            if(autori.get(i).getImeiPrezime().equals(knjig.getAutori().get(a).getImeiPrezime())){
                                autori.get(i).dodajKnjigu(knjig.getId());
                                pronadjen=true;
                            }
                        }
                        if(!pronadjen)
                            autori.add(new Autor(knjig.getAutori().get(a).getImeiPrezime(), knjig.getId()));
                    }
                }

                getFragmentManager().popBackStackImmediate();

            }
        });

        dPovratak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStackImmediate();
            }
        });

        return view;
    }

    @Override
    public void onNajnovijeDone(ArrayList<Knjiga> rez) {
        ArrayList<String> naziviKnjiga = new ArrayList<>();
        rezKnjige = new ArrayList<>();

        for (int i=0; i < rez.size(); i++) {
            naziviKnjiga.add(rez.get(i).getNaziv());
            rezKnjige.add(rez.get(i));
        }

        ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item, naziviKnjiga);

        sRez.setAdapter(adapter);
    }

    @Override
    public void onDohvatiDone(ArrayList<Knjiga> rez) {
        ArrayList<String> naziviKnjiga = new ArrayList<>();
        rezKnjige = new ArrayList<>();

        for (int i=0; i < rez.size(); i++) {
            naziviKnjiga.add(rez.get(i).getNaziv());
            rezKnjige.add(rez.get(i));
        }
        ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item, naziviKnjiga);
        sRez.setAdapter(adapter);
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        switch(resultCode){
            case KnjigePoznanika.STATUS_START:

                break;
            case KnjigePoznanika.STATUS_FINISH:
                onDohvatiDone((ArrayList<Knjiga>) resultData.getSerializable("lista"));
                break;
            case KnjigePoznanika.STATUS_ERROR:

                break;
        }
    }
}
