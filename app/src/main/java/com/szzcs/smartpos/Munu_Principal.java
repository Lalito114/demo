package com.szzcs.smartpos;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.szzcs.smartpos.Cortes2.Clave;
import com.szzcs.smartpos.Cortes2.FajillasBilletes;
import com.szzcs.smartpos.Cortes2.TotalProductos;
import com.szzcs.smartpos.Encriptacion.EncriptarMAC;
import com.szzcs.smartpos.Encriptacion.EncriptarObtenerIP;
import com.szzcs.smartpos.Gastos.claveGastos;
import com.szzcs.smartpos.Menus_Laterales.Close.Cerrar_Sesion;
import com.szzcs.smartpos.Pendientes.claveUPendientes;
import com.szzcs.smartpos.Productos.posicionProductos;
import com.szzcs.smartpos.Ticket.Definicion_Tarjeta;
import com.szzcs.smartpos.Ticket.tipo_ticket;
import com.szzcs.smartpos.Ticket.ventas;
import com.szzcs.smartpos.configuracion.SQLiteBD;
import com.zcs.sdk.card.CardReaderTypeEnum;

import org.json.JSONException;
import org.json.JSONObject;

import static com.zcs.sdk.card.CardReaderTypeEnum.MAG_CARD;

public class Munu_Principal extends AppCompatActivity {

    ListView list;

    String[] maintitle ={
            "Tickets","Monederos Electronicos",
            "Productos","Cortes",
            "Pendientes", "Gasto","Facturas",
    };

    String[] subtitle ={
            "Emite tickets de venta","Registro, Acumular y Redimir",
            "Administra productos","Realiza cortes de turnos",
            "Enlista pendientes", "Reporta lo Egresos","Imprime tus Tickets",
    };

    Integer[] imgid={
            R.drawable.ventas,R.drawable.monedero,
            R.drawable.product,R.drawable.cortes,
            R.drawable.pendientes,R.drawable.gastos,R.drawable.fact,
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_munu__principal);
        SQLiteBD data = new SQLiteBD(getApplicationContext());
        this.setTitle(data.getNombreEsatcion());

        //Toolbar toolbar = (Toolbar)findViewById(R.id.id);
//aplica color
        //toolbar.setBackgroundColor(Color.parseColor("#00FF00"));

        EncriptarMAC mac = new EncriptarMAC();
        String mac2 = mac.getMacAddr();
        String macmd5 = mac.getMD5(mac2);


            MyListAdapter adapter=new MyListAdapter(this, maintitle, subtitle,imgid);
            list= findViewById(R.id.list);
            list.setAdapter(adapter);


        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                // TODO Auto-generated method stub
                if(position == 0) {
                    //code specific to first list item
                   // Toast.makeText(getApplicationContext(),"Place Your First Option Code",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent( getApplicationContext(), tipo_ticket.class);
                    startActivity(intent);
                }

                else if(position == 1) {
                    CardReaderTypeEnum cardType = MAG_CARD;
                    CardFragment cf = new CardFragment();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("card_type", cardType);
                    cf.setArguments(bundle);
                    getFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.menu, cf).
                            commit();
                }
                else if(position == 2) {
                    Intent intent = new Intent(getApplicationContext(), posicionProductos.class);
                    startActivity(intent);
                }
                else if(position == 3) {
                    Intent intent = new Intent(getApplicationContext(), FajillasBilletes.class);
                    startActivity(intent);
                }
                else if(position == 4) {
                    Intent inten = new Intent(getApplicationContext(), claveUPendientes.class);
                    startActivity(inten);

                }else if(position == 5) {
                    Intent intent = new Intent (getApplicationContext(), claveGastos.class);
                    startActivity(intent);
                }
                else if(position == 6) {
                    Intent intent = new Intent(getApplicationContext(), Definicion_Tarjeta.class);
                    startActivity(intent);
                }

            }
        });




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_update:
                //metodoAdd()
                Toast.makeText(this, "configracion", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_graficos:
                //metodoSearch()
                Toast.makeText(this, "Graficos", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_sesion:
                Intent intent = new Intent(getApplicationContext(), Cerrar_Sesion.class);
                startActivity(intent);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void CerrarSesion() {
        try{
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Cerrar Sesion");
            builder.setMessage("Deseas restaurar los datos de la Aplicación");
            builder.setPositiveButton("Cerrar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
//                    ValidarUsuario();
                }
            });
            AlertDialog dialog= builder.create();
            dialog.show();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

//    private void ValidarUsuario() {
//        SQLiteBD data = new SQLiteBD(getApplicationContext());
//        String url = "http://"+data.getIpEstacion()+"/CorpogasService/api/SucursalEmpleados/clave/"+pass;
//
//        // Utilizamos el metodo Post para validar la contraseña
//        StringRequest eventoReq = new StringRequest(Request.Method.GET,url,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        try {
//                            //Se instancia la respuesta del json
//                            JSONObject validar = new JSONObject(response);
//                            String valido = validar.getString("Activo");
//                            String iduser = validar.getString("RolId");
////                            obteneridusuario(idusuario);
//                            if (valido == "true"){
//                                imprimirticket();
//                            }else{
//                                //Si no es valido se envia mensaje
//                                Toast.makeText(getApplicationContext(),"La contraseña es incorecta",Toast.LENGTH_SHORT).show();
//                            }
//                        } catch (JSONException e) {
//                            //herramienta  para diagnostico de excepciones
//                            e.printStackTrace();
//                        }
//                    }
//                    //funcion para capturar errores
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        // Añade la peticion a la cola
//        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
//        requestQueue.add(eventoReq);
//    }


}