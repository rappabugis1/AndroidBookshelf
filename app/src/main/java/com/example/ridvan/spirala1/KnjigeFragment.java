package com.example.ridvan.spirala1;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import java.util.ArrayList;

public class KnjigeFragment extends Fragment {

    ArrayList<Knjiga> knjige;
    String query;
    GridView grid;
    TextView lbl;
    Boolean opc;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


       Bundle bundle= this.getArguments();
       if(bundle!=null){
           knjige = (ArrayList<Knjiga>) bundle.getSerializable("knjige");
           query = bundle.getString("query");
       }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.lista_knjiga, container, false);

        lbl = view.findViewById(R.id.labelHeadListaK);
        grid= view.findViewById(R.id.gridKnjiga);

        lbl.setText("Search results for :"+ query);



        SKnjigeAdapter adapterS = new SKnjigeAdapter(getContext(), knjige);
        grid.setAdapter(adapterS);

        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TransManager(new FragmentKnjigaSaApi(), position);
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
