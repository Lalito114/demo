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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TotalProductos extends AppCompatActivity {

    ListView mListView;
    List<String> maintitle;
    List<String> subtitle;
    List<String> total;
    String cantidad;
    String total2;
    String descripcion;
    JSONArray totalLitros = new JSONArray();
    JSONArray importe = new JSONArray();
    TextView txtlitrosPiezas, txtImporteTotal;
    double sumaLitrosPiezas;
    double sumaTotalImporte;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_total_productos);
        obtenerTotales();


        txtlitrosPiezas = (TextView) findViewById(R.id.textLitrosPiezas);
        txtImporteTotal = (TextView) findViewById(R.id.textImporteProd);





    }

    public void obtenerTotales(){
        String url = "http://10.2.251.58/CorpogasService/api/cierres/registrar/sucursal/1/isla/2/origen/1";
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


                            maintitle.add(cantidad);
                            subtitle.add(descripcion);
                            total.add(total2);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    final ListAdapterBilletes adapter = new ListAdapterBilletes(TotalProductos.this,maintitle,subtitle,total);
                    mListView = (ListView) findViewById(R.id.list);
                    mListView.setAdapter(adapter);
                    sumaLitrosPiezas = calcularTotalLitros();
                    sumaTotalImporte = calcularTotalImporte();
                    String stringSumaLitrosPiezas = Double.toString(sumaLitrosPiezas);
                    String stringSumaTotalImporte = Double.toString(sumaTotalImporte);
                    txtlitrosPiezas.setText(stringSumaLitrosPiezas);
                    txtImporteTotal.setText(stringSumaTotalImporte);


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });

            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(stringRequest);
    }
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