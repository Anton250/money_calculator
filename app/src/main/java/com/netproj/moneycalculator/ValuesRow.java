package com.netproj.moneycalculator;

class ValuesRow {
    final String date;
    final String c_type;
    String comment;
    String value;
    final int id;

    ValuesRow (String date, String c_type, String comment, int value, int id){
        this.date = date;
        this.c_type = c_type;
        this.comment = comment;
        this.value = value + "";
        this.id = id;
    }

    String getComment(){
        return this.comment;
    }

    String getC_type(){
        return this.c_type;
    }

    String getDate(){
        return this.date;
    }

    void setValues(String newComment, int newValue){
        this.comment = newComment;
        this.value = newValue + "";
    }
}
