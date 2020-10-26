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
import com.szzcs.smartpos.Munu_Principal;
import com.szzcs.smartpos.Productos.ListAdapterProd;
import com.szzcs.smartpos.Productos.claveProducto;
import com.szzcs.smartpos.R;
import com.szzcs.smartpos.configuracion.SQLiteBD;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class posicionFinaliza extends AppCompatActivity {
    ListView list;
    String carga, usuarioId;
    String EstacionId, sucursalId, ipEstacion, numeroTarjetero ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posicion_finaliza);
        SQLiteBD db = new SQLiteBD(getApplicationContext());
        EstacionId = db.getIdEstacion();
        sucursalId=db.getIdSucursal();
        ipEstacion = db.getIpEstacion();
        posicionCargaFinaliza();
    }

    //Proceso para cargar el listView con las posiciones de carga
    public void posicionCargaFinaliza(){
        //Declaramos direccion URL de las posiciones de carga. Para acceder a los metodos de la API
        String url = "http://"+ipEstacion+"/CorpogasService/api/posicionCargas/estacion/"+EstacionId+"/maximo";
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

    //Procedimiento que carga las posiciones de carga
    private void vax(final String response) {
        //declaración de contenido del listview
        List<String> maintitle;
        maintitle = new ArrayList<String>();
        List<String> subtitle;
        subtitle = new ArrayList<String>();
        List<Integer> imgid;
        imgid = new ArrayList<>();

        //ciclo para cargar las posiciones de carga
        for (int i = 1; i <= Integer.parseInt(response); i++) {
            maintitle.add("PC" + String.valueOf(i));
            subtitle.add("Magna |  Premium  |  Diesel");
            imgid.add(R.drawable.gas);
        }

        //Inicializacion del listview con el adaptador
        ListAdapterProd adapterProd = new ListAdapterProd(this, maintitle, subtitle, imgid);
        list=(ListView)findViewById(R.id.list);
        list.setAdapter(adapterProd);
        //Definición del Evento Click del listview
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // declaracion de variables, posicion seleccionada en el listview
                int posicion = position +1;
                String posi = String.valueOf(posicion);
                //Se llama la clase para la clave del usuario
                validaPosicionDisponible(posi);
            }
        });
    }

    private void validaPosicionDisponible(final String posicioncarga){
        // URL para obtener los empleados  y huellas de la posición de carga X
        String url = "http://"+ipEstacion+"/CorpogasService/api/tickets/validaPendienteCobro/estacionId/"+EstacionId+"/posicionCargaId/"+posicioncarga;
        // Utilizamos el metodo Post para validar la contraseña
        StringRequest eventoReq = new StringRequest(Request.Method.GET,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject p1 = new JSONObject(response);

                            String correcto = p1.getString("ObjetoRespuesta");
                            if (correcto.equals("true")){
                                Intent intente = new Intent(getApplicationContext(), claveFinalizaVenta.class);
                                //se envia el id seleccionado a la clase Usuario Producto
                                intente.putExtra("posicion",posicioncarga);
                                //Ejecuta la clase del Usuario producto
                                startActivity(intente);
                            }else{
                                //Despacho en proceso
                                try {
                                    String mensaje = p1.getString("Mensaje");
                                    AlertDialog.Builder builder = new AlertDialog.Builder(posicionFinaliza.this);
                                    builder.setTitle("Finaliza Venta");
                                    builder.setMessage("Despacho en proceso: "+ mensaje )
                                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    Intent intente = new Intent(getApplicationContext(), Munu_Principal.class);
                                                    startActivity(intente);
                                                    finish();
                                                }
                                            }).show();
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                                //Toast.makeText(getApplicationContext(),response,Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            //herramienta  para diagnostico de excepciones
                            //e.printStackTrace();
                            Toast.makeText(getApplicationContext(),"error al obtener la validación posición disponible",Toast.LENGTH_SHORT).show();
                        }
                    }
                    //funcion para capturar errores
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_SHORT).show();
                //VolleyLog.e("Error: ", volleyError.getMessage());
                String algo = new String(error.networkResponse.data) ;
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
                    }catch (Exception e){
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



}