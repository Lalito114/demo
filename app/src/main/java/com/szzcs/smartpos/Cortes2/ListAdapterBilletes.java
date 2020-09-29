package com.szzcs.smartpos.Cortes2;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.szzcs.smartpos.R;

import java.util.List;

public class ListAdapterBilletes extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] maintitle;
    private final String[] subtitle;
    private final String[] calculo;


    public ListAdapterBilletes(Activity context, List<String> maintitle, List<String> subtitle, List<String> total1) {
        super((Context) context, R.layout.list, maintitle);
        // TODO Auto-generated constructor stub

        this.context= (Activity) context;
        this.maintitle= maintitle.toArray(new String[0]);
        this.subtitle= subtitle.toArray(new String[0]);
        this.calculo= total1.toArray(new String[0]);

    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.list_subtotal_oficina_billetes, null,true);

        TextView cantidad = (TextView) rowView.findViewById(R.id.txtCantidad);
        TextView denominacion = (TextView) rowView.findViewById(R.id.txtDenominacion);
        TextView total = (TextView) rowView.findViewById(R.id.txtTotal);

        cantidad.setText(maintitle[position]);
        denominacion.setText(subtitle[position]);
        total.setText(calculo[position]);

        return rowView;

    };
}
