package com.netproj.moneycalculator;


import androidx.annotation.NonNull;

class LegendRow {
    final int c_type;
    final String name;
    final String image;
    final String value;

    LegendRow(@NonNull String name, @NonNull String image, int value, int c_type) {
        this.name = name;
        this.value = value + "\u20BD";
        this.image = image;
        this.c_type = c_type;
    }
}
