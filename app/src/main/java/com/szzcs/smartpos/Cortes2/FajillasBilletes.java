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

public class FajillasBilletes extends AppCompatActivity {

    EditText folioInicial, folioFinal;
    Button btnValidaFajillas;
    String precioFajilla;
    public int dineroBilletes;
    int fajillaBillete;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fajillas_billetes);
        valorFajilla();



        folioInicial = (EditText) findViewById(R.id.editFolioInicialBilletes);
        folioFinal = (EditText) findViewById(R.id.editFolioFinalBilletes);
        btnValidaFajillas = (Button) findViewById(R.id.btnFajillaBilletes);

        btnValidaFajillas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String inicial = folioInicial.getText().toString();
                String foliof = folioFinal.getText().toString();

                if (inicial.isEmpty() || foliof.isEmpty()){

                    Toast.makeText(getApplicationContext(),"Error 301: Se requiere un Folio Inicial y Folio Final",Toast.LENGTH_LONG).show();
                }else{
                    if(Integer.parseInt(foliof) <= Integer.parseInt(inicial)){
                        Toast.makeText(getApplicationContext(),"Error 302: Verifica tus Numeros de Folio",Toast.LENGTH_LONG).show();
                    }else{
                        int folios = (Integer.parseInt(foliof) - Integer.parseInt(inicial)) + 1;
                        dineroBilletes = folios * fajillaBillete;
                        Toast.makeText(FajillasBilletes.this, "Fue un Total de " + dineroBilletes + " pesos", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getApplicationContext(),FajillaMorralla.class);
                        intent.putExtra("dinero",dineroBilletes);
                        intent.putExtra("fajillaBillete",fajillaBillete);
                        startActivity(intent);
                    }
                }

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