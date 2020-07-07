package com.szzcs.smartpos.Ticket;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.szzcs.smartpos.R;
import com.szzcs.smartpos.Ticket.Entidad;
import java.util.ArrayList;

import static android.view.View.*;


public class Adaptador extends BaseAdapter {
    private Context context;
    final ArrayList<Entidad> listItems;

    public Adaptador(Context context, ArrayList<Entidad> listItems) {
        this.context = context;
        this.listItems = listItems;
    }

    @Override
    public int getCount() {
        return listItems.size();
    }

    @Override
    public Object getItem(int position) {
        return listItems.get(position);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final Entidad Item = (Entidad) getItem(position);

        convertView = LayoutInflater.from(context).inflate(R.layout.list_row, null);
        ImageView imgFoto = (ImageView) convertView.findViewById(R.id.imgFoto);
        TextView tvTitulo = (TextView) convertView.findViewById(R.id.tvTitulo);
        TextView tvContenido = (TextView) convertView.findViewById(R.id.tvContenido );
        TextView tvCopias = (TextView) convertView.findViewById(R.id.tvCopias );
        //TextView tvIdentifica = (TextView) convertView.findViewById(R.id.tvIdentifica);
        imgFoto.setImageResource(Item.getImgFoto());
        tvTitulo.setText(Item.getTitulo());
        tvContenido.setText(Item.getContenido());
        tvCopias.setText(Item.getcopias());
        //tvIdentifica.setText(Item.getIdentifica());

        return convertView;
    }
}
