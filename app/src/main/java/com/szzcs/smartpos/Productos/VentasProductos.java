    package com.szzcs.smartpos.Productos;

import android.content.DialogInterface;
import android.content.Intent;
import android.security.keystore.SecureKeyImportUnavailableException;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.szzcs.smartpos.Munu_Principal;
import com.szzcs.smartpos.Pendientes.ticketPendientes;
import com.szzcs.smartpos.R;
import com.szzcs.smartpos.configuracion.SQLiteBD;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.StringBufferInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Integer.parseInt;
import org.json.JSONObject;
import org.json.JSONException;

import devliving.online.mvbarcodereader.MVBarcodeScanner;

    public class VentasProductos extends AppCompatActivity implements View.OnClickListener {
    //Declaracion de Variables
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    //Declaracion de objetos
    Button btnAgregar,btnEnviar, incrementar, decrementar, comprar;
    TextView cantidadProducto, txtDescripcion, NumeroProductos, precio, existencias, productoIdentificador;
    EditText Producto, tipoproductoid;
    String cantidad;
    JSONObject mjason = new JSONObject();
    JSONArray myArray = new JSONArray();
    String EstacionId, sucursalId, ipEstacion, tipoTransaccion, numerodispositivo ;
    ListView list;
    Integer ProductosAgregados = 0;
    String posicion, usuario;
    String transaccionId;


    private ImageButton b_auto, btnbuscar;
    private MVBarcodeScanner.ScanningMode modo_Escaneo;
    private TextView text_cod_escaneado;
    private int CODE_SCAN = 1;

    List<String> ID;
    List<String> NombreProducto;
    List<String> PrecioProducto;
    List<String> ClaveProducto;
    List<String> codigoBarras;
    List<String> ExistenciaProductos;
    List<String> ProductosId;
    List<String> TipoProductoId;
    List<String> DescripcionPr;

        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ventas_productos);
        //instruccion para que aparezca la flecha de regreso
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SQLiteBD db = new SQLiteBD(getApplicationContext());
        EstacionId = db.getIdEstacion();
        sucursalId = db.getIdSucursal();
        ipEstacion= db.getIpEstacion();
        tipoTransaccion = "1"; //Transaccion Normal
        numerodispositivo = "1";


        comprar=findViewById(R.id.comprar);
        comprar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //se asignan valores
                final String posicion;
                posicion = getIntent().getStringExtra("car");
                final String usuarioid;
                usuarioid = getIntent().getStringExtra("user");
                if (myArray.length()==0  )       //.length() >0)
                {
                    Toast.makeText(getApplicationContext(), "Seleccione al menos uno de los Productos", Toast.LENGTH_LONG).show();
                } else {
                AgregarDespacho(posicion, usuarioid);
                //EnviarProductos(posicion, usuarioid);
                }
            }
        });
        btnEnviar = findViewById(R.id.btnEnviar);
        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Valida si se ha agregado productos al arreglo
                if (myArray.length()==0  )       //.length() >0)
                {
                    Toast.makeText(getApplicationContext(), "Seleccione al menos uno de los Productos", Toast.LENGTH_LONG).show();
                } else {

                    EnviarDatos();
                }
            }
        });
        btnAgregar = findViewById(R.id.btnAgregar);
        btnAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //procedimiento para agregar un producto al arreglo
                AgregarProducto();
                //CrearJSON();
            }
        });


        incrementar = findViewById(R.id.incrementar);
        incrementar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Aumentar();
            }
        });
        decrementar= findViewById(R.id.decrementar);
        decrementar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Decrementar();
            }
        });
        //procedimiento para inicializar variables
        CantidadProducto();
        //procedimiento que despliega la lista de productos
        MostrarProductos();
        UI();

        btnbuscar = findViewById(R.id.btnbuscar);
        btnbuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buscaCodigoInterno(Producto.getText().toString());
            }
        });

    }

        private void UI() {
            b_auto = findViewById(R.id.btnscanner);
            b_auto.setOnClickListener(this);
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
            int indicecodigo = codigoBarras.indexOf(rawValue);
            if (indicecodigo > 0 ) {
                String  Descripcion = ID.get(indicecodigo);
                String precioUnitario = PrecioProducto.get(indicecodigo);
                String paso= ClaveProducto.get(indicecodigo);
                String existencia = ExistenciaProductos.get(indicecodigo);
                String idproduc = ProductosId.get(indicecodigo);


                Producto.setText(paso);
                txtDescripcion.setText(Descripcion);
                precio.setText(precioUnitario);
                existencias.setText(existencia);
                productoIdentificador.setText(idproduc);
            }else{
                Toast.makeText(getApplicationContext(), "Producto no encontrado en la lista", Toast.LENGTH_LONG).show();
                Producto.setText("");
                txtDescripcion.setText("");
                precio.setText("");
                //existencias.setText(existencia);
                productoIdentificador.setText("");
                cantidadProducto.setText("1");
            }
        }

        private void buscaCodigoInterno(String valor){
            int indicecodigo = ClaveProducto.indexOf(valor);
            if (indicecodigo > 0 ) {
                String Descripcion = ID.get(indicecodigo);
                String precioUnitario = PrecioProducto.get(indicecodigo);
                //String paso= ClaveProducto.get(indicecodigo);
                String existencia = ExistenciaProductos.get(indicecodigo);
                String idproduc = ProductosId.get(indicecodigo);
                //String codigo = codigoBarras.get(indicecodigo);

                //Producto.setText(paso);
                txtDescripcion.setText(Descripcion);
                precio.setText(precioUnitario);
                existencias.setText(existencia);
                productoIdentificador.setText(idproduc);
            }else{
                Toast.makeText(getApplicationContext(), "Producto no encontrado en la lista", Toast.LENGTH_LONG).show();
                Producto.setText("");
                txtDescripcion.setText("");
                precio.setText("");
                //existencias.setText(existencia);
                productoIdentificador.setText("");
                cantidadProducto.setText("1");
            }
        }


        private void EnviarDatos() {
        //Si es valido se asignan valores
        final String posicion;
        posicion = getIntent().getStringExtra("car");
        final String usuarioid;
        usuarioid = getIntent().getStringExtra("user");

        //EnviarProductos(posicion, usuarioid);
        AgregarDespacho(posicion, usuarioid);
        //Se instancia y se llama a la clase VentaProductos
        Intent intent = new Intent(getApplicationContext(), formapagoProducto.class);
        //DAtos enviados a formaPago
        intent.putExtra("posicion",posicion);
        intent.putExtra("usuario",usuarioid);
        //SE envía json con los productos seleccionados
        //Gson gson = new Gson();
        String myJson = myArray.toString();
        intent.putExtra("myjson", myArray.toString());
        startActivity(intent);
    }

    private void CantidadProducto() {
        cantidadProducto = findViewById(R.id.cantidadProducto);
        Producto= findViewById(R.id.Producto);
        cantidad = cantidadProducto.toString();
        txtDescripcion = findViewById(R.id.txtDescripcion);
        precio = findViewById(R.id.precio);
        existencias = findViewById(R.id.existencias);
        productoIdentificador = findViewById(R.id.productoIdentificador);
        tipoproductoid = findViewById(R.id.tipoproductoid);
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

    private void AgregarProducto(){
        String resultado  = "";
        //EditText cantidadProducto = (EditText)getActivity().findViewById();
        String  TipoProductoId;
        String  ProductoId;
        String  numInterno;
        String  descrProducto;
        int TotalProducto;
        int ProductoIdEntero;

        TotalProducto = Integer.parseInt(cantidadProducto.getText().toString());
        String PrecioMonto = precio.getText().toString();
        Double precioUnitario =  Double.valueOf(PrecioMonto);
        //ProductoId = Producto.getText().toString();
        ProductoId = productoIdentificador.getText().toString();
        ProductoIdEntero = Integer.parseInt(ProductoId);
        numInterno = Producto.getText().toString();
        descrProducto = txtDescripcion.getText().toString();
        TipoProductoId = tipoproductoid.getText().toString();
        //if (ProductoId.isEmpty())
        if (txtDescripcion.length() == 0)
        {
            Toast.makeText(getApplicationContext(), "Seleccione uno de los Productos", Toast.LENGTH_LONG).show();
        }
        else{
            try {
                boolean bandera=true;
                if (myArray.length()>0  ) {
                    for (int i = 0; i < myArray.length(); i++) {
                        try {
                            JSONObject jsonObject = myArray.getJSONObject(i);
                            if (jsonObject.has("ProductoId")) {
                                String valor = jsonObject.getString("ProductoId");
                                int res = Integer.parseInt(valor);
                                if (res==ProductoIdEntero){
                                    bandera=false;
                                    break;
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                if (bandera==true) {
                    JSONObject mjason = new JSONObject();
                    mjason.put("TipoProducto", Integer.parseInt(TipoProductoId));
                    mjason.put("ProductoId", ProductoIdEntero);
                    mjason.put("NumeroInterno", Integer.parseInt(numInterno));
                    //mjason.put("Descripcion", descrProducto.toString());
                    mjason.put("Cantidad", TotalProducto);
                    mjason.put("Precio", precioUnitario);
                    myArray.put(mjason);
                    ProductosAgregados = +ProductosAgregados;
                }else{
                    Toast.makeText(getApplicationContext(), "Producto: "+ ProductoId+" cargado anteriormente"  , Toast.LENGTH_LONG).show();
                }
                Producto.setText("");
                txtDescripcion.setText("");
                cantidadProducto.setText("1");
                precio.setText("");
                existencias.setText("");
                productoIdentificador.setText("");


            } catch (JSONException error) {
            }
        }
    }

    private void MostrarProductos() {
        final String posicion;
        posicion = getIntent().getStringExtra("car");
        String url = "http://"+ipEstacion+"/CorpogasService/api/islas/productos/estacion/"+EstacionId+"/posicionCargaId/"+posicion;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //mostarProductor(response);
                mostrarProductosExistencias(response);
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

    private void mostrarProductosExistencias(String response){
        //Declaracion de variables
        String preciol = null;
        String DescLarga;
        String idArticulo;
        String TProductoId;

        ID = new ArrayList<String>();

        NombreProducto = new ArrayList<String>();
        PrecioProducto = new ArrayList<>();
        ClaveProducto = new ArrayList();
        codigoBarras = new ArrayList();
        ExistenciaProductos = new ArrayList();
        ProductosId = new ArrayList();
        TipoProductoId = new ArrayList();
        DescripcionPr = new ArrayList();;


        //ArrayList<singleRow> singlerow = new ArrayList<>();
        try {
            JSONObject p1 = new JSONObject(response);

            String ni = p1.getString("NumeroInterno");
            String bodega = p1.getString("Bodega");
            JSONObject ps = new JSONObject(bodega);
            String producto = ps.getString("BodegaProductos");
            JSONArray bodegaprod = new JSONArray(producto);

            for (int i = 0; i <bodegaprod.length() ; i++){
                String IdProductos = null;
                JSONObject pA = bodegaprod.getJSONObject(i);
                String ExProductos=pA.getString("Existencias");
                ExistenciaProductos.add(ExProductos);
                String productoclave = pA.getString("Producto");
                JSONObject prod = new JSONObject(productoclave);
                TProductoId="2";//prod.getString("TipoSatProductoId");
                DescLarga=prod.getString("DescripcionLarga");
                idArticulo=prod.getString("NumeroInterno");
                String codigobarras=prod.getString("CodigoBarras");
                String PControl=prod.getString("ProductoControles");
                JSONArray PC = new JSONArray(PControl);
                for (int j = 0; j <PC.length() ; j++) {
                    JSONObject Control = PC.getJSONObject(j);
                    preciol  = Control.getString("Precio");
                    IdProductos=Control.getString("Id");
                }
                //if (IdProductos.equals("null")){
                //    ProductosId.add("0");
                //    preciol  = "0";
                //}
                NombreProducto.add("ID: " + idArticulo + "    |     $"+preciol); // + "    |    " + IdProductos );
                ID.add(DescLarga);
                PrecioProducto.add(preciol);
                ClaveProducto.add(idArticulo);
                ProductosId.add(IdProductos);
                codigoBarras.add(codigobarras);
                TipoProductoId.add(TProductoId);
                DescripcionPr.add(DescLarga);
            }

            //JSONArray productos = new JSONArray(response);
            //for (int i = 0; i <productos.length() ; i++) {
            //    JSONObject p1 = productos.getJSONObject(i);
            //    String idArticulo = p1.getString("IdArticulo");
            //    String DesLarga = p1.getString("DescLarga");
            //    String precio = p1.getString("Precio");
            //    NombreProducto.add("ID: " + idArticulo + "    |     $"+precio);
            //    ID.add(DesLarga);
            //    PrecioProducto.add(precio);
            //    ClaveProducto.add(idArticulo);
            //}
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
                String  Descripcion = ID.get(i).toString();
                String precioUnitario = PrecioProducto.get(i).toString();
                String paso= ClaveProducto.get(i).toString();
                String existencia = ExistenciaProductos.get(i).toString();
                String IProd = ProductosId.get(i).toString();
                String TProd = TipoProductoId.get(i).toString();

                Producto.setText(paso);
                txtDescripcion.setText(Descripcion);
                precio.setText(precioUnitario);
                existencias.setText(existencia);
                productoIdentificador.setText(IProd);
                tipoproductoid.setText(TProd);
            }
        });

    }

    private void mostarProductor(String response) {
        //Declaracion de variables
        final List<String> ID;
        ID = new ArrayList<String>();

        final List<String> NombreProducto;
        NombreProducto = new ArrayList<String>();

        final List<String> PrecioProducto;
        PrecioProducto = new ArrayList<>();

        final List<String> ClaveProducto;
        ClaveProducto = new ArrayList();
        //ArrayList<singleRow> singlerow = new ArrayList<>();

        try {
            JSONArray productos = new JSONArray(response);
            for (int i = 0; i <productos.length() ; i++) {
                JSONObject p1 = productos.getJSONObject(i);
                String idArticulo = p1.getString("IdArticulo");
                String DesLarga = p1.getString("DescLarga");
                String precio = p1.getString("Precio");
                NombreProducto.add("ID: " + idArticulo + "    |     $"+precio);
                ID.add(DesLarga);
                PrecioProducto.add(precio);
                ClaveProducto.add(idArticulo);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final ListAdapterProductos adapterP = new ListAdapterProductos(this,  ID, NombreProducto);
        list=(ListView)findViewById(R.id.list);
        list.setTextFilterEnabled(true);
        list.setAdapter(adapterP);
        Producto.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //adapterP.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
//        Agregado  click en la lista
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
               String  Descripcion = ID.get(i).toString();
               String precioUnitario = PrecioProducto.get(i).toString();
               String paso= ClaveProducto.get(i).toString();
               Producto.setText(paso);
               txtDescripcion.setText(Descripcion);
               precio.setText(precioUnitario);
            }
        });
    }

    private void enviarAFormasPago(final String posicionCarga, final String UsuarioVenta){

    }

    private void EnviarProductos(final String posicionCarga, final String Usuarioid) {
        //RequestQueue queue = Volley.newRequestQueue(this);
        //String url = "http://"+ipEstacion+"/CorpogasService/api/ventaProductos/sucursal/"+sucursalId+"/procedencia/"+posicionCarga+"/tipoTransaccion/"+tipoTransaccion+"/empleado/"+Usuarioid; //TipoTransaccion 1 (NORMAL)
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
                        AlertDialog.Builder builder = new AlertDialog.Builder(VentasProductos.this);
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


    private void AgregarDespacho(final String posicionCarga, final String Usuarioid){
        String url = "http://"+ipEstacion+"/CorpogasService/api/despachos/sucursal/"+sucursalId+"/utlima/posicionCarga/"+posicionCarga;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject respuesta = null;
                try {
                    respuesta = new JSONObject(response);
                    String correcto = respuesta.getString("Correcto");
                    if (correcto.equals("false")) {//Aqui debe ser true o false
                    }else{
                        String objetoRespuesta = respuesta.getString("ObjetoRespuesta");
                        JSONObject oRespuesta = new JSONObject(objetoRespuesta);
                        Integer ProductoIdEntero = Integer.parseInt(oRespuesta.getString("CombustibleId"));
                        Double TotalProducto = Double.parseDouble(oRespuesta.getString("Litros"));
                        Double Precio = Double.parseDouble(oRespuesta.getString("Precio"));
                        //Double PrecioUnitario = TotalProducto * Precio;
                        //String PrecioUnitario =  PrecioU.toString();
                        Double PrecioUnitario = Double.parseDouble(oRespuesta.getString("Importe"));
                        JSONObject combustible = new JSONObject(objetoRespuesta);
                        int  tProdId = 1; //Integer.parseInt(combustible.getString("TipoSatCombustibleId"));
                        String numInterno = combustible.getString("CodigoFranquicia");
                        //String descrProducto = combustible.getString("DescripcionLarga");
                        JSONObject mjason = new JSONObject();

                        mjason.put("TipoProducto", tProdId);
                        mjason.put("ProductoId", ProductoIdEntero);
                        mjason.put("NumeroInterno", numInterno);
                        //mjason.put("Descripcion", descrProducto);
                        mjason.put("Cantidad", TotalProducto);
                        mjason.put("Precio", PrecioUnitario);
                        myArray.put(mjason);
                    }
                    EnviarProductos(posicionCarga, Usuarioid);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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



}