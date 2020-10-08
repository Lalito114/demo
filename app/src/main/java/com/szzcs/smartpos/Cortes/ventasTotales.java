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
import android.widget.Button;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonArray;
import com.szzcs.smartpos.Munu_Principal;
import com.szzcs.smartpos.Pendientes.ticketPendientes;
import com.szzcs.smartpos.Productos.ListAdapterProductos;
import com.szzcs.smartpos.Productos.VentasProductos;
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

public class ventasTotales extends AppCompatActivity {
    List<String> maintitle, subtitle, calculo, cantidadEntregada, cantidadVendidos; //total
    ListView mList;
    JSONObject denom;

    String denominacion;

    ArrayList arrayMonto = new ArrayList();
    int resultado, cierreID;
    Double result;
    ImageView imgAceptar;
    JSONArray prueba2 = new JSONArray();
    double sumainter;
    int fajillaBillete;
    String EstacionId, sucursalId, ipEstacion, islaId ;
    Boolean banderaSigue;
    Button btnsiguiente;
    String numerointerno, idarticulo, descripcionarticulo;
    JSONObject mjason = new JSONObject();
    JSONArray ArrayventasFaltantes = new JSONArray();
    String posicion, usuario, numerodispositivo;
    boolean banderaValida;
    String turnoId, fechaTrabajo;
    JSONObject JOcompleto = new JSONObject();

    JSONArray ArrayResultante = new JSONArray();
    List<String> ID;
    List<String> NombreProducto;
    List<String> precio;
    List<String> ClaveProducto;
    List<String> codigobarras;
    List<String> ProductosId;
    List<String> productosVendidos;
    List<String> productosRecibidos;
    List<String> TipoProductoId;
    List<String> DescripcionPr;

    boolean banderaValidaBotonVentasFaltantes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ventas_totales);
        SQLiteBD db = new SQLiteBD(getApplicationContext());
        EstacionId = db.getIdEstacion();
        sucursalId = db.getIdSucursal();
        ipEstacion= db.getIpEstacion();
        islaId = "1"; // getIntent().getStringExtra("islaId")
        usuario  = "1"; //getIntent().getStringExtra("usuarioid");
        posicion = "1"; //getIntent().getStringExtra("car");
        numerodispositivo = "1";
        cierreID = 133; //getIntent().getStringExtra("islaId")
        turnoId = "1"; //getIntent().getStringExtra("turno");



        MostrarProductos();
        CargaProductosFaltantes();
        imgAceptar=findViewById(R.id.imgAceptar);
        imgAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                banderaValida = true;
                String valorCapturado;
                for (int k=0; k<mList.getCount(); k++){
                    valorCapturado = maintitle.get(k);
                    if (valorCapturado == "-"){
                        banderaValida = false;
                        break;
                    }
                }
                if (banderaValida == true) {

                    if (btnsiguiente.getVisibility() == View.VISIBLE) {
                        try {
                            AlertDialog.Builder builder = new AlertDialog.Builder(ventasTotales.this);
                            builder.setTitle("CORTE, Conteo Perifericos");
                            builder.setMessage("Hay diferencias entre los productos vendidos y los que entrega, oprima el botón VENTA PRODUCTOS FALTANTES")
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                        }
                                    }).show();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    } else {//Enviamos a Guardar
                        String url = "http://"+ipEstacion+"/CorpogasService/api/Turnos/fechaTrabajo/sucursal/"+sucursalId+"/turno/"+turnoId;
                        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try{
                                    JSONObject resultadorespuesta = new JSONObject(response);
                                    fechaTrabajo = resultadorespuesta.getString("ObjetoRespuesta");
                                    ObtieneArrayResultanteparaGuardar();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getApplicationContext(),error.toString(), Toast.LENGTH_LONG).show();
                            }
                        });
                        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                        requestQueue.add(stringRequest);
                    }
                }else{
                    try {
                        AlertDialog.Builder builder = new AlertDialog.Builder(ventasTotales.this);
                        builder.setTitle("CORTE, Conteo Productos");
                        builder.setMessage("Existe productos que aún no se han contabilzado")
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                    }
                                }).show();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

            }
        });

    }


    private void CargaProductosFaltantes(){
        btnsiguiente= findViewById(R.id.btnsiguiente);
        btnsiguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                banderaValida = true;
                String valorCapturado;
                for (int k=0; k<mList.getCount(); k++){
                    valorCapturado = maintitle.get(k);
                    if (valorCapturado == "-"){
                        banderaValida = false;
                        break;
                    }
                }
                if (banderaValida == true) {
                    //ObtieneArrayResultanteparaGuardar();
                    String ArregloProductosEntregados = ArrayResultante.toString();

                    Intent intent = new Intent(getApplicationContext(), ventaProductosFaltantes.class);
                    String paso = ArrayventasFaltantes.toString();
                    intent.putExtra("articulos", paso);
                    intent.putExtra("posicion", posicion);
                    intent.putExtra("usuario", usuario);
                    intent.putExtra("productosEntregados", ArregloProductosEntregados);
                    intent.putExtra("turnoId", turnoId);
                    intent.putExtra("islaId", islaId);
                    intent.putExtra("fechatrabajo", fechaTrabajo);
                    intent.putExtra("cierreId", cierreID);
                    startActivity(intent);

                }else{
                    try {
                        AlertDialog.Builder builder = new AlertDialog.Builder(ventasTotales.this);
                        builder.setTitle("CORTE, Conteo Productos");
                        builder.setMessage("Existe productos que aún no se han contabilzado")
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                    }
                                }).show();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    private void ObtieneArrayResultanteparaGuardar() throws JSONException {
        JsonArray acierrecombustibledetalle= new JsonArray();
        JsonArray acierreformapago= new JsonArray();
        JsonArray accarretes= new JsonArray();
        JsonArray acdetallecategoriaproducto= new JsonArray();
        String url = "http://"+ipEstacion+"/CorpogasService/api/cierres/GuardaCierreDetalle/usuario/"+usuario;
        RequestQueue queue = Volley.newRequestQueue(this);
        try {
            JOcompleto.put("SucursalId", Integer.parseInt(sucursalId));
            JOcompleto.put("TurnoId", Integer.parseInt(turnoId));
            JOcompleto.put("TurnoSucursalId", Integer.parseInt(sucursalId));
            JOcompleto.put("Transacciones", 0);
            JOcompleto.put("TotalVenta", 0);
            JOcompleto.put("TotalIva", 0);
            JOcompleto.put("TotalIeps", 0); //Entregada
            JOcompleto.put("Completado", true);
            JOcompleto.put("IslaId", Integer.parseInt(islaId));
            JOcompleto.put("IslaEstacionId", Integer.parseInt(EstacionId));
            JOcompleto.put("FechaTrabajo", fechaTrabajo);
            JOcompleto.put("CierreDetalles", ArrayResultante);
            JOcompleto.put("CierreCombustibleDetalles", acierrecombustibledetalle);
            JOcompleto.put("CierreFormaPagos", acierreformapago);
            JOcompleto.put("CierreCarretes", accarretes);
            JOcompleto.put("CierreDetalleCategoriaProducto", acdetallecategoriaproducto);
            JOcompleto.put("Id", cierreID);
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

    private void MostrarProductos() {
        banderaSigue= true;

        //String url = "http://"+ipEstacion+"/CorpogasService/api/islas/productos/estacion/"+EstacionId+"/posicionCargaId/"+posicion;
        String url = "http://"+ipEstacion+"/CorpogasService/api/cierres/registrar/sucursal/"+sucursalId+"/isla/"+posicion+"/usuario/"+usuario+"/origen/" + numerodispositivo;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mostrarProductosExistencias(response, posicion, usuario);
                //mostrarProductosCierre(response, posicion, usuario);
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
                        AlertDialog.Builder builder = new AlertDialog.Builder(ventasTotales.this);
                        builder.setTitle("Ventas Perifericos, CORTE");
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
        calculo = new ArrayList<String>();
        cantidadEntregada = new ArrayList<String>();

        ID = new ArrayList<String>();

        NombreProducto = new ArrayList<String>();
        precio = new ArrayList<>();
        ClaveProducto = new ArrayList();
        codigobarras = new ArrayList();
        ProductosId = new ArrayList();
        TipoProductoId = new ArrayList();

        productosVendidos = new ArrayList();
        productosRecibidos = new ArrayList();


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
                    calculo.add(cantidadrecibida);

                    ID.add(DescLarga);
                    ClaveProducto.add(idArticulo);
                    ProductosId.add(IdProductos);
                    codigobarras.add(codigobar);
                    productosVendidos.add(cantidadvendida);
                    precio.add(preciounitario);
                    productosRecibidos.add(cantidadrecibida);
                    TipoProductoId.add(TProductoId);
                    if (Integer.parseInt(cantidadvendida) > Integer.parseInt(cantidadrecibida)) {
                        cantidad = 0;
                    } else {
                        cantidad = Integer.parseInt(cantidadrecibida) - Integer.parseInt(cantidadvendida);
                    }
                    JSONObject mjasonF = new JSONObject();
                    mjasonF.put("SucursalId", Integer.parseInt(sucursalId));
                    mjasonF.put("CierreId", cierreID);
                    mjasonF.put("CierreSucursalId", Integer.parseInt(sucursalId));
                    mjasonF.put("CategoriaProductoId", Integer.parseInt(TProductoId));
                    mjasonF.put("RecursoId", Integer.parseInt(IdProductos));
                    mjasonF.put("Precio", Integer.parseInt(preciounitario));
                    mjasonF.put("Cantidad", Integer.parseInt(cantidadvendida));
                    mjasonF.put("Total", Integer.parseInt(preciounitario)*Integer.parseInt(cantidadvendida));
                    mjasonF.put("Iva", 0);
                    mjasonF.put("Ieps", 0);
                    mjasonF.put("NumeroInterno", Integer.parseInt(idArticulo));
                    mjasonF.put("CodigoBarras", codigobar);
                    mjasonF.put("ProductoDescripcion", DescLarga);
                    mjasonF.put("CantidadRecibida", Integer.parseInt(cantidadrecibida));
                    mjasonF.put("PrecioUnitarioRecibido", Integer.parseInt(preciounitario));
                    mjasonF.put("BodegaNumeroInterno", Integer.parseInt(IdProductos));
                    mjasonF.put("OrigenId", Integer.parseInt(numerodispositivo));

                    ArrayResultante.put(mjasonF);
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final ListAdapterBilletes adapterP = new ListAdapterBilletes(this,   maintitle, subtitle, calculo);
        mList=(ListView)findViewById(R.id.list);
        mList.setTextFilterEnabled(true);
        mList.setAdapter(adapterP);
//        Agregado  click en la lista
        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int position, long l) {
                String cantidadInicio = ClaveProducto.get(position).toString();
                final EditText input = new EditText(getApplicationContext());
                input.setTextColor(Color.BLACK);
                input.setGravity(Gravity.CENTER);
                input.setTextSize(22);
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                try{
                    final String  descripcionarticulo = ID.get(position).toString();
                    final String numerointerno= ClaveProducto.get(position).toString();
                    //final String articuloid= ProductosId.get(position).toString();
                    final String codbarras= codigobarras.get(position).toString();
                    final String preciou = precio.get(position).toString();
                    final String IProd = ProductosId.get(position).toString();
                    final String TProd = TipoProductoId.get(position).toString();

                    AlertDialog.Builder builder = new AlertDialog.Builder(ventasTotales.this);
                    builder.setTitle("Ingresa Cantidad \n");
                    builder.setView(input)
                            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    String denomi = input.getText().toString();
                                   // String preciou = "10";
                                    if (denomi.isEmpty()){
                                        Toast.makeText(ventasTotales.this, "Debes cargar un valor numérico", Toast.LENGTH_SHORT).show();
                                    }else {
                                        resultado = Integer.parseInt(denomi);
                                        maintitle.set(position, denomi);
                                        //total.set(position, String.valueOf(result));
                                        String tEntregados = calculo.get(position).toString();
                                        String tVendidos = productosRecibidos.get(position).toString();
                                        final int totalVendidos = Integer.parseInt(tVendidos);
                                        final int totalEntregados = Integer.parseInt(tEntregados);
                                        if (totalEntregados < resultado){
                                            Toast.makeText(ventasTotales.this, "La cantidad vendida no puede ser mayor que la que se recibió", Toast.LENGTH_SHORT).show();
                                        }else {
                                            if (resultado < (totalEntregados - totalVendidos)) {
                                                btnsiguiente.setVisibility(View.VISIBLE);
                                                String prod = ClaveProducto.get(position);
                                                int diferencia = ((totalEntregados - totalVendidos) - resultado);
                                                generaArreglo(prod, diferencia, preciou, subtitle.get(position), numerointerno, codbarras, IProd, TProd);
                                                //valido si hay diferencias
                                                banderaValidaBotonVentasFaltantes= true;
                                                for (int g= 0; g< mList.getCount(); g++)
                                                {
                                                    String valorCapturado;
                                                    valorCapturado = maintitle.get(g);
                                                    if (valorCapturado == "-"){
                                                        banderaValidaBotonVentasFaltantes = false;
                                                        break;
                                                    }else {

                                                        int entrega = Integer.parseInt(maintitle.get(g));
                                                        int recibido = Integer.parseInt(calculo.get(g));
                                                        int vendido = Integer.parseInt(productosVendidos.get(g));
                                                        if (entrega < (recibido - vendido)) {
                                                            banderaValidaBotonVentasFaltantes = false;
                                                            break;
                                                        }
                                                    }
                                                }
                                                if  (banderaValidaBotonVentasFaltantes == false){
                                                    btnsiguiente.setVisibility(View.VISIBLE);
                                                }else{
                                                    btnsiguiente.setVisibility(View.INVISIBLE);
                                                }
                                                ListAdapterBilletes adapter = new ListAdapterBilletes(ventasTotales.this, maintitle, subtitle, calculo);
                                                mList.setAdapter(adapter);
                                            }else{
                                                Toast.makeText(ventasTotales.this, "La cantidad capturada es mayor que el resultado de productos entregados menos las ventas", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }
                                }
                            })
                            .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            }).show();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void mostrarProductosExistencias(String response, final String posicion, final String usuario){
        String TProductoId;

        maintitle = new ArrayList<String>();
        subtitle = new ArrayList<String>();
        calculo = new ArrayList<String>();
        cantidadEntregada = new ArrayList<String>();

        ID = new ArrayList<String>();

        NombreProducto = new ArrayList<String>();
        precio = new ArrayList<>();
        ClaveProducto = new ArrayList();
        codigobarras = new ArrayList();
        ProductosId = new ArrayList();
        TipoProductoId = new ArrayList();
        DescripcionPr = new ArrayList();;

        productosVendidos = new ArrayList<>();
        productosRecibidos = new ArrayList();


        try {
            JSONObject p1 = new JSONObject(response);

            String ni = p1.getString("NumeroInterno");
            String bodega = p1.getString("Bodega");
            JSONObject ps = new JSONObject(bodega);
            String producto = ps.getString("BodegaProductos");
            JSONArray bodegaprod = new JSONArray(producto);

            for (int i = 21; i <25 ; i++){ //bodegaprod.length()
                String IdProductos = null;
                JSONObject pA = bodegaprod.getJSONObject(i);
                String ExProductos=pA.getString("Existencias");
                String productoclave = pA.getString("Producto");
                JSONObject prod = new JSONObject(productoclave);
                TProductoId="2"; //prod.getString("TipoSatProductoId");
                String DescLarga=prod.getString("DescripcionLarga");
                String idArticulo=prod.getString("NumeroInterno");
                //String preciounitario=prod.getString("preciounitario");
                String codigobar=prod.getString("CodigoBarras");


                String cantidadvendida="2";
                String cantidadrecibida=prod.getString("NumeroInterno");
                IdProductos=prod.getString("Id");
                String PControl=prod.getString("ProductoControles");
                JSONArray PC = new JSONArray(PControl);
                String preciou = "10";

                maintitle.add("-");
                subtitle.add(DescLarga);
                calculo.add(idArticulo);

                ID.add(DescLarga);
                ClaveProducto.add(idArticulo);
                ProductosId.add(IdProductos);
                productosVendidos.add("2");
                codigobarras.add(codigobar);
                precio.add(preciou);
                TipoProductoId.add(TProductoId);
                DescripcionPr.add(DescLarga);
                productosRecibidos.add(idArticulo);




                int cantidad = Integer.parseInt(cantidadrecibida)-Integer.parseInt(cantidadvendida);


                JSONObject mjasonF = new JSONObject();
                mjasonF.put("SucursalId", Integer.parseInt(sucursalId));
                mjasonF.put("CierreId", cierreID);
                mjasonF.put("CierreSucursalId", Integer.parseInt(sucursalId));
                mjasonF.put("CategoriaProductoId", Integer.parseInt(TProductoId));
                mjasonF.put("RecursoId", Integer.parseInt(IdProductos));
                mjasonF.put("Precio", Integer.parseInt(preciou));
                mjasonF.put("Cantidad", Integer.parseInt(cantidadvendida)); // Cantidad a Entregar
                mjasonF.put("Total", Integer.parseInt(preciou)*Integer.parseInt(cantidadvendida));
                mjasonF.put("Iva", 0);
                mjasonF.put("Ieps", 0);
                mjasonF.put("NumeroInterno", Integer.parseInt(idArticulo));
                mjasonF.put("CodigoBarras", codigobar);
                mjasonF.put("ProductoDescripcion", DescLarga);
                mjasonF.put("CantidadRecibida", Integer.parseInt(cantidadrecibida));
                mjasonF.put("PrecioUnitarioRecibido", Integer.parseInt(preciou));
                mjasonF.put("BodegaNumeroInterno", Integer.parseInt(IdProductos));
                mjasonF.put("OrigenId", Integer.parseInt(numerodispositivo));

                ArrayResultante.put(mjasonF);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final ListAdapterBilletes adapterP = new ListAdapterBilletes(this,   maintitle, subtitle, calculo);
        mList=(ListView)findViewById(R.id.list);
        mList.setTextFilterEnabled(true);
        mList.setAdapter(adapterP);
//        Agregado  click en la lista
        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int position, long l) {
                String cantidadInicio = ClaveProducto.get(position).toString();
                final EditText input = new EditText(getApplicationContext());
                input.setTextColor(Color.BLACK);
                input.setGravity(Gravity.CENTER);
                input.setTextSize(22);
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                try{
                    final String  descripcionarticulo = ID.get(position).toString();
                    final String numerointerno= ClaveProducto.get(position).toString();
                    //final String articuloid= ProductosId.get(position).toString();
                    final String codbarras= codigobarras.get(position).toString();
                    //final String preciou = precio.get(position).toString();
                    final String IProd = ProductosId.get(position).toString();
                    final String TProd = TipoProductoId.get(position).toString();


                    AlertDialog.Builder builder = new AlertDialog.Builder(ventasTotales.this);
                    builder.setTitle("Ingresa Cantidad \n");
                    builder.setView(input)
                            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    String denomi = input.getText().toString();
                                    String preciou = "10";
                                    resultado = Integer.parseInt(denomi);
                                    maintitle.set(position, denomi);
                                    //total.set(position, String.valueOf(result));
                                    String  tEntregados = calculo.get(position).toString();
                                    String  tVendidos = "2"; //cantidadEntregada.get(position).toString();
                                    final int totalVendidos = Integer.parseInt(tVendidos);
                                    final int totalEntregados = Integer.parseInt(tEntregados);
                                    if (resultado < (totalEntregados - totalVendidos)) {
                                        btnsiguiente.setVisibility(View.VISIBLE);
                                        String prod = ClaveProducto.get(position);
                                        int diferencia = ((totalEntregados - totalVendidos) - resultado);
                                        generaArreglo(prod, diferencia, preciou,  subtitle.get(position), numerointerno, codbarras, IProd, TProd);
                                    }
                                    //valido si hay diferencias
                                    banderaValidaBotonVentasFaltantes= true;
                                    for (int g= 0; g< mList.getCount(); g++)
                                    {
                                        String valorCapturado;
                                        valorCapturado = maintitle.get(g);
                                        if (valorCapturado == "-"){
                                            banderaValidaBotonVentasFaltantes = false;
                                            break;
                                        }else {

                                            int entrega = Integer.parseInt(maintitle.get(g));
                                            int recibido = Integer.parseInt(calculo.get(g));
                                            int vendido = Integer.parseInt(productosVendidos.get(g));
                                            if (entrega < (recibido - vendido)) {
                                                banderaValidaBotonVentasFaltantes = false;
                                                break;
                                            }
                                        }
                                    }
                                    if  (banderaValidaBotonVentasFaltantes == false){
                                        btnsiguiente.setVisibility(View.VISIBLE);
                                    }else{
                                        btnsiguiente.setVisibility(View.INVISIBLE);
                                    }

                                    ListAdapterBilletes adapter = new ListAdapterBilletes(ventasTotales.this, maintitle, subtitle, calculo);
                                    mList.setAdapter(adapter);
                                }
                            })
                            .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            }).show();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

private void generaArreglo(String numeroproducto, int cantidad, String preciounitario, String descCorta, String internonumero, String codBarras, String IProducto, String TProducto){
        JSONObject mjason = new JSONObject();
    try {
        mjason.put("TipoProducto",TProducto);
        mjason.put("ProductoId", numeroproducto);
        //ventaRestante.put("CodigoBarras", codigobarras);
        mjason.put("NumeroInterno", internonumero);
        mjason.put("Cantidad", cantidad);
        mjason.put("Precio", preciounitario);
        mjason.put ("DescCorta", descCorta);
        mjason.put("CodigoBarras", codBarras);

        ArrayventasFaltantes.put(mjason);

        //valido si hay




    } catch (JSONException e) {
        e.printStackTrace();
    }

}




    private void denominacionBilletes() {
        String url = "http://10.2.251.58/CorpogasService/api/Denominaciones";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                subtotalOficinaBilletes(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();

            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);

    }
    private void subtotalOficinaBilletes(String response) {
        maintitle = new ArrayList<String>();
        subtitle = new ArrayList<String>();
        calculo = new ArrayList<String>();
        try {

            JSONArray stl = new JSONArray(response);

            for (int i = 0; i < stl.length(); i++) {
                denom = stl.getJSONObject(i);
                denominacion = denom.getString("Importe");
                double monto = Double.parseDouble(denominacion);
                arrayMonto.add(monto);

                maintitle.add("0");
                subtitle.add(denominacion);
                calculo.add("");

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        final ListAdapterBilletes adapter = new ListAdapterBilletes(this, maintitle, subtitle, calculo);
        mList = (ListView) findViewById(R.id.list);
        mList.setAdapter(adapter);

        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                final String ray = arrayMonto.get(position).toString();

                try {
                    final EditText input = new EditText(getApplicationContext());
                    input.setTextColor(Color.BLACK);
                    input.setGravity(Gravity.CENTER);
                    input.setTextSize(22);
                    input.setInputType(InputType.TYPE_CLASS_NUMBER);

                    AlertDialog.Builder builder = new AlertDialog.Builder(ventasTotales.this);
                    builder.setTitle("Ingresa Cantidad \n");
                    builder.setView(input)

                            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    String denomi = input.getText().toString();
                                    result = Double.parseDouble(denomi) * Double.parseDouble(ray);
                                    if (result > fajillaBillete) {
                                        Toast.makeText(ventasTotales.this, "No puedes superar el valor de 1 Fajilla", Toast.LENGTH_SHORT).show();
                                    } else {
                                        maintitle.set(position, denomi);
                                        calculo.set(position, String.valueOf(result));
                                        final String ray2 = result.toString();

                                        sumainter = totalBilletes() + Double.parseDouble(ray2);

                                        if ((totalBilletes()<= (fajillaBillete-10)) && (sumainter <= (fajillaBillete-10))){
                                            Toast.makeText(ventasTotales.this, "Cantidad Agregada", Toast.LENGTH_SHORT).show();
                                            prueba2.put(ray2);
                                        }else{
                                            Toast.makeText(ventasTotales.this, "Los Valores que ingresaste pueden ser 1 fajilla", Toast.LENGTH_SHORT).show();
                                        }
                                        ListAdapterBilletes adapter = new ListAdapterBilletes(ventasTotales.this, maintitle, subtitle, calculo);
                                        mList.setAdapter(adapter);
                                    }
                                }
                            })
                            .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            }).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    public double totalBilletes() {
        double sumamax = 0;
        for (int i = 0; i < prueba2.length() ; i++) {
            double suma = 0;
            try {
                suma = prueba2.getDouble(i);
                sumamax += suma;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return sumamax;
    }

}