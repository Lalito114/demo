package com.szzcs.smartpos.Facturas;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.szzcs.smartpos.R;

public class CantidadTickets extends AppCompatActivity {

    EditText cantidadTickets;
    String rfc, email, idCliente, idAlias,Token;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cantidad_tickets);

        Bundle bundle = this.getIntent().getExtras();
        rfc = bundle.getString("RFC");
        email = bundle.getString("Email");
        idCliente = bundle.getString("IdCliente");
        idAlias = bundle.getString("IdAlias");
        Token = bundle.getString("Token");

        cantidadTickets = findViewById(R.id.editTxtNoTickets);


        Button btnCantidad = findViewById(R.id.btnEnviarCant);
        btnCantidad.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String cantidadT = cantidadTickets.getText().toString();
                if(cantidadT == ""){

                    Toast.makeText(getApplicationContext(), "Debe ingresar la cantidad de tickets que desea facturar.", Toast.LENGTH_SHORT).show();

                } else{

                    Intent intent = new Intent(CantidadTickets.this, Factura.class);

                    intent.putExtra("RFC", rfc);
                    intent.putExtra("Email", email);
                    intent.putExtra("IdCliente", idCliente);
                    intent.putExtra("IdAlias", idAlias);
                    intent.putExtra("Token", Token);
                    intent.putExtra("Cantidad", cantidadT);
                    startActivity(intent);

                }
            }
        }));

    }
}
