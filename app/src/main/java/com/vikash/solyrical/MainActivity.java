package com.vikash.solyrical;

import android.Manifest;
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
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    SQLiteDatabase database;
    static MediaPlayer mediaPlayer=new MediaPlayer();
    AudioManager audioManager;
    static int currentPosition=0,currentIndex=0;
    boolean shuffleOn=false;
    Random rand=new Random();
    Switch aSwitch;
    ListView listView;
    ViewPager viewPager;
    StatePageAdapter pageAdapter;
    Fragment_1 fragment1;
    Fragment_2 fragment2;
    Button button;
    GetLyrics getLyrics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        button=(Button)findViewById(R.id.play);
        aSwitch=findViewById(R.id.switch1);

        fragment1=new Fragment_1();
        fragment2=new Fragment_2();
        FragmentManager fm=getSupportFragmentManager();
        pageAdapter=new StatePageAdapter(fm);
        viewPager=(ViewPager) findViewById(R.id.container);

        pageAdapter.addFragment(fragment1);
        pageAdapter.addFragment(fragment2);
        viewPager.setAdapter(pageAdapter);
        setViewPager(0);


        database=this.openOrCreateDatabase("Songs",MODE_PRIVATE,null);
        database.execSQL("CREATE TABLE IF NOT EXISTS songs(title VARCHAR,filepath VARCHAR primary key)");


        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if(currentIndex>=fragment1.arrayList.size())
                    currentIndex=0;
                play(currentIndex);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        getFromStorage();
    }

    public void getFromStorage(){

        Cursor c=database.rawQuery("SELECT * FROM songs",null);
        int titleIndex=c.getColumnIndex("title");
        int pathIndex=c.getColumnIndex("filepath");
        if(c.moveToNext()){
            //data exists.
            fragment1.arrayList.clear();
            SongFinder.hashMap.clear();
            Log.i("info","getting from storage");
            do{
                fragment1.arrayList.add(c.getString(titleIndex));
                SongFinder.hashMap.put(c.getString(titleIndex),c.getString(pathIndex));




            }while(c.moveToNext());

        }
        else{
            //data doesn't exists.Create one.
            getFromLocal();
        }


        return;
    }

    public void togglePlayState(View view){
        try {
            if (mediaPlayer == null) {
                mediaPlayer.release();
                play(currentIndex++);
                return;
            }
            if (mediaPlayer.isPlaying()) {
                button.setText(R.string.play);
                currentPosition = mediaPlayer.getCurrentPosition();
                mediaPlayer.pause();
            } else if(mediaPlayer!=null) {
                button.setText(R.string.pause);
                mediaPlayer.seekTo(currentPosition);
                mediaPlayer.start();
            }
        }   catch (Exception e){
            Toast.makeText(this,"Error with the track",Toast.LENGTH_SHORT).show();
        }
    }

    public void playNext(View view){
       if(aSwitch.isChecked()){
           //Logic for shuffle.
           currentIndex=rand.nextInt(fragment1.arrayList.size());
       }
       else{
           if(currentIndex>=fragment1.arrayList.size())
               currentIndex=0;
       }
       play(currentIndex);
    }

    public void playPrev(View view){
        if(aSwitch.isChecked())
        {
            //logic for shuffle.
            currentIndex=rand.nextInt(fragment1.arrayList.size());
        }
        else
        {
            currentIndex-=2;
            if(currentIndex<0)
                currentIndex=fragment1.arrayList.size()-1;
        }
        play(currentIndex);
    }

    public void play(int index){
        try{
            if(mediaPlayer!=null&&mediaPlayer.isPlaying())
                mediaPlayer.release();
            Uri uri= Uri.parse(SongFinder.hashMap.get(fragment1.arrayList.get(index)));
            Log.i("song",uri.toString());
            mediaPlayer=MediaPlayer.create(this,uri);

            mediaPlayer.start();
        }   catch(Exception e){
            Toast.makeText(this,"Couldn't play the track you requested",Toast.LENGTH_SHORT).show();

        }
        Fragment_1.fetchLyrics(currentIndex);
        currentIndex=index+1;
        int color,acolor=ColorGenerator.generateRandomColor();
        findViewById(R.id.toolbar).setBackgroundColor(acolor);
        Fragment_1.listView.setBackgroundColor(acolor);
        findViewById(R.id.controlBar).setBackgroundColor(color=ColorGenerator.generateRandomColor());
        Fragment_2.textView.setBackgroundColor(color);

    }

    public void getFromLocal(){

        DownloadFromLocal task=new DownloadFromLocal();
        task.execute();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==1){
            if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED)
                getFromLocal();
        }
    }



    public void setViewPager(int index){
        viewPager.setCurrentItem(index);

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

    public class DownloadFromLocal extends AsyncTask<String,Void,ArrayList<String>>{

        protected ArrayList<String> doInBackground(String... strings) {


            if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                return null;
            }
            ArrayList<String> arrayList=new ArrayList<String>();
            try {
                SongFinder songFinder = new SongFinder();
                arrayList=songFinder.find(arrayList);
                Log.i("song", "List being updated");
                database.execSQL("DELETE FROM songs");

                for (int i = 0; i < arrayList.size(); i++) {
                    SQLiteStatement statement = database.compileStatement("INSERT INTO songs(title,filepath) VALUES(?,?)");
                    statement.bindString(1, arrayList.get(i));
                    statement.bindString(2, SongFinder.hashMap.get(arrayList.get(i)));
                    statement.execute();
                }
            }   catch (Exception e){
                Toast.makeText(MainActivity.this,"Error occured",Toast.LENGTH_SHORT).show();
            }

            return arrayList;
        }

        @Override
        protected void onPostExecute(ArrayList<String> list) {
            super.onPostExecute(list);

            if(list!=null&&!list.isEmpty()){
                Fragment_1.arrayList.clear();
                for(String each:list)
                    Fragment_1.arrayList.add(each);
            }
            fragment1.arrayAdapter.notifyDataSetChanged();
        }
    }

}
