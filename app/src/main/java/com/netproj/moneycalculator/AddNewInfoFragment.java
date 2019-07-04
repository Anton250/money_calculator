package com.netproj.moneycalculator;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;


public class AddNewInfoFragment extends Fragment implements View.OnClickListener {

    private int type;

    private DBHelper dbHelper;

    private static String TYPE = "type";
   // private EditText value;
  //  private EditText comment;
    private Spinner spinnerType;
    private Spinner spinnerCType;
    private String toastText;
    private CheckBox checkBox;


    private boolean is_c_type_spinner_shown = true;

    public AddNewInfoFragment() {
    }



    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        type = getArguments().getInt(TYPE);
        dbHelper = new DBHelper(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_info, container, false);
        String barTitle;
        spinnerType = view.findViewById(R.id.spinnerType);
        spinnerCType = view.findViewById(R.id.spinnerCType);
        checkBox = view.findViewById(R.id.checkBoxAddToCurrentMonth);
        if (type == DBHelper.COSTS){
            toastText = "Успешно добавлен расход!";
            barTitle = getContext().getString(R.string.addCost);
            String[] typesTmp = getContext().getResources().getStringArray(R.array.TagsTempCosts);
            String[] typesConst = getContext().getResources().getStringArray(R.array.TagsConstCosts);
            final ArrayAdapter <String> adapterTmp = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, typesTmp);
            final ArrayAdapter <String> adapterConst = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, typesConst);
            spinnerCType.setAdapter(adapterTmp);
            spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (position == 0){
                        spinnerCType.setAdapter(adapterTmp);
                        checkBox.setVisibility(View.GONE);
                    } else if (position == 1){
                        spinnerCType.setAdapter(adapterConst);
                        checkBox.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        } else {
            toastText = "Успешно добавлен доход!";
            barTitle = getContext().getString(R.string.addIncome);
            String[] notCompleteTypes = getContext().getResources().getStringArray(R.array.TagsIncomes);
            String[] types = new String[4];
            for (int i = 0; i < 4; i++){
                types[i] = notCompleteTypes[i];
            }
            ArrayAdapter <String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, types);
            spinnerCType.setAdapter(adapter);
            spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (position == 0){
                        spinnerCType.setVisibility(View.GONE);
                        is_c_type_spinner_shown = false;
                        checkBox.setVisibility(View.GONE);
                    } else if (position == 1){
                        spinnerCType.setVisibility(View.VISIBLE);
                        is_c_type_spinner_shown = true;
                        checkBox.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
        Button add = view.findViewById(R.id.buttonAdd);
        add.setOnClickListener(this);
        getActivity().setTitle(barTitle);


        return view;
    }

    @Override
    public void onClick(View v) {
        EditText value = getActivity().findViewById(R.id.editTextValueInAdd);
        EditText comment = getActivity().findViewById(R.id.addNewComment);
        if (value.length() < 1 || value.getText().toString().startsWith("0")){
            return;
        }
        boolean confirmed;
        if (spinnerType.getSelectedItemPosition() == 0){
            if (is_c_type_spinner_shown){
                confirmed = dbHelper.addValueToMonth(dbHelper.getCurrentMonthId() + 1, type,
                        spinnerCType.getSelectedItemPosition(), comment.getText().toString(), Integer.parseInt(value.getText().toString()));

            } else {
                confirmed = dbHelper.addValueToMonth(dbHelper.getCurrentMonthId() + 1, type,
                        3, null, Integer.parseInt(value.getText().toString()));
            }
        } else {
            confirmed = dbHelper.addNewValueToUserData(type, spinnerCType.getSelectedItemPosition(), comment.getText().toString(), Integer.parseInt(value.getText().toString()),
                    checkBox.isChecked());
        }
        if(confirmed) {
            Toast.makeText(getContext(), toastText, Toast.LENGTH_SHORT).show();
        }
        value.clearFocus();
        comment.clearFocus();
        comment.setText("");
        value.setText("");

    }

    @Override
    public void onDestroy(){
        spinnerCType.setAdapter(null);
        spinnerType.setAdapter(null);
        dbHelper.close();
        checkBox = null;
        super.onDestroy();
    }
}
