package com.neburizer.callreminder;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity{


    private Context cxt;

    //side drawer variables
    private String[] sTitles;
    TypedArray sImages;
    private DrawerLayout sDrawerLayout;
    private ListView sDrawerList;
    private ActionBarDrawerToggle sDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupVariables();
        setupNavigationDrawer();
    }


    private void setupVariables() {
        cxt = getApplicationContext();
        //initialize activity elements to variables
        /*skipDaysPicker = (NumberPicker) findViewById(R.id.skipDaysPicker);
        skipDaysPicker.setMinValue(1);
        skipDaysPicker.setMaxValue(25);
        skipDaysPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                skipDays = newVal;
            }
        });*/
    }

    private void setupNavigationDrawer() {
        sTitles = getResources().getStringArray(R.array.titles_array);
        sImages = getResources().obtainTypedArray(R.array.navigation_drawer_images);
        sDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        sDrawerList = (ListView) findViewById(R.id.left_drawer);
        sDrawerList.setAdapter(new CustomAdapter(this,sTitles,sImages));
        sDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        sDrawerToggle = new ActionBarDrawerToggle(
                this,                  //* host Activity *//*
                sDrawerLayout,         //* DrawerLayout object *//*
                R.string.open_drawer,  //* "open drawer" description *//*
                R.string.close_drawer  //* "close drawer" description *//*
        ) {
            //** Called when a drawer has settled in a completely closed state. *//*
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                //getActionBar().setTitle(mTitle);
            }

            //** Called when a drawer has settled in a completely open state. *//*
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                //getActionBar().setTitle(mDrawerTitle);
            }
        };
        sDrawerLayout.setDrawerListener(sDrawerToggle);
        ActionBar actionBar;
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        sDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        sDrawerToggle.onConfigurationChanged(newConfig);
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Fragment newF = new HelpFragment();
            Bundle args = new Bundle();
            args.putInt(HelpFragment.fragment_pos,i);
            newF.setArguments(args);
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.setCustomAnimations(R.animator.fade_in, 0, 0, 0);
            ft.replace(R.id.content_frame,newF).commit();
            sDrawerLayout.closeDrawer(sDrawerList);
            setTitle(sTitles[i]);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (sDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }






}
