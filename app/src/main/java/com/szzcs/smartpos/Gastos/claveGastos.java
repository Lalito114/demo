package com.szzcs.smartpos.Gastos;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.szzcs.smartpos.Munu_Principal;
import com.szzcs.smartpos.MyApp;
import com.szzcs.smartpos.Pendientes.claveUPendientes;
import com.szzcs.smartpos.Pendientes.ticketPendientes;
import com.szzcs.smartpos.Productos.VentasProductos;
import com.szzcs.smartpos.Productos.claveProducto;
import com.szzcs.smartpos.Productos.posicionProductos;
import com.szzcs.smartpos.R;
import com.szzcs.smartpos.base.BaseActivity;
import com.szzcs.smartpos.configuracion.SQLiteBD;
import com.zcs.sdk.fingerprint.FingerprintListener;
import com.zcs.sdk.fingerprint.FingerprintManager;
import com.zcs.sdk.util.StringUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class claveGastos extends BaseActivity implements FingerprintListener, View.OnClickListener  {
    private static final String TAG = "FingerprintActivity";
    TextView usuario, carga;
    EditText password;
    String idisla, idTurno, proviene;
    String EstacionId, sucursalId, ipEstacion ;

    Button btnhuella;
    private byte[] isoFeatureTmp;
    String empleadoIdentificado, claveEmpleadoIdentificado;
    private FingerprintManager mFingerprintManager;
    private Handler mHandler;
    TextView textResultado;
    Boolean banderaIdentificado;

    List<String> AEmpleadoId;
    List<byte[]> AHuella;
    List<String> AClaveEmpleado;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clave_gastos);

        empleadoIdentificado="0";
        claveEmpleadoIdentificado = "";
        btnhuella  =  findViewById(R.id.btnhuella);
        btnhuella.setOnClickListener(claveGastos.this);
        mHandler = new Handler(Looper.getMainLooper());
        textResultado = findViewById(R.id.textresultado);


        SQLiteBD db = new SQLiteBD(getApplicationContext());
        sucursalId=db.getIdSucursal();
        EstacionId = db.getIdEstacion();
        ipEstacion = db.getIpEstacion();

        //lee valores usuario y carga
        usuario= findViewById(R.id.usuario);
        carga = findViewById(R.id.carga);
        //carga.setText(getIntent().getStringExtra("posicion"));
        password = (EditText) findViewById(R.id.pasword);
        password.setText(claveEmpleadoIdentificado);


        proviene = "0";
        validaClave();
        initFinger();
        ObtieneHuellas();

    }

    private void ObtieneHuellas(){
        AEmpleadoId = new ArrayList<String>();
        AHuella = new ArrayList<byte []>();
        AClaveEmpleado = new ArrayList<String>();

        // URL para obtener los empleados  y huellas de la posición de carga X
        String url = "http://"+ipEstacion+"/CorpogasService/api/estacionControles/empleadosTipoBiometrico/estacionId/"+EstacionId+"/tipoBiometricoId/3";
        // Utilizamos el metodo Post para validar la contraseña
        StringRequest eventoReq = new StringRequest(Request.Method.GET,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject p1 = new JSONObject(response);

                            String correcto = p1.getString("Correcto");
                            String mensaje = p1.getString("Mensaje");
                            String objetoRespuesta = p1.getString("ObjetoRespuesta");
                            if (correcto.equals("true")){
                                JSONArray or = new JSONArray(objetoRespuesta);
                                for (int b = 0; b <or.length() ; b++) {
                                    JSONObject pA = or.getJSONObject(b);
                                    String sucEmpleado=pA.getString("SucursalEmpleado");
                                    JSONObject empHuella = new JSONObject(sucEmpleado);
                                    String claveEmpleado=empHuella.getString("Clave");

                                    String empleadohuellaEmpleado=empHuella.getString("EmpleadoHuellas");
                                    JSONArray huellas = new JSONArray(empleadohuellaEmpleado);
                                    for (int c = 0; c <huellas.length() ; c++) {
                                        JSONObject huellempleado = huellas.getJSONObject(c);
                                        String empleadoId = huellempleado.getString("SucursalEmpleadoId");
                                        String tipoBiometrico = huellempleado.getString("TipoBiometricoId");
                                        String huellaDerecha = huellempleado.getString("HuellaDerecha");
                                        if (tipoBiometrico == "3" && huellaDerecha.length() > 0) {
                                            AEmpleadoId.add(empleadoId);
                                            byte [] huella = StringUtils.convertHexToBytes(huellaDerecha);
                                            AHuella.add(huella);
                                            AClaveEmpleado.add(claveEmpleado);
                                        }
                                    }
                                }

                            }else{

                            }
                        } catch (JSONException e) {
                            //herramienta  para diagnostico de excepciones
                            //e.printStackTrace();
                            Toast.makeText(getApplicationContext(),"error al cargar las huellas de los empleados",Toast.LENGTH_SHORT).show();
                        }
                    }
                    //funcion para capturar errores
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_SHORT).show();
                //VolleyLog.e("Error: ", volleyError.getMessage());
                String algo = new String(error.networkResponse.data) ;
                try {
                    //creamos un json Object del String algo
                    JSONObject errorCaptado = new JSONObject(algo);
                    //Obtenemos el elemento ExceptionMesage del errro enviado
                    String errorMensaje = errorCaptado.getString("ExceptionMessage");
                    try {
                        AlertDialog.Builder builder = new AlertDialog.Builder(claveGastos.this);
                        builder.setTitle("Gastos");
                        builder.setMessage("Usuario ocupado: " + errorMensaje)
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent intente = new Intent(getApplicationContext(), Munu_Principal.class);
                                        startActivity(intente);
                                        finish();
                                    }
                                }).show();
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

        // Añade la peticion a la cola
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        eventoReq.setRetryPolicy(new DefaultRetryPolicy(12000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(eventoReq);

    }

    private void initFinger() {
        mFingerprintManager = MyApp.sDriverManager.getFingerprintManager();
        mFingerprintManager.addFignerprintListener(this);
        mFingerprintManager.init();
    }

    public void obtenerIsla(final String proviene) {
        if (claveEmpleadoIdentificado.length() >0){
            password.setText(claveEmpleadoIdentificado);
        }
        final String pass = password.getText().toString();


        String url = "http://"+ipEstacion+"/CorpogasService/api/estacionControles/estacion/"+ sucursalId +"/ClaveEmpleado/" +pass;
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
                    intent.putExtra("tipoGasto", proviene);
                    intent.putExtra("isla", idisla);
                    intent.putExtra("turno", idTurno);
                    startActivity(intent);
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Contraseña inexistente "+error.toString(), Toast.LENGTH_LONG).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(12000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
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
                    if (empleadoIdentificado == "0") {
                        ObtenerClave(pass, "1");
                    }else{
                        proviene = "1";
                        validaId(empleadoIdentificado);
                    }
                }
            }
        });
        ImageView btnenviarV = findViewById(R.id.imgvaleCaja);
        //En espera a recibir el evento Onclick del boton Enviar
        btnenviarV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Se lee el password del objeto y se asigna a variable
                final String pass = password.getText().toString();
                //Si no se terclea nada envia mensaje de teclear contraseña
                if (pass.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Ingresa la contraseña",Toast.LENGTH_SHORT).show();
                }else {
                    if (empleadoIdentificado == "0") {
                        ObtenerClave(pass,"2");
                    }else{
                        proviene = "2";
                        validaId(empleadoIdentificado);
                    }
                }

            }
        });

    }
    private void ObtenerClave(final String pass, final String proviene){
        //----------------------Aqui va el Volley Si se tecleo contraseña----------------------------

        //Conexion con la base y ejecuta valida clave
        String url = "http://"+ipEstacion+"/CorpogasService/api/SucursalEmpleados/clave/"+pass;
        // Utilizamos el metodo Post para validar la contraseña
        StringRequest eventoReq = new StringRequest(Request.Method.GET,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //Se instancia la respuesta del json
                            if (response.equals("null")){
                                Toast.makeText(getApplicationContext(),"Clave inexistente ",Toast.LENGTH_SHORT).show();
                            }else{
                                JSONObject validar = new JSONObject(response);
                                String valido = validar.getString("Activo");
                                String idusuario = validar.getString("Id");
                                String idRoll = validar.getString("RolId");

                                if (valido.equals("true") && idRoll.equals("3") || valido.equals("true") && idRoll.equals("1")){ // 1 es Jefe de ISla Autorizado por Gerente
                                    //Si es valido se asignan valores
                                    obtenerIsla(proviene);
                                }else{
                                    //Si no es valido se envia mensaje
                                    try {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(claveGastos.this);
                                        builder.setTitle("Contraseña Incorrecta");
                                        builder.setMessage("Debe ser de Jefe de Isla o Gerente")
                                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    password.setText("");
                                                }
                                            }).show();
                                    }catch (Exception e){
                                    e.printStackTrace();
                                    }
                                }
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
                    //Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_SHORT).show();
                    //VolleyLog.e("Error: ", volleyError.getMessage());
                    String algo = new String(error.networkResponse.data) ;
                    try {
                        //creamos un json Object del String algo
                        JSONObject errorCaptado = new JSONObject(algo);
                        //Obtenemos el elemento ExceptionMesage del errro enviado
                        String errorMensaje = errorCaptado.getString("ExceptionMessage");
                        try {
                            AlertDialog.Builder builder = new AlertDialog.Builder(claveGastos.this);
                            builder.setTitle("Gastos");
                            builder.setMessage("Usuario ocupado: " + errorMensaje)
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent intente = new Intent(getApplicationContext(), Munu_Principal.class);
                                        startActivity(intente);
                                    }
                                }).show();
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    } catch (JSONException e) {
                    e.printStackTrace();
                    }
                }
        });

        // Añade la peticion a la cola
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        eventoReq.setRetryPolicy(new DefaultRetryPolicy(12000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(eventoReq);
        //-------------------------Aqui termina el volley --------------
    }

    private void  validaId(final String EmpleadoId){
        //Crea Boton Enviar
        //Button btnenviar = (Button) findViewById(R.id.enviar);
        //Se lee el password del objeto y se asigna a variable
        final String posicion;
        posicion = getIntent().getStringExtra("posicion");
        //----------------------Aqui va el Volley Si se tecleo contraseña----------------------------

        //Conexion con la base y ejecuta valida clave
        String url = "http://"+ipEstacion+"/CorpogasService/api/SucursalEmpleados/"+EmpleadoId;
        // Utilizamos el metodo Post para validar la contraseña
        StringRequest eventoReq = new StringRequest(Request.Method.GET,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //Se instancia la respuesta del json
                            if (response.equals("null")){
                                Toast.makeText(getApplicationContext(),"Clave inexistente ",Toast.LENGTH_SHORT).show();
                            }else{
                                JSONObject validar = new JSONObject(response);
                                String valido = validar.getString("Activo");
                                String idusuario = validar.getString("Id");
                                String idRoll = validar.getString("RolId");

                                if (valido.equals("true") && idRoll.equals("3") || valido.equals("true") && idRoll.equals("1")){ // 1 es Jefe de ISla Autorizado por Gerente
                                    //Si es valido se asignan valores

                                    obtenerIsla(proviene);
                                }else{
                                    //Si no es valido se envia mensaje
                                    try {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(claveGastos.this);
                                        builder.setTitle("Contraseña Incorrecta");
                                        builder.setMessage("Debe ser de Jefe de Isla o Gerente")
                                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        password.setText("");
                                                    }
                                                }).show();
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            //herramienta  para diagnostico de excepciones
                            //e.printStackTrace();
                            Toast.makeText(getApplicationContext(),"Clave inexistente ",Toast.LENGTH_SHORT).show();
                        }
                    }
                    //funcion para capturar errores
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_SHORT).show();
                //VolleyLog.e("Error: ", volleyError.getMessage());
                String algo = new String(error.networkResponse.data) ;
                try {
                    //creamos un json Object del String algo
                    JSONObject errorCaptado = new JSONObject(algo);
                    //Obtenemos el elemento ExceptionMesage del errro enviado
                    String errorMensaje = errorCaptado.getString("ExceptionMessage");
                    try {
                        AlertDialog.Builder builder = new AlertDialog.Builder(claveGastos.this);
                        builder.setTitle("Gastos");
                        builder.setMessage("Usuario ocupado: " + errorMensaje)
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent intente = new Intent(getApplicationContext(), Munu_Principal.class);
                                        startActivity(intente);
                                        finish();
                                    }
                                }).show();
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

        // Añade la peticion a la cola
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        eventoReq.setRetryPolicy(new DefaultRetryPolicy(12000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(eventoReq);
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.btnhuella) {
            if  (AEmpleadoId.size() == 0){
                Toast.makeText(getApplicationContext(),"Ningún empleado con huellas capturadas",Toast.LENGTH_SHORT).show();
            }else {
                //mFingerprintManager.authenticate(3); ES PARA HUELLAS CARGADAS EN LA HANDHELD
                banderaIdentificado = true;
                for (int q = 0; q < AEmpleadoId.size(); q++) { //
                    if (banderaIdentificado == true) {
                        isoFeatureTmp = AHuella.get(q);
                        empleadoIdentificado = AEmpleadoId.get(q);
                        claveEmpleadoIdentificado = AClaveEmpleado.get(q);
                        mFingerprintManager.verifyWithISOFeature(isoFeatureTmp);
                        try {
                            Thread.sleep(1000);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void onEnrollmentProgress(int i, int i1, int i2) {

    }

    @Override
    public void onAuthenticationFailed(int i) {
        banderaIdentificado =Boolean.TRUE;
        showLog("Huella no Validada"); // :  Usuario = " + i );
    }

    @Override
    public void onAuthenticationSucceeded(int fingerId, Object obj) {
        banderaIdentificado =Boolean.FALSE;
        showLog("Identificación Correcta :  Usuario = " + empleadoIdentificado); // + "  score = " + obj);

        //validaId(empleadoIdentificado);
    }

    @Override
    public void onGetImageComplete(int i, byte[] bytes) {

    }

    @Override
    public void onGetImageFeature(int i, byte[] bytes) {

    }

    @Override
    public void onGetImageISOFeature(int i, byte[] bytes) {

    }
    StringBuffer _msg = new StringBuffer();
    private int _lines = 20;

    private void showLog(final String msg) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                textResultado.setText("");
                Log.e(TAG, msg);
                Date date = new Date();
                DateFormat dateFormat = new SimpleDateFormat("MM/dd HH:mm:ss");
                _msg.append(dateFormat.format(date)).append(":");
                _msg.append(msg);
                String text = textResultado.getText().toString();
                if (!TextUtils.isEmpty(text)) {
                    String str[] = text.split("\r\n");
                    for (int i = 0; i < _lines && i < str.length; i++) {
                        _msg.append("\r\n");
                        _msg.append(str[i]);
                    }
                }
                textResultado.setText(_msg.toString());
                _msg.setLength(0);
            }

        });
    }

    //Metodo para regresar a la actividad principal
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), Munu_Principal.class);
        startActivity(intent);
        //finish();
    }

}