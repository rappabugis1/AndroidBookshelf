package com.example.ridvan.spirala1;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.util.ArrayList;

public class HomeKnjigeRecViewAdapter extends RecyclerView.Adapter<HomeKnjigeRecViewAdapter.ViewHolder> {

    ArrayList<Knjiga> knjige;
    private ItemClickListener mClickListener;

    public HomeKnjigeRecViewAdapter(ArrayList<Knjiga> knjige) {

        this.knjige= knjige;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_thumbnail, parent, false);

        return new ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Knjiga knjiga = knjige.get(position);

        if(knjiga.getThumb()!=null) {
            holder.image.setImageBitmap(knjiga.getThumb());
            holder.bar.setVisibility(View.GONE);
        }
        else
        if(knjiga.getSlika()!=null)
            new DownloadImageTask(holder.image,holder.bar).execute(knjiga.getSlika().toString());
        else {
            holder.image.setImageResource(android.R.drawable.btn_dialog);
            holder.bar.setVisibility(View.GONE);
        }
    }


    @Override
    public int getItemCount() {
        return knjige.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView image;
        ProgressBar bar;

        public ViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.thumb);
            bar = itemView.findViewById(R.id.bar);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    public Knjiga getItem(int id) {
        return knjige.get(id);
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }



}
