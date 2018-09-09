package com.example.ridvan.spirala1;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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

public class SIDFrag extends Fragment implements DohvatiKnjige.IDohvatiKnjigeDone, BookshelveReceiver.Receiver{


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
                try {
                    String query = holder.querytxt.getText().toString();
                    Intent intent = new Intent(Intent.ACTION_SYNC, null, getActivity(), KnjigePoznanika.class);
                    BookshelveReceiver mReceiver = new BookshelveReceiver(new Handler());
                    mReceiver.setReceiver(SIDFrag.this);

                    intent.putExtra("id", query);
                    intent.putExtra("receiver", mReceiver);

                    getActivity().startService(intent);
                } catch(Exception e){
                    if(holder.querytxt.getText().toString().length()!=9){
                        CharSequence text = "ID of a BookShelf needs to be 9 numbers wide!";
                        int duration = Toast.LENGTH_SHORT;

                        Toast toast = Toast.makeText(getContext(), text, duration);
                        toast.show();
                    }

                }
            }

        });

        return view;
    }

    @Override
    public void onDohvatiDone(ArrayList<Knjiga> rez){
        rezKnjige = new ArrayList<>();

        for (int i=0; i < rez.size(); i++)
            rezKnjige.add(rez.get(i));

        try {
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e){}

        TransManager(new KnjigeFragment());
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

    private void TransManager ( Fragment frag){
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        Bundle bundle = new Bundle();
        bundle.putSerializable("knjige",rezKnjige );
        bundle.putSerializable("query", holder.querytxt.getText().toString());

        frag.setArguments(bundle);

        transaction.replace(R.id.fragment_view, frag);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
