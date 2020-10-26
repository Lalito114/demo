package com.szzcs.smartpos.Puntada.Redimir;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.szzcs.smartpos.Productos.ListAdapterProductos;
import com.szzcs.smartpos.Puntada.Acumular.ListAdapterSP;
import com.szzcs.smartpos.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.szzcs.smartpos.TanqueLleno.ProductoTLl;
import com.szzcs.smartpos.configuracion.SQLiteBD;

public class BalanceProductos extends AppCompatActivity {
    Button btnAgregar,btnEnviar, incrementar, decrementar, btnMostrarCombustibles, btnMostrarProductos;
    TextView cantidadProducto, txtSaldo;
    EditText Producto;
    String cantidad;
    JSONObject mjason = new JSONObject();
    ListView list;
    EditText litros, pesos;
    Button agregarcombustible, imprimirTicket, enviarProductos, limpiar;
    String IdCombustible, cs,numneroInterno,  descripcion, precio, IdCombus, Costo;
    double  LitrosCoversion;
    final JSONObject datos = new JSONObject();
    JSONObject jsonParam = new JSONObject();
    String folio, transaccion;
    List<String> ID;
    List<String> NombreProducto;
    List<String> PrecioProducto;
    List<String> ClaveProducto;
    List<String> CodigoBarras;
    List<String> ExistenciaProductos;
    List<String> ProductoId, CategoriaId, tipoprod;
    String tipoproductofinal, productoIdfinal, numeroInternofinal, Descripcionfinal, cantidadfinal, preciofinal, precio1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance_productos);
        String saldos = getIntent().getStringExtra("saldo");
//      enviamos el saldo disponible para poder visualizar
        txtSaldo= findViewById(R.id.txtSaldos);
        txtSaldo.setText(saldos);
        litros = findViewById(R.id.edtLitros);
        litros.setEnabled(false);
        pesos=findViewById(R.id.edtPesos);
        pesos.setEnabled(false);

        agregarcombustible = findViewById(R.id.btnAgregarProducto);
        agregarcombustible.setOnClickListener(new View.OnClickListener() {
            JSONArray array1 = new JSONArray();
            @Override
            public void onClick(View v) {

                if (litros.getText().toString().isEmpty()){
                    if (pesos.getText().toString().isEmpty()){
                        if (litros.getText().toString().isEmpty() || pesos.getText().toString().isEmpty()){
                            try {
                                jsonParam.put("TipoProducto",tipoproductofinal);
                                jsonParam.put("ProductoId",productoIdfinal);
                                jsonParam.put("NumeroInterno",numeroInternofinal);
                                jsonParam.put("Descripcion",descripcion);
                                jsonParam.put("Cantidad","1");
                                jsonParam.put("Precio",preciofinal);
                                array1.put(jsonParam);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }else{
                        double pideenpesos = Double.valueOf(pesos.getText().toString());
                        double litrospedidos = pideenpesos / Double.valueOf(precio);
                        String valor = String.valueOf(litrospedidos);
                        String valor1 = valor.substring(0,4);
                        litros.setText(valor1);

                        try {
                            //Add string params
                            jsonParam.put("TipoProducto",tipoproductofinal);
                            jsonParam.put("ProductoId",productoIdfinal);
                            jsonParam.put("NumeroInterno",productoIdfinal);
                            jsonParam.put("Descripcion",descripcion);
                            jsonParam.put("Cantidad",valor1);
                            jsonParam.put("Precio",pideenpesos);
                            array1.put(jsonParam);



                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }else{
                    double litrospedidos = Double.valueOf(litros.getText().toString());
                    double costofinal = litrospedidos * Double.valueOf(precio);
                    String valor = String.valueOf(costofinal);
                    String valor1 = valor.substring(0,4);
                    pesos.setText(valor1);

                    try {
                        //Add string params
                        cantidadProducto  = findViewById(R.id.edtCantidadProducto);
                        litros = findViewById(R.id.edtLitros);
                        jsonParam.put("TipoProducto",tipoproductofinal);
                        jsonParam.put("ProductoId",productoIdfinal);
                        jsonParam.put("NumeroInterno",numeroInternofinal);
                        jsonParam.put("Descripcion",Descripcionfinal);
                        jsonParam.put("Cantidad",cantidadProducto.getText().toString());
                        jsonParam.put("Precio",valor1);
                        array1.put(jsonParam);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

//      Los elementos anteriores son los primero que se mostraran en el activity al iniciar
        btnMostrarCombustibles = findViewById(R.id.btnCombustibles);
        btnMostrarCombustibles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                litros = findViewById(R.id.edtLitros);
                litros.setEnabled(true);
                pesos=findViewById(R.id.edtPesos);
                pesos.setEnabled(true);
                MostrarCombustibles();
            }
        });
        btnMostrarProductos = findViewById(R.id.btnAceites);
        btnMostrarProductos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                litros.setText("");
                pesos.setText("");
                litros = findViewById(R.id.edtLitros);
                litros.setEnabled(false);
                pesos=findViewById(R.id.edtPesos);
                pesos.setEnabled(false);
                MostrarProductos();
            }
        });
    }

    private void MostrarCombustibles() {
        SQLiteBD data = new SQLiteBD(getApplicationContext());
        String url = "http://"+data.getIpEstacion()+"/CorpogasService/api/precioCombustibles/estacion/"+data.getIdEstacion()+"/actuales";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                MetodoResponsecombustibles(response);
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
    private void MetodoResponsecombustibles(final String response) {
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

//                    String combustibleId = conbustibles.getString("EstacionCombustibleEstacionId");
//                    tipoprod.add(combustibleId);


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
        ListAdapterProductosRedimir adapterP = new ListAdapterProductosRedimir(this, maintitle, subtitle, imgid);
        list=(ListView)findViewById(R.id.list);
        list.setAdapter(adapterP);


        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub

                String identificadorCombustible = IdProducto.get(position);
                productoIdfinal = identificadorCombustible;

                String interno = IdCombustible1.get(position);
                numeroInternofinal = interno;

                String cuesta = Precio.get(position);
                preciofinal = cuesta;

                tipoproductofinal = "1";
            }
        });
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

        CategoriaId = new ArrayList<>();

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
                String categoria = prod.getString("CategoriaProducto");
                JSONObject categoriaProducto = new JSONObject(categoria);
                String IdCategoria = categoriaProducto.getString("NumeroInterno");
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
                CategoriaId.add(IdCategoria);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        final ListAdapterAceitesRedimir adapterP = new ListAdapterAceitesRedimir(this,  ID, NombreProducto);
        list=(ListView)findViewById(R.id.list);
        list.setTextFilterEnabled(true);
        list.setAdapter(adapterP);
//        Agregado  click en la lista
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Descripcionfinal = ID.get(i).toString();
                preciofinal = PrecioProducto.get(i).toString();
                numeroInternofinal= ClaveProducto.get(i).toString();
                String existencia = ExistenciaProductos.get(i).toString();
                productoIdfinal = ProductoId.get(i);
                tipoproductofinal = CategoriaId.get(i);

//                Producto.setText(paso);
//                txtDescripcion.setText(Descripcion);
//                precio.setText(precioUnitario);
//                existencias.setText(existencia);
//                idproducto.setText(idproduc);

            }
        });

    }


}
