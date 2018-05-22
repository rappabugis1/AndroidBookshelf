package com.example.ridvan.spirala1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;

public class AutorAdapter extends ArrayAdapter<Autor> implements Filterable {

    ArrayList<Autor> autori;
    Context mContext;

    public AutorAdapter(Context context, ArrayList<Autor> items) {
        super(context, R.layout.autor_layout, items);
        autori=new ArrayList<>(items);
        this.mContext=context;
    }

    private static class ViewHolder {
        TextView brKnjiga;
        TextView autor;
    }

    @Override
    public View getView (int position, View convertView, ViewGroup parent) {
        Autor autorObj = getItem(position);
        AutorAdapter.ViewHolder holder;

        if (convertView == null) {
            holder= new AutorAdapter.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.autor_layout, parent, false);
            holder.autor = convertView.findViewById(R.id.imeAutora);
            holder.brKnjiga= convertView.findViewById(R.id.brKnjiga);

            convertView.setTag(holder);
        } else {
            holder = (AutorAdapter.ViewHolder) convertView.getTag();
        }

        holder.autor.setText(autorObj.getImeiPrezime());
        holder.brKnjiga.setText(Integer.toString(autorObj.getKnjige().size()));

        return convertView;
    }

}
