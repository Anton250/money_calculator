package com.netproj.moneycalculator;



import lecho.lib.hellocharts.model.PieChartData;

class ChartData {

    LegendRowAdapter tmp_costs_list;
    LegendRowAdapter const_costs_list;
    PieChartData pieData;
    int costs;
    int balance;
    String date;
    ChartData (LegendRowAdapter tmp, LegendRowAdapter const_cost, PieChartData pie, int cost, int bal, String date){
        this.tmp_costs_list = tmp;
        this.const_costs_list = const_cost;
        this.pieData = pie;
        this.costs = cost;
        this.balance = bal;
        this.date = date;

    }
}
