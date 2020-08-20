package com.szzcs.smartpos.Productos;

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

public class posicionProductos extends AppCompatActivity {
    //declaracion de variables
    String titulo = "Seleccione Posicion de Carga";
    ListView list;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posicion_productos);
        //instruccion para que aparezca la flecha de regreso
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //Carga las Posiciones de Carga
        posicionCargaProductos();
    }
    //Proceso para cargar el listView con las posiciones de carga
    private void posicionCargaProductos() {
        //metodo que trae las posiciones de carga
        //String url = "http://sso.corpogas.com.mx:1080/ControllerService/api/posicionCargas/estacion/1/maximo";
        //String url = "http://10.0.1.20/TransferenciaDatosAPI/api/PosCarga/GetMax";
        String url = "http://10.0.2.11/ControllerService/api/posicionCargas/estacion/1/maximo";
        //Obtiene respuesta del metodo
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //procedimiento vax que recibe la respuesta del metodo
                vax(response);
            }
            //Respuesta con Error recibido en el metodo
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Enviamos el error a pantalla para ser visualizado
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

    //Procedimiento que carga las posiciones de carga
    private void vax(final String response) {
        //declaración de contenido del listview
        List<String> maintitle;
        maintitle = new ArrayList<String>();
        List<String> subtitle;
        subtitle = new ArrayList<String>();
        List<Integer> imgid;
        imgid = new ArrayList<>();

        //ciclo para cargar las posiciones de carga
        for (int i = 1; i <= Integer.parseInt(response); i++) {
            maintitle.add("PC" + String.valueOf(i));
            subtitle.add("Magna |  Premium  |  Diesel");
            imgid.add(R.drawable.gas);
        }

        //Inicializacion del listview con el adaptador
        ListAdapterProd adapterProd = new ListAdapterProd(this, maintitle, subtitle, imgid);
        list=(ListView)findViewById(R.id.list);
        list.setAdapter(adapterProd);
        //Definición del Evento Click del listview
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // declaracion de variables, posicion seleccionada en el listview
                int posicion = position +1;
                String posi = String.valueOf(posicion);
                //Se llama la clase para la clave del usuario
                Intent intente = new Intent(getApplicationContext(), claveUsuarioProducto.class);
                //se envia el id seleccionado a la clase Usuario Producto
                intente.putExtra("posicion",posi);
                //Ejecuta la clase del Usuario producto
                startActivity(intente);
            }
        });
    }


}