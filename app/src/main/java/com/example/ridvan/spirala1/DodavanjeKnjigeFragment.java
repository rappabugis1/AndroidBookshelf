package com.example.ridvan.spirala1;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;


public class DodavanjeKnjigeFragment extends Fragment {

    ArrayList<String> kategorije;
    ArrayList<Autor> autori;
    ArrayList<Knjiga> knjige;


    Button dNadjiSliku,dUpisiKnjigu, dPonisti;
    EditText imeAutora, nazivKnjige;
    Spinner katKnjige;
    ImageView naslovna;
    static final int img_get =1;
    Boolean slika_promjenjena;
    Intent data;

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

        View view = inflater.inflate(R.layout.dodavanje_knjige_akt, container, false);

        slika_promjenjena = false;
        dPonisti = view.findViewById(R.id.dPonisti);
        dUpisiKnjigu = view.findViewById(R.id.dUpisiKnjigu);
        katKnjige= view.findViewById(R.id.sKategorijaKnjige);
        dNadjiSliku = view.findViewById(R.id.dNadjiSliku);
        naslovna = view.findViewById(R.id.naslovnaStr);
        nazivKnjige = view.findViewById(R.id.nazivKnjige);
        imeAutora = view.findViewById(R.id.imeAutora);


        ArrayAdapter<String> sadapter ;
        sadapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, kategorije);
        katKnjige.setAdapter(sadapter);

        dNadjiSliku.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.setType("image/*");
                if (i.resolveActivity(getActivity().getPackageManager()) != null)
                    startActivityForResult(i, img_get);
            }
        });

        dPonisti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStackImmediate();
            }
        });

        dUpisiKnjigu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    FileOutputStream outputStream;
                    outputStream = getActivity().openFileOutput(nazivKnjige.getText().toString(), Context.MODE_PRIVATE);
                    getBitmapFromUri(data.getData()).compress(Bitmap.CompressFormat.JPEG, 30, outputStream);
                    outputStream.close();
                } catch (IOException e){}
                Knjiga temp = new Knjiga(imeAutora.getText().toString(), nazivKnjige.getText().toString(), katKnjige.getSelectedItem().toString(), nazivKnjige.getText().toString());
                temp.setId(imeAutora.getText().toString()+ nazivKnjige.getText().toString());

                knjige.add(0, temp);

                boolean pronadjen=false;

                for(int i=0; i<autori.size(); i++){
                    if(autori.get(i).getImeiPrezime().equals(imeAutora.getText().toString())){
                        autori.get(i).dodajKnjigu(temp.getId());
                        pronadjen=true;
                    }
                }
                if(!pronadjen)
                    autori.add(new Autor(imeAutora.getText().toString(), temp.getId()));

                getFragmentManager().popBackStackImmediate();

            }
        });

        imeAutora.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(imeAutora.getText().toString().matches("") || nazivKnjige.getText().toString().matches("") || slika_promjenjena==false)
                    dUpisiKnjigu.setEnabled(false);
                else dUpisiKnjigu.setEnabled(true);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        nazivKnjige.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(nazivKnjige.getText().toString().matches(""))
                    dNadjiSliku.setEnabled(false);
                else dNadjiSliku.setEnabled(true);
                if(nazivKnjige.getText().toString().matches("") || nazivKnjige.getText().toString().matches("") || slika_promjenjena==false)
                    dUpisiKnjigu.setEnabled(false);
                else dUpisiKnjigu.setEnabled(true);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return view;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == img_get) {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    slika_promjenjena=true;
                    this.data=data;
                    if(imeAutora.getText().toString().matches("") || nazivKnjige.getText().toString().matches("") || slika_promjenjena==false)
                        dUpisiKnjigu.setEnabled(false);
                    else dUpisiKnjigu.setEnabled(true);

                    naslovna.setImageBitmap(getBitmapFromUri(data.getData()));

                } catch (IOException e){}
            }
        }
    }


    Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor = getActivity().getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }
}
