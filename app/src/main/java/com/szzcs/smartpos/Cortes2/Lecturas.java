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
import com.google.gson.Gson;
import com.szzcs.smartpos.Munu_Principal;
import com.szzcs.smartpos.R;
import com.szzcs.smartpos.configuracion.SQLiteBD;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Lecturas extends AppCompatActivity {

    String idManguera, idTurno, turnoAuxiliar, fechaTrabajo, dfpLEM, dfpLED, descripcombus, MecanicaApi;
    JSONArray mecanicasInicales = new JSONArray();
    JSONArray litrosMecanico = new JSONArray();
    List<ResultadoManguera> listaResultado = new ArrayList<ResultadoManguera>();
    List<EnviarMecanicaFinal> listEnviarMecanicas = new ArrayList<EnviarMecanicaFinal>();
    Double resultado, litrosMecanicos;
    Button btnEnviarMecanicas;
    List<String> maintitle;
    List<String> subtitle;
    List<String> manguera;
    JSONObject mangueras;
    JSONObject mj;
    ListView list;
    TextView txtIsla, txtTurno;
    //int turnoId;
    String idisla;
    String numeroInterno;
    List<Integer> lecturaMeIni = new ArrayList<Integer>();
    Cierre lstcierre;
    RespuestaApi<Cierre> cierreRespuestaApi;
    String islaId;
    String idusuario;
    SQLiteBD data;
    String nombreCompleto;
    String password;
    int mangueraActual;
    long cierreId;
    long turnoId;
    double lecturasElectronicasMecanicas;
    double LecturasElectronicasDespachos;
    boolean bandera = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecturas);
        //cierreArrayList2 = (ArrayList<Cierre>) getIntent().getSerializableExtra( "lcierreRespuestaApi");
        cierreRespuestaApi = (RespuestaApi<Cierre>) getIntent().getSerializableExtra("lcierreRespuestaApi");
        cierreId = cierreRespuestaApi.getObjetoRespuesta().getId();
        turnoId = cierreRespuestaApi.getObjetoRespuesta().getTurnoId();
        lecturasElectronicasMecanicas = cierreRespuestaApi.getObjetoRespuesta().Variables.DiferenciaPermitida.LecturasElectronicasMecanicas;
        LecturasElectronicasDespachos = cierreRespuestaApi.getObjetoRespuesta().Variables.DiferenciaPermitida.LecturasElectronicasDespachos;
        islaId = getIntent().getStringExtra("islaId");
        nombreCompleto = getIntent().getStringExtra("nombreCompleto");
        idusuario = getIntent().getStringExtra("idusuario");
        password = getIntent().getStringExtra("password");
        data = new SQLiteBD(getApplicationContext());
        ValidaLecturaMecanica();
//        diferenciaPermitida();
        obtenerMecanicaInical(); ///////// CHECAR LA VARIABLE RESULTADO ////////

        txtIsla = (TextView) findViewById(R.id.textIsla);
        txtTurno = (TextView) findViewById(R.id.textTurno);
        txtTurno.setText("Turno: " + turnoId);
        txtIsla.setText("Isla: " + islaId);

        btnEnviarMecanicas = findViewById(R.id.btnEnviarMecanicas);

        btnEnviarMecanicas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (litrosMecanico.isNull(0)) {
                    try {
                        AlertDialog.Builder builder = new AlertDialog.Builder(Lecturas.this);
                        builder.setTitle("Aviso: ");
                        builder.setMessage("Ingresa las Lectura Mecanicas");
                        builder.setNegativeButton("Cerrar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();

                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    if (litrosMecanico.length() == mecanicasInicales.length()) {
                        EnviaValidaMecanicas();
                    } else {
                        try {
                            // aqui mostrar las mangueras que faltan por capturar a peticion del puto manuel mal "echo"!!
                            AlertDialog.Builder builder = new AlertDialog.Builder(Lecturas.this);
                            builder.setTitle("Aviso: ");
                            builder.setMessage("Capturar todas las lecturas faltantes");
                            builder.setNegativeButton("Cerrar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();

                                }
                            });
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
//                Toast.makeText(getApplicationContext(), "Lecturas Mecanicas finales Guardadas", Toast.LENGTH_LONG).show();


            }
        });

    }

    public void EnviaValidaMecanicas() {

        String url = "http://" + data.getIpEstacion() + "/CorpogasService/api/cierreCarretes/cierre/isla/" + islaId;
        RequestQueue queue = Volley.newRequestQueue(this);

        try {
            mj = new JSONObject();
            JSONArray jsonArray = new JSONArray();

            mj.put("Correcto", "");
            mj.put("Mensaje", "");
            for (EnviarMecanicaFinal item : listEnviarMecanicas) {
                JSONObject mjs = new JSONObject();
                mjs.put("SucursalId", item.SucursalId);
                mjs.put("CierreId", cierreRespuestaApi.getObjetoRespuesta().getId());
                mjs.put("CierreSucursalId", item.SucursalId);
                mjs.put("MangueraId", item.MangueraId);
                mjs.put("MangueraEstacionId", item.MangueraEstacionId);
                mjs.put("CombustibleId", item.CombustibleId);
                mjs.put("ValorInicial", item.ValorInicial);
                mjs.put("ValorFinal", item.ValorFinal);
                jsonArray.put(mjs);
            }
            mj.put("ObjetoRespuesta", jsonArray);
//            RespuestaApi respuesta = new RespuestaApi(listEnviarMecanicas);
//            respuesta.Correcto = false;
//            respuesta.Mensaje = "";

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request_json = new JsonObjectRequest(Request.Method.POST, url, mj, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (response != null) {
                    try {
                        String correcto = response.getString("Correcto");
                        if (correcto == "true") {
                            String mensaje = response.getString("Mensaje");
                            AlertDialog.Builder builder = new AlertDialog.Builder(Lecturas.this);
                            builder.setTitle("CorpoApp");
                            builder.setMessage(mensaje);
                            builder.setPositiveButton("Cerrar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    Intent intent = new Intent(getApplicationContext(), VentasTotales.class); //VentasTotales.class
                                    intent.putExtra("islaId", islaId);
                                    intent.putExtra("idusuario", idusuario);
                                    intent.putExtra("lcierreRespuestaApi", cierreRespuestaApi);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        } else {
                            if (correcto == "false") {
                                final String respuesta = response.getString("ObjetoRespuesta");
                                String mensaje = response.getString("Mensaje");
                                AlertDialog.Builder builder = new AlertDialog.Builder(Lecturas.this);
                                builder.setTitle("ERROR");
                                builder.setMessage(mensaje);
                                builder.setNegativeButton("Cerrar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        try {
                                            JSONObject nopasan = new JSONObject(respuesta);
                                            String idcombustible = nopasan.getString("CombustibleId");
                                            String idmanguera = nopasan.getString("MangueraId");

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        dialogInterface.dismiss();
                                    }
                                });
                                AlertDialog dialog = builder.create();
                                dialog.show();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                return headers;
            }

        };
        queue.add(request_json);
    }

    public void ValidaLecturaMecanica() {


        String url = "http://" + data.getIpEstacion() + "/CorpogasService/api/Consolas/estacion/" + data.getIdEstacion();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject lecturasMecanicas = new JSONObject(response);  //RegistrarLecturasMecanicas
                    String registroLecturaMecanica = lecturasMecanicas.getString("RegistrarLecturasMecanicas");
                    obtenerIsla(registroLecturaMecanica);


                } catch (Exception ex) {
                    Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                }

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

    public void CortesIslas(String idisla, final String validarLecturaMecanica) {
        final SQLiteBD data = new SQLiteBD(getApplicationContext());

        String url = "http://" + data.getIpEstacion() + "/CorpogasService/api/lecturaMangueras/sucursal/" + data.getIdSucursal() + "/CorteManguera/isla/" + islaId;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                obtenerDatos(response, validarLecturaMecanica);
                // obtenerTurno();
//                obtenerInternoIsla();
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
                    //turnoId = mangueras.getInt("TurnoId");
                    String valorInicial = mangueras.getString("ValorInicial");
                    String valorFinal = mangueras.getString("ValorFinal");
                    resultado = Double.parseDouble(valorFinal) - Double.parseDouble(valorInicial);
                    ResultadoManguera temp = new ResultadoManguera();
                    temp.manguera = Integer.parseInt(idManguera);
                    temp.resultado = resultado;
                    listaResultado.add(temp);

                    JSONObject estacionCombustible = new JSONObject(tipoCombustible);
                    String combustible = estacionCombustible.getString("Combustible");
                    JSONObject descripcionCombustible = new JSONObject(combustible);
                    descripcombus = descripcionCombustible.getString("DescripcionCorta");

                    maintitle.add(descripcombus);
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
                    boolean nuevo = true;
                    for (EnviarMecanicaFinal item : listEnviarMecanicas) {
                        if (item.posicion == position) {
                            //listEnviarMecanicas.remove(position);
                            //listEnviarMecanicas.add(enviarMecanica);
                            //listEnviarMecanicas.set(position, enviarMecanica);
                            nuevo = false;
                        }
                    }
                    if(nuevo == true){

                        try {


                            final EditText input = new EditText(getApplicationContext());
                            input.setTextColor(Color.BLACK);
                            input.setGravity(Gravity.CENTER);
                            input.setTextSize(22);
                            input.setInputType(InputType.TYPE_CLASS_NUMBER);



                            final AlertDialog.Builder builder = new AlertDialog.Builder(Lecturas.this);
                            builder.setTitle("Ingresa Lectura de la Manguera: " + manguera.get(position));

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
                                                    manguera.get(position).toString();

                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }

                                                double lecturaMecanica = Double.parseDouble(lectura);


                                                if (Double.parseDouble(MecanicaApi) <= lecturaMecanica) {
                                                    litrosMecanicos = lecturaMecanica - Double.parseDouble(MecanicaApi);
                                                    mangueraActual = Integer.parseInt(manguera.get(position).toString());
                                                    double result = 0;
                                                    for (ResultadoManguera item : listaResultado) {

                                                        int manguera = item.manguera;

                                                        if (manguera == mangueraActual) {
                                                            result = item.resultado;
                                                        }


                                                    }

                                                    if (((result + lecturasElectronicasMecanicas) >= litrosMecanicos) && ((result - lecturasElectronicasMecanicas) <= litrosMecanicos)) {



                                                        litrosMecanico.put(litrosMecanicos);
                                                        subtitle.set(position, lectura);
                                                        ListAdapter adapter = new ListAdapter(Lecturas.this, maintitle, subtitle, manguera);
                                                        list.setAdapter(adapter);



                                                        EnviarMecanicaFinal enviarMecanica = new EnviarMecanicaFinal();


                                                        enviarMecanica.SucursalId = Integer.parseInt(data.getIdSucursal());
                                                        enviarMecanica.CierreId = cierreRespuestaApi.getObjetoRespuesta().getId();
                                                        enviarMecanica.CierreSucursalId = Integer.parseInt(data.getIdSucursal());
                                                        enviarMecanica.MangueraId = mangueraActual;
                                                        enviarMecanica.MangueraEstacionId = Integer.parseInt(data.getIdSucursal());
                                                        enviarMecanica.CombustibleId = 1;
                                                        enviarMecanica.ValorInicial = Double.parseDouble(MecanicaApi);
                                                        enviarMecanica.ValorFinal = lecturaMecanica;
                                                        enviarMecanica.posicion = position;


                                                        listEnviarMecanicas.add(enviarMecanica);
//


                                                        Toast.makeText(getApplicationContext(), "Lectura ingresada correctamente", Toast.LENGTH_SHORT).show();



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
                    else{
                        Toast.makeText(getApplicationContext(), "No se pueden editar las lecturas", Toast.LENGTH_LONG).show();
                    }

                }

            });
        } else {
            Toast.makeText(getApplicationContext(), "Comparar lecturas electronicas con suma despacho", Toast.LENGTH_LONG).show();
        }
    }





    public void obtenerIsla(final String validarLecturaMecanica) {
        final String pass = getIntent().getStringExtra("password");
        final SQLiteBD data = new SQLiteBD(getApplicationContext());
        String url = "http://"+data.getIpEstacion()+"/CorpogasService/api/estacionControles/estacion/"+data.getIdEstacion()+"/ClaveEmpleado/" +pass;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONArray  jsonArray = new JSONArray(response);
                    for (int i = 0; i <jsonArray.length() ; i++) {
                        JSONObject slt1 = jsonArray.getJSONObject(i);
                        idisla = slt1.getString("IslaId");
                        //String idturno = slt1.getString("TurnoId");
                        CortesIslas(idisla, validarLecturaMecanica);

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



    public void obtenerMecanicaInical(){
        final SQLiteBD data = new SQLiteBD(getApplicationContext());
        //long idSuc = cierreRespuestaApi2.ObjetoRespuesta.SucursalId;
        String url = "http://"+data.getIpEstacion()+"/CorpogasService/api/cierreCarretes/LecturaInicialMecanica/sucursal/"+data.getIdSucursal()+"/isla/"+islaId;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject lecturaMecaIni = new JSONObject(response);
                            String correcto = lecturaMecaIni.getString("Correcto");
                            if (correcto == "true") {
                                JSONArray arrayMecaIni = lecturaMecaIni.getJSONArray("ObjetoRespuesta");
                                for (int i = 0; i < arrayMecaIni.length(); i++) {
                                    JSONObject obj1 = arrayMecaIni.getJSONObject(i);
                                    String mecanicaInicial = obj1.getString("ValorInicial");
                                    Double mecanicaIni = Double.parseDouble(mecanicaInicial);
                                    mecanicasInicales.put(mecanicaIni);
                                }
                            }else{
                                if (correcto == "false"){
                                    final String respuesta = lecturaMecaIni.getString("ObjetoRespuesta");
                                    String mensaje = lecturaMecaIni.getString("Mensaje");
                                    AlertDialog.Builder builder = new AlertDialog.Builder(Lecturas.this);
                                    builder.setTitle("ERROR");
                                    builder.setMessage(mensaje);
                                    builder.setNegativeButton("Cerrar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
//                                                try {
//                                                    JSONObject nopasan = new JSONObject(respuesta);
//                                                    String idcombustible = nopasan.getString("CombustibleId");
//                                                    String idmanguera = nopasan.getString("MangueraId");
//
//                                                } catch (JSONException e) {
//                                                    e.printStackTrace();
//                                                }
                                            Intent intent = new Intent(getApplicationContext(),IslasEstacion.class);
                                            intent.putExtra("nombreCompleto",nombreCompleto);
                                            intent.putExtra("password",password);
                                            intent.putExtra("idusuario",idusuario);
                                            startActivity(intent);
                                            dialogInterface.dismiss();

                                                                                    }
                                    });
                                    AlertDialog dialog= builder.create();
                                    dialog.show();
                                }
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







