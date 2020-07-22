package com.szzcs.smartpos.Productos;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.vision.barcode.Barcode;
import com.szzcs.smartpos.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import devliving.online.mvbarcodereader.MVBarcodeScanner;

public class VentaProductos extends AppCompatActivity {
    Button btnAgregar,btnEnviar;
    EditText cantidadProducto;
    EditText Producto;

    String cantidad;
    JSONObject mjason = new JSONObject();
    ListView list;
    Button incrementar;
    Button decrementar;

    ImageButton scanner;

    private static final int CODIGO_PERMISOS_CAMARA = 1, CODIGO_INTENT = 2;
    private boolean permisoCamaraConcedido = false, permisoSolicitadoDesdeBoton = false;
    private TextView tvCodigoLeido;

    private MVBarcodeScanner.ScanningMode modo_Escaneo;

    private int CODE_SCAN = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venta_productos);


        MostrarProductos();

        scanner = findViewById(R.id.scanner);
        scanner.setOnClickListener((View.OnClickListener) this);
        scanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modo_Escaneo = MVBarcodeScanner.ScanningMode.SINGLE_AUTO;

                new MVBarcodeScanner.Builder().setScanningMode(modo_Escaneo).setFormats(Barcode.ALL_FORMATS)
                        .build()
                        .launchScanner((Activity) getApplicationContext(), CODE_SCAN);


            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CODE_SCAN) {
            if (resultCode == RESULT_OK && data != null
                    && data.getExtras() != null) {

                if (data.getExtras().containsKey(MVBarcodeScanner.BarcodeObject)) {
                    Barcode mBarcode = data.getParcelableExtra(MVBarcodeScanner.BarcodeObject);
                    Producto = findViewById(R.id.Producto);
                    Producto.setText(mBarcode.rawValue);
                } else if (data.getExtras().containsKey(MVBarcodeScanner.BarcodeObjects)) {
                    List<Barcode> mBarcodes = data.getParcelableArrayListExtra(MVBarcodeScanner.BarcodeObjects);
                    StringBuilder s = new StringBuilder();
                    for (Barcode b:mBarcodes){
                        s.append(b.rawValue + "\n");
                    }
                    Producto.setText(s.toString());
                }
            }
        }
    }


    private void MostrarProductos() {
        String url = "http://10.0.1.20/TransferenciaDatosAPI/api/catarticulos/getall";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mostarProductor(response);
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


    private void mostarProductor(final String response) {

        List<String> ID;
        ID = new ArrayList<String>();

        List<String> NombreProducto;
        NombreProducto = new ArrayList<String>();

        List<String> PrecioProducto;
        PrecioProducto = new ArrayList<>();

        try {
            JSONArray productos = new JSONArray(response);
            for (int i = 0; i < productos.length(); i++) {
                JSONObject p1 = productos.getJSONObject(i);

                String idArticulo = p1.getString("IdArticulo");
                String DesLarga = p1.getString("DescLarga");
                String precio = p1.getString("Precio");
                NombreProducto.add("ID: " + idArticulo + "    |     $" + precio);
                ID.add(DesLarga);
                PrecioProducto.add(precio);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final ListAdapterProductos adapterProductos = new ListAdapterProductos(this, ID, NombreProducto);
        list = (ListView) findViewById(R.id.list);
        list.setAdapter(adapterProductos);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
               // Toast.makeText(getApplicationContext(), "Seleccionado " + i, Toast.LENGTH_LONG).show();
                int posicion = i +1;
                String posi = String.valueOf(posicion);


            }
        });




    }

}
