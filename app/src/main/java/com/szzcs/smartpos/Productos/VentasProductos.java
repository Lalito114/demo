    package com.szzcs.smartpos.Productos;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.szzcs.smartpos.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static java.lang.Integer.parseInt;
import org.json.JSONObject;
import org.json.JSONException;

public class VentasProductos extends AppCompatActivity{
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    Button btnAgregar,btnEnviar, incrementar, decrementar;
    TextView cantidadProducto, txtDescripcion, NumeroProductos;
    EditText Producto;
    String cantidad;
    JSONObject mjason = new JSONObject();
    ListView list;

    Integer ProductosAgregados = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ventas_productos);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        btnEnviar = findViewById(R.id.btnEnviar);
        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(), "Productos Seleccionados: " + ProductosAgregados, Toast.LENGTH_LONG).show();
                if (mjason.length() >0)       //!= null)
                    EnviarDatos();
                else
                    Toast.makeText(getApplicationContext(), "Seleccione al menos uno de los Productos", Toast.LENGTH_LONG).show();
           }
        });
        btnAgregar = findViewById(R.id.btnAgregar);
        btnAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AgregarProducto();
                //CrearJSON();
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
        //btnAgregar = findViewById(R.id.btnAgregar);
        //btnAgregar.setOnClickListener(new View.OnClickListener() {
        //    @Override
        //    public void onClick(View view) {
        //        AgregarProducto();
        //    }
        //});

        CantidadProducto();
        MostrarProductos();
    }
    private void EnviarDatos() {
        //Si es valido se asignan valores
        final String posicion;
        posicion = getIntent().getStringExtra("posicion");
        final String usuarioid;
        usuarioid = getIntent().getStringExtra("usuario");
        //Se instancia y se llama a la clase VentaProductos
//        Intent intent = new Intent(getApplicationContext(), formaPago.class);
//        intent.putExtra("posicion",posicion);
//        intent.putExtra("usuario",usuarioid);
//        Gson gson = new Gson();
//        String myJson = gson.toJson(mjason);
//        intent.putExtra("myjson", myJson);
//        startActivity(intent);
    }

    private void CantidadProducto() {
        cantidadProducto = findViewById(R.id.cantidadProducto);
        Producto= findViewById(R.id.Producto);
        cantidad = cantidadProducto.toString();
        txtDescripcion = findViewById(R.id.txtDescripcion);
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

    private void AgregarProducto(){
        String resultado  = "";
        //EditText cantidadProducto = (EditText)getActivity().findViewById();
        String  ProductoId;
        int TotalProducto = 0;

        TotalProducto = Integer.parseInt(cantidadProducto.getText().toString());
        ProductoId = Producto.getText().toString();
        if (ProductoId.isEmpty())
        {
            Toast.makeText(getApplicationContext(), "Seleccione uno de los Productos", Toast.LENGTH_LONG).show();
        }
        else{
            try {
                mjason.put("cantidad", TotalProducto);
                mjason.put("producto", ProductoId);
                ProductosAgregados++;
            } catch (JSONException error) {
            }
        }
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

        final List<String> ID;
        ID = new ArrayList<String>();

        final List<String> NombreProducto;
        NombreProducto = new ArrayList<String>();

        final List<String> PrecioProducto;
        PrecioProducto = new ArrayList<>();

        final List<String> ClaveProducto;
        ClaveProducto = new ArrayList();
        //ArrayList<singleRow> singlerow = new ArrayList<>();

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
                //singlerow.add(new singleRow(DesLarga, idArticulo));
            }
            //mRecyclerView = findViewById(R.id.recyclerView);
            //mRecyclerView.setHasFixedSize(true);
            //mLayoutManager = new LinearLayoutManager(this);
            //mAdapter = new AdaptadorProducto(singlerow);
            //mRecyclerView.setLayoutManager(mLayoutManager);
            //mRecyclerView.setAdapter(mAdapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        final ListAdapterProductos adapterP = new ListAdapterProductos(this,  ID, NombreProducto);
        list=(ListView)findViewById(R.id.list);
        list.setTextFilterEnabled(true);
        list.setAdapter(adapterP);
        Producto.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //adapterP.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
//        Agregado Mikel
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
               String  Descripcion = ID.get(i).toString();
               //String b = NombreProducto.get(i).toString();
               String paso= ClaveProducto.get(i).toString();
               Producto.setText(paso);
               txtDescripcion.setText(Descripcion);
            }
        });
    }


    private void CrearJSON() {
        btnAgregar = findViewById(R.id.btnAgregar);
        btnAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String  ProductoId;
                int TotalProducto = 0;

                TotalProducto = Integer.parseInt(cantidadProducto.getText().toString());
                ProductoId = Producto.getText().toString();
                if (ProductoId.isEmpty())
                {
                    Toast.makeText(getApplicationContext(), "Seleccione uno de los Productos", Toast.LENGTH_LONG).show();
                }
                else{
                    try {
                        mjason.put("cantidad", TotalProducto);
                        mjason.put("producto", ProductoId);
                        ProductosAgregados = ProductosAgregados +1;
                    } catch (JSONException error) {
                    }
                }
            }
        });

    }

}