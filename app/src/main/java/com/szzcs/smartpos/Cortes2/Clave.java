package com.szzcs.smartpos.Cortes2;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
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
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.szzcs.smartpos.Munu_Principal;
import com.szzcs.smartpos.Productos.VentasProductos;
import com.szzcs.smartpos.R;
import com.szzcs.smartpos.configuracion.SQLiteBD;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.ErrorListener;

public class Clave extends AppCompatActivity {

    String pass, idSucursal, idUsuario, contra;
    TextView usuario, estacion;
    EditText password;
    SQLiteBD data;
    RespuestaApi<AccesoUsuario> accesoUsuario;
    Type respuestaAccesoUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clave);

        usuario = findViewById(R.id.usuario);
        estacion = findViewById(R.id.estacion);
        password = findViewById(R.id.pasword);
        data = new SQLiteBD(getApplicationContext());

    }
    public void datosUsuario(){
        new Thread(new Runnable() {
            public void run() {
                //while(ejecutar) {
                try {
                    httpGetAccesoUsusario();
                    //ejecutar = false;

                } catch (IOException e) {
                    e.printStackTrace();
                }
                //}
                runOnUiThread(new Runnable() {
                    public void run() {
                       boolean  correcto = accesoUsuario.Correcto;
                        if(correcto == true){
                           if(accesoUsuario.getObjetoRespuesta().getClave().equals(pass) && accesoUsuario.getObjetoRespuesta().getNumeroInternoRol() == 3)
                           {
                                Intent intent = new Intent(getApplicationContext(), IslasEstacion.class);
                                intent.putExtra("accesoUsuario", accesoUsuario);
                                startActivity(intent);

                           }else{
                               password.setError("No eres jefe de isla");
                           }

                        }else{
                          password.setError(accesoUsuario.Mensaje);
                        }


                    }
                });
            }
        }).start();
    }

    public void  httpGetAccesoUsusario() throws IOException {

        String postUrl ="http://"+data.getIpEstacion()+"/CorpogasService/api/accesoUsuarios/sucursal/"+data.getIdSucursal()+"/clave/"+pass;
        HttpClient client = new DefaultHttpClient();
        HttpConnectionParams.setConnectionTimeout(client.getParams(), 5000);
        HttpResponse response;
        HttpGet request = new HttpGet(postUrl);
        response = client.execute(request);

        /*Checking response */
        if (response != null) {
            InputStream in = response.getEntity().getContent(); //Get the data in the entity

            BufferedReader reader = new BufferedReader(new InputStreamReader(in, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) // Read line by line
                sb.append(line + "\n");

            String resString = sb.toString(); // Result is here

            in.close(); // Close the stream
            Gson json2 = new Gson();

            respuestaAccesoUsuario = new TypeToken<RespuestaApi<AccesoUsuario>>(){}.getType();
            accesoUsuario = json2.fromJson(resString, respuestaAccesoUsuario);
        }
    }





    //Metodo para regresar a la actividad principal
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), Munu_Principal.class);

        startActivity(intent);
        // finish();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_ENTER:
                calculos();
                return true;
            default:
                return super.onKeyUp(keyCode, event);
        }
    }

    private void calculos() {
        //Se lee el password del objeto y se asigna a variable
        pass = password.getText().toString();

        //Si no se terclea nada envia mensaje de teclear contraseña
        if (pass.isEmpty()) {
            password.setError("Ingresa tu contraseña");
        }else{
            datosUsuario();
        }
    }
}