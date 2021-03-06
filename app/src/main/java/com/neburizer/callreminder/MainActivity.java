package com.neburizer.callreminder;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity{


    private Context cxt;

    //side drawer variables
    private String[] sTitles;
    TypedArray sImages;
    private DrawerLayout sDrawerLayout;
    private ListView sDrawerList;
    private ActionBarDrawerToggle sDrawerToggle;

    //Databases
    public static DatabaseHelper rdh;

    //***************************Default Activity functions*****************************//
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupVariables();
        setupNavigationDrawer();
        Log.v("test log","pass");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    //***************************Default Activity functions*****************************//

    //***************************Extra Activity functions for toggle bar*****************************//
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
    //***************************Extra Activity functions for toggle bar*****************************//


    private void setupVariables() {
        cxt = getApplicationContext();
        rdh = DatabaseHelper.getInstance(cxt);
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

    //***************************Navigation drawer code*****************************//
    private void setupNavigationDrawer() {
        sTitles = getResources().getStringArray(R.array.titles_array);
        sImages = getResources().obtainTypedArray(R.array.navigation_drawer_images);
        sDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        sDrawerList = (ListView) findViewById(R.id.left_drawer);
        sDrawerList.setAdapter(new NavigationBarAdapter(this,sTitles,sImages));
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.animator.fade_in, 0, 0, 0);
        ft.replace(R.id.content_frame,new RemindersFragment()).commit();
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

    /**
     * Click listener for navigation drawer
     */
    private class DrawerItemClickListener implements ListView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
            Fragment newF = null;
            switch (pos)
            {
                case 0:
                    newF = new HelpFragment();
                    break;
                case 1:
                    newF = new RemindersFragment();
                    break;
                case 2:
                    newF = new AnalyzeFragment();
                    break;
                case 3:
                    newF = new HelpFragment();
                    break;
            }
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.setCustomAnimations(R.animator.fade_in, 0, 0, 0);
            ft.replace(R.id.content_frame,newF).commit();
            sDrawerLayout.closeDrawer(sDrawerList);
            setTitle(sTitles[pos]);
        }
    }

    /**
     * Adapter class for initialising navigation bar
     */
    private class NavigationBarAdapter extends BaseAdapter {
        String[] txt_list;
        TypedArray imageId;
        private LayoutInflater inflater = null;

        public NavigationBarAdapter(Context c, String[] txt_list, TypedArray img_list) {
            this.txt_list = txt_list;
            imageId = img_list;
            inflater = (LayoutInflater) c.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }


        @Override
        public int getCount() {
            return txt_list.length;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public class Holder {
            TextView tv;
            ImageView img;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            Holder holder = new Holder();
            View rowView;
            rowView = inflater.inflate(R.layout.drawer_list_item, null);
            holder.tv = (TextView) rowView.findViewById(R.id.textView1);
            holder.img = (ImageView) rowView.findViewById(R.id.imageView1);
            holder.tv.setText(txt_list[position]);
            holder.img.setImageResource(imageId.getResourceId(position, -1));
            return rowView;
        }
    }

    //***************************Navigation drawer code*****************************//

}


