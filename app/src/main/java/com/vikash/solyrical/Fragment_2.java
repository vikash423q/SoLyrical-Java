package com.vikash.solyrical;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;


public class Fragment_2 extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    static TextView textView;
    static ArrayList<String> spinnerList;
    static ArrayAdapter<String> adapter;
    Spinner spinner;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public Fragment_2() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view= inflater.inflate(R.layout.fragment_fragment_2, container, false);
        textView=view.findViewById(R.id.textView);
        textView.setMovementMethod(new ScrollingMovementMethod());


        spinner=(Spinner)view.findViewById(R.id.spinner);
        spinnerList=new ArrayList<String>();

        adapter=new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_dropdown_item,spinnerList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                for(int i=0;i<position;i++)
                Log.i("temp",GetLyrics.tempList.get(i));
                GetLyrics get=new GetLyrics();
                get.downloadLyrics(GetLyrics.tempList.get(position));
                textView.setBackgroundColor(ColorGenerator.generateRandomColor());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return view;
    }


}
