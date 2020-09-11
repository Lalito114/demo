package com.szzcs.smartpos.Gastos;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.szzcs.smartpos.R;

public class autizacionGastos extends AppCompatActivity {
    Button enviar;
    ImageView Autoriza;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autizacion_gastos);

        final String proviene = getIntent().getStringExtra("tipoGasto");
        final String islaId = getIntent().getStringExtra("isla");
        final String turnoId = getIntent().getStringExtra("turno");


        enviar=findViewById(R.id.enviar);
        enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (proviene.equals("1") ) {
                    Intent intent = new Intent(getApplicationContext(), cargaGasto.class);
                    intent.putExtra("isla", islaId);
                    intent.putExtra("turno", turnoId);
                    startActivity(intent);
                }else{
                    Intent intent = new Intent(getApplicationContext(), cargavaleGasto.class);
                    intent.putExtra("isla", islaId);
                    intent.putExtra("turno", turnoId);
                    startActivity(intent);

                }
            }
        });

    }
}
