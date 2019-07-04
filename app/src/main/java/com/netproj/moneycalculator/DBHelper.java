package com.netproj.moneycalculator;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;


public class DBHelper extends SQLiteOpenHelper {

    private static String DB_NAME = "MoneyCalculator";
    private static String MAIN_TABLE = "Month_table";
    private static String USER_DATA_TABLE = "user_data";
    private static final String KEY_ID = "_id";
    private static final String KEY_TABLE_NAME = "table_name";
    private static final String KEY_YEAR = "year";
    private static final String KEY_END_DATE = "end_date";
    static final int COSTS = 1;
    static final int INCOME = 2;
    static final int CONST = 1;
    static final int TEMP = 2;
    private static final String KEY_MONTH = "month";
    private static final String KEY_DATE = "date";
    private static final String KEY_BALANCE = "balance";
    private static final String KEY_VALUE = "value";
    private static final String KEY_TYPE = "type";
    private static final String KEY_C_TYPE = "c_type";
    private static final String KEY_COMMENT = "comment";
    private static final String KEY_COSTS = "costs";
    private static final int DATABASE_VERSION = 1;
    private SQLiteDatabase thisDataBase;
    private Context mcontext;

    DBHelper(Context context) {
        super(context, DB_NAME, null, DATABASE_VERSION);
        thisDataBase = this.getWritableDatabase();
        mcontext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + MAIN_TABLE + "(" + KEY_ID
                + " integer primary key," + KEY_TABLE_NAME + " text," + KEY_YEAR +
                " integer," + KEY_MONTH + " integer," + KEY_COSTS + " integer," + KEY_BALANCE + " integer)");
        db.execSQL("create table " + USER_DATA_TABLE + "(" + KEY_ID +
                " integer primary key," + KEY_TYPE + " interger," +
                KEY_COMMENT + " text," + KEY_C_TYPE + " integer," +
                KEY_VALUE + " integer)");
        Calendar cal = Calendar.getInstance();
        int month = cal.get(Calendar.MONTH);
        int year = cal.get(Calendar.YEAR);
        thisDataBase = db;
        createMonthTable(++month, year);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    @Override
    public synchronized void close(){
        thisDataBase.close();
        super.close();
    }

    private void createNewMonthTable(){
        Cursor cursor = thisDataBase.query(MAIN_TABLE, null, null,  null, null, null, null);
            cursor.moveToLast();
            int month = cursor.getInt(cursor.getColumnIndex(KEY_MONTH));
            int year = cursor.getInt(cursor.getColumnIndex(KEY_YEAR));
            if (month != 12){
                month++;
                createMonthTable(month, year);
            } else {
                year++;
                month = 1;
                createMonthTable(month, year);
            }
            cursor.close();

    }

    int getCountOfPages(){
        Cursor cursor = thisDataBase.query(MAIN_TABLE, null, null, null, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }


    boolean addValueToMonth(int id, int type, int c_type, String comment, int value){
        Cursor cursor = thisDataBase.query(MAIN_TABLE, new String[]{KEY_TABLE_NAME, KEY_BALANCE, KEY_COSTS},
                KEY_ID + "=" + id, null, null, null, null);
        cursor.moveToNext();
        if (type == COSTS){
            int balance = cursor.getInt(cursor.getColumnIndex(KEY_BALANCE));
            if (balance - value < 0){
                Toast.makeText(mcontext, "Ошибка! Расходы не могут превышать доходы!", Toast.LENGTH_LONG).show();
                return false;
            }
        }
        String tableName = cursor.getString(0);
        ContentValues val = new ContentValues();
        Calendar cal = Calendar.getInstance();
        val.put(KEY_TYPE, type);
        val.put(KEY_C_TYPE, c_type);
        val.put(KEY_VALUE, value);
        val.put(KEY_COMMENT, comment);
        val.put(KEY_DATE, cal.get(Calendar.DAY_OF_MONTH) + "/" + (cal.get(Calendar.MONTH) + 1));
        thisDataBase.insert("\"" + tableName + "\"", null, val);
        cursor.close();
        updateBalance(id);
        return true;
    }


    boolean addNewValueToUserData (int type, int c_type, String comment, int value, boolean addToMonth) {
        if (type == COSTS) {
            Cursor cursor = thisDataBase.rawQuery("select " + KEY_TYPE + ", sum(" + KEY_VALUE + ") as sum from " +
                    USER_DATA_TABLE + " group by " + KEY_TYPE, null);
            if(!cursor.moveToNext()){
                Toast.makeText(mcontext, "Ошибка! Для начала необходимо ввести доходы!", Toast.LENGTH_LONG).show();
                return false;
            }
            int key_type = cursor.getColumnIndex(KEY_TYPE);
            int key_sum = cursor.getColumnIndex("sum");
            int incomes, costs;
            if (cursor.getInt(key_type) == COSTS) {
                costs = cursor.getInt(key_sum);
                cursor.moveToNext();
                incomes = cursor.getInt(key_sum);
            } else {
                incomes = cursor.getInt(key_sum);
                if (!cursor.moveToNext()){
                    costs = value;
                } else {
                    costs = cursor.getInt(key_sum);
                }

            }
            cursor.close();
            if ((incomes - value) < costs) {
                Toast.makeText(mcontext, "Ошибка! Расходы не могут быть меньше доходов!", Toast.LENGTH_LONG).show();
                return false;
            }
        }
        ContentValues val = new ContentValues();
        if (type == COSTS){
            c_type += 12;
        }
        val.put(KEY_TYPE, type);
        val.put(KEY_C_TYPE, c_type);
        val.put(KEY_COMMENT, comment);
        val.put(KEY_VALUE, value);
        thisDataBase.insert(USER_DATA_TABLE, null, val);
        if (addToMonth) {
            addValueToMonth(getCurrentMonthId() + 1, type, c_type, comment, value);
        }
        return true;
    }

    private void updateBalance (int month_id){
        Cursor cursor = thisDataBase.query(MAIN_TABLE, new String[]{KEY_TABLE_NAME}, KEY_ID + "=" + month_id, null, null, null, null);
        cursor.moveToNext();
        String tableName = cursor.getString(0);
        cursor.close();
        cursor = thisDataBase.rawQuery("select " + KEY_TYPE + ", sum(" + KEY_VALUE + ") as sum from \""
                + tableName + "\" group by " + KEY_TYPE, null);
        cursor.moveToNext();
        ContentValues val = new ContentValues();
        int balance;
        if (cursor.getInt(cursor.getColumnIndex(KEY_TYPE)) == 1){
            int costs = cursor.getInt(cursor.getColumnIndex("sum"));
            if (!cursor.moveToNext()){
                val.put(KEY_COSTS, 0);
                Log.d("UPDATED", "");
                thisDataBase.update(MAIN_TABLE, val, KEY_ID + "=" + month_id, null);
                return;
            }
            int incomes = cursor.getInt(cursor.getColumnIndex("sum"));
            balance = incomes - costs;
            val.put(KEY_COSTS, costs);
        } else {
            balance = cursor.getInt(cursor.getColumnIndex("sum"));
            val.put(KEY_COSTS, 0);
        }

        val.put(KEY_BALANCE, balance);
        thisDataBase.update(MAIN_TABLE, val, KEY_ID + "=" + month_id, null);
        cursor.close();
        SharedPreferences sp = mcontext.getSharedPreferences("appPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putBoolean("pieDataChanged", true);
        ed.apply();

    }

    private String getDate(int id){
        Cursor cursor = thisDataBase.query(MAIN_TABLE, new String[]{KEY_MONTH, KEY_YEAR}, KEY_ID + "=" + id, null, null, null, null);
        cursor.moveToNext();
        String[] str = mcontext.getResources().getStringArray(R.array.months);
        int month = cursor.getInt(cursor.getColumnIndex(KEY_MONTH));
        int year = cursor.getInt(cursor.getColumnIndex(KEY_YEAR));
        cursor.close();
        return str[--month] + " " + year;
    }

    int getCurrentMonthId(){
        Calendar cal = Calendar.getInstance();
        int month = cal.get(Calendar.MONTH);
        int year = cal.get(Calendar.YEAR);
        Cursor cursor = thisDataBase.query(MAIN_TABLE, new String[]{KEY_ID}, KEY_MONTH + "=" + ++month + " AND " + KEY_YEAR + "=" + year,
                null, null, null, null);
        if (!cursor.moveToNext()){
            createNewMonthTable();
            cursor.close();
            return getCurrentMonthId();
        } else {
            int id = cursor.getInt(0);
            cursor.close();
            return --id;
        }
    }

    ChartData getChartDataByMonthId (int id){
        List <SliceValue> pieData = new ArrayList<>();
        LegendRowAdapter tmp_costs = new LegendRowAdapter(mcontext, R.layout.activity_list_item);
        LegendRowAdapter const_costs = new LegendRowAdapter(mcontext, R.layout.activity_list_item);
        String[] colors_tmp = mcontext.getResources().getStringArray(R.array.ColorsTempCosts);
        String[] colors_const = mcontext.getResources().getStringArray(R.array.ColorsConstCosts);
        String[] strings_tmp = mcontext.getResources().getStringArray(R.array.TagsTempCosts);
        String[] strings_const = mcontext.getResources().getStringArray(R.array.TagsConstCosts);
        Cursor mcursor = thisDataBase.query(MAIN_TABLE, new String[]{KEY_TABLE_NAME, KEY_COSTS, KEY_BALANCE}, KEY_ID + "=" + id,
                null, null, null, null);
        mcursor.moveToNext();
        String tableName = mcursor.getString(0);
        int balance = mcursor.getInt(2);
        int costs = mcursor.getInt(1);
        mcursor.close();
        Cursor cursor = thisDataBase.rawQuery("select " + KEY_C_TYPE + ", sum(" + KEY_VALUE + ") as sum from \"" +
                tableName + "\" where " + KEY_TYPE + "=" + COSTS + " group by " + KEY_C_TYPE, null);
        int c_type = cursor.getColumnIndex(KEY_C_TYPE);
        int sum = cursor.getColumnIndex("sum");
        while (cursor.moveToNext()){
            if(cursor.getInt(c_type) >= 12){
                int type = cursor.getInt(c_type);
                pieData.add(new SliceValue(cursor.getInt(sum),
                        Color.parseColor(colors_const[type - 12])).setLabel("" + type));
                const_costs.add(new LegendRow(strings_const[type - 12], colors_const[type - 12], cursor.getInt(sum), type));

            } else {
                int type = cursor.getInt(c_type);
                pieData.add(new SliceValue(cursor.getInt(sum),
                        Color.parseColor(colors_tmp[type])).setLabel("" + type));
                tmp_costs.add(new LegendRow(strings_tmp[type], colors_tmp[type], cursor.getInt(sum), type));
            }
        }
        cursor.close();
        return new ChartData(tmp_costs, const_costs, new PieChartData(pieData), costs, balance, getDate(id));
     }


     ValuesRowAdapter getItemsToEditByType(int type, int tableType, int month_id){
        List <ValuesRow> list = new ArrayList<>();
        if (tableType == TEMP){
            String[] c_types;
            if (type == COSTS){
                c_types = mcontext.getResources().getStringArray(R.array.allCosts);
            } else {
                c_types = mcontext.getResources().getStringArray(R.array.TagsIncomes);
            }
            String tableName;
            if (month_id == 0) {
                tableName = getMonthTableName(getCurrentMonthId() + 1);
            } else {
                tableName = getMonthTableName(month_id);
            }
            Cursor cursor = thisDataBase.query(tableName, new String[]{KEY_ID, KEY_DATE, KEY_C_TYPE, KEY_COMMENT, KEY_VALUE},
                    KEY_TYPE + "=" + type, null, null, null, null);
            int date = cursor.getColumnIndex(KEY_DATE);
            int c_type = cursor.getColumnIndex(KEY_C_TYPE);
            int comment = cursor.getColumnIndex(KEY_COMMENT);
            int value = cursor.getColumnIndex(KEY_VALUE);
            int id = cursor.getColumnIndex(KEY_ID);
            if(!cursor.moveToLast()){
                return null;
            }
            int cur_c_type = cursor.getInt(c_type);
          //  if (cur_c_type >= 12){
           //     cur_c_type -= 12;
          //  }
            list.add(new ValuesRow(cursor.getString(date) + "", c_types[cur_c_type] + "", cursor.getString(comment) + "",
                    cursor.getInt(value), cursor.getInt(id)));
            while (cursor.moveToPrevious()){
                cur_c_type = cursor.getInt(c_type);
            //    if (cur_c_type >= 12){
            //        cur_c_type -= 12;
            //    }
                list.add(new ValuesRow(cursor.getString(date) + "", c_types[cur_c_type] + "", cursor.getString(comment) + "",
                        cursor.getInt(value), cursor.getInt(id)));
            }
            cursor.close();

        } else {
            Cursor cursorMain = thisDataBase.query(USER_DATA_TABLE, new String[]{KEY_ID, KEY_C_TYPE, KEY_COMMENT, KEY_VALUE},
                    KEY_TYPE + "=" + type, null, null, null, null);
            int c_type_id = cursorMain.getColumnIndex(KEY_C_TYPE);
            int comment = cursorMain.getColumnIndex(KEY_COMMENT);
            int value = cursorMain.getColumnIndex(KEY_VALUE);
            int id = cursorMain.getColumnIndex(KEY_ID);
            String[] c_types;
            if (type == COSTS){
                c_types = mcontext.getResources().getStringArray(R.array.TagsConstCosts);
            } else {
                c_types = mcontext.getResources().getStringArray(R.array.TagsIncomes);
            }
            if (!cursorMain.moveToLast()){
                return null;
            }
            int c_type = cursorMain.getInt(c_type_id);
            if (type == COSTS){
                c_type -= 12;
            }
            list.add(new ValuesRow("", c_types[c_type], cursorMain.getString(comment) + "",
                    cursorMain.getInt(value), cursorMain.getInt(id)));
            while (cursorMain.moveToPrevious()){
                c_type = cursorMain.getInt(c_type_id);
                if (type == COSTS){
                    c_type -= 12;
                }
                list.add(new ValuesRow("", c_types[c_type], cursorMain.getString(comment) + "",
                        cursorMain.getInt(value), cursorMain.getInt(id)));
            }
            cursorMain.close();
        }
         ValuesRowAdapter adapter = new ValuesRowAdapter(mcontext, R.layout.row_item, list);
        return adapter;
     }

     ArrayAdapter <String> getMonths(){

        Cursor cursor = thisDataBase.query(MAIN_TABLE, new String[]{KEY_MONTH, KEY_YEAR}, null, null, null, null, null);
        int count = cursor.getCount();
        int i = 0;
        String[] mothsLetters = mcontext.getResources().getStringArray(R.array.months);
        String[] months = new String[count];
        int month_index = cursor.getColumnIndex(KEY_MONTH);
        int year_index = cursor.getColumnIndex(KEY_YEAR);
        while (cursor.moveToNext()){
            int month_id = cursor.getInt(month_index);
            months[i] = mothsLetters[--month_id] + " " + cursor.getInt(year_index);
            i++;
        }
        cursor.close();
        return new ArrayAdapter<>(mcontext, R.layout.simple_list_item, months);
    }

    ValuesRowAdapter getItemsToEditByCType(int c_type, int month_id){
        List <ValuesRow> list = new ArrayList<>();
        ValuesRowAdapter adapter;


        String tableName = getMonthTableName(month_id);
        Cursor cursor = thisDataBase.query(tableName, new String[]{KEY_ID, KEY_DATE, KEY_COMMENT, KEY_VALUE},
                KEY_C_TYPE + "=" + c_type + " and " + KEY_TYPE + "=" + COSTS, null, null, null, null);
        int date = cursor.getColumnIndex(KEY_DATE);
        int comment = cursor.getColumnIndex(KEY_COMMENT);
        int value = cursor.getColumnIndex(KEY_VALUE);
        int id = cursor.getColumnIndex(KEY_ID); cursor.moveToLast();
        list.add(new ValuesRow(cursor.getString(date) + "", "", cursor.getString(comment) + "",
                cursor.getInt(value), cursor.getInt(id)));
        while (cursor.moveToPrevious()){
            list.add(new ValuesRow(cursor.getString(date) + "", "", cursor.getString(comment) + "",
                    cursor.getInt(value), cursor.getInt(id)));
        }

        cursor.close();
        SharedPreferences sp = mcontext.getSharedPreferences("appPref", Context.MODE_PRIVATE);
        if (month_id == sp.getInt(MainActivity.currentMonth, 0)){
            adapter = new ValuesRowAdapter(mcontext, R.layout.row_item, list);
            Log.d("DB", "Correct");
        } else {
            Log.d("DB", "incorrect");
            adapter = new ValuesRowAdapter(mcontext, R.layout.unactive_items_row, list);
        }
        return adapter;
    }

    boolean deleteValue(int month_id, int id, int type, int subtype, int oldVal){
        if(subtype == TEMP){
            Cursor cursor = thisDataBase.query(MAIN_TABLE, new String[]{KEY_TABLE_NAME, KEY_BALANCE}, KEY_ID + "=" + month_id,
                    null, null, null, null);
            cursor.moveToNext();
            int balance = cursor.getInt(cursor.getColumnIndex(KEY_BALANCE));
            if (type == INCOME && (balance - oldVal) < 0) {
                Toast.makeText(mcontext, "Ошибка! Расходы не могут быть меньше доходов!", Toast.LENGTH_LONG).show();
                return false;
            }
            String tableName = "\"" + cursor.getString(cursor.getColumnIndex(KEY_TABLE_NAME)) + "\"";
            thisDataBase.delete(tableName, KEY_ID + "=" + id, null);
            Toast.makeText(mcontext, "Успешно", Toast.LENGTH_SHORT).show();
            cursor.close();
            updateBalance(month_id);
            return true;
        } else {
            if (type == INCOME){
                Cursor cursor = thisDataBase.rawQuery("select " + KEY_TYPE + ", sum(" + KEY_VALUE + ") as sum from " +
                        USER_DATA_TABLE + " group by " + KEY_TYPE, null);
                cursor.moveToNext();
                int key_type = cursor.getColumnIndex(KEY_TYPE);
                int key_sum = cursor.getColumnIndex("sum");
                int incomes, costs;
                if (cursor.getInt(key_type) == COSTS){
                    costs = cursor.getInt(key_sum);
                    cursor.moveToNext();
                    incomes = cursor.getInt(key_sum);
                } else {
                    incomes = cursor.getInt(key_sum);
                    cursor.moveToNext();
                    costs = cursor.getInt(key_sum);
                }
                cursor.close();
                if (incomes - oldVal < costs) {
                    Toast.makeText(mcontext, "Ошибка! Расходы не могут быть меньше доходов!", Toast.LENGTH_LONG).show();
                    return false;
                }
            }
            thisDataBase.delete(USER_DATA_TABLE, KEY_ID + "=" + id, null);
            Toast.makeText(mcontext, "Успешно", Toast.LENGTH_SHORT).show();
            return true;
        }
    }

    boolean updateValue (int month_id, int id, int type, int subtype, int oldVal, int newVal, String newComment){
        if(subtype == TEMP){
            Cursor cursor = thisDataBase.query(MAIN_TABLE, new String[]{KEY_TABLE_NAME, KEY_BALANCE}, KEY_ID + "=" + month_id,
                    null, null, null, null);
            cursor.moveToNext();
            int balance = cursor.getInt(cursor.getColumnIndex(KEY_BALANCE));
            if ((type == INCOME && (((balance - oldVal) + newVal) < 0)) || (type == COSTS && (balance + oldVal) - newVal < 0)) {
                Toast.makeText(mcontext, "Ошибка! Расходы не могут быть меньше доходов!", Toast.LENGTH_LONG).show();
                return false;
            }
            String tableName = "\"" + cursor.getString(cursor.getColumnIndex(KEY_TABLE_NAME)) + "\"";
            ContentValues val = new ContentValues();
            val.put(KEY_COMMENT, newComment);
            val.put(KEY_VALUE, newVal);
            thisDataBase.update(tableName, val,KEY_ID + "=" + id, null);
            Toast.makeText(mcontext, "Успешно", Toast.LENGTH_SHORT).show();
            cursor.close();
            updateBalance(month_id);
            return true;
        } else {

            Cursor cursor = thisDataBase.rawQuery("select " + KEY_TYPE + ", sum(" + KEY_VALUE + ") as sum from " +
                    USER_DATA_TABLE + " group by " + KEY_TYPE, null);
            cursor.moveToNext();
            int key_type = cursor.getColumnIndex(KEY_TYPE);
            int key_sum = cursor.getColumnIndex("sum");
            int incomes, costs;
            if (cursor.getInt(key_type) == COSTS){
                costs = cursor.getInt(key_sum);
                cursor.moveToNext();
                incomes = cursor.getInt(key_sum);
            } else {
                incomes = cursor.getInt(key_sum);
                cursor.moveToNext();
                costs = cursor.getInt(key_sum);
            }
            cursor.close();
            if ((type == INCOME && (incomes - oldVal) + newVal < costs) || (type == COSTS && (incomes + oldVal) - newVal < costs)) {
                Toast.makeText(mcontext, "Ошибка! Расходы не могут быть меньше доходов!", Toast.LENGTH_LONG).show();
                return false;
            }

            ContentValues val = new ContentValues();
            val.put(KEY_COMMENT, newComment);
            val.put(KEY_VALUE, newVal);
            thisDataBase.update(USER_DATA_TABLE, val, KEY_ID + "=" + id, null);
            Toast.makeText(mcontext, "Успешно", Toast.LENGTH_SHORT).show();
            return true;
        }
    }


    private String getMonthTableName(int id){
        Cursor cursor = thisDataBase.query(MAIN_TABLE, new String[]{KEY_ID, KEY_TABLE_NAME}, KEY_ID + "=" + id,
                null, null, null, null);
        int id_tableName = cursor.getColumnIndex(KEY_TABLE_NAME);
        cursor.moveToNext();
        String tableName = "\"" + cursor.getString(id_tableName) + "\"";
        cursor.close();
        return tableName;
    }


    private void createMonthTable(int month, int year){
        Cursor getBalance = thisDataBase.query(MAIN_TABLE, new String[]{KEY_ID, KEY_BALANCE}, null, null,
                null, null, null);
        ContentValues val = new ContentValues();
        int id = 0;
        String tablename = String.valueOf(month) + String.valueOf(year);
        thisDataBase.execSQL("create table \"" + tablename + "\"(" + KEY_ID
                + " integer primary key," + KEY_TYPE + " integer," + KEY_DATE + " text," +
                KEY_C_TYPE + " integer," + KEY_COMMENT + " text," + KEY_VALUE + " integer)");


        val.put(KEY_MONTH, month);
        val.put(KEY_YEAR, year);
        val.put(KEY_TABLE_NAME, "" + month + year);
        if(getBalance.moveToLast()){
            id = getBalance.getInt(getBalance.getColumnIndex(KEY_ID));
            ContentValues values = new ContentValues();
            values.put(KEY_TYPE, INCOME);
            values.put(KEY_C_TYPE, 4);
            values.put(KEY_VALUE, getBalance.getInt(getBalance.getColumnIndex(KEY_BALANCE)));//removed date
            thisDataBase.insert("\"" + tablename + "\"", null, values);
        }
        getBalance.close();

        Cursor cursor = thisDataBase.query(USER_DATA_TABLE, new String[]{KEY_ID, KEY_C_TYPE, KEY_VALUE, KEY_TYPE, KEY_COMMENT},
                null, null, null, null, null);
        int c_type = cursor.getColumnIndex(KEY_C_TYPE);
        int value = cursor.getColumnIndex(KEY_VALUE);
        int type = cursor.getColumnIndex(KEY_TYPE);
        int comment = cursor.getColumnIndex(KEY_COMMENT);
        while (cursor.moveToNext()){
            ContentValues values = new ContentValues();
            values.put(KEY_DATE, "1/" + ++month);
            values.put(KEY_COMMENT, cursor.getString(comment));
            values.put(KEY_TYPE, cursor.getInt(type));
            values.put(KEY_C_TYPE, cursor.getInt(c_type));
            values.put(KEY_VALUE, cursor.getInt(value));//removed date
            thisDataBase.insert("\"" + tablename + "\"", null, values);
        }
        cursor.close();
        thisDataBase.insert(MAIN_TABLE, null, val);
        if (id != 0){
            updateBalance(id + 1);
        }



    }

}
