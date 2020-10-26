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
import com.szzcs.smartpos.configuracion.SQLiteBD;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class FajillasBilletes extends AppCompatActivity {

    // Se declaran las variables que se usaran en este Activity
    String  origenId, inicial, foliof;
    EditText folioInicial, folioFinal;
    public int dineroBilletes;
    Button btnValidaFajillas;
    int fajillaBillete,precioFajilla;
    String islaId;
    String usuarioId;
    String VentaProductos;
    String cantidadAceites;

    RespuestaApi<Cierre> cierreRespuestaApi;
    long cierreId;
    long turnoId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fajillas_billetes);

        //obtenerDatosOrigenCierre();


        // Hacemos la relacion de las variables con los objetos del layout
        folioInicial = (EditText) findViewById(R.id.editFolioInicialBilletes);
        folioFinal = (EditText) findViewById(R.id.editFolioFinalBilletes);
        btnValidaFajillas = (Button) findViewById(R.id.btnFajillaBilletes);

        islaId = getIntent().getStringExtra("islaId");
        usuarioId =getIntent().getStringExtra("idusuario");
        VentaProductos = getIntent().getStringExtra("VentaProductos");
        cantidadAceites = getIntent().getStringExtra("cantidadAceites");
        cierreRespuestaApi = (RespuestaApi<Cierre>) getIntent().getSerializableExtra( "lcierreRespuestaApi");
        turnoId = cierreRespuestaApi.getObjetoRespuesta().getTurnoId();
        cierreId = cierreRespuestaApi.getObjetoRespuesta().getId();
        //fajillaBillete = cierreRespuestaApi.getObjetoRespuesta().Variables.PrecioFajillas

        // Este sera el comportamiento del boton cuando el Usuario le de click
        btnValidaFajillas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for(PrecioFajilla item : cierreRespuestaApi.getObjetoRespuesta().Variables.getPrecioFajillas())
                {
                    if(item.getTipoFajillaId() == 1)
                    precioFajilla = item.getPrecio();
                }

                inicial = folioInicial.getText().toString();
                foliof = folioFinal.getText().toString();
                // Se valida que el Folio Inicial y el Folio final no esten vacios.
                if (inicial.isEmpty() || foliof.isEmpty()){
                    AlertDialog.Builder builder = new AlertDialog.Builder(FajillasBilletes.this);
                    builder.setTitle("ERROR 301");
                    builder.setMessage("Se requiere un Folio Inicial y Folio Final");
                    builder.setNegativeButton("Cerrar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    AlertDialog dialog= builder.create();
                    dialog.show();
                }else{
                    // Se valida si el Folio Final es menor o igual que el Folio Inicial
                    if(Integer.parseInt(foliof) <= Integer.parseInt(inicial)){
                        AlertDialog.Builder builder = new AlertDialog.Builder(FajillasBilletes.this);
                        builder.setTitle("ERROR 302");
                        builder.setMessage("Verifica tus Numeros de Folio");
                        builder.setNegativeButton("Cerrar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                        AlertDialog dialog= builder.create();
                        dialog.show();
                    }else{
                        // Restamos el Folio Final y el Folio Inicial. Al resultado de esta operacion le sumamos un 1
                        int folios = (Integer.parseInt(foliof) - Integer.parseInt(inicial)) + 1;
                        // Se multiplica el resultado de la resta por el valor de la Fajilla
                        dineroBilletes = folios * precioFajilla;
                        Toast.makeText(FajillasBilletes.this, "Fue un Total de " + dineroBilletes + " pesos", Toast.LENGTH_LONG).show();
                        enviarFolios();

                    }
                }

            }
        });

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
            mjason.put("SucursalId", "1");
            mjason.put("TipoFajillaId","1");
            mjason.put("FolioInicial", inicial);
            mjason.put("FolioFinal", foliof);
            mjason.put("Denominacion", fajillaBillete);
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
                        AlertDialog.Builder builder = new AlertDialog.Builder(FajillasBilletes.this);
                        builder.setTitle("CorpoApp");
                        builder.setMessage("Los datos se guardaron corretamente");
                        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                Intent intent = new Intent(getApplicationContext(),FajillaMorralla.class);
                                intent.putExtra("dinero",String.valueOf(dineroBilletes));
                                intent.putExtra("fajillaBillete",String.valueOf(fajillaBillete));
                                intent.putExtra("origenId",origenId);
                                intent.putExtra("islaId",islaId);
                                intent.putExtra("idusuario", usuarioId);
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

        };
        queue.add(request_json);

    }

}