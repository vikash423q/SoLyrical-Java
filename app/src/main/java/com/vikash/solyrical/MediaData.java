package com.vikash.solyrical;

import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MediaData extends MediaMetadataRetriever{

    public String[] fetchMeta(String file){
        try {
            this.setDataSource(file);
            String[] data = {this.extractMetadata(this.METADATA_KEY_TITLE), this.extractMetadata(this.METADATA_KEY_ALBUM), this.extractMetadata(this.METADATA_KEY_ARTIST)};
            return data;
        }   catch (Exception e){
            Log.i("exception",e.getMessage());
        }

        return null ;
    }

    public String getTitle(String file){

        String[] str=this.fetchMeta(file);
        if(str==null)
            return "No Title";
        String metadata="";
        try {
            Matcher matcher = Pattern.compile("(?:[\\w+'] ?)+").matcher(str[0]);
            if (matcher.find())
                metadata = matcher.group(0);
        }   catch (Exception e){
            e.printStackTrace();
        }

        if(metadata=="")
            metadata="No Title";
        return metadata;
    }

}
