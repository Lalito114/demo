package com.szzcs.smartpos.Cortes2;

import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Parcelable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SubOfiGasopass extends AppCompatActivity {

    ListView mListView;
    List<String> maintitle;
    List<String> subtitle;
    List<String> total;
    Spinner tipoVales;
    JSONObject denom;
    String denominacion;
    ArrayList arrayMonto = new ArrayList();
    ArrayList<ValePapelDenominacion> lCarrito = new ArrayList<ValePapelDenominacion>();
    Double result;
    JSONArray prueba2 = new JSONArray();
    Button btnaceptar;
    double sumainter;
    ArrayList<String> prueba = new ArrayList<>();
    ArrayAdapter<CharSequence> adapterTipoVales;
    long tipoValeSeleccion;
    String nombreVale;
    ListAdapterBilletes adapter;
    ListAdapterBilletes2 adapter2;

    String islaId;
    String usuarioId;
    String sumaPicosBilletes;
    String dineroBilletes;
    String dineroMorralla;
    String VentaProductos;
    String cantidadAceites;

    RespuestaApi<Cierre> cierreRespuestaApi;
    long cierreId;
    long turnoId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_ofi_gasopass);


        tipoVales = findViewById(R.id.idSpinner);
        btnaceptar = findViewById(R.id.btnAceptar);

        islaId =  getIntent().getStringExtra("islaId");
        usuarioId =getIntent().getStringExtra("idusuario");
        sumaPicosBilletes = getIntent().getStringExtra("picoBilletes");
        dineroBilletes = getIntent().getStringExtra("dineroBilletes");
        dineroMorralla = getIntent().getStringExtra("dineroMorralla");
        VentaProductos = getIntent().getStringExtra("VentaProductos");
        cantidadAceites = getIntent().getStringExtra("cantidadAceites");
        cierreRespuestaApi = (RespuestaApi<Cierre>) getIntent().getSerializableExtra( "lcierreRespuestaApi");
        turnoId = cierreRespuestaApi.getObjetoRespuesta().getTurnoId();
        cierreId = cierreRespuestaApi.getObjetoRespuesta().getId();

        tipoValesPapel();
        denominacionBilletes();

        btnaceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(lCarrito.size()> 0) {
                    if (sumainter <= 900000) {
                        Toast.makeText(SubOfiGasopass.this, "Se registro un Total de " + sumainter + " pesos", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), DesgloseVales.class);
                        intent.putExtra("lCarrito", lCarrito);
                        intent.putExtra("islaId",islaId);
                        intent.putExtra("idusuario", usuarioId);
                        intent.putExtra("sumaPicosBilletes",sumaPicosBilletes);
                        intent.putExtra("dineroBilletes",dineroBilletes);
                        intent.putExtra("dineroMorralla",dineroMorralla);
                        intent.putExtra("VentaProductos", VentaProductos);
                        intent.putExtra("cantidadAceites", cantidadAceites);
                        intent.putExtra("lcierreRespuestaApi", cierreRespuestaApi);
                        startActivity(intent);
                    } else {
                        Toast.makeText(SubOfiGasopass.this, "Existe un error", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(SubOfiGasopass.this, "Favor de capturar un tipo de vale", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void denominacionBilletes() {
        SQLiteBD data = new SQLiteBD(getApplicationContext());
        String url = "http://"+data.getIpEstacion()+"/CorpogasService/api/ValePapelDenominaciones";
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
        adapter2 = new ListAdapterBilletes2(this, maintitle, subtitle, total);
        mListView = (ListView) findViewById(R.id.list);
        mListView.setVisibility(View.INVISIBLE);
        mListView.setAdapter(adapter2);

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

                    AlertDialog.Builder builder = new AlertDialog.Builder(SubOfiGasopass.this);
                    builder.setTitle("Ingresa Cantidad \n");
                    builder.setView(input)

                            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    String denomi = input.getText().toString();
                                    //lCarrito.add(position);
                                    result = Double.parseDouble(denomi) * Double.parseDouble(ray);
                                    if (result > 900000) {
                                        Toast.makeText(SubOfiGasopass.this, "No puedes superar el valor de 1 Fajilla", Toast.LENGTH_SHORT).show();
                                    } else {


                                        for (int a = 0; a < lCarrito.size(); a++) {
                                            long tipovaleCarrito = lCarrito.get(a).TipoValePapelId;
                                            int posicionCarrito = lCarrito.get(a).Posicion;
                                            if(tipovaleCarrito == tipoValeSeleccion && posicionCarrito == position)
                                            {
                                                lCarrito.remove(a);
                                            }
                                        }

                                        ValePapelDenominacion valePapelDenominacion = new ValePapelDenominacion();
                                        valePapelDenominacion.TipoValePapelId = tipoValeSeleccion;
                                        valePapelDenominacion.Cantidad = Integer.parseInt(denomi);
                                        valePapelDenominacion.Importe = Double.parseDouble(ray);
                                        valePapelDenominacion.Total = result;
                                        valePapelDenominacion.Posicion = position;
                                        valePapelDenominacion.NombreVale = nombreVale;
                                        valePapelDenominacion.Denominacion = Double.parseDouble(denomi);
                                        lCarrito.add(valePapelDenominacion);


                                        maintitle.set(position, denomi);
                                        total.set(position, String.valueOf(result));
                                        final String ray2 = result.toString();

                                        sumainter = totalBilletes() + Double.parseDouble(ray2);

                                        if ((totalBilletes() <= 900000) && (sumainter <= 900000)){
                                            Toast.makeText(SubOfiGasopass.this, "Cantidad Agregada", Toast.LENGTH_SHORT).show();
                                            prueba2.put(ray2);
                                        }else {
                                            Toast.makeText(SubOfiGasopass.this, "Los Valores que ingresaste pueden ser 1 fajilla", Toast.LENGTH_SHORT).show();
                                        }
                                        proceso_grid(tipoValeSeleccion);
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

    public void tipoValesPapel(){
        SQLiteBD data = new SQLiteBD(getApplicationContext());
        String url = "http://"+data.getIpEstacion()+"/CorpogasService/api/TipoValePapeles";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONArray array = new JSONArray(response);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject obj1 = array.getJSONObject(i);
                        String descrip = obj1.getString("Descripcion");
                        prueba.add(descrip);

                    }
                    ArrayList<String> comboVales = new ArrayList<>();
                    comboVales.add("SELECCIONE");
                    for (int i = 0; i < prueba.size() ; i++) {
                        comboVales.add(String.valueOf(prueba.get(i)));
                    }

                    adapterTipoVales = new ArrayAdapter(getApplicationContext(),R.layout.custom_spinner,comboVales);

                    tipoVales.setAdapter(adapterTipoVales);

                    tipoVales.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            tipoValeSeleccion = id;
                            nombreVale = parent.getItemAtPosition(position).toString();


                            mListView.setAdapter(adapter2);
                            if (position == 1) {
                                proceso_grid(tipoValeSeleccion);
                                Toast.makeText(SubOfiGasopass.this, "SELECCIONADO: " + parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
                                mListView.setVisibility(View.VISIBLE);
                            }if (position == 2){
                                proceso_grid(tipoValeSeleccion);
                                Toast.makeText(SubOfiGasopass.this, "SELECCIONADO: " + parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
                                mListView.setVisibility(View.VISIBLE);
                            }if (position == 3){
                                proceso_grid(tipoValeSeleccion);
                                mListView.setVisibility(View.VISIBLE);
                                Toast.makeText(SubOfiGasopass.this, "SELECCIONADO: " + parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
                            }if (position == 4){
                                proceso_grid(tipoValeSeleccion);
                                mListView.setVisibility(View.VISIBLE);
                                Toast.makeText(SubOfiGasopass.this, "SELECCIONADO: " + parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });

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


    public void proceso_grid(long tipoValeSeleccion)
    {
        if(lCarrito.size()> 0)
        {
            //maintitle.removeAll(maintitle);

            for (int i = 0; i < maintitle.size(); i++) {
                maintitle.set(i,"");
                total.set(i,"");
            }

            int cantidad = 0;
            double totalcarrito = 0;
            int position = 0;
            for (int i = 0; i < lCarrito.size(); i++) {
                long tipoValeId =  lCarrito.get(i).TipoValePapelId;
                if(tipoValeSeleccion == tipoValeId)
                {
                    cantidad =  lCarrito.get(i).Cantidad;
                    totalcarrito = lCarrito.get(i).Total;
                    position = lCarrito.get(i).Posicion;
                    maintitle.set(position, String.valueOf(cantidad));
                    total.set(position, String.valueOf(totalcarrito));
                    adapter = new ListAdapterBilletes(SubOfiGasopass.this, maintitle, subtitle, total);
                    mListView.setAdapter(adapter);

                }
            }
        }
    }


    public double totalBilletes(){
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