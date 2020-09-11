package com.szzcs.smartpos.Gastos;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.szzcs.smartpos.Munu_Principal;
import com.szzcs.smartpos.Productos.ListAdapterProductos;
import com.szzcs.smartpos.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class cargaGasto extends AppCompatActivity {
    //Declaracion de variables
    ListView list;
    TextView txtDescripcion, txtClave, isla, turno, usuario;
    TextView SubTotal, Iva, total, Descripcion;
    String EstacionId = "1";
    String sucursalId = "1";
    String idisla, idTurno;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carga_gasto);


        txtDescripcion= findViewById(R.id.txtDescripcion);
        txtClave= findViewById(R.id.txtClave);
        isla=findViewById(R.id.isla);
        turno=findViewById(R.id.turno);
        usuario=findViewById(R.id.usuario);
        SubTotal=findViewById(R.id.SubTot);
        Iva=findViewById(R.id.Iva);
        total=findViewById(R.id.total);
        Descripcion=findViewById(R.id.Descripcion);

        isla.setText(getIntent().getStringExtra("isla"));
        //obtenerIsla();
        Button guardaGasto= findViewById(R.id.btnEnviar);

        guardaGasto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //se asignan valores
                if (txtClave.length() == 0)       //.length() >0)
                {
                    Toast.makeText(getApplicationContext(), "Seleccione al menos uno de los tipos de gasto", Toast.LENGTH_LONG).show();
                } else {
                    if (Descripcion.length() == 0) {
                        Toast.makeText(getApplicationContext(), "Dijite una descripción", Toast.LENGTH_LONG).show();

                    } else {
                        if (SubTotal.length() == 0) {
                            Toast.makeText(getApplicationContext(), "Digite el Subtotal", Toast.LENGTH_LONG).show();
                        } else {
                            if(Iva.length()==0){
                                Toast.makeText(getApplicationContext(), "Digite el IVA", Toast.LENGTH_LONG).show();
                            }else {
                                EnviarGastos();
                            }
                        }
                    }
                }
            }
        });
        cargaTipoGastos();
    }






    public void obtenerIsla() {
        final String pass = isla.getText().toString();


        String url = "http://10.2.251.58/CorpogasService/api/estacionControles/estacion/"+ EstacionId  +"/ClaveEmpleado/" +pass;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i <jsonArray.length() ; i++) {
                        JSONObject claveusuario = jsonArray.getJSONObject(i);
                        idisla = claveusuario.getString("IslaId");
                        idTurno= claveusuario.getString("TurnoId");

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),error.toString(), Toast.LENGTH_LONG).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }


    private void EnviarGastos() {
        String islaId = isla.getText().toString(); //getIntent().getStringExtra("isla");
        String turnoId = "1";//getIntent().getStringExtra("turno");


        //SQLiteBD data = new SQLiteBD(getApplicationContext());
        //String URL = "http://"+data.getIpEstacion()+"/CorpogasService/api/tanqueLleno/EnviarProductos";
        String URL = "http://10.2.251.58/CorpogasService/api/Gastos";
        final JSONObject mjason = new JSONObject();
        RequestQueue queue = Volley.newRequestQueue(this);
        try {
            mjason.put("EstacionId",EstacionId);
            mjason.put("IslaId", islaId);
            mjason.put("IslaEstacionId",EstacionId);
            mjason.put("TurnoId", turnoId); //turno.getText().toString());
            mjason.put("TurnoSucursalId",sucursalId); //turno.getText().toString());Sucursal
            mjason.put("ConceptoGastoId",txtClave.getText().toString());
            mjason.put("Descripcion", Descripcion.getText().toString());
            mjason.put("Subtotal", SubTotal.getText().toString());
            mjason.put("Iva", Iva.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request_json = new JsonObjectRequest(Request.Method.POST, URL, mjason, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Intent intente = new Intent(getApplicationContext(), Munu_Principal.class);
                startActivity(intente);
                Toast.makeText(getApplicationContext(),"Gasto Cargado Exitosamente",Toast.LENGTH_LONG).show();
                //Toast.makeText(getApplicationContext(),response.toString(),Toast.LENGTH_LONG).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            public Map<String,String>getHeaders() throws AuthFailureError{
                Map<String,String> headers = new HashMap<String, String>();
                return headers;
            }
            protected  Response<JSONObject> parseNetwokResponse(NetworkResponse response){
                if (response != null){

                    try {
                        String responseString;
                        JSONObject datos = new JSONObject();
                        responseString = new String(response.data,HttpHeaderParser.parseCharset(response.headers));

                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
                return Response.success(mjason, HttpHeaderParser.parseCacheHeaders(response));
            }
        };
        queue.add(request_json);

    }


    private void cargaGasto()  {

        final JSONObject mjason = new JSONObject();
        try {
            mjason.put("EstacionId",EstacionId);
            mjason.put("IslaId", isla.getText().toString());
            mjason.put("IslaEstacionId",EstacionId);
            mjason.put("TurnoId", "1"); //turno.getText().toString());
            mjason.put("TurnoSucursalId", "1"); //turno.getText().toString());
            mjason.put("ConceptoGastoId",txtClave.getText().toString());
            mjason.put("Descripcion", Descripcion.getText().toString());
            mjason.put("Subtotal", SubTotal.getText().toString());
            mjason.put("Iva", Iva.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = "http://10.2.251.58/CorpogasService/api/Gastos";
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
                params.put("", mjason.toString());  //mjason.toString()
                return params;
            }
        };

        // Añade la peticion a la cola
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(eventoReq);
    }


    private void cargaTipoGastos() {
        String url = "http://10.2.251.58/CorpogasService/api/ConceptoGastos";
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
        //Declaracion de variables
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
                String idtipoPago = p1.getString("NumeroInterno");
                String DesLarga = p1.getString("DescripcionCorta");
                NombreProducto.add("ID: " + idtipoPago );
                ID.add(DesLarga);
                ClaveProducto.add(idtipoPago);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final ListAdapterProductos adapterP = new ListAdapterProductos(this,  ID, NombreProducto);
        list=(ListView)findViewById(R.id.list);
        list.setTextFilterEnabled(true);
        list.setAdapter(adapterP);
//        Agregado  click en la lista
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String  Descripcion = ID.get(i).toString();
                //String precioUnitario = PrecioProducto.get(i).toString();
                String clave= ClaveProducto.get(i).toString();
                txtDescripcion.setText(Descripcion);
                txtClave.setText(clave);
            }
        });
    }


    private void CerrarTecladoMovil(){
        View view = this.getCurrentFocus();
        if (view.equals(null) ){ //!= null
            InputMethodManager item = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            item.hideSoftInputFromWindow(view.getWindowToken(), 0 );
        }else{
            InputMethodManager item = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            item.hideSoftInputFromWindow(view.getWindowToken(), 0 );

        }
    }

}