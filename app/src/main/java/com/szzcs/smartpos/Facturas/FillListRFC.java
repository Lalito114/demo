package com.szzcs.smartpos.Facturas;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.szzcs.smartpos.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FillListRFC extends AppCompatActivity {

    private ListView listView;
    private AdaptadorListRFC adaptadorListRFC;
    TextView rfc, email, idCliente, idAlias;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_rfc);

        Bundle bundle = this.getIntent().getExtras();
        token = bundle.getString("Token");

        listView = findViewById(R.id.listView);
        adaptadorListRFC = new AdaptadorListRFC(this, GetArrayItems());
        listView.setAdapter(adaptadorListRFC);

        ListView lview = findViewById(R.id.listView);
        lview.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                rfc = findViewById(R.id.txtRFC);
                email = findViewById(R.id.txtEmail);
                idCliente = findViewById(R.id.txtIdCliente);
                idAlias = findViewById(R.id.txtIdAlias);

                final String sRFC = rfc.getText().toString();
                final String sEmail = email.getText().toString();
                final String sIdCliente = idCliente.getText().toString();
                final String sIdAlias = idAlias.getText().toString();

                Intent intent = new Intent(FillListRFC.this, CantidadTickets.class);
                intent.putExtra("RFC", sRFC);
                intent.putExtra("Email", sEmail);
                intent.putExtra("IdCliente", sIdCliente);
                intent.putExtra("IdAlias", sIdAlias);
                intent.putExtra("Token", token);
                startActivity(intent);

            }
        });

    }

    private ArrayList<EntidadListRFC> GetArrayItems() {

        Bundle bundle = this.getIntent().getExtras();
        ArrayList<String> razonSocial = bundle.getStringArrayList("RazonSocial");
        ArrayList<String> RFC = bundle.getStringArrayList("RFC");
        ArrayList<String> email = bundle.getStringArrayList("Email");
        ArrayList<String> idCliente = bundle.getStringArrayList("IdCliente");
        ArrayList<String> idAlias = bundle.getStringArrayList("IdAlias");

        ArrayList<EntidadListRFC> listItems = new ArrayList<>();

        for(int i = 0; i < razonSocial.size(); i++){

            listItems.add( new EntidadListRFC(razonSocial.get(i), RFC.get(i),email.get(i),idCliente.get(i),idAlias.get(i)));

        }

        return listItems;

    }
}

