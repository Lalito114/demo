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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.szzcs.smartpos.Munu_Principal;
import com.szzcs.smartpos.Pendientes.ticketPendientes;
import com.szzcs.smartpos.Productos.ListAdapterProductos;
import com.szzcs.smartpos.Productos.VentasProductos;
import com.szzcs.smartpos.R;
import com.szzcs.smartpos.configuracion.SQLiteBD;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ventasTotales extends AppCompatActivity {
    List<String> maintitle, subtitle, total, cantidadEntregada, cantidadVendidos;
    ListView mList;
    JSONObject denom;
    String denominacion;
    ArrayList arrayMonto = new ArrayList();
    int resultado;
    Double result;
    ImageView imgAceptar;
    JSONArray prueba2 = new JSONArray();
    double sumainter;
    String precioFajilla;
    int fajillaBillete;
    String EstacionId, sucursalId, ipEstacion, islaId ;
    Boolean banderaSigue;
    Button btnsiguiente;
    String numerointerno, idarticulo, descripcionarticulo;
    JSONObject mjason = new JSONObject();
    JSONArray ArrayventasFaltantes = new JSONArray();
    String posicion, usuario;
    boolean banderaValida;

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

        MostrarProductos();
        CargaProductosFaltantes();
        imgAceptar=findViewById(R.id.imgAceptar);
        imgAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

    private void ObtieneArrayResultanteparaGuardar(){
        for (int prod=0; prod<mList.getCount(); prod++){

            //String  Descripcion = ID.get(prod);
            //String precioUnitario = PrecioProducto.get(prod);
            String numerointerno= ClaveProducto.get(prod);
            //String existencia = ExistenciaProductos.get(prod);
            String idproduc = ProductosId.get(prod);
            //String codigo = codigoBarras.get(prod);
            String pVendidos = productosVendidos.get(prod);
            String pRecibidos = productosRecibidos.get(prod);
            int cantidad = Integer.parseInt(pRecibidos)-Integer.parseInt(pVendidos);
            JSONObject mjasonF = new JSONObject();
            try {
                mjasonF.put("ProductoId", idproduc);
                mjasonF.put("ProductosEntregados", cantidad);
                //mjasonF.put ("DescCorta", descCorta);
                mjasonF.put("NumeroInterno", numerointerno);
                ArrayResultante.put(mjasonF);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        String ArregloProductosEntregados = ArrayResultante.toString();

        Intent intent = new Intent(getApplicationContext(), ventaProductosFaltantes.class);
        String paso = ArrayventasFaltantes.toString();
        intent.putExtra("articulos", paso);
        intent.putExtra("posicion", posicion);
        intent.putExtra("usuario", usuario);
        intent.putExtra("productosEntregados", ArregloProductosEntregados);
        startActivity(intent);

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

    private void MostrarProductos() {
        banderaSigue= true;

        String url = "http://"+ipEstacion+"/CorpogasService/api/islas/productos/estacion/"+EstacionId+"/posicionCargaId/"+posicion;
        //String url = "http://"+ipEstacion+"/CorpogasService/api/cierres/registrar/sucursal/"+sucursalId+"/isla/"+posicion+"/origen/1";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
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

        maintitle = new ArrayList<String>();
        subtitle = new ArrayList<String>();
        total = new ArrayList<String>();
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

            for (int i = 0; i <cierredetalles.length() ; i++){ //bodegaprod.length()
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
                maintitle.add("-");
                subtitle.add(DescLarga);
                total.add(cantidadrecibida);

                ID.add(DescLarga);
                ClaveProducto.add(idArticulo);
                ProductosId.add(IdProductos);
                codigobarras.add(codigobar);
                productosVendidos.add(cantidadvendida);
                precio.add(preciounitario);
                productosRecibidos.add(cantidadrecibida);

                int cantidad = Integer.parseInt(cantidadrecibida)-Integer.parseInt(cantidadvendida);

                JSONObject mjasonF = new JSONObject();

                mjasonF.put("TipoProducto", TProductoId);
                mjasonF.put("ProductoId", IdProductos);
                mjasonF.put("NumeroInterno", idArticulo);
                mjasonF.put("ProductosEntregados", cantidad);

                //mjasonF.put ("DescCorta", descCorta);
                ArrayResultante.put(mjasonF);



            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final ListAdapterBilletes adapterP = new ListAdapterBilletes(this,   maintitle, subtitle, total);
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
                                        String tEntregados = total.get(position).toString();
                                        String tVendidos = productosRecibidos.get(position).toString();
                                        final int totalVendidos = Integer.parseInt(tVendidos);
                                        final int totalEntregados = Integer.parseInt(tEntregados);
                                        if (resultado < (totalEntregados - totalVendidos)) {
                                            btnsiguiente.setVisibility(View.VISIBLE);
                                            String prod = ClaveProducto.get(position);
                                            int diferencia = ((totalEntregados - totalVendidos) - resultado);
                                            generaArreglo(prod, diferencia, preciou, subtitle.get(position), numerointerno, codbarras, IProd, TProd);
                                        }
                                        ListAdapterBilletes adapter = new ListAdapterBilletes(ventasTotales.this, maintitle, subtitle, total);
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

    private void mostrarProductosExistencias(String response, final String posicion, final String usuario){
        String TProductoId;

        maintitle = new ArrayList<String>();
        subtitle = new ArrayList<String>();
        total = new ArrayList<String>();
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

        try {
            JSONObject p1 = new JSONObject(response);

            String ni = p1.getString("NumeroInterno");
            String bodega = p1.getString("Bodega");
            JSONObject ps = new JSONObject(bodega);
            String producto = ps.getString("BodegaProductos");
            JSONArray bodegaprod = new JSONArray(producto);

            for (int i = 18; i <25 ; i++){ //bodegaprod.length()
                String IdProductos = null;
                JSONObject pA = bodegaprod.getJSONObject(i);
                String ExProductos=pA.getString("Existencias");
                String productoclave = pA.getString("Producto");
                JSONObject prod = new JSONObject(productoclave);
                TProductoId=prod.getString("TipoSatProductoId");
                String DescLarga=prod.getString("DescripcionLarga");
                String idArticulo=prod.getString("NumeroInterno");
                //String preciounitario=prod.getString("preciounitario");
                String codigobar=prod.getString("CodigoBarras");


                String cantidadvendida="2";
                String cantidadrecibida=prod.getString("NumeroInterno");

                String PControl=prod.getString("ProductoControles");
                JSONArray PC = new JSONArray(PControl);
                maintitle.add("-");
                subtitle.add(DescLarga);
                total.add(idArticulo);

                ID.add(DescLarga);
                ClaveProducto.add(idArticulo);
                ProductosId.add(IdProductos);
                productosVendidos.add("2");
                codigobarras.add(codigobar);
                //precio.add(preciounitario);
                TipoProductoId.add(TProductoId);
                DescripcionPr.add(DescLarga);


                int cantidad = Integer.parseInt(cantidadrecibida)-Integer.parseInt(cantidadvendida);

                JSONObject mjasonF = new JSONObject();
                mjasonF.put("ProductoId", IdProductos);
                mjasonF.put("ProductosEntregados", cantidad);
                //mjasonF.put ("DescCorta", descCorta);
                mjasonF.put("NumeroInterno", idArticulo);
                ArrayResultante.put(mjasonF);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final ListAdapterBilletes adapterP = new ListAdapterBilletes(this,   maintitle, subtitle, total);
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
                                    String  tEntregados = total.get(position).toString();
                                    String  tVendidos = "2"; //cantidadEntregada.get(position).toString();
                                    final int totalVendidos = Integer.parseInt(tVendidos);
                                    final int totalEntregados = Integer.parseInt(tEntregados);
                                    if (resultado < (totalEntregados - totalVendidos)) {
                                        btnsiguiente.setVisibility(View.VISIBLE);
                                        String prod = ClaveProducto.get(position);
                                        int diferencia = ((totalEntregados - totalVendidos) - resultado);
                                        generaArreglo(prod, diferencia, preciou, subtitle.get(position), numerointerno, codbarras, IProd, TProd);
                                    }
                                    ListAdapterBilletes adapter = new ListAdapterBilletes(ventasTotales.this, maintitle, subtitle, total);
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
    } catch (JSONException e) {
        e.printStackTrace();
    }

}





    private void subtotalOficinaBilletes(String response) {
        maintitle = new ArrayList<String>();
        subtitle = new ArrayList<String>();
        total = new ArrayList<String>();
        try {

            JSONArray stl = new JSONArray(response);

            for (int i = 0; i < stl.length(); i++) {
                denom = stl.getJSONObject(i);
                denominacion = denom.getString("Importe");
                double monto = Double.parseDouble(denominacion);
                arrayMonto.add(monto);

                maintitle.add("0");
                subtitle.add(denominacion);
                total.add("");

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        final ListAdapterBilletes adapter = new ListAdapterBilletes(this, maintitle, subtitle, total);
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
                                        total.set(position, String.valueOf(result));
                                        final String ray2 = result.toString();

                                        sumainter = totalBilletes() + Double.parseDouble(ray2);

                                        if ((totalBilletes()<= (fajillaBillete-10)) && (sumainter <= (fajillaBillete-10))){
                                            Toast.makeText(ventasTotales.this, "Cantidad Agregada", Toast.LENGTH_SHORT).show();
                                            prueba2.put(ray2);
                                        }else{
                                            Toast.makeText(ventasTotales.this, "Los Valores que ingresaste pueden ser 1 fajilla", Toast.LENGTH_SHORT).show();
                                        }
                                        ListAdapterBilletes adapter = new ListAdapterBilletes(ventasTotales.this, maintitle, subtitle, total);
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