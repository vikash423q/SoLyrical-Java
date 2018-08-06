package com.vikash.solyrical;

import android.Manifest;
import android.content.Context;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class SongFinder {

    File file= Environment.getExternalStorageDirectory();
    String root=file.getPath()+"/";
    ArrayList<String> songs=new ArrayList<String>();
    static HashMap<String,String> hashMap=new HashMap<String, String>();

    public ArrayList<String> find(ArrayList<String> list){

        list.clear();
        hashMap.clear();
        songs=list;
        File[] files=file.listFiles();
        scanDirectory(files);

        return songs;
    }

    public void scanDirectory(File[] files){
        for(File temp:files){
            if(temp.isDirectory()&&!temp.toString().contains("/."))
                scanDirectory(temp.listFiles());
            else
                filterSong(temp);
        }
    }

    public void filterSong(File file){
        String name="";
        if(file.getAbsolutePath().endsWith(".mp3")){
            name=file.getName().substring(0,file.getName().length()-4);
            songs.add(name);
            hashMap.put(name,file.getAbsolutePath());
        }
    }
}
