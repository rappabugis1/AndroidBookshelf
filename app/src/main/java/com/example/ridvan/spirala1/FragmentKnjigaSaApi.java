package com.example.ridvan.spirala1;


import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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

        setHasOptionsMenu(true);
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
        Switch procitana;
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
        holder.procitana=view.findViewById(R.id.procitanaSwitch);


        //populisanje informacijama

        ArrayList<String> imena = new ArrayList<>();
        for (Autor x: knjiga.getAutori()) {
            imena.add(x.getImeiPrezime());
        }
        if(knjiga.getAutori().size()==0)
            imena.add("Author unknown");
        holder.naziv.setText(knjiga.getNaziv());
        holder.autor.setText(imena.toString()
                .replace("[", "")  //remove the right bracket
                .replace("]", ""));
        holder.opis.setText(knjiga.getOpis());
        holder.brStranica.setText(Integer.toString(knjiga.getBrojStranica()));
        holder.datum.setText(knjiga.getDatumObjavljivanja());
        ProgressBar bar = view.findViewById(R.id.bar);

        try{
            if(knjiga.getThumb()!=null) {
                holder.slika.setImageBitmap(knjiga.getThumb());
                bar.setVisibility(View.GONE);
            }
            else
            holder.slika.setImageBitmap(BitmapFactory.decodeStream(getActivity().openFileInput(knjiga.getNaziv())));
        }catch(Exception e){
            if(knjiga.getSlika()!=null){
                new DownloadImageTask(holder.slika, bar).execute(knjiga.getSlika().toString());
            }
            else{
                holder.slika.setImageResource(android.R.drawable.btn_dialog);
                bar.setVisibility(View.GONE);
            }
        }

        //swithevi

        if(helper.pretraziKnjige(knjiga)==0) {
            holder.dodajKnjigu.setChecked(true);
            holder.dodajKnjigu.setClickable(false);
            if(helper.getKnjigaFromId(helper.dajIdKnjigePoIdServisu(knjiga.getId())).oznacena){
                holder.procitana.setChecked(true);
                holder.procitana.setClickable(false);
            } else {
                holder.procitana.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        holder.procitana.setClickable(false);
                        helper.oznaciKnjigu(helper.dajIdKnjigePoIdServisu(knjiga.getId()));
                    }
                });
            }
        } else {
            holder.dodajKnjigu.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(holder.slika.getDrawable()!=null) {
                        helper.dodajKategoriju(knjiga.getKategorija());
                        holder.dodajKnjigu.setClickable(false);
                        long id = helper.dodajKnjigu(knjiga);
                        if (knjiga.getSlika() != null) {
                            helper.dodajThumbIDKnjigi(id, helper.dodajThumbnail(id, bitmapToByte(((BitmapDrawable) holder.slika.getDrawable()).getBitmap())));
                        }
                        dajTost("Book has been added");
                    } else {
                        holder.dodajKnjigu.setChecked(false);
                        dajTost("Please wait while image is loading");
                    }
                }
            });

            holder.procitana.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(holder.slika.getDrawable()!=null) {
                        holder.procitana.setClickable(false);
                        holder.dodajKnjigu.setChecked(true);
                        helper.oznaciKnjigu(helper.dajIdKnjigePoIdServisu(knjiga.getId()));
                    }
                    else {
                        holder.procitana.setChecked(false);
                        dajTost("Please wait while image is loading");
                    }
                }
            });
        }

        //dio za email
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED) {

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
        } else {
            holder.posalji.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String emailName="";

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
        }





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

    // Bitmap to byte[]
    public byte[] bitmapToByte(Bitmap bitmap) {
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            //bitmap to byte[] stream
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] x = stream.toByteArray();
            //close stream to save memory
            stream.close();
            return x;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void dajTost(String text){
        Context context = getActivity();
        int duration = Toast.LENGTH_LONG;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    @Override
    public void onCreateOptionsMenu(
            Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.knjiga_menu, menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.change_cat:

                final BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(getActivity());
                View sheetView = getActivity().getLayoutInflater().inflate(R.layout.promijeni_kategoriju, null);
                mBottomSheetDialog.setContentView(sheetView);
                mBottomSheetDialog.show();

                Button ok=sheetView.findViewById(R.id.ok);
                ArrayList<String> kat= helper.dajImenaKategorija();

                final Spinner spinner = (Spinner) sheetView.findViewById(R.id.spinnerKat);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, kat);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);

                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        helper.promijeniKategorijuKnjige(knjiga.getId(), spinner.getSelectedItem().toString() );
                        dajTost("Kategorija promijenjena!");
                        mBottomSheetDialog.dismiss();
                    }
                });

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }



}
