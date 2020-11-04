package com.szzcs.smartpos.Cortes2;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.szzcs.smartpos.R;
import com.szzcs.smartpos.configuracion.SQLiteBD;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class TotalProductos extends AppCompatActivity {

    // Se declaran las variables que se usaran en este Activity
    String cantidad, total2, descripcion;
    JSONArray totalLitros = new JSONArray();
    JSONArray importe = new JSONArray();
    TextView txtImporteTotal;
    double sumaTotalImporte;
    List<String> maintitle;
    List<String> subtitle;
    List<String> total;
    ListView mListView;
    String islaId;

    String picos;
    String dineroBilletes;
    String dineroMorralla;
    Button totalCombustible;
    String subTotalOficina;
    String VentaProductos;
    String cantidadAceites;

    RespuestaApi<Cierre> cierreRespuestaApi;
    long cierreId;
    long turnoId;

    RespuestaApi<AccesoUsuario> accesoUsuario;
    long idusuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_total_productos);
        totalCombustible = (Button) findViewById(R.id.btnTotalCombustible);

        islaId = getIntent().getStringExtra("islaId");
        accesoUsuario = (RespuestaApi<AccesoUsuario>) getIntent().getSerializableExtra("accesoUsuario");
        idusuario = accesoUsuario.getObjetoRespuesta().getSucursalEmpleadoId();
//        picos = getIntent().getStringExtra("sumaPicosBilletes");
        dineroBilletes = getIntent().getStringExtra("dineroBilletes");
        dineroMorralla = getIntent().getStringExtra("dineroMorralla");
        subTotalOficina = getIntent().getStringExtra("subTotalOficina");
        VentaProductos = getIntent().getStringExtra("VentaProductos");
        cantidadAceites = getIntent().getStringExtra("cantidadAceites");
        cierreRespuestaApi = (RespuestaApi<Cierre>) getIntent().getSerializableExtra( "lcierreRespuestaApi");
        turnoId = cierreRespuestaApi.getObjetoRespuesta().getTurnoId();
        cierreId = cierreRespuestaApi.getObjetoRespuesta().getId();


        obtenerTotales();

        // Hacemos la relacion de las variables con los objetos del layout
        txtImporteTotal = (TextView) findViewById(R.id.textImporteProd);

        totalCombustible.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),GranTotal.class);
                intent.putExtra("origenId","1");
                intent.putExtra("islaId",islaId);
                intent.putExtra("dineroBilletes",dineroBilletes);
                intent.putExtra("dineroMorralla",dineroMorralla);
                intent.putExtra("subTotalOficina",subTotalOficina);
                intent.putExtra("VentaProductos", VentaProductos);
                intent.putExtra("cantidadAceites", cantidadAceites);
                intent.putExtra("lcierreRespuestaApi", cierreRespuestaApi);
                intent.putExtra("accesoUsuario", accesoUsuario);

                startActivity(intent);
            }
        });

    }
    // Se crea el metodo valorFajilla
    public void obtenerTotales(){
        // Creamos la variable data para poder llamar a las variables que usaremos de la clase SQLiteBD
        final SQLiteBD data = new SQLiteBD(getApplicationContext());
        // Declaramos la URl que se ocupara para el metodo obtenerTotales
        String url = "http://"+data.getIpEstacion()+"/CorpogasService/api/cierres/registrar/sucursal/"+data.getIdSucursal()+"/isla/"+islaId+"/usuario/"+idusuario+"/origen/1";
        // Utilizamos el metodo POST para obtener Cantidad, Total y ProductoDescripcion
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                maintitle = new ArrayList<String>();
                subtitle = new ArrayList<String>();
                total = new ArrayList<String>();

                try{
                    JSONObject cierres = new JSONObject(response);
                    JSONObject sl1 = cierres.getJSONObject("ObjetoRespuesta");
                    JSONArray array = sl1.getJSONArray("CierreDetalleCategoriaProducto");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject obj1 = array.getJSONObject(i);
                        cantidad = obj1.getString("Cantidad");
                        total2 = obj1.getString("Total");
                        descripcion = obj1.getString("ProductoDescripcion");
                        totalLitros.put(cantidad);
                        importe.put(total2);

                        // Se le asignan los valores obtenidos a cada lista creada.
                        maintitle.add(cantidad);
                        subtitle.add(descripcion);
                        total.add(total2);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                // Se crea el Adaptador para mostrar los datos de cantidad, descripcion y total
                final ListAdapterBilletes adapter = new ListAdapterBilletes(TotalProductos.this,maintitle,subtitle,total);
                mListView = (ListView) findViewById(R.id.list);
                mListView.setAdapter(adapter);
                // Se le asigna una variable a cada metodo que calculara el total de litros e importe
                sumaTotalImporte = calcularTotalImporte();
                // Se convierte el Total en formato BigDecimal
                BigDecimal bd = new BigDecimal(sumaTotalImporte);
                // Se limite el resultado a 4 decimales y se redondea
                String resultadoPrueba = String.valueOf(bd.setScale(4, RoundingMode.HALF_UP));
                // Se le asigna el resultado a el TextView correspondiente
                txtImporteTotal.setText("TOTAL: $"+resultadoPrueba);

                // Funcion para capturar errores
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        // Añade la peticion a la cola
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    // Se crea el metodo calcularTotalImporte para sumar los valores que tiene el arreglo importe
    public double calcularTotalImporte(){
        double sumamax = 0;
        for (int i = 0; i < importe.length() ; i++) {
            double suma = 0;
            try{
                suma = importe.getDouble(i);
                sumamax += suma;
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return sumamax;
    }

    //Metodo para regresar a la actividad principal
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), CierreFormaPago.class);
        intent.putExtra("origenId","1");
        intent.putExtra("islaId",islaId);
        intent.putExtra("dineroBilletes",dineroBilletes);
        intent.putExtra("dineroMorralla",dineroMorralla);
        intent.putExtra("subTotalOficina",subTotalOficina);
        intent.putExtra("VentaProductos", VentaProductos);
        intent.putExtra("cantidadAceites", cantidadAceites);
        intent.putExtra("lcierreRespuestaApi", cierreRespuestaApi);
        intent.putExtra("accesoUsuario", accesoUsuario);
        startActivity(intent);

    }

}