package com.szzcs.smartpos.Facturas;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.szzcs.smartpos.R;
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
    String token;
    String baseurl = "https://facturasgas.com/apifac/autenticacion";
    RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_obtener_token);

        password = findViewById(R.id.password);
        queue = Volley.newRequestQueue(ObtenerToken.this);

        ImageButton btnFact = findViewById(R.id.btnFact);
        btnFact.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                final String pwd = password.getText().toString();

                //Validaciones para campos vacíos
//                if(pwd.equals("")) {
//                    Toast.makeText(getApplicationContext(), "Ingrese un password válido.", Toast.LENGTH_SHORT).show();
//                }else  {
//                    //Si ambos campos contienen información se ejecuta el método para obtener el token
                    getToken(pwd);
//                }


            }
        });
    }

    private void getToken(final String password){

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

                        token = datos.getString("token");

                        Toast.makeText(getApplicationContext(), "Credenciales correctas.", Toast.LENGTH_SHORT).show();
                        //Enviamos el token al siguiente activity y lo inicializamos
                        Intent intent = new Intent(ObtenerToken.this, ObtenerRFC.class);
                        intent.putExtra("Token", token);
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

//    public static class HttpsTrustManager implements X509TrustManager {
//
//        private static TrustManager[] trustManagers;
//        private static final X509Certificate[] _AcceptedIssuers = new X509Certificate[]{};
//
//        @Override
//        public void checkClientTrusted(
//
//                java.security.cert.X509Certificate[] x509Certificates, String s)
//                throws java.security.cert.CertificateException {
//
//        }
//
//        @Override
//        public void checkServerTrusted(
//                java.security.cert.X509Certificate[] x509Certificates, String s)
//                throws java.security.cert.CertificateException {
//
//        }
//
//        public boolean isClientTrusted(X509Certificate[] chain) {
//            return true;
//        }
//
//        public boolean isServerTrusted(X509Certificate[] chain) {
//            return true;
//        }
//
//        @Override
//        public X509Certificate[] getAcceptedIssuers() {
//            return _AcceptedIssuers;
//        }
//
//        public static void allowAllSSL() {
//            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
//
//                @Override
//                public boolean verify(String arg0, SSLSession arg1) {
//                    return true;
//                }
//
//            });
//
//            SSLContext context = null;
//            if (trustManagers == null) {
//                trustManagers = new TrustManager[]{new HttpsTrustManager()};
//            }
//
//            try {
//                context = SSLContext.getInstance("TLS");
//                context.init(null, trustManagers, new SecureRandom());
//            } catch (NoSuchAlgorithmException e) {
//                e.printStackTrace();
//            } catch (KeyManagementException e) {
//                e.printStackTrace();
//            }
//
//            HttpsURLConnection.setDefaultSSLSocketFactory(context
//                    .getSocketFactory());
//        }
//
//    }
}
