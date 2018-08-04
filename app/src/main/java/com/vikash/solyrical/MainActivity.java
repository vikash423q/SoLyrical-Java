package com.vikash.solyrical;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    ArrayList<String> arrayList=new ArrayList<String>();
    ArrayAdapter<String> arrayAdapter;
    SQLiteDatabase database;
    static MediaPlayer mediaPlayer=new MediaPlayer();
    AudioManager audioManager;
    static int current=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final Fragment fragment=new PlayerFragment();
        FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentTransaction ft=fragmentManager.beginTransaction();
        ft.add(R.id.fragment,fragment);
        ft.commit();

        database=this.openOrCreateDatabase("Songs",MODE_PRIVATE,null);
        database.execSQL("CREATE TABLE IF NOT EXISTS songs(title VARCHAR,filepath VARCHAR primary key)");

        ListView listView=(ListView)findViewById(R.id.listView);

        arrayAdapter=new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1,arrayList);

        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Logic on item click.
                if(mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                    current = 0;
                }
                Uri uri= Uri.parse(SongFinder.hashMap.get(arrayList.get(position)));
                Log.i("Uri",uri.toString());
                mediaPlayer=MediaPlayer.create(getApplicationContext(),uri);
                fragment.getView().findViewById(R.id.button).callOnClick();
            }
        });
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        SongRetriever retriever=new SongRetriever();
        retriever.execute("");
    }

    public void getFromStorage(){

        Cursor c=database.rawQuery("SELECT * FROM songs",null);

        int titleIndex=c.getColumnIndex("title");
        int pathIndex=c.getColumnIndex("filepath");
        if(c.moveToNext()){
            //data exists.
            arrayList.clear();
            SongFinder.hashMap.clear();
            Log.i("info","getting from storage");
            do{
                arrayList.add(c.getString(titleIndex));
                SongFinder.hashMap.put(c.getString(titleIndex),c.getString(pathIndex));

            }while(c.moveToNext());
            arrayAdapter.notifyDataSetChanged();
        }
        else{
            //data doesn't exists.Create one.
            getFromLocal();
        }

        return;
    }

    public void getFromLocal(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            return;
        }
        SongFinder songFinder=new SongFinder();
        songFinder.find(arrayList);
        arrayAdapter.notifyDataSetChanged();
        Log.i("song","List being updated");
        database.execSQL("DELETE FROM songs");

        for(int i=0;i<arrayList.size();i++) {
            SQLiteStatement statement = database.compileStatement("INSERT INTO songs(title,filepath) VALUES(?,?)");
            statement.bindString(1, arrayList.get(i));
            statement.bindString(2, SongFinder.hashMap.get(arrayList.get(i)));
            statement.execute();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==1){
            if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED)
                getFromLocal();
        }
    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        else if(id==R.id.refresh){
            getFromLocal();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public class SongRetriever extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... strings) {
            getFromStorage();
            return null;
        }

    }
}
