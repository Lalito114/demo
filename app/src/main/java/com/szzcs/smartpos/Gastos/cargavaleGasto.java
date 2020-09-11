package com.szzcs.smartpos.Gastos;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.szzcs.smartpos.Munu_Principal;
import com.szzcs.smartpos.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class cargavaleGasto extends AppCompatActivity {
    ListView list;
    TextView txtDescripcion, txtClave, isla, turno, usuario;
    TextView SubTotal, Descripcion;
    String EstacionId = "1";
    String sucursalId = "1";
    String idisla, idTurno;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cargavale_gasto);
         SubTotal =findViewById(R.id.SubTot);
        Descripcion = findViewById(R.id.Descripcion);

        String islaId = getIntent().getStringExtra("isla");
        String turnoId = getIntent().getStringExtra("turno");
        EnviarGastos();
    }
    private void EnviarGastos() {
        String islaId = getIntent().getStringExtra("isla");
        String turnoId = getIntent().getStringExtra("turno");
        Date date = new Date();
        //Formato para el dia, mes y año
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        //Formato para la hora, minutos y segundos
        DateFormat hourFormat = new SimpleDateFormat("HH:mm:ss");
        String fechaTrabajo= (dateFormat.format(date)+ " " + hourFormat.format(date));

        //SQLiteBD data = new SQLiteBD(getApplicationContext());
        //String URL = "http://"+data.getIpEstacion()+"/CorpogasService/api/tanqueLleno/EnviarProductos";
        String URL = "http://10.2.251.58/CorpogasService/api/Gastos";
        final JSONObject mjason = new JSONObject();
        RequestQueue queue = Volley.newRequestQueue(this);
        try {
            mjason.put("EstacionId",EstacionId);
            mjason.put("TurnoId", turnoId); //turno.getText().toString());
            mjason.put("TurnoSucursalId",sucursalId); //turno.getText().toString());Sucursal
            mjason.put("IslaId", islaId);
            mjason.put("IslaEstacionId",EstacionId);
            mjason.put("FechaTrabajo",fechaTrabajo);
            mjason.put("Descripcion", Descripcion.getText().toString());
            mjason.put("Importe", SubTotal.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest request_json = new JsonObjectRequest(Request.Method.POST, URL, mjason, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Intent intente = new Intent(getApplicationContext(), Munu_Principal.class);
                startActivity(intente);
                Toast.makeText(getApplicationContext(),"Gasto Cargado Exitosamente",Toast.LENGTH_LONG).show();
                //Toast.makeText(getApplicationContext(),response.toString(),Toast.LENGTH_LONG).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

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
                return Response.success(mjason, HttpHeaderParser.parseCacheHeaders(response));
            }
        };
        queue.add(request_json);

    }

}
