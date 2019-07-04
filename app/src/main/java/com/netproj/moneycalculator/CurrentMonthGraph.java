package com.netproj.moneycalculator;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;


import lecho.lib.hellocharts.listener.PieChartOnValueSelectListener;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;

public class CurrentMonthGraph extends Fragment implements View.OnClickListener {
    //private View savedView;
    private int currentMonthId;
    private DBHelper dbHelper;
    private ChartData chartData;
    private AsyncView v;
    private MonthFragment.OnSliceValueClicked mListener;


    @Override
    public void onClick(View v) {
        mListener.onButtonAddClicked(v, currentMonthId);
    }




    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MonthFragment.OnSliceValueClicked) {
            mListener = (MonthFragment.OnSliceValueClicked) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement MyListFragment.OnItemSelectedListener");
        }
    }


    public CurrentMonthGraph() {
    }



    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Log.d("CREATED", "MONTH");
        dbHelper = new DBHelper(getContext());
        currentMonthId = dbHelper.getCurrentMonthId() + 1;

    }

    @Override
    public void onPause(){
        v.cancel(true);
        Log.d("PAUSED", "MONTH");
        System.runFinalization();
        super.onPause();
    }

    @Override
    public void onDestroy(){

        dbHelper.close();
        dbHelper = null;
     //   dbHelper = null;
     //   savedView = null;
        Log.d("DESTROYED", "MONTH");
        System.runFinalization();
        super.onDestroy();
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //   if (type == 1){
        // getActivity().getActionBar().setTitle(R.string.costs);
        //bottom = getActivity().findViewById(R.id.bottomNavigationView);
        //  bottom.getMenu().getItem(0).setChecked(true);
        //    }

        Log.d("GOT VIEW", "NOW");
       //  SharedPreferences sp = getActivity().getSharedPreferences("appPref", Context.MODE_PRIVATE);
       // if (savedView != null && !sp.getBoolean("pieDataChanged", false)){
       //      return savedView;
      //   } else {
        View savedView = inflater.inflate(R.layout.fragment, null);
        Button btn = savedView.findViewById(R.id.btnAddCostInMonth);
        Button bthShow = savedView.findViewById(R.id.btnShowIncomes);
        btn.setVisibility(View.VISIBLE);
        btn.setOnClickListener(this);
        bthShow.setOnClickListener(this);
        v = new AsyncView();
        v.execute();

         //  sp.edit().putBoolean("pieDataChanged", false).apply();
        return savedView;
      //   }
    }





    private class AsyncView extends AsyncTask<Void, Void, ChartData> implements PieChartOnValueSelectListener, AdapterView.OnItemClickListener {




        @Override
        protected ChartData doInBackground(Void... voids) {
            SharedPreferences sp = getActivity().getSharedPreferences("appPref", Context.MODE_PRIVATE);
            if (chartData != null && !sp.getBoolean("pieDataChanged", false)){
                return chartData;
            }
            sp.edit().putBoolean("pieDataChanged", false).apply();
            chartData = dbHelper.getChartDataByMonthId(currentMonthId);
            return chartData;
        }


        @Override
        protected void onPostExecute(ChartData result) {
            super.onPostExecute(result);
            View view = getView();
            final TextView text = view.findViewById(R.id.MonthAndYear);
            PieChartView chart = view.findViewById(R.id.chart);
            PieChartData data = result.pieData;
            NonScrollableListView constLegend = view.findViewById(R.id.legendConstListView);
            NonScrollableListView tempLegend = view.findViewById(R.id.legendTmpListView);
            if (result.const_costs_list.isEmpty()){
                view.findViewById(R.id.textView).setVisibility(View.INVISIBLE);
                constLegend.setVisibility(View.INVISIBLE);
            } else {
                constLegend.setAdapter(result.const_costs_list);
                constLegend.setOnItemClickListener(this);
            }
            if (result.tmp_costs_list.isEmpty()){
                view.findViewById(R.id.textView3).setVisibility(View.INVISIBLE);
                tempLegend.setVisibility(View.INVISIBLE);
            } else {
                tempLegend.setAdapter(result.tmp_costs_list);
                tempLegend.setOnItemClickListener(this);
            }
            chart.setOnValueTouchListener(this);
            TextView balance = view.findViewById(R.id.textView2);
            balance.setText("Остаток: " + result.balance + "\u20BD");
            data.setHasCenterCircle(true).setCenterText1(result.costs + "\u20BD").setCenterText1FontSize(20);
            data.setCenterText2("Расходы").setCenterText2FontSize(10);
            chart.setPieChartData(data);
            chart.setChartRotationEnabled(false);
            text.setText(result.date);
        }

        @Override
        public void onValueSelected(int arcIndex, SliceValue value) {
            mListener.onSliceValueClicked(Integer.parseInt(String.valueOf(value.getLabelAsChars())), currentMonthId);
        }

        @Override
        public void onValueDeselected() {

        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            LegendRow row = (LegendRow) parent.getItemAtPosition(position);
            mListener.onSliceValueClicked(row.c_type, currentMonthId);
        }


    }

}
