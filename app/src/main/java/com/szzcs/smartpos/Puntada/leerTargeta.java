package com.szzcs.smartpos.Puntada;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.szzcs.smartpos.CardFragment;
import com.szzcs.smartpos.MyListAdapter;
import com.szzcs.smartpos.R;
import com.szzcs.smartpos.Ticket.Entidad;
import com.szzcs.smartpos.Ticket.ventas;
import com.zcs.sdk.card.CardReaderTypeEnum;

import java.util.List;

import static com.zcs.sdk.card.CardReaderTypeEnum.MAG_CARD;


public class leerTargeta extends AppCompatActivity {
    ListView list;

    String[] maintitle ={
            "Acumular","Redimir",
            "Registrar",
    };

    String[] subtitle ={
            "Acumula puntos","Redimir puntos",
            "Registrar Tarjeta Puntada",
    };

    Integer[] imgid={
            R.drawable.acumular,R.drawable.redimir,
            R.drawable.registrar,
    };

    Button btnRegistrar, btnAcumular;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leer_targeta);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Bundle bundle = getIntent().getExtras();
        final String track2 = bundle.getString("track");

        final MyListAdapter adapter=new MyListAdapter(this, maintitle, subtitle,imgid);
        list= findViewById(R.id.list);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                switch (position) {
                    case 0: //Acumular
                        Intent intent = new Intent(getApplicationContext(), posicionCargaAcumular.class);
                        Bundle bundle  = new Bundle();
                        bundle.putString("track",track2);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    break;
                    case 1://Redimir
                        Toast.makeText(getApplicationContext(),"En construcción",Toast.LENGTH_SHORT).show();
                    break;
                    case 2://Registrar
                        Intent intent1 = new Intent(getApplicationContext(), ClavePuntada.class);
                        Bundle bundle1  = new Bundle();
                        bundle1.putString("track",track2);
                        intent1.putExtras(bundle1);
                        startActivity(intent1);
                    break;
                    default:

                    break;
                }
            }
        });

        btnRegistrar = findViewById(R.id.btnRegistrar);
        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(getApplicationContext(), ClavePuntada.class);
                //Bundle bundle  = new Bundle();
                //bundle.putString("track",track2);
                //intent.putExtras(bundle);
                //startActivity(intent);
            }
        });

        btnAcumular=findViewById(R.id.btnAcumular);
        btnAcumular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(getApplicationContext(), posicionCargaAcumular.class);
                //Bundle bundle  = new Bundle();
                //bundle.putString("track",track2);
                //intent.putExtras(bundle);
                //startActivity(intent);
            }
        });
    }
}