package com.szzcs.smartpos.Ticket.Monederos;

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
import com.szzcs.smartpos.configuracion.SQLiteBD;

import org.json.JSONException;
import org.json.JSONObject;

public class despachdorclave extends AppCompatActivity {

    String iduser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SQLiteBD data = new SQLiteBD(this);
        setContentView(R.layout.activity_despachdorclave);
        this.setTitle(data.getRazonSocial());
        Button siguinte = findViewById(R.id.btnsiguientetic);
        siguinte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                idUsuario();
            }
        });
    }

    public void idUsuario(){

        EditText pasword = (EditText) findViewById(R.id.edtclavedespachador);
        final String pass = pasword.getText().toString();
        final String idusuario;

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
                            iduser = validar.getString("Id");
                            if (valido == "true"){
                                Intent intent = new Intent(despachdorclave.this, posicionesDespachador.class);
                                intent.putExtra("IdUsuario", iduser);
                                intent.putExtra("ClaveDespachador", pass);
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

    }
}