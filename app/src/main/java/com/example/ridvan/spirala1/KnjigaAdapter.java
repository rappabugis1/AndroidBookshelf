package com.example.ridvan.spirala1;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.text.style.BackgroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class KnjigaAdapter extends ArrayAdapter<Knjiga> implements Filterable {

    ArrayList<Knjiga> knjige;
    ArrayList<Knjiga> fknjige;
    Context mContext;
    Filter filter;

    public KnjigaAdapter(Context context, ArrayList<Knjiga> items) {
        super(context, R.layout.knjiga_layout, items);
        this.knjige=items;
        this.mContext=context;
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
            holder.naziv = (TextView) convertView.findViewById(R.id.eNaziv);
            holder.autor= (TextView) convertView.findViewById(R.id.eAutor);
            holder.slika= (ImageView) convertView.findViewById(R.id.eNaslovna);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if(knjiga.oznacena)
            convertView.setBackgroundResource(R.color.colorPrimaryDark);
        holder.naziv.setText(knjiga.getNazivKnjige());
        holder.autor.setText(knjiga.getImeAutora());
        try{
            holder.slika.setImageBitmap(BitmapFactory.decodeStream(mContext.openFileInput(knjiga.nazivKnjige)));
        }catch(FileNotFoundException e){
            holder.slika.setImageResource(android.R.drawable.btn_dialog);
        }
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
                    final String value = knjiga.getKategorija().toLowerCase();

                    if (value.equals(prefix))
                    {
                        nlist.add(knjiga);
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
                Knjiga knjiga = (Knjiga)fknjige.get(i);
                add(knjiga);
            }
        }

    }

}
