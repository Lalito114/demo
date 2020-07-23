package com.szzcs.smartpos;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.szzcs.smartpos.Productos.VentaProductos;
import com.szzcs.smartpos.Puntada.leerTargeta;
import com.szzcs.smartpos.TanqueLleno.PosicionCargaTqll;
import com.szzcs.smartpos.Ticket.ventas;
import com.zcs.sdk.card.CardReaderTypeEnum;

import static com.zcs.sdk.card.CardReaderTypeEnum.MAG_CARD;

public class Munu_Principal extends AppCompatActivity {
    ListView list;

    String[] maintitle ={
            "Tickets","Facturas",
            "Productos","Cortes",
            "Pendientes","Reportes",
    };

    String[] subtitle ={
            "Emite tickets de venta","Emisión de facturas",
            "Administra productos","Realiza cortes de turnos",
            "Enlista pendientes","Muestra reportes",
    };

    Integer[] imgid={
            R.drawable.ventas,R.drawable.fact,
            R.drawable.product,R.drawable.cortes,
            R.drawable.pendientes, R.drawable.report,
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_munu__principal);


        CardReaderTypeEnum cardType = MAG_CARD;
        CardFragment cf = new CardFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("card_type", cardType);
        cf.setArguments(bundle);
        getFragmentManager().beginTransaction().replace(R.id.menu, cf).
                addToBackStack(CardFragment.class.getName()).
                commit();

        MyListAdapter adapter=new MyListAdapter(this, maintitle, subtitle,imgid);
        list= findViewById(R.id.list);
        list.setAdapter(adapter);


        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                // TODO Auto-generated method stub
                if(position == 0) {
                    //code specific to first list item
                   // Toast.makeText(getApplicationContext(),"Place Your First Option Code",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent( getApplicationContext(), ventas.class);
                    startActivity(intent);
                }

                else if(position == 1) {
                    //code specific to 2nd list item
                    //Intent intent = new Intent(getApplicationContext(), leerTargeta.class);
                    //startActivity(intent);
                    CardReaderTypeEnum cardType = MAG_CARD;
                    CardFragment cf = new CardFragment();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("card_type", cardType);
                    cf.setArguments(bundle);
                    getFragmentManager().beginTransaction().replace(R.id.menu, cf).
                            addToBackStack(CardFragment.class.getName()).
                            commit();


                   // cf.searchBankCard(cardType);
                }

                else if(position == 2) {

                    Intent intent = new Intent(getApplicationContext(), VentaProductos.class);
                    startActivity(intent);
                }
                else if(position == 3) {

                    Toast.makeText(getApplicationContext(),"Place Your Forth Option Code",Toast.LENGTH_SHORT).show();
                }
                else if(position == 4) {

                    Toast.makeText(getApplicationContext(),"Place Your Fifth Option Code",Toast.LENGTH_SHORT).show();
                }
                else if(position == 5) {

                    Toast.makeText(getApplicationContext(),"Place Your Fifth Option Code",Toast.LENGTH_SHORT).show();
                }

            }
        });


    }
}