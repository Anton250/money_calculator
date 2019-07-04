package com.netproj.moneycalculator;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;



public class EditNewInformationFragment extends Fragment implements DialogInterface.OnClickListener,
        Toolbar.OnMenuItemClickListener {

    private int type;
    private int c_type;
    private int month_id;
    private DBHelper dbHelper;
    private OnValueEdit editListener;
    //private View savedView;
    private AlertDialog dialog;
    private int currentSubType;
    private AsyncUpdate v;
    private int currentPosition;
    private boolean isEditing = false;


    private String oldComment;
    private int oldValue;
    private int chosenID;
    private Toolbar bar;
    private View chosenView;
    private TextView textComment;
    private TextView textVal;
    private ListView list;
    private ValuesRow row;
    private ValuesRowAdapter mAdapter;
    private static String TYPE = "type";
    private static String C_TYPE = "c_type";
    private static String ID = "month_id";
    private static String SUB_TYPE = "subtype";



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu, inflater);
       // inflater.inflate(R.menu.edit_menu, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
        MenuItemCompat.setActionView(item, searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                ValuesRowAdapter adapter = (ValuesRowAdapter) list.getAdapter();
                adapter.getFilter().filter(query);
                //adapter.notifyDataSetChanged();
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                if (isEditing){
                    endEdit(false);
                }
                ValuesRowAdapter adapter = (ValuesRowAdapter) list.getAdapter();
                adapter.getFilter().filter(newText);
                //adapter.notifyDataSetChanged();
                return true;
            }
        });
       // this.menu = menu;
    }


    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == 0){
            EditText editComment = chosenView.findViewById(R.id.item_text3);
            EditText editVal = chosenView.findViewById(R.id.item_text4);
            textComment = chosenView.findViewById(R.id.item_text5);
            textVal = chosenView.findViewById(R.id.item_text6);
            textComment.setVisibility(View.GONE);
            textVal.setVisibility(View.GONE);
            editComment.setVisibility(View.VISIBLE);
            editVal.setVisibility(View.VISIBLE);
            oldComment = editComment.getText().toString();
            oldValue = Integer.parseInt(editVal.getText().toString());
            editComment.requestFocus();
            list.setEnabled(false);
            editListener.onValueEdit(true);
            //editListener.onShowSearch(false);
            isEditing = true;
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        } else {
            int thisType;
            if (type == 0){
                thisType = 1;
            } else {
                thisType = type;
            }
            EditText editVal = chosenView.findViewById(R.id.item_text4);
            oldValue = Integer.parseInt(editVal.getText().toString());
            if (dbHelper.deleteValue(month_id, chosenID, thisType, currentSubType, oldValue)){
                ValuesRowAdapter ad = (ValuesRowAdapter) list.getAdapter();
                ad.remove(row);
                ad.notifyDataSetChanged();
            }

        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_confirm:{

                startEdit();
                return true;
            }
            case R.id.action_cancel:{
                endEdit(false);
                return true;
            }
            case R.id.action_search:{
               // startSearch();
            }
        }

        return true;
    }



    public interface OnValueEdit{
        void onValueEdit(boolean isEdit);
        void onShowSearch(boolean show);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnValueEdit) {
            editListener = (OnValueEdit) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement MyListFragment.OnItemSelectedListener");
        }
    }

    public EditNewInformationFragment(){

    }



    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        type = getArguments().getInt(TYPE);
        setHasOptionsMenu(true);
        c_type = getArguments().getInt(C_TYPE);
        month_id = getArguments().getInt(ID);
        currentSubType = getArguments().getInt(SUB_TYPE);
        dbHelper = new DBHelper(getContext());
        if (month_id == 0){
            month_id = dbHelper.getCurrentMonthId() + 1;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Выберите действие");
        builder.setCancelable(true);
        final String[] actions ={"Редактировть", "Удалить"};
        builder.setItems(actions, this);
        dialog = builder.create();
        editListener.onShowSearch(true);
        bar = getActivity().findViewById(R.id.toolbar);
        bar.setOnMenuItemClickListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View savedView = inflater.inflate(R.layout.list_view, container, false);
        if (type != 0){
            String barTitle;

            if (type == DBHelper.COSTS){
                if (currentSubType == DBHelper.CONST){
                    barTitle = getContext().getString(R.string.editConstCost);
                } else {
                    barTitle = getContext().getString(R.string.editTempCosts);
                }
            } else {
                if (currentSubType == DBHelper.CONST){
                    barTitle = getContext().getString(R.string.editConstIncome);
                } else {
                    barTitle = getContext().getString(R.string.editTempIncome);
                }
            }

            bar.setTitle(barTitle);

        } else {
            String[] strings = getActivity().getResources().getStringArray(R.array.allCosts);

            bar.setTitle(strings[c_type]);

        }
        v = new AsyncUpdate();
        v.execute();
        return savedView;
    }

    @Override
    public void onPause(){
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
        editListener.onValueEdit(false);
        v.cancel(true);
        bar.setOnMenuItemClickListener(null);
        chosenView = null;
        editListener.onShowSearch(false);
        super.onPause();
    }


    private class AsyncUpdate extends AsyncTask<Void, Void, ValuesRowAdapter> implements AdapterView.OnItemClickListener {

        @Override
        protected ValuesRowAdapter doInBackground(Void... voids) {

            if (type != 0){
                mAdapter = dbHelper.getItemsToEditByType(type, currentSubType, month_id);
            } else {
                mAdapter = dbHelper.getItemsToEditByCType(c_type, month_id);
            }
            return mAdapter;
        }
        @Override
        protected void onPostExecute(ValuesRowAdapter result) {

            list = getView().findViewById(R.id.months_list);
            list.setAdapter(result);
            SharedPreferences sp = getActivity().getApplicationContext().getSharedPreferences("appPref", Context.MODE_PRIVATE);
            Log.d("MONTH_ID", "" + month_id);
            if (month_id != sp.getInt(MainActivity.currentMonth, 0)){
                list.setSelector(R.color.none);
            } else {
                list.setOnItemClickListener(this);
            }


        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ValuesRowAdapter adapter = (ValuesRowAdapter) list.getAdapter();
            row = adapter.getItem(position);
            chosenID = row.id;
            currentPosition = position;
            if (type == DBHelper.INCOME && currentSubType == DBHelper.TEMP && chosenID == 1){
                return;
            }
            chosenView = view;
            dialog.show();
        }
    }

    private void startEdit(){
        int thisType;
        EditText editVal = chosenView.findViewById(R.id.item_text4);
        ValuesRowAdapter adapter = (ValuesRowAdapter) list.getAdapter();
        if (type == 0){
            thisType = 1;
        } else {
            thisType = type;
        }
        if (editVal.length() < 1 || editVal.getText().toString().startsWith("0")){
            return;
        }
        int newVal = Integer.parseInt(editVal.getText().toString());
        EditText editComment = chosenView.findViewById(R.id.item_text3);
        String newComment = editComment.getText().toString();
        if(dbHelper.updateValue(month_id, chosenID, thisType, currentSubType, oldValue, newVal, newComment)){

            adapter.setNewInfoToList(currentPosition, newComment, newVal);
            endEdit(true);
        }
    }

    private void endEdit(boolean isEdited){
        EditText editVal = chosenView.findViewById(R.id.item_text4);
        EditText editComment = chosenView.findViewById(R.id.item_text3);
        if (!isEdited){
            editComment.setText(oldComment);
            editVal.setText(String.valueOf(oldValue));
        } else {
            textComment.setText(editComment.getText().toString());
            textVal.setText(editVal.getText().toString());
        }
        editVal.setVisibility(View.GONE);
        editComment.setVisibility(View.GONE);
        textComment.setVisibility(View.VISIBLE);
        textVal.setVisibility(View.VISIBLE);

        editListener.onValueEdit(false);
       // editListener.onShowSearch(true);
        list.setEnabled(true);
        isEditing = false;
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);

    }

    @Override
    public void onDestroy(){
        dbHelper.close();
        editListener.onValueEdit(false);
        list.setAdapter(null);
        super.onDestroy();
    }
}
