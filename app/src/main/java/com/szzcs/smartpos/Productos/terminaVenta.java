package com.szzcs.smartpos.Productos;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.szzcs.smartpos.Munu_Principal;
import com.szzcs.smartpos.R;
import com.szzcs.smartpos.configuracion.SQLiteBD;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class terminaVenta extends AppCompatActivity {
    ImageView terminaVenta;
    String EstacionId, sucursalId, ipEstacion ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_termina_venta);

        SQLiteBD db = new SQLiteBD(getApplicationContext());
        sucursalId=db.getIdSucursal();
        EstacionId = db.getIdEstacion();
        ipEstacion = db.getIpEstacion();

        ImageView terminaVenta = findViewById(R.id.imgProducto);

        terminaVenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String posicion;
                posicion = getIntent().getStringExtra("car");

                //Utilizamos el metodo POST para  enviar la transaccion y regrese el numero de ticket
                String url = "http://"+ipEstacion+"/CorpogasService/api/Transacciones/finalizaVenta/sucursal/"+sucursalId+"/posicionCarga/"+posicion;
                StringRequest eventoReq = new StringRequest(Request.Method.POST,url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Toast.makeText(getApplicationContext(),response,Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getApplicationContext(), Munu_Principal.class);
                                startActivity(intent);
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
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
                        // Colocar parametros para ingresar la  url
                        Map<String, String> params = new HashMap<String, String>();
                        return params;
                    }
                };

                // Añade la peticion a la cola
                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                requestQueue.add(eventoReq);

            }
        });


    }
}