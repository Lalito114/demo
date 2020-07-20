package com.szzcs.smartpos.Productos;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
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
import java.util.List;

public class VentaProductos extends AppCompatActivity {
    Button btnAgregar,btnEnviar;
    EditText cantidadProducto;
    EditText Producto;

    String cantidad;
    JSONObject mjason = new JSONObject();
    ListView list;
    Button incrementar;
    Button decrementar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venta_productos);
        MostrarProductos();

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


    private void mostarProductor(final String response) {

        List<String> ID;
        ID = new ArrayList<String>();

        List<String> NombreProducto;
        NombreProducto = new ArrayList<String>();

        List<String> PrecioProducto;
        PrecioProducto = new ArrayList<>();

        try {
            JSONArray productos = new JSONArray(response);
            for (int i = 0; i < productos.length(); i++) {
                JSONObject p1 = productos.getJSONObject(i);

                String idArticulo = p1.getString("IdArticulo");
                String DesLarga = p1.getString("DescLarga");
                String precio = p1.getString("Precio");
                NombreProducto.add("ID: " + idArticulo + "    |     $" + precio);
                ID.add(DesLarga);
                PrecioProducto.add(precio);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final ListAdapterProductos adapterProductos = new ListAdapterProductos(this, ID, NombreProducto);
        list = (ListView) findViewById(R.id.list);
        list.setAdapter(adapterProductos);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
               // Toast.makeText(getApplicationContext(), "Seleccionado " + i, Toast.LENGTH_LONG).show();
                int posicion = i +1;
                String posi = String.valueOf(posicion);


            }
        });




    }

}
