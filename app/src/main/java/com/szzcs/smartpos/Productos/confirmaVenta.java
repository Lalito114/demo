package com.szzcs.smartpos.Productos;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.szzcs.smartpos.FinalizaVenta.posicionFinaliza;
import com.szzcs.smartpos.Munu_Principal;
import com.szzcs.smartpos.R;
import com.szzcs.smartpos.configuracion.SQLiteBD;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class confirmaVenta extends AppCompatActivity {
    String usuario, posicion, cadenaproductos, nombreproducto;
    ListView list;
    Double MontoTotal=0.0;
    Button Cobrar, Agregar;
    List<String> ID;
    List<String> NombreProducto;
    List<String> PrecioProducto;
    List<String> ClaveProducto;
    List<String> codigoBarras;
    List<String> ExistenciaProductos;
    List<String> ProductosId;
    List<String> TipoProductoId;
    List<String> DescripcionPr;
    JSONArray myArray = new JSONArray();
    String EstacionId, sucursalId, ipEstacion, tipoTransaccion, numerodispositivo ;
    TextView txttotal;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirma_venta);

        SQLiteBD db = new SQLiteBD(getApplicationContext());
        EstacionId = db.getIdEstacion();
        sucursalId = db.getIdSucursal();
        ipEstacion= db.getIpEstacion();
        tipoTransaccion = "1"; //Transaccion Normal
        numerodispositivo = "1";
        txttotal=findViewById(R.id.txttotal);

        posicion = getIntent().getStringExtra("posicion");
        usuario = getIntent().getStringExtra("usuario");
        cadenaproductos = getIntent().getStringExtra("cadenaproducto");
        nombreproducto = getIntent().getStringExtra("Descripcion");
        Cobrar = findViewById(R.id.comprar);
        Agregar = findViewById(R.id.btnagregar);

        Cobrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ////AgregarDespacho(posicion, usuarioid);
                EnviarProductos();
            }
        });

        Agregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), VentasProductos.class);
                intent.putExtra("posicion", posicion);
                intent.putExtra("usuario", usuario);
                intent.putExtra("cadenaproducto", myArray.toString());
                startActivity(intent);
                finish();

            }
        });

        despliegadatos();
    }

    private void EnviarProductos() {
        //RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://"+ipEstacion+"/CorpogasService/api/ventaProductos/GuardaProductos/sucursal/"+sucursalId+"/origen/"+numerodispositivo+"/usuario/"+usuario+"/posicionCarga/"+posicion;
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest request_json = new JsonArrayRequest(Request.Method.POST, url, myArray,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        //Get Final response
                        Toast.makeText(confirmaVenta.this, "Venta Realizada", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), FPaga.class);
                        intent.putExtra("posicion", posicion);
                        intent.putExtra("usuario", usuario);
                        startActivity(intent);
                        finish();
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
                        AlertDialog.Builder builder = new AlertDialog.Builder(confirmaVenta.this);
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
                        //transaccionId =
                        JSONObject obj = new JSONObject(responseString);
                        //Si es valido se asignan valores
                        //Intent intent = new Intent(getApplicationContext(), productoFormapago.class);
                        //DAtos enviados a formaPago
                        //intent.putExtra("posicion",posicionCarga);
                        //intent.putExtra("usuario",Usuarioid);
                        ////startActivity(intent);
                        //Toast.makeText(getApplicationContext(), "Venta realizada", Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                //return array;
                return Response.success(myArray, HttpHeaderParser.parseCacheHeaders(response));
            }
        };
        queue.add(request_json);
    }



    private void despliegadatos(){
        //Declaramos la lista de titulo
        ID = new ArrayList<String>();

        NombreProducto = new ArrayList<String>();
        PrecioProducto = new ArrayList<>();
        ClaveProducto = new ArrayList();
        codigoBarras = new ArrayList();
        ExistenciaProductos = new ArrayList();
        ProductosId = new ArrayList();
        TipoProductoId = new ArrayList();
        DescripcionPr = new ArrayList();;

        try {

            JSONArray response = new JSONArray(cadenaproductos);
            myArray = response;
            for (int i=0; i< response.length(); i++){
                JSONObject producto = response.getJSONObject(i);
                String describeproducto = producto.getString("NumeroInterno");
                String cantidad = producto.getString("Cantidad");
                String precio = producto.getString("Precio");
                String tproductoid = producto.getString("TipoProducto");
                String descripcioncorta = producto.getString("Descripcion");
                MontoTotal = MontoTotal +  Double.parseDouble(precio);


                NombreProducto.add("ID: " + describeproducto + "    |     $"+precio);
                ID.add(descripcioncorta);
                PrecioProducto.add(precio);
                ClaveProducto.add(describeproducto);
                //ProductosId.add(IdProductos);
                //codigoBarras.add(codigobarras);
                TipoProductoId.add(tproductoid);
                //DescripcionPr.add(DescLarga);
            }
            txttotal.setText("TOTAL : $"+MontoTotal);
            final ListAdapterProductos adapterP = new ListAdapterProductos(this,  ID, NombreProducto);
            list=(ListView)findViewById(R.id.list);
            list.setTextFilterEnabled(true);
            list.setAdapter(adapterP);
//        Agregado  click en la lista
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    String  Descripcion = ID.get(i).toString();
                    String precioUnitario = PrecioProducto.get(i).toString();
                    String paso= ClaveProducto.get(i).toString();
                    String existencia = ExistenciaProductos.get(i).toString();
                    String IProd = ProductosId.get(i).toString();
                    String TProd = TipoProductoId.get(i).toString();

                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}