package com.szzcs.smartpos.Puntada.Acumular;

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
import com.szzcs.smartpos.configuracion.SQLiteBD;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class  posicionCarga extends AppCompatActivity {
    String titulo = "Seleccione Posicion de Carga";
    ListView list;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SQLiteBD data = new SQLiteBD(getApplicationContext());
        setContentView(R.layout.activity_posicion_carga_acumular);
        this.setTitle(data.getNombreEsatcion());
    ObtenerPosicionesdeCarga();
    }

    private void ObtenerPosicionesdeCarga() {


        SQLiteBD data = new SQLiteBD(getApplicationContext());
        String IdUsuario = getIntent().getStringExtra("IdUsuario");
        String ClaveDespachador = getIntent().getStringExtra("ClaveDespachador");
        String url = "http://"+data.getIpEstacion()+"/CorpogasService/api/accesoUsuarios/sucursal/"+data.getIdSucursal()+"/clave/" + ClaveDespachador;

        // Utilizamos el metodo Post para validar la contraseña
        StringRequest eventoReq = new StringRequest(Request.Method.GET,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        MetodoResponse(response);
                    }
                    //funcion para capturar errores
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Error 500: No se establecio conexión en el servidor",Toast.LENGTH_SHORT).show();
            }
        });

        // Añade la peticion a la cola
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(eventoReq);
    }

    private void MetodoResponse(final String response) {
        //Declaramos la lista de titulo
        List<String> maintitle;
        //lo assignamos a un nuevo ArrayList
        maintitle = new ArrayList<String>();

        List<String> maintitle1;
        //lo assignamos a un nuevo ArrayList
        maintitle1 = new ArrayList<String>();

        //Creamos la lista para los subtitulos
        List<String> subtitle;
        //Lo asignamos a un nuevo ArrayList
        subtitle = new ArrayList<String>();

        //CReamos una nueva list de tipo Integer con la cual cargaremos a una imagen
        List<Integer> imgid;
        //La asignamos a un nuevo elemento de ArrayList
        imgid = new ArrayList<>();

        String carga;
        String pendientdecobro;
        try {
            JSONObject jsonObject = new JSONObject(response);
            String ObjetoRespuesta = jsonObject.getString("ObjetoRespuesta");

            JSONObject jsonObject1 = new JSONObject(ObjetoRespuesta);
            String control = jsonObject1.getString("Controles");

            JSONArray control1 = new JSONArray(control);
            for (int i = 0; i <control1.length() ; i++) {
                JSONObject posiciones = control1.getJSONObject(i);
                String posi = posiciones.getString("Posiciones");


                JSONArray mangue = new JSONArray(posi);
                for (int j = 0; j < mangue.length(); j++) {
                    JSONObject res = mangue.getJSONObject(j);
                    carga = res.getString("PosicionCargaId");
                    pendientdecobro = res.getString("PendienteCobro");
                    if (pendientdecobro.equals("false")){
                        maintitle.add("PC " + carga);
                        maintitle1.add(carga);
                        subtitle.add("Magna  |  Premium  |  Diesel");
                        imgid.add(R.drawable.gas);
                    }


                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //ListAdapterP adapterP=new ListAdapterP(this, maintitle, subtitle,imgid);
        ListAdapterP adapterP = new ListAdapterP(this, maintitle, subtitle, imgid);
        list=(ListView)findViewById(R.id.list);
        list.setAdapter(adapterP);


        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                int posicion = position +1;
                String posi = String.valueOf(posicion);
                String track = getIntent().getStringExtra("track");
                String idusuario = getIntent().getStringExtra("IdUsuario");
                String clavedespachador = getIntent().getStringExtra("ClaveDespachador");
                String nombrecompleto = getIntent().getStringExtra("nombrecompleto");


                Intent intente = new Intent(getApplicationContext(), productos.class);
                intente.putExtra("pos",posi);
                intente.putExtra("track", track);
                intente.putExtra("IdUsuario", idusuario);
                intente.putExtra("clavedespachador", clavedespachador);
                startActivity(intente);
                finish();
            }
        });
    }


}

