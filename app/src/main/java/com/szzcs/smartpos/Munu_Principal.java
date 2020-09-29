package com.szzcs.smartpos;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;


import com.szzcs.smartpos.Cortes.ventasTotales;
import com.szzcs.smartpos.Encriptacion.EncriptarMAC;
import com.szzcs.smartpos.Encriptacion.EncriptarObtenerIP;
import com.szzcs.smartpos.Gastos.claveGastos;
import com.szzcs.smartpos.Pendientes.claveUPendientes;
import com.szzcs.smartpos.Productos.posicionProductos;
import com.szzcs.smartpos.Ticket.tipo_ticket;
import com.szzcs.smartpos.Ticket.ventas;
import com.szzcs.smartpos.configuracion.SQLiteBD;
import com.zcs.sdk.card.CardReaderTypeEnum;

import static com.zcs.sdk.card.CardReaderTypeEnum.MAG_CARD;

public class Munu_Principal extends AppCompatActivity {

    ListView list;

    String[] maintitle ={
            "Tickets","Monederos Electronicos",
            "Productos","Cortes",
            "Pendientes", "Gasto","Facturas",
    };

    String[] subtitle ={
            "Emite tickets de venta","Registro, Acumular y Redimir",
            "Administra productos","Realiza cortes de turnos",
            "Enlista pendientes", "Reporta lo Egresos","Imprime tus Tickets",
    };

    Integer[] imgid={
            R.drawable.ventas,R.drawable.monedero,
            R.drawable.product,R.drawable.cortes,
            R.drawable.pendientes,R.drawable.gastos,R.drawable.fact,
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_munu__principal);
        SQLiteBD data = new SQLiteBD(getApplicationContext());
        this.setTitle(data.getNombreEsatcion());

        EncriptarMAC mac = new EncriptarMAC();
        String mac2 = mac.getMacAddr();
        String macmd5 = mac.getMD5(mac2);


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
                    Intent intent = new Intent( getApplicationContext(), tipo_ticket.class);
                    startActivity(intent);
                }

                else if(position == 1) {
                    CardReaderTypeEnum cardType = MAG_CARD;
                    CardFragment cf = new CardFragment();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("card_type", cardType);
                    cf.setArguments(bundle);
                    getFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.menu, cf).
                            commit();
                }
                else if(position == 2) {
                    Intent intent = new Intent(getApplicationContext(), posicionProductos.class);
                    startActivity(intent);
                }
                else if(position == 3) {
                    //EncriptarMAC mac = new EncriptarMAC();
                    //String mac2 = mac.getMacAddr();
                    //String macmd5 = mac.getMD5(mac2);
                    //Toast.makeText(getApplicationContext(),macmd5,Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getApplicationContext(), ventasTotales.class);
                    startActivity(intent);
                }
                else if(position == 4) {
                    Intent inten = new Intent(getApplicationContext(), claveUPendientes.class);
                    startActivity(inten);

                }else if(position == 5) {
                    Intent intent = new Intent (getApplicationContext(), claveGastos.class);
                    startActivity(intent);
                }
                else if(position == 6) {

                    try{
                        PrintFragment cf = new PrintFragment();
                        getFragmentManager().beginTransaction().replace(R.id.menu, cf).
                                addToBackStack(PrintFragment.class.getName()).
                                commit();

                    }catch (Exception e){

                    }
                }

            }
        });

        //Escribir url del Archivo


    }





}