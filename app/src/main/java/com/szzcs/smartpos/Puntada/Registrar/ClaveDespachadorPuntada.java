package com.szzcs.smartpos.Puntada.Registrar;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.szzcs.smartpos.R;

public class ClaveDespachadorPuntada extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clave_despachador_puntada);
        Bundle bundle = getIntent().getExtras();
        final String track2 = bundle.getString("track");

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
                   Intent intent = new Intent(getApplicationContext(),ClaveRegistrarPuntada.class);
                    Bundle bundle1  = new Bundle();
                    bundle1.putString("track",track2);
                    bundle1.putString("PasswordDespachador",password2);
                    intent.putExtras(bundle1);
                   startActivity(intent);
                }

            }
        });

    }
}
