package com.netproj.moneycalculator;


import android.content.Context;
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

public class MonthFragment extends Fragment implements View.OnClickListener {

    //private View savedView;
    private int pageNumber;
    private int type;
    private DBHelper dbHelper;
    private AsyncView v;
    private OnSliceValueClicked mListener;
    private static final String ARGUMENT_PAGE_NUMBER = "page_number";
    private static final String ARGUMENT_TYPE = "type_number";

    @Override
    public void onClick(View v) {
        mListener.onButtonAddClicked(v, pageNumber + 1);
    }


    public interface OnSliceValueClicked{
        void onSliceValueClicked(int c_type, int id);
        void onButtonAddClicked(View v, int month_id);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSliceValueClicked) {
            mListener = (OnSliceValueClicked) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement MyListFragment.OnItemSelectedListener");
        }
    }


    public MonthFragment() {
    }

    static MonthFragment newInstance(int page, int type) {

        MonthFragment monthFragment = new MonthFragment();
        Bundle arguments = new Bundle();
        arguments.putInt(ARGUMENT_PAGE_NUMBER, page);
        arguments.putInt(ARGUMENT_TYPE, type);
        monthFragment.setArguments(arguments);
        return monthFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        pageNumber = getArguments().getInt(ARGUMENT_PAGE_NUMBER);
        type = getArguments().getInt(ARGUMENT_TYPE);
        dbHelper = new DBHelper(getContext());
    }

    @Override
    public void onPause(){
        v.cancel(true);
        Log.d("PAUSED", "NOW");
        super.onPause();
    }

    @Override
    public void onDestroy(){

        dbHelper.close();
        //dbHelper = null;
        //savedView = null;
        Log.d("DESTROYED", "NOW");
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
      //  SharedPreferences sp = getActivity().getSharedPreferences("appPref", Context.MODE_PRIVATE);
       // if (savedView != null && !sp.getBoolean("pieDataChanged", false)){
   //         return savedView;
     //   } else {
            View savedView = inflater.inflate(R.layout.fragment, null);
            Button btn = savedView.findViewById(R.id.btnShowIncomes);
            btn.setOnClickListener(this);
            v = new AsyncView();
            v.execute();
         //   sp.edit().putBoolean("pieDataChanged", false).apply();
            return savedView;
      //  }
    }





   private class AsyncView extends AsyncTask<Void, Void, ChartData> implements PieChartOnValueSelectListener, AdapterView.OnItemClickListener {




        @Override
        protected ChartData doInBackground(Void... voids) {

            return dbHelper.getChartDataByMonthId(pageNumber + 1);
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
           mListener.onSliceValueClicked(Integer.parseInt(String.valueOf(value.getLabelAsChars())), pageNumber + 1);
       }

       @Override
       public void onValueDeselected() {

       }

       @Override
       public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            LegendRow row = (LegendRow) parent.getItemAtPosition(position);
            mListener.onSliceValueClicked(row.c_type, pageNumber + 1);
       }
   }

}
