package com.szzcs.smartpos.EmpleadoHuellas;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.szzcs.smartpos.MyApp;
import com.szzcs.smartpos.Productos.claveProducto;
import com.szzcs.smartpos.R;
import com.szzcs.smartpos.base.BaseActivity;
import com.zcs.sdk.fingerprint.FingerprintListener;
import com.zcs.sdk.fingerprint.FingerprintManager;
import com.zcs.sdk.util.StringUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class validaHuella  extends BaseActivity implements FingerprintListener, View.OnClickListener {
    private static final String TAG = "FingerprintActivity";
    private String huellaCapturada;
    private byte[] isoFeatureTmp;
    String IdEmpleado;
    Button btnhuella;
    TextView cantidadproducto;
    private FingerprintManager mFingerprintManager;
    private Handler mHandler;
    TextView textResultado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_valida_huella);
        cantidadproducto= findViewById(R.id.cantidadproducto);


        btnhuella = findViewById(R.id.btnhuella);
        btnhuella.setOnClickListener(validaHuella.this);

        obtieneDatos();
        initFinger();
    }

    private void initFinger() {
        mFingerprintManager = MyApp.sDriverManager.getFingerprintManager();
        mFingerprintManager.addFignerprintListener(this);
        mFingerprintManager.init();
    }

    private void obtieneDatos(){
        huellaCapturada = getIntent().getStringExtra("huellacapturada");
        IdEmpleado = getIntent().getStringExtra("idempleado");

        cantidadproducto.setText(IdEmpleado);
        isoFeatureTmp = StringUtils.convertHexToBytes(huellaCapturada);


    }

    @Override
    public void onClick(View view) {


        if (view.getId() == R.id.btnhuella) {
            //mFingerprintManager.authenticate(3);

            mFingerprintManager.verifyWithISOFeature(isoFeatureTmp);
        }
    }

    @Override
    public void onEnrollmentProgress(int i, int i1, int i2) {

    }

    @Override
    public void onAuthenticationFailed(int i) {

        showLog("Huella no Validada"); // :  Usuario = " + i );
    }

    @Override
    public void onAuthenticationSucceeded(int fingerId, Object obj) {
        showLog("Identificación Correcta :  Usuario = " + fingerId); // + "  score = " + obj);
    }

    @Override
    public void onGetImageComplete(int i, byte[] bytes) {

    }

    @Override
    public void onGetImageFeature(int i, byte[] bytes) {

    }

    @Override
    public void onGetImageISOFeature(int i, byte[] bytes) {

    }

    StringBuffer _msg = new StringBuffer();
    private int _lines = 20;

    private void showLog(final String msg) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                textResultado.setText("");
                Log.e(TAG, msg);
                Date date = new Date();
                DateFormat dateFormat = new SimpleDateFormat("MM/dd HH:mm:ss");
                _msg.append(dateFormat.format(date)).append(":");
                _msg.append(msg);
                String text = textResultado.getText().toString();
                if (!TextUtils.isEmpty(text)) {
                    String str[] = text.split("\r\n");
                    for (int i = 0; i < _lines && i < str.length; i++) {
                        _msg.append("\r\n");
                        _msg.append(str[i]);
                    }
                }
                textResultado.setText(_msg.toString());
                _msg.setLength(0);
            }

        });
    }

}