package com.netproj.moneycalculator;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;


import androidx.fragment.app.Fragment;

public class MonthsListFragment extends Fragment {


    private OnMonthClicked mListener;
    private setUpView v;

    public MonthsListFragment() {
    }



    public interface OnMonthClicked{
        void onMonthClicked(int id);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnMonthClicked) {
            mListener = (OnMonthClicked) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement MyListFragment.OnItemSelectedListener");
        }
    }
    @Override
    public void onCreate(Bundle s){
        super.onCreate(s);
        Log.d("CREATED", "MONTH_LIST");
    }

    @Override
    public void onDestroy(){

        Log.d("DESTROYED", "MONTH_LIST");
       // list.setAdapter(null);
        System.runFinalization();
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


      //  if (savedView != null){
       //     return savedView;
      //  }
        View savedView =  inflater.inflate(R.layout.list_view, null);
        v = new setUpView();
        v.execute();
        return savedView;
    }

    @Override
    public void onPause(){
        v.cancel(true);
        Log.d("PAUSED", "MONTH_LIST");
        super.onPause();
    }

    private class setUpView extends AsyncTask<Void, Void, ArrayAdapter<String>> implements AdapterView.OnItemClickListener {

        @Override
        protected ArrayAdapter<String> doInBackground(Void... voids) {
            DBHelper dbHelper = new DBHelper(getContext());
            ArrayAdapter<String> arr = dbHelper.getMonths();
            dbHelper.close();
            return arr;
        }

        @Override
        protected void onPostExecute(ArrayAdapter<String> result) {
            View view = getView();
            ListView list = view.findViewById(R.id.months_list);
            list.setAdapter(result);
            list.setOnItemClickListener(this);

        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mListener.onMonthClicked(position);
        }
    }

}
