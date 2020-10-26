package com.szzcs.smartpos.Cortes2;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
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
import com.szzcs.smartpos.configuracion.SQLiteBD;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SubOfiBilletes extends AppCompatActivity {
    ListView mListView;
    List<String> maintitle;
    List<String> subtitle;
    List<String> total;
    JSONObject denom;
    String denominacion;
    ArrayList arrayMonto = new ArrayList();
    Double result;
    Button btnaceptar;
    JSONArray prueba2 = new JSONArray();
    double sumainter;
    String precioFajilla;
    int fajillaBillete;
    String denomi;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_ofi_billetes);

        denominacionBilletes();
        valorFajilla();
        btnaceptar = findViewById(R.id.aceptarBilletes);

        btnaceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sumainter <= (fajillaBillete-10)){
                    Toast.makeText(SubOfiBilletes.this, "Se registro un Total de "+ sumainter + " pesos", Toast.LENGTH_LONG).show();

                    for (int i = 0; i < maintitle.size(); i++) {
                        if(!maintitle.get(i).equals("0")){
                            enviarFolios(maintitle.get(i), String.valueOf(arrayMonto.get(i)));
                        }
                    }
                }else{
                    Toast.makeText(SubOfiBilletes.this, "Existe un error", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void denominacionBilletes() {
        final SQLiteBD data = new SQLiteBD(getApplicationContext());
        String url = "http://"+data.getIpEstacion()+"/CorpogasService/api/DineroDenominaciones";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                subtotalOficinaBilletes(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();

            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);

    }

    private void subtotalOficinaBilletes(String response) {

        maintitle = new ArrayList<String>();
        subtitle = new ArrayList<String>();
        total = new ArrayList<String>();

        try {

            JSONArray stl = new JSONArray(response);

            for (int i = 0; i < stl.length(); i++) {
                denom = stl.getJSONObject(i);
                denominacion = denom.getString("Importe");
                double monto = Double.parseDouble(denominacion);
                arrayMonto.add(monto);

                maintitle.add("0");
                subtitle.add(denominacion);
                total.add("");

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        final ListAdapterBilletes adapter = new ListAdapterBilletes(this, maintitle, subtitle, total);
        mListView = (ListView) findViewById(R.id.list);
        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                final String ray = arrayMonto.get(position).toString();

                try {
                    final EditText input = new EditText(getApplicationContext());
                    input.setTextColor(Color.BLACK);
                    input.setGravity(Gravity.CENTER);
                    input.setTextSize(22);
                    input.setInputType(InputType.TYPE_CLASS_NUMBER);

                    AlertDialog.Builder builder = new AlertDialog.Builder(SubOfiBilletes.this);
                    builder.setTitle("Ingresa Cantidad \n");
                    builder.setView(input)

                            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    denomi = input.getText().toString();
                                    result = Double.parseDouble(denomi) * Double.parseDouble(ray);
                                    if (result > fajillaBillete) {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(SubOfiBilletes.this);
                                        builder.setTitle("Error");
                                        builder.setMessage("No puede superar el valor de 1 Fajilla");
                                        builder.setNegativeButton("Cerrar", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.dismiss();
                                            }
                                        });
                                        AlertDialog dialog= builder.create();
                                        dialog.show();

                                    } else {
                                        maintitle.set(position, denomi);
                                        total.set(position, String.valueOf(result));
                                        final String ray2 = result.toString();

                                        sumainter = totalBilletes() + Double.parseDouble(ray2);

                                        if ((totalBilletes()<= (fajillaBillete-10)) && (sumainter <= (fajillaBillete-10))){
                                            Toast.makeText(SubOfiBilletes.this, "Cantidad Agregada", Toast.LENGTH_SHORT).show();
                                            prueba2.put(ray2);
                                            ListAdapterBilletes adapter = new ListAdapterBilletes(SubOfiBilletes.this, maintitle, subtitle, total);
                                            mListView.setAdapter(adapter);
                                        }else{
                                            AlertDialog.Builder builder = new AlertDialog.Builder(SubOfiBilletes.this);
                                            builder.setTitle("Error");
                                            builder.setMessage("Los Valores que ingresaste pueden ser 1 Fajilla");
                                            builder.setNegativeButton("Cerrar", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    dialogInterface.dismiss();
                                                }
                                            });
                                            AlertDialog dialog= builder.create();
                                            dialog.show();

                                        }

                                    }
                                }
                            })
                            .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            }).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public double totalBilletes() {
        double sumamax = 0;
        for (int i = 0; i < prueba2.length() ; i++) {
            double suma = 0;
            try {
                suma = prueba2.getDouble(i);
                sumamax += suma;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return sumamax;
    }

    public void valorFajilla(){
        final SQLiteBD data = new SQLiteBD(getApplicationContext());
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

    private void enviarFolios(String folio, String valor) {
        final String origenId = getIntent().getStringExtra("origenId");
        final String cierreId = getIntent().getStringExtra("cierreId");
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
            mjason.put("TipoFajillaId","3");
            mjason.put("FolioInicial", "0");
            mjason.put("FolioFinal", folio);
            mjason.put("Denominacion", valor);
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
                        AlertDialog.Builder builder = new AlertDialog.Builder(SubOfiBilletes.this);
                        builder.setTitle("CorpoApp");
                        builder.setMessage("Los datos se guardaron corretamente");
                        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
//                                Intent intent = new Intent(getApplicationContext(),TotalProductos.class);
//                                startActivity(intent);
//                                finish();
                            }
                        });
                        AlertDialog dialog= builder.create();
                        dialog.show();

                    }else{
                        String mensaje  = response.getString("Mensaje");
                        AlertDialog.Builder builder = new AlertDialog.Builder(SubOfiBilletes.this);
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