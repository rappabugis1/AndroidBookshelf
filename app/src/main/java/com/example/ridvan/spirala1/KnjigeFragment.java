package com.example.ridvan.spirala1;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class KnjigeFragment extends Fragment {

    ArrayList<Knjiga> knjige;
    String query;
    GridView grid;
    TextView lbl;
    Boolean opc;

    //BazaOpenHelper db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       /* db = new BazaOpenHelper(getActivity());

        Bundle bundle= this.getArguments();

        if (bundle != null) {
            knjige = new ArrayList<Knjiga>();
            if(bundle.getBoolean("opc"))
                knjige=db.knjigeAutora(db.dajIdAutoraPoImenu(bundle.getString("autor")));
            else
                knjige=db.knjigeKategorije(db.dajIdKatPoImenu(bundle.getString("kat")));
        }
        */

       Bundle bundle= this.getArguments();
       if(bundle!=null){
           knjige = (ArrayList<Knjiga>) bundle.getSerializable("knjige");
           query = bundle.getString("query");
       }
    }

    public interface BtnClickListener {
        public abstract void onBtnClick(int position);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.lista_knjiga, container, false);

        lbl = view.findViewById(R.id.labelHeadListaK);
        grid= view.findViewById(R.id.gridKnjiga);

        lbl.setText("Search results for :"+ query);

        /*KnjigaAdapter adapter = new KnjigaAdapter(getContext(), knjige, opc, new BtnClickListener() {
            @Override
            public void onBtnClick(int position) {
                FragmentPreporuci frag = new FragmentPreporuci();

            }
        });
        */

        SKnjigeAdapter adapterS = new SKnjigeAdapter(getContext(), knjige);
        grid.setAdapter(adapterS);

        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /*
                Knjiga knjiga= (Knjiga) lista.getItemAtPosition(position);
                db.oznaciKnjigu(db.dajIdKnjigePoIdServisu(knjiga.id));
                view.setBackgroundResource(R.color.colorLightBlue);
                */
                TransManager(new FragmentPreporuci(), position);
            }
        });

        return view;
    }

    private void TransManager ( Fragment frag, int pos ){
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        Bundle bundle = new Bundle();
        bundle.putSerializable("knjiga",(Knjiga) knjige.get(pos));

        frag.setArguments(bundle);

        transaction.replace(R.id.fragment_view, frag);
        transaction.addToBackStack(null);
        transaction.commit();
    }



}
