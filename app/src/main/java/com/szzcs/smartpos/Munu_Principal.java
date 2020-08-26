package com.szzcs.smartpos;

import android.app.Activity;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;


import com.szzcs.smartpos.Pendientes.posicionPendientes;
import com.szzcs.smartpos.Productos.VentasProductos;
import com.szzcs.smartpos.Ticket.ventas;
import com.zcs.sdk.card.CardReaderTypeEnum;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

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
                    CardReaderTypeEnum cardType = MAG_CARD;
                    CardFragment cf = new CardFragment();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("card_type", cardType);
                    cf.setArguments(bundle);
                    getFragmentManager().beginTransaction().replace(R.id.menu, cf).
                            commit();
                }
                else if(position == 2) {
                    Intent intent = new Intent(getApplicationContext(), VentasProductos.class);
                    startActivity(intent);
                }
                else if(position == 3) {
                    Toast.makeText(getApplicationContext(),"Place Your Forth Option Code",Toast.LENGTH_SHORT).show();
                }
                else if(position == 4) {
                    Intent intente = new Intent(getApplicationContext(), posicionPendientes.class);
                    startActivity(intente);
                }
                else if(position == 5) {
                    Toast.makeText(getApplicationContext(),"Place Your Fifth Option Code",Toast.LENGTH_SHORT).show();
                }

            }
        });
        //Escribir url del Archivo

       
    }


}