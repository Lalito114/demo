package com.szzcs.smartpos.FinalizaVenta;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.szzcs.smartpos.Cortes2.VentasTotales;
import com.szzcs.smartpos.Munu_Principal;
import com.szzcs.smartpos.Productos.ListAdapterProd;
import com.szzcs.smartpos.Productos.VentasProductos;
import com.szzcs.smartpos.Productos.claveProducto;
import com.szzcs.smartpos.Productos.posicionProductos;
import com.szzcs.smartpos.R;
import com.szzcs.smartpos.configuracion.SQLiteBD;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class posicionFinaliza extends AppCompatActivity {
    ListView list;
    String carga, usuarioid;
    String EstacionId, sucursalId, ipEstacion, numeroTarjetero, lugarproviene, usuario, posicion, clave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posicion_finaliza);
        SQLiteBD db = new SQLiteBD(getApplicationContext());
        EstacionId = db.getIdEstacion();
        sucursalId = db.getIdSucursal();
        ipEstacion = db.getIpEstacion();
        lugarproviene = getIntent().getStringExtra("lugarproviene");
        usuarioid = getIntent().getStringExtra("usuario");
        usuario = getIntent().getStringExtra("clave");
        posicionCargaFinaliza();
    }

    public void posicionCargaFinaliza(){
    String url = "http://"+ipEstacion+"/CorpogasService/api/accesoUsuarios/sucursal/"+sucursalId+"/clave/" + usuario;

        // Utilizamos el metodo Post para validar la contraseña
        StringRequest eventoReq = new StringRequest(Request.Method.GET,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Declaramos la lista de titulo
                        List<String> maintitle;
                        //lo assignamos a un nuevo ArrayList
                        maintitle = new ArrayList<String>();

                        List<String> maintitle1;
                        //lo assignamos a un nuevo ArrayList
                        maintitle1 = new ArrayList<String>();

                        //Creamos la lista para los subtitulos
                        List<String> subtitle;
                        //Lo asignamos a un nuevo ArrayList
                        subtitle = new ArrayList<String>();

                        //CReamos una nueva list de tipo Integer con la cual cargaremos a una imagen
                        List<Integer> imgid;
                        //La asignamos a un nuevo elemento de ArrayList
                        imgid = new ArrayList<>();

                        String carga;
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String ObjetoRespuesta = jsonObject.getString("ObjetoRespuesta");
                            String mensaje = jsonObject.getString("Mensaje");

                            if (ObjetoRespuesta.equals("null")){
                                //Toast.makeText(posicionProductos.this, mensaje, Toast.LENGTH_SHORT).show();
                                try {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(posicionFinaliza.this);
                                    builder.setTitle("Productos");
                                    builder.setMessage("" + mensaje)
                                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    Intent intent = new Intent(getApplicationContext(), claveFinalizaVenta.class);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            }).show();
                                }catch (Exception e){
                                    e.printStackTrace();
                                }

                            }else {

                                JSONObject jsonObject1 = new JSONObject(ObjetoRespuesta);
                                String control = jsonObject1.getString("Controles");

                                JSONArray control1 = new JSONArray(control);
                                for (int i = 0; i <control1.length() ; i++) {
                                    JSONObject posiciones = control1.getJSONObject(i);
                                    String posi = posiciones.getString("Posiciones");

                                    JSONArray mangue = new JSONArray(posi);
                                    for (int j = 0; j < mangue.length(); j++) {
                                        JSONObject res = mangue.getJSONObject(j);
                                        carga = res.getString("PosicionCargaId");

                                        maintitle.add("PC " + carga);
                                        maintitle1.add(carga);
                                        subtitle.add("Magna  |  Premium  |  Diesel");
                                        imgid.add(R.drawable.gas);
                                    }

                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        ListAdapterProd adapterProd = new ListAdapterProd(posicionFinaliza.this, maintitle, subtitle, imgid);
                        list = (ListView) findViewById(R.id.list);
                        list.setAdapter(adapterProd);

                        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                // TODO Auto-generated method stub
                                String numerocarga = maintitle1.get(position);
                                posicion = numerocarga;
                                //Se llama la clase para la clave del usuario
                                if (lugarproviene.equals("1")) {
                                    solicitadespacho();
                                }else {
                                    validaPosicionDisponible(numerocarga);
                                }


                            }
                        });
                    }
                    //funcion para capturar errores
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_SHORT).show();
            }
        });

        // Añade la peticion a la cola
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(eventoReq);
    }


    //Proceso para cargar el listView con las posiciones de carga

    //Procedimiento que carga las posiciones de carga
    private void vax(final String response) {
        //declaración de contenido del listview
        List<String> maintitle;
        maintitle = new ArrayList<String>();
        List<String> subtitle;
        subtitle = new ArrayList<String>();
        List<Integer> imgid;
        imgid = new ArrayList<>();


        try {
            JSONObject respuesta = new JSONObject(response);
            String correcto = respuesta.getString("Correcto");
            String objetorespuesta = respuesta.getString("ObjetoRespuesta");
            String mensaje = respuesta.getString("Mensaje");
            if (correcto.equals("true")){
                JSONObject respuestaobjeto = new JSONObject(objetorespuesta);
                String control = respuestaobjeto.getString("Controles");
                JSONArray controles = new JSONArray(control);
                for (int j = 0; j<=controles.length(); j++) {
                    JSONObject controlfinal = controles.getJSONObject(j);
                    //JSONObject posicionA = new JSONObject(controlfinal);
                    //ciclo para cargar las posiciones de carga
                    for (int m = 0; m <= controlfinal.length(); m++) {
                        JSONObject posiciones = controlfinal.getJSONObject(String.valueOf(m));
                        String posicioncarga = posiciones.getString("PosicionCargaId");
                        String numnerointerno = posiciones.getString("NumeroInterno");

                        maintitle.add("PC" + numnerointerno);
                        subtitle.add("Magna |  Premium  |  Diesel");
                        imgid.add(R.drawable.gas);
                    }
                }
            }else{
                //El usuario no tiene posiciones de carga asociadas
                try {
                    AlertDialog.Builder builder = new AlertDialog.Builder(posicionFinaliza.this);
                    builder.setTitle("Ventas");
                    builder.setMessage(mensaje)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent1 = new Intent(getApplicationContext(), claveFinalizaVenta.class);
                                    startActivity(intent1);
                                    finish();
                                }
                            }).show();
                }catch (Exception e){
                    e.printStackTrace();
                }



            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Inicializacion del listview con el adaptador
        ListAdapterProd adapterProd = new ListAdapterProd(this, maintitle, subtitle, imgid);
        list = (ListView) findViewById(R.id.list);
        list.setAdapter(adapterProd);
        //Definición del Evento Click del listview
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // declaracion de variables, posicion seleccionada en el listview
                int posic = position + 1;
                String posi = String.valueOf(posic);
                posicion = posi;
                //Se llama la clase para la clave del usuario
                if (lugarproviene.equals("1")) {
                    solicitadespacho();
                }else {
                    validaPosicionDisponible(posi);
                }
            }
        });
    }

    private void validaPosicionDisponible(final String posicioncarga) {
        // URL para obtener los empleados  y huellas de la posición de carga X
        String url = "http://" + ipEstacion + "/CorpogasService/api/tickets/validaPendienteCobro/estacionId/" + EstacionId + "/posicionCargaId/" + posicioncarga;
        // Utilizamos el metodo Post para validar la contraseña
        StringRequest eventoReq = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject p1 = new JSONObject(response);

                            String correcto = p1.getString("ObjetoRespuesta");
                            if (correcto.equals("true")) {
                                if (lugarproviene == "2") {
                                    finalizaventa();
                                }
                            } else {
                                //Despacho en proceso
                                try {
                                    String mensaje = p1.getString("Mensaje");
                                    AlertDialog.Builder builder = new AlertDialog.Builder(posicionFinaliza.this);
                                    builder.setTitle("Finaliza Venta");
                                    builder.setMessage("Despacho en proceso: " + mensaje)
                                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    Intent intente = new Intent(getApplicationContext(), Munu_Principal.class);
                                                    startActivity(intente);
                                                    finish();
                                                }
                                            }).show();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                //Toast.makeText(getApplicationContext(),response,Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            //herramienta  para diagnostico de excepciones
                            //e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "error al obtener la validación posición disponible", Toast.LENGTH_SHORT).show();
                        }
                    }
                    //funcion para capturar errores
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_SHORT).show();
                //VolleyLog.e("Error: ", volleyError.getMessage());
                String algo = new String(error.networkResponse.data);
                try {
                    //creamos un json Object del String algo
                    JSONObject errorCaptado = new JSONObject(algo);
                    //Obtenemos el elemento ExceptionMesage del errro enviado
                    String errorMensaje = errorCaptado.getString("ExceptionMessage");
                    try {
                        AlertDialog.Builder builder = new AlertDialog.Builder(posicionFinaliza.this);
                        builder.setTitle("Finaliza Venta");
                        builder.setMessage("Usuario ocupado: " + errorMensaje)
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent intente = new Intent(getApplicationContext(), Munu_Principal.class);
                                        startActivity(intente);
                                    }
                                }).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

        // Añade la peticion a la cola
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        eventoReq.setRetryPolicy(new DefaultRetryPolicy(12000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(eventoReq);

    }

    private void finalizaventa() {
        //Utilizamos el metodo POST para  finalizar la Venta
        String url = "http://" + ipEstacion + "/CorpogasService/api/Transacciones/finalizaVenta/sucursal/" + sucursalId + "/posicionCarga/" + posicion + "/usuario/" + usuario;
        StringRequest eventoReq = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Toast.makeText(getApplicationContext(),response,Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getApplicationContext(), Munu_Principal.class);
                        startActivity(intent);
                        finish();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Colocar parametros para ingresar la  url
                Map<String, String> params = new HashMap<String, String>();
                return params;
            }
        };


        // Añade la peticion a la cola
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        eventoReq.setRetryPolicy(new DefaultRetryPolicy(12000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(eventoReq);
    }

    private void solicitadespacho() {

        String url = "http://" + ipEstacion + "/CorpogasService/api/despachos/autorizaDespacho/posicionCargaId/" + posicion + "/usuarioId/" + usuarioid;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject respuesta = new JSONObject(response);
                    String correctoautoriza = respuesta.getString("Correcto");
                    String mensajeautoriza = respuesta.getString("Mensaje");
                    String objetoRespuesta = respuesta.getString("ObjetoRespuesta");
                    if (correctoautoriza.equals("true")) {
                        //Se inicializa el control para solicitar confirmacion
                        AlertDialog.Builder builder;
                        //Obtengo la posicion de carga que se pasa como parametro
                        //String Posi = Posicion;
                        //Enviar datos de peoductos y posicion de carga para regresar Ticket
                        builder = new AlertDialog.Builder(posicionFinaliza.this);
                        builder.setMessage("Desea agregar productos?");
                        builder.setTitle("Ventas");
                        builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(getApplicationContext(), VentasProductos.class);
                                startActivity(intent);
                                finish();
                                dialogInterface.cancel();
                            }
                        }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                enviaMunu();
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();


                    } else {
                        Toast.makeText(getApplicationContext(), mensajeautoriza, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this.getApplicationContext());
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(12000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);

    }

    private void enviaMunu() {
        //Se inicializa el control para solicitar confirmacion
        AlertDialog.Builder builder;
        //Obtengo la posicion de carga que se pasa como parametro
        //String Posi = Posicion;
        //Enviar datos de peoductos y posicion de carga para regresar Ticket
        builder = new AlertDialog.Builder(posicionFinaliza.this);
        builder.setMessage("Venta inicializada");
        builder.setTitle("Ventas");
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(getApplicationContext(), Munu_Principal.class);
                startActivity(intent);
                finish();
                dialogInterface.cancel();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

    }
    //Metodo para regresar a la actividad principal
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), Munu_Principal.class);
        startActivity(intent);
        finish();
    }
}