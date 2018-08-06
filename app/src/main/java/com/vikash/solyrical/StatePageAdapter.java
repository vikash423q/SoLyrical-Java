package com.vikash.solyrical;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

public class StatePageAdapter extends FragmentStatePagerAdapter {
    ArrayList<Fragment> fragments=new ArrayList<Fragment>();



    public StatePageAdapter(FragmentManager fm) {
        super(fm);
    }

    public void addFragment(Fragment fragment){
        fragments.add(fragment);
    }


    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}
