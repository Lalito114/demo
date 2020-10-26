package com.szzcs.smartpos.Cortes2;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
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
import com.google.gson.Gson;
import com.szzcs.smartpos.Munu_Principal;
import com.szzcs.smartpos.Productos.VentasProductos;
import com.szzcs.smartpos.R;
import com.szzcs.smartpos.configuracion.SQLiteBD;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.ErrorListener;

public class Clave extends AppCompatActivity {

    String pass, idSucursal, idUsuario, contra;
    TextView usuario, estacion;
    EditText password;

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
                pass = password.getText().toString();

                //Si no se terclea nada envia mensaje de teclear contraseña
                if (pass.isEmpty()) {
                    password.setError("Ingresa tu contraseña");
                }else{
                    datosUsuario();
                }
            }
        });
    }
    public void datosUsuario(){

        String URL_LOGIN = "http://10.0.1.20/CorpogasService/api/SucursalEmpleados/clave/"+pass;
        // Utilizamos el metodo Post para validar la contraseña
        StringRequest eventoReq = new StringRequest(Request.Method.GET,URL_LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.equals("null")){
                            password.setError("La contraseña es Incorrecta");
                        }
                        try {
                            //Se instancia la respuesta del json
                            JSONObject validar = new JSONObject(response);
                            String valido = validar.getString("Activo");
                            idSucursal = validar.getString("SucursalId");
                            String nombre = validar.getString("NombreCompleto");
                            contra = validar.getString("Clave");
                            idUsuario = validar.getString("Id");
                            String rol = validar.getString("Rol");
                            JSONObject obj1 = new JSONObject(rol);
                            String numeroInterno = obj1.getString("NumeroInterno");

                            if(pass.equals(contra) && numeroInterno.equals("3")){
                                Intent intent = new Intent(getApplicationContext(), IslasEstacion.class);
                                intent.putExtra("password", pass);
                                intent.putExtra("idsucursal",idSucursal);
                                intent.putExtra("idusuario",idUsuario);
                                intent.putExtra("nombreCompleto",nombre);
                                startActivity(intent);
                            }else{
                                password.setError("No eres Jefe de Isla");
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
                String algo = new String(error.networkResponse.data) ;
                String json = new Gson().toJson(algo);
                try {
                    //creamos un json Object del String algo
                    JSONObject errorCaptado = new JSONObject(algo);
                    //Obtenemos el elemento ExceptionMesage del errro enviado
                    String errorMensaje = errorCaptado.getString("ExceptionMessage");
                    Toast.makeText(Clave.this, errorMensaje, Toast.LENGTH_LONG).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        // Añade la peticion a la cola
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(eventoReq);

    }
    //Metodo para regresar a la actividad principal
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), Munu_Principal.class);

        startActivity(intent);
        // finish();
    }
}