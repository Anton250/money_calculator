package com.netproj.moneycalculator;



import android.app.SearchManager;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;


import com.google.android.material.bottomnavigation.BottomNavigationView;


public class MainActivity extends AppCompatActivity implements
        EditInformationFragment.OnButtonClicked,
        MonthFragment.OnSliceValueClicked,
        EditNewInformationFragment.OnValueEdit,
        MonthsListFragment.OnMonthClicked,
        NavController.OnDestinationChangedListener {



    DBHelper dbHelper;
    static String currentMonth = "CurrentMonth";
    boolean editVisible = false;
    Toolbar toolbar;
    BottomNavigationView navigation;
    NavController navController;
    NavOptions options;
    boolean searchVisible = false;
    boolean inCosts = true;
    boolean inMonths = false;
    boolean inEdit = false;



    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.edit_menu, menu);
        menu.setGroupEnabled(R.id.edit_values, true);
        menu.setGroupEnabled(R.id.search_value, true);


        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        menu.setGroupVisible(R.id.edit_values, editVisible);
        menu.setGroupVisible(R.id.search_value, searchVisible);
        return true;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = new DBHelper(this);
        SharedPreferences sp = getSharedPreferences("appPref", MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putInt(currentMonth, dbHelper.getCurrentMonthId() + 1);
        ed.apply();
        setContentView(R.layout.activity_main);
        navigation = findViewById(R.id.bottomNavigationView);
        navController = Navigation.findNavController(this, R.id.my_nav_host_fragment);
        navController.addOnDestinationChangedListener(this);
        toolbar = findViewById(R.id.toolbar);
        setTitle(R.string.costs);
        setSupportActionBar(toolbar);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(R.id.monthFragment, R.id.editInformationFragment,
                R.id.monthsListFragment).build();
        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navigation, navController);


        options = new NavOptions.Builder().setEnterAnim(R.anim.right_in)
                //.setExitAnim(R.anim.nav_default_exit_anim)
                //.setLaunchSingleTop(true)
                //.setPopEnterAnim(R.anim.left_in)
                .setPopExitAnim(R.anim.right_out)
                .build();


    }




    @Override
    public void onButtonClicked(View v) {
        switch (v.getId()){
            case R.id.buttonAddCost:{
                Bundle data = new Bundle();
                data.putInt("type", DBHelper.COSTS);
                navController.navigate(R.id.addNewInfoFragment, data);
                break;
            }
            case R.id.buttonAddIncome:{
                Bundle data = new Bundle();
                data.putInt("type", DBHelper.INCOME);
                navController.navigate(R.id.addNewInfoFragment, data);
                break;
            }
            case R.id.buttonEditCosts:{
                Bundle data = new Bundle();
                data.putInt("type", DBHelper.COSTS);
                data.putInt("c_type", 0);
                data.putInt("month_id", 0);
                data.putInt("subtype", DBHelper.CONST);
                navController.navigate(R.id.editNewInformationFragment, data);
                break;
            }
            case R.id.buttonEditIncomes:{
                Bundle data = new Bundle();
                data.putInt("type", DBHelper.INCOME);
                data.putInt("c_type", 0);
                data.putInt("month_id", 0);
                data.putInt("subtype", DBHelper.CONST);
                navController.navigate(R.id.editNewInformationFragment, data);
                break;
            }
            case R.id.buttonEditCurrentCosts:{
                Bundle data = new Bundle();
                data.putInt("type", DBHelper.COSTS);
                data.putInt("c_type", 0);
                data.putInt("month_id", 0);
                data.putInt("subtype", DBHelper.TEMP);
                navController.navigate(R.id.editNewInformationFragment, data);
                break;
            }
            case R.id.buttonEditCurrentIncomes:{
                Bundle data = new Bundle();
                data.putInt("type", DBHelper.INCOME);
                data.putInt("c_type", 0);
                data.putInt("month_id", 0);
                data.putInt("subtype", DBHelper.TEMP);
                navController.navigate(R.id.editNewInformationFragment, data);
                break;
            }
        }
    }

    @Override
    public void onSliceValueClicked(int c_type, int id) {
        Bundle data = new Bundle();
        data.putInt("type", 0);
        data.putInt("c_type", c_type);
        data.putInt("month_id", id);
        data.putInt("subtype", DBHelper.TEMP);
        navController.navigate(R.id.editNewInformationFragment, data, options);
    }

    @Override
    public void onButtonAddClicked(View v, int month_id) {
        switch (v.getId()){
            case R.id.btnAddCostInMonth:{
                Bundle data = new Bundle();
                data.putInt("type", DBHelper.COSTS);
                navController.navigate(R.id.addNewInfoFragment, data, options);
                break;
            }
            case R.id.btnShowIncomes:{
                Bundle data = new Bundle();
                data.putInt("type", DBHelper.INCOME);
                data.putInt("c_type", 0);
                data.putInt("month_id", month_id);
                data.putInt("subtype", DBHelper.TEMP);
                navController.navigate(R.id.editNewInformationFragment, data, options);
                break;
            }
        }

    }

    @Override
    public void onValueEdit(boolean isEdit) {
        toolbar.getMenu().setGroupVisible(R.id.edit_values, isEdit);
        editVisible = isEdit;
       // invalidateOptionsMenu();
    }

    @Override
    public void onShowSearch(boolean show) {
        searchVisible = show;
        invalidateOptionsMenu();
    }

    @Override
    public void onMonthClicked(int id) {
        Bundle data = new Bundle();
        data.putInt("month_id", id);

        navController.navigate(R.id.monthGraphShowFragment, data, options);
    }


    @Override
    public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
        int id = destination.getId();
        if(id == R.id.editNewInformationFragment ||
          id == R.id.monthGraphShowFragment || id == R.id.addNewInfoFragment) {

            navigation.setVisibility(View.GONE);
        } else {

            navigation.setVisibility(View.VISIBLE);
        }
        switch (id){
            case R.id.monthFragment:{
                    inCosts = true;
                    inEdit = false;
                    inMonths = false;

                break;
            }
            case R.id.monthsListFragment:{

                    inCosts = false;
                    inEdit = false;
                    inMonths = true;

                break;
            }
            case R.id.editInformationFragment:{

                    inCosts = false;
                    inEdit = true;
                    inMonths = false;


                break;
            }
            default:break;
        }
    }


}
