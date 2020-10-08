package com.szzcs.smartpos.Facturas;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.szzcs.smartpos.R;

import java.util.ArrayList;

public class AdaptadorListRFC extends BaseAdapter {

    private Context context;
    private ArrayList<EntidadListRFC> listItems;

    public AdaptadorListRFC(Context context, ArrayList<EntidadListRFC> listItems) {
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

        EntidadListRFC Item = (EntidadListRFC) getItem(position);

        convertView = LayoutInflater.from(context).inflate(R.layout.list_rfc, null);
        TextView tvRazonSocial = (TextView) convertView.findViewById(R.id.txtRazonSocial);
        TextView tvRFC = (TextView) convertView.findViewById(R.id.txtRFC);
        TextView tvEmail = (TextView) convertView.findViewById(R.id.txtEmail);
        TextView tvIdCliente = (TextView) convertView.findViewById(R.id.txtIdCliente);
        TextView tvIdAlias = (TextView) convertView.findViewById(R.id.txtIdAlias);

        tvRazonSocial.setText(Item.getRazonSocial());
        tvRFC.setText(Item.getRfc());
        tvEmail.setText(Item.getEmail());
        tvIdCliente.setText(Item.getIdCliente());
        tvIdAlias.setText(Item.getIdAlias());

        return convertView;
    }
}
