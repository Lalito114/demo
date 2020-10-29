package com.szzcs.smartpos.TanqueLleno;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.szzcs.smartpos.Munu_Principal;
import com.szzcs.smartpos.R;
import com.szzcs.smartpos.configuracion.SQLiteBD;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ProductoTLl extends AppCompatActivity {
    ListView list;
    EditText litros, pesos;
    Button agregarcombustible, imprimirTicket, enviarProductos, limpiar;
    String IdCombustible, cs,numneroInterno,  descripcion, precio, IdCombus, Costo;
    double  LitrosCoversion;
    final JSONObject datos = new JSONObject();
    JSONArray array1 = new JSONArray();
    JSONObject jsonParam = new JSONObject();
    String folio, transaccion;
    boolean pasa;
    String NumeroInternoSucursal,SucursalEmpleadoId,PosicionDeCarga,NumeroDeTarjeta,ClaveTanqueLleno,TipoCliente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_producto_tll);
        SQLiteBD data = new SQLiteBD(this);
        this.setTitle(data.getRazonSocial());


        NumeroInternoSucursal = getIntent().getStringExtra("NumeroInternoEstacion");
        SucursalEmpleadoId = getIntent().getStringExtra("SucursalEmpleadoId");
        PosicionDeCarga = getIntent().getStringExtra("PosicioDeCarga");
        NumeroDeTarjeta = getIntent().getStringExtra("NumeroDeTarjeta");
        ClaveTanqueLleno = getIntent().getStringExtra("ClaveTanqueLleno");
        TipoCliente = getIntent().getStringExtra("Tipocliente");

        Combustibles();

        pesos = findViewById(R.id.edtPesos);
        pesos.setEnabled(false);

        litros = findViewById(R.id.edtLitros);
        litros.setEnabled(false);


        limpiar = findViewById(R.id.btnLipmpiarProducto);
        limpiar.setVisibility(View.INVISIBLE);
        limpiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                array1 = new JSONArray();
                limpiar.setVisibility(View.INVISIBLE);
                Combustibles();
                litros.setText("");
                pesos.setText("");
                agregarcombustible.setVisibility(View.VISIBLE);
                enviarProductos.setVisibility(View.INVISIBLE);

            }
        });

        agregarcombustible = findViewById(R.id.btnAgregarcombustible);
        agregarcombustible.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (litros.getText().toString().isEmpty()){
                    if (pesos.getText().toString().isEmpty()){
                        Toast.makeText(ProductoTLl.this, "Ingresa uno de los dos campos que se requieren", Toast.LENGTH_SHORT).show();
                    }else{
                        double pideenpesos = Double.valueOf(pesos.getText().toString());
                        double litrospedidos = pideenpesos / Double.valueOf(Costo);
                        String valor = String.valueOf(litrospedidos);

//                        NumberFormat nf = NumberFormat.getNumberInstance(Locale.GERMAN);
//                        DecimalFormat df = (DecimalFormat)nf;
                        DecimalFormatSymbols simbolos = new DecimalFormatSymbols();
                        simbolos.setDecimalSeparator('.');
                        DecimalFormat df = new DecimalFormat("####.####",simbolos);

                        df.setMaximumFractionDigits(3);
                        litros.setText(df.format(litrospedidos));

                        try {
                            //Add string params
                            jsonParam.put("TipoProducto","1");
                            jsonParam.put("ProductoId",IdCombustible);
                            jsonParam.put("NumeroInterno",IdCombustible);
                            jsonParam.put("Descripcion",descripcion);
                            jsonParam.put("Cantidad",df.format(litrospedidos));
                            jsonParam.put("Precio",pideenpesos);
                            array1.put(jsonParam);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }else{
                    double litrospedidos = Double.valueOf(litros.getText().toString());
                    double costofinal = litrospedidos * Double.valueOf(Costo);
                    String valor = String.valueOf(costofinal);

                    DecimalFormatSymbols simbolos = new DecimalFormatSymbols();
                    simbolos.setDecimalSeparator('.');
                    DecimalFormat df = new DecimalFormat("####.####",simbolos);

                    df.setMaximumFractionDigits(3);
                    pesos.setText(df.format(costofinal));

                    try {
                        //Add string params
                        litros = findViewById(R.id.edtLitros);
                        jsonParam.put("TipoProducto","1");
                        jsonParam.put("ProductoId",IdCombustible);
                        jsonParam.put("NumeroInterno",IdCombustible);
                        jsonParam.put("Descripcion",descripcion);
                        jsonParam.put("Cantidad",litrospedidos);
                        jsonParam.put("Precio",df.format(costofinal));
                        array1.put(jsonParam);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                v.setVisibility(View.INVISIBLE);
                enviarProductos.setVisibility(View.VISIBLE);
                limpiar.setVisibility(View.VISIBLE);
                //imprimirTicket.setVisibility(View.INVISIBLE);

            }
        });

        enviarProductos = findViewById(R.id.btnenciarProductos);
        enviarProductos.setVisibility(View.INVISIBLE);
        enviarProductos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (array1!= null){
                    ProgressDialog bar = new ProgressDialog(ProductoTLl.this);
                    bar.setTitle("Tanque Lleno");
                    bar.setMessage("Esperando Respuesta");
                    bar.setIcon(R.drawable.tanquelleno);
                    bar.show();
                    EnviarProductos1();
                    v.setVisibility(View.INVISIBLE);

                }else{
                    Toast.makeText(getApplicationContext(),"Ingresa el producto", Toast.LENGTH_LONG).show();
                }

            }
        });

    }

    private void EnviarProductos1() {
        SQLiteBD data = new SQLiteBD(getApplicationContext());
        String URL = "http://"+data.getIpEstacion()+"/CorpogasService/api/tanqueLleno/EnviarProductos";
        RequestQueue queue = Volley.newRequestQueue(this);
        try {

            datos.put("NumeroInternoSucursal", NumeroInternoSucursal);
            datos.put("EstacionId",data.getIdEstacion());
            datos.put("SucursalEmpleadoId",SucursalEmpleadoId);
            datos.put("PosicionCarga",PosicionDeCarga);
            datos.put("TarjetaCliente", NumeroDeTarjeta);
            String placas = getIntent().getStringExtra("placas");
            datos.put("Placas", placas);
            String odometro = getIntent().getStringExtra("odometro");
            datos.put("Odometro",odometro);
            datos.put( "ClaveTanqueLleno", ClaveTanqueLleno);
            datos.put("Nip", "0e674a918ebca3f78bfe02e2f387689d");
            datos.put("TipoCliente", TipoCliente);
            datos.put("Productos", array1);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request_json = new JsonObjectRequest(Request.Method.POST, URL, datos, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                pasa= true;

                try {
                    String validacion = response.getString("Correcto");
                     transaccion = response.getString("TransaccionId");
                     folio = response.getString("Folio");
                    if (validacion == "true"){
                        agregarcombustible.setVisibility(View.INVISIBLE);
//                        imprimirTicket.setVisibility(View.VISIBLE);
                        limpiar.setVisibility(View.INVISIBLE);

                        String estado = response.getString("Mensaje");
                        AlertDialog.Builder builder = new AlertDialog.Builder(ProductoTLl.this);
                        builder.setTitle("Tarjeta Tanque Lleno");
                        builder.setMessage("La peticion se ha completado corectamente");
                        builder.setPositiveButton("Cerrar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                Intent intent = new Intent(getApplicationContext(), Munu_Principal.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                        AlertDialog dialog= builder.create();
                        dialog.show();

                    }else{
                        String estado = response.getString("Mensaje");
                        AlertDialog.Builder builder = new AlertDialog.Builder(ProductoTLl.this);
                        builder.setTitle("Tarjeta Tanque Lleno");
                        builder.setMessage(estado);
                        builder.setPositiveButton("Cerrar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(getApplicationContext(), Munu_Principal.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                        AlertDialog dialog= builder.create();
                        dialog.show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String error1 = error.networkResponse.data.toString();
                Toast.makeText(ProductoTLl.this, error1, Toast.LENGTH_SHORT).show();
                pasa= false;
            }
        }){
            public Map<String,String>getHeaders() throws AuthFailureError{
                Map<String,String> headers = new HashMap<String, String>();
                return headers;
            }
            protected  Response<JSONObject> parseNetwokResponse(NetworkResponse response){
                if (response != null){

                    try {
                        String responseString;
                        JSONObject datos = new JSONObject();
                        responseString = new String(response.data,HttpHeaderParser.parseCharset(response.headers));

                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
                return Response.success(datos, HttpHeaderParser.parseCacheHeaders(response));
            }
        };
        request_json.setRetryPolicy(new DefaultRetryPolicy(
                120000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(request_json);



    }



    private void Combustibles() {
        SQLiteBD data = new SQLiteBD(getApplicationContext());
        String url = "http://"+data.getIpEstacion()+"/CorpogasService/api/precioCombustibles/estacion/"+data.getIdEstacion()+"/actuales";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                MetodoResponse(response);
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

    private void MetodoResponse(final String response) {
        List<String> maintitle;
        maintitle = new ArrayList<String>();

        List<String> subtitle;
        subtitle = new ArrayList<String>();

        List<Integer> imgid;
        imgid = new ArrayList<>();

        final List<String> IdProducto;
        IdProducto = new ArrayList<String>();

        final List<String> IdCombustible1;
        IdCombustible1 = new ArrayList<String>();

        final List<String> Precio;
        Precio = new ArrayList<String>();

        try {
            JSONArray combusti = new JSONArray(response);
            for (int i = 0; i <combusti.length() ; i++) {

                JSONObject conbustibles = combusti.getJSONObject(i);
                String activo = conbustibles.getString("Aplicado");
                if (activo == "true"){
                    cs = conbustibles.getString("EstacionCombustibleId");
                    IdCombustible1.add(cs);


                    precio = conbustibles.getString("Importe");
                    Precio.add(precio);

                    String combus = conbustibles.getString("EstacionCombustible");
                    JSONObject combustib = new JSONObject(combus);

                    numneroInterno = combustib.getString("NumeroInterno");
                    String combustible = combustib.getString("Combustible");
                    IdProducto.add(numneroInterno);


                    JSONObject nombre = new JSONObject(combustible);
                    descripcion = nombre.getString("DescripcionLarga");
                }




                maintitle.add(descripcion);
                subtitle.add("Precio: $"+precio);
                if (cs == "1"){
                    imgid.add(R.drawable.premium);
                }else{
                    if (cs == "2"){
                        imgid.add(R.drawable.magna);
                    }else{
                        if (cs == "3"){
                            imgid.add(R.drawable.diesel);
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //ListAdapterP adapterP=new ListAdapterP(this, maintitle, subtitle,imgid);
        ListAdapterConbustiblesTLl adapterP = new ListAdapterConbustiblesTLl(this, maintitle, subtitle, imgid);
        list=(ListView)findViewById(R.id.list);
        list.setAdapter(adapterP);


        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub

                String identificadorCombustible = IdProducto.get(position);
                IdCombustible = identificadorCombustible;

                String interno = IdCombustible1.get(position);
                IdCombus = interno;

                String cuesta = Precio.get(position);
                Costo = cuesta;

                final List<String> main;
                main = new ArrayList<String>();

                main.add(maintitle.get(position));


                final List<String> sub;
                sub = new ArrayList<String>();

                sub.add(subtitle.get(position));


                final List<Integer> img;
                img = new ArrayList<>();

                img.add(imgid.get(position));

                ListAdapterConbustiblesTLl adapterP = new ListAdapterConbustiblesTLl(ProductoTLl.this, main, sub, img);
                list=(ListView)findViewById(R.id.list);
                list.setAdapter(adapterP);
                pesos = findViewById(R.id.edtPesos);
                litros = findViewById(R.id.edtLitros);
                litros.setEnabled(true);

                pesos.setEnabled(true);


            }
        });
    }
}
