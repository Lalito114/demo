package com.szzcs.smartpos.Cortes2;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.szzcs.smartpos.Cortes2.Lecturas;
import com.szzcs.smartpos.R;

import java.util.List;

public class ListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] maintitle;
    private final String[] subtitle;
    private final String[] nomanguera;


    public ListAdapter(Lecturas context, List<String> maintitle, List<String> subtitle, List<String> nomanguera1) {
        super((Context) context, R.layout.list, maintitle);
        // TODO Auto-generated constructor stub

        this.context= (Activity) context;
        this.maintitle= maintitle.toArray(new String[0]);
        this.subtitle= subtitle.toArray(new String[0]);
        this.nomanguera= nomanguera1.toArray(new String[0]);

    }


    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.mylist1, null,true);

        TextView titleText = (TextView) rowView.findViewById(R.id.title);
        TextView manguera = (TextView) rowView.findViewById(R.id.manguera);
        TextView subtitleText = (TextView) rowView.findViewById(R.id.subtitle);

        titleText.setText(maintitle[position]);
        manguera.setText(nomanguera[position]);
        subtitleText.setText(subtitle[position]);

        return rowView;

    };


}
