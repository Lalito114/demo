package com.szzcs.smartpos;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.szzcs.smartpos.utils.Kits;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class formas_de_pago extends AppCompatActivity {

    TextView nose;
    String carga;
    String nousuario;

    EditText pago;
    Bundle args = new Bundle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formas_de_pago);
        nose = findViewById(R.id.nose);
        obtenerformasdepago();

    }

    private void obtenerformasdepago(){

        Button enviar = (Button) findViewById(R.id.enviar);
        enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                obtenerEncabezado();
                obtenerdatosticket();


            }
        });

        JsonArrayRequest jsonArrayRequest=new JsonArrayRequest(Request.Method.GET,"http://10.0.1.20/TransferenciaDatosAPI/api/FormasPago/GetAll",null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try{
                    // Loop through the array elements
                    for(int i=0;i<response.length();i++){
                        // Get current json object
                        JSONObject student = response.getJSONObject(i);

                        // Get the current student (json object) data
                        String numero_pago = student.getString("IdFormaPago");
                        String nombre_pago = student.getString("DescLarga");

                        // Display the formatted json data in text view

                        TextView numero = (TextView)findViewById(R.id.numero);
                        numero.append(numero_pago);
                        numero.append("\n\n");

                        TextView nombre = (TextView)findViewById(R.id.nombre);
                        nombre.append(nombre_pago);
                        nombre.append("\n\n");
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_SHORT).show();

            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(jsonArrayRequest);

    }

    public void obtenerdatosticket(){
        String url = "http://10.0.1.20/TransferenciaDatosAPI/api/tickets/getticket";

        StringRequest eventoReq = new StringRequest(Request.Method.POST,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
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

                                protic += "   "+value + "    " + num + "     " + descripcion + "  " + prec + "  " + importe+"\n";
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

                            PrintFragment newFragment = new PrintFragment();
                            newFragment.setArguments(args);

                            FragmentManager fm = getFragmentManager();





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
                carga = getIntent().getExtras().getString("car");
                nousuario = getIntent().getExtras().getString("user");
                pago = findViewById(R.id.pago);
                String formapago = pago.getText().toString();
                if (formapago.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Ingresa la forma de pago",Toast.LENGTH_SHORT).show();
                }else{
                    args.putString("formadepago", formapago);
                    params.put("IdFormaPago", formapago);
                    params.put("Id_Usuario",nousuario);
                    args.putString("idusuario", nousuario);
                    params.put("PosCarga",carga);
                }
                return params;
            }
        };

        // Añade la peticion a la cola
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(eventoReq);

        //-------------------------------------------------------------------------------

    }


    public void obtenerEncabezado(){
        String url = "http://10.0.1.20/TransferenciaDatosAPI/api/tickets/getheader";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject encabezado = new JSONObject(response);
                    if (encabezado != null){
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

                        PrintFragment newFragment = new PrintFragment();
                        newFragment.setArguments(args);

                        FragmentManager fm = getFragmentManager();
                        FragmentTransaction fragmentTransaction = fm.beginTransaction();
                        fragmentTransaction.replace(R.id.tv1, newFragment); //donde fragmentContainer_id es el ID del FrameLayout donde tu Fragment está contenido.
                        fragmentTransaction.commit();

                    }else{
                        Toast.makeText(getApplicationContext(),"No se obtuvo un venta", Toast.LENGTH_LONG).show();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
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

































