package com.example.ridvan.spirala1;

import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class SBookFrag extends Fragment{

    EditText querytxt;
    Button search;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.home_layout_sbook, container, false);

        querytxt = view.findViewById(R.id.sBook);
        search=view.findViewById(R.id.button);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = querytxt.getText().toString();
            }
        });

        return view;
    }

}