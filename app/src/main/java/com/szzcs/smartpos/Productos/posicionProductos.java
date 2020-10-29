package com.szzcs.smartpos.Productos;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.szzcs.smartpos.FinalizaVenta.claveFinalizaVenta;
import com.szzcs.smartpos.FinalizaVenta.posicionFinaliza;
import com.szzcs.smartpos.Munu_Principal;
import com.szzcs.smartpos.R;
import com.szzcs.smartpos.configuracion.SQLiteBD;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class posicionProductos extends AppCompatActivity {
    //declaracion de variables
    String titulo = "Seleccione Posicion de Carga";
    ListView list;
    String EstacionId, sucursalId, ipEstacion,  usuario, posicion, usuarioid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posicion_productos);
        //instruccion para que aparezca la flecha de regreso
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        SQLiteBD db = new SQLiteBD(getApplicationContext());
        EstacionId = db.getIdEstacion();
        sucursalId = db.getIdSucursal();
        ipEstacion = db.getIpEstacion();
        usuarioid = getIntent().getStringExtra("usuario");
        usuario = getIntent().getStringExtra("clave");
        //Carga las Posiciones de Carga
        posicionCargaProductos();
    }
    //Proceso para cargar el listView con las posiciones de carga

    public void posicionCargaProductos(){
        String url = "http://"+ipEstacion+"/CorpogasService/api/accesoUsuarios/sucursal/"+sucursalId+"/clave/" + usuario;

        // Utilizamos el metodo Post para validar la contraseña
        StringRequest eventoReq = new StringRequest(Request.Method.GET,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
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
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String ObjetoRespuesta = jsonObject.getString("ObjetoRespuesta");
                            String mensaje = jsonObject.getString("Mensaje");

                            if (ObjetoRespuesta.equals("null")){
                                //Toast.makeText(posicionProductos.this, mensaje, Toast.LENGTH_SHORT).show();
                                try {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(posicionProductos.this);
                                    builder.setTitle("Productos");
                                    builder.setMessage("" + mensaje)
                                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    Intent intent = new Intent(getApplicationContext(), claveProducto.class);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            }).show();
                                }catch (Exception e){
                                    e.printStackTrace();
                                }

                            }else {
                                JSONObject jsonObject1 = new JSONObject(ObjetoRespuesta);
                                String control = jsonObject1.getString("Controles");

                                JSONArray control1 = new JSONArray(control);
                                for (int i = 0; i < control1.length(); i++) {
                                    JSONObject posiciones = control1.getJSONObject(i);
                                    String posi = posiciones.getString("Posiciones");

                                    JSONArray mangue = new JSONArray(posi);
                                    for (int j = 0; j < mangue.length(); j++) {
                                        JSONObject res = mangue.getJSONObject(j);
                                        carga = res.getString("PosicionCargaId");

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
                        ListAdapterProd adapterProd = new ListAdapterProd(posicionProductos.this, maintitle, subtitle, imgid);
                        list = (ListView) findViewById(R.id.list);
                        list.setAdapter(adapterProd);

                        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                // TODO Auto-generated method stub
                                String numerocarga = maintitle1.get(position);
                                posicion = numerocarga;
                                //Se llama la clase para la clave del usuario
                                Intent intente = new Intent(getApplicationContext(), VentasProductos.class);
                                //se envia el id seleccionado a la clase Usuario Producto
                                intente.putExtra("posicion",posicion);
                                intente.putExtra("usuario",usuarioid);
                                intente.putExtra("cadenaproducto", "");
                                //Ejecuta la clase del Usuario producto
                                startActivity(intente);
                                //Finaliza activity
                                finish();
                            }
                        });
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
        eventoReq.setRetryPolicy(new DefaultRetryPolicy(12000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(eventoReq);
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
                finish();
            }
        });
    }
    //Metodo para regresar a la actividad principal
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), Munu_Principal.class);
        startActivity(intent);
        finish();
    }


}