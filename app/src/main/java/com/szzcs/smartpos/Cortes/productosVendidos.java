package com.szzcs.smartpos.Cortes;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.szzcs.smartpos.Munu_Principal;
import com.szzcs.smartpos.Productos.FPaga;
import com.szzcs.smartpos.Productos.VentasProductos;
import com.szzcs.smartpos.R;
import com.szzcs.smartpos.configuracion.SQLiteBD;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class productosVendidos extends AppCompatActivity {
    List<String> maintitle, subtitle, total, cantidadEntregada, cantidadVendidos;
    ListView list;
    ImageView imgAceptar;
    Boolean banderaSigue;
    String EstacionId, sucursalId, ipEstacion, islaId ;
    JSONObject mjason = new JSONObject();
    JSONArray ArrayventasPerifericos = new JSONArray();
    String posicion, usuario, numerodispositivo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productos_vendidos);
        SQLiteBD db = new SQLiteBD(getApplicationContext());
        EstacionId = db.getIdEstacion();
        sucursalId = db.getIdSucursal();
        ipEstacion= db.getIpEstacion();
        islaId = "1"; // getIntent().getStringExtra("islaId")
        usuario  = "1"; //getIntent().getStringExtra("usuarioid");
        posicion = "1"; //getIntent().getStringExtra("car");
        numerodispositivo = "1";

        list = findViewById(R.id.list);
        imgAceptar = findViewById(R.id.imgAceptar);
        imgAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Enviar a siguiente Activity
                EnviarProductosPerifericos(posicion, usuario);
            }
        });

        MostrarProductos();
    }

    private void EnviarProductosPerifericos(final String posicionCarga, final String Usuarioid) {
        String url = "http://"+ipEstacion+"/CorpogasService/api/ventaProductos/GuardaProductos/sucursal/"+sucursalId+"/origen/"+numerodispositivo+"/usuario/"+Usuarioid+"/posicionCarga/"+posicionCarga;
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest request_json = new JsonArrayRequest(Request.Method.POST, url, ArrayventasPerifericos,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        //Get Final response
                        Intent intent = new Intent(getApplicationContext(), tipoVenta.class);
                        //intent.putExtra("posicion", posicionCarga);
                        //intent.putExtra("usuario", Usuarioid);
                        startActivity(intent);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                //VolleyLog.e("Error: ", volleyError.getMessage());
                String algo = new String(volleyError.networkResponse.data) ;
                try {
                    //creamos un json Object del String algo
                    JSONObject errorCaptado = new JSONObject(algo);
                    //Obtenemos el elemento ExceptionMesage del errro enviado
                    String errorMensaje = errorCaptado.getString("ExceptionMessage");
                    try {
                        AlertDialog.Builder builder = new AlertDialog.Builder(productosVendidos.this);
                        builder.setTitle("Vemta Productos");
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

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                // Add headers
                return headers;
            }
            //Important part to convert response to JSON Array Again
            @Override
            protected Response<JSONArray> parseNetworkResponse(NetworkResponse response) {
                String responseString;
                JSONArray array = new JSONArray();
                if (response != null) {

                    try {
                        responseString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                        JSONObject obj = new JSONObject(responseString);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                //return array;
                return Response.success(ArrayventasPerifericos, HttpHeaderParser.parseCacheHeaders(response));
            }
        };
        queue.add(request_json);
    }


    private void MostrarProductos() {
        banderaSigue= true;

        String url = "http://"+ipEstacion+"/CorpogasService/api/cierres/registrar/sucursal/"+sucursalId+"/isla/"+posicion+"/usuario/"+usuario+"/origen/" + numerodispositivo;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //mostrarProductosExistencias(response, posicion, usuario);
                mostrarProductosCierre(response, posicion, usuario);
            }
        }, new Response.ErrorListener() {
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
                        AlertDialog.Builder builder = new AlertDialog.Builder(productosVendidos.this);
                        builder.setTitle("Ventas Realizadas, CORTE");
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
        RequestQueue requestQueue = Volley.newRequestQueue(this.getApplicationContext());
        requestQueue.add(stringRequest);
    }
    private void mostrarProductosCierre(String response, final String posicion, final String usuario){
        String TProductoId;
        int cantidad;
        maintitle = new ArrayList<String>();
        subtitle = new ArrayList<String>();
        total = new ArrayList<String>();

        try {
            JSONObject p1 = new JSONObject(response);
            //String ni = p1.getString("NumeroInterno");
            String objetorespuesta = p1.getString("ObjetoRespuesta");
            JSONObject ps = new JSONObject(objetorespuesta);
            String producto = ps.getString("CierreDetalles");
            JSONArray cierredetalles = new JSONArray(producto);

            for (int i = 0; i <10 ; i++){ //bodegaprod.length()
                String IdProductos = null;
                JSONObject pA = cierredetalles.getJSONObject(i);
                //String ExProductos=pA.getString("Existencias");
                String productoclave = pA.getString("ProductoDescripcion");
                //JSONObject prod = new JSONObject(productoclave);
                String DescLarga=pA.getString("ProductoDescripcion");
                TProductoId=pA.getString("CategoriaProductoId");
                String idArticulo=pA.getString("NumeroInterno");
                String codigobar=pA.getString("CodigoBarras");
                String cantidadvendida=pA.getString("Cantidad");
                String preciounitario=pA.getString("Precio");
                String cantidadrecibida=pA.getString("CantidadRecibida");
                IdProductos=pA.getString("Id");
                //String PControl=prod.getString("ProductoControles");
                //JSONArray PC = new JSONArray(PControl);

                if (TProductoId=="1" ) {
                }else{
                    maintitle.add("-");
                    subtitle.add(DescLarga);
                    total.add(cantidadrecibida);

                    mjason.put("TipoProducto", TProductoId);
                    mjason.put("ProductoId", IdProductos);
                    mjason.put("NumeroInterno", idArticulo);
                    mjason.put("ProductosEntregados", cantidadrecibida);

                    //mjasonF.put ("DescCorta", descCorta);
                    ArrayventasPerifericos.put(mjason);
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final ListAdapterBilletes adapterP = new ListAdapterBilletes(this,   maintitle, subtitle, total);
        list.setTextFilterEnabled(true);
        list.setAdapter(adapterP);
    }


}