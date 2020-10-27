package com.szzcs.smartpos.Productos;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.szzcs.smartpos.Munu_Principal;
import com.szzcs.smartpos.R;
import com.szzcs.smartpos.configuracion.SQLiteBD;

import org.json.JSONException;
import org.json.JSONObject;

public class tipoproducto extends AppCompatActivity {
    Button gasolina, producto;
    String usuario, posicion;
    String EstacionId, sucursalId, ipEstacion ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tipoproducto);

        usuario = getIntent().getStringExtra("user");
        posicion = getIntent().getStringExtra("car");
        SQLiteBD db = new SQLiteBD(getApplicationContext());
        sucursalId=db.getIdSucursal();
        EstacionId = db.getIdEstacion();
        ipEstacion = db.getIpEstacion();



        gasolina = findViewById(R.id.btndespacho);

        gasolina.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Autorizadespacho();
            }
        });

        producto = findViewById(R.id.btnproductos);
        producto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), VentasProductos.class);
                intent.putExtra("car",posicion);
                intent.putExtra("user", usuario);
                //inicia el activity
                startActivity(intent);
                finish();
            }
        });

    }

    private void Autorizadespacho(){

        String url = "http://"+ipEstacion+"/CorpogasService/api/despachos/autorizaDespacho/posicionCargaId/"+posicion+"/usuarioId/"+usuario;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject respuesta = new JSONObject(response);
                    String correctoautoriza = respuesta.getString("Correcto");
                    String mensajeautoriza = respuesta.getString("Mensaje");
                    String objetoRespuesta = respuesta.getString("ObjetoRespuesta");
                    if (correctoautoriza.equals("true")) {
                        Toast.makeText(getApplicationContext(),mensajeautoriza, Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getApplicationContext(), Munu_Principal.class);
                        startActivity(intent);
                        finish();

                    }else{
                        Toast.makeText(getApplicationContext(),mensajeautoriza, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),error.toString(), Toast.LENGTH_LONG).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this.getApplicationContext());
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(12000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);

    }

    //Metodo para regresar a la actividad principal
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), Munu_Principal.class);
        startActivity(intent);
        finish();
    }

}