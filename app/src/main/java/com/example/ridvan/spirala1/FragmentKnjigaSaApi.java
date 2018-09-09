package com.example.ridvan.spirala1;


import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;

public class FragmentKnjigaSaApi extends Fragment
{

    Knjiga knjiga;
    BazaOpenHelper helper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle= this.getArguments();
        if(bundle!=null)
            knjiga= (Knjiga) bundle.getSerializable("knjiga");

        helper=new BazaOpenHelper(getActivity());
    }

    private static class Contact {
        String email;
        String name;
    }

    private static class ViewHolder {
        TextView naziv;
        TextView autor;
        TextView brStranica;
        TextView opis;
        TextView datum;
        ImageView slika;
        Button posalji;
        Spinner kontakti;
        Switch dodajKnjigu;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_knjiga_sa_api, container, false);

        final ViewHolder holder= new ViewHolder();

        holder.naziv = view.findViewById(R.id.eNazivP);
        holder.autor= view.findViewById(R.id.eAutorP);
        holder.slika= view.findViewById(R.id.thumb);
        holder.brStranica = view.findViewById(R.id.eBrojStranicaP);
        holder.opis= view.findViewById(R.id.eOpisP);
        holder.datum= view.findViewById(R.id.eDatumObjavljivanjaP);
        holder.posalji= view.findViewById(R.id.dPosalji);
        holder.kontakti= view.findViewById(R.id.sKontakti);
        holder.dodajKnjigu=view.findViewById(R.id.dodajKnjigu);


        //swith

        if(helper.pretraziKnjige(knjiga)==0) {
            holder.dodajKnjigu.setChecked(true);
            holder.dodajKnjigu.setClickable(false);
        } else {
            holder.dodajKnjigu.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    helper.dodajKategoriju(knjiga.getKategorija());
                    holder.dodajKnjigu.setChecked(true);
                    holder.dodajKnjigu.setClickable(false);
                    helper.dodajKnjigu(knjiga);
                }
            });
        }


        //populisanje informacijama

        ArrayList<String> imena = new ArrayList<>();
        for (Autor x: knjiga.getAutori()) {
            imena.add(x.getImeiPrezime());
        }
        holder.naziv.setText(knjiga.getNaziv());
        holder.autor.setText(imena.toString()
                .replace("[", "")  //remove the right bracket
                .replace("]", ""));
        holder.opis.setText(knjiga.getOpis());
        holder.brStranica.setText(Integer.toString(knjiga.getBrojStranica()));
        holder.datum.setText(knjiga.getDatumObjavljivanja());
        ProgressBar bar = view.findViewById(R.id.bar);

        try{
            holder.slika.setImageBitmap(BitmapFactory.decodeStream(getActivity().openFileInput(knjiga.getNaziv())));
        }catch(FileNotFoundException e){
            if(knjiga.getSlika()!=null){
                new DownloadImageTask(holder.slika, bar).execute(knjiga.getSlika().toString());
            }
            else{
                holder.slika.setImageResource(android.R.drawable.btn_dialog);
            }
        }

        //dio za email

        final ArrayList<Contact> email = new ArrayList<Contact>(getNameEmailDetails());

        final ArrayList<String > emailadres = new ArrayList<>();
        for (Contact a: email)
            emailadres.add(a.email);



        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, emailadres);

        holder.kontakti.setAdapter(adapter);

        holder.posalji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailName="";

                for (Contact a: email)
                    if(a.email==holder.kontakti.getSelectedItem())
                        emailName = a.name;

                String[] TO ={((String) holder.kontakti.getSelectedItem())};
                String[] CC ={((String) holder.kontakti.getSelectedItem())};


                Intent emailIntent = new Intent(Intent.ACTION_SEND);

                emailIntent.setData(Uri.parse("mailto:"));
                emailIntent.setType("text/plain");
                emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
                emailIntent.putExtra(Intent.EXTRA_CC, CC);
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Preporuka");
                emailIntent.putExtra(Intent.EXTRA_TEXT,
                        "Zdravo "+emailName+"," +System.getProperty("line.separator")+ "Procitaj knjigu "
                        + holder.naziv.getText()+" od autora " + holder.autor.getText()+"!");

                try {
                    startActivity(Intent.createChooser(emailIntent, "Send mail..."));
                }
                catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(getActivity(), "There is no email client installed.", Toast.LENGTH_SHORT).show();
                }
            }
        });


        return view;
    }

    public ArrayList<Contact> getNameEmailDetails(){

        ArrayList<Contact> emlRecs = new ArrayList<Contact>();
        HashSet<String> emlRecsHS = new HashSet<String>();
        Context context = getActivity();
        ContentResolver cr = context.getContentResolver();

        String[] PROJECTION = new String[] { ContactsContract.RawContacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts.PHOTO_ID,
                ContactsContract.CommonDataKinds.Email.DATA,
                ContactsContract.CommonDataKinds.Photo.CONTACT_ID };

        String order = "CASE WHEN "
                + ContactsContract.Contacts.DISPLAY_NAME
                + " <> '%@%' THEN 1 ELSE 2 END, "
                + ContactsContract.Contacts.DISPLAY_NAME
                + ", "
                + ContactsContract.CommonDataKinds.Email.DATA
                + " COLLATE NOCASE";

        String filter = ContactsContract.CommonDataKinds.Email.DATA + " <> ''";

        Cursor cur = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, PROJECTION, filter, null, order);

        if (cur.moveToFirst()) {
            do {
                // names comes in hand sometimes
                String name = cur.getString(1);
                String emlAddr = cur.getString(3);

                // keep unique only
                if (emlRecsHS.add(emlAddr.toLowerCase())) {
                    Contact kont=new Contact();
                    kont.email=emlAddr;
                    kont.name=name;
                    emlRecs.add(kont);
                }
            } while (cur.moveToNext());
        }

        cur.close();
        return emlRecs;
    }

}
