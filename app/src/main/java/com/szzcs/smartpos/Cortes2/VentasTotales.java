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
import android.widget.ListView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.szzcs.smartpos.Helpers.Modales.Modales;
import com.szzcs.smartpos.Munu_Principal;
import com.szzcs.smartpos.R;
import com.szzcs.smartpos.configuracion.SQLiteBD;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class VentasTotales extends AppCompatActivity {
    List<String> maintitle, subtitle, calculo, cantidadEntregada, cantidadVendidos; //total
    ListView mList;
    JSONObject denom;

    String denominacion;

    ArrayList arrayMonto = new ArrayList();
    int resultado;
    Double result;

    JSONArray prueba2 = new JSONArray();
    double sumainter;
    int fajillaBillete;
    String EstacionId, sucursalId, ipEstacion, islaId ;
    Boolean banderaSigue;
    Button btnsiguiente, btnAceptar;
    String numerointerno, idarticulo, descripcionarticulo;
    JSONObject mjason = new JSONObject();
    JSONArray ArrayventasFaltantes2 = new JSONArray();
    ArrayList<ProductosFaltantes> ArrayventasFaltantes = new ArrayList<ProductosFaltantes>();
    String posicion, numerodispositivo;
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
    RespuestaApi<AccesoUsuario> accesoUsuario;
    long idusuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ventas_totales);
        SQLiteBD db = new SQLiteBD(getApplicationContext());
        EstacionId = db.getIdEstacion();
        sucursalId = db.getIdSucursal();
        ipEstacion= db.getIpEstacion();
        islaId =  getIntent().getStringExtra("islaId");
        accesoUsuario = (RespuestaApi<AccesoUsuario>) getIntent().getSerializableExtra("accesoUsuario");
        idusuario = accesoUsuario.getObjetoRespuesta().getSucursalEmpleadoId();
        posicion = "1"; //getIntent().getStringExtra("car");
        numerodispositivo = "1";
        cierreRespuestaApi = (RespuestaApi<Cierre>) getIntent().getSerializableExtra( "lcierreRespuestaApi");
        turnoId = cierreRespuestaApi.getObjetoRespuesta().getTurnoId();
        cierreId = cierreRespuestaApi.getObjetoRespuesta().getId();
        fechaTrabajo = cierreRespuestaApi.getObjetoRespuesta().getFechaTrabajo();

        MostrarProductos();
        CargaProductosFaltantes();
        btnAceptar=findViewById(R.id.btnAceptar);
        btnAceptar.setOnClickListener(new View.OnClickListener() {
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

                            //String titulo = "AVISO";
                            String nombrebtnAceptar ="SI";
                            String nombreBtnCancelar ="NO";
                            String mensaje = "Existen diferencias. ¿DESEA IR A VENTA PRODUCTOS FALTANTES?"; //Hay diferencias entre los productos vendidos y los que entrega, oprima el botón VENTA PRODUCTOS FALTANTES
                            Modales modales = new Modales(VentasTotales.this); 
                            View view1 = modales.MostrarDialogoAlerta(VentasTotales.this,mensaje,nombrebtnAceptar,nombreBtnCancelar);
                            view1.findViewById(R.id.buttonYes).setOnClickListener(new View.OnClickListener() {
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
                                        intent.putExtra("posicion", posicion);
                                        intent.putExtra("productosEntregados", ArregloProductosEntregados);
                                        intent.putExtra("islaId", islaId);
                                        intent.putExtra("VentaProductos", String.valueOf(VentaProductos));
                                        intent.putExtra("cantidadAceites", String.valueOf(cantidadAceites));
                                        intent.putExtra("lcierreRespuestaApi", cierreRespuestaApi);
                                        intent.putExtra("accesoUsuario", accesoUsuario);

                                        startActivity(intent);

                                    }
                                }
                            });

                        view1.findViewById(R.id.buttonNo).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                modales.alertDialog.dismiss();

                            }
                        });


                    } else {//Enviamos a Guardar

                                    Intent intent = new Intent(getApplicationContext(),FajillasBilletes.class);
                                    intent.putExtra("idusuario", idusuario);
                                    intent.putExtra("islaId", islaId);
                                    intent.putExtra("VentaProductos", String.valueOf(VentaProductos));
                                    intent.putExtra("cantidadAceites", String.valueOf(cantidadAceites));
                                    intent.putExtra("lcierreRespuestaApi", cierreRespuestaApi);
                                    intent.putExtra("accesoUsuario", accesoUsuario);
                                    startActivity(intent);

                    }

                }else{

                        String titulo = "AVISO";
                        String mensaje = "Existe productos que aún no se han contabilzado.";
                        Modales modales = new Modales(VentasTotales.this);
                        View view1 = modales.MostrarDialogoAlertaAceptar(VentasTotales.this,mensaje,titulo);
                        view1.findViewById(R.id.buttonYes).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                modales.alertDialog.dismiss();

                            }
                        });


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
                    ArrayventasFaltantes2 = new JSONArray();
                    for(ProductosFaltantes item : ArrayventasFaltantes)
                    {
                        JSONObject mjason = new JSONObject();
                        try {
                            mjason.put("TipoProducto", item.TProducto);
                            mjason.put("ProductoId", item.numeroproducto);
                            //ventaRestante.put("CodigoBarras", codigobarras);
                            mjason.put("NumeroInterno", item.internonumero);
                            mjason.put("Cantidad", item.cantidad);
                            mjason.put("Precio", item.preciounitario);
                            mjason.put("DescCorta", item.descCorta);
                            mjason.put("CodigoBarras", item.codBarras);


                            ArrayventasFaltantes2.put(mjason);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    }
                    String paso = ArrayventasFaltantes2.toString();
                    intent.putExtra("articulos", paso);
                    intent.putExtra("posicion", posicion);
                    intent.putExtra("productosEntregados", ArregloProductosEntregados);
                    intent.putExtra("islaId", islaId);
                    intent.putExtra("VentaProductos", String.valueOf(VentaProductos));
                    intent.putExtra("cantidadAceites", String.valueOf(cantidadAceites));
                    intent.putExtra("lcierreRespuestaApi", cierreRespuestaApi);
                    intent.putExtra("accesoUsuario", accesoUsuario);

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


        String url = "http://"+ipEstacion+"/CorpogasService/api/cierreDetalles/sucursal/"+sucursalId+"/isla/"+islaId+"/usuario/"+idusuario;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mostrarProductosCierre(response, posicion, String.valueOf(idusuario));
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
    mostrarProductosCierre(String response, final String posicion, final String usuario){
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
            //JSONObject ps = new JSONObject(objetorespuesta);
            //String producto = ps.getString("CierreDetalles");
            JSONArray cierredetalles = new JSONArray(objetorespuesta);

            for (int i = 0; i < 3; i++){ //cierredetalles.length()
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

                if (TProductoId.equals("1")) {// No considera combustibles
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



                    String titulo = "PRODUCTOS";
                    String mensaje = "Ingresa cantidad: " ;
                    Modales modales = new Modales(VentasTotales.this);
                    View viewLectura = modales.MostrarDialogoInsertaDato(VentasTotales.this, mensaje, titulo);
                    EditText edtProductoCantidad = ((EditText) viewLectura.findViewById(R.id.textInsertarDato));


                    viewLectura.findViewById(R.id.buttonYes).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String cantidad = edtProductoCantidad.getText().toString();
                            if (cantidad.isEmpty()){
                                edtProductoCantidad.setError("Campo requerido");
                            }else {
                                resultado = Integer.parseInt(cantidad);
                                maintitle.set(position, cantidad);
                                //total.set(position, String.valueOf(result));
                                String tEntregados =  productosRecibidos.get(position).toString();
                                String tVendidos = productosVendidos.get(position).toString();
                                final int totalVendidos = Integer.parseInt(tVendidos);
                                final int totalEntregados = Integer.parseInt(tEntregados);
                                if (totalEntregados < resultado){
                                    edtProductoCantidad.setError("La cantidad vendida no puede ser mayor que la que se recibió");
                                }else {
                                    if (resultado <= (totalEntregados - totalVendidos)) {
                                        btnsiguiente.setVisibility(View.VISIBLE);
                                        String prod = ClaveProducto.get(position);
                                        int diferencia = ((totalEntregados - totalVendidos) - resultado);
                                        //if (diferencia !=0) {
                                            generaArreglo(prod, diferencia, preciou, subtitle.get(position), numerointerno, codbarras, IProd, TProd, position, diferencia);

                                    //    }else {//valido si hay diferencias
                                      //      boolean nuevo = true;


//                                            for (int i = 0; i < ArrayventasFaltantes.size(); i++) {
//                                                try {
//                                                    JSONObject jsonObject = ArrayventasFaltantes.getJSONObject(i);
//                                                    if (jsonObject.has("Posicion")) {
//                                                        String valor = jsonObject.getString("Posicion");
//                                                        int inPosicion = Integer.parseInt(valor);
////                                                        if (inPosicion==posicion){
////                                                            nuevo=false;
////                                                        }
//                                                    }
//                                                } catch (JSONException e) {
//                                                    e.printStackTrace();
//                                                }
//                                            }

           //                             }
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

                                                int entrega = Integer.parseInt(valorCapturado);
                                                int recibido = Integer.parseInt(productosRecibidos.get(g));
                                                int vendido = Integer.parseInt(productosVendidos.get(g));
                                                if (entrega < (recibido - vendido)) {
                                                    banderaValidaBotonVentasFaltantes = false;
                                                    break;
                                                }else{
                                                    banderaValidaBotonVentasFaltantes =true;
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
                                        modales.alertDialog.dismiss();
                                    }else{
                                        edtProductoCantidad.setError("La cantidad capturada es mayor que el resultado de productos entregados menos las ventas");
                                        //Toast.makeText(VentasTotales.this, "La cantidad capturada es mayor que el resultado de productos entregados menos las ventas", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }

                        }
                    });

                    viewLectura.findViewById(R.id.buttonNo).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            modales.alertDialog.dismiss();

                        }
                    });



//                    AlertDialog.Builder builder = new AlertDialog.Builder(VentasTotales.this);
//                    builder.setTitle("Ingresa Cantidad \n");
//                    builder.setView(input)
//                            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialogInterface, int i) {
//                                    String denomi = input.getText().toString();
//                                    // String preciou = "10";
//                                    if (denomi.isEmpty()){
//                                        Toast.makeText(VentasTotales.this, "Debes cargar un valor numérico", Toast.LENGTH_SHORT).show();
//                                    }else {
//                                        resultado = Integer.parseInt(denomi);
//                                        maintitle.set(position, denomi);
//                                        //total.set(position, String.valueOf(result));
//                                        String tEntregados =  productosRecibidos.get(position).toString();
//                                        String tVendidos = productosVendidos.get(position).toString();
//                                        final int totalVendidos = Integer.parseInt(tVendidos);
//                                        final int totalEntregados = Integer.parseInt(tEntregados);
//                                        if (totalEntregados < resultado){
//                                            Toast.makeText(VentasTotales.this, "La cantidad vendida no puede ser mayor que la que se recibió", Toast.LENGTH_SHORT).show();
//                                        }else {
//                                            if (resultado <= (totalEntregados - totalVendidos)) {
//                                                btnsiguiente.setVisibility(View.VISIBLE);
//                                                String prod = ClaveProducto.get(position);
//                                                int diferencia = ((totalEntregados - totalVendidos) - resultado);
//                                                generaArreglo(prod, diferencia, preciou, subtitle.get(position), numerointerno, codbarras, IProd, TProd);
//                                                //valido si hay diferencias
//                                                banderaValidaBotonVentasFaltantes= true;
//                                                Boolean banderaNoCargada = true;
//                                                for (int g= 0; g< mList.getCount(); g++)
//                                                {
//                                                    String valorCapturado;
//                                                    valorCapturado = maintitle.get(g);
//                                                    if (valorCapturado == "-"){
//                                                        banderaValidaBotonVentasFaltantes = false;
//                                                        banderaNoCargada = false;
//                                                        break;
//                                                    }else {
//
//                                                        int entrega = Integer.parseInt(valorCapturado);
//                                                        int recibido = Integer.parseInt(productosRecibidos.get(g));
//                                                        int vendido = Integer.parseInt(productosVendidos.get(g));
//                                                        if (entrega < (recibido - vendido)) {
//                                                            banderaValidaBotonVentasFaltantes = false;
//                                                            break;
//                                                        }else{
//                                                            banderaValidaBotonVentasFaltantes =true;
//                                                        }
//                                                    }
//                                                }
//                                                if (banderaNoCargada == false){
//                                                    btnsiguiente.setVisibility(View.INVISIBLE);
//                                                }else {
//                                                    if (banderaValidaBotonVentasFaltantes == false) {
//                                                        btnsiguiente.setVisibility(View.VISIBLE);
//                                                    } else {
//                                                        btnsiguiente.setVisibility(View.INVISIBLE);
//                                                    }
//                                                }
//                                                ListAdapterBilletes adapter = new ListAdapterBilletes(VentasTotales.this, maintitle, subtitle, calculo);
//                                                mList.setAdapter(adapter);
//                                            }else{
//                                                Toast.makeText(VentasTotales.this, "La cantidad capturada es mayor que el resultado de productos entregados menos las ventas", Toast.LENGTH_SHORT).show();
//                                            }
//                                        }
//                                    }
//                                }
//                            })
//                            .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    dialog.cancel();
//                                }
//                            }).show();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }


    private void generaArreglo(String numeroproducto, int cantidad, String preciounitario, String descCorta, String internonumero, String codBarras, String IProducto, String TProducto, int posicion, int diferencia){
        boolean nuevo = true;
        ProductosFaltantes productosFaltantes = new ProductosFaltantes();
        for(ProductosFaltantes item : ArrayventasFaltantes)
        {
            if(item.posicion == posicion)
            {
                nuevo = false;
                break;
            }

        }

        if( nuevo == true) {
            if(cantidad != 0){
            productosFaltantes.TProducto = TProducto;
            productosFaltantes.numeroproducto = numeroproducto;
            productosFaltantes.internonumero = internonumero;
            productosFaltantes.cantidad = cantidad;
            productosFaltantes.preciounitario = preciounitario;
            productosFaltantes.descCorta = descCorta;
            productosFaltantes.codBarras = codBarras;
            productosFaltantes.posicion = posicion;
            productosFaltantes.diferencia = diferencia;
            ArrayventasFaltantes.add(productosFaltantes);
            }

        }else
        {
            if(cantidad != 0)
            {
                productosFaltantes.setCantidad(cantidad);
            }
            else
            {
                for(int K=0; K<= ArrayventasFaltantes.size()-1; K++)
                {
                    if(ArrayventasFaltantes.get(K).posicion == posicion)
                    {
                        ArrayventasFaltantes.remove(K);
                        break;
                    }

                }

            }


        }


//        JSONObject mjason = new JSONObject();
//        try {
//                mjason.put("TipoProducto", TProducto);
//                mjason.put("ProductoId", numeroproducto);
//                //ventaRestante.put("CodigoBarras", codigobarras);
//                mjason.put("NumeroInterno", internonumero);
//                mjason.put("Cantidad", cantidad);
//                mjason.put("Precio", preciounitario);
//                mjason.put("DescCorta", descCorta);
//                mjason.put("CodigoBarras", codBarras);
//                mjason.put("Posicion", posicion);

//                ArrayventasFaltantes.add(productosFaltantes);
            //valido si hay
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
    }

    //Metodo para regresar a la actividad principal
    @Override
    public void onBackPressed() {
        String mensaje = "Seras regresado al menú principal. ¿Estas seguro?";
        Modales modales = new Modales(VentasTotales.this);
        String nombrebtnAceptar ="SI";
        String nombreBtnCancelar ="NO";
        View view1 = modales.MostrarDialogoAlerta(VentasTotales.this,mensaje, nombrebtnAceptar, nombreBtnCancelar);

        view1.findViewById(R.id.buttonYes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Munu_Principal.class);
                startActivity(intent);
            }
        });

        view1.findViewById(R.id.buttonNo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                modales.alertDialog.dismiss();

            }
        });

    }


}