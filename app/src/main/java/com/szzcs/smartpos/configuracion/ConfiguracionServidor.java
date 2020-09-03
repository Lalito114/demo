package com.szzcs.smartpos.configuracion;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import com.szzcs.smartpos.Splash;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ConfiguracionServidor extends AppCompatActivity {
    EditText edtOct1, edtOct2, edtOct3, edtOct4;
    Button btnenviar;
    String oct1, oct2,oct3,oct4, ip, ip2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracion_servidor);
        ip2 = "10.0.1.20";




        btnenviar = findViewById(R.id.btnEnviar);

        btnenviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtOct1 = findViewById(R.id.edtOct1);
                edtOct2 = findViewById(R.id.edtOct2);
                edtOct3 = findViewById(R.id.edtOct3);
                edtOct4 = findViewById(R.id.edtOct4);

                oct1 = edtOct1.getText().toString();
                oct2 = edtOct2.getText().toString();
                oct3 = edtOct3.getText().toString();
                oct4 = edtOct4.getText().toString();

                ip = oct1+"/"+oct2+"/"+oct3+"/"+oct4;


                if (oct1.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Ingresa el campo 1",Toast.LENGTH_LONG).show();
                }else{
                    if (oct2.isEmpty()){
                        Toast.makeText(getApplicationContext(),"Ingresa este campo 2",Toast.LENGTH_LONG).show();
                    }else{
                        if (oct3.isEmpty()){
                            Toast.makeText(getApplicationContext(),"Ingresa este campo 3",Toast.LENGTH_LONG).show();
                        }else{
                            if (oct4.isEmpty()){
                                Toast.makeText(getApplicationContext(),"Ingresa este campo 4",Toast.LENGTH_LONG).show();
                            }else{
                                ConectarIP();
                            }
                        }
                    }
                }

            }
        });
    }

    private void ConectarIP() {
        String url = "http://" + ip2 + "/CorpogasService/api/estaciones/ip/"+ip;
        //Utilizamos el metodo Post para colocar los datos en el  ticket
        StringRequest eventoReq = new StringRequest(Request.Method.GET,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        guardarDatosDBEmpresa(response);
//                        obtenerPosicionesdeCarga();
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

    private void obtenerPosicionesdeCarga() {

        //Declaramos direccion URL de las posiciones de carga. Para acceder a los metodos de la API

        SQLiteBD data = new SQLiteBD(this);
        SQLiteDatabase db  = data.getWritableDatabase();

        if (db != null){
            Cursor c = db.rawQuery("SELECT ID, IP FROM comments", null);

            if (c != null) {
                c.moveToFirst();
                do {
                    //Asignamos el valor en nuestras variables para usarlos en lo que necesitemos
                    String user = c.getString(c.getColumnIndex("user"));
                    String comment = c.getString(c.getColumnIndex("comment"));
                } while (c.moveToNext());
            }
            String url = "http://"+ip2+"/CorpogasService/api/posicionCargas/estacion/1/maximo";
            //inicializamos el String reques que es el metodo de la funcion de Volley que no va a permir accder a la API
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                //El metodo onResponse el cual va cachar si hay una respuesta de tipo cadena
                public void onResponse(String response) {
                    //llamamos al metodo posicion en donde aoptine como resultado
                    //el valos maximo de posiciones de carga


                }
                //si exite un error este entrata de el metodo ErrorListener
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(),error.toString(), Toast.LENGTH_LONG).show();
                }
            });
            //Ejecutamos el stringrequest para invocar a la clase volley
            RequestQueue requestQueue = Volley.newRequestQueue(this.getApplicationContext());
            //Agregamos el stringrequest al Requestque
            requestQueue.add(stringRequest);
        }

    }

    private void guardarDatosDBEmpresa(String response) {
        try {
            JSONObject empresa = new JSONObject(response);
            String id = empresa.getString("Id");
            String siic = empresa.getString("Siic");
            String sucursal = empresa.getString("Sucursal");

            JSONObject sucursal1 = new JSONObject(sucursal);
            String correo = sucursal1.getString("Correo");
            String empresaid  = sucursal1.getString("EmpresaId");
            String ip = sucursal1.getString("Ip");
            String nombre = sucursal1.getString("Nombre");
            String numerofranquicia = sucursal1.getString("NumeroFranquicia");
            String numinterno = sucursal1.getString("NumeroInterno");

            SQLiteBD data = new SQLiteBD(getApplicationContext());
            data.DatosEstacion(id,siic,correo,empresaid,ip,nombre,numerofranquicia,numinterno);

            Toast.makeText(getApplicationContext(),"Los datos se guardaron correctamente",Toast.LENGTH_LONG).show();
            Intent intent = new Intent(getApplicationContext(), Splash.class);
            startActivity(intent);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    
    


}
