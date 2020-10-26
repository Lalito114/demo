package com.szzcs.smartpos.Cortes2;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.szzcs.smartpos.R;

import java.util.List;

public class ListAdapterFormasPago extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] maintitle;
    private final String[] subtitle;
    private final String[] calculo;

    public ListAdapterFormasPago(Activity context, List<String> maintitle, List<String> subtitle, List<String> total1){
        super(context, R.layout.list, maintitle);

        this.context = context;
        this.maintitle = maintitle.toArray(new String[0]);
        this.subtitle = subtitle.toArray(new String[0]);
        this.calculo = total1.toArray(new String[0]);


    }

    public View getView(int position, View view, ViewGroup parent){


        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.list_cierre_formas_pago, null, true);
        rowView.setClickable(false);
        rowView.setEnabled(false);
        rowView.setActivated(false);
        rowView.setFocusable(false);




        TextView cantidad = rowView.findViewById(R.id.txtNumeroFormasPago);
        TextView formasPago = rowView.findViewById(R.id.txtFormasPago);
        TextView total = rowView.findViewById(R.id.txtTotalFormasPago);



        cantidad.setText(maintitle[position]);
        formasPago.setText(subtitle[position]);
        total.setText(calculo[position]);

        return rowView;


    }


}
