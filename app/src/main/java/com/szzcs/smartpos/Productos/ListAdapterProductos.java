package com.szzcs.smartpos.Productos;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.view.ViewGroup;
import android.widget.TextView;

import com.szzcs.smartpos.R;

import com.szzcs.smartpos.Productos.posicionProductos;
import com.szzcs.smartpos.Productos.VentasProductos;


import java.util.List;

public class ListAdapterProductos extends ArrayAdapter<String>{
    private final Activity context;
    private final String[] ID;
    private final String[] DescripcionProducto;

    public ListAdapterProductos(VentasProductos context, List<String> ID, List<String> DescripcionProducto){
        super((Context) context, R.layout.list, ID);

        this.context=(Activity) context;
        this.ID = ID.toArray(new String[0]);
        this.DescripcionProducto = DescripcionProducto.toArray(new String[0]);
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.listaproductos, null,true);

        TextView titleText = (TextView) rowView.findViewById(R.id.title);
        TextView subtitleText = (TextView) rowView.findViewById(R.id.subtitle);

        titleText.setText(ID[position]);
        subtitleText.setText(DescripcionProducto[position]);

        return rowView;

    };


}
