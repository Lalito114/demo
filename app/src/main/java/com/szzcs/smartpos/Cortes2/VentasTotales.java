package com.szzcs.smartpos.Cortes2;

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
import com.google.gson.JsonArray;
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

public class VentasTotales extends AppCompatActivity {
    List<String> maintitle, subtitle, calculo, cantidadEntregada, cantidadVendidos; //total
    ListView mList;
    JSONObject denom;

    String denominacion;

    ArrayList arrayMonto = new ArrayList();
    int resultado;
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
    String posicion, idusuario, numerodispositivo;
    boolean banderaValida;
    String fechaTrabajo;
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
    double VentaProductos = 0;
    int cantidadAceites = 0;

    RespuestaApi<Cierre> cierreRespuestaApi;
    long cierreId;
    long turnoId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ventas_totales);
        SQLiteBD db = new SQLiteBD(getApplicationContext());
        EstacionId = db.getIdEstacion();
        sucursalId = db.getIdSucursal();
        ipEstacion= db.getIpEstacion();
        islaId =  getIntent().getStringExtra("islaId");
        idusuario  = getIntent().getStringExtra("idusuario");
        numerodispositivo = "1";
        cierreRespuestaApi = (RespuestaApi<Cierre>) getIntent().getSerializableExtra( "lcierreRespuestaApi");
        turnoId = cierreRespuestaApi.getObjetoRespuesta().getTurnoId();
        cierreId = cierreRespuestaApi.getObjetoRespuesta().getId();
        fechaTrabajo = cierreRespuestaApi.getObjetoRespuesta().getFechaTrabajo();

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
                            AlertDialog.Builder builder = new AlertDialog.Builder(VentasTotales.this);
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

                                    Intent intent = new Intent(getApplicationContext(),FajillasBilletes.class);
                                    intent.putExtra("idusuario", idusuario);
                                    intent.putExtra("islaId", islaId);
                                    intent.putExtra("VentaProductos", String.valueOf(VentaProductos));
                                    intent.putExtra("cantidadAceites", String.valueOf(cantidadAceites));
                                    intent.putExtra("lcierreRespuestaApi", cierreRespuestaApi);
                                    startActivity(intent);

                    }

                }else{
                    try {
                        AlertDialog.Builder builder = new AlertDialog.Builder(VentasTotales.this);
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

                    Intent intent = new Intent(getApplicationContext(), VentaProductosFaltantes.class);
                    String paso = ArrayventasFaltantes.toString();
                    intent.putExtra("articulos", paso);
                    intent.putExtra("productosEntregados", ArregloProductosEntregados);
                    intent.putExtra("turnoId", turnoId);
                    intent.putExtra("fechatrabajo", fechaTrabajo);

                    intent.putExtra("idusuario", idusuario);
                    intent.putExtra("islaId", islaId);
                    intent.putExtra("ventaproductos", VentaProductos);
                    intent.putExtra("cantidadAceites", String.valueOf(cantidadAceites));
                    intent.putExtra("lcierreRespuestaApi", cierreRespuestaApi);
                    startActivity(intent);



                    startActivity(intent);

                }else{
                    try {
                        AlertDialog.Builder builder = new AlertDialog.Builder(VentasTotales.this);
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


    private void MostrarProductos() {
        banderaSigue= true;


        String url = "http://"+ipEstacion+"/CorpogasService/api/cierres/registrar/sucursal/"+sucursalId+"/isla/"+islaId+"/usuario/"+idusuario+"/origen/" + numerodispositivo;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mostrarProductosCierre(response,  idusuario);
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
                        AlertDialog.Builder builder = new AlertDialog.Builder(VentasTotales.this);
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
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this.getApplicationContext());
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(12000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

    private void
    mostrarProductosCierre(String response, final String usuario){
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
                IdProductos=pA.getString("RecursoId");
                //String PControl=prod.getString("ProductoControles");
                //JSONArray PC = new JSONArray(PControl);

                if (TProductoId.equals("1")) {
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
                    mjasonF.put("CierreId", cierreId);
                    mjasonF.put("CierreSucursalId", Integer.parseInt(sucursalId));
                    mjasonF.put("CategoriaProductoId", Integer.parseInt(TProductoId));
                    mjasonF.put("RecursoId", Integer.parseInt(IdProductos));
                    mjasonF.put("Precio", Double.parseDouble(preciounitario));
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

                    VentaProductos = VentaProductos + (Integer.parseInt(cantidadvendida) * Double.parseDouble(preciounitario));
                    cantidadAceites = cantidadAceites + Integer.parseInt(cantidadvendida);
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

                    AlertDialog.Builder builder = new AlertDialog.Builder(VentasTotales.this);
                    builder.setTitle("Ingresa Cantidad \n");
                    builder.setView(input)
                            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    String denomi = input.getText().toString();
                                    // String preciou = "10";
                                    if (denomi.isEmpty()){
                                        Toast.makeText(VentasTotales.this, "Debes cargar un valor numérico", Toast.LENGTH_SHORT).show();
                                    }else {
                                        resultado = Integer.parseInt(denomi);
                                        maintitle.set(position, denomi);
                                        //total.set(position, String.valueOf(result));
                                        String tEntregados = calculo.get(position).toString();
                                        String tVendidos = productosRecibidos.get(position).toString();
                                        final int totalVendidos = Integer.parseInt(tVendidos);
                                        final int totalEntregados = Integer.parseInt(tEntregados);
                                        if (totalEntregados < resultado){
                                            Toast.makeText(VentasTotales.this, "La cantidad vendida no puede ser mayor que la que se recibió", Toast.LENGTH_SHORT).show();
                                        }else {
                                            if (resultado > (totalEntregados - totalVendidos)) {
                                                btnsiguiente.setVisibility(View.VISIBLE);
                                                String prod = ClaveProducto.get(position);
                                                int diferencia = ((totalEntregados - totalVendidos) - resultado);
                                                generaArreglo(prod, diferencia, preciou, subtitle.get(position), numerointerno, codbarras, IProd, TProd);
                                                //valido si hay diferencias
                                                banderaValidaBotonVentasFaltantes= true;
                                                Boolean banderaNoCargada = true;
                                                for (int g= 0; g< mList.getCount(); g++)
                                                {
                                                    String valorCapturado;
                                                    valorCapturado = maintitle.get(g);
                                                    if (valorCapturado == "-"){
                                                        banderaValidaBotonVentasFaltantes = false;
                                                        banderaNoCargada = false;
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
                                                if (banderaNoCargada == false){
                                                    btnsiguiente.setVisibility(View.INVISIBLE);
                                                }else {
                                                    if (banderaValidaBotonVentasFaltantes == false) {
                                                        btnsiguiente.setVisibility(View.VISIBLE);
                                                    } else {
                                                        btnsiguiente.setVisibility(View.INVISIBLE);
                                                    }
                                                }
                                                ListAdapterBilletes adapter = new ListAdapterBilletes(VentasTotales.this, maintitle, subtitle, calculo);
                                                mList.setAdapter(adapter);
                                            }else{
                                                Toast.makeText(VentasTotales.this, "La cantidad capturada es mayor que el resultado de productos entregados menos las ventas", Toast.LENGTH_SHORT).show();
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
}