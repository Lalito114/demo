package com.szzcs.smartpos.Productos;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.szzcs.smartpos.FinalizaVenta.posicionFinaliza;
import com.szzcs.smartpos.Helpers.Modales.Modales;
import com.szzcs.smartpos.Munu_Principal;
import com.szzcs.smartpos.PrintFragment;
import com.szzcs.smartpos.PrintFragmentVale;
import com.szzcs.smartpos.R;
import com.szzcs.smartpos.configuracion.SQLiteBD;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FPaga extends AppCompatActivity {
    String EstacionId, sucursalId, ipEstacion, numerooperativa ;
    Bundle args = new Bundle();
    String tanquellenonumerooperativa="5";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SQLiteBD db = new SQLiteBD(getApplicationContext());
        EstacionId = db.getIdEstacion();
        sucursalId = db.getIdSucursal();
        ipEstacion = db.getIpEstacion();

        setContentView(R.layout.activity_f_paga);
        ListView list;
        list = findViewById(R.id.list);
        numerooperativa = getIntent().getStringExtra("numeroOperativa");
        if (numerooperativa == tanquellenonumerooperativa) {//"Tanque LLeno"
            String NombreFormapago = "Tanque Lleno";
            String  FormaPagoId = "18";
            String  copias = "2";
            EnviarDatos(FormaPagoId, copias, NombreFormapago);
        } else{
            cargaFormapago();
        }
    }
    private void cargaFormapago(){
        //Declaramos direccion URL de las posiciones de carga. Para acceder a los metodos de la API
        String url = "http://"+ipEstacion+"/CorpogasService/api/sucursalformapagos/sucursal/"+sucursalId;
        //inicializamos el String reques que es el metodo de la funcion de Volley que no va a permir accder a la API
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            //El metodo onResponse el cual va cachar si hay una respuesta de tipo cadena
            public void onResponse(String response) {
                //llamamos al metodo posicion en donde aoptine como resultado
                //el valos maximo de posiciones de carga
                vax(response);
            }
            //si exite un error este entrata de el metodo ErrorListener
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),error.toString(), Toast.LENGTH_LONG).show();
            }
        });
        //Ejecutamos el stringrequest para invocar a la clase volley
        RequestQueue requestQueue = Volley.newRequestQueue(this.getApplicationContext());
        //Agregamos el stringrequest al Requestque
        requestQueue.add(stringRequest);
    }

    private void vax(final String response){
        try {
            //Se inicializan los arreglos  para el titulo, el subtitulo y la imagen
            final List<String> maintitle;
            maintitle = new ArrayList<String>();
            final List<String> subtitle;
            subtitle = new ArrayList<String>();
            List<Integer> imgid;
            imgid = new ArrayList<>();

            final List<Integer> idcopias;
            idcopias = new ArrayList<>();

            //Se convierte el string a array para poder parsearlo
            JSONArray nodo = new JSONArray(response);
            for (int i = 0; i <nodo.length() ; i++) {

                JSONObject nodo1 = nodo.getJSONObject(i);
                String numero_pago = nodo1.getString("FormaPagoId");
                String formapago1 = nodo1.getString("FormaPago");
                JSONObject nodo2 = new JSONObject(formapago1);
                String nombre_pago = nodo2.getString("DescripcionLarga");
                String numero_ticket = nodo2.getString("NumeroTickets");
                String VerTarjetero = nodo2.getString("VisibleTarjetero");

                if (VerTarjetero == "true") {
                    maintitle.add(nombre_pago);
                    subtitle.add(numero_pago);
                    idcopias.add(Integer.parseInt(numero_ticket));
                    switch (numero_pago) {
                        case "1":
                            imgid.add(R.drawable.monedero);
                            break;
                        case "2":
                            imgid.add(R.drawable.billete);
                            break;
                        case "3":
                            imgid.add(R.drawable.vale);
                            break;
                        case "4":
                            imgid.add(R.drawable.amex);
                            break;
                        case "5":
                            imgid.add(R.drawable.gascard);
                            break;
                        case "6":
                            imgid.add(R.drawable.visa);
                            break;
                        case "7":
                            imgid.add(R.drawable.valeelectronico);
                            break;
                        case "8":
                            imgid.add(R.drawable.corpogas);
                            break;
                        case "9":
                            imgid.add(R.drawable.corpomobil);
                            break;
                        case "10":
                            imgid.add(R.drawable.monedero);
                            break;
                        case "11":
                            imgid.add(R.drawable.jarreo);
                            break;
                        case "12":
                            imgid.add(R.drawable.monedero);
                            break;
                        default:
                            imgid.add(R.drawable.camera);
                    }
                    //Inicializacion del listview con el adaptador
                }
                ListAdapterFProductos adaptador = new ListAdapterFProductos(this, maintitle, subtitle, imgid);
                ListView list;
                list= findViewById(R.id.list);
                list.setAdapter(adaptador);
                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                        //Obtiene valor del numero de copias y la forma de pago
                        String NombreFormapago = maintitle.get(i).toString();
                        String  FormaPagoId = subtitle.get(i).toString();
                        String  copias = idcopias.get(i).toString();
                        EnviarDatos(FormaPagoId, copias, NombreFormapago);
                    }
                });
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void EnviarDatos(final String FormaPagoId, final String Copias, final String NombreFormapago){
        final String posicion;
        posicion = getIntent().getStringExtra("posicion");
        final String usuarioid;
        usuarioid = getIntent().getStringExtra("usuario");

        //Se inicializa el control para solicitar confirmacion
        AlertDialog.Builder builder;
        //Obtengo la posicion de carga que se pasa como parametro
        //String Posi = Posicion;
        //Enviar datos de peoductos y posicion de carga para regresar Ticket
        builder = new AlertDialog.Builder(this);
        if (FormaPagoId == "2") { //Efectivo

            String titulo = "Finaliza Venta";
            String mensaje = "Desea finalizar la venta?";
            Modales modales = new Modales(FPaga.this);
            View viewLectura = modales.MostrarDialogoAlerta(FPaga.this, mensaje,  "FINALIZAR", "Imprimir");
            viewLectura.findViewById(R.id.buttonYes).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finalizaventa(posicion, usuarioid);
                }
            });

            viewLectura.findViewById(R.id.buttonNo).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ObtenerCuerpoTicket(NombreFormapago, Copias, posicion, usuarioid, FormaPagoId);
                    modales.alertDialog.dismiss();
                }
            });

//            builder.setMessage("Desea finalizar  la Venta?");
//            builder.setTitle("Venta de Productos");
//            builder.setCancelable(false);
//            builder.setPositiveButton("FINALIZAR", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialogInterface, int i) {
//                    //Finaliza Venta;
//                    dialogInterface.cancel();
//                    finalizaventa(posicion, usuarioid);
//                }
//            }).setNegativeButton("imprimir", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialogInterface, int i) {
//                    //Funcion para obtener los datos del ticker
//                    ObtenerCuerpoTicket(NombreFormapago, Copias, posicion, usuarioid, FormaPagoId);
//                }
//            });
//            AlertDialog dialog = builder.create();
//            dialog.show();
        } else {
            //Funcion para obtener los datos del ticker
            ObtenerCuerpoTicket(FormaPagoId, Copias, posicion, usuarioid, FormaPagoId);
        }
    }

    private void finalizaventa(final String posicion, final String idUsuario){
        //Utilizamos el metodo POST para  finalizar la Venta
        String url = "http://"+ipEstacion+"/CorpogasService/api/Transacciones/finalizaVenta/sucursal/"+sucursalId+"/posicionCarga/"+posicion+"/usuario/"+idUsuario;
        StringRequest eventoReq = new StringRequest(Request.Method.POST,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject respuesta = new JSONObject(response);
                            String correcto = respuesta.getString("Correcto");
                            String mensaje = respuesta.getString("Mensaje");
                            String objetoRespuesta = respuesta.getString("ObjetoRespuesta");
                            if (objetoRespuesta.equals(null)) {
                                try {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(FPaga.this);
                                    builder.setTitle("Productos");
                                    builder.setCancelable(false);
                                    builder.setMessage(""+ mensaje )
                                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    Intent intent = new Intent(getApplicationContext(), Munu_Principal.class);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            }).show();
                                }catch (Exception e){
                                    e.printStackTrace();
                                }

                            }else {
                                try {
                                    String titulo = "AVISO";
                                    String mensajes = "Venta Finalizada Correctamente";
                                    Modales modales = new Modales(FPaga.this);
                                    View view1 = modales.MostrarDialogoAlertaAceptar(FPaga.this,mensajes,titulo);
                                    view1.findViewById(R.id.buttonYes).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            modales.alertDialog.dismiss();
                                            Intent intent = new Intent(getApplicationContext(), Munu_Principal.class);
                                            startActivity(intent);
                                            finish();

                                        }
                                    });


//                                    AlertDialog.Builder builder = new AlertDialog.Builder(FPaga.this);
//                                    builder.setTitle("Productos");
//                                    builder.setCancelable(false);
//                                    builder.setMessage("Venta Finalizada Correctamente" )
//                                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                                                @Override
//                                                public void onClick(DialogInterface dialogInterface, int i) {
//                                                    Intent intent = new Intent(getApplicationContext(), Munu_Principal.class);
//                                                    startActivity(intent);
//                                                    finish();
//                                                }
//                                            }).show();
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_SHORT).show();
            }
        }){
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



    public void ObtenerCuerpoTicket(final String nombrepago, final String numticket, final String posicion, final String usuario, final String FormaPagoId) {
        final SQLiteBD data = new SQLiteBD(getApplicationContext());
        String url = "http://"+ipEstacion+"/CorpogasService/api/tickets/generar";

        StringRequest eventoReq = new StringRequest(Request.Method.POST,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String detalle = jsonObject.getString("Detalle");
                            String pie = jsonObject.getString("Pie");
                            JSONObject mensaje = new JSONObject(pie);
                            JSONArray names = mensaje.getJSONArray("Mensaje");

                            JSONObject det = new JSONObject(detalle);
                            String numerorecibo = det.getString("NoRecibo");
                            String numerotransaccion = det.getString("NoTransaccion");
                            String numerorastreo = det.getString("NoRastreo");
                            String poscarga = det.getString("PosCarga");
                            String despachador = det.getString("Desp");
                            String vendedor = det.getString("Vend");
                            String prod = det.getString("Productos");

                            JSONArray producto = det.getJSONArray("Productos");
                            String protic = new String();
                            for (int i = 0; i <producto.length() ; i++) {
                                JSONObject p1 = producto.getJSONObject(i);
                                String value = p1.getString("Cantidad");

                                String descripcion = p1.getString("Descripcion");

                                String importe = p1.getString("Importe");

                                String prec = p1.getString("Precio");

                                protic +=value + " | " + descripcion + " | " + prec + " | " + importe+"\n";
                            }

                            String subtotal = det.getString("Subtotal");
                            String iva = det.getString("IVA");
                            String total = det.getString("Total");
                            String totaltexto = det.getString("TotalTexto");
                            String clave = det.getString("Clave");

                            args.putString("numerorecibo", numerorecibo);
                            args.putString("nombrepago", nombrepago);
                            args.putString("numticket", numticket);
                            args.putString("numerotransaccion", numerotransaccion);
                            args.putString("numerorastreo", numerorastreo);
                            args.putString("posicion", posicion);
                            args.putString("despachador",despachador);
                            args.putString("vendedor",usuario);
                            args.putString("productos",protic);

                            args.putString("subtotal",subtotal);
                            args.putString("iva",iva);
                            args.putString("total",total);
                            args.putString("totaltexto",totaltexto);
                            args.putString("mensaje",names.toString());
                            PrintFragment cf = new PrintFragment();
                            cf.setArguments(args);
                            getFragmentManager().beginTransaction().replace(R.id.tv1, cf).
                                    addToBackStack(PrintFragment.class.getName()).
                                    commit();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("PosCarga", posicion);
                params.put("IdUsuario",usuario);
                params.put("IdFormaPago", FormaPagoId);
                //"TipoMonedero": 4,
                //"TransaccionId": 5,
                params.put("SucursalId",data.getIdSucursal());
                //"Folio": 7,
                //"Clave": "sample string 8"
                return params;
            }
        };
        // Añade la peticion a la cola
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(eventoReq);
    }

    //Metodo para regresar a la actividad principal
    @Override
    public void onBackPressed() {
        final String posicion;
        posicion = getIntent().getStringExtra("posicion");
        final String usuarioid;
        usuarioid = getIntent().getStringExtra("usuario");

        finalizaventa(posicion, usuarioid);
        Intent intent = new Intent(getApplicationContext(), Munu_Principal.class);
        startActivity(intent);
        finish();
    }

}