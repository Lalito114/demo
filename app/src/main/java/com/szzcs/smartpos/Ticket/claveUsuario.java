package com.szzcs.smartpos.Ticket;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.szzcs.smartpos.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class claveUsuario extends AppCompatActivity {
    TextView usuario, carga;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clave_usuario);

        usuario= findViewById(R.id.usuario);
        carga = findViewById(R.id.carga);
        Button btnenviar = (Button) findViewById(R.id.enviar);
        btnenviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String posicion;
                posicion = getIntent().getStringExtra("pos");
                EditText pasword = (EditText) findViewById(R.id.pasword);
                final String pass = pasword.getText().toString();


                if (pass.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Ingresa la contraseña",Toast.LENGTH_SHORT).show();
                }else{
                    //----------------------Aqui va el Volley ----------------------------
                    String url = "http://10.0.1.20/TransferenciaDatosAPI/api/cve/GetValidaCve";

                    StringRequest eventoReq = new StringRequest(Request.Method.POST,url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        JSONObject validar = new JSONObject(response);
                                        String valido = validar.getString("EsValido");
                                        String idusuario = validar.getString("IdUsuario");
                                        String reimpresion = validar.getString("Reimpresion");
                                        String ticket = validar.getString("TicketImpreso");
                                        if (valido == "true"){
                                            usuario.setText(idusuario);
                                            carga.setText(posicion);
                                            Intent intent = new Intent(getApplicationContext(), formas_de_pago.class);
                                            intent.putExtra("car",posicion);
                                            intent.putExtra("user",idusuario);
                                            startActivity(intent);
                                        }else{
                                            Toast.makeText(getApplicationContext(),"La contraseña es incorecta",Toast.LENGTH_SHORT).show();
                                        }
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
                            params.put("PosCarga",posicion);
                            params.put("Clave",pass);
                            return params;
                        }
                    };

                    // Añade la peticion a la cola
                    RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                    requestQueue.add(eventoReq);



                    //-------------------------Aqui termina el volley --------------
                }

            }
        });
    }


}
