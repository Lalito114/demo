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
import com.szzcs.smartpos.Helpers.Modales.Modales;
import com.szzcs.smartpos.Munu_Principal;
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
    String islaId;

    String dineroBilletes;
    String dineroMorralla;
    String VentaProductos;
    String cantidadAceites;

    RespuestaApi<Cierre> cierreRespuestaApi;
    long cierreId;
    long turnoId;

    RespuestaApi<AccesoUsuario> accesoUsuario;
    long idusuario;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_ofi_billetes);

        denominacionBilletes();
        valorFajilla();
        btnaceptar = findViewById(R.id.aceptarBilletes);
        cierreRespuestaApi = (RespuestaApi<Cierre>) getIntent().getSerializableExtra( "lcierreRespuestaApi");
        accesoUsuario = (RespuestaApi<AccesoUsuario>) getIntent().getSerializableExtra("accesoUsuario");

        islaId = getIntent().getStringExtra("islaId");
        idusuario = accesoUsuario.getObjetoRespuesta().getSucursalEmpleadoId();
        dineroBilletes = getIntent().getStringExtra("dineroBilletes");
        dineroMorralla = getIntent().getStringExtra("dineroMorralla");
        VentaProductos = getIntent().getStringExtra("VentaProductos");
        cantidadAceites = getIntent().getStringExtra("cantidadAceites");
        turnoId = cierreRespuestaApi.getObjetoRespuesta().getTurnoId();
        cierreId = cierreRespuestaApi.getObjetoRespuesta().getId();

        btnaceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sumainter == 0) {
                    String mensaje = "No haz insertado ningun VALOR. ¿Deseas continuar?";
                    Modales modales = new Modales(SubOfiBilletes.this);
                    String nombrebtnAceptar ="SI";
                    String nombreBtnCancelar ="NO";
                    View view1 = modales.MostrarDialogoAlerta(SubOfiBilletes.this,mensaje, nombrebtnAceptar, nombreBtnCancelar);

                    view1.findViewById(R.id.buttonYes).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getApplicationContext(), SubOfiGasopass.class);
                            intent.putExtra("picoBilletes",String.valueOf(sumainter));
                            intent.putExtra("origenId",1);
                            intent.putExtra("islaId",islaId);
                            intent.putExtra("dineroBilletes",dineroBilletes);
                            intent.putExtra("dineroMorralla",dineroMorralla);
                            intent.putExtra("VentaProductos", VentaProductos);
                            intent.putExtra("cantidadAceites", cantidadAceites);
                            intent.putExtra("lcierreRespuestaApi", cierreRespuestaApi);
                            intent.putExtra("accesoUsuario", accesoUsuario);
                            startActivity(intent);
                            modales.alertDialog.dismiss();
                        }
                    });

                    view1.findViewById(R.id.buttonNo).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            modales.alertDialog.dismiss();

                        }
                    });
                } else {
                    if (sumainter <= (fajillaBillete - 10)) {


                        for (int i = 0; i < maintitle.size(); i++) {
                            if (!maintitle.get(i).equals("0")) {
                                enviarFolios(maintitle.get(i), String.valueOf(arrayMonto.get(i)));
                            }
                        }
                    } else {
                        Toast.makeText(SubOfiBilletes.this, "Existe un error", Toast.LENGTH_SHORT).show();
                    }
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


                    String titulo = "PICOS BILLETES";
                    String mensaje = "Ingresa Cantidad : ";
                    Modales modales = new Modales(SubOfiBilletes.this);
                    View viewLectura = modales.MostrarDialogoInsertaDato(SubOfiBilletes.this, mensaje, titulo);
                    EditText edtPicoBillete = ((EditText) viewLectura.findViewById(R.id.textInsertarDato));

                    viewLectura.findViewById(R.id.buttonYes).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String denomi = edtPicoBillete.getText().toString();
                            if (denomi.equals("")){
                                edtPicoBillete.setError("No ingresaste un Valor");
                            }else{
                                result = Double.parseDouble(denomi) * Double.parseDouble(ray);
                                if (result>fajillaBillete){
                                    edtPicoBillete.setError("No puede superar el valor de 1 Fajilla");
//                                    String titulo = "AVISO";
//                                    String mensaje = "No puede superar el valor de 1 Fajilla.";
//                                    Modales modales = new Modales(SubOfiBilletes.this);
//                                    View view1 = modales.MostrarDialogoAlertaAceptar(SubOfiBilletes.this,mensaje,titulo);
//                                    view1.findViewById(R.id.buttonYes).setOnClickListener(new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View view) {
//                                            modales.alertDialog.dismiss();
//
//                                        }
//                                    });
                                }else {

                                    maintitle.set(position, denomi);
                                    total.set(position, String.valueOf(result));
                                    final String ray2 = result.toString();
                                    sumainter = totalBilletes() + Double.parseDouble(ray2);

                                    if ((totalBilletes() <= (fajillaBillete - 10)) && (sumainter <= (fajillaBillete - 10))){

//                                        Toast.makeText(SubOfiBilletes.this, "Cantidad Agregada", Toast.LENGTH_SHORT).show();
                                        prueba2.put(ray2);
                                        modales.alertDialog.dismiss();
                                        ListAdapterBilletes adapter = new ListAdapterBilletes(SubOfiBilletes.this, maintitle, subtitle, total);
                                        mListView.setAdapter(adapter);
                                    }else{
                                        edtPicoBillete.setError("Los Valores que ingresaste pueden ser 1 Fajilla");
//                                        String titulo = "AVISO";
//                                        String mensaje = "Los Valores que ingresaste pueden ser 1 Fajilla.";
//                                        Modales modales = new Modales(SubOfiBilletes.this);
//                                        View view1 = modales.MostrarDialogoAlertaAceptar(SubOfiBilletes.this,mensaje,titulo);
//                                        view1.findViewById(R.id.buttonYes).setOnClickListener(new View.OnClickListener() {
//                                            @Override
//                                            public void onClick(View view) {
//                                                modales.alertDialog.dismiss();
//
//                                            }
//                                        });
                                    }

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
        // String islaId = isla.getText().toString(); //getIntent().getStringExtra("isla");
        // String turnoId = "1";//getIntent().getStringExtra("turno");
        final SQLiteBD data = new SQLiteBD(getApplicationContext());
        String URL = "http://"+data.getIpEstacion()+"/CorpogasService/api/cierreFajillas/usuario/" + idusuario ;
//        String URL = "http://"+data.getIpEstacion()+"/CorpogasService/api/Fajillas/GuardaFoliosCierreFajillas/usuario/" + idusuario;
        final JSONObject mjason = new JSONObject();
        RequestQueue queue = Volley.newRequestQueue(this);
        try {
            mjason.put("CierreId",cierreId);
            mjason.put("CierreSucursalId", "1"); //turno.getText().toString());
            JSONObject prueba = new JSONObject();
            prueba.put("IslaId", islaId);
            mjason.put("Cierre",prueba);//turno.getText().toString());Sucursal
            mjason.put("SucursalId", data.getIdSucursal());
            mjason.put("TipoFajillaId","3");
//            mjason.put("FolioInicial", "1");
            mjason.put("FolioFinal", folio);
            mjason.put("Denominacion", valor);
            mjason.put("OrigenId", "1");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request_json = new JsonObjectRequest(Request.Method.POST, URL, mjason, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String estado  = response.getString("Correcto");
                    if (estado == "true"){

                        String mensaje = "Se registro un Total de $"+ sumainter + " pesos.";
                        Modales modales = new Modales(SubOfiBilletes.this);
                        View view1 = modales.MostrarDialogoCorrecto(SubOfiBilletes.this,mensaje);
                        view1.findViewById(R.id.buttonAction).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(getApplicationContext(),SubOfiGasopass.class);
                                intent.putExtra("picoBilletes",String.valueOf(sumainter));
                                intent.putExtra("origenId",1);
                                intent.putExtra("islaId",islaId);
                                intent.putExtra("dineroBilletes",dineroBilletes);
                                intent.putExtra("dineroMorralla",dineroMorralla);
                                intent.putExtra("VentaProductos", VentaProductos);
                                intent.putExtra("cantidadAceites", cantidadAceites);
                                intent.putExtra("lcierreRespuestaApi", cierreRespuestaApi);
                                intent.putExtra("accesoUsuario", accesoUsuario);
                                startActivity(intent);
                                modales.alertDialog.dismiss();
                            }
                        });
                    }else{
                        String mensaje  = response.getString("Mensaje");
                        Modales modales = new Modales(SubOfiBilletes.this);
                        modales.MostrarDialogoError(SubOfiBilletes.this,mensaje);
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

    //Metodo para regresar a la actividad principal
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), FajillaMorralla.class);
        intent.putExtra("picoBilletes",String.valueOf(sumainter));
        intent.putExtra("origenId",1);
        intent.putExtra("islaId",islaId);
        intent.putExtra("dineroBilletes",dineroBilletes);
        intent.putExtra("dineroMorralla",dineroMorralla);
        intent.putExtra("VentaProductos", VentaProductos);
        intent.putExtra("cantidadAceites", cantidadAceites);
        intent.putExtra("lcierreRespuestaApi", cierreRespuestaApi);
        intent.putExtra("accesoUsuario", accesoUsuario);
        startActivity(intent);

    }
}