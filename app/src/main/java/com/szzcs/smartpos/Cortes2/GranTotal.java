package com.szzcs.smartpos.Cortes2;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.szzcs.smartpos.Munu_Principal;
import com.szzcs.smartpos.R;
import com.szzcs.smartpos.configuracion.SQLiteBD;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;

public class GranTotal extends AppCompatActivity {

    TextView subFajBilletes, subFajMorralla, subOficina, granTotal, totalVendido, mensaje;
    Button finalizarCierre;
    double total, totalVendido2;
    String dineroBilletes;
    String dineroMorralla;
    String subTotalOficina;
    String VentaProductos;
    String cantidadAceites;
    RespuestaApi<Cierre> cierreRespuestaApi;
    RespuestaApi<Cierre> respuestaApiCierre;
    long cierreId;
    long turnoId;
    Cierre cierre;
    SQLiteBD data;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gran_total);

        subTotalOficina = getIntent().getStringExtra("subTotalOficina");
        dineroBilletes = getIntent().getStringExtra("dineroBilletes");
        dineroMorralla = getIntent().getStringExtra("dineroMorralla");
        VentaProductos = getIntent().getStringExtra("VentaProductos");
        cantidadAceites = getIntent().getStringExtra("cantidadAceites");
        cierreRespuestaApi = (RespuestaApi<Cierre>) getIntent().getSerializableExtra( "lcierreRespuestaApi");
        turnoId = cierreRespuestaApi.getObjetoRespuesta().getTurnoId();
        cierreId = cierreRespuestaApi.getObjetoRespuesta().getId();

       // total = 175630.2648;
       // totalVendido2 = 175630.2653;
        subFajBilletes = (TextView) findViewById(R.id.txtSubtotalFajillaBilletes);
        subFajMorralla = (TextView) findViewById(R.id.txtSubtotalFajillaMoralla);
        subOficina = (TextView) findViewById(R.id.txtSubtotalOficina2);
        granTotal = (TextView) findViewById(R.id.txtGranTotal);

        totalVendido = (TextView) findViewById(R.id.textTotalVendido);
        mensaje = (TextView) findViewById(R.id.textMensaje);

        totalVendido.setText("TOTAL VENDIDO: $"+String.valueOf(totalVendido2));

        finalizarCierre = (Button) findViewById(R.id.btnFinalizarCierre);

        finalizarCierre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                diferenciasCierre();
            }
        });

        double total = Double.parseDouble(dineroBilletes) + Double.parseDouble(dineroMorralla) + Double.parseDouble(subTotalOficina);

        subFajBilletes.setText("SUBTOTAL FAJILLAS DE BILLETES      $"+dineroBilletes);
        subFajMorralla.setText("SUBTOTAL FAJILLAS DE MORRALLA    $"+dineroMorralla);
        subOficina.setText("SUBTOTAL OFICINA -------------  $"+subTotalOficina);
        granTotal.setText("GRAN TOTAL ------------------------  $"+total);
    }

    public void diferenciasCierre(){

//        cierre  = new Cierre();
//        cierre.SucursalId = respuestaApiCierre.getObjetoRespuesta().getSucursalId();


        new Thread(new Runnable() {
            public void run() {

                try {
                    httpsJsonPost();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(),"Corte generado correctamente",Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getApplicationContext(), Munu_Principal.class);
                        startActivity(intent);
                        finish();
                    }
                });
            }
        }).start();

    }



    public void  httpsJsonPost() throws IOException, ParseException {

        String json = new Gson().toJson(cierreRespuestaApi.getObjetoRespuesta());
        String postUrl = "http://10.0.1.20/CorpogasService/api/cierres/completaCierreForzado";                    //"http://10.0.1.20/CorpogasService/api/cierreValePapeles/cierre/sucursalId/1/usuarioId/1/islaId/1";// put in your url

        HttpClient client = new DefaultHttpClient();
        HttpConnectionParams.setConnectionTimeout(client.getParams(), 1000);
        HttpResponse response;
        HttpPost request = new HttpPost(postUrl);
        StringEntity se = new StringEntity(json);
        se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
        request.setEntity(se);
        response = client.execute(request);

        /*Checking response */
        if (response != null) {
            InputStream in = response.getEntity().getContent();

            BufferedReader reader = new BufferedReader(new InputStreamReader(in, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null)
                sb.append(line + "\n");

            String resString = sb.toString();

            in.close(); // Close the stream
            Gson  json2 = new Gson();
            respuestaApiCierre = json2.fromJson(resString, RespuestaApi.class);
            String pruebas = "";

        }
    }

}
