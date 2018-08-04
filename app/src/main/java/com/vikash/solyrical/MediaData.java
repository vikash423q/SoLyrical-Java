package com.vikash.solyrical;

import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.util.Log;

import java.io.File;

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

}
