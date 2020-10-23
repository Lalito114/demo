package com.szzcs.smartpos.Ticket;


import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.szzcs.smartpos.R;
import com.szzcs.smartpos.configuracion.SQLiteBD;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


//Clase para validar la contraseña del empleado
public class claveUsuario extends AppCompatActivity {
    TextView usuario, carga;
    String iduser;
    Bundle args = new Bundle();
    String formapago;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clave_usuario);
        SQLiteBD data = new SQLiteBD(getApplicationContext());
        this.setTitle(data.getNombreEsatcion());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //lee valores usuario y carga
        usuario= findViewById(R.id.usuario);
        carga = findViewById(R.id.carga);

        //Crea Boton Enviar
        Button btnenviar = (Button) findViewById(R.id.btnsiguiente);
        //En espera a recibir el evento Onclick del boton Enviar
        btnenviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tipo = getIntent().getStringExtra("tipo");
                if (tipo.equals("0")){
                    enviardatos();
                }else{
                    if (tipo.equals("1") || tipo.equals("2")){
                        idUsuario();
                    }
                }


            }
        });
    }

    private void imprimirticket() {
        final SQLiteBD data = new SQLiteBD(getApplicationContext());
        String url = "http://"+data.getIpEstacion()+"/CorpogasService/api/tickets/generar";

        StringRequest eventoReq = new StringRequest(Request.Method.POST,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String detalle = jsonObject.getString("Detalle");
                            if (detalle.equals("null")){
                                try{
                                    String resul = jsonObject.getString("Resultado");
                                    JSONObject resultado = new JSONObject(resul);
                                    String descripcion = resultado.getString("Descripcion");
                                    AlertDialog.Builder builder = new AlertDialog.Builder(claveUsuario.this);
                                    builder.setTitle("Tanque Lleno");
                                    builder.setMessage(descripcion);
                                    builder.setPositiveButton("Cerrar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            Intent intent = new Intent(getApplicationContext(),Munu_Principal.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    });
                                    AlertDialog dialog= builder.create();
                                    dialog.show();
                                }catch (Exception e){
                                    e.printStackTrace();
                                }

                            }else{
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
                                final String finalProtic = protic;

                                for (int i = 0; i <producto.length() ; i++) {
                                    JSONObject p1 = producto.getJSONObject(i);
                                    String value = p1.getString("Cantidad");

                                    String descripcion = p1.getString("Descripcion");

                                    String importe = p1.getString("Importe");

                                    String prec = p1.getString("Precio");

                                    protic +=value + " | " + descripcion + " | " + prec + " | " + importe+"\n";
                                }
                                String formapago = det.getString("FormaPago");

                                final String subtotal = det.getString("Subtotal");
                                final String iva = det.getString("IVA");
                                final String total = det.getString("Total");
                                final String totaltexto = det.getString("TotalTexto");
                                String clave = det.getString("Clave");
                                String carga = getIntent().getStringExtra("car");
                                String user = getIntent().getStringExtra("user");
                                args.putString("numerorecibo", numerorecibo);
                                args.putString("nombrepago", formapago);
                                args.putString("numticket", "2");
                                args.putString("numerotransaccion", numerotransaccion);
                                args.putString("numerorastreo", numerorastreo);
                                args.putString("posicion", carga);
                                args.putString("despachador",despachador);
                                args.putString("vendedor",user);
                                args.putString("productos", protic);

                                args.putString("subtotal",subtotal);
                                args.putString("iva",iva);
                                args.putString("total",total);
                                args.putString("totaltexto",totaltexto);
                                args.putString("mensaje",names.toString());
                                String tipo = getIntent().getStringExtra("tipo");
                                args.putString("tipo",tipo);

                                PrintFragment cf = new PrintFragment();
                                cf.setArguments(args);
                                getFragmentManager().beginTransaction().replace(R.id.tv1, cf).
                                        addToBackStack(PrintFragment.class.getName()).
                                        commit();
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
                String carga = getIntent().getStringExtra("pos");
                String tipo = getIntent().getStringExtra("tipo");
                if (tipo.equals("1")){
                    formapago = "19";
                }else{
                    if (tipo.equals("2")){
                        formapago ="18";
                    }
                }
                params.put("PosCarga", carga);
                params.put("IdUsuario",iduser);
                params.put("IdFormaPago", formapago);
                params.put("SucursalId",data.getIdEstacion());
                return params;
            }
        };
        // Añade la peticion a la cola
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(eventoReq);
    }

    private void enviardatos() {
        //Se lee el password del objeto y se asigna a variable
        final String posicion;
        posicion = getIntent().getStringExtra("pos");
        EditText pasword = (EditText) findViewById(R.id.pasword);
        final String pass = pasword.getText().toString();

        //Si no se terclea nada envia mensaje de teclear contraseña
        if (pass.isEmpty()){
            Toast.makeText(getApplicationContext(),"Ingresa la contraseña",Toast.LENGTH_SHORT).show();
        }else{
            //----------------------Aqui va el Volley Si se tecleo contraseña----------------------------

            //Conexion con la base y ejecuta valida clave
            SQLiteBD data = new SQLiteBD(getApplicationContext());
            String url = "http://"+data.getIpEstacion()+"/CorpogasService/api/SucursalEmpleados/clave/"+pass;

            // Utilizamos el metodo Post para validar la contraseña
            StringRequest eventoReq = new StringRequest(Request.Method.GET,url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                //Se instancia la respuesta del json
                                JSONObject validar = new JSONObject(response);
                                String valido = validar.getString("Activo");
                                String idusuario = validar.getString("RolId");
                                if (valido == "true"){
                                    //Si es valido se asignan valores
                                    usuario.setText(idusuario);
                                    carga.setText(posicion);
                                    //Se instancia y se llama a la clase formas de pago
                                    Intent intent = new Intent(getApplicationContext(), formas_de_pago.class);
                                    intent.putExtra("car",posicion);
                                    intent.putExtra("user",idusuario);
                                    startActivity(intent);
                                    finish();
                                }else{
                                    //Si no es valido se envia mensaje
                                    Toast.makeText(getApplicationContext(),"La contraseña es incorecta",Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_SHORT).show();
                }
            });

            // Añade la peticion a la cola
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(eventoReq);
            //-------------------------Aqui termina el volley --------------
        }
    }

    public void idUsuario(){
        final String posicion;
        posicion = getIntent().getStringExtra("pos");
        EditText pasword = (EditText) findViewById(R.id.pasword);
        final String pass = pasword.getText().toString();
        final String idusuario;

        SQLiteBD data = new SQLiteBD(getApplicationContext());
        String url = "http://"+data.getIpEstacion()+"/CorpogasService/api/SucursalEmpleados/clave/"+pass;

        // Utilizamos el metodo Post para validar la contraseña
        StringRequest eventoReq = new StringRequest(Request.Method.GET,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //Se instancia la respuesta del json
                            JSONObject validar = new JSONObject(response);
                            String valido = validar.getString("Activo");
                            iduser = validar.getString("Id");
//                            obteneridusuario(idusuario);
                            if (valido == "true"){
                                imprimirticket();
                            }else{
                                //Si no es valido se envia mensaje
                                Toast.makeText(getApplicationContext(),"La contraseña es incorecta",Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_SHORT).show();
            }
        });

        // Añade la peticion a la cola
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(eventoReq);

    }


}
