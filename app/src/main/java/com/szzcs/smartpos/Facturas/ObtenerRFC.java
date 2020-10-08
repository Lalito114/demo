package com.szzcs.smartpos.Facturas;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.szzcs.smartpos.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ObtenerRFC extends AppCompatActivity {

    EditText rfc, despachador, terminal;
    String token;
    String baseurl = "https://facturasgas.com/apifac/obtenerRfc";
    RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_obtener_rfc);

        token = getIntent().getStringExtra("Token");
        rfc = findViewById(R.id.txtRFC);
        despachador = findViewById(R.id.txtDespachador);
        terminal = findViewById(R.id.txtTerminal);
        queue = Volley.newRequestQueue(ObtenerRFC.this);

        ImageButton bntRFC = findViewById(R.id.bntRFC);
        bntRFC.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                final String RFC = rfc.getText().toString();
                final String desp = despachador.getText().toString();
                final String term = terminal.getText().toString();

                //Validaciones para campos vacíos
//                if(RFC.equals("") && desp.equals("") && term.equals("")){
//                    Toast.makeText(getApplicationContext(),"Ingrese su RFC, el código de despachador y de terminal.",Toast.LENGTH_SHORT).show();
//                } else if(RFC.equals("")) {
//                    Toast.makeText(getApplicationContext(), "Ingrese su RFC.", Toast.LENGTH_SHORT).show();
//                }else if(desp.equals("")){
//                    Toast.makeText(getApplicationContext(), "Ingrese el código de despachador.", Toast.LENGTH_SHORT).show();
//                }else if(term.equals("")){
//                    Toast.makeText(getApplicationContext(), "Ingrese el código de terminal.", Toast.LENGTH_SHORT).show();
//                }  else{
                    //Si ambos campos contienen información se ejecuta el método para obtener el RFC
                    getRFC(RFC, desp, term, token);
//                }
            }
        });
    }

    private void getRFC(String RFC, String despachador, String terminal, final String token){

        //Clase que permite hacer request post a url con certificado ssl
        //ObtenerToken.HttpsTrustManager.allowAllSSL();
        try {

            //Instancia para una nueva request
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            //Se forma el JSON que irá en el body del request
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("rfc", "XAXX010101000");
            jsonBody.put("despachador", "123");
            jsonBody.put("terminal", "89");

            //Se envían los parámetros y url para la obtención de la respuesta del API
            JsonObjectRequest request_json = new JsonObjectRequest(Request.Method.POST, baseurl, jsonBody, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {

                    try {

                        //Si el response devuelve un status code = 200, obtenemos el RFC.
                        JSONArray JArrayDatos = response.getJSONArray("datos");
                        int arrSize = JArrayDatos.length();
                        ArrayList<String> razonSocial = new ArrayList<String>(arrSize);
                        ArrayList<String> RFC = new ArrayList<String>(arrSize);
                        ArrayList<String> email = new ArrayList<String>(arrSize);
                        ArrayList<String> idCliente = new ArrayList<String>(arrSize);
                        ArrayList<String> idAlias = new ArrayList<String>(arrSize);

                        for (int i = 0; i < arrSize; ++i) {
                            response = JArrayDatos.getJSONObject(i);
                            razonSocial.add(response.getString("RazonSocial"));
                            RFC.add(response.getString("RFC"));
                            email.add(response.getString("Email"));
                            idCliente.add(response.getString("IdCliente"));
                            idAlias.add(response.getString("IdAlias"));
                        }

                        Bundle b = new Bundle();
                        b.putStringArrayList("RazonSocial", razonSocial);
                        b.putStringArrayList("RFC", RFC);
                        b.putStringArrayList("Email", email);
                        b.putStringArrayList("IdCliente", idCliente);
                        b.putStringArrayList("IdAlias", idAlias);
                        b.putString("Token", token);
                        Intent intent = new Intent(ObtenerRFC.this, FillListRFC.class);
                        intent.putExtras(b);
                        startActivity(intent);

                    } catch (JSONException e) {

                        //Si existe error en el JSON aquí lo cachamos
                        e.printStackTrace();

                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {

                    //Si el response devuelce un status code != 200, devolcemos un mensaje de error
                    Toast.makeText(getApplicationContext(),"error:" + error.toString(),Toast.LENGTH_SHORT).show();

                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();

                    headers.put("Authorization", "Bearer " + token);
                    return headers;
                }
            };

            //Se envía la petición a cola
            requestQueue.add(request_json);

        } catch (JSONException e) {

            e.printStackTrace();

        }
    }

}
