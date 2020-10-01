package com.szzcs.smartpos.Cortes2;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.szzcs.smartpos.R;
import com.szzcs.smartpos.configuracion.SQLiteBD;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class FajillaMorralla extends AppCompatActivity {

    // Se declaran las variables que se usaran en este Activity
    String precioFajilla, morralla;
    EditText fajillasMorralla;
    Button btnFajillasMorralla;
    int fajillaMorralla;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fajilla_morralla);
        valorFajilla();

        // Hacemos la relacion de las variables con los objetos del layout
        fajillasMorralla = (EditText) findViewById(R.id.editFajillasMorralla);
        btnFajillasMorralla = (Button) findViewById(R.id.btnFajillasMorralla);

        // Este sera el comportamiento del boton cuando el Usuario le de click
        btnFajillasMorralla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Se obtiene el numero que digita el Usuario
                morralla = fajillasMorralla.getText().toString();

                // Se valida que el Campo no este vacio
                if(morralla.isEmpty()){

                    Toast.makeText(getApplicationContext(),"ERROR 401: Ingresar Numero de Fajillas de Morralla",Toast.LENGTH_LONG).show();
                }
                    // Se multiplica el numero ingresado por el usuario con el valor de la Fajilla de Morralla
                    int dineroMorralla = Integer.parseInt(morralla) * fajillaMorralla;
                    Toast.makeText(getApplicationContext(),"Fue un Total de "+ dineroMorralla + " pesos",Toast.LENGTH_LONG).show();
                    // Se ejecuta el metodo enviarFolios
                    enviarFolios();
            }
        });

    }
    // Se crea el metodo valorFajilla
    public void valorFajilla(){
        // Creamos la variable data para poder llamar a las variables que usaremos de la clase SQLiteBD
        final SQLiteBD data = new SQLiteBD(getApplicationContext());
        // Declaramos la URl que se ocupara para el metodo valorFajilla
        String url = "http://"+data.getIpEstacion()+"/CorpogasService/api/PrecioFajillas/Sucursal/"+data.getIdSucursal();
        // Utilizamos el metodo Get para obtener TipoFajillaId y Precio
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray array = new JSONArray(response);

                    for (int i = 0; i < array.length() ; i++) {
                        JSONObject sl1 = array.getJSONObject(i);
                        String tipoFajilla = sl1.getString("TipoFajillaId");
                        precioFajilla = sl1.getString("Precio");
                        if(tipoFajilla.equals("2")){
                            fajillaMorralla = Integer.parseInt(precioFajilla);
                        }

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                // Funcion para capturar errores
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        // Añade la peticion a la cola
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }
    // Se crea el Metodo enviarFolios
    private void enviarFolios() {
        // Llamamos a las variables obtenidas en el Activity FajillasBilletes
        final String origenId = getIntent().getStringExtra("origenId");
        final String cierreId = getIntent().getStringExtra("cierreId");

        // Creamos la variable data para poder llamar a las variables que usaremos de la clase SQLiteBD
        final SQLiteBD data = new SQLiteBD(getApplicationContext());
        // Declaramos la URl que se ocupara para el metodo enviarFolios
        String URL = "http://"+data.getIpEstacion()+"/CorpogasService/api/Fajillas/GuardaFoliosCierreFajillas/usuario/1";
        final JSONObject mjason = new JSONObject();
        // Añade la peticion a la cola
        RequestQueue queue = Volley.newRequestQueue(this);
        // Se declaran todos los datos que tiene que llevar el JSON
        try {
            mjason.put("CierreId",cierreId);
            mjason.put("CierreSucursalId", 1); //turno.getText().toString());
            JSONObject prueba = new JSONObject();
            prueba.put("IslaId", "1");
            mjason.put("Cierre",prueba);//turno.getText().toString());Sucursal
            mjason.put("SucursalId", "1");
            mjason.put("TipoFajillaId","2");
            mjason.put("FolioInicial", '0');
            mjason.put("FolioFinal", morralla);
            mjason.put("Denominacion", fajillaMorralla);
            mjason.put("OrigenId", origenId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Utilizamos el metodo POST para enviar el Objeto mjason
        JsonObjectRequest request_json = new JsonObjectRequest(Request.Method.POST, URL, mjason, new Response.Listener<JSONObject>() {
            @Override
            // Si la respuesta a la base de datos es satisfactoria, se manipula el JSON obtenido.
            public void onResponse(JSONObject response) {
                try {
                    String estado  = response.getString("Correcto");
                    if (estado == "true"){
                        AlertDialog.Builder builder = new AlertDialog.Builder(FajillaMorralla.this);
                        builder.setTitle("CorpoApp");
                        builder.setMessage("Los datos se guardaron corretamente");
                        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(getApplicationContext(),SubOfiBilletes.class);
                                intent.putExtra("origenId",origenId);
                                intent.putExtra("cierreId",cierreId);
                                startActivity(intent);
                                finish();
                            }
                        });
                        AlertDialog dialog= builder.create();
                        dialog.show();

                    }else{
                        // Si existe un error al recibir la respuesta de la API, mostraremos el mensaje de error.
                        String mensaje  = response.getString("Mensaje");
                        AlertDialog.Builder builder = new AlertDialog.Builder(FajillaMorralla.this);
                        builder.setTitle("Error");
                        builder.setMessage(mensaje);
                        builder.setNegativeButton("Cerrar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                        AlertDialog dialog= builder.create();
                        dialog.show();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
//                Intent intente = new Intent(getApplicationContext(), Munu_Principal.class);
//                startActivity(intente);
                Toast.makeText(getApplicationContext(),"Gasto Cargado Exitosamente",Toast.LENGTH_LONG).show();
                //Toast.makeText(getApplicationContext(),response.toString(),Toast.LENGTH_LONG).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            public Map<String,String> getHeaders() throws AuthFailureError {
                Map<String,String> headers = new HashMap<String, String>();
                return headers;
            }
            protected  Response<JSONObject> parseNetwokResponse(NetworkResponse response){
                if (response != null){

                    try {
                        String responseString;
                        JSONObject datos = new JSONObject();
                        responseString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));

                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
                return Response.success(mjason, HttpHeaderParser.parseCacheHeaders(response));
            }
        };
        queue.add(request_json);

    }
}