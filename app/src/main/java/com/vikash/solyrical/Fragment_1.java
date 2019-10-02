package com.vikash.solyrical;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.ToggleButton;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Fragment_1.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Fragment_1#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_1 extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    static ListView listView;
    ArrayAdapter<String> arrayAdapter;
    static ArrayList<String>  arrayList=new ArrayList<String>();
    static ToggleButton aSwitch;


    public Fragment_1() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_fragment_1, container, false);
        aSwitch = view.findViewById(R.id.switch1);
        aSwitch.setText(null);
        aSwitch.setTextOn(null);
        aSwitch.setTextOff(null);
        listView=(ListView)view.findViewById(R.id.listView1);

        arrayAdapter=new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,arrayList);

        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Logic on item click.

                ((MainActivity)getActivity()).play(position);
                ((MainActivity)getActivity()).button.setBackground(getResources().getDrawable(R.drawable.pausebutton,null));
                ;
            }
        });
        return view;
    }

    public static  void fetchLyrics(int index){

        new GetLyrics().getLyrics(SongFinder.hashMap.get(arrayList.get(index)));

    }

}
