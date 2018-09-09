package com.example.ridvan.spirala1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.util.ArrayList;

public class SKnjigeAdapter extends BaseAdapter {
    private Context mContext;

    ArrayList<Knjiga> knjige;

    public SKnjigeAdapter(Context c, ArrayList<Knjiga> knjige) {
        mContext = c;
        this.knjige=knjige;
    }

    public int getCount() {
        return knjige.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView=inflater.inflate(R.layout.book_thumbnail, parent, false);

        imageView= convertView.findViewById(R.id.thumb);
        ProgressBar bar = convertView.findViewById(R.id.bar);
        Knjiga knjiga=knjige.get(position);

        if(knjiga.getSlika()!=null)
            new DownloadImageTask(imageView,bar).execute(knjiga.getSlika().toString());
        else
            imageView.setImageResource(android.R.drawable.btn_dialog);


        return convertView;
    }

}
