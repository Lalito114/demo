package com.szzcs.smartpos.TanqueLleno;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.szzcs.smartpos.Munu_Principal;
import com.szzcs.smartpos.R;
import com.szzcs.smartpos.configuracion.SQLiteBD;

import java.util.HashMap;
import java.util.Map;

public class ClaveDespachadorTL extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clave_despachador_tl);
        SQLiteBD data = new SQLiteBD(getApplicationContext());
        this.setTitle(data.getNombreEsatcion());

        //Crea Boton Enviar
        Button btnenviar = (Button) findViewById(R.id.btnsiguiente);
        //En espera a recibir el evento Onclick del boton Enviar
        btnenviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Se lee el password del objeto y se asigna a variable
                EditText pasword = (EditText) findViewById(R.id.pasword);
                String pass = pasword.getText().toString();

                //Si no se terclea nada envia mensaje de teclear contraseña
                if (pass.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Ingresa tu contraseña de Despachador", Toast.LENGTH_LONG).show();
                    AlertDialog.Builder builder;
                }else{
                    String track = getIntent().getStringExtra("track");
                    Intent intent = new Intent(getApplicationContext(),PosicionCargaTLl.class);
                    intent.putExtra("track", track);
                    intent.putExtra("pass",pass);
                    startActivity(intent);
                    pasword.setText("");
                }

            }
        });
    }


}
