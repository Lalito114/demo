package com.szzcs.smartpos.Ticket;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ventas extends AppCompatActivity {

    //Lista del segundo panel donde van a formar las posiciones de carga
    ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ventas);
        SQLiteBD data = new SQLiteBD(getApplicationContext());
        this.setTitle(data.getNombreEsatcion());
        //Habilitamos la opcion de regresar en la parte superior izquierda
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //Llamamos al metodo posiciones cargar
        PosicionesCargar();
    }

    public void PosicionesCargar(){

        //Declaramos direccion URL de las posiciones de carga. Para acceder a los metodos de la API
        SQLiteBD db = new SQLiteBD(getApplicationContext());
        String url = "http://"+db.getIpEstacion()+"/CorpogasService/api/posicionCargas/estacion/"+db.getIdEstacion()+"/maximo";
        //inicializamos el String reques que es el metodo de la funcion de Volley que no va a permir accder a la API
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            //El metodo onResponse el cual va cachar si hay una respuesta de tipo cadena
            public void onResponse(String response) {
                //llamamos al metodo posicion en donde aoptine como resultado
                //el valos maximo de posiciones de carga
                posicion(response);

            }
            //si exite un error este entrata de el metodo ErrorListener
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),error.toString(), Toast.LENGTH_LONG).show();
            }
        });
        //Ejecutamos el stringrequest para invocar a la clase volley
        RequestQueue requestQueue = Volley.newRequestQueue(this.getApplicationContext());
        //Agregamos el stringrequest al Requestque
        requestQueue.add(stringRequest);

    }

    private void posicion(final String response) {
        //Declaramos la lista de titulo
        List<String> maintitle;
        //lo assignamos a un nuevo ArrayList
        maintitle = new ArrayList<String>();

        //Creamos la lista para los subtitulos
        List<String> subtitle;
        //Lo asignamos a un nuevo ArrayList
        subtitle = new ArrayList<String>();

        //CReamos una nueva list de tipo Integer con la cual cargaremos a una imagen
        List<Integer> imgid;
        //La asignamos a un nuevo elemento de ArrayList
        imgid = new ArrayList<>();

        //Hacemos el ciclo para que cuente las posiciones de carga en las cuales se van a ver dibijadas
        for (int i = 1; i <= Integer.parseInt(response); i++) {
            maintitle.add("PC" + String.valueOf(i));
            subtitle.add("Magna  |  Premium  |  Diesel");
            imgid.add(R.drawable.gas);
        }
        //Invocamos a la clase de listadapter para crear la vista sobre el layout
        ListAdapter adapter=new ListAdapter(this, maintitle, subtitle,imgid);
        list=(ListView)findViewById(R.id.list);
        list.setAdapter(adapter);

        //Optenemos el numero del Item seleccionado que corresponde a al numero de posicion de carga
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                  int posicion = position +1;
                  String positionfinal = String.valueOf(posicion);
                    //cuando se haya seleccionado una posicion de carga desatara un evento de pasar a otra activity
                    Intent intente = new Intent(getApplicationContext(), claveUsuario.class);
                    //Enviamos el valor de la posicion
                    intente.putExtra("pos",positionfinal);
                    startActivity(intente);
                    finish();
            }
        });
    }

}
