package com.szzcs.smartpos.Gastos;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.szzcs.smartpos.R;

public class cargavaleGasto extends AppCompatActivity {
    ListView list;
    TextView txtDescripcion, txtClave, isla, turno, usuario;
    TextView SubTotal, Descripcion;
    String EstacionId = "1";
    String sucursalId = "1";
    String idisla, idTurno;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cargavale_gasto);
         SubTotal =findViewById(R.id.SubTot);
        Descripcion = findViewById(R.id.Descripcion);

        String islaId = getIntent().getStringExtra("isla");
        String turnoId = getIntent().getStringExtra("turno");
        Button enviar = findViewById(R.id.btnEnviar);
        enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
