package com.szzcs.smartpos;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class ventas extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ventas);


        PosicionesCargar();
    }
    public void PosicionesCargar(){
        String url = "http://10.0.1.20/TransferenciaDatosAPI/api/PosCarga/GetMax";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                //Obtenemos el linear layout donde colocar los botones
                LinearLayout llBotonera = (LinearLayout) findViewById(R.id.posicionCarga);

                //Creamos las propiedades de layout que tendrán los botones.
                //Son LinearLayout.LayoutParams porque los botones van a estar en un LinearLayout.
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT );

                //Creamos los botones en bucle
                for (int i=1; i<=Integer.parseInt(response); i++){
                    Button button = new Button(getApplicationContext());
                    //Asignamos propiedades de layout al boton
                    button.setLayoutParams(lp);
                    //Asignamos Texto al botón
                    button.setText("" + i);

                    //Asignamose el Listener
                    button.setOnClickListener(new ButtonsOnClickListener(this));
                    //Añadimos el botón a la botonera
                    llBotonera.addView(button);

                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
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
    class ButtonsOnClickListener implements View.OnClickListener
    {
        public ButtonsOnClickListener(Response.Listener<String> stringListener) {

        }

        @Override
        public void onClick(View v)
        {
            Button b = (Button) v;
            Intent intente = new Intent(getApplicationContext(), claveUsuario.class);
            intente.putExtra("pos",b.getText());
            startActivity(intente);
        }

    }



}
