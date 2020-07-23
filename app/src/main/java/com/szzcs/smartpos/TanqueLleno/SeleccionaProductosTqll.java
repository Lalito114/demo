package com.szzcs.smartpos.TanqueLleno;
import android.app.FragmentManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonObject;
import com.szzcs.smartpos.TanqueLleno.ListAdapterTqll;
import com.szzcs.smartpos.R;
import com.szzcs.smartpos.TanqueLleno.SeleccionaProductosTqll;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SeleccionaProductosTqll extends AppCompatActivity {

    Button btnAgregar, btnSiguiente;
    TextView cantidadProducto;
    String cantidad, poscarga;
    JSONObject mjason = new JSONObject();
    ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selecciona_productos_tqll);
        poscarga = getIntent().getStringExtra("car");
        String usuario = getIntent().getStringExtra("user");
        CantidadProducto();
        MostrarProductos();
        CrearJSON();

        btnSiguiente = findViewById(R.id.btnSiguiente);
        btnSiguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Se instancia y se llama a la clase pedir nip
                Intent intent = new Intent(getApplicationContext(), PedirNipTqll.class);

                //intent.putExtra("json", mjason.toString());
                //intent.putExtra("mjason", (Serializable) mjason);
                intent.putExtra("poscarga",poscarga);
                startActivity(intent);
            }
        });








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
                mostrarProductos(response);
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


    private void mostrarProductos(String response) {

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
        ListAdapterTqll adapterP = new ListAdapterTqll(this, ID, NombreProducto);
        list=(ListView)findViewById(R.id.list);
        list.setAdapter(adapterP);
//        ListView listview = null;
//
//        List<String> maintitle;
//        maintitle = new ArrayList<String>();
//
//        ArrayList<String> names = null;
//
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, names);
//        listview.setAdapter(adapter);
    }

    private class ButonsOnClickListener implements View.OnClickListener {
        public ButonsOnClickListener(SeleccionaProductosTqll seleccionaProductosTqll) {
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