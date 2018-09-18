package com.example.ridvan.spirala1;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class SAutorFrag extends Fragment implements DohvatiNajnovije.IDohvatiNajnovijeDone{


    ArrayList<Knjiga> rezKnjige;

    @Override
    public void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
    }

    private static class ViewHolder {
        EditText querytxt;
        Button search;
    }

    ViewHolder holder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.home_layout_sauthor, container, false);

        holder = new ViewHolder();

        holder.querytxt = view.findViewById(R.id.sBook);
        holder.search=view.findViewById(R.id.button);

        holder.search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = holder.querytxt.getText().toString();
                new DohvatiNajnovije(SAutorFrag.this).execute(query);

            }
        });

        return view;
    }

    @Override
    public void onNajnovijeDone(ArrayList<Knjiga> rez){
        rezKnjige = new ArrayList<>();

        for (int i=0; i < rez.size(); i++)
            rezKnjige.add(rez.get(i));

        try {
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e){}

        if(rezKnjige.size()>0)
            TransManager(new KnjigeFragment());
        else dajTost("No books found");
    }

    private void TransManager ( Fragment frag){
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        Bundle bundle = new Bundle();
        bundle.putSerializable("knjige",rezKnjige );
        bundle.putSerializable("query", holder.querytxt.getText().toString());

        frag.setArguments(bundle);

        getActivity().setTitle(R.string.Pretraga);

        transaction.replace(R.id.fragment_view, frag);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void dajTost(String text){
        Context context = getActivity();
        int duration = Toast.LENGTH_LONG;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
}
