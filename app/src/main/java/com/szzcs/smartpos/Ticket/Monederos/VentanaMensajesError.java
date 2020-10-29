package com.szzcs.smartpos.Ticket.Monederos;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.view.View;

import com.szzcs.smartpos.Munu_Principal;
import com.szzcs.smartpos.R;

import org.json.JSONException;
import org.json.JSONObject;

public class VentanaMensajesError extends View {
    public VentanaMensajesError(Context context) {
        super(context);
    }

    public void mostrarVentana(String error){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(Html.fromHtml("<font color='#000000'>Alerta</font>"));
        builder.setMessage(Html.fromHtml("<font color='#000000'>"+error+"</font>"));
        builder.setIcon(R.drawable.alerta);
        builder.setCancelable(false);
        builder.setPositiveButton(Html.fromHtml("<font color='#000000'>Cerrar</font>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog dialog= builder.create();
        dialog.show();
    }
}
