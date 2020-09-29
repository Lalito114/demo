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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Lecturas extends AppCompatActivity {


    JSONObject mangueras;
    double txtlitrosElectronicos;
    Double resultado;
    String idManguera, idTurno, turnoAuxiliar, fechaTrabajo, dfpLEM, dfpLED, descripcombus;
    Double litrosMecanicos;
    ListView list;
    List<String> maintitle;
    List<String> subtitle;
    List<String> manguera;
    JSONArray litrosMecanico = new JSONArray();
    Button btnEnviarMecanicas;
    JSONObject mj;
    JSONArray mecanicasInicales = new JSONArray();
    String MecanicaApi;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecturas);


        ValidaLecturaMecanica();

        diferenciaPermitida();
        obtenerMecanicaInical(); ///////// CHECAR LA VARIABLE RESULTADO ////////

        btnEnviarMecanicas = findViewById(R.id.btnEnviarMecanicas);

        btnEnviarMecanicas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Lecturas Mecanicas finales Guardadas", Toast.LENGTH_LONG).show();
                EnviaValidaMecanicas();
                Intent intent = new Intent(getApplicationContext(),FajillasBilletes.class);
                startActivity(intent);
            }
        });

    }

    public void EnviaValidaMecanicas(){
        final String sucursalid = getIntent().getStringExtra("idsucursal");
        String url = "http://10.2.251.58/CorpogasService/api/lecturaMangueras/sucursal/1/LecturaInicialMecanica/isla/"+sucursalid;
        RequestQueue queue = Volley.newRequestQueue(this);
            try {
                mj = new JSONObject();
                JSONArray jsonArray = new JSONArray();

                mj.put("Correcto","");
                mj.put("Mensaje","");
                for (int i = 0; i <litrosMecanico.length(); i++) {
                    JSONObject mjs = new JSONObject();
                    mjs.put("SucursalId",sucursalid);
                    mjs.put("MangueraId", manguera.get(i));
                    mjs.put("TurnoId",idTurno);
                    mjs.put("TurnoAuxiliar", turnoAuxiliar);
                    mjs.put("FechaTrabajo", fechaTrabajo);
                    mjs.put("ContadorMecanico",litrosMecanico.get(i));
                    mjs.put("Respuesta","");
                    jsonArray.put(mjs);
                }
               mj.put("ObjetoRespuesta",jsonArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        JsonObjectRequest request_json = new JsonObjectRequest(Request.Method.GET, url, mj, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

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
                return Response.success(mj, HttpHeaderParser.parseCacheHeaders(response));
            }
        };
        queue.add(request_json);
    }

    public void ValidaLecturaMecanica(){
        final String sucursalid = getIntent().getStringExtra("idsucursal");

        String url = "http://10.2.251.58/CorpogasService/api/Consolas/estacion/"+sucursalid;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject lecturasMecanicas = new JSONObject(response);  //RegistrarLecturasMecanicas
                    String registroLecturaMecanica = lecturasMecanicas.getString("RegistrarLecturasMecanicas");
                    obtenerIsla(registroLecturaMecanica);


                } catch (Exception ex){
                    Toast.makeText(getApplicationContext(),ex.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),error.toString(), Toast.LENGTH_SHORT).show();

            }

        });
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);

    }

     public void CortesIslas(String idisla, final String validarLecturaMecanica ){
         final String sucursalid = getIntent().getStringExtra("idsucursal");

         String url = "http://10.2.251.58/CorpogasService/api/lecturaMangueras/sucursal/" + sucursalid + "/CorteManguera/isla/" + idisla;

         StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
             @Override
             public void onResponse(String response) {

                 obtenerDatos(response, validarLecturaMecanica);
             }
         }, new Response.ErrorListener() {
             @Override
             public void onErrorResponse(VolleyError error) {
                 Toast.makeText(getApplicationContext(),error.toString(), Toast.LENGTH_SHORT).show();

             }

         });
         RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
         requestQueue.add(stringRequest);

     }

    private void obtenerDatos(String response, String validarLecturaMecanica) {
        if (validarLecturaMecanica == "true") {

            maintitle = new ArrayList<String>();
            subtitle = new ArrayList<String>();
            manguera = new ArrayList<String>();

            try {
                JSONArray lecturas = new JSONArray(response);

                for (int i = 0; i < lecturas.length(); i++) {
                    mangueras = lecturas.getJSONObject(i);
                    idManguera = mangueras.getString("MangueraId");
                    idTurno = mangueras.getString("TurnoId");
                    turnoAuxiliar = mangueras.getString("TurnoAuxiliar");
                    fechaTrabajo = mangueras.getString("FechaTrabajo");
                    String tipoCombustible = mangueras.getString("EstacionCombustible");
                    String valorInicial = mangueras.getString("ValorInicial");
                    String valorFinal = mangueras.getString("ValorFinal");
                    resultado = Double.parseDouble(valorFinal) - Double.parseDouble(valorInicial);

                    JSONObject estacionCombustible = new JSONObject(tipoCombustible);
                    String combustible = estacionCombustible.getString("Combustible");
                    JSONObject descripcionCombustible = new JSONObject(combustible);
                    descripcombus = descripcionCombustible.getString("DescripcionCorta");

                    maintitle.add("Tipo Combustible: " + descripcombus);
                    subtitle.add("000.00");
                    manguera.add(idManguera);

                }
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
            }
            final ListAdapter adapter = new ListAdapter(this, maintitle, subtitle, manguera);
            list = (ListView) findViewById(R.id.list);
            list.setAdapter(adapter);

            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                    try {

                        final EditText input = new EditText(getApplicationContext());
                        input.setTextColor(Color.BLACK);
                        input.setGravity(Gravity.CENTER);
                        input.setTextSize(22);
                        input.setInputType(InputType.TYPE_CLASS_NUMBER);

                        final AlertDialog.Builder builder = new AlertDialog.Builder(Lecturas.this);
                        builder.setTitle("Ingresa Lectura Mecanica  \n");
                        builder.setView(input)
                                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        String lectura = input.getText().toString();
                                        if (lectura.isEmpty()) {
                                            Toast.makeText(getApplicationContext(), "Favor de poner una cantidad", Toast.LENGTH_LONG).show();
                                        } else {
                                            try {
                                                String ray = mecanicasInicales.get(position).toString();
                                                MecanicaApi = ray;
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                            double lecturaMecanica = Double.parseDouble(lectura);
                                            if (Double.parseDouble(MecanicaApi) <= lecturaMecanica) {
                                                litrosMecanicos = lecturaMecanica - Double.parseDouble(MecanicaApi);
                                                litrosMecanico.put(litrosMecanicos);
                                                if (((resultado + Double.parseDouble(dfpLEM)) >= litrosMecanicos) && ((resultado - Double.parseDouble(dfpLEM)) <= litrosMecanicos)) {
//                                                    Toast.makeText(Lecturas.this, "la lectura fue: " + lectura, Toast.LENGTH_SHORT).show();

                                                    subtitle.set(position, lectura);
                                                    ListAdapter adapter = new ListAdapter(Lecturas.this, maintitle, subtitle, manguera);
                                                    list.setAdapter(adapter);
                                                    Toast.makeText(getApplicationContext(), "Lectura ingresada correctamente", Toast.LENGTH_LONG).show();

                                                } else {
                                                    Toast.makeText(getApplicationContext(), "ERROR 202: La diferencia de litros esta fuera de lo Permitido", Toast.LENGTH_LONG).show();
                                                }

                                            } else {
                                                Toast.makeText(getApplicationContext(), "ERROR 201: No puedes tener menos litros que la Lectura Mecanica Inicial", Toast.LENGTH_LONG).show();
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
        } else {
            Toast.makeText(getApplicationContext(), "Comparar lecturas electronicas con suma despacho",Toast.LENGTH_LONG).show();
        }
    }
    public void obtenerIsla(final String validarLecturaMecanica) {
        final String pass = getIntent().getStringExtra("password");
        final String sucursalid = getIntent().getStringExtra("idsucursal");

        String url = "http://10.2.251.58/CorpogasService/api/estacionControles/estacion/"+ sucursalid +"/ClaveEmpleado/" +pass;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONArray  jsonArray = new JSONArray(response);
                    for (int i = 0; i <jsonArray.length() ; i++) {
                        JSONObject slt1 = jsonArray.getJSONObject(i);
                        String idisla = slt1.getString("IslaId");
                        String idturno = slt1.getString("TurnoId");
                        CortesIslas(idisla, validarLecturaMecanica);

//                        enviarDatos(idisla);
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

    public void diferenciaPermitida(){
        final String sucursalid = getIntent().getStringExtra("idsucursal");

        String url = "http://10.2.251.58/CorpogasService/api/diferenciapermitidas/estacion/" + sucursalid;
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                    @Override
                        public void onResponse(String response) {
                            try{
                                JSONObject slt2 = new JSONObject(response);
                                dfpLEM = slt2.getString("LecturasElectronicasMecanicas");
                                dfpLED = slt2.getString("LecturasElectronicasDespachos");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                    }
                     }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_SHORT).show();
                        }
            });
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(stringRequest);

    }

    public void obtenerMecanicaInical(){
        final String sucursalid = getIntent().getStringExtra("idsucursal");
        String url = "http://10.2.251.58/CorpogasService/api/lecturaMangueras/sucursal/"+sucursalid+"/LecturaInicialMecanica/isla/1";

            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try{
                                JSONObject lecturaMecaIni = new JSONObject(response);
                                JSONArray arrayMecaIni = lecturaMecaIni.getJSONArray("ObjetoRespuesta");
                                for (int i = 0; i < arrayMecaIni.length(); i++) {
                                    JSONObject obj1 = arrayMecaIni.getJSONObject(i);
                                    String mecanicaInicial = obj1.getString("ValorInicial");
                                    Double mecanicaIni = Double.parseDouble(mecanicaInicial);
                                    mecanicasInicales.put(mecanicaIni);
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_SHORT).show();

                }
            });
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(stringRequest);
    }

    }







