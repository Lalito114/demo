package com.szzcs.smartpos.Facturas;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.szzcs.smartpos.R;
import com.szzcs.smartpos.configuracion.SQLiteBD;

import org.json.JSONException;
import org.json.JSONObject;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class ObtenerToken extends AppCompatActivity {

    EditText usuario, password;
    String iduser, pwd;
    String baseurl = "https://facturasgas.com/apifac/autenticacion";
    RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_obtener_token);

        password = findViewById(R.id.password);
        queue = Volley.newRequestQueue(ObtenerToken.this);

        Button btnFact = findViewById(R.id.btnFact);
        btnFact.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                 pwd = password.getText().toString();

                //Validaciones para campos vacíos
                if(pwd.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Ingrese un password válido.", Toast.LENGTH_SHORT).show();
                }else  {
                    //Si ambos campos contienen información se ejecuta el método para obtener el token
                    ValidarUsuario(pwd);
                }


            }
        });
    }

    private void ValidarUsuario(String password) {
        SQLiteBD data = new SQLiteBD(getApplicationContext());
        String url = "http://"+data.getIpEstacion()+"/CorpogasService/api/SucursalEmpleados/clave/"+password;

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
//                            obteneridusuario(idusuario);
                            if (valido == "true"){
                                getToken(pwd, iduser);
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

    private void getToken(final String password, final String iduser1){

        //Clase que permite hacer request post a url con certificado ssl
        //HttpsTrustManager.allowAllSSL();
        try {

            //Instancia para una nueva request
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            //Se forma el JSON que irá en el body del request
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("usuario", "dev_user");
            jsonBody.put("password", "9455w0");

            //Se envían los parámetros y url para la obtención de la respuesta del API
            JsonObjectRequest request_json = new JsonObjectRequest(Request.Method.POST, baseurl, jsonBody, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {

                    try {

                        //Si el response devuelve un status code = 200, obtenemos el token.
                        JSONObject datos = new JSONObject();
                        datos = response.getJSONObject("datos");

                        String token = datos.getString("token");

                        Toast.makeText(getApplicationContext(), "Credenciales correctas.", Toast.LENGTH_SHORT).show();
                        //Enviamos el token al siguiente activity y lo inicializamos
                        Intent intent = new Intent(ObtenerToken.this, ObtenerRFC.class);
                        intent.putExtra("Token", token);
                        intent.putExtra("IdUser", iduser1);
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
                    Toast.makeText(getApplicationContext(),"Credenciales inválidas, intente de nuevo.",Toast.LENGTH_SHORT).show();

                }
            }) {
                @Override
                public String getBodyContentType() {

                    return "application/json; charset=utf-8";

                }
            };

            //Se envía la petición a cola
            requestQueue.add(request_json);

        } catch (JSONException e) {

            e.printStackTrace();

        }

    }

}
