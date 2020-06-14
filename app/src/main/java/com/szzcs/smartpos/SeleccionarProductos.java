package com.szzcs.smartpos;

import android.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SeleccionarProductos extends AppCompatActivity {
    Button btnp1, btnp2, btnp3,btnAgregar,btnEnviar;
    String cantidad;
    JSONObject mjason = new JSONObject();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seleccionar_productos);
        btnEnviar = findViewById(R.id.btnEnviar);
        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EnviarDatos();
            }
        });
        CantidadProducto();
        MostrarProductos();
        CrearJSON();

    }

    private void EnviarDatos() {
        String url = "http://10.0.1.20/TransferenciaDatosAPI/api/tarjetas/sendtarjeta";

        StringRequest eventoReq = new StringRequest(Request.Method.POST,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(getApplicationContext(),response,Toast.LENGTH_LONG).show();
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
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                    params.put("RequestId", "33");
                    params.put("PosCarga","1");
                    params.put("Tarjeta","4000004210500001");
                    params.put("Productos",mjason.toString());

                return params;
            }
        };

        // Añade la peticion a la cola
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(eventoReq);
    }

    private void CantidadProducto() {
        btnp1 = findViewById(R.id.btnp1);
        btnp1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cantidad = "1";
            }
        });

        btnp2 = findViewById(R.id.btnp2);
        btnp2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cantidad = "2";
            }
        });

        btnp3 = findViewById(R.id.btnp3);
        btnp3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cantidad = "3";
            }
        });
    }

    private void MostrarProductos() {
        String url = "http://10.0.1.20/TransferenciaDatosAPI/api/catarticulos/getall";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mostarProductor(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),error.toString(), Toast.LENGTH_LONG).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this.getApplicationContext());
        requestQueue.add(stringRequest);
    }

    private void mostarProductor(String response) {
        try {
            JSONArray productos = new JSONArray(response);
            for (int i = 0; i <productos.length() ; i++) {
                JSONObject p1 = productos.getJSONObject(i);

                String idArticulo = p1.getString("IdArticulo");

                String DesLarga = p1.getString("DescLarga");

                String precio = p1.getString("Precio");

                //Obtenemos el linear layout donde colocar los botones
                LinearLayout llBotonera = (LinearLayout) findViewById(R.id.llBotonera);

                //Creamos las propiedades de layout que tendrán los botones.
                //Son LinearLayout.LayoutParams porque los botones van a estar en un LinearLayout.
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT );

                //Creamos los botones en bucle
                    Button button = new Button(getApplicationContext());
                    //Asignamos propiedades de layout al boton
                    button.setLayoutParams(lp);
                    //Asignamos Texto al botón
                    button.setText(idArticulo + " | " + DesLarga + " $" + precio);

                    //Asignamose el Listener
                    button.setOnClickListener(new ButonsOnClickListener(this));
                    //Añadimos el botón a la botonera
                    llBotonera.addView(button);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class ButonsOnClickListener implements View.OnClickListener {
        public ButonsOnClickListener(SeleccionarProductos seleccionarProductos) {
        }

        @Override
        public void onClick(View v) {

        }
    }

    private void CrearJSON() {
        btnAgregar = findViewById(R.id.btnAgregar);
        btnAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    for (int i = 0; i <1; i++) {
                        mjason.put("Cantidad",cantidad);
                        mjason.put("IdProducto","35");
                    }
                    Toast.makeText(getApplicationContext(),mjason.toString(),Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

    }
}
