//package com.szzcs.smartpos.Productos;
//
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.support.v7.app.AlertDialog;
//import android.support.v7.app.AppCompatActivity;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.AdapterView;
//import android.widget.EditText;
//import android.widget.ListView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.android.volley.AuthFailureError;
//import com.android.volley.Request;
//import com.android.volley.RequestQueue;
//import com.android.volley.Response;
//import com.android.volley.VolleyError;
//import com.android.volley.toolbox.JsonArrayRequest;
//import com.android.volley.toolbox.StringRequest;
//import com.android.volley.toolbox.Volley;
//import com.google.gson.Gson;
//import com.szzcs.smartpos.Munu_Principal;
//import com.szzcs.smartpos.PrintFragment;
//import com.szzcs.smartpos.R;
//import com.szzcs.smartpos.Ticket.Adaptador;
//import com.szzcs.smartpos.Ticket.Entidad;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Map;
//
//public class formaPago extends AppCompatActivity {
//    //Definicion de variables
//    private ListView lvItems;
//    private Adaptador adaptador;
//    private ArrayList<Entidad> arrayentidad;
//
//    TextView nose;
//    String carga;
//    String nousuario;
//
//    EditText pago;
//    Bundle args = new Bundle();
//
//    private Entidad Item;
//    private TextView tvDescripcion;
//    private String tvIdent;
//
//    ArrayList<Entidad> listItems = new ArrayList<>();
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_forma_pago);
//        //instruccion para que aparezca la flecha de regreso
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//
//        //inicializa listview
//        nose = findViewById(R.id.nose);
//        lvItems = (ListView) findViewById(R.id.lvItems);
//        arrayentidad = GetArrayItems();
//        //se realiza el adaptador para el listview
//        adaptador = new Adaptador(this, arrayentidad);
//        lvItems.setAdapter(adaptador);
//        //Se obtienen formas de pago para desplegar en el listview
//        obtenerformasdepago();
//
//        //Evento clic en el listview
//        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
//                //Obtiene valor del numero de copias!
//                TextView numcopias = (TextView) view.findViewById(R.id.tvCopias);
//                String copias = numcopias.getText().toString();
//
//                //Se asigna el valor del elelmento seleccionado Forma de Pago a la variable PagoS
//                Entidad e =  arrayentidad.get(position);
//                String PagoS = e.getContenido();
//                //Se envian datos
//                EnviarDatos(PagoS, copias);
//            }
//        });
//    }
//
//    private ArrayList<Entidad> GetArrayItems()
//    {
//        return listItems;
//    };
//
//    //funcion para obtener formas de pago
//    public void obtenerformasdepago(){
//
//        //Solicitud de las formas de Pago/GetAll
//        JsonArrayRequest jsonArrayRequest=new JsonArrayRequest(Request.Method.GET,"http://10.0.1.20/TransferenciaDatosAPI/api/FormasPago/GetAll",null, new Response.Listener<JSONArray>() {
//            @Override
//            public void onResponse(JSONArray response) {
//                try{
//
//                    //ArrayList<Entidad> listItems = new ArrayList<>();
//                    // Recorre los elementos de la matriz del json
//                    for(int i=0;i<response.length();i++){
//                        // Obtiene el objeto json actual
//                        JSONObject student = response.getJSONObject(i);
//
//                        // Get the current student (json object) data
//                        String numero_pago = student.getString("IdFormaPago");
//                        String nombre_pago = student.getString("DescLarga");
//                        String numero_ticket = student.getString("NumCopias");
//
//
//                        String NPIzquierda = nombre_pago.substring(0,1).toUpperCase();
//                        String NPDerecha = nombre_pago.substring(1, nombre_pago.length()).toLowerCase();
//
//                        nombre_pago = NPIzquierda + NPDerecha;
//
//
//
//                        switch(numero_pago)
//                        {
//                            case "1":
//                            {
//                                listItems.add(new Entidad(R.drawable.billete, nombre_pago,  numero_pago, numero_ticket));
//                                break;
//                            }
//                            case "2":{
//                                listItems.add(new Entidad(R.drawable.vale, nombre_pago,  numero_pago, numero_ticket));
//                                break;
//                            }
//
//                            case "3":{
//                                listItems.add(new Entidad(R.drawable.amex, nombre_pago,  numero_pago, numero_ticket));
//                                break;
//                            }
//                            case "4":{
//                                listItems.add(new Entidad(R.drawable.gascard, nombre_pago,  numero_pago, numero_ticket));
//                                break;
//                            }
//                            case "5":
//                            {
//                                listItems.add(new Entidad(R.drawable.visa, nombre_pago,  numero_pago, numero_ticket));
//                                break;
//                            }
//                            case "6":{
//                                listItems.add(new Entidad(R.drawable.valeelectronico, nombre_pago,  numero_pago, numero_ticket));
//                                break;
//                            }
//
//                            case "7":{
//                                listItems.add(new Entidad(R.drawable.corpogas, nombre_pago,  numero_pago, numero_ticket));
//                                break;
//                            }
//                            case "10":{
//                                listItems.add(new Entidad(R.drawable.corpomobil, nombre_pago,  numero_pago, numero_ticket));
//                                break;
//                            }
//                            default:{
//                                listItems.add(new Entidad(R.drawable.camera, nombre_pago,  numero_pago, numero_ticket));
//                                break;
//                            }
//                        }
//                    }
//
//                }catch (JSONException e){
//                    //herramienta  para diagnostico de excepciones
//                    e.printStackTrace();
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                //descripcion del error
//                Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_SHORT).show();
//
//            }
//        });
//        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
//        requestQueue.add(jsonArrayRequest);
//    }
//
//    //Funcion para obtener los datos del ticket
//    public void obtenerdatosticket(final String PagoSeleccionado, final String ncopias){
//        String url = "http://10.0.1.20/TransferenciaDatosAPI/api/tickets/getticket";
//        //Utilizamos el metodo Post para colocar los datos en el  ticket
//        StringRequest eventoReq = new StringRequest(Request.Method.POST,url,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        try {
//                            //Se instancia la respuesta del json
//                            JSONObject ticket = new JSONObject(response);
//                            String body1 = ticket.getString("body");
//
//                            String footer = ticket.getString("footer");
//                            JSONObject footer1 = new JSONObject(footer);
//                            String mensaje = footer1.getString("Mensaje");
//
//
//                            JSONObject partebody = new JSONObject(body1);
//                            String recibo = partebody.getString("NoRecibo");
//                            String transaccion = partebody.getString("NoTransaccion");
//                            String rastreo = partebody.getString("NoRastreo");
//                            String posicion = partebody.getString("PosCarga");
//                            String desp = partebody.getString("Desp");
//                            String vendedor = partebody.getString("Vend");
//                            //String mensaje = partebody.getString("footer");
//
//                            String subtotal = partebody.getString("Subtotal");
//                            String iva = partebody.getString("IVA");
//                            String total = partebody.getString("Total");
//                            String totaltexto = partebody.getString("TotalTexto");
//
//                            JSONObject person = new JSONObject(response);
//
//                            String name = person.getString("body");
//
//                            JSONObject product = new JSONObject(name);
//
//                            JSONArray producto = product.getJSONArray("producto");
//
//                            String cantidad = new String();
//                            String protic = new String();
//                            String numero = new String();
//
//                            String descrip = new String();
//                            String impor = new String();
//                            String precio = new String();
//
//                            for (int i = 0; i <producto.length() ; i++) {
//                                JSONObject p1 = producto.getJSONObject(i);
//                                String value = p1.getString("Cantidad");
//                                cantidad +=  value;
//
//                                String num = p1.getString("No");
//                                numero +=num ;
//
//                                String descripcion = p1.getString("Descripcion");
//                                descrip += descripcion;
//
//                                String importe = p1.getString("Importe");
//                                impor +=importe;
//
//                                String prec = p1.getString("Precio");
//                                precio += prec;
//
//                                protic += "   "+ value + "    " + num + "     " + descripcion + "  " + prec + "  " + importe+"\n";
//                            }
//
//
//
//                            args.putString("norecibo", recibo);
//                            args.putString("norastreo", rastreo);
//                            args.putString("posicion", posicion);
//                            args.putString("cantidad", protic);
//                            args.putString("numero", numero);
//                            args.putString("descrip",descrip);
//                            args.putString("impor",impor);
//                            args.putString("precio",precio);
//
//                            args.putString("subtotal",subtotal);
//                            args.putString("iva",iva);
//                            args.putString("total",total);
//                            args.putString("totaltexto",totaltexto);
//                            args.putString("mensaje",mensaje);
//                            //args.putString("mensaje",mensaje);
//
////                            PrintFragment newFragment = new PrintFragment();
////
////                            newFragment.setArguments(args);
////
////
////                            FragmentManager fm = getFragmentManager();
//
//
////                            PrintFragment cf = new PrintFragment();
////                            cf.setArguments(args);
////                            getFragmentManager().beginTransaction().replace(R.id.tv1, cf).
////                                    addToBackStack(PrintFragment.class.getName()).
////                                    commit();
//
//
//
//
//
//                        } catch (JSONException e) {
//                            //herramienta  para diagnostico de excepciones
//                            e.printStackTrace();
//                        }
//                    }
//                    //funcion para capturar errores
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_SHORT).show();
//            }
//        }){
//            @Override
//            protected Map<String, String> getParams() {
//                // Posting parameters to login url
//                Map<String, String> params = new HashMap<String, String>();
//                carga = getIntent().getExtras().getString("car");
//                nousuario = getIntent().getExtras().getString("user");
//                //pago = findViewById(R.id.pago);
//                String formapago = PagoSeleccionado; //pago.getText().toString();
//                // Toast.makeText(getApplicationContext(), formapago, Toast.LENGTH_SHORT).show();
//
//
//                args.putString("numcopias", ncopias);
//                args.putString("formadepago", PagoSeleccionado);
//                params.put("IdFormaPago", formapago);
//                params.put("Id_Usuario",nousuario);
//                args.putString("idusuario", nousuario);
//                params.put("PosCarga",carga);
//
//                return params;
//            }
//        };
//
//        // Añade la peticion a la cola
//        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
//        requestQueue.add(eventoReq);
//
//        //-------------------------------------------------------------------------------
//
//    }
//
//    //Funcion para encabezado del Ticket
//    public void obtenerEncabezado(final String ncopias){
//        //Utilizamos el metodo Get para obtener el encabezado para los tickets
//        String url = "http://10.0.1.20/TransferenciaDatosAPI/api/tickets/getheader";
//        //Se solicita peticion GET para obtener el encabezado
//        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                try {
//                    //se instancia la respuesta del JSON
//                    JSONObject encabezado = new JSONObject(response);
//                    // si la respuesta del json contine informacion
//                    if (encabezado != null){
//                        //Asignacion a variables para encabezado
//
//                        String idestacion = encabezado.getString("IdEstacionInt");
//                        String nombre = encabezado.getString("Nombre");
//                        String rfc = encabezado.getString("RFC");
//                        String siic = encabezado.getString("SIIC");
//                        String regimen = encabezado.getString("Regimen");
//
//                        JSONObject direccion = encabezado.getJSONObject("direccion");
//                        String calle = direccion.getString("Calle");
//                        String exterior = direccion.getString("NoExterior");
//                        String colonia = direccion.getString("Colonia");
//                        String localidad = direccion.getString("Localidad");
//                        String municipio = direccion.getString("Municipio");
//                        String estado = direccion.getString("Estado");
//                        String cp = direccion.getString("CodigoPostal");
//                        String pais = direccion.getString("Pais");
//
//
//
//                        args.putString("numcopias",ncopias);
//                        args.putString("noestacion", idestacion);
//                        args.putString("nombreestacion", nombre);
//                        args.putString("razonsocial", rfc);
//                        args.putString("datos",siic);
//                        args.putString("regimenfiscal",regimen);
//                        args.putString("calle",calle);
//                        args.putString("exterior",exterior);
//                        args.putString("colonia",colonia);
//                        args.putString("localidad",localidad);
//                        args.putString("municipio",municipio);
//                        args.putString("estado",estado);
//                        args.putString("cp",cp);
//                        args.putString("pais",pais);
//
//
////                        PrintFragment newFragment = new PrintFragment();
////                        newFragment.setArguments(args);
////
////                        FragmentManager fm = getFragmentManager();
////                        FragmentTransaction fragmentTransaction = fm.beginTransaction();
////                        fragmentTransaction.replace(R.id.tv1, newFragment); //donde fragmentContainer_id es el ID del FrameLayout donde tu Fragment está contenido.
////                        fragmentTransaction.commit();
//
//                        //Se instancia el PrintFragment
//
//                        PrintFragment cf = new PrintFragment();
//                        cf.setArguments(args);
//                        getFragmentManager().beginTransaction().replace(R.id.tv1, cf).
//                                addToBackStack(PrintFragment.class.getName()).
//                                commit();
//
//
//                    }else{
//                        //Si el json no contiene informacion se envia mensahje
//                        Toast.makeText(getApplicationContext(),"No se obtuvo un venta", Toast.LENGTH_LONG).show();
//                    }
//
//
//                } catch (JSONException e) {
//                    //herramienta  para diagnostico de excepciones
//                    e.printStackTrace();
//                }
//            }
//            //funcion para capturar errores
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(getApplicationContext(),error.toString(), Toast.LENGTH_LONG).show();
//            }
//        }){
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String,String> parametros = new HashMap<String, String>();
//                return parametros;
//            }
//        };
//        RequestQueue requestQueue = Volley.newRequestQueue(this.getApplicationContext());
//        requestQueue.add(stringRequest);
//
//    }
//
//    private void EnviarDatos(final String FormaPago, final String Copias){
//        //Se inicializa el control para solicitar confirmacion
//        AlertDialog.Builder builder;
//        //Obtengo la posicion de carga que se pasa como parametro
//        //String Posi = Posicion;
//        //Enviar datos de peoductos y posicion de carga para regresar Ticket
//        EnviarTransaccion();
//        builder = new AlertDialog.Builder(this);
//
//        builder.setMessage("Desea imprimir el ticket?");
//        builder.setTitle("Venta de Productos");
//        builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                //se genera el encabezado para el ticket
//                obtenerEncabezado(Copias);
//                //Funcion para obtener los datos del ticker
//                obtenerdatosticket(FormaPago, Copias);
//                Intent intent = new Intent(getApplicationContext(), Munu_Principal.class);
//                startActivity(intent);
//            }
//        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                //EnviaVenta;
//                dialogInterface.cancel();
//                Intent intent = new Intent(getApplicationContext(), Munu_Principal.class);
//                startActivity(intent);
//
//            }
//        });
//        AlertDialog dialog= builder.create();
//        dialog.show();
//    }
//
//    private void EnviarTransaccion(){
//        //Se obtienen los datos de las variables posicion de carga y usuario
//        final String posicion;
//        posicion = getIntent().getStringExtra("posicion");
//        final String usuarioid;
//        usuarioid = getIntent().getStringExtra("usuario");
//        //se inicializa nuevo objeto gson
//        Gson gson = new Gson();
//        //se recibe el objeto json con los productos a vender
//        final JSONObject mjason = gson.fromJson(getIntent().getStringExtra("myjson"),  JSONObject.class);
//        //Cambiarlo a arreglo para poder enviar el json
//
//
//        //Utilizamos el metodo POST para  enviar la transaccion y regrese el numero de ticket
//        String url = "http://10.0.1.20/TransferenciaDatosAPI/api/tarjetas/sendtarjeta";
//
//        StringRequest eventoReq = new StringRequest(Request.Method.POST,url,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        Toast.makeText(getApplicationContext(),response,Toast.LENGTH_LONG).show();
//                        try {
//                            JSONObject jsonObject = new JSONObject(response);
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_SHORT).show();
//            }
//        }){
//            @Override
//            protected Map<String, String> getParams() {
//                // Colocar parametros para ingresar la  url
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("PosCarga",posicion);
//                params.put("Productos",mjason.toString());
//                return params;
//            }
//        };
//
//        // Añade la peticion a la cola
//        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
//        requestQueue.add(eventoReq);
//    }
//}