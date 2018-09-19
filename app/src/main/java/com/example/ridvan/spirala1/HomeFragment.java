package com.example.ridvan.spirala1;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class HomeFragment extends Fragment{

    PagerAdaptor adapterPager;
    HomeKnjigeRecViewAdapter adapter1, adapter2;
    ViewPager pager;
    View view;
    BazaOpenHelper db;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        db= new BazaOpenHelper(getActivity());
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.home_layout, container, false);

        getActivity().setTitle(R.string.Home);

        //populisanje pagera
        pager = view.findViewById(R.id.pager);
        adapterPager = new PagerAdaptor(getFragmentManager());
        pager.setAdapter(adapterPager);
        pager.setSaveFromParentEnabled(false);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        //populisanje recviewa

        RecyclerView rec1 = view.findViewById(R.id.bestKatRec);
        RecyclerView rec2 = view.findViewById(R.id.bestAutRec);
        TextView najKat = view.findViewById(R.id.najKat);
        TextView najAut = view.findViewById(R.id.najAut);

        String imeKat= db.dajImeNajveceKat();
        String imeAut=db.dajImeNajvecAut();

        if(imeKat!=null){
            adapter1 = new HomeKnjigeRecViewAdapter(db.knjigeKategorije(db.dajIdKatPoImenu(imeKat)));

            adapter1.setClickListener(new HomeKnjigeRecViewAdapter.ItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    TransManager(new FragmentKnjigaSaApi(), adapter1.getItem(position));
                }
            });

            rec1.setAdapter(adapter1);
            najKat.setText(imeKat);
        }

        if(imeAut!=null){
            adapter2 = new HomeKnjigeRecViewAdapter(db.knjigeAutora(db.dajIdAutoraPoImenu(imeAut)));
            rec2.setAdapter(adapter2);

            adapter2.setClickListener(new HomeKnjigeRecViewAdapter.ItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    TransManager(new FragmentKnjigaSaApi(), adapter2.getItem(position));
                }
            });

            najAut.setText(imeAut);
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Set title
        getActivity().setTitle(R.string.Home);
    }

    private void TransManager (Fragment frag, Knjiga knjiga ){
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        Bundle bundle = new Bundle();
        bundle.putSerializable("knjiga",knjiga);

        frag.setArguments(bundle);

        transaction.replace(R.id.fragment_view, frag);
        transaction.addToBackStack(null);
        transaction.commit();
    }

}
