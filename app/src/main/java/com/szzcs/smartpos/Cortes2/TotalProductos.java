package com.szzcs.smartpos.Cortes2;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.List;

public class TotalProductos extends AppCompatActivity {

    // Se declaran las variables que se usaran en este Activity
    TextView txtlitrosPiezas, txtImporteTotal;
    double sumaLitrosPiezas, sumaTotalImporte;
    String cantidad, total2, descripcion;
    JSONArray totalLitros = new JSONArray();
    JSONArray importe = new JSONArray();
    List<String> maintitle;
    List<String> subtitle;
    List<String> total;
    ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_total_productos);
        obtenerTotales();

        // Hacemos la relacion de las variables con los objetos del layout
        txtlitrosPiezas = (TextView) findViewById(R.id.textLitrosPiezas);
        txtImporteTotal = (TextView) findViewById(R.id.textImporteProd);

    }
    // Se crea el metodo valorFajilla
    public void obtenerTotales(){
        // Creamos la variable data para poder llamar a las variables que usaremos de la clase SQLiteBD
        final SQLiteBD data = new SQLiteBD(getApplicationContext());
        // Declaramos la URl que se ocupara para el metodo obtenerTotales
        String url = "http://"+data.getIpEstacion()+"/CorpogasService/api/cierres/registrar/sucursal/"+data.getIdSucursal()+"/isla/2/usuario/1/origen/1";
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
                    sumaLitrosPiezas = calcularTotalLitros();
                    sumaTotalImporte = calcularTotalImporte();
                    // Convertimos el valor obtenido de litros e importe
                    String stringSumaLitrosPiezas = Double.toString(sumaLitrosPiezas);
                    String stringSumaTotalImporte = Double.toString(sumaTotalImporte);
                    // Se le asigna el resultado a el TextView correspondiente
                    txtlitrosPiezas.setText(stringSumaLitrosPiezas);
                    txtImporteTotal.setText(stringSumaTotalImporte);

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
    // Se crea el metodo calcularTotalLitros para sumar los valores que tiene el arreglo totalLitros
    public double calcularTotalLitros (){
        double sumamax = 0;
        for (int i = 0; i < totalLitros.length() ; i++) {
            double suma = 0;
            try {
                suma = totalLitros.getDouble(i);
                sumamax += suma;
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return sumamax;
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

}