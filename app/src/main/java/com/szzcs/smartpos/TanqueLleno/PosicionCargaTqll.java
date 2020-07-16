package com.szzcs.smartpos.TanqueLleno;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.szzcs.smartpos.R;
import com.szzcs.smartpos.TanqueLleno.claveuser_tqll;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PosicionCargaTqll extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posicion_carga_tqll);
        verPosiciones();
    }


    public void verPosiciones(){
        String url = "http://10.0.1.20/TransferenciaDatosAPI/api/PosCarga/GetMax";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                //Obtenemos el linear layout donde colocar los botones
                LinearLayout llBotonera = (LinearLayout) findViewById(R.id.posicionCarga);

                //Creamos las propiedades de layout que tendrán los botones.
                //Son LinearLayout.LayoutParams porque los botones van a estar en un LinearLayout.
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT );

                //Creamos los botones en bucle
                for (int i=1; i<=Integer.parseInt(response); i++){
                    Button button = new Button(getApplicationContext());
                    //Asignamos propiedades de layout al boton
                    button.setLayoutParams(lp);
                    //Asignamos Texto al botón
                    button.setText("" + i);

                    //Asignamose el Listener
                    button.setOnClickListener(new PosicionCargaTqll.ButtonOnClickListener(this));
                    //Añadimos el botón a la botonera
                    llBotonera.addView(button);

                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),error.toString(), Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> parametros = new HashMap<String, String>();



                return parametros;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this.getApplicationContext());
        requestQueue.add(stringRequest);

    }


    class ButtonOnClickListener implements OnClickListener {
        public ButtonOnClickListener(Response.Listener<String> listener) {
        }

        @Override
        public void onClick(View v) {
//            Button b = (Button) v;
//            Intent intente = new Intent(getApplicationContext(), claveuser_tqll.class);
//            intente.putExtra("pos",b.getText());
//            startActivity(intente);

            Bundle bundle = getIntent().getExtras();
            final String track2 = bundle.getString("track");
            final String nip = bundle.getString("nip");
            final Button b = (Button)v;

            String url = "http://10.0.1.20/TransferenciaDatosAPI/api/tarjetas/sendinfo";

            StringRequest eventoReq = new StringRequest(Request.Method.POST,url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Toast.makeText(getApplicationContext(),response,Toast.LENGTH_LONG).show();
                            MesanjeVista(response);

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
                    params.put("RequestId","1");
                    params.put("PosCarga", (String) b.getText());
                    params.put("Tarjeta",track2);
                    params.put("NuTarjetero","1");
                    return params;
                }
            };

            // Añade la peticion a la cola
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(eventoReq);

        }
    }

    private void MesanjeVista(String response) {
        try {
            JSONObject validar = new JSONObject(response);
            String valido = validar.getString("sMensaje");

            if (valido.isEmpty()){
                Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_LONG).show();
            }else {
                Toast.makeText(getApplicationContext(),valido,Toast.LENGTH_LONG).show();


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }




}