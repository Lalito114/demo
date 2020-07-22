package com.szzcs.smartpos.TanqueLleno;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonObject;
import com.szzcs.smartpos.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PedirNipTqll extends AppCompatActivity {

    String poscarga;
    String mjason;
    TextView nip;
    TextView placas;
    TextView odometro;
    JSONObject tanqueLleno = new JSONObject();
    Button btnEnviar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedir_nip_tqll);
        poscarga = getIntent().getStringExtra("poscarga");
        // mjason = getIntent().getStringExtra("mjason");

        btnEnviar = findViewById(R.id.btnEnviar);
        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    //lee valores de placas, odometro y nip
                    nip= findViewById(R.id.edtvnip);
                    placas = findViewById(R.id.edtvPlacas);
                    odometro = findViewById(R.id.edtvOdometro);
                    tanqueLleno.put("Odometro",odometro);
                    tanqueLleno.put("Placas",placas );
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                EnviarDatos();
            }
        });



    }

    private void EnviarDatos() {
        String url = "http://10.0.1.20/TransferenciaDatosAPI/api/tarjetas/sendtarjeta";

        StringRequest eventoReq = new StringRequest(Request.Method.POST,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(getApplicationContext(),response,Toast.LENGTH_LONG).show();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("RequestId", "3");
                params.put("PosCarga",poscarga);
                params.put("Tarjeta","3999999900100055");
                params.put("NuTarjetero","1");
                params.put("NIP",nip.toString());
                params.put("tanqueLleno",tanqueLleno.toString());
                params.put("Productos",mjason.toString());
                return params;
            }
        };

        // Añade la peticion a la cola
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(eventoReq);
    }


}