package com.szzcs.smartpos.Puntada.Acumular;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.zxing.BarcodeFormat;
import com.szzcs.smartpos.Munu_Principal;
import com.szzcs.smartpos.R;
import com.szzcs.smartpos.configuracion.SQLiteBD;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import devliving.online.mvbarcodereader.MVBarcodeScanner;


public class productos extends AppCompatActivity implements View.OnClickListener{
    Button btnAgregar,btnEnviar, aumentar, decrementar, comprar;
    ImageButton btn_m_auto;
    TextView cantidadProducto, txtDescripcion, NumeroProductos, precio, existencias,idproducto;
    EditText Producto;
    String cantidad, idproductos;
    JSONObject datos = new JSONObject();
    JSONArray myArray = new JSONArray();
    String EstacionId, sucursalId;
    ListView list;
    Integer ProductosAgregados = 0;
    String posicion;
    private ImageButton b_auto;
    private MVBarcodeScanner.ScanningMode modo_Escaneo;
    private TextView text_cod_escaneado;
    private int CODE_SCAN = 1;

    List<String> ID;
    List<String> NombreProducto;
    List<String> PrecioProducto;
    List<String> ClaveProducto;
    List<String> CodigoBarras;
    List<String> ExistenciaProductos;
    List<String> ProductoId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SQLiteBD data = new SQLiteBD(getApplicationContext());
        setContentView(R.layout.activity_productos__puntada);
        this.setTitle(data.getNombreEsatcion());

        btnAgregar = findViewById(R.id.btnAgregar);
        btnAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject Productos = new JSONObject();
                try {
                    Productos.put("TipoProducto",2);

                    Productos.put("ProductoId",idproducto.getText().toString());
                    Productos.put("NumeroInterno",Producto.getText().toString());
                    Productos.put("Descripcion",txtDescripcion.getText().toString());
                    Productos.put("Cantidad",cantidadProducto.getText().toString());
                    Productos.put("Precio",precio.getText().toString());
                    myArray.put(Productos);

                    idproducto.setText("");
                    Producto.setText("");
                    txtDescripcion.setText("");
                    cantidadProducto.setText("1");
                    precio.setText("");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

        btnEnviar = findViewById(R.id.btnEnviar);
        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EnviarDatos();
            }
        });

        MostrarProductos();
        CantidadProducto();
        UI();


        aumentar = findViewById(R.id.btnIncrementar);
        aumentar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Aumentar();
            }
        });

        decrementar = findViewById(R.id.btnDecrementar);
        decrementar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Decrementar();
            }
        });

        

    }


    private void UI() {
        b_auto = findViewById(R.id.btn_m_auto);
        b_auto.setOnClickListener(this);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_m_auto:
                modo_Escaneo = MVBarcodeScanner.ScanningMode.SINGLE_AUTO;
                break;
        }

        new MVBarcodeScanner.Builder().setScanningMode(modo_Escaneo).setFormats(Barcode.ALL_FORMATS)
                .build()
                .launchScanner(this, CODE_SCAN);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CODE_SCAN) {
            if (resultCode == RESULT_OK && data != null
                    && data.getExtras() != null) {

                if (data.getExtras().containsKey(MVBarcodeScanner.BarcodeObject)) {
                    Barcode mBarcode = data.getParcelableExtra(MVBarcodeScanner.BarcodeObject);
//                    Producto.setText(mBarcode.rawValue);
                    buscarCodigoBarra(mBarcode.rawValue);
                } else if (data.getExtras().containsKey(MVBarcodeScanner.BarcodeObjects)) {
                    List<Barcode> mBarcodes = data.getParcelableArrayListExtra(MVBarcodeScanner.BarcodeObjects);
                    StringBuilder s = new StringBuilder();
                    for (Barcode b:mBarcodes){
                        s.append(b.rawValue + "\n");
                    }
//                    Producto.setText(s.toString());
                    buscarCodigoBarra(s.toString());
                }
            }
        }
    }

    private void buscarCodigoBarra(String rawValue) {
        int indicecodigo = CodigoBarras.indexOf(rawValue);

        String  Descripcion = ID.get(indicecodigo);
        String precioUnitario = PrecioProducto.get(indicecodigo);
        String paso= ClaveProducto.get(indicecodigo);
        String existencia = ExistenciaProductos.get(indicecodigo);
        String idproduc = ProductoId.get(indicecodigo);


        Producto.setText(paso);
        txtDescripcion.setText(Descripcion);
        precio.setText(precioUnitario);
        existencias.setText(existencia);
        idproducto.setText(idproduc);

    }

    private void CantidadProducto() {
        cantidadProducto = findViewById(R.id.cantidadProducto);
        Producto= findViewById(R.id.Producto);
        cantidad = cantidadProducto.toString();
        txtDescripcion = findViewById(R.id.txtDescripcion);
        precio = findViewById(R.id.precio);
        existencias = findViewById(R.id.existencias);
        idproducto = findViewById(R.id.idproducto);
        idproductos = idproducto.toString();
    }
    private void Aumentar() {
        cantidad = cantidadProducto.getText().toString();
        int numero = Integer.parseInt(cantidad);
        int totalexistencia = Integer.parseInt(existencias.getText().toString());
        if (numero<totalexistencia) {
            int total = numero + 1;
            String resultado = String.valueOf(total);
            cantidadProducto.setText(resultado);
        }else{
            Toast.makeText(getApplicationContext(), "solo hay "+ existencias.getText().toString()+ " en existencia ", Toast.LENGTH_LONG).show();
        }

    }

    private void Decrementar() {
        cantidad = cantidadProducto.getText().toString();
        int numero = Integer.parseInt(cantidad);
        if (numero > 1) {
            int total = numero - 1;
            String resultado = String.valueOf(total);
            cantidadProducto.setText(resultado);
        }else{
            Toast.makeText(getApplicationContext(), "el valor minimo debe ser 1", Toast.LENGTH_LONG).show();
        }
    };

    private void EnviarDatos() {
        SQLiteBD data = new SQLiteBD(getApplicationContext());
        String clave = getIntent().getStringExtra("PasswordDespachador");
        String url = "http://"+data.getIpEstacion()+"/CorpogasService/api/puntadas/actualizaPuntos/clave/"+clave;

        RequestQueue queue = Volley.newRequestQueue(this);

        try {

            datos.put("EstacionId", data.getIdEstacion());
            datos.put("RequestID",35);
            String PosicionDeCarga = getIntent().getStringExtra("pos");
            datos.put("PosicionCarga",PosicionDeCarga);
            String NumeroDeTarjeta = getIntent().getStringExtra("track");
            datos.put("Tarjeta", NumeroDeTarjeta);
            datos.put("NuTarjetero", 1);
            datos.put("Productos", myArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request_json = new JsonObjectRequest(Request.Method.POST, url, datos, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                String estado = null;
                String mensaje = null;
                try {
                    estado = response.getString("Estado");
                    mensaje = response.getString("Mensaje");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (estado.equals("true")){
                        try {
                            AlertDialog.Builder builder = new AlertDialog.Builder(productos.this);
                            builder.setTitle("Tarjeta Puntada");

                            builder.setMessage(mensaje);
                            builder.setPositiveButton("Cerrar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(productos.this, Munu_Principal.class);
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
                        try {
                            AlertDialog.Builder builder = new AlertDialog.Builder(productos.this);
                            builder.setTitle("Tarjeta Puntada");
                            builder.setMessage(mensaje);
                            builder.setPositiveButton("Cerrar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.cancel();
                                }
                            });
                            AlertDialog dialog= builder.create();
                            dialog.show();
                        }catch (Exception e){
                            e.printStackTrace();

                        }

                    }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


            }
        }){
            public Map<String,String>getHeaders() throws AuthFailureError {
                Map<String,String> headers = new HashMap<String, String>();
                return headers;
            }
            protected  Response<JSONObject> parseNetwokResponse(NetworkResponse response){
                if (response != null){

                    try {
                        String responseString;
                        JSONObject datos = new JSONObject();
                        responseString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));

                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
                return Response.success(datos, HttpHeaderParser.parseCacheHeaders(response));
            }
        };
        queue.add(request_json);
    }

    private void MostrarProductos() {
        SQLiteBD data = new SQLiteBD(getApplicationContext());
        String posicion = getIntent().getStringExtra("pos");
        String url = "http://"+data.getIpEstacion()+"/CorpogasService/api/islas/productos/estacion/"+data.getIdEstacion()+"/posicionCargaId/"+posicion;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //mostarProductor(response);
                mostrarProductor(response);
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
    private void mostrarProductor(String response) {

        String preciol = null;
        String DescLarga;
        String idArticulo;

        //Declaracion de variables

        ID = new ArrayList<String>();

        NombreProducto = new ArrayList<String>();

        PrecioProducto = new ArrayList<>();

        ClaveProducto = new ArrayList();

        CodigoBarras = new ArrayList();

        ExistenciaProductos = new ArrayList();

        ProductoId = new ArrayList();

        //ArrayList<singleRow> singlerow = new ArrayList<>();
        try {
            JSONObject p1 = new JSONObject(response);

            String ni = p1.getString("NumeroInterno");
            String bodega = p1.getString("Bodega");
            JSONObject ps = new JSONObject(bodega);
            String producto = ps.getString("BodegaProductos");
            JSONArray bodegaprod = new JSONArray(producto);

            for (int i = 0; i <bodegaprod.length() ; i++){
                String idproductos = null;
                JSONObject pA = bodegaprod.getJSONObject(i);
                String ExProductos=pA.getString("Existencias");
                ExistenciaProductos.add(ExProductos);
                String productoclave = pA.getString("Producto");
                JSONObject prod = new JSONObject(productoclave);
                DescLarga=prod.getString("DescripcionLarga");
                String codigobarra = prod.getString("CodigoBarras");
                idArticulo=prod.getString("NumeroInterno");
                String PControl=prod.getString("ProductoControles");
                JSONArray PC = new JSONArray(PControl);
                for (int j = 0; j <PC.length() ; j++) {
                    JSONObject Control = PC.getJSONObject(j);
                    preciol  = Control.getString("Precio");
                    idproductos = Control.getString("Id");

                }
                NombreProducto.add("ID: " + idArticulo + "    |     $"+preciol);
                ID.add(DescLarga);
                PrecioProducto.add(preciol);
                ClaveProducto.add(idArticulo);
                CodigoBarras.add(codigobarra);
                ProductoId.add(idproductos);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        final ListAdapterSP adapterP = new ListAdapterSP(this,  ID, NombreProducto);
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
                String idproduc = ProductoId.get(i);

                Producto.setText(paso);
                txtDescripcion.setText(Descripcion);
                precio.setText(precioUnitario);
                existencias.setText(existencia);
                idproducto.setText(idproduc);

            }
        });

    }

}
