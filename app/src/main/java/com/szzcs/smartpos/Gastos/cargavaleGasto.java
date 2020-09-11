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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.szzcs.smartpos.Munu_Principal;
import com.szzcs.smartpos.R;

import org.json.JSONArray;
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

        SubTotal =findViewById(R.id.subTot);
        Descripcion = findViewById(R.id.descripcion);

        String islaId = getIntent().getStringExtra("isla");
        String turnoId = getIntent().getStringExtra("turno");
        Button enviar = findViewById(R.id.btnEnviar);
        enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SubTotal.length()==0 )       //.length() >0)
                {
                    Toast.makeText(getApplicationContext(), "digite el importe", Toast.LENGTH_LONG).show();
                } else { if (Descripcion.length()==0){
                        Toast.makeText(getApplicationContext(), "digite la descripción", Toast.LENGTH_LONG).show();
                        }else {
                            EnviarGastos();
                        }
                }
            }
        });
    }
    private void EnviarGastos() {
        final String turnoId = getIntent().getStringExtra("turno");

        String url = "http://10.2.251.58/CorpogasService/api/Turnos/fechaTrabajo/sucursal/"+sucursalId+"/turno/"+turnoId;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject resultadorespuesta = new JSONObject(response);
                    String valor = resultadorespuesta.getString("ObjetoRespuesta");
                    GuardarGasto(valor, turnoId);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),error.toString(), Toast.LENGTH_LONG).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }


private void GuardarGasto(String fechatrabajo, String turnoId){

    String islaId = getIntent().getStringExtra("isla");
    Date date = new Date();
    String fechaTrabajo= fechatrabajo;

    //SQLiteBD data = new SQLiteBD(getApplicationContext());
    //String URL = "http://"+data.getIpEstacion()+"/CorpogasService/api/tanqueLleno/EnviarProductos";
    String URL = "http://10.2.251.58/CorpogasService/api/CajaChicas";
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
