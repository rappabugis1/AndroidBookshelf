package com.example.ridvan.spirala1;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;


public class ListeFragment extends Fragment {


    ListView lista;
    ArrayAdapter<String> adapter;
    AutorAdapter adapter1;
    BazaOpenHelper db;
    boolean opc;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle= this.getArguments();
        if(bundle!=null)
            opc= bundle.getBoolean("opc");

        if(!opc)
            setHasOptionsMenu(true);
        db= new BazaOpenHelper(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.kategorije_akt, container, false);

        lista = view.findViewById(R.id.listaKategorija);

        if(opc){
            adapter1 = new AutorAdapter(getActivity(), db.dajAutore());
            lista.setAdapter(adapter1);}
        else {
            adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, db.dajImenaKategorija());
            lista.setAdapter(adapter);
        }


        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                KnjigeFragment frag = new KnjigeFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                Bundle bundle = new Bundle();

                if(opc){
                    Autor autorTemp=(Autor) parent.getAdapter().getItem(position);
                    bundle.putSerializable("knjige", db.knjigeAutora(db.dajIdAutoraPoImenu(autorTemp.getImeiPrezime())));
                    bundle.putString("query", autorTemp.getImeiPrezime());
                    getActivity().setTitle(autorTemp.getImeiPrezime());
                }
                else {
                    bundle.putSerializable("knjige", db.knjigeKategorije(db.dajIdKatPoImenu(lista.getItemAtPosition(position).toString())));
                    bundle.putString("query", lista.getItemAtPosition(position).toString());
                    getActivity().setTitle(lista.getItemAtPosition(position).toString());
                }

                frag.setArguments(bundle);
                transaction.replace(R.id.fragment_view, frag);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if(opc)
            getActivity().setTitle(R.string.Autori);
        else
            getActivity().setTitle(R.string.Kategorije);
    }

    @Override
    public void onCreateOptionsMenu(
            Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.cat_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_cat_item:

                final BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(getActivity());
                View sheetView = getActivity().getLayoutInflater().inflate(R.layout.dodaj_kategoriju, null);
                mBottomSheetDialog.setContentView(sheetView);
                mBottomSheetDialog.show();

                Button dodaj=sheetView.findViewById(R.id.unesiKat);
                final EditText ime= sheetView.findViewById(R.id.textKategorije);

                dodaj.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(db.dodajKategoriju(ime.getText().toString())<0)
                            dajTost("Kategorija vec postoji!");
                        else {
                            dajTost("Kategorija dodana!");
                            adapter.add(ime.getText().toString());
                        }

                        mBottomSheetDialog.dismiss();
                    }
                });

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void dajTost(String text){
        Context context = getActivity();
        int duration = Toast.LENGTH_LONG;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
}
