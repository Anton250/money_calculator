package com.netproj.moneycalculator;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class PageViewAdapter extends FragmentStatePagerAdapter {



    private Context localContext;
    PageViewAdapter(FragmentManager fm, Context context) {
        super(fm);
        localContext = context;
    }
    @Override
    public Fragment getItem(int i) {
        return MonthFragment.newInstance(i, 0);
    }

    @Override
    public int getCount() {
        DBHelper dbHelper = new DBHelper(localContext);
        int count = dbHelper.getCountOfPages();
        dbHelper.close();

        return count;
    }





}

