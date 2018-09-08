package com.example.ridvan.spirala1;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class KnjigeFragment extends Fragment {

    ArrayList<Knjiga> knjige;
    String kategorija, autor;
    ListView lista;
    TextView lblkat;
    Button nazad;
    Boolean opc;

    BazaOpenHelper db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = new BazaOpenHelper(getActivity());

        Bundle bundle= this.getArguments();

        if (bundle != null) {
            knjige = new ArrayList<Knjiga>();
            if(bundle.getBoolean("opc"))
                knjige=db.knjigeAutora(db.dajIdAutoraPoImenu(bundle.getString("autor")));
            else
                knjige=db.knjigeKategorije(db.dajIdKatPoImenu(bundle.getString("kat")));
        }
    }

    public interface BtnClickListener {
        public abstract void onBtnClick(int position);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.lista_knjiga_akt, container, false);

        nazad= view.findViewById(R.id.dPovratak);
        lblkat = view.findViewById(R.id.lblNazivKategorije);
        lista= view.findViewById(R.id.listaKnjiga);

        lblkat.setText(kategorija);

        KnjigaAdapter adapter = new KnjigaAdapter(getContext(), knjige, opc, new BtnClickListener() {
            @Override
            public void onBtnClick(int position) {
                FragmentPreporuci frag = new FragmentPreporuci();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();

                Bundle bundle = new Bundle();
                bundle.putSerializable("knjiga",(Knjiga) lista.getItemAtPosition(position) );

                frag.setArguments(bundle);

                transaction.replace(R.id.fragment_view, frag);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        lista.setAdapter(adapter);

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
                db.oznaciKnjigu(db.dajIdKnjigePoIdServisu(knjiga.id));
                view.setBackgroundResource(R.color.colorLightBlue);
            }
        });

        return view;
    }



}
