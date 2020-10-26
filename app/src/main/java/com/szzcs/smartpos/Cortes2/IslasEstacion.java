package com.szzcs.smartpos.Cortes2;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Parcelable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.szzcs.smartpos.R;
import com.szzcs.smartpos.configuracion.SQLiteBD;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class IslasEstacion extends AppCompatActivity {

    TextView textNombre;
    Spinner islasEstacion;
    Button btnAceptar;
    //    final SQLiteBD data = new SQLiteBD(getApplicationContext());
    ArrayList arrayIslas =  new ArrayList<String>();
    ArrayAdapter<CharSequence> adaptadorIslas;
    String islaCorte;
    //List<Cierre> cierreList;
    RespuestaApi<Cierre> cierreRespuestaApi;
    Type tipóRespuesta;
    String idsucursal;
    String idusuario;
    String password;
    String nombreCompleto;

    SQLiteBD data;

    volatile boolean ejecutar = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_islas_estacion);
        nombreCompleto = getIntent().getStringExtra("nombreCompleto");
        textNombre = (TextView) findViewById(R.id.textNombre);
        islasEstacion = (Spinner) findViewById(R.id.spinnerIslas);
        textNombre.setText(nombreCompleto);

        idusuario = getIntent().getStringExtra("idusuario");
        password = getIntent().getStringExtra("password");
        data = new SQLiteBD(getApplicationContext());

        btnAceptar = (Button) findViewById(R.id.btnAceptarIsla);
        islasEstacion();
        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(islasEstacion.getSelectedItemPosition()==0){
                    AlertDialog.Builder builder = new AlertDialog.Builder(IslasEstacion.this);
                    builder.setTitle("ERROR");
                    builder.setMessage("Selecciona una Isla para Continuar con el Cierre");
                    builder.setNegativeButton("Cerrar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    AlertDialog dialog= builder.create();
                    dialog.show();
                }else{

                    ObtenCierre();

                }

            }
        });

    }
        public void ObtenCierre(){
        new Thread(new Runnable() {
            public void run() {
                //while(ejecutar) {
                try {
                    httpJsonPost();
                    //ejecutar = false;

                } catch (IOException e) {
                    e.printStackTrace();
                }
                //}
                runOnUiThread(new Runnable() {
                    public void run() {
                        String respuetas = null;
                        boolean  Correcto = cierreRespuestaApi.Correcto;
                        if(Correcto == true){ // true
//                            respuetas =  "Se recupero el cierre";
                            Intent intent = new Intent(getApplicationContext(),Lecturas.class);
                            intent.putExtra("islaId",islaCorte);
                            intent.putExtra("idusuario",idusuario);
                            intent.putExtra("password",password);
                            intent.putExtra("nombreCompleto",nombreCompleto);
                            intent.putExtra("lcierreRespuestaApi", cierreRespuestaApi);
                            startActivity(intent);
                        }else{
                            respuetas =  cierreRespuestaApi.Mensaje;
                            Toast.makeText(getApplicationContext(), respuetas, Toast.LENGTH_SHORT).show();

                        }
//                        Toast.makeText(getApplicationContext(), respuetas, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).start();
    }

        public void  httpJsonPost() throws IOException {
        String postUrl = "http://"+data.getIpEstacion()+"/CorpogasService/api/cierres/registrar/sucursal/"+data.getIdSucursal()+"/isla/"+islaCorte+"/usuario/"+idusuario+"/origen/1";                    //"http://"+data.getIpEstacion()+"/CorpogasService/api/cierreValePapeles/cierre/sucursalId/1/usuarioId/1/islaId/1";// put in your url

        HttpClient client = new DefaultHttpClient();
        HttpConnectionParams.setConnectionTimeout(client.getParams(), 5000);
        HttpResponse response;
        HttpPost request = new HttpPost(postUrl);
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

            tipóRespuesta = new TypeToken<RespuestaApi<Cierre>>(){}.getType();

            cierreRespuestaApi = json2.fromJson(resString, tipóRespuesta);
//            final RespuestaApi<Cierre> envoltorioEmpleado = json2.fromJson(resString, tipóRespuesta);
            String pruebas = "";

        }
    }


    public void islasEstacion(){
        String url = "http://"+data.getIpEstacion()+"/CorpogasService/api/islas/estacion/"+data.getIdEstacion();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray array = new JSONArray(response);
                    for (int i = 0; i < array.length() ; i++) {
                        JSONObject obj1 = array.getJSONObject(i);
                        String numeroInterno = obj1.getString("NumeroInterno");
                        arrayIslas.add(numeroInterno);
                    }
                    ArrayList<String> comboIslas = new ArrayList<>();
                    comboIslas.add(" Isla: ");
                    for (int i = 0; i < arrayIslas.size() ; i++) {
                        comboIslas.add(String.valueOf(arrayIslas.get(i)));

                    }
                    adaptadorIslas = new ArrayAdapter(getApplicationContext(),R.layout.custom_spinner,comboIslas);
                    islasEstacion.setAdapter(adaptadorIslas);

                    islasEstacion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            if(position!=0){

                                islaCorte = parent.getItemAtPosition(position).toString();
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);

    }
    //Metodo para regresar a la actividad principal
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), Clave.class);

        startActivity(intent);
        // finish();
    }
}