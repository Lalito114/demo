package com.szzcs.smartpos.Puntada;



import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.szzcs.smartpos.R;

import java.util.List;

public class ListAdapterSP  extends ArrayAdapter<String>{
    private final Activity context;
    private final String[] ID;
    private final String[] NombreProducto;


    public ListAdapterSP(SeleccionarProductos context, List<String> ID, List<String> NombreProducto) {
        super((Context) context, R.layout.list, ID);
        // TODO Auto-generated constructor stub

        this.context= (Activity) context;
        this.ID= ID.toArray(new String[0]);
        this.NombreProducto= NombreProducto.toArray(new String[0]);

    }


    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.listaproductos, null,true);

        TextView titleText = (TextView) rowView.findViewById(R.id.title);
        TextView subtitleText = (TextView) rowView.findViewById(R.id.subtitle);

        titleText.setText(ID[position]);
        subtitleText.setText(NombreProducto[position]);

        return rowView;

    };

}

