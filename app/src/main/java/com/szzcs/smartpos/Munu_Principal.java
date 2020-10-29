package com.szzcs.smartpos;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.szzcs.smartpos.Cortes2.Clave;
import com.szzcs.smartpos.EmpleadoHuellas.capturaEmpleadoHuella;
import com.szzcs.smartpos.Encriptacion.EncriptarMAC;
import com.szzcs.smartpos.Facturas.ObtenerToken;
import com.szzcs.smartpos.FinalizaVenta.ventash;
import com.szzcs.smartpos.Gastos.claveGastos;
import com.szzcs.smartpos.Menus_Laterales.Close.Cerrar_Sesion;
import com.szzcs.smartpos.Pendientes.claveUPendientes;
import com.szzcs.smartpos.Productos.posicionProductos;
import com.szzcs.smartpos.Ticket.Monederos.despachdorclave;
import com.szzcs.smartpos.Ticket.tipo_ticket;
import com.szzcs.smartpos.configuracion.SQLiteBD;
import com.zcs.sdk.card.CardReaderTypeEnum;


import static com.zcs.sdk.card.CardReaderTypeEnum.MAG_CARD;

public class Munu_Principal extends AppCompatActivity {

    ListView list;

    String[] maintitle = {
            "Ventas", "Tickets", "Monederos Electronicos",
            "Productos", "Cortes",
            "Pendientes", "Gasto", "Facturas", "Captura Huellas",
    };

    String[] subtitle = {
            "Procesos Venta", "Emite tickets de venta", "Registro, Acumular y Redimir",
            "Administra productos", "Realiza cortes de turnos",
            "Enlista pendientes", "Reporta lo Egresos", "Imprime tus Tickets",
            "Captura Huellas Empleados",
    };

    Integer[] imgid = {
            R.drawable.ventastotal, R.drawable.ventas, R.drawable.monedero,
            R.drawable.product, R.drawable.cortes,
            R.drawable.pendientes, R.drawable.gastos, R.drawable.fact, R.drawable.huella
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_munu__principal);
        SQLiteBD data = new SQLiteBD(getApplicationContext());
        this.setTitle(data.getNombreEsatcion());

        EncriptarMAC mac = new EncriptarMAC();
        String mac2 = mac.getMacAddr();
        String macmd5 = mac.getMD5(mac2);


        MyListAdapter adapter = new MyListAdapter(this, maintitle, subtitle, imgid);
        list = findViewById(R.id.list);
        list.setAdapter(adapter);


        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                if (position == 0) {
                    //code specific to first list item
                    Intent intent = new Intent(getApplicationContext(), ventash.class);
                    startActivity(intent);
                } else if (position == 1) {
                    Intent intent = new Intent(getApplicationContext(), despachdorclave.class);
                    startActivity(intent);
                } else if (position == 2) {
                    CardReaderTypeEnum cardType = MAG_CARD;
                    CardFragment cf = new CardFragment();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("card_type", cardType);
                    cf.setArguments(bundle);
                    getFragmentManager().beginTransaction().replace(R.id.menu, cf).commit();
                } else if (position == 3) {
                    Intent intent = new Intent(getApplicationContext(), posicionProductos.class);
                    startActivity(intent);
                } else if (position == 4) {
                    Intent intent = new Intent(getApplicationContext(), Clave.class);
                    startActivity(intent);
                } else if (position == 5) {
                    Intent inten = new Intent(getApplicationContext(), claveUPendientes.class);
                    startActivity(inten);

                } else if (position == 6) {
                    Intent intent = new Intent(getApplicationContext(), claveGastos.class);
                    startActivity(intent);
                } else if (position == 7) {
                    Intent intent = new Intent(getApplicationContext(), ObtenerToken.class);
                    startActivity(intent);
                } else if (position == 8) {
                    Intent intent = new Intent(getApplicationContext(), capturaEmpleadoHuella.class);
                    startActivity(intent);
                }
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_update:
                //metodoAdd()
                Toast.makeText(this, "configracion", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_graficos:
                //metodoSearch()
                Toast.makeText(this, "Graficos", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_sesion:
                CerrarSesion();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void CerrarSesion() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Cerrar Sesion");
            builder.setCancelable(false);
            builder.setMessage("Deseas restaurar los datos de la Aplicación");
            builder.setPositiveButton("Cerrar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(getApplicationContext(), Cerrar_Sesion.class);
                    startActivity(intent);
                    finish();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}