package com.szzcs.smartpos.Productos;

import android.content.Intent;
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
import com.szzcs.smartpos.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class formaPagoProducto extends AppCompatActivity {
    ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forma_pago_producto);
        //instruccion para que aparezca la flecha de regreso
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //Carga las Posiciones de Carga
        MostrarFormasPago();
    }
    //Proceso para cargar el listView con las posiciones de carga
    private void MostrarFormasPago() {
        String url = "http://10.0.1.20/TransferenciaDatosAPI/api/FormasPago/GetAll";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                MostrarFormasPagos(response);
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


    private void MostrarFormasPagos(String response) {

        final List<String> ID;
        ID = new ArrayList<String>();

        final List<String> NombreProducto;
        NombreProducto = new ArrayList<String>();

        final List<String> PrecioProducto;
        PrecioProducto = new ArrayList<>();

        final List<String> ClaveProducto;
        ClaveProducto = new ArrayList();

        try {
            JSONArray productos = new JSONArray(response);
            for (int i = 0; i <productos.length() ; i++) {
                JSONObject p1 = productos.getJSONObject(i);
                // Get the current student (json object) data
                String numero_pago = p1.getString("IdFormaPago");
                String nombre_pago = p1.getString("DescLarga");
                String numero_ticket = p1.getString("NumCopias");
                String NPIzquierda = nombre_pago.substring(0,1).toUpperCase();
                String NPDerecha = nombre_pago.substring(1, nombre_pago.length()).toLowerCase();
                nombre_pago = NPIzquierda + NPDerecha;
                //NombreProducto.add("ID: " + idArticulo + "    |     $"+precio);
                ID.add(nombre_pago);
                PrecioProducto.add(numero_pago);
                ClaveProducto.add(numero_ticket);

                //String idFormaPago = p1.getString("idFormaPago");
                //String DesLarga = p1.getString("DescLarga");
                //String precio = p1.getString("Precio");
                //NombreProducto.add("ID: " + idArticulo + "    |     $"+precio);
                //ID.add(DesLarga);
                //PrecioProducto.add(precio);
                //ClaveProducto.add(idArticulo);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ListAdapterProductos adapterP = new ListAdapterProductos(this, ID, NombreProducto);
        list=(ListView)findViewById(R.id.list);
        list.setAdapter(adapterP);
//        Agregado Mikel
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String  Descripcion = ID.get(i).toString();
                //String b = NombreProducto.get(i).toString();
                String paso= ClaveProducto.get(i).toString();
                //Producto.setText(paso);
                //txtDescripcion.setText(Descripcion);
            }
        });
    }

}