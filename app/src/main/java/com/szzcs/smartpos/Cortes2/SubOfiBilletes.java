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
import android.widget.Toast;

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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class SubOfiBilletes extends AppCompatActivity {
    ListView mListView;
    List<String> maintitle;
    List<String> subtitle;
    List<String> total;
    JSONObject denom;
    String denominacion;
    ArrayList arrayMonto = new ArrayList();
    Double result;
    Button btnaceptar;
    JSONArray prueba2 = new JSONArray();
    double sumainter;
    String precioFajilla;
    int fajillaBillete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_ofi_billetes);

        denominacionBilletes();
        valorFajilla();
        btnaceptar = findViewById(R.id.aceptarBilletes);

        btnaceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sumainter <= (fajillaBillete-10)){
                    Toast.makeText(SubOfiBilletes.this, "Se registro un Total de "+ sumainter + " pesos", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getApplicationContext(),SubOfiGasopass.class);
                    startActivity(intent);

                }else{
                    Toast.makeText(SubOfiBilletes.this, "Existe un error", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private void denominacionBilletes() {
        String url = "http://10.2.251.58/CorpogasService/api/DineroDenominaciones";
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
        mListView = (ListView) findViewById(R.id.list);
        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                final String ray = arrayMonto.get(position).toString();

                try {
                    final EditText input = new EditText(getApplicationContext());
                    input.setTextColor(Color.BLACK);
                    input.setGravity(Gravity.CENTER);
                    input.setTextSize(22);
                    input.setInputType(InputType.TYPE_CLASS_NUMBER);

                    AlertDialog.Builder builder = new AlertDialog.Builder(SubOfiBilletes.this);
                    builder.setTitle("Ingresa Cantidad \n");
                    builder.setView(input)

                            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    String denomi = input.getText().toString();
                                    result = Double.parseDouble(denomi) * Double.parseDouble(ray);
                                    if (result > fajillaBillete) {
                                        Toast.makeText(SubOfiBilletes.this, "No puedes superar el valor de 1 Fajilla", Toast.LENGTH_SHORT).show();
                                    } else {
                                        maintitle.set(position, denomi);
                                        total.set(position, String.valueOf(result));
                                        final String ray2 = result.toString();

                                        sumainter = totalBilletes() + Double.parseDouble(ray2);

                                        if ((totalBilletes()<= (fajillaBillete-10)) && (sumainter <= (fajillaBillete-10))){
                                            Toast.makeText(SubOfiBilletes.this, "Cantidad Agregada", Toast.LENGTH_SHORT).show();
                                            prueba2.put(ray2);
                                        }else{
                                            Toast.makeText(SubOfiBilletes.this, "Los Valores que ingresaste pueden ser 1 fajilla", Toast.LENGTH_SHORT).show();
                                        }
                                        ListAdapterBilletes adapter = new ListAdapterBilletes(SubOfiBilletes.this, maintitle, subtitle, total);
                                        mListView.setAdapter(adapter);
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

    public void valorFajilla(){
//        final String sucursalid = getIntent().getStringExtra("idsucursal");
        String url = "http://10.2.251.58/CorpogasService/api/PrecioFajillas/Sucursal/1";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray array = new JSONArray(response);

                    for (int i = 0; i < array.length() ; i++) {
                        JSONObject sl1 = array.getJSONObject(i);
                        String tipoFajilla = sl1.getString("TipoFajillaId");
                        precioFajilla = sl1.getString("Precio");
                        if(tipoFajilla.equals("1")){
                            fajillaBillete = Integer.parseInt(precioFajilla);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }
}