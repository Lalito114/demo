package com.szzcs.smartpos.Gastos;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.szzcs.smartpos.Munu_Principal;
import com.szzcs.smartpos.Pendientes.ticketPendientes;
import com.szzcs.smartpos.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class claveGastos extends AppCompatActivity {
    TextView usuario, carga;
    EditText password;
    String idisla;
    String idTurno;
    final String sucursalid = "1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clave_gastos);
        //lee valores usuario y carga
        usuario= findViewById(R.id.usuario);
        carga = findViewById(R.id.carga);
        //carga.setText(getIntent().getStringExtra("posicion"));
        password = (EditText) findViewById(R.id.pasword);

        validaClave();
    }

    public void obtenerIsla() {
        final String pass = password.getText().toString();


        String url = "http://10.2.251.58/CorpogasService/api/estacionControles/estacion/"+ sucursalid +"/ClaveEmpleado/" +pass;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i <jsonArray.length() ; i++) {
                        JSONObject claveusuario = jsonArray.getJSONObject(i);
                        idisla = claveusuario.getString("IslaId");
                        idTurno= claveusuario.getString("TurnoId");
                    }
                    //usuario.setText(idusuario);
                    //Se instancia y se llama a la clase formas de pago
                    Intent intent = new Intent(getApplicationContext(), autizacionGastos.class);
                    //intent.putExtra("user", idusuario);
                    intent.putExtra("isla", idisla);
                    intent.putExtra("turno", idTurno);
                    startActivity(intent);

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

    private void  validaClave(){
        //Crea Boton Enviar
        //Button btnenviar = (Button) findViewById(R.id.enviar);
        ImageView btnenviar = findViewById(R.id.imgGasto);
        //En espera a recibir el evento Onclick del boton Enviar
        btnenviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Se lee el password del objeto y se asigna a variable
                final String pass = password.getText().toString();

                //Si no se terclea nada envia mensaje de teclear contraseña
                if (pass.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Ingresa la contraseña",Toast.LENGTH_SHORT).show();
                }else {
                    ObtenerClave(pass);
                }

            }
        });
    }
    private void ObtenerClave(final String pass){
        //----------------------Aqui va el Volley Si se tecleo contraseña----------------------------

        //Conexion con la base y ejecuta valida clave
        String url = "http://10.2.251.58/CorpogasService/api/SucursalEmpleados/clave/"+pass;
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
                            String idRoll = validar.getString("RolId");

                            if (valido.equals("true") && idRoll.equals("3")){ // 1 es Jefe de ISla Autorizado por Gerente
                                //Si es valido se asignan valores
                                obtenerIsla();
                            }else{
                                //Si no es valido se envia mensaje
                                try {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(claveGastos.this);
                                    builder.setTitle("Contraseña Incorrecta");
                                    builder.setMessage("Debe ser de Jefe de Isla")
                                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    password.setText("");
                                                }
                                            }).show();
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                                //Toast.makeText(getApplicationContext(),"La contraseña es incorecta, debe ser de un Jefe de Isla",Toast.LENGTH_SHORT).show();
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