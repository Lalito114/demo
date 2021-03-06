package com.szzcs.smartpos.Puntada.Registrar;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.szzcs.smartpos.R;
import com.szzcs.smartpos.configuracion.SQLiteBD;

public class ClaveRegistrarPuntada extends AppCompatActivity {
    Button btnSiguiente;
    EditText pasword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clave_registrar_puntada);
        SQLiteBD data = new SQLiteBD(getApplicationContext());
        this.setTitle(data.getNombreEsatcion());
        Bundle bundle = getIntent().getExtras();
        final String track2 = bundle.getString("track");
        final String ClaveDespachador = bundle.getString("PasswordDespachador");

        btnSiguiente = findViewById(R.id.btnSiguente);
        btnSiguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pasword = findViewById(R.id.etNIP);
                String pas = pasword.getText().toString().trim();
                if (pas.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Ingresa el NIP de la nueva targeta",Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(getApplicationContext(), pasword.getText(), Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getApplicationContext(), PosicionCargasPuntada.class);
                    Bundle bundle  = new Bundle();
                    bundle.putString("ClaveDespachador",ClaveDespachador);
                    bundle.putString("track",track2);
                    bundle.putString("nip",pas);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
}
