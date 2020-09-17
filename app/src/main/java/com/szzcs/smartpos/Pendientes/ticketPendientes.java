package com.szzcs.smartpos.Pendientes;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.szzcs.smartpos.Munu_Principal;
import com.szzcs.smartpos.PrintFragment;
import com.szzcs.smartpos.Productos.ListAdapterFProductos;
import com.szzcs.smartpos.R;
import com.szzcs.smartpos.configuracion.SQLiteBD;

import org.bouncycastle.jce.exception.ExtCertPathValidatorException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Thread.sleep;

public class ticketPendientes extends AppCompatActivity {



    String mac=null;
    TextView txtMac;
    Bundle args = new Bundle();
    ListView list;
    String carga;
    String nousuario;
    String EstacionId, sucursalId, ipEstacion ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_pendientes);
        SQLiteBD db = new SQLiteBD(getApplicationContext());
        EstacionId = db.getIdEstacion();
        sucursalId=db.getIdSucursal();
        ipEstacion = db.getIdEstacion();

        TextView txtMac = findViewById(R.id.txtmac);
        txtMac.setText(getMacAddr());
        txtMac.setText("");
        formapagoProductos();
    }



    private void formapagoProductos(){
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
            final List<String> maintitle;
            maintitle = new ArrayList<String>();
            final List<String> subtitle;
            subtitle = new ArrayList<String>();
            List<Integer> imgid;
            imgid = new ArrayList<>();

            final List<Integer> idcopias;
            idcopias = new ArrayList<>();

            //JSONObject jsonObject = new JSONObject(response);
            //String formapago = jsonObject.getString("SucursalFormapagos");


            JSONArray nodo = new JSONArray(response);
            for (int i = 0; i <nodo.length() ; i++) {
                JSONObject nodo1 = nodo.getJSONObject(i);
                String numero_pago = nodo1.getString("FormaPagoId");
                String formapago1 = nodo1.getString("FormaPago");
                JSONObject nodo2 = new JSONObject(formapago1);
                String nombre_pago = nodo2.getString("DescripcionLarga");
                String numero_ticket = nodo2.getString("NumeroTickets");

                maintitle.add(nombre_pago);
                subtitle.add(numero_pago);
                idcopias.add(Integer.parseInt(numero_ticket));
                switch(numero_pago) {
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
                }  //Inicializacion del listview con el adaptador

                ListAdapterFProductos adaptador = new ListAdapterFProductos(this, maintitle, subtitle, imgid);
                list=(ListView)findViewById(R.id.list);
                list.setAdapter(adaptador);
                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                        //Obtiene valor del numero de copias y la forma de pago
                        String  FormaPagoId = maintitle.get(i).toString();
                        String  copias = idcopias.get(i).toString();
                        //EncabezadoTicket();
                        TicketPendiente(FormaPagoId, copias);
                        //TicketPendiente();

                    }
                });
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    private void TicketPendiente(final String FormaPagoId, final String copias){
        EditText pasword = (EditText) findViewById(R.id.pasword);
        final String numeroTarjetero = pasword.getText().toString();

        final String numerorecibo = "";
        final String numerotransaccion = "";
        final String numerorastreo="";
        final String posicion="";
        final String despachador="";
        final String vendedor="";
        final String subtotal="";
        final String iva="";
        final String totaltotalTexto="";

        //Conexion con la base y ejecuta consulta para saber si tiene tickets Pendientes
        String url = "http://10.2.251.58/CorpogasService/api/tickets/pendiente/"+numeroTarjetero;
        // Utilizamos el metodo Post para validar la contraseña
        StringRequest eventoReq = new StringRequest(Request.Method.POST,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //Se instancia la respuesta del json
                            JSONObject validar = new JSONObject(response);
                            //Se asigna el resultado a String
                            String valido = validar.getString("Resultado");
                            if (valido.equals("null")) { //==null
                                String detalle = validar.getString("Detalle");
                                //Si el detalle es null es que ya se imprimiio
                                //validar detalle con un if
                                if (detalle.equals("null")) {
                                    //JSONObject mensaj = new JSONObject(valido);
                                    //String mensajes = mensaj.getString("Descripcion");
                                    //Toast.makeText(getApplicationContext(), mensajes, Toast.LENGTH_SHORT).show();
                                    try {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(ticketPendientes.this);
                                        builder.setTitle("Tickets Pendientes");
                                        builder.setMessage("Sin datos")
                                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        Intent intente = new Intent(getApplicationContext(), Munu_Principal.class);
                                                        startActivity(intente);
                                                    }
                                                }).show();
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }

                                }else {
                                    JSONObject detalleRespuesta = new JSONObject(detalle);
                                    String numeroticket = detalleRespuesta.getString("NoRecibo");
                                    String numerorecibo = detalleRespuesta.getString("NoRecibo");
                                    String numerotransaccion = detalleRespuesta.getString("NoTransaccion");
                                    String numerorastreo = detalleRespuesta.getString("NoRastreo");
                                    String posicion = detalleRespuesta.getString("PosCarga");
                                    String despachador = detalleRespuesta.getString("Desp"); //
                                    String vendedor = detalleRespuesta.getString("Vend");//Vend

                                    String subtotal = detalleRespuesta.getString("Subtotal");
                                    String iva = detalleRespuesta.getString("IVA");
                                    String total = detalleRespuesta.getString("Total");
                                    String totalTexto = detalleRespuesta.getString("TotalTexto");

                                    //Convertir a strig Productos
                                    String ProductosEncontrados = detalleRespuesta.getString("Productos");
                                    //Generar el jsonArray del string anterior
                                    JSONArray producto = new JSONArray(ProductosEncontrados);

                                    String cantidad = new String();
                                    String protic = new String();
                                    String numero = new String();

                                    String descrip = new String();
                                    String impor = new String();
                                    String precio = new String();

                                    for (int i = 0; i < producto.length(); i++) {
                                        JSONObject p1 = producto.getJSONObject(i);
                                        String value = p1.getString("Cantidad").trim();
                                        cantidad += value;
                                        String num = " ";// p1.getString("No");
                                        numero += num;
                                        String descripcion = p1.getString("Descripcion");
                                        descrip += descripcion;
                                        String importe = p1.getString("Importe");
                                        impor += importe;
                                        String prec = p1.getString("Precio");
                                        precio += prec;
                                        protic +=value + " | " + descripcion + " | " + prec + " | " + importe+"\n";
                                    }
                                    args.putString("numerorecibo", numerorecibo);
                                    args.putString("nombrepago", FormaPagoId);
                                    args.putString("numerotransaccion", numerotransaccion);
                                    args.putString("numerorastreo", numerorastreo);
                                    args.putString("posicion", posicion);
                                    args.putString("despachador", despachador);
                                    args.putString("vendedor", vendedor);
                                    args.putString("productos", protic);

                                    args.putString("numero", numero);
                                    args.putString("descrip",descrip);
                                    args.putString("impor",impor);
                                    args.putString("precio",precio);

                                    args.putString("subtotal",subtotal);
                                    args.putString("iva",iva);
                                    args.putString("total",total);
                                    args.putString("totaltexto",totalTexto);

                                    nousuario = getIntent().getStringExtra("user"); //usuarioid
                                    String formapago = FormaPagoId; //pago.getText().toString();

                                    args.putString("numticket", copias);
                                    args.putString("idusuario", nousuario);
                                }

                                String pieTicket = validar.getString("Pie");
                                JSONObject mensajeTicket = new JSONObject(pieTicket);
                                String mensajePie = mensajeTicket.getString("Mensaje");
                                args.putString("mensaje",mensajePie);


                                //args.putString("numerorecibo", numerorecibo);
                                //args.putString("nombrepago", nombrepago);
                                //args.putString("numticket", numticket);
                                //args.putString("numerotransaccion", numerotransaccion);
                                //args.putString("numerorastreo", numerorastreo);
                                //args.putString("posicion", carga);
                                //args.putString("despachador",despachador);
                                //args.putString("vendedor",user);
                                //args.putString("productos",protic);

                                //args.putString("subtotal",subtotal);
                                //args.putString("iva",iva);
                                //args.putString("total",total);
                                //args.putString("totaltexto",totaltexto);
                                //args.putString("mensaje",names.toString());
                                try {
                                    PrintFragment cf = new PrintFragment();
                                    cf.setArguments(args);
                                    getFragmentManager().beginTransaction().replace(R.id.tv1, cf).
                                            addToBackStack(PrintFragment.class.getName()).
                                            commit();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }else{
                                JSONObject algo = new JSONObject(valido);
                                String desc = algo.getString("Descripcion");
                                String errorenviado = algo.getString("Error");
                                Toast.makeText(getApplicationContext(), errorenviado.toString(), Toast.LENGTH_SHORT).show();
                                try {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(ticketPendientes.this);
                                    builder.setTitle("Tickets Pendientes");
                                    builder.setMessage(errorenviado)
                                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    Intent intente = new Intent(getApplicationContext(), Munu_Principal.class);
                                                    startActivity(intente);
                                                }
                                            }).show();
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }

                        } catch (JSONException e) {
                            //herramienta  para diagnostico de excepciones
                            e.printStackTrace();
                        }
                    }
                    //funcion para capturar errores
                }, new Response.ErrorListener() {
            String PruebaError;
            @Override
            public void onErrorResponse(VolleyError error) {
                //asiganmos a una variable el error para desplegar la descripcion de Tickets no asignados a la terminal
                String algo = new String(error.networkResponse.data) ;
                try {
                    //creamos un json Object del String algo
                    JSONObject errorCaptado = new JSONObject(algo);
                    //Obtenemos el elemento ExceptionMesage del errro enviado
                    String errorMensaje = errorCaptado.getString("ExceptionMessage");
                    try {
                        AlertDialog.Builder builder = new AlertDialog.Builder(ticketPendientes.this);
                        builder.setTitle("Tickets Pendientes");
                        builder.setMessage(errorMensaje)
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent intente = new Intent(getApplicationContext(), Munu_Principal.class);
                                        startActivity(intente);
                                    }
                                }).show();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    //MostrarDialogoSimple(errorMensaje);
                    //Toast.makeText(getApplicationContext(),errorMensaje,Toast.LENGTH_SHORT).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        // Añade la peticion a la cola
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(eventoReq);
            //-------------------------Aqui termina el volley --------------
    }


    private void MostrarDialogoSimple(final String mensaje){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Tickets Pendientes");
        builder.setMessage(mensaje)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intente = new Intent(getApplicationContext(), Munu_Principal.class);
                        startActivity(intente);
                    }
                }).show();
    }


    public String getMacAddr() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }
                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    //res1.append(Integer.toHexString(b & 0xFF) + ":");
                    res1.append(String.format("%02X:",b));
                }
                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
        }
        return "";
    }
}