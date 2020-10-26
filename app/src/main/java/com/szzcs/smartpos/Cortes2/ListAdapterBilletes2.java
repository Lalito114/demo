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

public class ListAdapterBilletes2 extends ArrayAdapter<String> {
    private final Activity context2;
    private final String[] maintitle2;
    private final String[] subtitle2;
    private final String[] calculo2;


    public ListAdapterBilletes2(Activity context, List<String> maintitle, List<String> subtitle, List<String> total1) {
        super((Context) context, R.layout.list, maintitle);
        // TODO Auto-generated constructor stub

        this.context2= (Activity) context;
        this.maintitle2= maintitle.toArray(new String[0]);
        this.subtitle2= subtitle.toArray(new String[0]);
        this.calculo2= total1.toArray(new String[0]);

    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context2.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.list_subtotal_oficina_billetes, null,true);

        TextView cantidad = (TextView) rowView.findViewById(R.id.txtCantidad);
        TextView denominacion = (TextView) rowView.findViewById(R.id.txtDenominacion);
        TextView total = (TextView) rowView.findViewById(R.id.txtTotal);

        cantidad.setText(maintitle2[position]);
        denominacion.setText(subtitle2[position]);
        total.setText(calculo2[position]);

        return rowView;

    };
}
