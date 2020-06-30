package com.szzcs.smartpos.Puntada;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.szzcs.smartpos.R;


public class leerTargeta extends AppCompatActivity {
    Button btnRegistrar, btnAcumular;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leer_targeta);
        Bundle bundle = getIntent().getExtras();
        final String track2 = bundle.getString("track");


        btnRegistrar = findViewById(R.id.btnRegistrar);
        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ClavePuntada.class);
                Bundle bundle  = new Bundle();
                bundle.putString("track",track2);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        btnAcumular=findViewById(R.id.btnAcumular);
        btnAcumular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), posicionCargaAcumular.class);
                Bundle bundle  = new Bundle();
                bundle.putString("track",track2);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }
}
