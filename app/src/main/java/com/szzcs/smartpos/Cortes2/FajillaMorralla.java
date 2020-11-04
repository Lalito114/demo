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
import com.szzcs.smartpos.Helpers.Modales.Modales;
import com.szzcs.smartpos.R;
import com.szzcs.smartpos.configuracion.SQLiteBD;

import org.apache.commons.lang3.StringUtils;
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
    int dineroMorralla;
    String islaId;
    String dineroBilletes;
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
        setContentView(R.layout.activity_fajilla_morralla);

        fajillasMorralla = (EditText) findViewById(R.id.editFajillasMorralla);
        btnFajillasMorralla = (Button) findViewById(R.id.btnFajillasMorralla);
        cierreRespuestaApi = (RespuestaApi<Cierre>) getIntent().getSerializableExtra( "lcierreRespuestaApi");
        accesoUsuario = (RespuestaApi<AccesoUsuario>) getIntent().getSerializableExtra("accesoUsuario");
        islaId = getIntent().getStringExtra("islaId");
        idusuario = accesoUsuario.getObjetoRespuesta().getSucursalEmpleadoId();
        dineroBilletes = getIntent().getStringExtra("dinero");
        VentaProductos = getIntent().getStringExtra("VentaProductos");
        cantidadAceites = getIntent().getStringExtra("cantidadAceites");

        turnoId = cierreRespuestaApi.getObjetoRespuesta().getTurnoId();
        cierreId = cierreRespuestaApi.getObjetoRespuesta().getId();



        btnFajillasMorralla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                morralla = fajillasMorralla.getText().toString();

                for(PrecioFajilla item : cierreRespuestaApi.getObjetoRespuesta().Variables.getPrecioFajillas())
                {
                    if(item.getTipoFajillaId() == 2)
                        fajillaMorralla = item.getPrecio();
                }
                Modales modales = new Modales(FajillaMorralla.this);
                if(morralla.isEmpty()){

                    String titulo = "AVISO";
                    String mensaje = "Ingresa tu Numero de Fajillas de Morralla.";
                    View view1 = modales.MostrarDialogoAlertaAceptar(FajillaMorralla.this, mensaje, titulo);
                    view1.findViewById(R.id.buttonYes).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            modales.alertDialog.dismiss();

                        }
                    });

                }else {
                    fajillasMorralla.setText(StringUtils.stripStart(morralla, "0"));
                    dineroMorralla = Integer.parseInt(morralla) * fajillaMorralla;
                    enviarFolios();
                }
            }
        });



    }


    private void enviarFolios() {

        // String islaId = isla.getText().toString(); //getIntent().getStringExtra("isla");
        // String turnoId = "1";//getIntent().getStringExtra("turno");
        final SQLiteBD data = new SQLiteBD(getApplicationContext());
        String URL = "http://10.0.1.20/CorpogasService/api/cierreFajillas/usuario/" + idusuario;
//        String URL = "http://"+data.getIpEstacion()+"/CorpogasService/api/Fajillas/GuardaFoliosCierreFajillas/usuario/" + idusuario;
        final JSONObject mjason = new JSONObject();
        RequestQueue queue = Volley.newRequestQueue(this);
        try {
            mjason.put("CierreId",cierreId);
            mjason.put("CierreSucursalId", 1); //turno.getText().toString());
            JSONObject prueba = new JSONObject();
            prueba.put("IslaId", islaId);
            mjason.put("Cierre",prueba);//turno.getText().toString());Sucursal
            mjason.put("SucursalId", data.getIdSucursal());
            mjason.put("TipoFajillaId","2");
            mjason.put("FolioFinal", morralla);
            mjason.put("Denominacion", fajillaMorralla);
            mjason.put("OrigenId", 1);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request_json = new JsonObjectRequest(Request.Method.POST, URL, mjason, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String estado  = response.getString("Correcto");
                    if (estado == "true"){

                        String mensaje = "Tu numero de Fajillas ha sido registrado. Total: $" + dineroMorralla + " pesos";
                        Modales modales = new Modales(FajillaMorralla.this);
                        View view1 = modales.MostrarDialogoCorrecto(FajillaMorralla.this,mensaje);
                        view1.findViewById(R.id.buttonAction).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(getApplicationContext(), SubOfiBilletes.class);
                                intent.putExtra("origenId",1);
                                intent.putExtra("dineroBilletes",dineroBilletes);
                                intent.putExtra("dineroMorralla",String.valueOf(dineroMorralla));
                                intent.putExtra("islaId",islaId);
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
                        Modales modales = new Modales(FajillaMorralla.this);
                        modales.MostrarDialogoError(FajillaMorralla.this,mensaje);

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
        Intent intent = new Intent(getApplicationContext(), FajillasBilletes.class);
        intent.putExtra("origenId",1);
        intent.putExtra("dineroBilletes",dineroBilletes);
        intent.putExtra("dineroMorralla",String.valueOf(dineroMorralla));
        intent.putExtra("islaId",islaId);
        intent.putExtra("VentaProductos", VentaProductos);
        intent.putExtra("cantidadAceites", cantidadAceites);
        intent.putExtra("lcierreRespuestaApi", cierreRespuestaApi);
        intent.putExtra("accesoUsuario", accesoUsuario);
        startActivity(intent);
        // finish();
    }

}