package com.vikash.solyrical;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GetLyrics {
    ArrayList<String> matches;
    static ArrayList<String> tempList=new ArrayList<String>();
    String lyricsUrl,lyrics="";
    String listregex="<a (?:href=\"(.*?)\".*<b>(.*?)</b></a>  by <b>(.*?)</b><br>)+?";
    String lyricsregex="<div>\\n<!-- Usage of azlyrics.com content by any third-party lyrics provider is prohibited by our licensing agreement. Sorry about that. -->\\n((?:(?:.*)?\\n)*?)<\\/div>";

    public void getLyrics(String metadata){
        matches=new ArrayList<String>();
        DownloadTask task=new DownloadTask();
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
                Log.i("listpage", matcher.group(1) + "  " + matcher.group(2) + "  " + matcher.group(3));
            }   while(matcher.find());
            downloadLyrics(lyricsUrl);
        }
        else
            Log.i("listpage","DownloadList error in extractList");
    }

    public void downloadLyrics(String url){
        new DownloadTask().execute(url,lyricsregex,"lyrics");
        Log.i("dropdown","dropdown clicked");
    }


    public void extractLyrics(Matcher matcher){

        if(matcher.find()){

            lyrics=matcher.group(1);
            lyrics=lyrics.replaceAll("<br>","\n");
            lyrics=lyrics.replaceAll("<.*?>","");
            lyrics=lyrics.replaceAll("&quot;","\"");
        }
        if(lyrics!=null) {
            //Log.i("lyrics",lyrics);
        }
        else
            Log.i("lyrics","lyrics can't be set");
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
                Fragment_2.textView.setText(lyrics);
                Fragment_2.adapter.notifyDataSetChanged();
            }


        }
    }
}
