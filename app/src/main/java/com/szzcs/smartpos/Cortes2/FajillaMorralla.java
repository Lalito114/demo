package com.szzcs.smartpos.Cortes2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.szzcs.smartpos.R;

import org.json.JSONArray;
import org.json.JSONObject;

public class FajillaMorralla extends AppCompatActivity {

    EditText fajillasMorralla;
    Button btnFajillasMorralla;
    String precioFajilla;
    int fajillaMorralla;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fajilla_morralla);
        valorFajilla();

        fajillasMorralla = (EditText) findViewById(R.id.editFajillasMorralla);
        btnFajillasMorralla = (Button) findViewById(R.id.btnFajillasMorralla);



        btnFajillasMorralla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String morralla = fajillasMorralla.getText().toString();

                if(morralla.isEmpty()){

                    Toast.makeText(getApplicationContext(),"ERROR 401: Ingresar Numero de Fajillas de Morralla",Toast.LENGTH_LONG).show();
                }
                    int dineroMorralla = Integer.parseInt(morralla) * fajillaMorralla;
                    Toast.makeText(getApplicationContext(),"Fue un Total de "+ dineroMorralla + " pesos",Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getApplicationContext(),SubOfiBilletes.class);
                    startActivity(intent);
            }
        });



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
                        if(tipoFajilla.equals("2")){
                            fajillaMorralla = Integer.parseInt(precioFajilla);
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