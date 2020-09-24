package com.szzcs.smartpos.Puntada;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.szzcs.smartpos.MyListAdapter;
import com.szzcs.smartpos.Puntada.Acumular.posicionCarga;
import com.szzcs.smartpos.Puntada.Redimir.PosicionRedimir;
import com.szzcs.smartpos.Puntada.Registrar.ClaveDespachadorPuntada;
import com.szzcs.smartpos.Puntada.Registrar.ClaveRegistrarPuntada;
import com.szzcs.smartpos.R;
import com.szzcs.smartpos.configuracion.SQLiteBD;

public class SeccionTarjeta extends AppCompatActivity {
    ListView list;

    String[] maintitle ={
            "Puntada Acumular","Puntada Redimir",
            "Puntada Registrar",
    };

    String[] subtitle ={
            "Acumula Puntos Para Otra Ocasion","Paga con Puntos",
            "Registrar Tarjeta Puntada",
    };

    Integer[] imgid={
            R.drawable.acumularpuntada,R.drawable.redimirpuntada,
            R.drawable.registrarpuntada,
    };

    Button btnRegistrar, btnAcumular;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seccion_tarjeta);
        SQLiteBD data = new SQLiteBD(getApplicationContext());
        this.setTitle(data.getNombreEsatcion());

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
                        Intent intent = new Intent(getApplicationContext(), posicionCarga.class);
                        Bundle bundle  = new Bundle();
                        bundle.putString("track",track2);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        finish();
                        break;
                    case 1://Redimir
                        Intent intent2 = new Intent(getApplicationContext(), PosicionRedimir.class);
                        Bundle bundle2  = new Bundle();
                        bundle2.putString("track",track2);
                        intent2.putExtras(bundle2);
                        startActivity(intent2);
                        break;
                    case 2://Registrar
                        Intent intent1 = new Intent(getApplicationContext(), ClaveDespachadorPuntada.class);
                        Bundle bundle1  = new Bundle();
                        bundle1.putString("track",track2);
                        intent1.putExtras(bundle1);
                        startActivity(intent1);
                        finish();
                        break;
                    default:

                        break;
                }
            }
        });

    }
}
