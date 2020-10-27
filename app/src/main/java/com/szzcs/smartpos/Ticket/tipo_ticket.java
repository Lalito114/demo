package com.szzcs.smartpos.Ticket;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.szzcs.smartpos.R;
import com.szzcs.smartpos.Ticket.Monederos.despachdorclave;
import com.szzcs.smartpos.configuracion.SQLiteBD;

public class tipo_ticket extends AppCompatActivity {

    ListView list;

    String[] maintitle ={
            "Ticket","Monederos Electronicos"
    };

    String[] subtitle ={
            "Emite tickets de venta","Imprime ticketde monederos electronicos",
    };

    Integer[] imgid={
            R.drawable.ventas,R.drawable.trajeta,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tipo_ticket);
        SQLiteBD data = new SQLiteBD(getApplicationContext());
        this.setTitle(data.getNombreEsatcion());
        AdapterList adapter=new AdapterList(this, maintitle, subtitle,imgid);
        list= findViewById(R.id.list);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0) {
                    Intent intent = new Intent( getApplicationContext(), ventas.class);
                    intent.putExtra("tipo","0");
                    startActivity(intent);
                }

                else if(position == 1) {
                    Intent intent = new Intent( getApplicationContext(), despachdorclave.class);
                    intent.putExtra("tipo" , "1");
                    startActivity(intent);
                }

            }
        });
    }
}
