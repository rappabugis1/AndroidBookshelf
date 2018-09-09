package com.example.ridvan.spirala1;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import com.facebook.stetho.Stetho;



public class BaseActivity extends AppCompatActivity{

    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Stetho.initializeWithDefaults(this);
        setContentView(R.layout.base_layout);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_24dp);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navView= findViewById(R.id.nav_view);

        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                item.setChecked(true);

                mDrawerLayout.closeDrawers();

                switch (item.getItemId()){
                    case R.id.nav_home:
                        FragManager(new HomeFragment(),false);
                        break;
                    case R.id.nav_categories:
                        FragManager(new ListeFragment(),false);
                        break;
                    case R.id.nav_authors:
                        FragManager(new ListeFragment(),true);
                        break;
                    case R.id.nav_settings:
                        break;
                }
                return false;
            }
        });

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS},100);
        }

        BazaOpenHelper db = new BazaOpenHelper(getApplicationContext());

        FragManager(new HomeFragment(),false);
    }

    private void FragManager(Fragment frag, boolean opc){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        Bundle bundle = new Bundle();
        bundle.putBoolean("opc",opc);
        frag.setArguments(bundle);

        fragmentTransaction.replace(R.id.fragment_view, frag);
        fragmentTransaction.commit();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }



}
