package com.szzcs.smartpos.TanqueLleno;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
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
import com.szzcs.smartpos.TanqueLleno.claveuser_tqll;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PosicionCargaTqll extends AppCompatActivity {

    String titulo = "Ticket - Posicion";
    ListView list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posicion_carga_tqll);
        PosicionesCargar();
    }

    public void PosicionesCargar(){

        String url = "http://10.0.1.20/TransferenciaDatosAPI/api/PosCarga/GetMax";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                vax(response);

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

    private void vax(final String response) {
        List<String> maintitle;
        maintitle = new ArrayList<String>();

        List<String> subtitle;
        subtitle = new ArrayList<String>();

        List<Integer> imgid;
        imgid = new ArrayList<>();


        for (int i = 1; i <= Integer.parseInt(response); i++) {
            maintitle.add("PC" + String.valueOf(i));
            subtitle.add("Combustible Disponible");
            imgid.add(R.drawable.gas);
        }

        ListAdapterTqllPosCarga adapter=new ListAdapterTqllPosCarga(this, maintitle, subtitle,imgid);
        list=(ListView)findViewById(R.id.list);
        list.setAdapter(adapter);


        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                int posicion = position +1;
                String posi = String.valueOf(posicion);

                Intent intente = new Intent(getApplicationContext(), claveuser_tqll.class);
                intente.putExtra("pos",posi);
                startActivity(intente);




            }
        });
    }

    class ButtonsOnClickListener implements View.OnClickListener
    {
        public ButtonsOnClickListener(Response.Listener<String> stringListener) {

        }

        @Override
        public void onClick(View v)
        {
            Button b = (Button) v;
            Intent intente = new Intent(getApplicationContext(), claveuser_tqll.class);
            intente.putExtra("pos",b.getText());
            startActivity(intente);
        }

    }





//    class ButtonOnClickListener implements OnClickListener {
//        public ButtonOnClickListener(Response.Listener<String> listener) {
//        }
//
//        @Override
//        public void onClick(View v) {
////            Button b = (Button) v;
////            Intent intente = new Intent(getApplicationContext(), claveuser_tqll.class);
////            intente.putExtra("pos",b.getText());
////            startActivity(intente);
//
//            Bundle bundle = getIntent().getExtras();
//            final String track2 = bundle.getString("track");
//            final String nip = bundle.getString("nip");
//            final Button b = (Button)v;
//
//            String url = "http://10.0.1.20/TransferenciaDatosAPI/api/tarjetas/sendinfo";
//
//            StringRequest eventoReq = new StringRequest(Request.Method.POST,url,
//                    new Response.Listener<String>() {
//                        @Override
//                        public void onResponse(String response) {
//                            Toast.makeText(getApplicationContext(),response,Toast.LENGTH_LONG).show();
//                            MesanjeVista(response);
//
//                        }
//                    }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_SHORT).show();
//                }
//            }){
//                @Override
//                protected Map<String, String> getParams() {
//                    // Posting parameters to login url
//                    Map<String, String> params = new HashMap<String, String>();
//                    params.put("RequestId","1");
//                    params.put("PosCarga", (String) b.getText());
//                    params.put("Tarjeta",track2);
//                    params.put("NuTarjetero","1");
//                    return params;
//                }
//            };
//
//            // Añade la peticion a la cola
//            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
//            requestQueue.add(eventoReq);
//
//        }
//    }
//
//    private void MesanjeVista(String response) {
//        try {
//            JSONObject validar = new JSONObject(response);
//            String valido = validar.getString("sMensaje");
//
//            if (valido.isEmpty()){
//                Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_LONG).show();
//            }else {
//                Toast.makeText(getApplicationContext(),valido,Toast.LENGTH_LONG).show();
//
//
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }




}