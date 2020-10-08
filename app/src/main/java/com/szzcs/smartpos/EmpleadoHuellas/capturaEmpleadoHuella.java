package com.szzcs.smartpos.EmpleadoHuellas;

import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.szzcs.smartpos.FingerprintActivity;
import com.szzcs.smartpos.MyApp;
import com.szzcs.smartpos.Productos.ListAdapterProductos;
import com.szzcs.smartpos.R;
import com.szzcs.smartpos.base.BaseActivity;
import com.szzcs.smartpos.configuracion.SQLiteBD;
import com.zcs.sdk.SdkResult;
import com.zcs.sdk.fingerprint.FingerprintListener;
import com.zcs.sdk.fingerprint.FingerprintManager;
import com.zcs.sdk.fingerprint.Result;
import com.zcs.sdk.util.StringUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class capturaEmpleadoHuella extends BaseActivity implements FingerprintListener, View.OnClickListener {
    private static final String TAG = "capturaEmpleadoHuella";
    Button bt_iso;
    ImageView mIvResult;
    ListView list;
    TextView txtEmpleado, txtIdEmpleado;
    String EstacionId, sucursalId, ipEstacion, islaId ;
    protected TextView mTextStatus;
    protected TextView mEtFingerId;

    private String files = "/sdcard/";
    private long lastClick = 0;
    private Handler mHandler;
    private FingerprintManager mFingerprintManager;
    private int mFingerId = 0;
    private int mTimeout = 3;
    private byte[] featureTmp;
    private byte[] isoFeatureTmp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_captura_empleado_huella);

        SQLiteBD db = new SQLiteBD(getApplicationContext());
        EstacionId = db.getIdEstacion();
        sucursalId=db.getIdSucursal();
        ipEstacion = db.getIpEstacion();

        mEtFingerId = findViewById(R.id.txtidempleado);
        txtEmpleado = findViewById(R.id.txtempleado);
        txtIdEmpleado = findViewById(R.id.txtidempleado);
        mIvResult = (ImageView) findViewById(R.id.iv_result);
        bt_iso = findViewById(R.id.bt_iso);
        bt_iso.setOnClickListener(capturaEmpleadoHuella.this);


        CargaEmpleados();
        initFinger();
        mHandler = new Handler(Looper.getMainLooper());
    }

    private void initFinger() {
        mFingerprintManager = MyApp.sDriverManager.getFingerprintManager();
        mFingerprintManager.addFignerprintListener(this);
        mFingerprintManager.init();
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
        boolean banderaI;

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

                banderaI = false;
                switch (IdentificadorEmpleado){
                    case "1": //Gerente
                        banderaI= true;
                        break;
                    case "3": //Jefe de Isla
                         banderaI= true;
                         break;
                    //case "4": //Vendedor
                    //    banderaI= true;
                    //    break;
                    case "5": //Jefe de Piso
                        banderaI= true;
                        break;
                }
                if (banderaI == true) {
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
                txtEmpleado.setText(Descripcion);
                txtIdEmpleado.setText(clave);
            }
        });
    }

    private boolean clickCheck() {
        if (System.currentTimeMillis() - lastClick <= 3000) {
            return false;
        }
        lastClick = System.currentTimeMillis();
        return true;
    }

    StringBuffer _msg = new StringBuffer();
    private int _lines = 20;

    private void showLog(final String msg) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Log.e(TAG, msg);
                Date date = new Date();
                DateFormat dateFormat = new SimpleDateFormat("MM/dd HH:mm:ss");
                _msg.append(dateFormat.format(date)).append(":");
                _msg.append(msg);
                String text = mTextStatus.getText().toString();
                if (!TextUtils.isEmpty(text)) {
                    String str[] = text.split("\r\n");
                    for (int i = 0; i < _lines && i < str.length; i++) {
                        _msg.append("\r\n");
                        _msg.append(str[i]);
                    }
                }
                mTextStatus.setText(_msg.toString());
                _msg.setLength(0);
            }

        });
    }



    @Override
    public void onClick(View view) {
        if (!clickCheck()) {
            showLog("Click too quicly");
            return;
        }
        mIvResult.setVisibility(View.GONE);
        mTextStatus.setText("");

        String fingerText = mEtFingerId.getText().toString().trim();
        mFingerId = Integer.parseInt(TextUtils.isEmpty(fingerText) ? "0" : fingerText);
        Log.e(TAG, "FingerId: " + Integer.toHexString(mFingerId));
        if (view.getId() == R.id.bt_iso) {
            mFingerprintManager.captureAndGetISOFeature();
        }
    }

    @Override
    public void onEnrollmentProgress(int fingerId, int remaining, int reason) {
        if (reason == 0 && remaining == 0) {
            showLog("Fingerprint ID:" + fingerId + "  Enrollment success!");
        } else {
            showLog("Fingerprint ID:" + fingerId);
            showLog("remaining times:" + remaining);
            showLog("reason:" + reason);
        }
    }

    @Override
    public void onAuthenticationFailed(int i) {
        showLog("Autenticación de huella fallido: " + i);
    }

    @Override
    public void onAuthenticationSucceeded(int fingerId, Object obj) {
        showLog("Autenticación de huella exitoso:  fingerId = " + fingerId + "  score = " + obj);
    }

    @Override
    public void onGetImageComplete(int i, byte[] bytes) {

    }

    @Override
    public void onGetImageFeature(int i, byte[] bytes) {

    }

    @Override
    public void onGetImageISOFeature(int result, byte[] feature) {
        showLog("onGetImageISOFeature: ret =  " + result + (result == SdkResult.SDK_OK ? "\tISO feature = " + StringUtils.convertBytesToHex(feature) : null));
        if (result == SdkResult.SDK_OK) {
            isoFeatureTmp = feature;
        }
    }
}