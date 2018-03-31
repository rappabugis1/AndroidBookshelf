package com.example.ridvan.spirala1;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class DodavanjeKnjigeAkt extends AppCompatActivity {

    Button dNadjiSliku,dUpisiKnjigu, dPonisti;
    EditText imeAutora, nazivKnjige;
    Spinner katKnjige;
    ImageView naslovna;
    static final int img_get =1;
    Boolean slika_promjenjena;
    Intent data;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == img_get) {
            if (resultCode == RESULT_OK) {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dodavanje_knjige_akt);

        slika_promjenjena = false;
        dPonisti = (Button) findViewById(R.id.dPonisti);
        dUpisiKnjigu = (Button) findViewById(R.id.dUpisiKnjigu);
        katKnjige= (Spinner) findViewById(R.id.sKategorijaKnjige);
        dNadjiSliku = (Button) findViewById(R.id.dNadjiSliku);
        naslovna = (ImageView) findViewById(R.id.naslovnaStr);
        nazivKnjige = (EditText) findViewById(R.id.nazivKnjige);
        imeAutora = (EditText) findViewById(R.id.imeAutora);

        final ArrayList<Knjiga> knjige = (ArrayList<Knjiga>) getIntent().getSerializableExtra("knjige");

        ArrayList<String> kategorije = (ArrayList<String>) getIntent().getSerializableExtra("kategorije");
        ArrayAdapter<String> sadapter ;
        sadapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, kategorije);
        katKnjige.setAdapter(sadapter);

        dNadjiSliku.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.setType("image/*");
                if (getIntent().resolveActivity(getPackageManager()) != null)
                    startActivityForResult(i, img_get);
            }
        });

        dPonisti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        dUpisiKnjigu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    FileOutputStream outputStream;
                    outputStream = openFileOutput(nazivKnjige.getText().toString(), Context.MODE_PRIVATE);
                    getBitmapFromUri(data.getData()).compress(Bitmap.CompressFormat.JPEG, 30, outputStream);
                    outputStream.close();
                } catch (IOException e){}

                knjige.add(0, new Knjiga(imeAutora.getText().toString(), nazivKnjige.getText().toString(), katKnjige.getSelectedItem().toString(), nazivKnjige.getText().toString()));
                Intent intent = new Intent();
                intent.putExtra("knjige",knjige);
                setResult(RESULT_OK, intent);
                finish();
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

    }

    Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor = getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }

}
