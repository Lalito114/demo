package com.szzcs.smartpos.Cortes2;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
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
import com.google.gson.reflect.TypeToken;
import com.szzcs.smartpos.Helpers.Modales.Modales;
import com.szzcs.smartpos.Munu_Principal;
import com.szzcs.smartpos.Productos.VentasProductos;
import com.szzcs.smartpos.R;
import com.szzcs.smartpos.configuracion.SQLiteBD;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Lecturas extends AppCompatActivity {

    String idManguera, idTurno, turnoAuxiliar, fechaTrabajo, dfpLEM, dfpLED, descripcombus, MecanicaApi;
    List<Double> mecanicasInicales = new ArrayList<>();
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
    long idusuario;
    SQLiteBD data;
    String nombreCompleto;
    String password;
    int mangueraActual;
    long cierreId;
    long turnoId;
    double lecturasElectronicasMecanicas;
    double LecturasElectronicasDespachos;
    boolean bandera = true;
    RespuestaApi<List<CierreCarrete>> cierreCarretes;
    RespuestaApi<CierreCarrete> getCierreCarrete;
    RespuestaApi<AccesoUsuario> accesoUsuario;
    Type respuestaCierreCarrete;
    EditText input ;
    String pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecturas);

        cierreCarretes = (RespuestaApi<List<CierreCarrete>>) getIntent().getSerializableExtra("cierreCarretes");
        cierreRespuestaApi = (RespuestaApi<Cierre>) getIntent().getSerializableExtra("lcierreRespuestaApi");
        accesoUsuario = (RespuestaApi<AccesoUsuario>) getIntent().getSerializableExtra("accesoUsuario");
        pass = accesoUsuario.getObjetoRespuesta().getClave();
        islaId = getIntent().getStringExtra("islaId");
        nombreCompleto = accesoUsuario.getObjetoRespuesta().getNombreCompleto();
        idusuario = accesoUsuario.getObjetoRespuesta().getSucursalEmpleadoId();
        password = accesoUsuario.getObjetoRespuesta().getClave();
        if(cierreCarretes.Mensaje.equals("COMPLETO")) // CAMBIAR A CORRECTO
        {
            Intent intent = new Intent(getApplicationContext(), VentasTotales.class);
            intent.putExtra("islaId",islaId);
            intent.putExtra("lcierreRespuestaApi", cierreRespuestaApi);
            intent.putExtra("accesoUsuario", accesoUsuario);
            startActivity(intent);

        }else
        {
            //cierreArrayList2 = (ArrayList<Cierre>) getIntent().getSerializableExtra( "lcierreRespuestaApi");

            cierreId = cierreRespuestaApi.getObjetoRespuesta().getId();
            turnoId = cierreRespuestaApi.getObjetoRespuesta().getTurnoId();
            lecturasElectronicasMecanicas = cierreRespuestaApi.getObjetoRespuesta().Variables.DiferenciaPermitida.LecturasElectronicasMecanicas;
            LecturasElectronicasDespachos = cierreRespuestaApi.getObjetoRespuesta().Variables.DiferenciaPermitida.LecturasElectronicasDespachos;
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
                            String titulo = "AVISO";
                            String mensaje = "Ingresa todas las Lecturas Mecanicas.";
                            Modales modales = new Modales(Lecturas.this);
                            View view1 = modales.MostrarDialogoAlertaAceptar(Lecturas.this,mensaje,titulo);
                            view1.findViewById(R.id.buttonYes).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                modales.alertDialog.dismiss();

                            }
                        });

                    } else {
                        if (litrosMecanico.length() == mecanicasInicales.size()) {
                            EnviaValidaMecanicas();

                        } else {
                                String titulo = "AVISO";
                                String mensaje = "Capturar todas las Lecturas Mecanicas faltantes.";
                                Modales modales = new Modales(Lecturas.this);
                                View view1 = modales.MostrarDialogoAlertaAceptar(Lecturas.this,mensaje,titulo);
                                view1.findViewById(R.id.buttonYes).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    modales.alertDialog.dismiss();

                                }
                            });

                        }
                    }
//                    Toast.makeText(getApplicationContext(), "Lecturas Mecanicas finales Guardadas", Toast.LENGTH_LONG).show();

                }
            });
        }
    }

    public void EnviaValidaMecanicas() {

        new Thread(new Runnable() {
            public void run() {

                try {
                    httpsJsonPost();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    public void run() {
                        String respuetas = null;
                        boolean  Correcto = getCierreCarrete.Correcto;

                        if(Correcto == true){

                            String mensaje = "Lecturas guardadas.";
                            Modales modales = new Modales(Lecturas.this);
                            View view1 = modales.MostrarDialogoCorrecto(Lecturas.this,mensaje);

                            view1.findViewById(R.id.buttonAction).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(getApplicationContext(), VentasTotales.class);
                                    intent.putExtra("islaId",islaId);
                                    intent.putExtra("lcierreRespuestaApi", cierreRespuestaApi);
                                    intent.putExtra("accesoUsuario", accesoUsuario);
                                    startActivity(intent);
                                    modales.alertDialog.dismiss();

                                }
                            });

                        }else{
                            String titulo = "AVISO";
                            String mensaje = getCierreCarrete.Mensaje;
                            Modales modales = new Modales(Lecturas.this);
                            modales.MostrarDialogoAlertaAceptar(Lecturas.this,mensaje,titulo);

                        }

                    }
                });
            }
        }).start();
    }

    public void  httpsJsonPost() throws IOException, ParseException {


        String json = new Gson().toJson(listEnviarMecanicas);
        String postUrl = "http://"+data.getIpEstacion()+"/CorpogasService/api/cierreCarretes/sucursal/"+data.getIdSucursal()+"/isla/"+islaId+"/usuario/"+idusuario;                    //"http://10.0.1.20/CorpogasService/api/cierreValePapeles/cierre/sucursalId/1/usuarioId/1/islaId/1";// put in your url

        HttpClient client = new DefaultHttpClient();
        HttpConnectionParams.setConnectionTimeout(client.getParams(), 1000);
        HttpResponse response;
        HttpPost request = new HttpPost(postUrl);
        StringEntity se = new StringEntity(json);
        se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
        request.setEntity(se);
        response = client.execute(request);

        /*Checking response */
        if (response != null) {
            InputStream in = response.getEntity().getContent(); //Get the data in the entity

            BufferedReader reader = new BufferedReader(new InputStreamReader(in, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) // Read line by line
                sb.append(line + "\n");

            String resString = sb.toString(); // Result is here

            in.close(); // Close the stream
            Gson json2 = new Gson();

            respuestaCierreCarrete = new TypeToken<RespuestaApi<List<CierreCarrete>>>(){}.getType();
            getCierreCarrete = json2.fromJson(resString, respuestaCierreCarrete);

            String pruebas = "";


        }
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
                    subtitle.add("0.00");
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
                            nuevo = false;
                        }
                    }

                    if(nuevo == true) {

                        String mensaje = "Ingresa la Lectura de la Manguera : " + manguera.get(position);
                        String titulo = "LECTURA MECANICA";
                        Modales modales = new Modales(Lecturas.this);
                        View viewLectura = modales.MostrarDialogoInsertaDato(Lecturas.this, mensaje, titulo);
                        EditText edtLectura = ((EditText) viewLectura.findViewById(R.id.textInsertarDato));


                        viewLectura.findViewById(R.id.buttonYes).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String lectura = edtLectura.getText().toString();
                                if (lectura.isEmpty()) {
                                    edtLectura.setError("Campo requerido");
                                    //Toast.makeText(getApplicationContext(), "vacio", Toast.LENGTH_SHORT).show();
                                } else {
                                    String ray = mecanicasInicales.get(position).toString();
                                    MecanicaApi = ray;
                                    manguera.get(position).toString();

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
                                            Toast.makeText(getApplicationContext(), "Lectura ingresada correctamente", Toast.LENGTH_SHORT).show();
                                            modales.alertDialog.dismiss();

                                        } else {
                                            edtLectura.setError("La diferencia de litros esta fuera de lo Permitido");
                                        }

                                    } else {
                                        edtLectura.setError("No puedes tener menos litros que la Lectura Mecanica Inicial");
                                    }
                                }
                            }
                        });

                        viewLectura.findViewById(R.id.buttonNo).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                modales.alertDialog.dismiss();

                            }
                        });
                    }
                }
            });
    }

    public void obtenerIsla(final String validarLecturaMecanica) {
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
        for(CierreCarrete  item: cierreCarretes.getObjetoRespuesta() )
        {
            mecanicasInicales.add(item.getValorInicial());
        }
    }

}







