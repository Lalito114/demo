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
import com.szzcs.smartpos.Munu_Principal;
import com.szzcs.smartpos.R;
import com.szzcs.smartpos.configuracion.SQLiteBD;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class posicionProductos extends AppCompatActivity {
    //declaracion de variables
    String titulo = "Seleccione Posicion de Carga";
    ListView list;
    String EstacionId, sucursalId, ipEstacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posicion_productos);
        SQLiteBD db = new SQLiteBD(getApplicationContext());
        EstacionId = db.getIdEstacion();
        sucursalId = db.getIdSucursal();
        ipEstacion= db.getIpEstacion();
        //instruccion para que aparezca la flecha de regreso
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //Carga las Posiciones de Carga
        posicionCargaProductos();
    }
    //Proceso para cargar el listView con las posiciones de carga
    public void posicionCargaProductos(){

        //Declaramos direccion URL de las posiciones de carga. Para acceder a los metodos de la API
        String url = "http://"+ipEstacion+"/CorpogasService/api/posicionCargas/estacion/"+EstacionId+"/maximo";
        //inicializamos el String reques que es el metodo de la funcion de Volley que no va a permir accder a la API
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            //El metodo onResponse el cual va cachar si hay una respuesta de tipo cadena
            public void onResponse(String response) {
                //llamamos al metodo posicion en donde aoptine como resultado
                //el valos maximo de posiciones de carga
                vax(response);

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
                Intent intente = new Intent(getApplicationContext(), claveProducto.class);
                //se envia el id seleccionado a la clase Usuario Producto
                intente.putExtra("posicion",posi);
                //Ejecuta la clase del Usuario producto
                startActivity(intente);
            }
        });
    }
    //Metodo para regresar a la actividad principal
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), Munu_Principal.class);
        startActivity(intent);
        //finish();
    }


}