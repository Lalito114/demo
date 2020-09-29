package com.szzcs.smartpos.Cortes2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.szzcs.smartpos.Productos.VentasProductos;
import com.szzcs.smartpos.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.ErrorListener;

public class Clave extends AppCompatActivity {

    EditText password;
    TextView usuario;
    TextView estacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clave);

        usuario = findViewById(R.id.usuario);
        estacion = findViewById(R.id.estacion);
        password = findViewById(R.id.pasword);


        //Crea Boton Enviar
        Button btnenviar = (Button) findViewById(R.id.enviar);
        //En espera a recibir el evento Onclick del boton Enviar
        btnenviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Se lee el password del objeto y se asigna a variable


                final String pass = password.getText().toString();

                //Si no se terclea nada envia mensaje de teclear contraseña
                if (pass.isEmpty()){
                    password.setError("Ingresa tu contraseña");
                }else{
                    //----------------------Aqui va el Volley Si se tecleo contraseña----------------------------
                    String URL_LOGIN = "http://10.2.251.58/CorpogasService/api/SucursalEmpleados/clave/"+pass;
                  // Utilizamos el metodo Post para validar la contraseña
                    StringRequest eventoReq = new StringRequest(Request.Method.GET,URL_LOGIN,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        //Se instancia la respuesta del json
                                        JSONObject validar = new JSONObject(response);
                                        String valido = validar.getString("Activo");
                                        String idSucursal = validar.getString("SucursalId");
                                        String idRol = validar.getString("RolId");
                                        String contra = validar.getString("Clave");

                                        if (valido == "true"){

                                            //Se instancia y se llama a la clase formas de pago
                                            Intent intent = new Intent(getApplicationContext(), Lecturas.class);
                                            intent.putExtra("password", pass);
                                            intent.putExtra("idsucursal",idSucursal);

                                            startActivity(intent);

                                        }else{
                                            //Si no es valido se envia mensaje

                                            Toast.makeText(getApplicationContext(),"La contraseña es incorecta",Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (JSONException e) {
                                        //herramienta  para diagnostico de excepciones
                                        e.printStackTrace();
                                    }
                                }
                                //funcion para capturar errores
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_SHORT).show();
                        }
                    });

                    // Añade la peticion a la cola
                    RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                    requestQueue.add(eventoReq);



                    //-------------------------Aqui termina el volley --------------
                }

            }
        });
    }
}