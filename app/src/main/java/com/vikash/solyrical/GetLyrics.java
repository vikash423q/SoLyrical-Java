package com.vikash.solyrical;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.AsyncTask;
import android.text.LoginFilter;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GetLyrics {
    ArrayList<String> matches;
    boolean find=false;
    static ArrayList<String> tempList=new ArrayList<String>();
    String lyricsUrl,lyrics="";
    String listregex="<a (?:href=\"(.*?)\".*<b>(.*?)</b></a>  by <b>(.*?)</b><br>)+?";
    String lyricsregex="<div>\\n<!-- Usage of azlyrics.com content by any third-party lyrics provider is prohibited by our licensing agreement. Sorry about that. -->\\n((?:(?:.*)?\\n)*?)<\\/div>";
    String filepath=null;
    boolean firstTrial=true;
    public void getLyrics(String file){
        Cursor c=MainActivity.database.rawQuery("SELECT lyric FROM LYRICS WHERE filepath=?",new String[]{file});
        filepath=file;
        if(c.moveToNext()){

            String temp=c.getString(c.getColumnIndex("lyric"));
            Log.i("lyrics","Lyrics fetched from database");
            Fragment_2.textView.setText("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n"+temp);
            find=true;
        }
        matches=new ArrayList<String>();
        DownloadTask task=new DownloadTask();
        MediaData mediaData=new MediaData();
        String metadata="";

        String[] str=mediaData.fetchMeta(file);
        if(str!=null) {
            if(firstTrial)
                metadata=str[0] + "   "+str[2];
            else {
                metadata=mediaData.getTitle(file);

            }
            metadata=metadata.replaceAll("(\\(?[^ ]+?\\.[^ ]+)|\\(.*?\\)|\\d+", "");
            Log.i("listview",metadata);
        }

        String page,url="https://search.azlyrics.com/search.php?q=";
        try {
            task.execute(url+metadata,listregex,"list");
        }   catch (Exception e){
            Log.i("listpage","Error occured in getLyrics");
        }
        return;
    }

    public void extracttList(Matcher matcher){


        if(matcher.find()) {
            Fragment_2.spinnerList.clear();
            lyricsUrl=matcher.group(1);
            Log.i("url",lyricsUrl.toString());
            tempList.clear();
            do {
                tempList.add(matcher.group(1));
                Fragment_2.spinnerList.add(matcher.group(2) + " by " + matcher.group(3));
            }   while(matcher.find());

            Fragment_2.adapter.notifyDataSetChanged();
            if(!find)
                downloadLyrics(lyricsUrl);
        }
        else {
            Log.i("listpage", "DownloadList error in extractList.Trying again with title only.");
            if(firstTrial) {
                Fragment_2.textView.setText("Oops..lyrics not found.");
                firstTrial=false;
                getLyrics(filepath);
            }

        }
    }


    public void downloadLyrics(String url){
        DownloadTask task = new DownloadTask();
        task.execute(url,lyricsregex,"lyrics");
    }


    public void extractLyrics(Matcher matcher){

        if(matcher.find()){

            lyrics=matcher.group(1);
            lyrics=lyrics.replaceAll("<br>","\n");
            lyrics=lyrics.replaceAll("<.*?>","");
            lyrics=lyrics.replaceAll("&quot;","\"");
        }
        if(lyrics!=null) {
            //store your lyrics here.;
            saveLocally(lyrics,MainActivity.currentFilePath);

        }
        else
            Log.i("lyrics","lyrics can't be set");
    }

    private void saveLocally(String lyrics,String filepath){

        try {
            Log.i("lyrics", "Lyrics being saved locally");
            SQLiteStatement statement = MainActivity.database.compileStatement("INSERT INTO lyrics(lyric,filepath) VALUES(?,?)");
            statement.bindString(1, lyrics);
            statement.bindString(2, filepath);
            statement.execute();
        }   catch (Exception e){
            Log.i("lyrics","error in saving locally");
        }
    }


    public class DownloadTask extends AsyncTask<String,Void,Matcher>{

        @Override
        protected Matcher doInBackground(String... strings) {
            URL url;
            HttpURLConnection urlConnection;
            String regex,page,line="";
            StringBuilder out=new StringBuilder();

            try {
                url = new URL(strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestProperty( "User-agent", "Mozilla/5.0 (Windows NT 6.1; WOW64)");
                urlConnection.connect();
                InputStream in = urlConnection.getInputStream();

                BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                while ((line = reader.readLine()) != null) {
                    out.append(line).append("\n");
                }
                reader.close();
                line = out.toString();
            }   catch(Exception e) {
                Log.i("listpage","error occcured");
            }
            page=line;
            regex=strings[1];
            Pattern pattern=Pattern.compile(regex);
            Matcher matcher=pattern.matcher(page);


            if(strings[2]=="lyrics"){
                extractLyrics(matcher);
                return null;
            }


            return matcher;
        }

        @Override
        protected void onPostExecute(Matcher m) {

            if(m!=null)
                extracttList(m);
            else{
                Fragment_2.textView.setText("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n"+lyrics);

                if(filepath!=null){
                    saveLocally(lyrics,filepath);
                }

            }


        }
    }

}
