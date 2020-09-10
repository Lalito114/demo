package com.szzcs.smartpos.Gastos;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.szzcs.smartpos.R;

public class autizacionGastos extends AppCompatActivity {
    Button enviar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autizacion_gastos);

        final String islaId = getIntent().getStringExtra("isla");
        final String turnoId = getIntent().getStringExtra("turno");

        enviar.findViewById(R.id.enviar);
        enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), cargaGasto.class);
                //intent.putExtra("user", idusuario);
                intent.putExtra("isla", islaId);
                intent.putExtra("turno", turnoId);
                
                startActivity(intent);

            }
        });

    }
}
