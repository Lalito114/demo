package com.szzcs.smartpos.Puntada.Registrar;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.szzcs.smartpos.Munu_Principal;
import com.szzcs.smartpos.R;
import com.szzcs.smartpos.Ticket.ListAdapter;
import com.szzcs.smartpos.Ticket.claveUsuario;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PosicionCargasPuntada extends AppCompatActivity {
    ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posicion_cargas_puntada);
        PosicionesCargar();

    }
    public void PosicionesCargar(){

        String url = "http://10.0.1.20/TransferenciaDatosAPI/api/PosCarga/GetMax";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                crearPosiciones(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),error.toString(), Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> parametros = new HashMap<String, String>();

                return parametros;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this.getApplicationContext());
        requestQueue.add(stringRequest);

    }

    private void crearPosiciones(String response) {
        List<String> maintitle;
        maintitle = new ArrayList<String>();

        List<String> subtitle;
        subtitle = new ArrayList<String>();

        List<Integer> imgid;
        imgid = new ArrayList<>();


        for (int i = 1; i <= 16; i++) {
            maintitle.add("PC" + String.valueOf(i));
            subtitle.add("Magna  |  Premium  |  Diesel");
            imgid.add(R.drawable.gas);
        }
        ListAdapterRegistrar adapter = new ListAdapterRegistrar(this, maintitle, subtitle, imgid);
        list= findViewById(R.id.list);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                int posicion = position +1;
                final String posi = String.valueOf(posicion);

                Bundle bundle = getIntent().getExtras();
                final String track2 = bundle.getString("track");
                final String nip = bundle.getString("nip");
                final String ClaveDespachador = bundle.getString("ClaveDespachador");

                String url = "http://10.0.1.20/CorpogasService/api/puntadas/Registrar/clave/"+ClaveDespachador;

                StringRequest eventoReq = new StringRequest(Request.Method.POST,url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                               EnviarDatos(response);


                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_SHORT).show();
                    }
                }){
                    @Override
                    protected Map<String, String> getParams() {
                        // Posting parameters to login url
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("EstacionId", "1");
                        params.put("RequestID","39");
                        params.put("PosicionCarga", posi);
                        params.put("Tarjeta",track2);
                        params.put("NuTarjetero","1");
                        params.put("NIP", nip);
                        return params;
                    }
                };

                // Añade la peticion a la cola
                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                requestQueue.add(eventoReq);

            }
        });



    }

    private void EnviarDatos(String response) {
        try {
            JSONObject respuesta = new JSONObject(response);
            String estado = respuesta.getString("Mensaje");
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Tarjeta Puntada");
            builder.setMessage(estado);
            builder.setPositiveButton("Cerrar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(getApplicationContext(),Munu_Principal.class);
                    startActivity(intent);
                    finish();
                }
            });
            AlertDialog dialog= builder.create();
            dialog.show();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
