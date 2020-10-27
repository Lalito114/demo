package com.szzcs.smartpos.TanqueLleno;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.client.android.Contents;
import com.szzcs.smartpos.Munu_Principal;
import com.szzcs.smartpos.R;
import com.szzcs.smartpos.configuracion.SQLiteBD;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PosicionCargaTLl extends AppCompatActivity {
    ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posicion_carga_tll);
        SQLiteBD data = new SQLiteBD(getApplicationContext());
        this.setTitle(data.getNombreEsatcion());
        MetodoResponse("16");
    }


    private void MetodoResponse(final String response) {
        List<String> maintitle;
        maintitle = new ArrayList<String>();

        List<String> subtitle;
        subtitle = new ArrayList<String>();

        List<Integer> imgid;
        imgid = new ArrayList<>();


        for (int i = 1; i <= Integer.parseInt(response); i++) {
            maintitle.add("PC" + String.valueOf(i));
            subtitle.add("Magna | Premium | Diesel");
            imgid.add(R.drawable.gas);
        }

        //ListAdapterP adapterP=new ListAdapterP(this, maintitle, subtitle,imgid);
        ListAdapterPPCTL adapterP = new ListAdapterPPCTL(this, maintitle, subtitle, imgid);
        list=(ListView)findViewById(R.id.list);
        list.setAdapter(adapterP);


        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                int posicion = position +1;
                String posi = String.valueOf(posicion);
                String track = getIntent().getStringExtra("track");
                String pass = getIntent().getStringExtra("pass");
                enviardatos(pass,posi,track);


            }
        });
    }
    private void enviardatos(String pass, final String posi, final String track){
        //----------------------Aqui va el Volley Si se tecleo contraseña----------------------------
        //Conexion con la base y ejecuta valida clave
        final SQLiteBD data = new SQLiteBD(getApplicationContext());
        String url = "http://"+data.getIpEstacion()+"/CorpogasService/api/tanqueLleno/InicioAutorizacion/clave/" + pass;
        // Utilizamos el metodo Post para validar la contraseña
        StringRequest eventoReq = new StringRequest(Request.Method.POST,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject datos = new JSONObject(response);
                            String correcto = datos.getString("Correcto");
                            if (correcto.equals("false")){
                                String mensaje = datos.getString("Mensaje");
                                AlertDialog.Builder builder = new AlertDialog.Builder(PosicionCargaTLl.this);
                                builder.setTitle("Tarjeta Puntada");
                                builder.setMessage(mensaje);
                                builder.setPositiveButton("Cerrar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent intent = new Intent(getApplicationContext(), Munu_Principal.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                });
                                AlertDialog dialog= builder.create();
                                dialog.show();

                            }else{
                                String creditodispoble = datos.getString("CreditoDisponible");
                                final String numerointernosucursal = datos.getString("NumeroInternoSucursal");
                                final String odometro = datos.getString("PideOdometro");

                                final String placa = datos.getString("PidePlaca");
                                final String sucursalempleados = datos.getString("SucursalEmpleadoId");
                                final String tipocliente = datos.getString("TipoCliente");
                                String conbustibles = datos.getString("CombustiblesAutorizados");
                                final String clave = datos.getString("Clave");
                                final String transaccionId = datos.getString("TransaccionId");
                                final String folio = datos.getString("Folio");
                                //JSONObject conbustible = new JSONObject(conbustibles);

                                if (correcto == "true"){
                                    if (odometro.equals("true")){
                                        try {
                                            final EditText input = new EditText(getApplicationContext());
                                            input.setTextColor(Color.BLACK);
                                            input.setInputType(InputType.TYPE_CLASS_NUMBER);
                                            input.setGravity(Gravity.CENTER);
                                            input.setTextSize(22);
                                            AlertDialog.Builder builder = new AlertDialog.Builder(PosicionCargaTLl.this);
                                            builder.setTitle("Ingresa el Odometro \n");
                                            builder.setView(input)
                                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                            String odometro = input.getText().toString();
                                                            if (odometro != null){
                                                                IngresarPlacas(placa,odometro,numerointernosucursal,sucursalempleados,posi,track,clave,tipocliente,transaccionId,folio);
                                                            }else{
                                                                Toast.makeText(PosicionCargaTLl.this, "Ingresa el odometro", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    })
                                                    .setNegativeButton("Calcelar", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            dialog.cancel();
                                                        }
                                                    }).show();
                                        }catch (Exception e){
                                            e.printStackTrace();
                                        }
                                    }else{
                                        if (placa.equals("true")){
                                            try {
                                                final EditText input = new EditText(getApplicationContext());
                                                input.setTextColor(Color.BLACK);
                                                input.setGravity(Gravity.CENTER);
                                                input.setTextSize(22);
                                                AlertDialog.Builder builder = new AlertDialog.Builder(PosicionCargaTLl.this);
                                                builder.setTitle("Ingresa las Placas \n");
                                                builder.setView(input)
                                                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                                String placas = input.getText().toString();
                                                                if (placas != null){
                                                                    Intent intent = new Intent(getApplicationContext(),ProductoTLl.class);
                                                                    intent.putExtra("placas",placas);
                                                                    intent.putExtra("odometro",odometro);
                                                                    intent.putExtra("NumeroInternoEstacion", numerointernosucursal);
                                                                    intent.putExtra("SucursalEmpleadoId",sucursalempleados);
                                                                    intent.putExtra("PosicioDeCarga", posi);
                                                                    intent.putExtra("NumeroDeTarjeta",track);
                                                                    intent.putExtra("ClaveTanqueLleno",clave);
                                                                    intent.putExtra("Tipocliente",tipocliente);
                                                                    startActivity(intent);
                                                                }else{
                                                                    Toast.makeText(PosicionCargaTLl.this, "Ingresa las Placas", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        })
                                                        .setNegativeButton("Calcelar", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                dialog.cancel();
                                                            }
                                                        }).show();
                                            }catch (Exception e){
                                                e.printStackTrace();
                                            }
                                        }else{
                                            Intent intent = new Intent(getApplicationContext(),ProductoTLl.class);
                                            startActivity(intent);
                                        }

                                    }

                                }else{
                                    Toast.makeText(getApplicationContext(),"La contraseña ingresada no es correcta",Toast.LENGTH_LONG).show();
                                }
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    //funcion para capturar errores
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
                params.put("EstacionId",data.getIdEstacion());
                params.put("PosicionCarga",posi);
                params.put("TarjetaCliente",track);
                return params;
            }
        };

        // Añade la peticion a la cola
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(eventoReq);
    }

    private void IngresarPlacas(String placa, final String odometro, final String numerointernosucursal,
                                final String sucursalempleados, final String posi, final String track,
                                final String clave, final String tipocliente, final String transaccionId, final String folio) {
        if (placa.equals("true")){
            try {
                final EditText input = new EditText(getApplicationContext());
                input.setTextColor(Color.BLACK);
                input.setInputType(InputType. TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
                input.setGravity(Gravity.CENTER);
                input.setTextSize(22);
                AlertDialog.Builder builder = new AlertDialog.Builder(PosicionCargaTLl.this);
                builder.setTitle("Ingresa las Placas \n");
                builder.setView(input)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String placas = input.getText().toString();
                                if (placas != null){
                                    Intent intent = new Intent(getApplicationContext(),ProductoTLl.class);
                                    intent.putExtra("placas",placas);
                                    intent.putExtra("odometro",odometro);
                                    intent.putExtra("NumeroInternoEstacion", numerointernosucursal);
                                    intent.putExtra("SucursalEmpleadoId",sucursalempleados);
                                    intent.putExtra("PosicioDeCarga", posi);
                                    intent.putExtra("NumeroDeTarjeta",track);
                                    intent.putExtra("ClaveTanqueLleno",clave);
                                    intent.putExtra("Tipocliente",tipocliente);
                                    intent.putExtra("transaccionid",transaccionId);
                                    intent.putExtra("folio", folio);
                                    startActivity(intent);
                                }else{
                                    Toast.makeText(PosicionCargaTLl.this, "Ingresa las Placas", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton("Calcelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }).show();
            }catch (Exception e){
                e.printStackTrace();
            }
        } else{
            Toast.makeText(this, "Algo salio mal", Toast.LENGTH_SHORT).show();
         }
    }


}
