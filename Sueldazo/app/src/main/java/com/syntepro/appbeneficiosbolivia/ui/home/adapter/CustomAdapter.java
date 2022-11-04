package com.syntepro.sueldazo.ui.home.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.syntepro.sueldazo.R;

import java.util.ArrayList;

public class CustomAdapter extends BaseAdapter {

    private final int[] flags;
    private final ArrayList<String> countryNames;
    private final LayoutInflater inflater;

    public CustomAdapter(Context applicationContext, int[] flags, ArrayList<String> countryNames) {
        this.flags = flags;
        this.countryNames = countryNames;
        inflater = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return flags.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @SuppressLint({"ViewHolder", "InflateParams"})
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.custom_countryspinner_items, null);
        ImageView icon = view.findViewById(R.id.imageView);
        TextView names = view.findViewById(R.id.textView);
        try {
            icon.setImageResource(flags[i]);
            names.setText(countryNames.get(i));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

}
