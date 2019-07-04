package com.netproj.moneycalculator;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;



public class ValuesRowAdapter extends ArrayAdapter<ValuesRow> implements Filterable {

    private int res;
    private List<ValuesRow> list;
    private List<ValuesRow> filteredList;


    ValuesRowAdapter(Context context, int resource, List<ValuesRow> list) {
        super(context, resource);
        res = resource;
        this.list = list;
        this.filteredList = list;
    }




    @Override
    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        ValuesRow row = filteredList.get(position);


        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(res, null);
        }
        ((TextView) convertView.findViewById(R.id.item_text1))
                .setText(row.date);
        ((TextView) convertView.findViewById(R.id.item_text2)).setText(row.c_type);
        if (res == R.layout.unactive_items_row){
            ((TextView) convertView.findViewById(R.id.item_text3)).setText(row.comment);
            ((TextView) convertView.findViewById(R.id.item_text4)).setText(row.value);
        } else {
            ((EditText) convertView.findViewById(R.id.item_text3)).setText(row.comment);
            ((EditText) convertView.findViewById(R.id.item_text4)).setText(row.value);
            ((TextView) convertView.findViewById(R.id.item_text5)).setText(row.comment);
            ((TextView) convertView.findViewById(R.id.item_text6)).setText(row.value);
        }


        return convertView;
    }


    @Override
    public Filter getFilter(){

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charString = constraint.toString();
                Log.d("INFILTER", "WORK");
                if (charString.isEmpty()){
                    filteredList = list;
                } else {
                    List<ValuesRow> tmpFilteredList = new ArrayList<>();
                    for (ValuesRow row : list){

                        if(row.getComment().toLowerCase().contains(charString.toLowerCase()) ||
                                row.getC_type().toLowerCase().contains(charString.toLowerCase()) ||
                                row.getDate().contains(charString)){
                            Log.d("INROW", row.getComment());
                            tmpFilteredList.add(row);
                        }
                    }
                    filteredList = tmpFilteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredList = (ArrayList<ValuesRow>) results.values;
                notifyDataSetChanged();

            }
        };
    }



    @Override
    public int getCount(){
        return filteredList.size();
    }

    @Override
    public ValuesRow getItem(int position){
        return filteredList.get(position);
    }

    @Override
    public long getItemId(int position){
        return 0;
    }


    @Override
    public void remove(ValuesRow obj){
        filteredList.remove(obj);
        list.remove(obj);
        notifyDataSetChanged();
    }

    void setNewInfoToList(int position, String comment, int value){
        int id = list.indexOf(filteredList.get(position));
        filteredList.get(position).setValues(comment, value);
        list.get(id).setValues(comment, value);
        notifyDataSetChanged();
    }

}
