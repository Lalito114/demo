    package com.szzcs.smartpos.Productos;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
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
import com.szzcs.smartpos.R;
import com.szzcs.smartpos.configuracion.SQLiteBD;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Integer.parseInt;

    public class VentasProductos extends AppCompatActivity{
    //Declaracion de Variables
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    //Declaracion de objetos
    Button btnAgregar,btnEnviar, incrementar, decrementar, comprar;
    TextView cantidadProducto, txtDescripcion, NumeroProductos, precio, existencias;
    EditText Producto;
    String cantidad;
    JSONObject mjason = new JSONObject();
    JSONArray myArray = new JSONArray();
    String EstacionId, sucursalId;
    ListView list;
    Integer ProductosAgregados = 0;
    String posicion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ventas_productos);
        //instruccion para que aparezca la flecha de regreso
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final String posicion;
        posicion = getIntent().getStringExtra("car");

        SQLiteBD db = new SQLiteBD(getApplicationContext());
        EstacionId = db.getIdEstacion();
        sucursalId = db.getIdSucursal();

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
        txtDescripcion = findViewById(R.id.tv1);
        precio = findViewById(R.id.precio);
        existencias = findViewById(R.id.existencias);
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
        String  ProductoId;
        int TotalProducto;
        int ProductoIdEntero;

        TotalProducto = Integer.parseInt(cantidadProducto.getText().toString());
        String PrecioMonto = precio.getText().toString();
        Double precioUnitario =  Double.valueOf(PrecioMonto);
        ProductoId = Producto.getText().toString();
        ProductoIdEntero = Integer.parseInt(ProductoId);
        if (ProductoId.isEmpty())
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
                    mjason.put("ProductoId", ProductoIdEntero);
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


            } catch (JSONException error) {
            }
        }
    }

    private void MostrarProductos() {
        final String posicion;
        posicion = getIntent().getStringExtra("car");
        String url = "http://10.2.251.58/CorpogasService/api/islas/productos/estacion/"+EstacionId+"/posicionCargaId/"+posicion;
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

        //ArrayList<singleRow> singlerow = new ArrayList<>();
        try {
            JSONObject p1 = new JSONObject(response);

            String ni = p1.getString("NumeroInterno");
            String bodega = p1.getString("Bodega");
            JSONObject ps = new JSONObject(bodega);
            String producto = ps.getString("BodegaProductos");
            JSONArray bodegaprod = new JSONArray(producto);

            for (int i = 0; i <bodegaprod.length() ; i++){
                JSONObject pA = bodegaprod.getJSONObject(i);
                String ExProductos=pA.getString("Existencias");
                ExistenciaProductos.add(ExProductos);
                String productoclave = pA.getString("Producto");
                JSONObject prod = new JSONObject(productoclave);
                DescLarga=prod.getString("DescripcionLarga");
                idArticulo=prod.getString("NumeroInterno");
                String PControl=prod.getString("ProductoControles");
                JSONArray PC = new JSONArray(PControl);
                for (int j = 0; j <PC.length() ; j++) {
                    JSONObject Control = PC.getJSONObject(j);
                    preciol  = Control.getString("Precio");
                }
                NombreProducto.add("ID: " + idArticulo + "    |     $"+preciol);
                ID.add(DescLarga);
                PrecioProducto.add(preciol);
                ClaveProducto.add(idArticulo);
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
                String  Descripcion = ID.get(i).toString();
                String precioUnitario = PrecioProducto.get(i).toString();
                String paso= ClaveProducto.get(i).toString();
                String existencia = ExistenciaProductos.get(i).toString();

                Producto.setText(paso);
                txtDescripcion.setText(Descripcion);
                precio.setText(precioUnitario);
                existencias.setText(existencia);
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


    private void EnviarProductos(final String posicionCarga, final String Usuarioid) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://10.2.251.58/CorpogasService/api/ventaProductos/sucursal/"+sucursalId+"/procedencia/"+posicionCarga+"/tipoTransaccion/1/empleado/"+Usuarioid; //TipoTransaccion 1 (NORMAL)
        queue = Volley.newRequestQueue(this);

        JsonArrayRequest request_json = new JsonArrayRequest(Request.Method.POST, url, myArray,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        //Get Final response
                        ////Intent intent = new Intent(getApplicationContext(), formapagoProducto.class);
                        //DAtos enviados a formaPago posicion de carga y usuario
                        ////intent.putExtra("posicion",posicionCarga);
                        ////intent.putExtra("usuario",Usuarioid);
                        ////startActivity(intent);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                VolleyLog.e("Error: ", volleyError.getMessage());
                Intent intent = new Intent(getApplicationContext(), productoFormapago.class);
                intent.putExtra("posicion",posicionCarga);
                intent.putExtra("usuario",Usuarioid);
                startActivity(intent);

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
                        Intent intent = new Intent(getApplicationContext(), productoFormapago.class);
                        //DAtos enviados a formaPago
                        intent.putExtra("posicion",posicionCarga);
                        intent.putExtra("usuario",Usuarioid);
                        //SE envía json con los productos seleccionados
                        //Gson gson = new Gson();
                        //String myJson = myArray.toString();
                        //intent.putExtra("myjson", myArray.toString());
                        startActivity(intent);
                        //Toast.makeText(getApplicationContext(), "Venta realizada", Toast.LENGTH_LONG).show();
                        //(array).put(obj);
                    } catch (Exception ex) {
                        Toast.makeText(getApplicationContext(), ex.toString(), Toast.LENGTH_LONG).show();
                    }
                }
                //return array;
                return Response.success(myArray, HttpHeaderParser.parseCacheHeaders(response));
            }
        };
        queue.add(request_json);
    }


    private void AgregarDespacho(final String posicionCarga, final String Usuarioid){
        String url = "http://10.2.251.58/CorpogasService/api/despachos/sucursal/"+sucursalId+"/utlima/posicionCarga/"+posicionCarga;
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
                        JSONObject mjason = new JSONObject();
                        mjason.put("ProductoId", ProductoIdEntero);
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


 //   private void CrearJSON() {
 //       btnAgregar = findViewById(R.id.btnAgregar);
 //       btnAgregar.setOnClickListener(new View.OnClickListener() {
 //           @Override
 //           public void onClick(View v) {
 //              String  ProductoId;
    //              int TotalProducto = 0;
//                TotalProducto = Integer.parseInt(cantidadProducto.getText().toString());
//                ProductoId = Producto.getText().toString();
//                if (ProductoId.isEmpty())
//                {
//                    Toast.makeText(getApplicationContext(), "Seleccione uno de los Productos", Toast.LENGTH_LONG).show();
//                }
//                else{
//                    try {
//                        mjason.put("cantidad", TotalProducto);
//                        mjason.put("producto", ProductoId);
//                        ProductosAgregados = ProductosAgregados +1;
//                    } catch (JSONException error) {
//                    }
//                }
//            }
//        });
//    }

}