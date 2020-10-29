package com.szzcs.smartpos.Gastos;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.szzcs.smartpos.Munu_Principal;
import com.szzcs.smartpos.PrintFragment;
import com.szzcs.smartpos.PrintFragmentVale;
import com.szzcs.smartpos.Productos.posicionProductos;
import com.szzcs.smartpos.R;
import com.szzcs.smartpos.configuracion.SQLiteBD;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class cargavaleGasto extends AppCompatActivity {
    ListView list;
    TextView txtDescripcion, txtClave, isla, turno, usuario;
    TextView SubTotal, Descripcion;
    String EstacionId, sucursalId, ipEstacion ;
    String idisla, idTurno;
    Bundle args = new Bundle();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cargavale_gasto);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        SQLiteBD db = new SQLiteBD(getApplicationContext());
        EstacionId = db.getIdEstacion();
        sucursalId=db.getIdSucursal();
        ipEstacion = db.getIpEstacion();
        SubTotal =findViewById(R.id.subTot);
        Descripcion = findViewById(R.id.descripcion);

        String islaId = getIntent().getStringExtra("isla");
        String turnoId = getIntent().getStringExtra("turno");
        Button enviar = findViewById(R.id.btnEnviar);
        enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SubTotal.length()==0 )       //.length() >0)
                {
                    Toast.makeText(getApplicationContext(), "digite el importe", Toast.LENGTH_LONG).show();
                } else { if (Descripcion.length()==0){
                        Toast.makeText(getApplicationContext(), "digite la descripción", Toast.LENGTH_LONG).show();
                        }else {
                            EnviarGastos();
                        }
                }
            }
        });
    }
    private void EnviarGastos() {
        final String turnoId = getIntent().getStringExtra("turno");

        String url = "http://"+ipEstacion+"/CorpogasService/api/Turnos/fechaTrabajo/sucursal/"+sucursalId+"/turno/"+turnoId;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject resultadorespuesta = new JSONObject(response);
                    String valor = resultadorespuesta.getString("ObjetoRespuesta");
                    GuardarGasto(valor, turnoId);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),error.toString(), Toast.LENGTH_LONG).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }


private void GuardarGasto(String fechatrabajo, String turnoId){

    String islaId = getIntent().getStringExtra("isla");
    Date date = new Date();
    String fechaTrabajo= fechatrabajo;

    String empleadoId = getIntent().getStringExtra("empleadoid");
    final String empleado = getIntent().getStringExtra("empleado");

    //String URL = "http://"+data.getIpEstacion()+"/CorpogasService/api/tanqueLleno/EnviarProductos";
    String URL = "http://"+ipEstacion+"/CorpogasService/api/CajaChicas";
    final JSONObject mjason = new JSONObject();
    RequestQueue queue = Volley.newRequestQueue(this);
    try {
        mjason.put("EstacionId",EstacionId);
        mjason.put("TurnoId", turnoId); //turno.getText().toString());
        mjason.put("TurnoSucursalId",sucursalId); //turno.getText().toString());Sucursal
        mjason.put("IslaId", islaId);
        mjason.put("IslaEstacionId",EstacionId);
        mjason.put("FechaTrabajo",fechaTrabajo);
        mjason.put("Descripcion", Descripcion.getText().toString());
        mjason.put("Importe", SubTotal.getText().toString());
        mjason.put("SucursalEmpleadoId", empleadoId);
        mjason.put("SucursalEmpleadoSucursalId", sucursalId);
    } catch (JSONException e) {
        e.printStackTrace();
    }
    JsonObjectRequest request_json = new JsonObjectRequest(Request.Method.POST, URL, mjason, new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
            Toast.makeText(getApplicationContext(),"Gasto Cargado Exitosamente",Toast.LENGTH_LONG).show();
            String numeroticket = null;
            try {
                numeroticket = response.getString("Id");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ObtenerCuerpoTicket(SubTotal.getText().toString(), Descripcion.getText().toString(), numeroticket, empleado);
            //Intent intente = new Intent(getApplicationContext(), Munu_Principal.class);
            //startActivity(intente);
        }
    }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Toast.makeText(getApplicationContext(),error.toString(), Toast.LENGTH_LONG).show();
        }
    }){
        public Map<String,String> getHeaders() throws AuthFailureError {
            Map<String,String> headers = new HashMap<String, String>();
            return headers;
        }
        protected  Response<JSONObject> parseNetwokResponse(NetworkResponse response){
            if (response != null){

                try {
                    String responseString;
                    JSONObject datos = new JSONObject();
                    responseString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            return Response.success(mjason, HttpHeaderParser.parseCacheHeaders(response));
        }
    };
    queue.add(request_json);

}

    public void ObtenerCuerpoTicket(String total, String descripcion, String numeroticket, String nombreautorizo) {
        try{
            args.putString("proviene", "2");
            args.putString("descripcion", descripcion);
            args.putString("subtotal", "0");
            args.putString("iva", "0");
            args.putString("total", total);
            args.putString("tipogasto", "");
            args.putString("numeroticket", numeroticket);
            args.putString("nombreautorizo", nombreautorizo);
            PrintFragmentVale cf = new PrintFragmentVale();
            cf.setArguments(args);
            getFragmentManager().beginTransaction().replace(R.id.tv1, cf).
                    addToBackStack(PrintFragment.class.getName()).
                    commit();
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    //Metodo para regresar a la actividad principal
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), Munu_Principal.class);
        startActivity(intent);
        finish();
    }


}
