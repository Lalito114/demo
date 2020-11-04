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
import com.szzcs.smartpos.Helpers.Modales.Modales;
import com.szzcs.smartpos.Munu_Principal;
import com.szzcs.smartpos.R;
import com.szzcs.smartpos.configuracion.SQLiteBD;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class FajillasBilletes extends AppCompatActivity {

    // Se declaran las variables que se usaran en este Activity
    String  origenId, inicial, foliof, islaId, VentaProductos, cantidadAceites;
    EditText folioInicial, folioFinal;
    public int dineroBilletes;
    Button btnValidaFajillas;
    int precioFajilla;
    RespuestaApi<Cierre> cierreRespuestaApi;
    long cierreId, turnoId;
    RespuestaApi<AccesoUsuario> accesoUsuario;
    long idusuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fajillas_billetes);

        //obtenerDatosOrigenCierre();

        // Hacemos la relacion de las variables con los objetos del layout
        folioInicial = (EditText) findViewById(R.id.editFolioInicialBilletes);
        folioFinal = (EditText) findViewById(R.id.editFolioFinalBilletes);
        btnValidaFajillas = (Button) findViewById(R.id.btnFajillaBilletes);

        cierreRespuestaApi = (RespuestaApi<Cierre>) getIntent().getSerializableExtra( "lcierreRespuestaApi");
        accesoUsuario = (RespuestaApi<AccesoUsuario>) getIntent().getSerializableExtra("accesoUsuario");
        cantidadAceites = getIntent().getStringExtra("cantidadAceites");
        VentaProductos = getIntent().getStringExtra("VentaProductos");
        turnoId = cierreRespuestaApi.getObjetoRespuesta().getTurnoId();
        cierreId = cierreRespuestaApi.getObjetoRespuesta().getId();
        idusuario = accesoUsuario.getObjetoRespuesta().getSucursalEmpleadoId();
        islaId = getIntent().getStringExtra("islaId");

        //fajillaBillete = cierreRespuestaApi.getObjetoRespuesta().Variables.PrecioFajillas

        // Este sera el comportamiento del boton cuando el Usuario le de click
        btnValidaFajillas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for (PrecioFajilla item : cierreRespuestaApi.getObjetoRespuesta().Variables.getPrecioFajillas()) {
                    if (item.getTipoFajillaId() == 1)
                        precioFajilla = item.getPrecio();
                }

                inicial = folioInicial.getText().toString();

                foliof = folioFinal.getText().toString();
                Modales modales = new Modales(FajillasBilletes.this);

                if (inicial.isEmpty() && foliof.isEmpty()) {
                    String titulo = "AVISO";
                    String mensaje = "Favor de ingresar un Folio Inicial y Folio Final.";
                    View view1 = modales.MostrarDialogoAlertaAceptar(FajillasBilletes.this, mensaje, titulo);
                    view1.findViewById(R.id.buttonYes).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            modales.alertDialog.dismiss();

                        }
                    });

                } else {

                    if (inicial.isEmpty()) {
                        String titulo = "AVISO";
                        String mensaje = "Favor de ingresar un Folio Inicial.";
                        View view1 = modales.MostrarDialogoAlertaAceptar(FajillasBilletes.this, mensaje, titulo);
                        view1.findViewById(R.id.buttonYes).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                modales.alertDialog.dismiss();

                            }
                        });

                    } else {
                        if (foliof.isEmpty()) {
                            String titulo = "AVISO";
                            String mensaje = "Favor de ingresar un Folio Final.";
                            View view1 = modales.MostrarDialogoAlertaAceptar(FajillasBilletes.this, mensaje, titulo);
                            view1.findViewById(R.id.buttonYes).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    modales.alertDialog.dismiss();

                                }
                            });

                        } else {
                            if(inicial.equals("0")) {
                                String titulo = "AVISO";
                                String mensaje = "Tu Folio Inicial no puede ser 0.";
                                View view1 = modales.MostrarDialogoAlertaAceptar(FajillasBilletes.this, mensaje, titulo);
                                view1.findViewById(R.id.buttonYes).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        modales.alertDialog.dismiss();

                                    }
                                });

                            }else{

                            if (Integer.parseInt(foliof) < Integer.parseInt(inicial)) {
                                String titulo = "AVISO";
                                String mensaje = "Tu Folio Final no puede ser mayor que tu Folio Inical.";
                                View view1 = modales.MostrarDialogoAlertaAceptar(FajillasBilletes.this, mensaje, titulo);
                                view1.findViewById(R.id.buttonYes).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        modales.alertDialog.dismiss();

                                    }
                                });

                            } else {
                                folioInicial.setText(StringUtils.stripStart(inicial, "0"));
                                folioFinal.setText(StringUtils.stripStart(foliof, "0"));
                                // Restamos el Folio Final y el Folio Inicial. Al resultado de esta operacion le sumamos un 1
                                int folios = (Integer.parseInt(foliof) - Integer.parseInt(inicial)) + 1;
                                // Se multiplica el resultado de la resta por el valor de la Fajilla
                                dineroBilletes = folios * precioFajilla;
                                enviarFolios();
                            }
                        }
                    }
                }
            }    // Se valida que el Folio Inicial y el Folio final no esten vacios.


//                }else{
//                    // Se valida si el Folio Final es menor o igual que el Folio Inicial
//                    if(Integer.parseInt(foliof) <= Integer.parseInt(inicial)){
//                        AlertDialog.Builder builder = new AlertDialog.Builder(FajillasBilletes.this);
//                        builder.setTitle("ERROR 302");
//                        builder.setMessage("Verifica tus Numeros de Folio");
//                        builder.setNegativeButton("Cerrar", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                dialogInterface.dismiss();
//                            }
//                        });
//                        AlertDialog dialog= builder.create();
//                        dialog.show();
//                    }else{
//                        // Restamos el Folio Final y el Folio Inicial. Al resultado de esta operacion le sumamos un 1
//                        int folios = (Integer.parseInt(foliof) - Integer.parseInt(inicial)) + 1;
//                        // Se multiplica el resultado de la resta por el valor de la Fajilla
//                        dineroBilletes = folios * precioFajilla;
//                        Toast.makeText(FajillasBilletes.this, "Fue un Total de " + dineroBilletes + " pesos", Toast.LENGTH_LONG).show();
//                        enviarFolios();
//
//                    }
            }

        });

    }

    private void enviarFolios() {

        final SQLiteBD data = new SQLiteBD(getApplicationContext());
//        String URL = "http://"+data.getIpEstacion()+"/CorpogasService/api/Fajillas/GuardaFoliosCierreFajillas/usuario/"+ usuarioId;
        String URL = "http://"+data.getIpEstacion()+"/CorpogasService/api/cierreFajillas/usuario/" + idusuario;
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
            mjason.put("Denominacion", precioFajilla);
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

                        String mensaje = "Sus Folios han sido registrados. Total : $" + dineroBilletes + " pesos";
                        Modales modales = new Modales(FajillasBilletes.this);
                        View view1 = modales.MostrarDialogoCorrecto(FajillasBilletes.this,mensaje);
                        view1.findViewById(R.id.buttonAction).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(getApplicationContext(), FajillaMorralla.class);
                                intent.putExtra("dinero",String.valueOf(dineroBilletes));
                                intent.putExtra("fajillaBillete",String.valueOf(precioFajilla));
                                intent.putExtra("origenId",origenId);
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
                        Modales modales = new Modales(FajillasBilletes.this);
                        modales.MostrarDialogoError(FajillasBilletes.this,mensaje);


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

    public void onBackPressed() {
        String mensaje = "Seras regresado al menú principal. ¿Estas seguro?";
        Modales modales = new Modales(FajillasBilletes.this);
        String nombrebtnAceptar ="SI";
        String nombreBtnCancelar ="NO";
        View view1 = modales.MostrarDialogoAlerta(FajillasBilletes.this,mensaje, nombrebtnAceptar, nombreBtnCancelar);

        view1.findViewById(R.id.buttonYes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Munu_Principal.class);
                startActivity(intent);
            }
        });

        view1.findViewById(R.id.buttonNo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                modales.alertDialog.dismiss();

            }
        });

    }

}