package com.example.ridvan.spirala1;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class PagerAdaptor extends FragmentStatePagerAdapter {

    private static int NUM_ITEMS=3;

    public PagerAdaptor (FragmentManager manager){
        super(manager);
    }

    @Override
    public int getCount(){
        return NUM_ITEMS;
    }

    @Override
    public Fragment getItem(int pos){
        switch(pos){
            case 0:
                return new SBookFrag();

            case 1:
                return new SAutorFrag();

            case 2:
                return new SIDFrag();
        }
        return null;
    }

}
