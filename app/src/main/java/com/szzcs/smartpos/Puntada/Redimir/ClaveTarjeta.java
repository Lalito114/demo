package com.szzcs.smartpos.Puntada.Redimir;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
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
import com.szzcs.smartpos.Cortes2.ListAdapterBilletes;
import com.szzcs.smartpos.Cortes2.SubOfiAccor;
import com.szzcs.smartpos.Munu_Principal;
import com.szzcs.smartpos.PrintFragment;
import com.szzcs.smartpos.R;
import com.szzcs.smartpos.Ticket.formas_de_pago;
import com.szzcs.smartpos.configuracion.SQLiteBD;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ClaveTarjeta extends AppCompatActivity {
    String clave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clave_tarjeta);
        Button enviarnip = findViewById( R.id.btnEnviar);
        enviarnip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{

                    final EditText input = new EditText(getApplicationContext());
                    input.setTextColor(Color.BLACK);
                    input.setGravity(Gravity.CENTER);
                    input.setTextSize(22);
                    input.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);

                    AlertDialog.Builder builder = new AlertDialog.Builder(ClaveTarjeta.this);
                    builder.setTitle("Ingresa Contraseña Despachador \n");
                    builder.setView(input)

                            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                     clave = input.getText().toString();
                                     if (clave.isEmpty()){
                                         Toast.makeText(ClaveTarjeta.this, "Ingresa la Contraseña", Toast.LENGTH_SHORT).show();
                                     }else{
                                         solicitarBalanceTarjeta();
                                     }
                                }
                            })
                            .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            }).show();

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    private void solicitarBalanceTarjeta() {
        final EditText nip =  findViewById(R.id.txtclave);
        final SQLiteBD data = new SQLiteBD(getApplicationContext());
        String url = "http://"+data.getIpEstacion()+"/CorpogasService/api/puntadas/actualizaPuntos/clave/"+clave;

        StringRequest eventoReq = new StringRequest(Request.Method.POST,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject resultado = new JSONObject(response);
                            String estado = resultado.getString("Estado");
                            String mensaje = resultado.getString("Mensaje");
                            final String saldo = resultado.getString("Saldo");
                                if (estado == "true"){

                                    try{
                                        AlertDialog.Builder builder = new AlertDialog.Builder(ClaveTarjeta.this);
                                        builder.setTitle("Tarjeta Puntada");
                                        builder.setMessage(mensaje);
                                        builder.setPositiveButton("Cerrar", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                String carga = getIntent().getStringExtra("pos");
                                                Intent intent = new Intent(getApplicationContext(),BalanceProductos.class);
                                                intent.putExtra("pos",carga);
                                                intent.putExtra("saldo",saldo);
                                                startActivity(intent);
                                                finish();
                                            }
                                        });
                                        AlertDialog dialog= builder.create();
                                        dialog.show();

                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
                                }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                //Obtenemos los parmetros a enviar
                String carga = getIntent().getStringExtra("pos");
                String track = getIntent().getStringExtra("track");
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("EstacionId",data.getIdEstacion());
                params.put("RequestID","33");
                params.put("PosicionCarga", carga);
                params.put("Tarjeta",track);
                params.put("NuTarjetero", "1");
                params.put("NIP",nip.getText().toString());
                return params;
            }
        };
        // Añade la peticion a la cola
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(eventoReq);
    }
}
