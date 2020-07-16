package com.szzcs.smartpos.Ticket;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.szzcs.smartpos.MainActivity;
import com.szzcs.smartpos.Munu_Principal;
import com.szzcs.smartpos.PrintFragment;
import com.szzcs.smartpos.R;
import com.szzcs.smartpos.utils.Kits;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

//Clase para desplegar Formas de Pago
public class formas_de_pago extends AppCompatActivity {
    private ListView lvItems;
    private Adaptador adaptador;
    private ArrayList<Entidad> arrayentidad;

    //Definicion de variables
    TextView nose;
    String carga;
    String nousuario;

    EditText pago;
    Bundle args = new Bundle();

    private Entidad Item;
    private TextView tvDescripcion;
    private String tvIdent;

    ArrayList<Entidad> listItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_formas_de_pago);
        nose = findViewById(R.id.nose);
        obtenerformasdepago();

        lvItems = (ListView) findViewById(R.id.lvItems);
        arrayentidad = GetArrayItems();
        adaptador = new Adaptador(this, arrayentidad);
        lvItems.setAdapter(adaptador);

        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                //Obtiene valor del numero de copias!
                TextView numcopias = (TextView) view.findViewById(R.id.tvCopias);
                String copias = numcopias.getText().toString();

                //Toast.makeText(getApplicationContext(), "posicion  " +  (position + 1), Toast.LENGTH_SHORT).show();
                Entidad e =  arrayentidad.get(position);
                String PagoS = e.getContenido();


                //Toast.makeText(getApplicationContext(), "Pago  " +  PagoS, Toast.LENGTH_SHORT).show();

                //se genera el encabezado para el ticket
                obtenerEncabezado(copias);
                //Funcion para obtener los datos del ticker
                obtenerdatosticket(PagoS, copias);

            }
        });
    }

    private ArrayList<Entidad> GetArrayItems()
    {
        //ArrayList<Entidad> listItems = new ArrayList<>();
        //String Titulo = "EFECTIVO";
        //String Contenido =  "1";
        //listItems.add(new Entidad(R.drawable.billete, Titulo,  Contenido ));
        //listItems.add(new Entidad(R.drawable.vale , "VALES", "2"  ));
        //listItems.add(new Entidad(R.drawable.amex, "AMERICAN EXPRESS", "3"  ));
//        listItems.add(new Entidad(R.drawable.gascard, "GAS CARD AMEX", "4"  ));
//        listItems.add(new Entidad(R.drawable.visa, "VISA MASTERCARD", "5" ));
//        listItems.add(new Entidad(R.drawable.valeelectronico, "VALE ELECTRONICO", "6" ));
//        listItems.add(new Entidad(R.drawable.corpogas, "CREDITO ES", "7" ));
//        listItems.add(new Entidad(R.drawable.corpomobil, "CORPOMOBILE ", "8" ));
        return listItems;
    };



    //funcion para obtener formas de pago
    public void obtenerformasdepago(){

        //Solicitud de las formas de Pago/GetAll
        JsonArrayRequest jsonArrayRequest=new JsonArrayRequest(Request.Method.GET,"http://10.0.1.20/TransferenciaDatosAPI/api/FormasPago/GetAll",null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try{

                    //ArrayList<Entidad> listItems = new ArrayList<>();
                    // Recorre los elementos de la matriz del json
                    for(int i=0;i<response.length();i++){
                        // Obtiene el objeto json actual
                        JSONObject student = response.getJSONObject(i);

                        // Get the current student (json object) data
                        String numero_pago = student.getString("IdFormaPago");
                        String nombre_pago = student.getString("DescLarga");
                        String numero_ticket = student.getString("NumCopias");


                        String NPIzquierda = nombre_pago.substring(0,1).toUpperCase();
                        String NPDerecha = nombre_pago.substring(1, nombre_pago.length()).toLowerCase();

                        nombre_pago = NPIzquierda + NPDerecha;



                        switch(numero_pago)
                        {
                            case "1":
                            {
                                listItems.add(new Entidad(R.drawable.billete, nombre_pago,  numero_pago, numero_ticket));
                                break;
                            }
                            case "2":{
                                listItems.add(new Entidad(R.drawable.vale, nombre_pago,  numero_pago, numero_ticket));
                                break;
                            }

                            case "3":{
                                listItems.add(new Entidad(R.drawable.amex, nombre_pago,  numero_pago, numero_ticket));
                                break;
                            }
                            case "4":{
                                listItems.add(new Entidad(R.drawable.gascard, nombre_pago,  numero_pago, numero_ticket));
                                break;
                            }
                            case "5":
                            {
                                listItems.add(new Entidad(R.drawable.visa, nombre_pago,  numero_pago, numero_ticket));
                                break;
                            }
                            case "6":{
                                listItems.add(new Entidad(R.drawable.valeelectronico, nombre_pago,  numero_pago, numero_ticket));
                                break;
                            }

                            case "7":{
                                listItems.add(new Entidad(R.drawable.corpogas, nombre_pago,  numero_pago, numero_ticket));
                                break;
                            }
                            case "10":{
                                listItems.add(new Entidad(R.drawable.corpomobil, nombre_pago,  numero_pago, numero_ticket));
                                break;
                            }
                            default:{
                                listItems.add(new Entidad(R.drawable.camera, nombre_pago,  numero_pago, numero_ticket));
                                break;
                            }
                        }
                    }

                }catch (JSONException e){
                    //herramienta  para diagnostico de excepciones
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //descripcion del error
                Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_SHORT).show();

            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(jsonArrayRequest);
    }

    //Funcion para obtener los datos del ticket
    public void obtenerdatosticket(final String PagoSeleccionado, final String ncopias){
        String url = "http://10.0.1.20/TransferenciaDatosAPI/api/tickets/getticket";
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
                            //args.putString("mensaje",mensaje);

//                            PrintFragment newFragment = new PrintFragment();
//
//                            newFragment.setArguments(args);
//
//
//                            FragmentManager fm = getFragmentManager();


//                            PrintFragment cf = new PrintFragment();
//                            cf.setArguments(args);
//                            getFragmentManager().beginTransaction().replace(R.id.tv1, cf).
//                                    addToBackStack(PrintFragment.class.getName()).
//                                    commit();





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
                carga = getIntent().getExtras().getString("car");
                nousuario = getIntent().getExtras().getString("user");
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

    //Funcion para encabezado del Ticket
    public void obtenerEncabezado(final String ncopias){
        //Utilizamos el metodo Get para obtener el encabezado para los tickets
        String url = "http://10.0.1.20/TransferenciaDatosAPI/api/tickets/getheader";
        //Se solicita peticion GET para obtener el encabezado
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    //se instancia la respuesta del JSON
                    JSONObject encabezado = new JSONObject(response);
                    // si la respuesta del json contine informacion
                    if (encabezado != null){
                        //Asignacion a variables para encabezado

                        String idestacion = encabezado.getString("IdEstacionInt");
                        String nombre = encabezado.getString("Nombre");
                        String rfc = encabezado.getString("RFC");
                        String siic = encabezado.getString("SIIC");
                        String regimen = encabezado.getString("Regimen");

                        JSONObject direccion = encabezado.getJSONObject("direccion");
                        String calle = direccion.getString("Calle");
                        String exterior = direccion.getString("NoExterior");
                        String colonia = direccion.getString("Colonia");
                        String localidad = direccion.getString("Localidad");
                        String municipio = direccion.getString("Municipio");
                        String estado = direccion.getString("Estado");
                        String cp = direccion.getString("CodigoPostal");
                        String pais = direccion.getString("Pais");



                        args.putString("numcopias",ncopias);
                        args.putString("noestacion", idestacion);
                        args.putString("nombreestacion", nombre);
                        args.putString("razonsocial", rfc);
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


//                        PrintFragment newFragment = new PrintFragment();
//                        newFragment.setArguments(args);
//
//                        FragmentManager fm = getFragmentManager();
//                        FragmentTransaction fragmentTransaction = fm.beginTransaction();
//                        fragmentTransaction.replace(R.id.tv1, newFragment); //donde fragmentContainer_id es el ID del FrameLayout donde tu Fragment está contenido.
//                        fragmentTransaction.commit();

                        //Se instancia el PrintFragment

                            PrintFragment cf = new PrintFragment();
                            cf.setArguments(args);
                            getFragmentManager().beginTransaction().replace(R.id.tv1, cf).
                                    addToBackStack(PrintFragment.class.getName()).
                                    commit();


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
}

































