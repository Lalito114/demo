package com.szzcs.smartpos.Puntada.Acumular;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.szzcs.smartpos.R;
import com.szzcs.smartpos.configuracion.SQLiteBD;

public class ClaveDespachadorAcumular extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clave_despachador_acumular);
        SQLiteBD data = new SQLiteBD(getApplicationContext());
        this.setTitle(data.getNombreEsatcion());
        Bundle bundle = getIntent().getExtras();
        final String track = bundle.getString("track");
        final String posicioncarga = bundle.getString("pos");

        Button btnsiguiente;
        btnsiguiente = findViewById(R.id.btnSiguientePuntada);

        btnsiguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText password;
                password = findViewById(R.id.edtDespachadorclave);
                String password2 = password.getText().toString();

                if (password2.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Ingresa la Contraseña", Toast.LENGTH_LONG).show();
                }else{
                    Intent intent = new Intent(getApplicationContext(),productos.class);
                    Bundle bundle1  = new Bundle();
                    bundle1.putString("track",track);
                    bundle1.putString("pos",posicioncarga);
                    bundle1.putString("PasswordDespachador",password2);
                    intent.putExtras(bundle1);
                    startActivity(intent);
                    finish();
                }

            }
        });
    }
}
