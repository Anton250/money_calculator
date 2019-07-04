package com.netproj.moneycalculator;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;



public class MonthGraphShowFragment extends Fragment {
    private int month_id;
    private static String MONTH_ID = "month_id";
    private ViewPager pager;
    private View savedView;



    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        month_id = getArguments().getInt(MONTH_ID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if(savedView != null){
            return savedView;
        }
        savedView = inflater.inflate(R.layout.main_window_fragment, null);
        pager = savedView.findViewById(R.id.mainViewPager);
        PageViewAdapter pagerAdapter = new PageViewAdapter(getActivity().getSupportFragmentManager(), this.getContext());
        pager.setAdapter(pagerAdapter);
        pager.setCurrentItem(month_id);
        return savedView;
    }

    @Override
    public void onDestroy(){

        pager.setAdapter(null);
        System.runFinalization();
        super.onDestroy();
    }

}
