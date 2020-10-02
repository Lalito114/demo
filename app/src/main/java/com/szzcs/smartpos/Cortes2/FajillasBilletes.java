package com.szzcs.smartpos.Cortes2;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.szzcs.smartpos.TanqueLleno.PosicionCargaTLl;
import com.szzcs.smartpos.TanqueLleno.ProductoTLl;
import com.szzcs.smartpos.configuracion.SQLiteBD;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class FajillasBilletes extends AppCompatActivity {

    // Se declaran las variables que se usaran en este Activity
    String precioFajilla, origenId, cierreId, inicial, foliof;
    EditText folioInicial, folioFinal;
    public int dineroBilletes;
    Button btnValidaFajillas;
    int fajillaBillete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fajillas_billetes);

        valorFajilla();
        obtenerDatosOrigenCierre();


        // Hacemos la relacion de las variables con los objetos del layout
        folioInicial = (EditText) findViewById(R.id.editFolioInicialBilletes);
        folioFinal = (EditText) findViewById(R.id.editFolioFinalBilletes);
        btnValidaFajillas = (Button) findViewById(R.id.btnFajillaBilletes);

        // Este sera el comportamiento del boton cuando el Usuario le de click
        btnValidaFajillas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                inicial = folioInicial.getText().toString();
                foliof = folioFinal.getText().toString();
                // Se valida que el Folio Inicial y el Folio final no esten vacios.
                if (inicial.isEmpty() || foliof.isEmpty()){

                    Toast.makeText(getApplicationContext(),"Error 301: Se requiere un Folio Inicial y Folio Final",Toast.LENGTH_LONG).show();
                }else{
                    // Se valida si el Folio Final es menor o igual que el Folio Inicial
                    if(Integer.parseInt(foliof) <= Integer.parseInt(inicial)){
                        Toast.makeText(getApplicationContext(),"Error 302: Verifica tus Numeros de Folio",Toast.LENGTH_LONG).show();
                    }else{
                        // Restamos el Folio Final y el Folio Inicial. Al resultado de esta operacion le sumamos un 1
                        int folios = (Integer.parseInt(foliof) - Integer.parseInt(inicial)) + 1;
                        // Se multiplica el resultado de la resta por el valor de la Fajilla
                        dineroBilletes = folios * fajillaBillete;
                        Toast.makeText(FajillasBilletes.this, "Fue un Total de " + dineroBilletes + " pesos", Toast.LENGTH_LONG).show();
                        enviarFolios();

                    }
                }

            }
        });

    }
    // Se crea un metodo
    private void obtenerDatosOrigenCierre() {
        // Creamos la variable data para poder llamar a las variables que usaremos de la clase SQLiteBD
        final SQLiteBD data = new SQLiteBD(getApplicationContext());
        // Declaramos la URl que se ocupara para el metodo obtenerDatosOrigenCierre
        String url = "http://"+data.getIpEstacion()+"/CorpogasService/api/cierres/registrar/sucursal/"+data.getIdSucursal()+"/isla/1/usuario/1/origen/1";
        // Utilizamos el metodo Post para obtener OrigenID y ID
        StringRequest eventoReq = new StringRequest(Request.Method.POST,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject responce = new JSONObject(response);
                            String respuesta = responce.getString("ObjetoRespuesta");
                            JSONObject objetorespues = new JSONObject(respuesta);
                           origenId =objetorespues.getString("OrigenId");
                            cierreId = objetorespues.getString("Id");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    // Funcion para capturar errores
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
                return params;
            }
        };

        // Añade la peticion a la cola
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(eventoReq);

    }
    // Se crea el metodo valorFajilla
    public void valorFajilla(){
        // Creamos la variable data para poder llamar a las variables que usaremos de la clase SQLiteBD
        final SQLiteBD data = new SQLiteBD(getApplicationContext());
        String url = "http://"+data.getIpEstacion()+"/CorpogasService/api/PrecioFajillas/Sucursal/1";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray array = new JSONArray(response);

                    for (int i = 0; i < array.length() ; i++) {
                        JSONObject sl1 = array.getJSONObject(i);
                        String tipoFajilla = sl1.getString("TipoFajillaId");
                        precioFajilla = sl1.getString("Precio");
                            if(tipoFajilla.equals("1")){
                                fajillaBillete = Integer.parseInt(precioFajilla);
                            }

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    private void enviarFolios() {
        // String islaId = isla.getText().toString(); //getIntent().getStringExtra("isla");
        // String turnoId = "1";//getIntent().getStringExtra("turno");
        final SQLiteBD data = new SQLiteBD(getApplicationContext());
        String URL = "http://"+data.getIpEstacion()+"/CorpogasService/api/Fajillas/GuardaFoliosCierreFajillas/usuario/1";
        final JSONObject mjason = new JSONObject();
        RequestQueue queue = Volley.newRequestQueue(this);
        try {
            mjason.put("CierreId",cierreId);
            mjason.put("CierreSucursalId", 1); //turno.getText().toString());
            JSONObject prueba = new JSONObject();
            prueba.put("IslaId", "1");
            mjason.put("Cierre",prueba);//turno.getText().toString());Sucursal
            mjason.put("SucursalId", "1");
            mjason.put("TipoFajillaId","1");
            mjason.put("FolioInicial", inicial);
            mjason.put("FolioFinal", foliof);
            mjason.put("Denominacion", fajillaBillete);
            mjason.put("OrigenId", origenId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request_json = new JsonObjectRequest(Request.Method.POST, URL, mjason, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String estado  = response.getString("Correcto");
                    if (estado == "true"){
                        AlertDialog.Builder builder = new AlertDialog.Builder(FajillasBilletes.this);
                        builder.setTitle("CorpoApp");
                        builder.setMessage("Los datos se guardaron corretamente");
                        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(getApplicationContext(),FajillaMorralla.class);
                                intent.putExtra("dinero",dineroBilletes);
                                intent.putExtra("fajillaBillete",fajillaBillete);
                                intent.putExtra("origenId",origenId);
                                intent.putExtra("cierreId",cierreId);
                                startActivity(intent);
                                finish();
                            }
                        });
                        AlertDialog dialog= builder.create();
                        dialog.show();

                    }else{
                        String mensaje  = response.getString("Mensaje");
                        AlertDialog.Builder builder = new AlertDialog.Builder(FajillasBilletes.this);
                        builder.setTitle("Error");
                        builder.setMessage(mensaje);
                        builder.setNegativeButton("Cerrar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                        AlertDialog dialog= builder.create();
                        dialog.show();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Toast.makeText(getApplicationContext(),"Gasto Cargado Exitosamente",Toast.LENGTH_LONG).show();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            public Map<String,String> getHeaders() throws AuthFailureError {
                Map<String,String> headers = new HashMap<String, String>();
                return headers;
            }
            protected  Response<JSONObject> parseNetwokResponse(NetworkResponse response){
                if (response != null){

                    try {
                        String responseString;
                        JSONObject datos = new JSONObject();
                        responseString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));

                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
                return Response.success(mjason, HttpHeaderParser.parseCacheHeaders(response));
            }
        };
        queue.add(request_json);

    }


}