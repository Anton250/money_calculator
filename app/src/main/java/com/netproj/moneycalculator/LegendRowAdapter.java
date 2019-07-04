package com.netproj.moneycalculator;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;


class LegendRowAdapter extends ArrayAdapter<LegendRow> {

    LegendRowAdapter(Context context, int resource) {
        super(context, resource);
    }

    @Override
    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        LegendRow row = getItem(position);


        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.activity_list_item, null);
        }
        ((TextView) convertView.findViewById(android.R.id.text1))
                .setText(row.name);
        ((TextView) convertView.findViewById(android.R.id.text2))
                .setText(row.value);
        Bitmap img = Bitmap.createBitmap(24, 24, Bitmap.Config.ARGB_8888);
        //img = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.cicrle);
        img.eraseColor(Color.parseColor(row.image));
        ((ImageView) convertView.findViewById(android.R.id.icon)).setImageBitmap(img);
        return convertView;
    }
}
