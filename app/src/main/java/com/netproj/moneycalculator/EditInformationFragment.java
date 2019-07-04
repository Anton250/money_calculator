package com.netproj.moneycalculator;


import android.content.Context;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;


public class EditInformationFragment extends Fragment implements View.OnClickListener {

    private OnButtonClicked mListener;

    public EditInformationFragment() {
    }

    @Override
    public void onClick(View v) {
        mListener.onButtonClicked(v);
    }

    public interface OnButtonClicked {
        void onButtonClicked(View v);
    }

    public static EditInformationFragment newInstance() {
        return new EditInformationFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.window_edit, container, false);
        Button addCost = view.findViewById(R.id.buttonAddCost);
        Button addIncome = view.findViewById(R.id.buttonAddIncome);
        Button editCosts = view.findViewById(R.id.buttonEditCosts);
        Button editIncomes = view.findViewById(R.id.buttonEditIncomes);
        Button editTempCosts = view.findViewById(R.id.buttonEditCurrentCosts);
        Button editConstIncomes = view.findViewById(R.id.buttonEditCurrentIncomes);
        addCost.setOnClickListener(this);
        addIncome.setOnClickListener(this);
        editCosts.setOnClickListener(this);
        editIncomes.setOnClickListener(this);
        editTempCosts.setOnClickListener(this);
        editConstIncomes.setOnClickListener(this);
//        getActivity().getActionBar().setTitle(R.string.edit);
      //  BottomNavigationView bottom = getActivity().findViewById(R.id.bottomNavigationView);
    //    bottom.getMenu().getItem(2).setChecked(true);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnButtonClicked) {
            mListener = (OnButtonClicked) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement MyListFragment.OnItemSelectedListener");
        }
    }
}
