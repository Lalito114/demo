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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class posicionCarga extends AppCompatActivity {
    String titulo = "Seleccione Posicion de Carga";
    ListView list;

    static int numBotones = 20;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posicion_carga_acumular);
        posicionAcumular();
    }

    private void posicionAcumular() {
        String url = "http://10.0.1.20/TransferenciaDatosAPI/api/PosCarga/GetMax";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                MetodoResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),error.toString(), Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> parametros = new HashMap<String, String>();

                return parametros;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this.getApplicationContext());
        requestQueue.add(stringRequest);

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
        ListAdapterP adapterP = new ListAdapterP(this, maintitle, subtitle, imgid);
        list=(ListView)findViewById(R.id.list);
        list.setAdapter(adapterP);


        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                int posicion = position +1;
                String posi = String.valueOf(posicion);

                Intent intente = new Intent(getApplicationContext(), Productos_Seleccionar_Puntada.class);
                intente.putExtra("pos",posi);
                startActivity(intente);
            }
        });
    }


}

