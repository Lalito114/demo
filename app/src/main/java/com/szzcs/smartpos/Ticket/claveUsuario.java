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
import com.szzcs.smartpos.configuracion.SQLiteBD;

import org.json.JSONException;
import org.json.JSONObject;

//Clase para validar la contraseña del empleado
public class claveUsuario extends AppCompatActivity {
    TextView usuario, carga;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clave_usuario);
        SQLiteBD data = new SQLiteBD(getApplicationContext());
        this.setTitle(data.getNombreEsatcion());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //lee valores usuario y carga
        usuario= findViewById(R.id.usuario);
        carga = findViewById(R.id.carga);
        //Crea Boton Enviar
        Button btnenviar = (Button) findViewById(R.id.btnsiguiente);
        //En espera a recibir el evento Onclick del boton Enviar
        btnenviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Se lee el password del objeto y se asigna a variable
                final String posicion;
                posicion = getIntent().getStringExtra("pos");
                EditText pasword = (EditText) findViewById(R.id.pasword);
                final String pass = pasword.getText().toString();

                //Si no se terclea nada envia mensaje de teclear contraseña
                if (pass.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Ingresa la contraseña",Toast.LENGTH_SHORT).show();
                }else{
                    //----------------------Aqui va el Volley Si se tecleo contraseña----------------------------

                    //Conexion con la base y ejecuta valida clave
                    SQLiteBD data = new SQLiteBD(getApplicationContext());
                    String url = "http://"+data.getIpEstacion()+"/CorpogasService/api/SucursalEmpleados/clave/"+pass;

                    // Utilizamos el metodo Post para validar la contraseña
                    StringRequest eventoReq = new StringRequest(Request.Method.GET,url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        //Se instancia la respuesta del json
                                        JSONObject validar = new JSONObject(response);
                                        String valido = validar.getString("Activo");
                                        String idusuario = validar.getString("Id");
                                        if (valido == "true"){
                                            //Si es valido se asignan valores
                                            usuario.setText(idusuario);
                                            carga.setText(posicion);
                                            //Se instancia y se llama a la clase formas de pago
                                            Intent intent = new Intent(getApplicationContext(), formas_de_pago.class);
                                            intent.putExtra("car",posicion);
                                            intent.putExtra("user",idusuario);
                                            startActivity(intent);
                                            finish();
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
