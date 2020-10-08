package com.szzcs.smartpos.configuracion;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
import com.szzcs.smartpos.Munu_Principal;
import com.szzcs.smartpos.PrintFragment;
import com.szzcs.smartpos.R;
import com.szzcs.smartpos.SplashEmpresas.Splash;
import com.szzcs.smartpos.SplashEmpresas.SplashGulf;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfiguracionServidor extends AppCompatActivity {

    EditText edtOct1, edtOct2, edtOct3, edtOct4;
    Button btnenviar;
    String oct1, oct2,oct3,oct4, ip, ip2;
    static String mac;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SQLiteBD data = new SQLiteBD(getApplicationContext());
        boolean verdad = data.checkDataBase("/data/data/com.szzcs.smartpos/databases/ConfiguracionEstacion.db");
        if(verdad == true){
            String tipo = data.getTipoEstacion();
            if (tipo.equals("CORPOGAS")){
                        Intent intent = new Intent(getApplicationContext(), Splash.class);
                        startActivity(intent);//h

                        finish();
            }else{
                if (tipo.equals("GULF")){
                            Intent intent = new Intent(getApplicationContext(), SplashGulf.class);
                            startActivity(intent);
                            finish();
                }else{
                    if (tipo.equals("PEMEX")){
                                Intent intent = new Intent(getApplicationContext(), Splash.class);
                                startActivity(intent);
                                finish();
                    }
                }
            }
        }else{
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_configuracion_servidor);
            this.setTitle("Configuracion Inicial Servidor");
            getMacAddress();
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

                    ip2 = oct1 + "." + oct2 + "." + oct3 + "." + oct4;


                    if (oct1.isEmpty()){
                        Toast.makeText(getApplicationContext(),"Ingresa el campo 12",Toast.LENGTH_LONG).show();
                        //happy
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
                    edtOct1.setText("");
                    edtOct2.setText("");
                    edtOct3.setText("");
                    edtOct4.setText("");
                }
            });
        }



    }

    private void ConectarIP() {
        String url = "http://" + ip2 + "/CorpogasService/api/estaciones/ip/"+ip;
        //Utilizamos el metodo Post para colocar los datos en el  ticket
        StringRequest eventoReq = new StringRequest(Request.Method.GET,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        guardarDatosDBEmpresa(response);

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
    public static String getMacAddress() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    mac = String.valueOf(res1.append(Integer.toHexString(b & 0xFF) + ":"));
                }

                if (res1.length() > 0) {
                    mac = String.valueOf(res1.deleteCharAt(res1.length() - 1));

                }

                return res1.toString();
            }
        } catch (Exception ex) {
            Log.e("Error", ex.getMessage());
        }
        return "";
    }

    private void guardarDatosDBEmpresa(String response) {
        try {
            JSONObject empresa = new JSONObject(response);
            String id = empresa.getString("Id");
            String siic = empresa.getString("Siic");
            String sucursalid = empresa.getString("SucursalId");
            String sucursal = empresa.getString("Sucursal");

            JSONObject sucursal1 = new JSONObject(sucursal);
            String correo = sucursal1.getString("Correo");
            String empresaid  = sucursal1.getString("EmpresaId");
            String ip = sucursal1.getString("Ip");
            String nombre = sucursal1.getString("Nombre");
            String numerofranquicia = sucursal1.getString("NumeroFranquicia");
            String numinterno = sucursal1.getString("NumeroInterno");
            String tipoestablecimiento = sucursal1.getString("Empresa");

            JSONObject tipoestablecimiento1 = new JSONObject(tipoestablecimiento);
            String grupo = tipoestablecimiento1.getString("Grupo");

            JSONObject quees = new JSONObject(grupo);
            String descripcion = quees.getString("Descripcion");

            SQLiteBD data = new SQLiteBD(getApplicationContext());
            data.InsertarDatosEstacion(id,sucursalid,siic,correo,empresaid,ip,nombre,numerofranquicia,numinterno, descripcion);
            
            guardarDatosEncabezado(ip,empresaid);
            obtenerNumeroTarjetero(sucursalid);

            if (descripcion.equals("CORPOGAS")){
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Inicio de Configuración");
                builder.setMessage("Los datos de guardaron correctamente");
                builder.setPositiveButton("Cerrar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(getApplicationContext(), Splash.class);
                        startActivity(intent);
                        finish();
                    }
                });
                AlertDialog dialog= builder.create();
                dialog.show();
            }else{
                if (descripcion.equals("GULF")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Inicio de Configuración");
                    builder.setMessage("Los datos de guardaron correctamente");
                    builder.setPositiveButton("Cerrar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(getApplicationContext(), SplashGulf.class);
                            startActivity(intent);
                            finish();
                        }
                    });
                    AlertDialog dialog= builder.create();
                    dialog.show();
                }else{
                    if (descripcion.equals("PEMEX")){
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setTitle("Inicio de Configuración");
                        builder.setMessage("Los datos de guardaron correctamente");
                        builder.setPositiveButton("Cerrar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(getApplicationContext(), Splash.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                        AlertDialog dialog= builder.create();
                        dialog.show();
                    }else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setTitle("Inicio de Configuración");
                        builder.setMessage("Los datos no se guardaron en la base de datos," +
                                " POR FAVOR ingresa correctamente la direccion IP");
                        builder.setPositiveButton("Cerrar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });
                        AlertDialog dialog= builder.create();
                        dialog.show();

                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void obtenerNumeroTarjetero(final String sucursalid) {
        String url = "http://"+ip2+"/CorpogasService/api/conexiones/ConfigurarConexion";

        StringRequest eventoReq = new StringRequest(Request.Method.POST,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject respons = new JSONObject(response);
                            String sucursalid = respons.getString("SucursalId");
                            String sucursal = respons.getString("Sucursal");
                            String tipoconexionid = respons.getString("TipoConexionId");
                            String tipoconexion =  respons.getString("TipoConexion");
                            String ip = respons.getString("Ip");
                            String puerto = respons.getString("Puerto");
                            String consecutivo = respons.getString("Consecutivo");
                            String direccionmac = respons.getString("DireccionMac");
                            String propiedadconexion = respons.getString("PropiedadConexion");
                            String url = respons.getString("Url");
                            String id = respons.getString("Id");

                            SQLiteBD data = new SQLiteBD(ConfiguracionServidor.this);
                            data.InsertarDatosNumeroTarjetero(direccionmac,propiedadconexion,id);

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
                params.put("SucursalId", sucursalid);
                params.put("TipoConexionId","7");
                params.put("DireccionMac", mac);
                return params;
            }
        };

        // Añade la peticion a la cola
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(eventoReq);
    }

    private void guardarDatosEncabezado(String ip, String empresaid) {
        //Utilizamos el metodo Get para obtener el encabezado para los tickets
        //hay que cambiar el volo 1 del fina po el numeo de la estacion que se encuentra
        String url = "http://"+ip+"/CorpogasService/api/tickets/cabecero/estacion/"+empresaid;
        //Se solicita peticion GET para obtener el encabezado
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    //se instancia la respuesta del JSON
                    JSONObject cabecero = new JSONObject(response);

                    String cabecero1 = cabecero.getString("Cabecero");
                    JSONObject encabezado1 = new JSONObject(cabecero1);

                    String domicilio = encabezado1.getString("Domicilio");
                    JSONObject domicilio1 = new JSONObject(domicilio);

                    String calle = domicilio1.getString("Calle");
                    String cp = domicilio1.getString("CodigoPostal");
                    String colonia = domicilio1.getString("Colonia");
                    String empresaid = domicilio1.getString("EmpresaId");
                    String estado = domicilio1.getString("Estado");
                    String localidad = domicilio1.getString("Localidad");
                    String municipio = domicilio1.getString("Municipio");
                    String numeroexterior = domicilio1.getString("NumeroExterior");
                    String numerointerior = domicilio1.getString("NumeroInterior");
                    String pais = domicilio1.getString("Pais");

                    String empresa = encabezado1.getString("Empresa");
                    JSONObject empresa1 = new JSONObject(empresa);

                    String razonsocial = empresa1.getString("RazonSocial");
                    String rfc2 = empresa1.getString("Rfc");
                    String rfc1 = empresa1.getString("TipoRegimenFiscalId");
                    String regimenfiscal = null;
                    if (rfc1.equals("1")){
                         regimenfiscal = "REGIMEN GENERAL DE LEY PERSONAS MORALES";
                    }
                    SQLiteBD data = new SQLiteBD(getApplicationContext());
                    data.InsertarDatosEncabezado(empresaid, razonsocial, regimenfiscal,calle, numeroexterior,numerointerior,colonia,localidad,municipio,estado,pais,cp,rfc2);

                } catch (JSONException e) {
                    //herramienta  para diagnostico de excepciones
                    e.printStackTrace();
                }
            }
            //funcion para capturar errores
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),error.toString(), Toast.LENGTH_LONG).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this.getApplicationContext());
        requestQueue.add(stringRequest);
    }


}
