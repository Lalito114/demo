package com.szzcs.smartpos.Ticket;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.szzcs.smartpos.R;

public class Definicion_Tarjeta extends AppCompatActivity {
    ListView list;

    String[] maintitle ={
            "Puntada","Tanque LLeno",
    };

    String[] subtitle ={
            "Imprime tickets de Puntada","Imprime tickets de Tanque LLeno",
    };

    Integer[] imgid={
            R.drawable.nuevapuntada,R.drawable.tanquelleno,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_definicion__tarjeta);

        Adapter_Tipo adapter=new Adapter_Tipo(this, maintitle, subtitle,imgid);
        list= findViewById(R.id.list);
        list.setAdapter(adapter);

    }


}
