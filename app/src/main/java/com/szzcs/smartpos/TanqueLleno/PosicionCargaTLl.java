package com.szzcs.smartpos.TanqueLleno;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.szzcs.smartpos.R;

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
            subtitle.add("C");
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
        String url = "http://10.0.1.20/CorpogasService/api/tanqueLleno/InicioAutorizacion/clave/" + pass;
        // Utilizamos el metodo Post para validar la contraseña
        StringRequest eventoReq = new StringRequest(Request.Method.POST,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(getApplicationContext(),response,Toast.LENGTH_LONG).show();
                        try {
                            JSONObject datos = new JSONObject(response);
                            String correcto = datos.getString("Correcto");
                            String creditodispoble = datos.getString("CreditoDisponible");
                            String numerointernosucursal = datos.getString("NumeroInternoSucursal");
                            String odometro = datos.getString("PideOdometro");
                            String placa = datos.getString("PidePlaca");
                            String sucursalempleados = datos.getString("SucursalEmpleadoId");
                            String tipocliente = datos.getString("TipoCliente");
                            String conbustibles = datos.getString("CombustiblesAutorizados");
                            String clave = datos.getString("Clave");
                            String transaccionId = datos.getString("TransaccionId");
                            String folio = datos.getString("Folio");
                            //JSONObject conbustible = new JSONObject(conbustibles);

                            if (correcto == "true"){
                                Intent intent = new Intent(getApplicationContext(),ProductoTLl.class);
                                intent.putExtra("NumeroInternoEstacion",numerointernosucursal);
                                intent.putExtra("SucursalEmpleadoId",sucursalempleados);
                                intent.putExtra("PosicioDeCarga",posi);
                                intent.putExtra("NumeroDeTarjeta",track);
                                intent.putExtra("ClaveTanqueLleno",clave);
                                intent.putExtra("Tipocliente",tipocliente);
                                intent.putExtra("Folio",folio);
                                intent.putExtra("TransaccionId",transaccionId);
                                startActivity(intent);
                            }else{
                                Toast.makeText(getApplicationContext(),"No tienes autorizacion",Toast.LENGTH_LONG).show();
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
                params.put("EstacionId","1");
                params.put("PosicionCarga",posi);
                params.put("TarjetaCliente",track);
                return params;
            }
        };

        // Añade la peticion a la cola
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(eventoReq);
    }
}
