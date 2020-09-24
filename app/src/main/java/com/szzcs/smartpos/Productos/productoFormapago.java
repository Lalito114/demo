package com.szzcs.smartpos.Productos;

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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.szzcs.smartpos.Munu_Principal;
import com.szzcs.smartpos.PrintFragment;
import com.szzcs.smartpos.R;
import com.szzcs.smartpos.configuracion.SQLiteBD;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class productoFormapago extends AppCompatActivity {
    //Definicion de variables
    ListView list;
    TextView nose;
    String carga;
    String nousuario;
    Bundle args = new Bundle();

    String EstacionId, sucursalId, ipEstacion ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_producto_formapago);
        //instruccion para que aparezca la flecha de regreso
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        nose = findViewById(R.id.nose);
        SQLiteBD db = new SQLiteBD(getApplicationContext());
        EstacionId = db.getIdEstacion();
        sucursalId=db.getIdSucursal();
        ipEstacion = db.getIpEstacion();

        //Carga las Posiciones de Carga
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
            //Se inicializan los arreglos  para el titulo, el subtitulo y la imagen
            List<String> maintitle;
            maintitle = new ArrayList<String>();
            final List<String> subtitle;
            subtitle = new ArrayList<String>();
            List<Integer> imgid;
            imgid = new ArrayList<>();

            final List<Integer> idcopias;
            idcopias = new ArrayList<>();

            //Se convierte el response a un json
            //JSONObject jsonObject = new JSONObject(response);
            //se convierte a estring SucursalFormaPagos
            //String formapago = jsonObject.getString("SucursalFormapagos");

            //Se convierte el string a array para poder parsearlo
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
                }
                //Inicializacion del listview con el adaptador

                ListAdapterFProductos adaptador = new ListAdapterFProductos(this, maintitle, subtitle, imgid);
                list=(ListView)findViewById(R.id.list);
                list.setAdapter(adaptador);
                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                        //Obtiene valor del numero de copias y la forma de pago
                        String  FormaPagoId = subtitle.get(i).toString();
                        String  copias = idcopias.get(i).toString();
                        EnviarDatos(FormaPagoId, copias);
                    }
                });
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void EnviarDatos(final String FormaPagoId, final String Copias){
        final String posicion;
        posicion = "3";//getIntent().getStringExtra("posicion");
        //final String usuarioid;
        //usuarioid = getIntent().getStringExtra("usuario");

        //Se inicializa el control para solicitar confirmacion
        AlertDialog.Builder builder;
        //Obtengo la posicion de carga que se pasa como parametro
        //String Posi = Posicion;
        //Enviar datos de peoductos y posicion de carga para regresar Ticket
        builder = new AlertDialog.Builder(this);
        if (FormaPagoId == "2") { //Efectivo
            builder.setMessage("Desea imprimir el ticket?");
            builder.setTitle("Venta de Productos");
            builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //se genera el encabezado para el ticket
                    //obtenerEncabezado(Copias);
                    //Funcion para obtener los datos del ticker
                    //obtenerdatosticket(FormaPagoId, Copias);
                    ObtenerCuerpoTicket(FormaPagoId, Copias);

                }
            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //EnviaVenta;
                    dialogInterface.cancel();
                    //Intent intent = new Intent(getApplicationContext(), terminaVenta.class);
                    //intent.putExtra("car",posicion);
                    //intent.putExtra("user",usuarioid);
                    //startActivity(intent);
                    //Utilizamos el metodo POST para  finalizar la Venta
                    String url = "http://"+ipEstacion+"/CorpogasService/api/Transacciones/finalizaVenta/sucursal/"+sucursalId+"/posicionCarga/"+posicion;
                    StringRequest eventoReq = new StringRequest(Request.Method.POST,url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    Toast.makeText(getApplicationContext(),response,Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(getApplicationContext(), Munu_Principal.class);
                                    startActivity(intent);
                                    try {
                                        JSONObject jsonObject = new JSONObject(response);
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
                    requestQueue.add(eventoReq);
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }else{
            //se genera el encabezado para el ticket
            //obtenerEncabezado(Copias);
            //Funcion para obtener los datos del ticker
            //obtenerdatosticket(FormaPagoId, Copias);
            ObtenerCuerpoTicket(FormaPagoId, Copias);
        }
    }

    //Funcion para encabezado del Ticket
    public void obtenerEncabezado(final String ncopias){
        //Utilizamos el metodo Get para obtener el encabezado para los tickets
        //hay que cambiar el volo 1 del fina po el numeo de la estacion que se encuentra
        String url = "http://"+ipEstacion+"/CorpogasService/api/tickets/cabecero/estacion/"+EstacionId;
        //Se solicita peticion GET para obtener el encabezado
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    //se instancia la respuesta del JSON
                    JSONObject encabezado = new JSONObject(response);

                    String cabecero = encabezado.getString("Cabecero");
                    JSONObject encabezado1 = new JSONObject(cabecero);
                    String empresa = encabezado1.getString("Empresa");

                    JSONObject empresa1 = new JSONObject(empresa);
                    String rfc = empresa1.getString("Rfc");


                    // si la respuesta del json contine informacion
                    if (encabezado != null){
                        //Asignacion a variables para encabezado

                        String idestacion = encabezado.getString("GrupoId"); //IdEstacionInt
                        String nombre = encabezado.getString("RazonSocial"); //Nombre
                        String Rfc = encabezado.getString("RFC");
                        String siic = "00000000000"; //encabezado.getString("SIIC");
                        String regimen = "Regimen General de Ley Personas Morales"; //encabezado.getString("Regimen");

                        JSONObject direccion = encabezado.getJSONObject("Domicilio");
                        String calle = direccion.getString("Calle");
                        String exterior = direccion.getString("NumeroExterior");
                        String interior = direccion.getString("NumeroInterior");
                        String colonia = direccion.getString("Colonia");
                        String localidad = direccion.getString("Localidad");
                        String municipio = direccion.getString("Municipio");
                        String estado = direccion.getString("Estado");
                        String cp = direccion.getString("CodigoPostal");
                        String pais = direccion.getString("Pais");

                        args.putString("numcopias",ncopias);
                        args.putString("noestacion", idestacion);
                        args.putString("nombreestacion", nombre);
                        args.putString("razonsocial", Rfc);
                        args.putString("datos",siic);
                        args.putString("regimenfiscal",regimen);
                        args.putString("calle",calle);
                        args.putString("exterior",exterior);
                        args.putString("colonia",colonia);
                        args.putString("localidad",localidad);
                        args.putString("municipio",municipio);
                        args.putString("estado",estado);
                        args.putString("cp",cp);
                        args.putString("pais",pais);

                        //Se instancia el PrintFragment
//                        PrintFragment cf = new PrintFragment();
//                        cf.setArguments(args);
//                        getFragmentManager().beginTransaction().replace(R.id.tv1, cf).
//                                addToBackStack(PrintFragment.class.getName()).  //Modificado
//                                commit();
                    }else{
                        //Si el json no contiene informacion se envia mensahje
                        Toast.makeText(getApplicationContext(),"No se obtuvo un venta", Toast.LENGTH_LONG).show();
                    }


                } catch (JSONException e) {
                    //herramienta  para diagnostico de excepciones
                    e.printStackTrace();
                }
            }
            //funcion para capturar errores
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),error.toString(), Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> parametros = new HashMap<String, String>();



                return parametros;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this.getApplicationContext());
        requestQueue.add(stringRequest);

    }

    //Funcion para obtener los datos del ticket
    public void obtenerdatosticket(final String PagoSeleccionado, final String ncopias){
        String url = "http://"+ipEstacion+"/TransferenciaDatosAPI/api/tickets/getticket";
        //Utilizamos el metodo Post para colocar los datos en el  ticket
        StringRequest eventoReq = new StringRequest(Request.Method.POST,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //Se instancia la respuesta del json
                            JSONObject ticket = new JSONObject(response);
                            String body1 = ticket.getString("body");

                            String footer = ticket.getString("footer");
                            JSONObject footer1 = new JSONObject(footer);
                            String mensaje = footer1.getString("Mensaje");


                            JSONObject partebody = new JSONObject(body1);
                            String recibo = partebody.getString("NoRecibo");
                            String transaccion = partebody.getString("NoTransaccion");
                            String rastreo = partebody.getString("NoRastreo");
                            String posicion = partebody.getString("PosCarga");
                            String desp = partebody.getString("Desp");
                            String vendedor = partebody.getString("Vend");
                            //String mensaje = partebody.getString("footer");

                            String subtotal = partebody.getString("Subtotal");
                            String iva = partebody.getString("IVA");
                            String total = partebody.getString("Total");
                            String totaltexto = partebody.getString("TotalTexto");

                            JSONObject person = new JSONObject(response);

                            String name = person.getString("body");

                            JSONObject product = new JSONObject(name);

                            JSONArray producto = product.getJSONArray("producto");

                            String cantidad = new String();
                            String protic = new String();
                            String numero = new String();

                            String descrip = new String();
                            String impor = new String();
                            String precio = new String();

                            for (int i = 0; i <producto.length() ; i++) {
                                JSONObject p1 = producto.getJSONObject(i);
                                String value = p1.getString("Cantidad");
                                cantidad +=  value;

                                String num = p1.getString("No");
                                numero +=num ;

                                String descripcion = p1.getString("Descripcion");
                                descrip += descripcion;

                                String importe = p1.getString("Importe");
                                impor +=importe;

                                String prec = p1.getString("Precio");
                                precio += prec;

                                protic += "   "+ value + "    " + num + "     " + descripcion + "  " + prec + "  " + importe+"\n";
                            }



                            args.putString("norecibo", recibo);
                            args.putString("norastreo", rastreo);
                            args.putString("posicion", posicion);
                            args.putString("cantidad", protic);
                            args.putString("numero", numero);
                            args.putString("descrip",descrip);
                            args.putString("impor",impor);
                            args.putString("precio",precio);

                            args.putString("subtotal",subtotal);
                            args.putString("iva",iva);
                            args.putString("total",total);
                            args.putString("totaltexto",totaltexto);
                            args.putString("mensaje",mensaje);

                        } catch (JSONException e) {
                            //herramienta  para diagnostico de excepciones
                            e.printStackTrace();
                        }
                    }
                    //funcion para capturar errores
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
                carga = getIntent().getExtras().getString("posicion");
                nousuario = getIntent().getExtras().getString("usuario");
                //pago = findViewById(R.id.pago);
                String formapago = PagoSeleccionado; //pago.getText().toString();
                // Toast.makeText(getApplicationContext(), formapago, Toast.LENGTH_SHORT).show();


                args.putString("numcopias", ncopias);
                args.putString("formadepago", PagoSeleccionado);
                params.put("IdFormaPago", formapago);
                params.put("Id_Usuario",nousuario);
                args.putString("idusuario", nousuario);
                params.put("PosCarga",carga);

                return params;
            }
        };

        // Añade la peticion a la cola
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(eventoReq);

        //-------------------------------------------------------------------------------
    }



    private void EnviarTransaccion(final String FormaPago, final String Copias) {
        //Se obtienen los datos de las variables posicion de carga y usuario
        final String posicion;
        posicion = getIntent().getStringExtra("posicion");
        final String usuarioid;

        usuarioid = getIntent().getStringExtra("usuario");
        final String arreglo;
        arreglo = getIntent().getStringExtra("myjson");

        //Cambiarlo a arreglo para poder enviar el json

        JSONArray mjasonArray= null;
        try {
            mjasonArray = new JSONArray(arreglo);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final JSONArray finalMjasonArray = mjasonArray;

        //Utilizamos el metodo POST para  enviar la transaccion y regrese el transaccionId
        String url = "http://"+ipEstacion+"/CorpogasService/api/ventaProductos/sucursal/"+sucursalId+"/procedencia/"+posicion+"/tipoTransaccion/1/empleado/"+usuarioid;  //api/tarjetas/sendtarjeta
        StringRequest eventoReq = new StringRequest(Request.Method.POST,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String transaccion = jsonObject.getString("TransaccionId");
                            String transaccionId = jsonObject.getString("Id");
                            String transaccionImporte = jsonObject.getString("Importe");
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
                params.put("", finalMjasonArray.toString());  //mjason.toString()
                return params;
            }
        };

        // Añade la peticion a la cola
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(eventoReq);
    }

    public void ObtenerCuerpoTicket(final String nombrepago, final String numticket) {
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

                            String carga = getIntent().getStringExtra("car");
                            String user = getIntent().getStringExtra("user");
                            args.putString("numerorecibo", numerorecibo);
                            args.putString("nombrepago", nombrepago);
                            args.putString("numticket", numticket);
                            args.putString("numerotransaccion", numerotransaccion);
                            args.putString("numerorastreo", numerorastreo);
                            args.putString("posicion", carga);
                            args.putString("despachador",despachador);
                            args.putString("vendedor",user);
                            args.putString("productos",protic);

                            args.putString("subtotal",subtotal);
                            args.putString("iva",iva);
                            args.putString("total",total);
                            args.putString("totaltexto",totaltexto);
                            args.putString("mensaje",names.toString());
//                            PrintFragment cf = new PrintFragment();
//                            cf.setArguments(args);
//                            getFragmentManager().beginTransaction().replace(R.id.tv1, cf).
//                                    addToBackStack(PrintFragment.class.getName()).
//                                    commit();
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
                String carga = getIntent().getStringExtra("car");
                String user = getIntent().getStringExtra("user");
                params.put("PosCarga", carga);
                params.put("IdUsuario",user);
                params.put("IdFormaPago", nombrepago);
                params.put("SucursalId",data.getIdEstacion());
                return params;
            }
        };


        // Añade la peticion a la cola
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(eventoReq);
    }



}