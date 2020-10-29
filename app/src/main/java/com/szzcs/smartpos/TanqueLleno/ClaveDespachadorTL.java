package com.szzcs.smartpos.TanqueLleno;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
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
import com.szzcs.smartpos.Ticket.Monederos.despachdorclave;
import com.szzcs.smartpos.Ticket.Monederos.posicionesDespachador;
import com.szzcs.smartpos.configuracion.SQLiteBD;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ClaveDespachadorTL extends AppCompatActivity {
    String iduser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clave_despachador_tl);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
                if (pass.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Ingresa tu contraseña de Despachador", Toast.LENGTH_LONG).show();
                    AlertDialog.Builder builder;
                } else {
                    SQLiteBD data = new SQLiteBD(getApplicationContext());
                    String url = "http://" + data.getIpEstacion() + "/CorpogasService/api/SucursalEmpleados/clave/" + pass;

                    // Utilizamos el metodo Post para validar la contraseña
                    StringRequest eventoReq = new StringRequest(Request.Method.GET, url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        //Se instancia la respuesta del json
                                        JSONObject validar = new JSONObject(response);
                                        String valido = validar.getString("Activo");
                                        iduser = validar.getString("Id");
                                        if (valido == "true") {
                                            String track = getIntent().getStringExtra("track");
                                            Intent intent = new Intent(getApplicationContext(), PosicionCargaTLl.class);
                                            intent.putExtra("IdUsuario", iduser);
                                            intent.putExtra("ClaveDespachador", pass);
                                            intent.putExtra("track", track);
                                            intent.putExtra("pass", pass);
                                            startActivity(intent);
                                            pasword.setText("");
                                        } else {
                                            //Si no es valido se envia mensaje
                                            Toast.makeText(getApplicationContext(), "La contraseña es incorecta", Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (JSONException e) {
                                        //herramienta  para diagnostico de excepciones
                                        e.printStackTrace();
                                    }
                                }
                                //funcion para capturar errores
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });

                    // Añade la peticion a la cola
                    RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                    requestQueue.add(eventoReq);
                }

            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getBaseContext(), Munu_Principal.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
        finish();
    }

    //procedimiento para  cachar el Enter del teclado
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_ENTER:
                calculos();
                return true;
            default:
                return super.onKeyUp(keyCode, event);
        }
    }

    private void calculos() {
        //Se lee el password del objeto y se asigna a variable
        EditText pasword = (EditText) findViewById(R.id.pasword);
        String pass = pasword.getText().toString();

        //Si no se terclea nada envia mensaje de teclear contraseña
        if (pass.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Ingresa tu contraseña de Despachador", Toast.LENGTH_LONG).show();
            AlertDialog.Builder builder;
        } else {
            SQLiteBD data = new SQLiteBD(getApplicationContext());
            String url = "http://" + data.getIpEstacion() + "/CorpogasService/api/SucursalEmpleados/clave/" + pass;

            // Utilizamos el metodo Post para validar la contraseña
            StringRequest eventoReq = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                //Se instancia la respuesta del json
                                JSONObject validar = new JSONObject(response);
                                String valido = validar.getString("Activo");
                                iduser = validar.getString("Id");
                                if (valido == "true") {
                                    String track = getIntent().getStringExtra("track");
                                    Intent intent = new Intent(getApplicationContext(), PosicionCargaTLl.class);
                                    intent.putExtra("IdUsuario", iduser);
                                    intent.putExtra("ClaveDespachador", pass);
                                    intent.putExtra("track", track);
                                    intent.putExtra("pass", pass);
                                    startActivity(intent);
                                    pasword.setText("");
                                    finish();
                                } else {
                                    //Si no es valido se envia mensaje
                                    Toast.makeText(getApplicationContext(), "La contraseña es incorecta", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                //herramienta  para diagnostico de excepciones
                                e.printStackTrace();
                            }
                        }
                        //funcion para capturar errores
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
                }
            });

            // Añade la peticion a la cola
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(eventoReq);
        }
    }

}
