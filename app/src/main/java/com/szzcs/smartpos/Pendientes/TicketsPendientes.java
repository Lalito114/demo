package com.szzcs.smartpos.Pendientes;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.szzcs.smartpos.PrintFragment;
import com.szzcs.smartpos.Productos.ListAdapterProductos;
import com.szzcs.smartpos.R;
import com.szzcs.smartpos.Ticket.Entidad;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TicketsPendientes extends AppCompatActivity {
    Button btnEnviar;
    TextView txtMonto, txtNoTicket;
    EditText Producto;
    Bundle args = new Bundle();
    //Definicion de variables
    TextView nose;
    String carga;
    String nousuario;
    String cantidad;
    JSONObject mjason = new JSONObject();
    ListView list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tickets_pendientes);
        //instruccion para que aparezca la flecha de regreso
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btnEnviar = findViewById(R.id.btnEnviar);
        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String PagoS = "1";
                String Copias= "2";
                EnviarDatos(PagoS, Copias);
            }
        });
        txtNoTicket =findViewById(R.id.txtNoTicket);
        txtMonto =findViewById(R.id.txtMonto);

        MostrarProductos();
    }
    private void EnviarDatos(String Ticket, String Copias) {

        obtenerEncabezado(Copias);
        obtenerdatosticket(Ticket, Copias);
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




    private void MostrarProductos() {
        String url = "http://10.0.1.20/TransferenciaDatosAPI/api/catarticulos/getall";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mostarProductor(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),error.toString(), Toast.LENGTH_LONG).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this.getApplicationContext());
        requestQueue.add(stringRequest);
    }

    private void mostarProductor(String response) {
        final List<String> ID;
        ID = new ArrayList<String>();

        final List<String> NombreProducto;
        NombreProducto = new ArrayList<String>();

        final List<String> PrecioProducto;
        PrecioProducto = new ArrayList<>();

        final List<String> ClaveProducto;
        ClaveProducto = new ArrayList();

        try {
            JSONArray productos = new JSONArray(response);
            for (int i = 0; i <productos.length() ; i++) {
                JSONObject p1 = productos.getJSONObject(i);
                String idArticulo = p1.getString("IdArticulo");
                String DesLarga = p1.getString("DescLarga");
                String precio = p1.getString("Precio");
                NombreProducto.add(" $"+precio);
                Integer Numero = i + 1;
                ID.add("Transacción No. " + Numero);
                PrecioProducto.add(precio);
                String llena =   Numero.toString();
                ClaveProducto.add(llena);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ListAdapterProductos adapterP = new ListAdapterProductos(this, ID, NombreProducto);
        list=(ListView)findViewById(R.id.list);
        list.setAdapter(adapterP);
//        Agregado Mikel
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String  monto = PrecioProducto.get(i).toString();
                //String b = NombreProducto.get(i).toString();
                String ticket= ClaveProducto.get(i).toString();
                txtMonto.setText(monto);
                txtNoTicket.setText(ticket);
            }
        });
    }

}