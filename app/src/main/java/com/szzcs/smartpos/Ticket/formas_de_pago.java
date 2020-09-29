package com.szzcs.smartpos.Ticket;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AlertDialog;
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
import com.szzcs.smartpos.configuracion.SQLiteBD;
import com.szzcs.smartpos.configuracion.tipoempresa;
import com.szzcs.smartpos.utils.Kits;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//Clase para desplegar Formas de Pago
public class formas_de_pago extends AppCompatActivity {
    private ListView list;
    private Adaptador adaptador;
    private ArrayList<Entidad> arrayentidad;

    //Definicion de variables
    TextView nose;
    String carga;
    String nousuario;

    EditText pago;
    String formapago, nombrepago, numticket;
    Bundle args = new Bundle();




    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_formas_de_pago);
        SQLiteBD data = new SQLiteBD(getApplicationContext());
        this.setTitle(data.getNombreEsatcion());
        obtenerformasdepago();

    }

    //funcion para obtener formas de pago
    public void obtenerformasdepago(){
        SQLiteBD data = new SQLiteBD(getApplicationContext());
        String url = "http://"+data.getIpEstacion()+"/CorpogasService/api/sucursalformapagos/sucursal/"+data.getIdEstacion();
        StringRequest eventoReq = new StringRequest(Request.Method.GET,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        formadepago(response);
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

    private void formadepago(String response) {
        //Declaramos la lista de titulo
        final List<String> maintitle;
        //lo assignamos a un nuevo ArrayList
        maintitle = new ArrayList<String>();

        //Creamos la lista para los subtitulos
        List<String> subtitle;
        //Lo asignamos a un nuevo ArrayList
        subtitle = new ArrayList<String>();

        //CReamos una nueva list de tipo Integer con la cual cargaremos a una imagen
        List<Integer> imgid;
        //La asignamos a un nuevo elemento de ArrayList
        imgid = new ArrayList<>();

        final List<String> numerotickets;
        //Lo asignamos a un nuevo ArrayList
        numerotickets = new ArrayList<String>();

        try {
            //JSONObject jsonObject = new JSONObject(response);
            //String formapago = jsonObject.getString("SucursalFormapagos");
            JSONArray nodo = new JSONArray(response);
            for (int i = 0; i <=nodo.length() ; i++) {

                JSONObject nodo1 = nodo.getJSONObject(i);
                String numero_pago = nodo1.getString("FormaPagoId");
                String formapago1 = nodo1.getString("FormaPago");
                JSONObject nodo2 = new JSONObject(formapago1);
                String nombre_pago = nodo2.getString("DescripcionLarga");
                String numero_ticket = nodo2.getString("NumeroTickets");
                String visible = nodo2.getString("VisibleTarjetero");
                if (visible == "true"){
                    numerotickets.add(numero_ticket);
                    maintitle.add( nombre_pago);
                    subtitle.add("ID Forma de Pago:" + numero_pago);
                }


                    int idpago = Integer.parseInt(numero_pago);
                switch (idpago){
                    case 1:
                        imgid.add(R.drawable.monedero);
                        break;
                    case 2:
                        imgid.add(R.drawable.billete);
                        break;
                    case 3:
                        imgid.add(R.drawable.vale);
                        break;
                    case 4:
                        imgid.add(R.drawable.american);
                        break;
                    case 5:
                        imgid.add(R.drawable.gascard);
                        break;
                    case  6:
                        imgid.add(R.drawable.visa);
                        break;
                    case 7:
                        imgid.add(R.drawable.valeelectronico);
                        break;
                    case 8:
                        imgid.add(R.drawable.credito);
                        break;
                    case 9:
                        imgid.add(R.drawable.corpomobil);
                        break;
                    case 10:
                        imgid.add(R.drawable.monedero);
                        break;
                    case 11:
                        imgid.add(R.drawable.jarreo);
                        break;
                    case 12:
                        imgid.add(R.drawable.monedero);
                     break;
                     default:
                         imgid.add(R.drawable.monedero);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Hacemos el ciclo para que cuente las posiciones de carga en las cuales se van a ver dibijadas

        //Invocamos a la clase de listadapter para crear la vista sobre el layout
        Adaptador adapter=new Adaptador(this, maintitle, subtitle,imgid);
        list=(ListView)findViewById(R.id.list);
        list.setAdapter(adapter);

        //Optenemos el numero del Item seleccionado que corresponde a al numero de posicion de carga
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                int pos = position + 1;

                formapago = String.valueOf(pos);
                nombrepago = maintitle.get(position);

                numticket = numerotickets.get(position);
                ObtenerCuerpoTicket(nombrepago,numticket);


            }
        });
    }

    public void ObtenerCuerpoTicket(final String nombrepago, final String numticket) {
        final SQLiteBD data = new SQLiteBD(getApplicationContext());
        String url = "http://"+data.getIpEstacion()+"/CorpogasService/api/tickets/generar";

        StringRequest eventoReq = new StringRequest(Request.Method.POST,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String detalle = jsonObject.getString("Detalle");
                            String pie = jsonObject.getString("Pie");
                            JSONObject mensaje = new JSONObject(pie);
                            final JSONArray names = mensaje.getJSONArray("Mensaje");

                            JSONObject det = new JSONObject(detalle);
                            final String numerorecibo = det.getString("NoRecibo");
                            final String numerotransaccion = det.getString("NoTransaccion");
                            final String numerorastreo = det.getString("NoRastreo");
                            final String poscarga = det.getString("PosCarga");
                            final String despachador = det.getString("Desp");
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

                            final String subtotal = det.getString("Subtotal");
                            final String iva = det.getString("IVA");
                            final String total = det.getString("Total");
                            final String totaltexto = det.getString("TotalTexto");
                            String clave = det.getString("Clave");
                            try {
                                AlertDialog.Builder builder;

                                builder = new AlertDialog.Builder(formas_de_pago.this);
                                builder.setMessage("Desea imprimir el ticket?");
                                builder.setTitle("Venta de Productos");
                                final String finalProtic = protic;
                                builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
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
                                        args.putString("productos", finalProtic);

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

                                    }
                                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        //EnviaVenta;
                                        dialogInterface.cancel();
                                        //Utilizamos el metodo POST para  finalizar la Venta
                                        SQLiteBD data = new SQLiteBD(formas_de_pago.this);
                                        String url = "http://"+data.getIpEstacion()+"/CorpogasService/api/Transacciones/finalizaVenta/sucursal/"+data.getIdEstacion()+"/posicionCarga/"+poscarga;
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
                            }catch (Exception e){
                                e.printStackTrace();
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
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                String carga = getIntent().getStringExtra("car");
                String user = getIntent().getStringExtra("user");
                params.put("PosCarga", carga);
                params.put("IdUsuario",user);
                params.put("IdFormaPago", formapago);
                params.put("SucursalId",data.getIdEstacion());
                return params;
            }
        };
        // Añade la peticion a la cola
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(eventoReq);
    }


}

































