package com.szzcs.smartpos.Cortes;

import android.content.DialogInterface;
import android.content.Intent;
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
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.gson.internal.$Gson$Preconditions;
import com.szzcs.smartpos.Munu_Principal;
import com.szzcs.smartpos.Productos.FPaga;
import com.szzcs.smartpos.Productos.ListAdapterProductos;
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

import devliving.online.mvbarcodereader.MVBarcodeScanner;

public class ventaProductosFaltantes extends AppCompatActivity implements View.OnClickListener {
    ImageButton btnscanner;
    EditText cantidadProducto, producto, productoIdentificador, precio, tipoproductoid;
    TextView txtdescripcion, txtproductoidentificador, txtcodigobarras, txtprecio;
    String EstacionId, sucursalId, ipEstacion, tipoTransaccion, numerodispositivo ;
    ListView list;
    Button comprar;
    JSONObject mjason = new JSONObject();
    JSONArray myArray = new JSONArray();

    String posicion, usuario;
    int elementoSeleccionado;
    int elementoSeleccionadoID;
    int [] ArrayArticulos;
    String Articulos;
    boolean bandera;

    JSONArray myArrayArticulo = new JSONArray();
    JSONArray jArrayv = new JSONArray();

    JSONArray productosEntregados = new JSONArray();
    String productosaEntregar;

    private MVBarcodeScanner.ScanningMode modo_Escaneo;
    private TextView text_cod_escaneado;
    private int CODE_SCAN = 1;

    ImageButton b_auto;
    List<String> ID;
    List<String> NombreProducto;
    List<String> PrecioProducto;
    List<String> ClaveProducto;
    List<String> CodigoBarra;
    List<String> ExistenciaProductos;
    List<String> ProductosId;
    List<String> totalProductos;
    List<String> TipoProductoId;
    List<String> DescripcionPr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venta_productos_faltantes);

        SQLiteBD db = new SQLiteBD(getApplicationContext());
        EstacionId = db.getIdEstacion();
        sucursalId = db.getIdSucursal();
        ipEstacion= db.getIpEstacion();
        tipoTransaccion = "1"; //Transaccion Normal
        numerodispositivo = "1";

        posicion = getIntent().getStringExtra("posicion");
        usuario = getIntent().getStringExtra("usuario");
        productosaEntregar= getIntent().getStringExtra("productosEntregados");

        cantidadProducto = findViewById(R.id.cantidadProducto);
        producto = findViewById(R.id.producto);
        productoIdentificador = findViewById(R.id.productoIdentificador);
        precio = findViewById(R.id.precio);
        txtdescripcion = findViewById(R.id.txtdescripcion);
        txtproductoidentificador = findViewById(R.id.productoIdentificador);
        txtcodigobarras = findViewById(R.id.txtcodigobarras);
        txtprecio = findViewById(R.id.precio);
        tipoproductoid = findViewById(R.id.tipoproductoid);

        mostrarProductosExistencias();

        comprar = findViewById(R.id.comprar);
        comprar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    String resultado = "";
                    //EditText cantidadProducto = (EditText)getActivity().findViewById();
                    String ProductoId;
                    int TotalProducto;
                    int ProductoIdEntero;
                    int tProdId;
                    TotalProducto = Integer.parseInt(cantidadProducto.getText().toString());
                    String PrecioMonto = precio.getText().toString();
                    Double precioUnitario = Double.valueOf(PrecioMonto);
                    ProductoId = productoIdentificador.getText().toString();
                    ProductoIdEntero = Integer.parseInt(ProductoId);
                    tProdId = Integer.parseInt(tipoproductoid.getText().toString());
                    int numerointerno = Integer.parseInt(producto.getText().toString());

                    JSONObject mjason = new JSONObject();

                    mjason.put("TipoProducto", tProdId);
                    mjason.put("ProductoId", ProductoIdEntero);
                    mjason.put("NumeroInterno", numerointerno);
                    mjason.put("Cantidad", TotalProducto);
                    mjason.put("Precio", precioUnitario);
                    myArray.put(mjason);
                    //String cadenaVenta = myArray.toString();
                    EnviarProductos(posicion, usuario);

                    JSONObject mjasonP = new JSONObject();
                    mjasonP.put("ProductoId", ProductoIdEntero);
                    myArrayArticulo.put(mjasonP);

                    producto.setText("");
                    txtdescripcion.setText("");
                    precio.setText("");
                    productoIdentificador.setText("");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        UI();
    }

    private void UI() {
        btnscanner = findViewById(R.id.btnscanner);
        btnscanner.setOnClickListener(this);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnscanner:
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
        int indicecodigo = CodigoBarra.indexOf(rawValue);

        if (indicecodigo > 0 ){
            String  descripcion = ID.get(indicecodigo);
            String precioUnitario = PrecioProducto.get(indicecodigo);
            String paso= ClaveProducto.get(indicecodigo);
            //String existencia = ExistenciaProductos.get(indicecodigo);
            String IProd = ProductosId.get(indicecodigo);
            String CBarras = CodigoBarra.get(indicecodigo).toString();
            String CantidadVender = totalProductos.get(indicecodigo);
            String TProd = TipoProductoId.get(indicecodigo);

            bandera = true;
            if (myArrayArticulo.length()>0  ) {
                //for (i = 0; i < myArrayArticulo.length(); i++) {
                try {
                    //jArrayv = new JSONArray(myArrayArticulo);
                    for (int m= 0; m <myArrayArticulo.length(); m++) {
                        JSONObject jsonObject = myArrayArticulo.getJSONObject(m);
                        if (jsonObject.has("ProductoId")) {
                            String valor = jsonObject.getString("ProductoId");
                            int res = Integer.parseInt(valor);
                            int productoSeleccionado = Integer.parseInt(ProductosId.get(indicecodigo).toString());
                            if (res == productoSeleccionado) {
                                Toast.makeText(getApplicationContext(), "Venta ya fue capturada", Toast.LENGTH_LONG).show();
                                bandera = false;
                                break;
                            } else {
                                elementoSeleccionado = productoSeleccionado;
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //}
            }else{
                elementoSeleccionado = Integer.parseInt(ProductosId.get(indicecodigo).toString());
            }
            if (bandera == true) {
                producto.setText(paso);
                txtdescripcion.setText(descripcion);
                precio.setText(precioUnitario);
                //existencias.setText(existencia);
                productoIdentificador.setText(IProd);
                txtcodigobarras.setText(CBarras);
                cantidadProducto.setText(CantidadVender);
                tipoproductoid.setText(TProd);

            }
        }else{
            Toast.makeText(getApplicationContext(), "Producto no encontrado en la lista", Toast.LENGTH_LONG).show();
            producto.setText("");
            txtdescripcion.setText("");
            precio.setText("");
            //existencias.setText(existencia);
            productoIdentificador.setText("");
            txtcodigobarras.setText("");
            cantidadProducto.setText("1");
            tipoproductoid.setText("");
        }
    }



    private void AgregarProducto() {
        String preciol = null;
        String DescLarga;
        String idArticulo;

        //Declaracion de variables
        final List<String> ID;
        ID = new ArrayList<String>();

        final List<String> NombreProducto;
        NombreProducto = new ArrayList<String>();

        final List<String> PrecioProducto;
        PrecioProducto = new ArrayList<>();

        final List<String> ClaveProducto;
        ClaveProducto = new ArrayList();

        final List<String> ExistenciaProductos;
        ExistenciaProductos = new ArrayList();

        final List<String> ProductosId;
        ProductosId = new ArrayList<>();

        String Arreglo = getIntent().getStringExtra("articulos");
        try{
            JSONArray productos = new JSONArray();
            for (int i=0; i<productos.length(); i++) {
                JSONObject desglose = productos.getJSONObject(i);
                String numerointerno = desglose.getString("ProductoId");
                //String numerointerno = desglose.getString("NumeroInterno");
                //String numerointerno = desglose.getString("codigobarras");
                String cantidad = desglose.getString("cantidad");
                preciol = desglose.getString("preciounitario");
                DescLarga = desglose.getString("DescCorta");

                NombreProducto.add("ID: " + numerointerno + "    |     $"+preciol); // + "    |    " + IdProductos );
                ID.add(DescLarga);
                PrecioProducto.add(preciol);
                ClaveProducto.add(numerointerno);
                //ProductosId.add(IdProductos);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final ListAdapterProductos adapterP = new ListAdapterProductos(this,  ID, NombreProducto);
        list=(ListView)findViewById(R.id.list);
        list.setTextFilterEnabled(true);
        list.setAdapter(adapterP);




        //txtproductoidentificador.setText(getIntent().getStringExtra("articuloid"));
        //txtproductoidentificador.setText(getIntent().getStringExtra("cantidadrecibida"));
        //txtproductoidentificador.setText(getIntent().getStringExtra("ventasrealizadas"));
        //txtcodigobarras.setText(getIntent().getStringExtra("preciounitario"));
        //txtcodigobarras.setText(getIntent().getStringExtra("codigobarras"));
        //txtnumerointerno.setText(getIntent().getStringExtra("numerointerno"));
        //txtdescripcion.setText(getIntent().getStringExtra("descripcionarticulo"));
        //cantidadProducto.setText(getIntent().getStringExtra("total"));
        //String usuario = getIntent().getStringExtra("usuario");
        //String posicion = getIntent().getStringExtra("posicion");


        String ProductoId;
        int TotalProducto;
        int ProductoIdEntero;

        //TotalProducto = Integer.parseInt(cantidadProducto.getText().toString());
        //String Precio = txtprecio.getText().toString();
        //Double precioUnitario = Double.valueOf(Precio);
        //ProductoId = txtproductoidentificador.getText().toString();
        //ProductoIdEntero = Integer.parseInt(ProductoId);

        //if (ProductoId.isEmpty()) {
        //    Toast.makeText(getApplicationContext(), "Escanee el Producto", Toast.LENGTH_LONG).show();
        //} else {
        //    try {
        //        JSONObject mjason = new JSONObject();
        //        mjason.put("ProductoId", ProductoIdEntero);
        //        mjason.put("Cantidad", TotalProducto);
        //        mjason.put("Precio", precioUnitario);
        //        myArray.put(mjason);
        //        //EnviarProductos(posicion, usuario);

        //    } catch (JSONException error) {

        //    }
        //}
    }

    private void EnviarProductos(final String posicionCarga, final String Usuarioid) {
        String url = "http://"+ipEstacion+"/CorpogasService/api/ventaProductos/GuardaProductos/sucursal/"+sucursalId+"/origen/"+numerodispositivo+"/usuario/"+Usuarioid+"/posicionCarga/"+posicionCarga;
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest request_json = new JsonArrayRequest(Request.Method.POST, url, myArray,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        //Get Final response
                        Intent intent = new Intent(getApplicationContext(), FPaga.class);
                        intent.putExtra("posicion", posicionCarga);
                        intent.putExtra("usuario", Usuarioid);
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
                        AlertDialog.Builder builder = new AlertDialog.Builder(ventaProductosFaltantes.this);
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
                        //JSONObject obj = new JSONObject(responseString);
                        //Si es valido se asignan valores
                        //Intent intent = new Intent(getApplicationContext(), productoFormapago.class);
                        //DAtos enviados a formaPago
                        //intent.putExtra("posicion",posicionCarga);
                        //intent.putExtra("usuario",Usuarioid);
                        ////startActivity(intent);
                        //Toast.makeText(getApplicationContext(), "Venta realizada", Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        e.printStackTrace();                    }
                }
                //return array;
                return Response.success(myArray, HttpHeaderParser.parseCacheHeaders(response));
            }
        };
        queue.add(request_json);
    }

    private void mostrarProductosExistencias(){
        String preciol = null;
        String DescLarga;
        String idArticulo;

        //Declaracion de variables


        ID = new ArrayList<String>();

        NombreProducto = new ArrayList<String>();
        PrecioProducto = new ArrayList<>();
        ClaveProducto = new ArrayList();
        CodigoBarra = new ArrayList();
        ExistenciaProductos = new ArrayList();
        ProductosId = new ArrayList();
        totalProductos = new ArrayList<>();
        TipoProductoId = new ArrayList();
        DescripcionPr = new ArrayList();;

        String arregloProductos = getIntent().getStringExtra("articulos");
        try {
            JSONArray productos = new JSONArray(arregloProductos);
            for (int i=0; i<productos.length(); i++) {
                JSONObject desglose = productos.getJSONObject(i);
                idArticulo = desglose.getString("ProductoId");
                String numerointerno = desglose.getString("NumeroInterno");
                //String numerointerno = desglose.getString("codigobarras");
                String cantidad = desglose.getString("Cantidad");
                preciol = desglose.getString("Precio");
                DescLarga = desglose.getString("DescCorta");
                String CBarra = desglose.getString("CodigoBarras");

                NombreProducto.add("ID: " + numerointerno + "    |     $"+preciol); // + "    |    " + IdProductos );
                ID.add(DescLarga);
                PrecioProducto.add(preciol);
                ClaveProducto.add(numerointerno);
                ProductosId.add(idArticulo);
                CodigoBarra.add(CBarra);
                totalProductos.add(cantidad);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final ListAdapterProductos adapterP = new ListAdapterProductos(this,  ID, NombreProducto);
        list=(ListView)findViewById(R.id.list);
        list.setTextFilterEnabled(true);
        list.setAdapter(adapterP);
//        Agregado  click en la lista
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                producto.setText("");
                txtdescripcion.setText("");
                precio.setText("");
                productoIdentificador.setText("");
                cantidadProducto.setText("1");

                elementoSeleccionadoID= i;
                bandera = true;
                if (myArrayArticulo.length()>0  ) {
                    //for (i = 0; i < myArrayArticulo.length(); i++) {
                        try {
                            //jArrayv = new JSONArray(myArrayArticulo);
                            for (int m= 0; m <myArrayArticulo.length(); m++) {
                                    JSONObject jsonObject = myArrayArticulo.getJSONObject(m);
                                    if (jsonObject.has("ProductoId")) {
                                        String valor = jsonObject.getString("ProductoId");
                                        int res = Integer.parseInt(valor);
                                        int productoSeleccionado = Integer.parseInt(ProductosId.get(i).toString());
                                        if (res == productoSeleccionado) {
                                            Toast.makeText(getApplicationContext(), "Venta ya fue capturada", Toast.LENGTH_LONG).show();
                                            bandera = false;
                                            break;
                                        } else {
                                            elementoSeleccionado = productoSeleccionado;
                                        }
                                    }
                                }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    //}
                }else{
                    elementoSeleccionado = Integer.parseInt(ProductosId.get(i).toString());
                }
                if (bandera == true) {
                    //ArrayArticulos.add(i);
                    String descripcion = ID.get(i).toString();
                    String precioUnitario = PrecioProducto.get(i).toString();
                    String paso = ClaveProducto.get(i).toString();
                    String IProd = ProductosId.get(i).toString();
                    String CBarras = CodigoBarra.get(i).toString();
                    String CantidadVender = totalProductos.get(i);
                    String tipoprod = TipoProductoId.get(i);

                    producto.setText(paso);
                    txtdescripcion.setText(descripcion);
                    precio.setText(precioUnitario);
                    //existencias.setText(existencia);
                    productoIdentificador.setText(IProd);
                    txtcodigobarras.setText(CBarras);
                    cantidadProducto.setText(CantidadVender);
                    tipoproductoid.setText(tipoprod);
                }
            }
        });
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}