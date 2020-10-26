package com.szzcs.smartpos.Cortes2;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.szzcs.smartpos.R;
import com.szzcs.smartpos.configuracion.SQLiteBD;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CierreFormaPago extends AppCompatActivity {

    ListView list;
    List<String> maintitle;
    List<String> subtitle;
    List<String> total;
    EditText morrallita;
    TextView totalPicos, subTotalOficina, txtGasto;
    JSONArray contSubtotalOficina = new JSONArray();
    Button btnAceptar;
    String morralla;
    Double sumaTotalFormas;
    Double varMorralla;
    String resultadoPrueba;
    String islaId;
    String usuarioId;
    String picos;
    String dineroBilletes;
    String dineroMorralla;
    String VentaProductos;
    String cantidadAceites;
    String denominacion;
    double denom;

    RespuestaApi<Cierre> cierreRespuestaApi;
    long cierreId;
    long turnoId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cierre_forma_pago);

        morrallita = (EditText) findViewById(R.id.edtMorrallita);
        totalPicos = (TextView)findViewById(R.id.txtTotalPicos);
        subTotalOficina = (TextView) findViewById(R.id.txtSubtotalOficina);
        btnAceptar = findViewById(R.id.aceptarSubtotalOficina);
        txtGasto = findViewById(R.id.txtGastos);

        islaId = getIntent().getStringExtra("islaId");
        usuarioId =getIntent().getStringExtra("idusuario");
        picos = getIntent().getStringExtra("sumaPicosBilletes");
        dineroBilletes = getIntent().getStringExtra("dineroBilletes");
        dineroMorralla = getIntent().getStringExtra("dineroMorralla");
        VentaProductos = getIntent().getStringExtra("VentaProductos");
        cantidadAceites = getIntent().getStringExtra("cantidadAceites");
        cierreRespuestaApi = (RespuestaApi<Cierre>) getIntent().getSerializableExtra( "lcierreRespuestaApi");
        turnoId = cierreRespuestaApi.getObjetoRespuesta().getTurnoId();
        cierreId = cierreRespuestaApi.getObjetoRespuesta().getId();



        BigDecimal bd1 = new BigDecimal(picos);
        String resultadoPicos = String.valueOf(bd1.setScale(0,RoundingMode.HALF_UP));
        totalPicos.setText("TOTAL PICOS: " + resultadoPicos);
        obtenerCierreFormasPago();
        obtenerGasto();

        // Se crea un evento listener para el campo EditText
        morrallita.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode == EditorInfo.IME_ACTION_DONE
                        || keyCode == KeyEvent.KEYCODE_ENTER) {
                    // Se colocan las instrcucciones para ocultar el teclado cuando se le de enter.
                    InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(morrallita.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });

        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                morralla = morrallita.getText().toString();
                if (morralla.isEmpty()){
                    AlertDialog.Builder builder = new AlertDialog.Builder(CierreFormaPago.this);
                    builder.setTitle("ERROR");
                    builder.setMessage("No haz ingresado un valor de Morralla");
                    builder.setNegativeButton("Cerrar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    AlertDialog dialog= builder.create();
                    dialog.show();
//                }else
//                if (Double.parseDouble(morralla) > 200){
//                    AlertDialog.Builder builder = new AlertDialog.Builder(CierreFormaPago.this);
//                    builder.setTitle("ERROR");
//                    builder.setMessage("No puedes superar el valor de una Fajilla");
//                    builder.setNegativeButton("Cerrar", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i) {
//                            dialogInterface.dismiss();
//                        }
//                    });
//                    AlertDialog dialog= builder.create();
//                    dialog.show();
//                }else {
//                    if (Double.parseDouble(morralla) == 200){
//                        AlertDialog.Builder builder = new AlertDialog.Builder(CierreFormaPago.this);
//                        builder.setTitle("ERROR");
//                        builder.setMessage("Tu valor puede ser una fajilla");
//                        builder.setNegativeButton("Cerrar", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                dialogInterface.dismiss();
//                            }
//                        });
//                        AlertDialog dialog= builder.create();
//                        dialog.show();
                }else {
                    if (Double.parseDouble(morralla) < 900000){
                        Toast.makeText(CierreFormaPago.this, "Tu Valor fue: "+morralla, Toast.LENGTH_SHORT).show();
                        varMorralla = Double.valueOf(morralla);

                        // Se suma el total de Picos billetes, Morralla y el total de las distintas formas de Pago
                        Double pruebaResult = (sumaTotalFormas + varMorralla + Double.parseDouble(picos)) + denom;
                        BigDecimal bd = new BigDecimal(pruebaResult);
                        // Se limite el resultado a 4 decimales y se redondea
                        resultadoPrueba = String.valueOf(bd.setScale(4, RoundingMode.HALF_UP));
                        subTotalOficina.setText("SUBTOTAL OFICINA: " + resultadoPrueba);
                        Toast.makeText(CierreFormaPago.this, "Tu subTotal es: "+ resultadoPrueba, Toast.LENGTH_LONG).show();
                        enviarFolios();
                    }
                }
            }


        });

    }

    public void obtenerCierreFormasPago() {
        final SQLiteBD data = new SQLiteBD(getApplicationContext());
        // Declaramos la URl que se ocupara para el metodo obtenerTotales
        String url = "http://" + data.getIpEstacion() + "/CorpogasService/api/cierres/registrar/sucursal/" + data.getIdSucursal() + "/isla/"+islaId+"/usuario/"+usuarioId+"/origen/1";
        // Utilizamos el metodo POST para obtener Cantidad, Total y ProductoDescripcion
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                maintitle = new ArrayList<String>();
                subtitle = new ArrayList<String>();
                total = new ArrayList<String>();

                try {
                    JSONObject cierres = new JSONObject(response);
                    JSONObject sl1 = cierres.getJSONObject("ObjetoRespuesta");
                    JSONArray array = sl1.getJSONArray("CierreFormaPagos");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject obj1 = array.getJSONObject(i);
                        String cantidad = obj1.getString("Cantidad");
                        String formapago = obj1.getString("FormaPago");
                        String numTickets = obj1.getString("NumeroTickets");
                        JSONObject formapage = new JSONObject(formapago);
                        String descripLarga = formapage.getString("DescripcionLarga");

                        contSubtotalOficina.put(cantidad);

                        maintitle.add(numTickets);
                        subtitle.add(descripLarga);
                        total.add(cantidad);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                final ListAdapterFormasPago adapter = new ListAdapterFormasPago(CierreFormaPago.this, maintitle, subtitle, total);
                list = (ListView) findViewById(R.id.list);
                list.setAdapter(adapter);

                sumaTotalFormas = calcularTotalFormasPago();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        // Añade la peticion a la cola
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);

    }

    // Se crea el metodo calcularTotalImporte para sumar los valores que tiene el arreglo importe
    public double calcularTotalFormasPago() {
        double sumamax = 0;
        for (int i = 0; i < contSubtotalOficina.length(); i++) {
            double suma = 0;
            try {
                suma = contSubtotalOficina.getDouble(i);
                sumamax += suma;
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return sumamax;
    }

    private void enviarFolios() {
        final SQLiteBD data = new SQLiteBD(getApplicationContext());
        String URL = "http://"+data.getIpEstacion()+"/CorpogasService/api/Fajillas/GuardaFoliosCierreFajillas/usuario/"+ usuarioId;
        final JSONObject mjason = new JSONObject();
        RequestQueue queue = Volley.newRequestQueue(this);
        try {
            mjason.put("CierreId",cierreId);
            mjason.put("CierreSucursalId", 1); //turno.getText().toString());
            JSONObject prueba = new JSONObject();
            prueba.put("IslaId", islaId);
            mjason.put("Cierre",prueba);//turno.getText().toString());Sucursal
            mjason.put("SucursalId", data.getIdSucursal());
            mjason.put("TipoFajillaId","4");
            mjason.put("FolioFinal", "1");
            mjason.put("Denominacion", varMorralla);
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
                        AlertDialog.Builder builder = new AlertDialog.Builder(CierreFormaPago.this);
                        builder.setTitle("CorpoApp");
                        builder.setMessage("Los datos se guardaron corretamente");
                        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(getApplicationContext(),TotalProductos.class);
                                intent.putExtra("origenId","1");
                                intent.putExtra("islaId",islaId);
                                intent.putExtra("idusuario", usuarioId);
                                intent.putExtra("dineroBilletes",dineroBilletes);
                                intent.putExtra("dineroMorralla",dineroMorralla);
                                intent.putExtra("subTotalOficina",String.valueOf(resultadoPrueba));
                                intent.putExtra("VentaProductos", VentaProductos);
                                intent.putExtra("cantidadAceites", cantidadAceites);
                                intent.putExtra("lcierreRespuestaApi", cierreRespuestaApi);
                                startActivity(intent);
                                dialogInterface.dismiss();
                            }
                        });
                        AlertDialog dialog= builder.create();
                        dialog.show();

                    }else{
                        String mensaje  = response.getString("Mensaje");
                        AlertDialog.Builder builder = new AlertDialog.Builder(CierreFormaPago.this);
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

        };
        queue.add(request_json);

    }

    public void obtenerGasto(){
        final SQLiteBD data = new SQLiteBD(getApplicationContext());
        String URL = "http://"+data.getIpEstacion()+"/CorpogasService/api/Fajillas/GuardaFoliosCierreFajillas/usuario/"+ usuarioId;
        final JSONObject mjason = new JSONObject();
        RequestQueue queue = Volley.newRequestQueue(this);
        try {
            mjason.put("CierreId",cierreId);
            mjason.put("CierreSucursalId", 1); //turno.getText().toString());
            JSONObject prueba = new JSONObject();
            prueba.put("IslaId", islaId);
            mjason.put("Cierre",prueba);//turno.getText().toString());Sucursal
            mjason.put("SucursalId", data.getIdSucursal());
            mjason.put("TipoFajillaId","5");
            mjason.put("FolioFinal", "1");
            mjason.put("Denominacion", "0");
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
                        JSONObject obj1 = response.getJSONObject("ObjetoRespuesta");
                        denominacion = obj1.getString("Denominacion");
                        txtGasto.setText("GASTOS: $"+ denominacion);
                        denom = Double.parseDouble(denominacion);

                    }else{
                        String mensaje  = response.getString("Mensaje");
                        AlertDialog.Builder builder = new AlertDialog.Builder(CierreFormaPago.this);
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

        };
        queue.add(request_json);

    }

}


