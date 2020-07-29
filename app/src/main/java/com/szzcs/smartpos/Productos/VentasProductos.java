package com.szzcs.smartpos.Productos;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
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

import static java.lang.Integer.parseInt;

public class  VentasProductos extends AppCompatActivity {
    Button btnAgregar,btnEnviar, incrementar, decrementar;
    TextView cantidadProducto;
    EditText Producto;
    String cantidad;
    JSONObject mjason = new JSONObject();
    ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ventas_productos);
        btnEnviar = findViewById(R.id.btnEnviar);
        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EnviarDatos();
            }
        });
        incrementar = findViewById(R.id.incrementar);
        incrementar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Aumentar();
            }
        });
        decrementar= findViewById(R.id.decrementar);
        decrementar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Decrementar();
            }
        });

        CantidadProducto();
        MostrarProductos();
        CrearJSON();
    }
    private void EnviarDatos() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Desea imprimir el ticket?");
        builder.setTitle("Venta de Productos");
        builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
//                String url = "http://10.0.1.20/TransferenciaDatosAPI/api/tarjetas/sendtarjeta";
//
//                StringRequest eventoReq = new StringRequest(Request.Method.POST,url,
//                        new Response.Listener<String>() {
//                            @Override
 //                           public void onResponse(String response) {
//                                Toast.makeText(getApplicationContext(),response,Toast.LENGTH_LONG).show();
//                                try {
//                                    JSONObject jsonObject = new JSONObject(response);
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                }

//                            }
//                        }, new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_SHORT).show();
//                    }
//                }){
//                    @Override
//                    protected Map<String, String> getParams() {
//                        // Posting parameters to login url
//                        Map<String, String> params = new HashMap<String, String>();
//                        params.put("RequestId", "33");
//                        params.put("PosCarga","1");
//                        params.put("Tarjeta","4000004210500001");
//                        params.put("Productos",mjason.toString());

//                        return params;
//                    }
//                };

//                // Añade la peticion a la cola
//                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
//                requestQueue.add(eventoReq);
//
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        AlertDialog dialog= builder.create();
        dialog.show();
    }

    private void CantidadProducto() {
        cantidadProducto = findViewById(R.id.cantidadProducto);
        Producto= findViewById(R.id.Producto);
        cantidad = cantidadProducto.toString();
    }
    private void Aumentar() {
        cantidad = cantidadProducto.getText().toString();
        int numero = Integer.parseInt(cantidad);
        int total = numero + 1;
        String resultado = String.valueOf(total);
        cantidadProducto.setText(resultado);

    }

    private void Decrementar() {
        cantidad = cantidadProducto.getText().toString();
        int numero = Integer.parseInt(cantidad);
        if (numero > 1) {
            int total = numero - 1;
            String resultado = String.valueOf(total);
            cantidadProducto.setText(resultado);
        }else{
            Toast.makeText(getApplicationContext(), "el valor minimo debe ser 1", Toast.LENGTH_LONG).show();
        }
    };


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
                String idArticulo = p1.getString("IdArticulo");
                String DesLarga = p1.getString("DescLarga");
                String precio = p1.getString("Precio");
                NombreProducto.add("ID: " + idArticulo + "    |     $"+precio);
                ID.add(DesLarga);
                PrecioProducto.add(precio);
                ClaveProducto.add(idArticulo);
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
               //String  a = PrecioProducto.get(i).toString();
               //String b = NombreProducto.get(i).toString();
               String paso= ClaveProducto.get(i).toString();
               Producto.setText(paso);
            }
        });
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