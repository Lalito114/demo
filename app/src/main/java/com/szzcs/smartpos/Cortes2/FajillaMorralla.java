package com.szzcs.smartpos.Cortes2;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.szzcs.smartpos.configuracion.SQLiteBD;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class FajillaMorralla extends AppCompatActivity {

    EditText fajillasMorralla;
    Button btnFajillasMorralla;
    String precioFajilla;
    int fajillaMorralla;
    String morralla;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fajilla_morralla);
        valorFajilla();

        fajillasMorralla = (EditText) findViewById(R.id.editFajillasMorralla);
        btnFajillasMorralla = (Button) findViewById(R.id.btnFajillasMorralla);



        btnFajillasMorralla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                morralla = fajillasMorralla.getText().toString();

                if(morralla.isEmpty()){

                    Toast.makeText(getApplicationContext(),"ERROR 401: Ingresar Numero de Fajillas de Morralla",Toast.LENGTH_LONG).show();
                }
                    int dineroMorralla = Integer.parseInt(morralla) * fajillaMorralla;
                    Toast.makeText(getApplicationContext(),"Fue un Total de "+ dineroMorralla + " pesos",Toast.LENGTH_LONG).show();
                    enviarFolios();
            }
        });



    }
    public void valorFajilla(){
//        final String sucursalid = getIntent().getStringExtra("idsucursal");
        SQLiteBD data = new SQLiteBD(FajillaMorralla.this);

        String url = "http://"+data.getIpEstacion()+"/CorpogasService/api/PrecioFajillas/Sucursal/"+data.getIdSucursal();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray array = new JSONArray(response);

                    for (int i = 0; i < array.length() ; i++) {
                        JSONObject sl1 = array.getJSONObject(i);
                        String tipoFajilla = sl1.getString("TipoFajillaId");
                        precioFajilla = sl1.getString("Precio");
                        if(tipoFajilla.equals("2")){
                            fajillaMorralla = Integer.parseInt(precioFajilla);
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
        final String origenId = getIntent().getStringExtra("origenId");
        final String cierreId = getIntent().getStringExtra("cierreId");
        // String islaId = isla.getText().toString(); //getIntent().getStringExtra("isla");
        // String turnoId = "1";//getIntent().getStringExtra("turno");
        SQLiteBD data = new SQLiteBD(FajillaMorralla.this);
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
            mjason.put("TipoFajillaId","2");
            mjason.put("FolioInicial", '0');
            mjason.put("FolioFinal", morralla);
            mjason.put("Denominacion", fajillaMorralla);
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
                        AlertDialog.Builder builder = new AlertDialog.Builder(FajillaMorralla.this);
                        builder.setTitle("CorpoApp");
                        builder.setMessage("Los datos se guardaron corretamente");
                        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(getApplicationContext(),SubOfiBilletes.class);
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
                        AlertDialog.Builder builder = new AlertDialog.Builder(FajillaMorralla.this);
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
//                Intent intente = new Intent(getApplicationContext(), Munu_Principal.class);
//                startActivity(intente);
                Toast.makeText(getApplicationContext(),"Gasto Cargado Exitosamente",Toast.LENGTH_LONG).show();
                //Toast.makeText(getApplicationContext(),response.toString(),Toast.LENGTH_LONG).show();
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