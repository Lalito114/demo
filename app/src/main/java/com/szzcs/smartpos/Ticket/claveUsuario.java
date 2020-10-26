package com.szzcs.smartpos.Ticket;


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
import com.szzcs.smartpos.PrintFragment;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;


//Clase para validar la contraseña del empleado
public class claveUsuario extends BaseActivity implements FingerprintListener, View.OnClickListener  {
    private static final String TAG = "FingerprintActivity";
    TextView usuario, carga;
    String iduser;
    Bundle args = new Bundle();
    ImageView btnEnviar;

    Button btnhuella;
    private byte[] isoFeatureTmp;
    String empleadoIdentificado;
    private FingerprintManager mFingerprintManager;
    private Handler mHandler;
    TextView textResultado;
    Boolean banderaIdentificado;
    String EstacionId, sucursalId, ipEstacion ;

    List<String> AEmpleadoId;
    List<byte[]> AHuella;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clave_usuario);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SQLiteBD db = new SQLiteBD(getApplicationContext());
        sucursalId=db.getIdSucursal();
        EstacionId = db.getIdEstacion();
        ipEstacion = db.getIpEstacion();
        this.setTitle(db.getNombreEsatcion());

        btnhuella  =  findViewById(R.id.btnhuella);
        btnhuella.setOnClickListener(claveUsuario.this);
        mHandler = new Handler(Looper.getMainLooper());
        textResultado = findViewById(R.id.textresultado);

        //lee valores usuario y carga
        usuario= findViewById(R.id.usuario);
        carga = findViewById(R.id.carga);
        idUsuario();
        //Crea Boton Enviar

        btnEnviar = findViewById(R.id.imgticket);

        //Button btnenviar = (Button) findViewById(R.id.btnsiguiente);
        //En espera a recibir el evento Onclick del boton Enviar
        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tipo = getIntent().getStringExtra("tipo");
                if (tipo.equals("0")){
                    enviardatos();
                }else{
                    if (tipo.equals("1")){
                        idUsuario();
                    }
                }


            }
        });
        initFinger();
        ObtieneHuellas();

    }

    private void ObtieneHuellas(){
        AEmpleadoId = new ArrayList<String>();
        AHuella = new ArrayList<byte []>();


        // URL para obtener los empleados  y huellas de la posición de carga X
        String url = "http://"+ipEstacion+"/CorpogasService/api/estacionControles/empleadosPorIsla/estacionId/"+EstacionId+"/tipoBiometricoId/3/posicionCargaId/"+carga.getText().toString();
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
                        AlertDialog.Builder builder = new AlertDialog.Builder(claveUsuario.this);
                        builder.setTitle("Vemta Productos");
                        builder.setMessage("Usuario ocupado: " + errorMensaje)
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent intente = new Intent(getApplicationContext(), ventas.class);
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

    }

    private void initFinger() {
        mFingerprintManager = MyApp.sDriverManager.getFingerprintManager();
        mFingerprintManager.addFignerprintListener(this);
        mFingerprintManager.init();
    }





    private void imprimirticket() {
        final SQLiteBD data = new SQLiteBD(getApplicationContext());
        String url = "http://"+data.getIpEstacion()+"/CorpogasService/api/tickets/generar";

        StringRequest eventoReq = new StringRequest(Request.Method.POST,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String detalle = jsonObject.getString("Detalle");
                            String pie = jsonObject.getString("Pie");
                            JSONObject mensaje = new JSONObject(pie);
                            final JSONArray names = mensaje.getJSONArray("Mensaje");

                            JSONObject det = new JSONObject(detalle);
                            final String numerorecibo = det.getString("NoRecibo");
                            final String numerotransaccion = det.getString("NoTransaccion");
                            final String numerorastreo = det.getString("NoRastreo");
                            final String poscarga = det.getString("PosCarga");
                            final String despachador = det.getString("Desp");
                            String vendedor = det.getString("Vend");
                            String prod = det.getString("Productos");

                            JSONArray producto = det.getJSONArray("Productos");

                            String protic = new String();
                            final String finalProtic = protic;

                            for (int i = 0; i <producto.length() ; i++) {
                                JSONObject p1 = producto.getJSONObject(i);
                                String value = p1.getString("Cantidad");

                                String descripcion = p1.getString("Descripcion");

                                String importe = p1.getString("Importe");

                                String prec = p1.getString("Precio");

                                protic +=value + " | " + descripcion + " | " + prec + " | " + importe+"\n";
                            }

                            final String subtotal = det.getString("Subtotal");
                            final String iva = det.getString("IVA");
                            final String total = det.getString("Total");
                            final String totaltexto = det.getString("TotalTexto");
                            String clave = det.getString("Clave");
                            String carga = getIntent().getStringExtra("car");
                            String user = getIntent().getStringExtra("user");
                            args.putString("numerorecibo", numerorecibo);
                            //args.putString("nombrepago", nombrepago);
                            args.putString("numticket", "2");
                            args.putString("numerotransaccion", numerotransaccion);
                            args.putString("numerorastreo", numerorastreo);
                            args.putString("posicion", carga);
                            args.putString("despachador",despachador);
                            args.putString("vendedor",user);
                            args.putString("productos", finalProtic);

                            args.putString("subtotal",subtotal);
                            args.putString("iva",iva);
                            args.putString("total",total);
                            args.putString("totaltexto",totaltexto);
                            args.putString("mensaje",names.toString());
                            String tipo = getIntent().getStringExtra("tipo");
                            args.putString("tipo",tipo);

                            PrintFragment cf = new PrintFragment();
                            cf.setArguments(args);
                            getFragmentManager().beginTransaction().replace(R.id.tv1, cf).
                                    addToBackStack(PrintFragment.class.getName()).
                                    commit();


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }



                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                String carga = getIntent().getStringExtra("pos");
                //String user = getIntent().getStringExtra("user");
                params.put("PosCarga", carga);
                params.put("IdUsuario",iduser);
                params.put("IdFormaPago", "1");
                params.put("SucursalId",data.getIdEstacion());
                return params;
            }
        };
        // Añade la peticion a la cola
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(eventoReq);
    }

    private void enviardatos() {
        //Se lee el password del objeto y se asigna a variable
        final String posicion;
        posicion = getIntent().getStringExtra("pos");
        EditText pasword = (EditText) findViewById(R.id.pasword);
        final String pass = pasword.getText().toString();

        //Si no se terclea nada envia mensaje de teclear contraseña
        if (pass.isEmpty()){
            Toast.makeText(getApplicationContext(),"Ingresa la contraseña",Toast.LENGTH_SHORT).show();
        }else{
            //----------------------Aqui va el Volley Si se tecleo contraseña----------------------------

            //Conexion con la base y ejecuta valida clave
            SQLiteBD data = new SQLiteBD(getApplicationContext());
            String url = "http://"+data.getIpEstacion()+"/CorpogasService/api/SucursalEmpleados/clave/"+pass;

            // Utilizamos el metodo Post para validar la contraseña
            StringRequest eventoReq = new StringRequest(Request.Method.GET,url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                //Se instancia la respuesta del json
                                JSONObject validar = new JSONObject(response);
                                String valido = validar.getString("Activo");
                                String idusuario = validar.getString("Id");
                                if (valido == "true"){
                                    //Si es valido se asignan valores
                                    usuario.setText(idusuario);
                                    carga.setText(posicion);
                                    //Se instancia y se llama a la clase formas de pago
                                    Intent intent = new Intent(getApplicationContext(), formas_de_pago.class);
                                    intent.putExtra("car",posicion);
                                    intent.putExtra("user",idusuario);
                                    startActivity(intent);
                                    finish();
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
            //-------------------------Aqui termina el volley --------------
        }
    }

    public void idUsuario(){
        final String posicion;
        posicion = getIntent().getStringExtra("pos");
        EditText pasword = (EditText) findViewById(R.id.pasword);
        final String pass = pasword.getText().toString();
        final String idusuario;

        SQLiteBD data = new SQLiteBD(getApplicationContext());
        String url = "http://"+data.getIpEstacion()+"/CorpogasService/api/SucursalEmpleados/clave/"+pass;

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
                                imprimirticket();
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

    private String obteneridusuario(String idusuario) {
        iduser = idusuario.toString();

        return idusuario;
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
                            JSONObject validar = new JSONObject(response);
                            String valido = validar.getString("Activo");
                            String idusuario = validar.getString("Id");
                            if (valido == "true"){
                                //Si es valido se asignan valores
                                usuario.setText(idusuario);
                                carga.setText(posicion);
                                //Se instancia y se llama a la clase formas de pago
                                Intent intent = new Intent(getApplicationContext(), formas_de_pago.class);
                                intent.putExtra("car",posicion);
                                intent.putExtra("user",idusuario);
                                startActivity(intent);
                                finish();
                            }else{
                                //Si no es valido se envia mensaje de conteaseña incorrecta
                                try {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(claveUsuario.this);
                                    builder.setTitle("Productos");
                                    builder.setMessage("Usuario no validado")
                                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    usuario.setText("");
                                                }
                                            }).show();
                                }catch (Exception e){
                                    e.printStackTrace();
                                }

                                //Toast.makeText(getApplicationContext(),"La contraseña es incorecta",Toast.LENGTH_SHORT).show();
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
                        AlertDialog.Builder builder = new AlertDialog.Builder(claveUsuario.this);
                        builder.setTitle("Venta Productos");
                        builder.setMessage("Usuario ocupado: " + errorMensaje)
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent intente = new Intent(getApplicationContext(), posicionProductos.class);
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
    }


    @Override
    public void onClick(View view) {


        if (view.getId() == R.id.btnhuella) {
            //mFingerprintManager.authenticate(3); ES PARA HUELLAS CARGADAS EN LA HANDHELD
            if  (AEmpleadoId.size() == 0){
                Toast.makeText(getApplicationContext(),"Ningún empleado con huellas capturadas",Toast.LENGTH_SHORT).show();
            }else {
                banderaIdentificado = true;
                for (int q = 0; q < AEmpleadoId.size(); q++) { //
                    if (banderaIdentificado == true) {
                        isoFeatureTmp = AHuella.get(q);
                        empleadoIdentificado = AEmpleadoId.get(q);
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
        validaId(empleadoIdentificado);
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
}
