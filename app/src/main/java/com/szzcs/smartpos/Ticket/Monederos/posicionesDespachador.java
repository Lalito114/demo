package com.szzcs.smartpos.Ticket.Monederos;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.szzcs.smartpos.Munu_Principal;
import com.szzcs.smartpos.PrintFragment;
import com.szzcs.smartpos.R;
import com.szzcs.smartpos.Ticket.ListAdapter;
import com.szzcs.smartpos.Ticket.claveUsuario;
import com.szzcs.smartpos.configuracion.SQLiteBD;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class posicionesDespachador extends AppCompatActivity {
    ListView list;
    Bundle args = new Bundle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posiciones_despachador);
        SQLiteBD data = new SQLiteBD(this);
        this.setTitle(data.getRazonSocial());

        Posicionesdecarga();
    }

    public void Posicionesdecarga(){


        SQLiteBD data = new SQLiteBD(getApplicationContext());
        String IdUsuario = getIntent().getStringExtra("IdUsuario");
        String ClaveDespachador = getIntent().getStringExtra("ClaveDespachador");
        String url = "http://"+data.getIpEstacion()+"/CorpogasService/api/accesoUsuarios/sucursal/"+data.getIdSucursal()+"/clave/" + ClaveDespachador;

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
                        String pendientecobro;
                        JSONArray validar = new JSONArray();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String ObjetoRespuesta = jsonObject.getString("ObjetoRespuesta");
                            if (ObjetoRespuesta.equals("null")){
                                String mensaje = jsonObject.getString("Mensaje");
                                try{
                                    AlertDialog.Builder builder = new AlertDialog.Builder(posicionesDespachador.this);
                                    builder.setTitle("Tarjeta Puntada");
                                    builder.setMessage(mensaje);
                                    builder.setPositiveButton("Cerrar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            Intent intent = new Intent(getApplicationContext(),Munu_Principal.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    });
                                    AlertDialog dialog= builder.create();
                                    dialog.show();
                                }catch (Exception e){
                                    e.printStackTrace();
                                }

                            }else{
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
                                        pendientecobro = res.getString("PendienteCobro");
                                        if (pendientecobro.equals("true")){
                                            maintitle.add("PC " + carga);
                                            maintitle1.add(carga);
                                            subtitle.add("Magna  |  Premium  |  Diesel");
                                            imgid.add(R.drawable.gas);
                                        }else{
                                            validar.put(pendientecobro);
                                            if (validar.length() == mangue.length()){
                                                try{
                                                    AlertDialog.Builder builder = new AlertDialog.Builder(posicionesDespachador.this);
                                                    builder.setTitle("Tarjeta Puntada");
                                                    builder.setMessage("No hay tickets Pendientes");
                                                    builder.setPositiveButton("Cerrar", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                            Intent intent = new Intent(getApplicationContext(),Munu_Principal.class);
                                                            startActivity(intent);
                                                            finish();
                                                        }
                                                    });
                                                    AlertDialog dialog= builder.create();
                                                    dialog.show();
                                                }catch (Exception e){
                                                    e.printStackTrace();
                                                }

                                            }else{
                                                AdapterPosiciones adapter=new AdapterPosiciones(posicionesDespachador.this, maintitle, subtitle,imgid);
                                                list=(ListView)findViewById(R.id.list);
                                                list.setAdapter(adapter);

                                                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                                                    @Override
                                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                        // TODO Auto-generated method stub
                                                        String numerocarga = maintitle1.get(position);
                                                        imprimirticket(numerocarga);
                                                    }
                                                });
                                            }
                                        }


                                    }

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
        });

        // Añade la peticion a la cola
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(eventoReq);
    }

    private void imprimirticket(String carga) {
        final SQLiteBD data = new SQLiteBD(getApplicationContext());
        String url = "http://"+data.getIpEstacion()+"/CorpogasService/api/tickets/generar";

        StringRequest eventoReq = new StringRequest(Request.Method.POST,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String detalle = jsonObject.getString("Detalle");
                            if (detalle.equals("null")){
                                String estado1 = jsonObject.getString("Resultado");
                                JSONObject descripcion = new JSONObject(estado1);
                                String estado = descripcion.getString("Descripcion");
                                AlertDialog.Builder builder = new AlertDialog.Builder(posicionesDespachador.this);
                                builder.setTitle("Tarjeta Puntada");
                                builder.setMessage(estado);
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
                                String pie = jsonObject.getString("Pie");
                                JSONObject mensaje = new JSONObject(pie);
                                final JSONArray names = mensaje.getJSONArray("Mensaje");
                                String nombretarjeta = mensaje.getString("NombreTarjeta");
                                String numerotarjeta = mensaje.getString("NumeroTarjeta");
                                String odometro = mensaje.getString("Odometro");
                                String saldo= mensaje.getString("Saldo");


                                JSONObject det = new JSONObject(detalle);
                                final String numerorecibo = det.getString("NoRecibo");
                                final String numerotransaccion = det.getString("NoTransaccion");
                                final String numerorastreo = det.getString("NoRastreo");
                                final String poscarga = det.getString("PosCarga");
                                final String despachador = det.getString("Desp");
                                String formapago = det.getString("FormaPago");
                                String vendedor = det.getString("Vend");
                                String prod = det.getString("Productos");

                                JSONArray producto = det.getJSONArray("Productos");

                                String protic = new String();
                                final String finalProtic = protic;

                                for (int i = 0; i <producto.length() ; i++) {
                                    JSONObject p1 = producto.getJSONObject(i);
                                    String value = p1.getString("Cantidad");

                                    String descripcion = p1.getString("Descripcion");

                                    String importe = p1.getString("Importe");

                                    String prec = p1.getString("Precio");

                                    protic +=value + " | " + descripcion + " | " + prec + " | " + importe+"\n";
                                }

                                final String subtotal = det.getString("Subtotal");
                                final String iva = det.getString("IVA");
                                final String total = det.getString("Total");
                                final String totaltexto = det.getString("TotalTexto");
                                String clave = det.getString("Clave");
                                String carga = getIntent().getStringExtra("car");
                                String user = getIntent().getStringExtra("user");
                                args.putString("numerorecibo", numerorecibo);
                                //args.putString("nombrepago", nombrepago);
                                args.putString("numticket", "2");
                                args.putString("numerotransaccion", numerotransaccion);
                                args.putString("numerorastreo", numerorastreo);
                                args.putString("posicion", carga);
                                args.putString("despachador",despachador);
                                args.putString("vendedor",user);
                                args.putString("formapago",formapago);
                                args.putString("productos", protic);

                                args.putString("subtotal",subtotal);
                                args.putString("iva",iva);
                                args.putString("total",total);
                                args.putString("totaltexto",totaltexto);
                                args.putString("mensaje",names.toString());
                                args.putString("nombretarjeta",nombretarjeta);
                                args.putString("numerotarjeta",numerotarjeta);
                                args.putString("odometro",odometro);
                                args.putString("saldo",saldo);

                                String tipo = getIntent().getStringExtra("tipo");
                                args.putString("tipo",tipo);

                                PrintFragment cf = new PrintFragment();
                                cf.setArguments(args);
                                getFragmentManager().beginTransaction().replace(R.id.tv1, cf).
                                        addToBackStack(PrintFragment.class.getName()).
                                        commit();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }



                    }
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
                String iduser = getIntent().getStringExtra("IdUsuario");
                //String user = getIntent().getStringExtra("user");
                params.put("PosCarga", carga);
                params.put("IdUsuario",iduser);
                params.put("SucursalId",data.getIdEstacion());
                return params;
            }
        };
        // Añade la peticion a la cola
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(eventoReq);
    }
}