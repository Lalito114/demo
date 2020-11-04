package com.szzcs.smartpos.Cortes2;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.szzcs.smartpos.Helpers.Modales.Modales;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class DesgloseVales extends AppCompatActivity {

    RecyclerView recyclerView;
    List<CierreDesgloceVales> cierreDesgloceValesListList;
    ArrayList<ValePapelDenominacion> lCarrito;
    List<ValePapelDenominacion> lTiposVales;
    //TanqueLlenoPrueba ltaTanqueLlenoPruebas;
    Button btnGuardar, btnRegresaVales;
    TextView valesImporteTxt;
    RespuestaApi<ValePapelDenominacion> outputList;
    SQLiteBD data;
    String islaId;

    String sumaPicosBilletes;
    String dineroBilletes;
    String dineroMorralla;
    String VentaProductos;
    String cantidadAceites;

    RespuestaApi<Cierre> cierreRespuestaApi;
    long cierreId;
    long turnoId;

    RespuestaApi<AccesoUsuario> accesoUsuario;
    long idusuario;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_desglose_vales);
        recyclerView = findViewById(R.id.recyclerView);
        btnGuardar = (Button) findViewById(R.id.guardarValesDetalles);
        valesImporteTxt = (TextView) findViewById(R.id.totalValesDetalle);
        btnRegresaVales =(Button) findViewById(R.id.regresaValesPapel);

        data = new SQLiteBD(getApplicationContext());
        cierreRespuestaApi = (RespuestaApi<Cierre>) getIntent().getSerializableExtra( "lcierreRespuestaApi");
        accesoUsuario = (RespuestaApi<AccesoUsuario>) getIntent().getSerializableExtra("accesoUsuario");
        lCarrito = (ArrayList<ValePapelDenominacion>) getIntent().getSerializableExtra( "lCarrito");
        islaId = getIntent().getStringExtra("islaId");
        idusuario = accesoUsuario.getObjetoRespuesta().getSucursalEmpleadoId();
        sumaPicosBilletes = getIntent().getStringExtra("sumaPicosBilletes");
        dineroBilletes = getIntent().getStringExtra("dineroBilletes");
        dineroMorralla = getIntent().getStringExtra("dineroMorralla");
        VentaProductos = getIntent().getStringExtra("VentaProductos");
        cantidadAceites = getIntent().getStringExtra("cantidadAceites");
        turnoId = cierreRespuestaApi.getObjetoRespuesta().getTurnoId();
        cierreId = cierreRespuestaApi.getObjetoRespuesta().getId();

        InitData();
        SetRecyclerView();

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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
                                String respuetas = null;
                                boolean  Correcto = outputList.Correcto;
                                if(Correcto == true){

                                    String mensaje = "Registro de Vales exitoso.";
                                    Modales modales = new Modales(DesgloseVales.this);
                                    View view1 = modales.MostrarDialogoCorrecto(DesgloseVales.this,mensaje);
                                    view1.findViewById(R.id.buttonAction).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Intent intent = new Intent(getApplicationContext(), CierreFormaPago.class);
                                            intent.putExtra("lCarrito", lCarrito);
                                            intent.putExtra("islaId",islaId);
                                            intent.putExtra("sumaPicosBilletes",sumaPicosBilletes);
                                            intent.putExtra("dineroBilletes",dineroBilletes);
                                            intent.putExtra("dineroMorralla",dineroMorralla);
                                            intent.putExtra("VentaProductos", VentaProductos);
                                            intent.putExtra("cantidadAceites", cantidadAceites);
                                            intent.putExtra("lcierreRespuestaApi", cierreRespuestaApi);
                                            intent.putExtra("accesoUsuario", accesoUsuario);
                                            startActivity(intent);
                                            modales.alertDialog.dismiss();
                                        }
                                    });


//                                    respuetas =  "Se registraron con exito los vales";
//                                    Intent intent = new Intent(getApplicationContext(), CierreFormaPago.class);
//                                    intent.putExtra("lCarrito", lCarrito);
//                                    intent.putExtra("islaId",islaId);
//                                    intent.putExtra("sumaPicosBilletes",sumaPicosBilletes);
//                                    intent.putExtra("dineroBilletes",dineroBilletes);
//                                    intent.putExtra("dineroMorralla",dineroMorralla);
//                                    intent.putExtra("VentaProductos", VentaProductos);
//                                    intent.putExtra("cantidadAceites", cantidadAceites);
//                                    intent.putExtra("lcierreRespuestaApi", cierreRespuestaApi);
//                                    intent.putExtra("accesoUsuario", accesoUsuario);
//
//                                    startActivity(intent);
                                }else{
                                    respuetas =  outputList.Mensaje;

                                }
                                Toast.makeText(getApplicationContext(), respuetas, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).start();
            }
        });

        btnRegresaVales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Falta programar el regreso del objeto", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void  httpsJsonPost() throws IOException, ParseException {
//        TanqueLlenoPrueba tanqueLlenoPrueba= new TanqueLlenoPrueba();
//        tanqueLlenoPrueba.EstacionId = 2;
//        tanqueLlenoPrueba.PosicionCarga = 4;
//        tanqueLlenoPrueba.TarjetaCliente = "1234567890123456";
//        String json = new Gson().toJson(tanqueLlenoPrueba);

        String json = new Gson().toJson(lCarrito);
        String postUrl = "http://"+data.getIpEstacion()+"/CorpogasService/api/cierreValePapeles/sucursal/"+data.getIdSucursal()+"/isla/"+islaId+"/usuario/"+idusuario;
//        String postUrl = "http://"+data.getIpEstacion()+"/CorpogasService/api/cierreValePapeles/cierre/sucursalId/"+data.getIdSucursal()+"/usuarioId/"+idusuario+"/islaId/"+islaId;                    //"http://10.0.1.20/CorpogasService/api/cierreValePapeles/cierre/sucursalId/1/usuarioId/1/islaId/1";// put in your url

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
            InputStream in = response.getEntity().getContent(); //Get the data in the entity

            BufferedReader reader = new BufferedReader(new InputStreamReader(in, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) // Read line by line
                sb.append(line + "\n");

            String resString = sb.toString(); // Result is here

            in.close(); // Close the stream
            Gson  json2 = new Gson();
            outputList = json2.fromJson(resString, RespuestaApi.class);
            String pruebas = "";

        }
    }

    private void SetRecyclerView() {
        VersionsAdapter versionsAdapter =  new VersionsAdapter(cierreDesgloceValesListList);
        recyclerView.setAdapter(versionsAdapter);
        recyclerView.setHasFixedSize(true);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor)
    {
        Map<Object, Boolean> map = new ConcurrentHashMap<>();
        return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void InitData() {
            int version = Build.VERSION.SDK_INT;
            int versionpro = Build.VERSION_CODES.N;
            cierreDesgloceValesListList = new ArrayList<>();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                double ImporteValesTotal = 0;

                lTiposVales =  lCarrito.stream().filter(distinctByKey(x -> x.getTipoValePapelId()) ).collect(Collectors.toList());

                for(ValePapelDenominacion item : lTiposVales)
                {
                    Integer NumeroVales = lCarrito.stream().filter(x -> x.getTipoValePapelId() == item.getTipoValePapelId()).mapToInt(ValePapelDenominacion::getCantidad).sum();
                    double ImporteVales = lCarrito.stream().filter(x -> x.getTipoValePapelId() == item.getTipoValePapelId()).mapToDouble(ValePapelDenominacion::getTotal).sum();
                    CierreDesgloceVales cierreDesgloceVales = new CierreDesgloceVales("",item.getNombreVale(), "Numero de vales: "+ NumeroVales,"Importe: $"+ImporteVales);
                    cierreDesgloceValesListList.add(cierreDesgloceVales);
                    ImporteValesTotal += ImporteVales;
                };

                valesImporteTxt.setText("Total: $"+String.valueOf(ImporteValesTotal));
        }
        else
            {
                int numeroValesGasopas = 0;
                int numeroValesEfectivale = 0;
                int numeroValesAcor = 0;
                int numeroValesSiVale = 0;
                double totalValesGasopas = 0;
                double totalValesEfectivale = 0;
                double totalValesAcor = 0;
                double totalValesSiVale = 0;
                double totalPrice = 0.0;
                String nombreGasopas = "";
                String nombreEfectivale = "";
                String nombreAcor = "";
                String nombreSiVale = "";
                    for ( ValePapelDenominacion item : lCarrito) {

                                    totalPrice += item.getTotal();
                                    long tipo = item.getTipoValePapelId();
                                    if(tipo ==1){
                                        nombreGasopas = item.getNombreVale();
                                        numeroValesGasopas += item.getCantidad();
                                        totalValesGasopas += item.getTotal();
                                    }
                                    if(tipo ==2){
                                        nombreEfectivale =  item.getNombreVale();
                                        numeroValesEfectivale += item.getCantidad();
                                        totalValesEfectivale += item.getTotal();
                                    }
                                    if(tipo ==3){
                                        nombreAcor = item.getNombreVale();
                                        numeroValesAcor += item.getCantidad();
                                        totalValesAcor += item.getTotal();
                                    }
                                    if(tipo ==4){
                                        nombreSiVale = item.getNombreVale();
                                        numeroValesSiVale += item.getCantidad();
                                        totalValesSiVale += item.getTotal();
                                    }
                    }
                if(nombreGasopas.equals("GASOPASS")){
                    CierreDesgloceVales cierreDesgloceVales = new CierreDesgloceVales("",nombreGasopas, "Numero de vales: "+ numeroValesGasopas,"Importe: $"+totalValesGasopas);
                    cierreDesgloceValesListList.add(cierreDesgloceVales);
                }
                if(nombreEfectivale.equals("EFECTIVALE")){
                    CierreDesgloceVales cierreDesgloceVales = new CierreDesgloceVales("",nombreEfectivale, "Numero de vales: "+ numeroValesEfectivale,"Importe: $"+totalValesEfectivale);
                    cierreDesgloceValesListList.add(cierreDesgloceVales);
                }
                if(nombreAcor.equals("ACCOR")){
                    CierreDesgloceVales cierreDesgloceVales = new CierreDesgloceVales("",nombreAcor, "Numero de vales: "+ numeroValesAcor,"Importe: $"+totalValesAcor);
                    cierreDesgloceValesListList.add(cierreDesgloceVales);
                }
                if(nombreSiVale.equals("SI VALE ")){
                    CierreDesgloceVales cierreDesgloceVales = new CierreDesgloceVales("",nombreSiVale, "Numero de vales: "+ numeroValesSiVale,"Importe: $"+totalValesSiVale);
                    cierreDesgloceValesListList.add(cierreDesgloceVales);
                }





                valesImporteTxt.setText("Total: $"+String.valueOf(totalPrice));
            }
    }
    //Metodo para regresar a la actividad principal
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), SubOfiGasopass.class);
        intent.putExtra("lCarrito", lCarrito);
        intent.putExtra("islaId",islaId);
        intent.putExtra("sumaPicosBilletes",sumaPicosBilletes);
        intent.putExtra("dineroBilletes",dineroBilletes);
        intent.putExtra("dineroMorralla",dineroMorralla);
        intent.putExtra("VentaProductos", VentaProductos);
        intent.putExtra("cantidadAceites", cantidadAceites);
        intent.putExtra("lcierreRespuestaApi", cierreRespuestaApi);
        intent.putExtra("accesoUsuario", accesoUsuario);
        startActivity(intent);

    }
}