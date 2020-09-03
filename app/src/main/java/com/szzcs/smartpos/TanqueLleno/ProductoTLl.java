package com.szzcs.smartpos.TanqueLleno;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
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
import com.szzcs.smartpos.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductoTLl extends AppCompatActivity {
    ListView list;
    EditText litros, pesos;
    Button agregarcombustible, agregarProductos, enviarProductos;
    String IdCombustible, cs,numneroInterno,  descripcion, precio ;
    final JSONObject datos = new JSONObject();

    JSONArray array1 = new JSONArray();

    //Create json objects for two filter Ids
    JSONObject jsonParam = new JSONObject();

    String NumeroInternoSucursal,SucursalEmpleadoId,PosicionDeCarga,NumeroDeTarjeta,ClaveTanqueLleno,TipoCliente;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_producto_tll);

        NumeroInternoSucursal = getIntent().getStringExtra("NumeroInternoEstacion");
        SucursalEmpleadoId = getIntent().getStringExtra("SucursalEmpleadoId");
        PosicionDeCarga = getIntent().getStringExtra("PosicioDeCarga");
        NumeroDeTarjeta = getIntent().getStringExtra("NumeroDeTarjeta");
        ClaveTanqueLleno = getIntent().getStringExtra("ClaveTanqueLleno");
        TipoCliente = getIntent().getStringExtra("Tipocliente");


        Combustibles();



        agregarcombustible = findViewById(R.id.btnAgregarcombustible);
        agregarcombustible.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    //Add string params
                    litros = findViewById(R.id.edtLitros);
                    String litro =litros.getText().toString();
                    jsonParam.put("TipoProducto","1");
                    jsonParam.put("ProductoId",cs);
                    jsonParam.put("NumeroInterno",numneroInterno);
                    jsonParam.put("Descripcion",descripcion);
                    jsonParam.put("Cantidad",litro);
                    jsonParam.put("Precio",precio);
                    array1.put(jsonParam);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        enviarProductos = findViewById(R.id.btnenciarProductos);
        enviarProductos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EnviarProductos1();
            }
        });

    }

    private void EnviarProductos1() {
        String URL = "http://10.0.1.20/CorpogasService/api/tanqueLleno/EnviarProductos";
        RequestQueue queue = Volley.newRequestQueue(this);

        try {

            datos.put("NumeroInternoSucursal", NumeroInternoSucursal);
            datos.put("EstacionId","1");
            datos.put("SucursalEmpleadoId",SucursalEmpleadoId);
            datos.put("PosicionCarga",PosicionDeCarga);
            datos.put("TarjetaCliente", NumeroDeTarjeta);
            datos.put("Placas", "");
            datos.put("Odometro","0");
            datos.put( "ClaveTanqueLleno", ClaveTanqueLleno);
            datos.put("Nip", "0e674a918ebca3f78bfe02e2f387689d");
            datos.put("TipoCliente", TipoCliente);
            datos.put("Productos", array1);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request_json = new JsonObjectRequest(Request.Method.POST, URL, datos, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

            Toast.makeText(getApplicationContext(),response.toString(),Toast.LENGTH_LONG).show();
                try {
                    String validacion = response.getString("Correcto");
                    String transaccion = response.getString("TransaccionId");
                    String folio = response.getString("Folio");
                    if (validacion == "true"){
                            enviarDatosCuerpoTicket(transaccion,folio);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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
                return Response.success(datos, HttpHeaderParser.parseCacheHeaders(response));
            }
        };
        queue.add(request_json);

    }

    private void enviarDatosCuerpoTicket(final String Transaccion, final String folio) {
        String url = "http://10.0.1.20/CorpogasService/api/tickets/generar";
        //Utilizamos el metodo Post para colocar los datos en el  ticket
        StringRequest eventoReq = new StringRequest(Request.Method.POST,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                    }
                    //funcion para capturar errores
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
                params.put("PosCarga",PosicionDeCarga);
                params.put("IdUsuario",SucursalEmpleadoId);
                params.put("IdFormaPago","1");
                params.put("TipoMonedero","1");
                params.put("TransaccionId",Transaccion);
                params.put("SucursalId","1");
                params.put("Folio",folio);
                params.put("Clave",ClaveTanqueLleno);

                return params;
            }
        };

        // Añade la peticion a la cola
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(eventoReq);
    }

    private void Combustibles() {
        String url = "http://10.0.1.20/CorpogasService/api/precioCombustibles/estacion/1/actuales";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                MetodoResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),error.toString(), Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> parametros = new HashMap<String, String>();

                return parametros;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this.getApplicationContext());
        requestQueue.add(stringRequest);

    }
    private void MetodoResponse(final String response) {
        List<String> maintitle;
        maintitle = new ArrayList<String>();

        List<String> subtitle;
        subtitle = new ArrayList<String>();

        List<Integer> imgid;
        imgid = new ArrayList<>();

        try {
            JSONArray combusti = new JSONArray(response);
            for (int i = 0; i <combusti.length() ; i++) {

                JSONObject conbustibles = combusti.getJSONObject(i);
                String activo = conbustibles.getString("Aplicado");
                if (activo == "true"){
                    cs = conbustibles.getString("EstacionCombustibleId");


                    precio = conbustibles.getString("Importe");

                    String combus = conbustibles.getString("EstacionCombustible");
                    JSONObject combustib = new JSONObject(combus);
                    numneroInterno = combustib.getString("NumeroInterno");
                    String combustible = combustib.getString("Combustible");


                    JSONObject nombre = new JSONObject(combustible);
                    descripcion = nombre.getString("DescripcionLarga");
                }




                maintitle.add(descripcion);
                subtitle.add("Precio: $"+precio);
                if (cs == "1"){
                    imgid.add(R.drawable.premium);
                }else{
                    if (cs == "2"){
                        imgid.add(R.drawable.magna);
                    }else{
                        if (cs == "3"){
                            imgid.add(R.drawable.diesel);
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //ListAdapterP adapterP=new ListAdapterP(this, maintitle, subtitle,imgid);
        ListAdapterConbustiblesTLl adapterP = new ListAdapterConbustiblesTLl(this, maintitle, subtitle, imgid);
        list=(ListView)findViewById(R.id.list);
        list.setAdapter(adapterP);


        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                switch (position){
                    case 0:
                        IdCombustible = "2";
                        break;
                    case 1:
                        IdCombustible ="1";
                        break;
                    case 2:
                        IdCombustible ="3";
                        break;
                     default:
                         IdCombustible = "0";
                }
            }
        });
    }
}
