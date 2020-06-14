package com.szzcs.smartpos;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.zcs.sdk.card.CardReaderTypeEnum;

import static com.zcs.sdk.card.CardReaderTypeEnum.MAG_CARD;
import static com.zcs.sdk.card.CardReaderTypeEnum.MAG_IC_RF_CARD;

public class Munu_Principal extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_munu__principal);

        ImageButton btnventa = (ImageButton) findViewById(R.id.ventas);
        btnventa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( getApplicationContext(), ventas.class);
                startActivity(intent);
            }
        });

        ImageButton btnPuntada = (ImageButton)findViewById(R.id.puntada);
        btnPuntada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(),leerTargeta.class);
//                startActivity(intent);
                CardReaderTypeEnum cardType = MAG_CARD;
                CardFragment cf = new CardFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("card_type", cardType);
                cf.setArguments(bundle);
                getFragmentManager().beginTransaction().replace(R.id.fragments_container, cf).
                        addToBackStack(CardFragment.class.getName()).
                        commit();

                //cf.searchBankCard(cardType);
            }
        });
    }

}
