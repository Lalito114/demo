package com.szzcs.smartpos.Puntada.Redimir;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.szzcs.smartpos.R;

public class ClaveTarjeta extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clave_tarjeta);
        Button enviarnip = findViewById( R.id.btnEnviar);
        enviarnip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), BalanceProductos.class);
                startActivity(intent);
            }
        });
    }
}
