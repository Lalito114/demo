package com.szzcs.smartpos.Menus_Laterales.Close;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.szzcs.smartpos.R;
import com.szzcs.smartpos.configuracion.ConfiguracionServidor;
import com.szzcs.smartpos.configuracion.SQLiteBD;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class Cerrar_Sesion extends AppCompatActivity {
    EditText claveDespachador;
    String clave;
    Button enviarclave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cerrar__sesion);
// ------------------------- Nombre de la base de datos del Toolbar------
        final SQLiteBD data = new SQLiteBD(Cerrar_Sesion.this);
        this.setTitle(data.getNombreEsatcion());

//        --------------------Enlazamos los componentes del XML con la clase Java-----------------------------
        claveDespachador = findViewById(R.id.edtClave);
        enviarclave = findViewById(R.id.btnEnviarCerrar);

//        ---------------------Validamos que la contraseña no este vacia, si es asi, pedimos que por favor sea ingresada---------------

        enviarclave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clave = claveDespachador.getText().toString();
                if (clave.isEmpty()){
//      ----------------------Pedimos que ingresen la contraseña----------------------
                    try{
                        AlertDialog.Builder builder = new AlertDialog.Builder(Cerrar_Sesion.this);
                        builder.setTitle("Alerta");
                        builder.setMessage("Por favor, ingresa la contraseña para restablecer la aplicación");
                        builder.setPositiveButton("Cerrar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                        AlertDialog dialog= builder.create();
                        dialog.show();

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else{
//            ----------------- si el campo de password esta lleno, verificamos el ranfo que tiene el usurio y si este tiene------------------------------
//                                                      acceso y puede restablecer la aplicacion
                    String url = "http://"+data.getIpEstacion()+"/CorpogasService/api/SucursalEmpleados/clave/"+clave;

                    // Utilizamos el metodo Post para validar la contraseña
                    StringRequest eventoReq = new StringRequest(Request.Method.GET,url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                //Se instancia la respuesta del json
                                JSONObject validar = new JSONObject(response);
                                String valido = validar.getString("Activo");
                                String rolId = validar.getString("RolId");
//
                                if (valido.equals("true") && rolId.equals("1")){
                                    borrardatosTarjetero();
                                }else{
                                    AlertDialog.Builder builder = new AlertDialog.Builder(Cerrar_Sesion.this);
                                    builder.setTitle("Alerta");
                                    builder.setMessage("La contraseña ingresada es incorrecta o NO tienes permisos para restabler la configuracion");
                                    builder.setPositiveButton("Cerrar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            claveDespachador.setText("");
                                            dialogInterface.dismiss();

                                        }
                                    });
                                    AlertDialog dialog= builder.create();
                                    dialog.show();

                                }
                            } catch (JSONException e) {
                                //herramienta  para diagnostico de excepciones
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
            }
        });
    }

    private void borrardatosTarjetero() {
        //String myPath = DB_PATH + DB_NAME;
        SQLiteDatabase.deleteDatabase(new File("/data/data/com.szzcs.smartpos/databases/ConfiguracionEstacion.db"));
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
