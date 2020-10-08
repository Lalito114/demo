package com.szzcs.smartpos.Gastos;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.szzcs.smartpos.Productos.ListAdapterProductos;
import com.szzcs.smartpos.R;
import com.szzcs.smartpos.configuracion.SQLiteBD;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class autizacionGastos extends AppCompatActivity {
    Button enviar;
    ImageView Autoriza;
    ListView list;
    TextView txtusuario, txtclaveusuario;
    String EstacionId, sucursalId, ipEstacion, islaId ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autizacion_gastos);

        SQLiteBD db = new SQLiteBD(getApplicationContext());
        EstacionId = db.getIdEstacion();
        sucursalId=db.getIdSucursal();
        ipEstacion = db.getIpEstacion();

        txtusuario = findViewById(R.id.txtempleado);
        txtclaveusuario= findViewById(R.id.txtclaveempleado);

        final String proviene = getIntent().getStringExtra("tipoGasto");
        final String islaId = getIntent().getStringExtra("isla");
        final String turnoId = getIntent().getStringExtra("turno");


        enviar=findViewById(R.id.enviar);
        enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (txtusuario.length() >0) {
                    if (proviene.equals("1")) {
                        Intent intent = new Intent(getApplicationContext(), cargaGasto.class);
                        intent.putExtra("isla", islaId);
                        intent.putExtra("turno", turnoId);
                        intent.putExtra("empleadoid", txtclaveusuario.getText().toString());
                        intent.putExtra("empleado", txtusuario.getText().toString());
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(getApplicationContext(), cargavaleGasto.class);
                        intent.putExtra("isla", islaId);
                        intent.putExtra("turno", turnoId);
                        intent.putExtra("empleadoid", txtclaveusuario.getText().toString());
                        intent.putExtra("empleado", txtusuario.getText().toString());
                        startActivity(intent);
                    }
                }else{
                    Toast.makeText(getApplicationContext(),"Seleccione un empleado de la lista", Toast.LENGTH_LONG).show();
                }
            }
        });
        CargaEmpleados();
    }

    private void CargaEmpleados() {
        String url = "http://"+ipEstacion+"/CorpogasService/api/SucursalEmpleados/sucursal/"+sucursalId;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mostrarEmpleados(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),error.toString(), Toast.LENGTH_LONG).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this.getApplicationContext());
        requestQueue.add(stringRequest);
    }


    private void mostrarEmpleados(String response) {
        //Declaracion de variables
        String IdentificadorEmpleado;
        final List<String> ID;
        ID = new ArrayList<String>();

        final List<String> NombreUsuario;
        NombreUsuario = new ArrayList<String>();


        try {

            JSONArray productos = new JSONArray(response);
            for (int i = 0; i <productos.length() ; i++) {

                JSONObject p1 = productos.getJSONObject(i);
                IdentificadorEmpleado = p1.getString("RolId");
                if (IdentificadorEmpleado.equals("1")) {
                    String idEmpleado = p1.getString("Id");
                    String DesLarga = p1.getString("Nombre") + " " + p1.getString("ApellidoPaterno") + " " + p1.getString("ApellidoMaterno");
                    NombreUsuario.add("" + idEmpleado);
                    ID.add(DesLarga);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final ListAdapterProductos adapterP = new ListAdapterProductos(this,  ID, NombreUsuario);
        list=(ListView)findViewById(R.id.list);
        list.setTextFilterEnabled(true);
        list.setAdapter(adapterP);
//        Agregado  click en la lista
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String  Descripcion = ID.get(i).toString();
                String  clave = NombreUsuario.get(i).toString();
                txtusuario.setText(Descripcion);
                txtclaveusuario.setText(clave);
            }
        });
    }
}
