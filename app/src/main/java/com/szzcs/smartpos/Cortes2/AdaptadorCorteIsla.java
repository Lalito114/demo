package com.szzcs.smartpos.Cortes2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;
import com.szzcs.smartpos.R;
import org.json.JSONArray;
import java.util.ArrayList;

public class AdaptadorCorteIsla extends BaseAdapter {
    public ArrayList<CorteIsla> listItems;
    public ArrayList<LecturasMecanicas> listItemsLecturasMecanicas = new ArrayList<>();
    private Context context;
    JSONArray jsonArray = new JSONArray();


    public AdaptadorCorteIsla(Context context, ArrayList<CorteIsla> listItems) {
        this.context = context;
        this.listItems = listItems;
    }

    //retorna el tamaño de nuestro listview
    @Override
    public int getCount() {
        return listItems.size();
    }
    //Regresamos la posicion
    @Override
    public Object getItem(int position) {
        return listItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
    // regresamos los elementos de lalista
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View row;
        final ListViewHolder listViewHolder;
        if(convertView == null){
            LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            row = layoutInflater.inflate(R.layout.list_corte_isla,parent,false);
            listViewHolder = new ListViewHolder();
            listViewHolder.posicionManguera=row.findViewById(R.id.idPosicion);
            listViewHolder.tipoCombustible=row.findViewById(R.id.txtTipoCombustible);
           listViewHolder.lecturaMecanica=row.findViewById(R.id.textLecturaMecanica);
            row.setTag(listViewHolder);

        }
        else{
            row= convertView;
            listViewHolder = (ListViewHolder) row.getTag();
        }
        final CorteIsla corteIsla = (CorteIsla) getItem(position);

        listViewHolder.posicionManguera.setText(corteIsla.getPosicionManguera());
        listViewHolder.tipoCombustible.setText(corteIsla.getTipoCombustible());
        listViewHolder.lecturaMecanica.setText(corteIsla.getLitroselectronicos().toString());

//        listViewHolder.lecturaMecanica.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                String posicion = ((CorteIsla) getItem(position)).getPosicionManguera().toString() ;
//                String edtlecturaMecanica = s.toString();
//
//                listItemsLecturasMecanicas.add(new LecturasMecanicas(posicion,edtlecturaMecanica));
//            }
//
//        });
        return row;
    }

    public class ListViewHolder {

        TextView posicionManguera;
        TextView tipoCombustible;
        TextView lecturaMecanica;

    }


}



