package com.szzcs.smartpos.FinalizaVenta;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;


import com.szzcs.smartpos.CardFragment;
import com.szzcs.smartpos.Cortes2.VentasTotales;
import com.szzcs.smartpos.EmpleadoHuellas.capturaEmpleadoHuella;
import com.szzcs.smartpos.FingerprintActivity;
import com.szzcs.smartpos.Gastos.claveGastos;
import com.szzcs.smartpos.Munu_Principal;
import com.szzcs.smartpos.MyListAdapter;
import com.szzcs.smartpos.Pendientes.claveUPendientes;
import com.szzcs.smartpos.Productos.posicionProductos;
import com.szzcs.smartpos.R;
import com.szzcs.smartpos.Ticket.tipo_ticket;
import com.zcs.sdk.card.CardReaderTypeEnum;

import static com.zcs.sdk.card.CardReaderTypeEnum.MAG_CARD;

public class ventash extends AppCompatActivity {

    Button IniciaVenta, FinalizaVenta;
    ListView list;
    String[] maintitle ={
            "Inicia Venta","Finaliza Venta",
    };

    String[] subtitle ={
            "Inicia Proceso de Venta","Finaliza Proceso de Venta",
    };

    Integer[] imgid={
            R.drawable.gas,R.drawable.gas,
        };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ventash);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        MyListAdapter adapter=new MyListAdapter(ventash.this, maintitle, subtitle,imgid);
        list= findViewById(R.id.list);
        list.setAdapter(adapter);


        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                // TODO Auto-generated method stub
                if(position == 0) {
                    Intent intent = new Intent(getApplicationContext(), claveIniciaVenta.class);
                    intent.putExtra("lugarproviene", "1");
                    startActivity(intent);
                    finish();
                }

                else if(position == 1) {
                    Intent intent1 = new Intent(getApplicationContext(), claveFinVenta.class);
                    intent1.putExtra("lugarproviene", "2");
                    startActivity(intent1);
                    finish();
                }
            }
        });





    }

    private void validaSeleccion(){
        IniciaVenta= findViewById(R.id.btniniciaventa);
        FinalizaVenta=findViewById(R.id.btnfinalizaventa);

        IniciaVenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), claveFinalizaVenta.class);
                intent.putExtra("lugarproviene", "1");
                startActivity(intent);
                finish();
            }
        });

        FinalizaVenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(getApplicationContext(), claveFinalizaVenta.class);
                intent1.putExtra("lugarproviene", "2");
                startActivity(intent1);
                finish();
            }
        });

    }
    //Metodo para regresar a la actividad principal
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), Munu_Principal.class);
        startActivity(intent);
        finish();
    }
}