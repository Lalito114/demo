package com.szzcs.smartpos.Cortes2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.szzcs.smartpos.Cortes.ClaveIsla;
import com.szzcs.smartpos.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Islas2 extends AppCompatActivity {

    ListView list;




    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_islas2);

        PosisionesIslas();
        set("1");

    }

    public void PosisionesIslas() {

        final String claveUsuario;
        claveUsuario = getIntent().getStringExtra("password");

         String url = "http://10.0.1.20/corpogasservice/api/estacionControles/estacion/1/ClaveEmpleado/" + claveUsuario;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {



            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),error.toString(), Toast.LENGTH_LONG).show();
            }

        }){
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<String, String>();

                return parametros;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this.getApplicationContext());
        requestQueue.add(stringRequest);
    }

    private void set(final String response) {
        List<String> maintitle;
        maintitle = new ArrayList<String>();

        List<String> subtitle;
        subtitle = new ArrayList<String>();

        List<Integer> imgid;
        imgid = new ArrayList<>();

        for (int i = 1; i <= Integer.parseInt(response); i++) {
            maintitle.add("Isla " + String.valueOf(i));
            subtitle.add("");
            imgid.add(R.drawable.isla_p);
        }

        ListAdapterIsla2 adapter=new ListAdapterIsla2(this, maintitle, subtitle,imgid);
        list=(ListView)findViewById(R.id.list);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int posicion = position + 1;
                String posi = String.valueOf(posicion);

                Intent intente = new Intent(getApplicationContext(), PosicionCargaCorte.class);
                intente.putExtra("pos", posi);
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
            Intent intente = new Intent(getApplicationContext(), PosicionCargaCorte.class);
            intente.putExtra("pos",b.getText());
            startActivity(intente);
        }

    }



}