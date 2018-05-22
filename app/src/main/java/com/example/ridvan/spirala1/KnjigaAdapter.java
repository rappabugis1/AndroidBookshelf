package com.example.ridvan.spirala1;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class KnjigaAdapter extends ArrayAdapter<Knjiga> implements Filterable {

    ArrayList<Knjiga> knjige, fknjige;
    Context mContext;
    Filter filter;
    Boolean opc;

    public KnjigaAdapter(Context context, ArrayList<Knjiga> items, Boolean opc) {
        super(context, R.layout.knjiga_layout, items);
        this.knjige=items;
        this.mContext=context;
        this.opc=opc;
    }

    private static class ViewHolder {
        TextView naziv;
        TextView autor;
        ImageView slika;
    }

    @Override
    public View getView (int position, View convertView, ViewGroup parent) {
        Knjiga knjiga = getItem(position);
        ViewHolder holder;

        if (convertView == null) {
            holder= new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.knjiga_layout, parent, false);
            holder.naziv = convertView.findViewById(R.id.eNaziv);
            holder.autor= convertView.findViewById(R.id.eAutor);
            holder.slika= convertView.findViewById(R.id.eNaslovna);
            if(knjiga.oznacena)
                convertView.setBackgroundResource(R.color.colorLightBlue);

            try{
                holder.slika.setImageBitmap(BitmapFactory.decodeStream(mContext.openFileInput(knjiga.getNaziv())));
            }catch(FileNotFoundException e){
                if(knjiga.getSlika()!=null){
                    new DownloadImageTask(holder.slika).execute(knjiga.getSlika().toString());
                }
                else{
                    holder.slika.setImageResource(android.R.drawable.btn_dialog);
                }
            }

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();

        }

        holder.naziv.setText(knjiga.getNaziv());

        ArrayList<String> imena = new ArrayList<>();
        for (Autor x: knjiga.getAutori()) {
            imena.add(x.getImeiPrezime());
        }

        holder.autor.setText(imena.toString());


        return convertView;
    }

    @Override
    public Filter getFilter()
    {
        if (filter == null)
            filter = new FilterKnjiga();

        return filter;
    }

    private class FilterKnjiga extends Filter{

        @Override
        protected FilterResults performFiltering(CharSequence constraint)
        {
            FilterResults results = new FilterResults();
            String prefix = constraint.toString().toLowerCase();

            if (prefix == null || prefix.length() == 0)
            {
                ArrayList<Knjiga> list = new ArrayList<Knjiga>(knjige);
                results.values = list;
                results.count = list.size();
            }
            else
            {
                final ArrayList<Knjiga> list = new ArrayList<Knjiga>(knjige);
                final ArrayList<Knjiga> nlist = new ArrayList<Knjiga>();
                int count = list.size();

                for (int i=0; i<count; i++)
                {
                    final Knjiga knjiga = list.get(i);
                    String value;
                    if(!opc) {
                        value = knjiga.getKategorija().toLowerCase();
                        if (value.equals(prefix)) {
                            nlist.add(knjiga);
                        }
                    }
                    else {
                        for(int i2=0;i2<knjiga.getAutori().size();i2++) {
                            value = knjiga.getAutori().get(i2).getImeiPrezime().toLowerCase();
                            if (value.equals(prefix))
                            {
                                nlist.add(knjiga);
                            }
                        }
                    }

                }
                results.values = nlist;
                results.count = nlist.size();
            }
            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            fknjige = (ArrayList<Knjiga>)results.values;

            clear();
            int count = fknjige.size();
            for (int i=0; i<count; i++)
            {
                Knjiga knjiga = fknjige.get(i);
                add(knjiga);
            }
        }

    }

}
