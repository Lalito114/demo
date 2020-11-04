package com.szzcs.smartpos.Cortes2;

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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.gson.JsonArray;
import com.google.gson.internal.$Gson$Preconditions;
import com.szzcs.smartpos.Munu_Principal;
import com.szzcs.smartpos.Productos.FPaga;
import com.szzcs.smartpos.Productos.ListAdapterProductos;
import com.szzcs.smartpos.Productos.VentasProductos;
import com.szzcs.smartpos.Puntada.Acumular.productos;
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

public class VentaProductosFaltantes extends AppCompatActivity implements View.OnClickListener {
    ImageButton btnscanner;
    EditText cantidadProducto, producto, productoIdentificador, precio, tipoproductoid;
    TextView txtdescripcion, txtproductoidentificador, txtcodigobarras, txtprecio;
    String EstacionId, sucursalId, ipEstacion, tipoTransaccion, numerodispositivo ;
    ListView list;
    Button comprar, enviar;
    JSONObject mjason;
    JSONArray myArray = new JSONArray();
    JSONObject JOcompleto = new JSONObject();
    String islaId, fechatrabajo;

    String posicion;
    int elementoSeleccionado;
    int elementoSeleccionadoID;
    int [] ArrayArticulos;
    String Articulos;
    boolean bandera;

    JSONArray myArrayArticulo = new JSONArray();
    JSONArray ArrayResultante = new JSONArray();

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

//    Double VentaTotalProductos;

    RespuestaApi<Cierre> cierreRespuestaApi;
    long cierreId;
    long turnoId;

    String VentaProductos;
    String cantidadAceites;

    RespuestaApi<AccesoUsuario> accesoUsuario;
    long idusuario;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venta_productos_faltantes);

        SQLiteBD db = new SQLiteBD(getApplicationContext());
        cierreRespuestaApi = (RespuestaApi<Cierre>) getIntent().getSerializableExtra( "lcierreRespuestaApi");
        accesoUsuario = (RespuestaApi<AccesoUsuario>) getIntent().getSerializableExtra("accesoUsuario");
        EstacionId = db.getIdEstacion();
        sucursalId = db.getIdSucursal();
        ipEstacion= db.getIpEstacion();
        tipoTransaccion = "1"; //Transaccion Normal
        numerodispositivo = "1";
        posicion = getIntent().getStringExtra("posicion");
        idusuario = accesoUsuario.getObjetoRespuesta().getSucursalEmpleadoId();
        productosaEntregar= getIntent().getStringExtra("articulos");
        islaId = getIntent().getStringExtra("islaId");
        fechatrabajo = cierreRespuestaApi.getObjetoRespuesta().getFechaTrabajo();
        VentaProductos = getIntent().getStringExtra("VentaProductos"); //null
        cantidadAceites = getIntent().getStringExtra("cantidadAceites");

        turnoId = cierreRespuestaApi.getObjetoRespuesta().getTurnoId();
        cierreId = cierreRespuestaApi.getObjetoRespuesta().getId();

//        VentaTotalProductos = Double.parseDouble(getIntent().getStringExtra("ventaproductos"));

        try {
            ArrayResultante = new JSONArray(productosaEntregar);
        } catch (JSONException e) {
            e.printStackTrace();
        }

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
        enviar = findViewById(R.id.enviar);
        enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myArrayArticulo.length()>0  ) {
                    int TotalElementosLista = list.getCount();
                    int TotalElementosArregloVendidos = myArrayArticulo.length();

                    if (TotalElementosLista == TotalElementosArregloVendidos){
                        //Envia a siguiente Activity
                            Intent intent = new Intent(getApplicationContext(),FajillasBilletes.class);
                            intent.putExtra("idusuario", idusuario);
                            intent.putExtra("islaId", islaId);
                            intent.putExtra("VentaProductos", String.valueOf(VentaProductos));
                            intent.putExtra("cantidadAceites", String.valueOf(cantidadAceites));
                            intent.putExtra("lcierreRespuestaApi", cierreRespuestaApi);
                            intent.putExtra("accesoUsuario", accesoUsuario);
                            startActivity(intent);
                    }else{
                        Toast.makeText(getApplicationContext(), "Aún existe productos que no se han capturado las ventas pendientes", Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(getApplicationContext(), "Aún existe productos que no se han capturado las ventas pendientes", Toast.LENGTH_LONG).show();
                }
            }
        });

        comprar = findViewById(R.id.comprar);
        comprar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    String validaProducto = producto.getText().toString();
                    if (validaProducto.isEmpty()) {
                        Toast.makeText(getApplicationContext(), "Seleccione uno de los Productos", Toast.LENGTH_LONG).show();
                    }else {
                        //EditText cantidadProducto = (EditText)getActivity().findViewById();
                        String ProductoId;
                        int TotalProducto;
                        int ProductoIdEntero;
                        int tProdId;
                        String descripcion;
                        TotalProducto = Integer.parseInt(cantidadProducto.getText().toString());
                        String PrecioMonto = precio.getText().toString();
                        Double precioUnitario = Double.valueOf(PrecioMonto);
                        ProductoId = productoIdentificador.getText().toString();
                        ProductoIdEntero = Integer.parseInt(ProductoId);
                        tProdId = Integer.parseInt(tipoproductoid.getText().toString());
                        int numerointerno = Integer.parseInt(producto.getText().toString());
                        descripcion = txtdescripcion.getText().toString();
                        mjason = new JSONObject();

                        mjason.put("TipoProducto", tProdId);
                        mjason.put("ProductoId", ProductoIdEntero);
                        mjason.put("NumeroInterno", numerointerno);
                        mjason.put("Descripcion", descripcion);
                        mjason.put("Cantidad", TotalProducto);
                        mjason.put("Precio", precioUnitario);

                        //String cadenaVenta = myArray.toString();
                        EnviarProductos(posicion, String.valueOf(idusuario), ProductoIdEntero);


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        UI();
    }

    private void ObtieneArrayResultanteparaGuardar() throws JSONException {
        JsonArray acierrecombustibledetalle= new JsonArray();
        JsonArray acierreformapago= new JsonArray();
        JsonArray accarretes= new JsonArray();
        JsonArray acdetallecategoriaproducto= new JsonArray();
        String url = "http://"+ipEstacion+"/CorpogasService/api/cierres/GuardaCierreDetalle/usuario/"+idusuario;
        RequestQueue queue = Volley.newRequestQueue(this);
        try {
            JOcompleto.put("SucursalId", Integer.parseInt(sucursalId));
            JOcompleto.put("TurnoId", turnoId);
            JOcompleto.put("TurnoSucursalId", Integer.parseInt(sucursalId));
            JOcompleto.put("Transacciones", 0);
            JOcompleto.put("TotalVenta", 0);
            JOcompleto.put("TotalIva", 0);
            JOcompleto.put("TotalIeps", 0); //Entregada
            JOcompleto.put("Completado", true);
            JOcompleto.put("IslaId", Integer.parseInt(islaId));
            JOcompleto.put("IslaEstacionId", Integer.parseInt(EstacionId));
            JOcompleto.put("FechaTrabajo", fechatrabajo);
            JOcompleto.put("CierreDetalles", ArrayResultante);
            JOcompleto.put("CierreCombustibleDetalles", acierrecombustibledetalle);
            JOcompleto.put("CierreFormaPagos", acierreformapago);
            JOcompleto.put("CierreCarretes", accarretes);
            JOcompleto.put("CierreDetalleCategoriaProducto", acdetallecategoriaproducto);
            JOcompleto.put("Id", cierreId);
            JOcompleto.put("OrigenId", Integer.parseInt(numerodispositivo));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest request_json = new JsonObjectRequest(Request.Method.POST, url, JOcompleto, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //concluye guardado de corte perifericos
                Toast.makeText(getApplicationContext(),"Perifericos Cargados Exitosamente",Toast.LENGTH_LONG).show();
                //Enviar a la siguiente pantalla del CORTE
                //Intent intent = new Intent(getApplicationContext(), productosVendidos.class);
                //startActivity(intent);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),error.toString(), Toast.LENGTH_LONG).show();
            }
        }){
            public Map<String,String> getHeaders() throws AuthFailureError {
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
                return Response.success(JOcompleto, HttpHeaderParser.parseCacheHeaders(response));
            }
        };
        queue.add(request_json);
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

    private void EnviarProductos(final String posicionCarga, final String Usuarioid, final Integer ProductoIdEntero) {
        //String url = "http://"+ipEstacion+"/CorpogasService/api/ventaProductos/GuardaProductos/sucursal/"+sucursalId+"/origen/"+numerodispositivo+"/usuario/"+Usuarioid+"/posicionCarga/"+posicionCarga;
        String url = "http://"+ipEstacion+"/CorpogasService/api/ventaProductos/GuardaProducto/sucursal/"+sucursalId+"/origen/"+numerodispositivo+"/usuario/"+Usuarioid+"/islaId/"+posicionCarga;
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest request_json = new JsonObjectRequest(Request.Method.POST, url, mjason, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                String estado = null;
                String mensaje = null;
                try {
                    estado = response.getString("Correcto");
                    mensaje = response.getString("Mensaje");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (estado.equals("true")){
                    try {
                        JSONObject mjasonP = new JSONObject();
                        mjasonP.put("ProductoId", ProductoIdEntero);
                        myArrayArticulo.put(mjasonP);
                        AlertDialog.Builder builder = new AlertDialog.Builder(VentaProductosFaltantes.this);
                        builder.setTitle("Venta Productos");

                        builder.setMessage(mensaje);
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                producto.setText("");
                                txtdescripcion.setText("");
                                precio.setText("");
                                productoIdentificador.setText("");
                                cantidadProducto.setText("1");
                            }
                        });
                        AlertDialog dialog= builder.create();
                        dialog.show();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else{
                    try {
                        AlertDialog.Builder builder = new AlertDialog.Builder(VentaProductosFaltantes.this);
                        builder.setTitle("Venta Productos");
                        builder.setMessage(mensaje);
                        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
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
                return Response.success(mjason, HttpHeaderParser.parseCacheHeaders(response));
            }
        };
        queue.add(request_json);
    }

    private void mostrarProductosExistencias(){
        String preciol = null;
        String DescLarga;
        String idArticulo;
        String TProducto;
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
                TProducto = desglose.getString("TipoProducto");
                NombreProducto.add("ID: " + numerointerno + "    |     $"+preciol); // + "    |    " + IdProductos );
                ID.add(DescLarga);
                PrecioProducto.add(preciol);
                ClaveProducto.add(numerointerno);
                ProductosId.add(idArticulo);
                CodigoBarra.add(CBarra);
                totalProductos.add(cantidad);
                TipoProductoId.add(TProducto);
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
                tipoproductoid.setText("");

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
                    String tipoprod = TipoProductoId.get(i).toString();

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