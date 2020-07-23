package com.szzcs.smartpos.Puntada;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.szzcs.smartpos.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Puntada_Productos_Seleccionar extends AppCompatActivity {
    Button btnAgregar,btnEnviar;
    TextView cantidadProducto;
    String cantidad;
    JSONObject mjason = new JSONObject();
    ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_puntada__productos);
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
        cantidadProducto = findViewById(R.id.cantidadProducto);
        cantidad = cantidadProducto.toString();

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

        List<String> ID;
        ID = new ArrayList<String>();

        List<String> NombreProducto;
        NombreProducto = new ArrayList<String>();

        List<String> PrecioProducto;
        PrecioProducto = new ArrayList<>();

        try {
            JSONArray productos = new JSONArray(response);
            for (int i = 0; i <productos.length() ; i++) {
                JSONObject p1 = productos.getJSONObject(i);

                String idArticulo = p1.getString("IdArticulo");

                String DesLarga = p1.getString("DescLarga");

                String precio = p1.getString("Precio");

                NombreProducto.add("ID: " + idArticulo + "    |     $"+precio);
                ID.add(DesLarga);
                PrecioProducto.add(precio);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        //ListAdapterP adapterP=new ListAdapterP(this, maintitle, subtitle,imgid);
        ListAdapterSP adapterP = new ListAdapterSP(this, ID, NombreProducto);
        list=(ListView)findViewById(R.id.list);
        list.setAdapter(adapterP);

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
