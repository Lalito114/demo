package com.szzcs.smartpos.Facturas;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.szzcs.smartpos.Munu_Principal;
import com.szzcs.smartpos.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Factura extends AppCompatActivity {

    EditText numTicket;
    String token, rfc, email, idCliente, idAlias, noTicket;
    int CantidadTickets;
    String baseurl = "https://facturasgas.com/apifac/solicitarFactura";
    ArrayList<String> tickets = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_factura);

        Bundle bundle = this.getIntent().getExtras();
        token = bundle.getString("Token");
        rfc = bundle.getString("RFC");
        email = bundle.getString("Email");
        idCliente = bundle.getString("IdCliente");
        idAlias = bundle.getString("IdAlias");
        String cantidad = bundle.getString("Cantidad");
        CantidadTickets = Integer.parseInt(cantidad);
        numTicket = findViewById(R.id.editTxtTicket);

        final Button btnAgregar = findViewById(R.id.btnAgregar);
        final Button btnFacturar = findViewById(R.id.btnFacturar);
        btnFacturar.setEnabled(false);

            btnAgregar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    int agregar = Agregar();
                    numTicket.setText("");
                    if(agregar == 0){

                        btnAgregar.setEnabled(false);
                        btnFacturar.setEnabled(true);

                        btnFacturar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                Facturar(rfc, email, idCliente, idAlias, token, tickets);
                            }
                        });
                    }else {
                        Toast.makeText(getApplicationContext(), "Faltan " + agregar + " tickets por agregar.",Toast.LENGTH_SHORT).show();
                    }

                }
            });
    }

    public int Agregar(){

        noTicket = numTicket.getText().toString();

        tickets.add(noTicket);

        CantidadTickets = CantidadTickets - 1;
        return CantidadTickets;

    }

    private void Facturar(String rfc, String email, String idCliente, String idAlias, final String token, ArrayList tickets){

        try {

            //Instancia para una nueva request
            RequestQueue requestQueue = Volley.newRequestQueue(this);

            String elementos[] = new String[tickets.size()];
            for(int i = 0; i < tickets.size(); i++){

                elementos[i] = (String) tickets.get(i);
            }

            JSONArray jArray = new JSONArray();

            String JsonO;

            for(int i = 0; i < tickets.size(); i++){

                try {
                    JSONObject jObj = new JSONObject();
                    jObj.put("NoRastreo",elementos[i]);

                    for(int j = 0; j < tickets.size() - 1; j++){

                        jArray.put(jObj);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }



            //Se forma el JSON que irá en el body del request
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("tickets", jArray);
            jsonBody.put("rfc", "XAXX010101000");
            jsonBody.put("email", "123");
            jsonBody.put("idCliente", "89");
            jsonBody.put("idAlias", "89");

            //Se envían los parámetros y url para la obtención de la respuesta del API
            JsonObjectRequest request_json = new JsonObjectRequest(Request.Method.POST, baseurl, jsonBody, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {

                    try {

                        //Si el response devuelve un status code = 200, obtenemos el token.
                        JSONObject datos = new JSONObject();
                        datos = response.getJSONObject("datos");

                        String codigo = datos.getString("codigo");
                        String mensaje = datos.getString("mensaje");

                        if(codigo == "0"){

                            Toast.makeText(getApplicationContext(), mensaje,Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Factura.this, Munu_Principal.class);

                        } else{

                            Toast.makeText(getApplicationContext(), mensaje,Toast.LENGTH_SHORT).show();

                        }

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
