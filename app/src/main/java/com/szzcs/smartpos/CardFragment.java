package com.szzcs.smartpos;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.szzcs.smartpos.Puntada.Registrar.ClaveDespachadorPuntada;
import com.szzcs.smartpos.Puntada.SeccionTarjeta;
import com.szzcs.smartpos.TanqueLleno.ClaveDespachadorTL;
import com.szzcs.smartpos.TanqueLleno.PosicionCargaTLl;
import com.szzcs.smartpos.configuracion.SQLiteBD;
import com.szzcs.smartpos.utils.DialogUtils;
import com.szzcs.smartpos.utils.SDK_Result;
import com.zcs.sdk.DriverManager;
import com.zcs.sdk.SdkData;
import com.zcs.sdk.SdkResult;
import com.zcs.sdk.card.CardInfoEntity;
import com.zcs.sdk.card.CardReaderManager;
import com.zcs.sdk.card.CardReaderTypeEnum;
import com.zcs.sdk.card.CardSlotNoEnum;
import com.zcs.sdk.card.ICCard;
import com.zcs.sdk.card.MagCard;
import com.zcs.sdk.card.RfCard;
import com.zcs.sdk.listener.OnSearchCardListener;
import com.zcs.sdk.util.StringUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

import static android.support.v4.app.ActivityCompat.finishAffinity;
import static android.support.v4.app.ActivityCompat.getPermissionCompatDelegate;


/**
 * Created by yyzz on 2018/5/24.
 */

public class CardFragment extends PreferenceFragment {

    private static final String TAG = "CardFragment";

    private static final int READ_TIMEOUT = 60 * 100000;
    private static final int MSG_CARD_OK = 2001;
    private static final int MSG_CARD_ERROR = 2002;
    private static final int MSG_CARD_APDU = 2003;
    private static final int MSG_RF_CARD_APDU = 2007;
    private static final int MSG_CARD_M1 = 2004;
    private static final int MSG_CARD_MF_PLUS = 2005;
    private static final int MSG_CARD_FELICA = 2006;

    public static final byte[] APDU_SEND_IC = {0x00, (byte) 0xA4, 0x04, 0x00, 0x0E, 0x31, 0x50, 0x41, 0x59, 0x2E, 0x53, 0x59, 0x53, 0x2E, 0x44, 0x44, 0x46, 0x30, 0x31, 0X00};
    public static final byte[] APDU_SEND_RF = {0x00, (byte) 0xA4, 0x04, 0x00, 0x0E, 0x32, 0x50, 0x41, 0x59, 0x2E, 0x53, 0x59, 0x53, 0x2E, 0x44, 0x44, 0x46, 0x30, 0x31, 0x00};
    public static final byte[] APDU_SEND_RANDOM = {0x00, (byte) 0x84, 0x00, 0x00, 0x08};
    // 10 06 01 2E 45 76 BA C5 45 2B 01 09 00 01 80 00
    public static final byte[] APDU_SEND_FELICA = {0x10, 0x06, 0x01, 0x2E, 0x45, 0x76, (byte) 0xBA, (byte) 0xC5, 0x45, 0x2B, 0x01, 0x09, 0x00, 0x01, (byte) 0x80, 0x00};
    private static final String KEY_APDU = "APDU";
    private static final String KEY_RF_CARD_TYPE = "RF_CARD_TYPE";
    private static final byte SLOT_USERCARD = 0x00;
    private static final byte SLOT_PSAM1 = 0x01;
    private static final byte SLOT_PSAM2 = 0x02;

    private DriverManager mDriverManager = MyApp.sDriverManager;
    //private CardHandler mHandler;

    public ICCard mICCard;
    private RfCard mRfCard;
    private MagCard mMagCard;

    private ProgressDialog mProgressDialog;
    private Dialog mCardInfoDialog;

    private Handler mHandler;

    boolean ifSearch = true;
    boolean isM1 = false;
    boolean isMfPlus = false;

    String keyM1 = "FFFFFFFFFFFF";
    byte keyType = 0x00; // 0x00 typeA, 0x01 typeB
    boolean hasSetM1 = false;

    String keyMfPlus = "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF";
    byte[] addressMfPlus = {0x40, 0x00};
    boolean hasSetMf = false;

    byte mRfCardType = 0;
    //CardReaderManager mCardReadManager = null;
    //CardReaderManager mCardReadManager = new CardReaderManager();
    CardReaderManager mCardReadManager;
    CardReaderTypeEnum mCardType = CardReaderTypeEnum.MAG_IC_RF_CARD;
    private CardReaderTypeEnum mCardType2;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();

        mCardType2 = (CardReaderTypeEnum) args.getSerializable("card_type");

        addPreferencesFromResource(R.xml.pref_card);
        mHandler = new CardHandler(this);

        mCardReadManager = mDriverManager.getCardReadManager();
        mICCard = mCardReadManager.getICCard();
        mRfCard = mCardReadManager.getRFCard();
        mMagCard = mCardReadManager.getMAGCard();
        mMagCard.magCardClose();

    }

    //Este metodo lee cualquier tipo de tarjeta
    //Se va a utilizar para leer la tarjeta tanto de puntada como de tanque lleno
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // search card and read, just wait a moment
//        findPreference(getString(R.string.key_read_all)).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
//            @Override
//            public boolean onPreferenceClick(Preference preference) {
//                searchBankCard(CardReaderTypeEnum.MAG_IC_RF_CARD);
//                return true;
//            }
//        });
//
        searchBankCard(mCardType2);
        // read mag card
//        findPreference(getString(R.string.key_magnetic)).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
//            @Override
//            public boolean onPreferenceClick(Preference preference) {
//                searchBankCard(CardReaderTypeEnum.MAG_CARD);
//                return true;
//            }
//        });

        // ic card
//        findPreference(getString(R.string.key_ic)).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
//            @Override
//            public boolean onPreferenceClick(Preference preference) {
//                searchBankCard(CardReaderTypeEnum.IC_CARD);
//                return true;
//            }
//        });

//        findPreference(getString(R.string.key_rf)).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
//            @Override
//            public boolean onPreferenceClick(Preference preference) {
//                searchBankCard(CardReaderTypeEnum.RF_CARD);
//                return true;
//            }
//        });


    }

    OnSearchCardListener mListener = new OnSearchCardListener() {
        @Override
        public void onCardInfo(CardInfoEntity cardInfoEntity) {
            CardReaderTypeEnum cardType = cardInfoEntity.getCardExistslot();
            switch (cardType) {
                case RF_CARD:
                    // only can get SdkData.RF_TYPE_A / SdkData.RF_TYPE_B / SdkData.RF_TYPE_FELICA /
                    // SdkData.RF_TYPE_MEMORY_A / SdkData.RF_TYPE_MEMORY_B
                    byte rfCardType = cardInfoEntity.getRfCardType();
                    Log.e(TAG, "rfCardType: " + rfCardType);
                    if (isM1) {
                        readM1Card();
                    } else if (isMfPlus) {
                        readMFPlusCard();
                    } else {
                        readRfCard(rfCardType);
                    }
                    break;
                case MAG_CARD:
                    readMagCard();
                    break;
                case IC_CARD:
                    readICCard(CardSlotNoEnum.SDK_ICC_USERCARD);
                    break;
                case PSIM1:
                    readICCard(CardSlotNoEnum.SDK_ICC_SAM1);
                    break;
                case PSIM2:
                    readICCard(CardSlotNoEnum.SDK_ICC_SAM2);
                    break;
            }
        }

        @Override
        public void onError(int i) {
            isM1 = false;
            isMfPlus = false;
            mHandler.sendEmptyMessage(i);
        }

        @Override
        public void onNoCard(CardReaderTypeEnum cardReaderTypeEnum, boolean b) {

        }
    };

    void searchBankCard(CardReaderTypeEnum cardType) {
        mCardType = cardType;
        mRfCardType = SdkData.RF_TYPE_A | SdkData.RF_TYPE_B;
        switch (cardType) {
            case MAG_IC_RF_CARD:
                showSearchCardDialog(R.string.title_waiting, R.string.msg_bank_card);
                break;
            case MAG_CARD:
                showSearchCardDialog(R.string.title_waiting, R.string.msg_mag_card);
                break;
            case RF_CARD:
                showSearchCardDialog(R.string.title_waiting, R.string.msg_rf_card);
                break;
            case IC_CARD:
                showSearchCardDialog(R.string.title_waiting, R.string.msg_ic_card);
                break;
            case PSIM1:
            case PSIM2:
                showSearchCardDialog(R.string.title_waiting, R.string.msg_reading);
                break;
        }
        mCardReadManager.cancelSearchCard();
        mCardReadManager.searchCard(cardType, READ_TIMEOUT, mListener);
    }

    void searchFelica() {
        mCardType = CardReaderTypeEnum.RF_CARD;
        mRfCardType = SdkData.RF_TYPE_FELICA;
        showSearchCardDialog(R.string.title_waiting, R.string.msg_rf_card);
        mCardReadManager.cancelSearchCard();
        mCardReadManager.searchCard(CardReaderTypeEnum.RF_CARD, READ_TIMEOUT, SdkData.RF_TYPE_FELICA, mListener);
    }

    void searchM1() {
        isM1 = true;
        mCardType = CardReaderTypeEnum.RF_CARD;
        mRfCardType = SdkData.RF_TYPE_A;
        showM1Dialog(CardReaderTypeEnum.RF_CARD, READ_TIMEOUT, SdkData.RF_TYPE_A, mListener);
    }

    void searchMf() {
        isMfPlus = true;
        mCardType = CardReaderTypeEnum.RF_CARD;
        mRfCardType = SdkData.RF_TYPE_A;
        showMFPlusDialog(CardReaderTypeEnum.RF_CARD, READ_TIMEOUT, SdkData.RF_TYPE_A, mListener);
    }

    /**
     * detect card has been removed
     * if it cant be detected, then research card
     */
    private void researchICC() {
        try {
            Thread.sleep(150);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ifSearch = true;
        while (ifSearch) {
            mICCard.setCardType(true);
            int i = mICCard.getIcCardStatus(CardSlotNoEnum.SDK_ICC_USERCARD);
            if (i == SdkResult.SDK_ICC_NO_CARD) {
                break;
            }
        }
        if (ifSearch) {
            mCardReadManager.searchCard(mCardType, READ_TIMEOUT, mListener);
        }
    }

    /**
     * if there is no rf card, research again
     */
    private void researchRfCard() {
        try {
            Thread.sleep(150);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ifSearch = true;
        while (ifSearch) {
            byte[] outCardType = new byte[1];
            byte[] uid = new byte[300];
            mRfCard.setCardType(true);
            int i = mRfCard.rfSearchCard(mRfCardType, outCardType, uid);
            if (i == SdkResult.SDK_RF_ERR_NOCARD) {
                break;
            }
        }
        if (ifSearch) {
            mCardReadManager.searchCard(mCardType, READ_TIMEOUT, mRfCardType, mListener);
        }
    }

    private void showSearchCardDialog(@StringRes int title, @StringRes int msg) {
        mProgressDialog = (ProgressDialog) DialogUtils.showProgress(getActivity(), getString(title), getString(msg), new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                mCardReadManager.cancelSearchCard();
            }
        });
    }


    private void readMagCard() {
        // use `getMagReadData` to get mag track data and parse data. if it is bank card, then parse exp and card no
        // use `getMagTrackData` to get origin track data
        //CardInfoEntity cardInfo = mMagCard.getMagReadData();
        CardInfoEntity cardInfo = mMagCard.getMagTrackData();
        Log.d(TAG, "cardInfo.getResultcode():" + cardInfo.getResultcode());
        String tk1 = cardInfo.getTk1();
        String tk3 = cardInfo.getTk3();
        String tk2 = cardInfo.getTk2();
       // String tk2 = "4000004210100001";
        String mtk2 =tk2.substring(0,16);
        if(mtk2.isEmpty()){
           Toast.makeText(getActivity(), "No se ha leido correctamente la tarjeta", Toast.LENGTH_LONG).show();
        }else{
            mMagCard.magCardClose();
            // search again
            mCardReadManager.searchCard(mCardType, READ_TIMEOUT, mListener);

            String space = mtk2.substring(0,2);

            CompararTarjetas(mtk2);

        }

    }

    private void CompararTarjetas(final String mtk2) {
        SQLiteBD data = new SQLiteBD(getActivity());
        String url = "http://"+data.getIpEstacion()+"/CorpogasService/api/Bines";

        StringRequest eventoReq = new StringRequest(Request.Method.GET,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            String mascaraInicialTarjeta = mtk2.substring(0,5);
                            String mascaraIdentificador;
                            JSONArray identificado = new JSONArray(response);
                            for (int i = 0; i <identificado.length() ; i++) {
                                JSONObject identificador = identificado.getJSONObject(i);
                                String mascara = identificador.getString("Mascara");
                                mascaraIdentificador = mascara.substring(0,5);

                                char[] cadena_div = mascara.toCharArray();
                                String n = "";
                                for (int j = 0; j <cadena_div.length ; j++) {
                                    if (Character.isDigit(cadena_div[j])){
                                        n += cadena_div[j];
                                    }
                                }

                                switch (i){
                                    case 0:
                                        if (mtk2.contains(n)){
                                            Intent intent = new Intent(getActivity(),ClaveDespachadorTL.class);
                                            intent.putExtra("track",mtk2);
                                            startActivity(intent);

                                        }
                                        break;
                                    case 1:
                                        if (mtk2.contains(n)){
                                            Toast.makeText(getActivity(),"Esta tarjeta es Tanque Lleno SurEste", Toast.LENGTH_LONG).show();
                                        }
                                        break;
                                    case 2:
                                        if (mtk2.contains(n)){
                                            Intent intent = new Intent(getActivity(),SeccionTarjeta.class);
                                            intent.putExtra("track",mtk2);
                                            startActivity(intent);
                                        }
                                        break;
                                    default:
                                        Intent intent = new Intent(getActivity(),Munu_Principal.class);
                                        startActivity(intent);
                                        break;
                                }

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                    //funcion para capturar errores
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(),error.toString(),Toast.LENGTH_SHORT).show();
            }
        });

        // Añade la peticion a la cola
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(eventoReq);
    }

    private void IdentificarTarjeta(String response,String mtk2) {


    }




    private void readICCard(CardSlotNoEnum slotNo) {
        int icCardReset = mICCard.icCardReset(slotNo);

        int[] recvLen = new int[1];
        byte[] recvData = new byte[300];

        if (icCardReset == SdkResult.SDK_OK) {
            int icRes;
            byte[] apdu;
            if (slotNo.getType() == SLOT_PSAM1 || slotNo.getType() == SLOT_PSAM2) {
                apdu = APDU_SEND_RANDOM;
            } else {
                apdu = APDU_SEND_IC;
            }
            icRes = mICCard.icExchangeAPDU(slotNo, apdu, recvData, recvLen);
            if (icRes == SdkResult.SDK_OK) {
                Message msg = Message.obtain();
                msg.what = MSG_CARD_APDU;
                msg.arg1 = icRes;
                msg.obj = StringUtils.convertBytesToHex(recvData).substring(0, recvLen[0] * 2);
                Bundle icBundle = new Bundle();
                icBundle.putString(KEY_APDU, StringUtils.convertBytesToHex(apdu));
                msg.setData(icBundle);
                mHandler.sendMessage(msg);
            } else {
                mHandler.sendEmptyMessage(icRes);
            }
        } else {
            mHandler.sendEmptyMessage(icCardReset);
        }
        int icCardPowerDown = mICCard.icCardPowerDown(CardSlotNoEnum.SDK_ICC_USERCARD);

        if (slotNo.getType() == SLOT_USERCARD) {
            researchICC();
        }
    }

    private void readRfCard(byte realRfType) {
        Message msg = Message.obtain();
        int rfReset = mRfCard.rfReset();
        if (rfReset == SdkResult.SDK_OK) {
            byte[] apduSend;
            if (realRfType == SdkData.RF_TYPE_FELICA) { // felica card
                apduSend = APDU_SEND_FELICA;
            } else {
                apduSend = APDU_SEND_RF;
            }
            byte[] recvData = new byte[300];
            int[] recvLen = new int[1];
            int rfRes = mRfCard.rfExchangeAPDU(apduSend, recvData, recvLen);
            int powerDownRes = mRfCard.rfCardPowerDown();
            if (rfRes != SdkResult.SDK_OK) {
                mHandler.sendEmptyMessage(rfRes);
            } else {
                String recv = StringUtils.convertBytesToHex(recvData).substring(0, recvLen[0] * 2);
                msg.what = MSG_RF_CARD_APDU;
                msg.arg1 = rfRes;
                msg.obj = recv;
                Bundle rfBundle = new Bundle();
                rfBundle.putString(KEY_APDU, StringUtils.convertBytesToHex(apduSend));
                rfBundle.putByte(KEY_RF_CARD_TYPE, realRfType);
                msg.setData(rfBundle);
                mHandler.sendMessage(msg);
            }
        } else {
            mHandler.sendEmptyMessage(rfReset);
        }
        researchRfCard();
    }

    private void readM1Card() {
        StringBuilder m1_message = new StringBuilder();
        byte[] key = StringUtils.convertHexToBytes(keyM1);
        int status;
        do {
            // sector 10 = 4 * 10
            status = mRfCard.m1VerifyKey((byte) (4 * 10), keyType, key);
            if (status != SdkResult.SDK_OK) {
                break;
            }
            m1_message.append("Read sector 10:");
            for (int i = 0; i < 4; i++) {
                byte[] out = new byte[16];
                status = mRfCard.m1ReadBlock((byte) (4 * 10 + i), out);
                if (status == SdkResult.SDK_OK) {
                    m1_message.append("\nBlock").append(i).append(":")
                            .append(StringUtils.convertBytesToHex(out));
                } else {
                    break;
                }
            }
        } while (false);
        if (status == SdkResult.SDK_OK) {
            Message msg = Message.obtain();
            msg.what = MSG_CARD_M1;
            msg.obj = m1_message.toString();
            mHandler.sendMessage(msg);

        } else {
            mHandler.sendEmptyMessage(status);
        }
        researchRfCard();
    }

    void writeM1() {
        // 1. verify the sector key
        // 2. write it
        byte[] key = StringUtils.convertHexToBytes(keyM1);
        int status = mRfCard.m1VerifyKey((byte) (4 * 10), keyType, key);
        if (status == SdkResult.SDK_OK) {
            for (int i = 0; i < 3; i++) {
                byte[] input = com.zcs.sdk.util.StringUtils.convertHexToBytes("0123456789ABCDEF0123456789ABCDEF");
                mRfCard.m1WirteBlock((byte) (4 * 10 + i), input);
            }
        }
    }

    private void readMFPlusCard() {
        StringBuilder m1_mf_puls = new StringBuilder();
        byte[] key = StringUtils.convertHexToBytes(keyMfPlus);
        int status = mRfCard.mFPlusFirstAuthen(addressMfPlus, key);
        if (status == SdkResult.SDK_OK) {
            m1_mf_puls.append("Read sector 0:");
            byte[] outdata = new byte[64];
            if (mRfCard.mFPlusL3Read(StringUtils.convertHexToBytes("0000"), (byte) 4, outdata) == SdkResult.SDK_OK) {
                m1_mf_puls.append(StringUtils.convertBytesToHex(outdata));
            }
        }

        if (status == SdkResult.SDK_OK) {
            Message msg = Message.obtain();
            msg.what = MSG_CARD_MF_PLUS;
            msg.obj = m1_mf_puls.toString();
            mHandler.sendMessage(msg);

        } else {
            mHandler.sendEmptyMessage(status);
        }
        researchRfCard();
    }

    void showM1Dialog(final CardReaderTypeEnum cardType, final int timeout, final byte rfCardType, final OnSearchCardListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View contentView = getActivity().getLayoutInflater().inflate(
                R.layout.activity_m1_dialog, null);
        final EditText password = contentView.findViewById(R.id.password);
        final EditText key_type = contentView.findViewById(R.id.key_type);

        builder.setTitle("M1 Card Password Input:");
        builder.setView(contentView);
        builder.setPositiveButton(getString(R.string.set_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                keyM1 = password.getText().toString().trim();
                String keyType1 = key_type.getText().toString().trim();
                keyType = StringUtils.convertHexToBytes(keyType1)[0];
                hasSetM1 = true;
                isM1 = true;
                showSearchCardDialog(R.string.title_waiting, R.string.msg_m1_card);
                mCardReadManager.searchCard(cardType, timeout, rfCardType, listener);
            }
        }).setNegativeButton(getString(R.string.set_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                hasSetM1 = false;
                isM1 = false;
            }
        }).create().show();
    }

    void showMFPlusDialog(final CardReaderTypeEnum cardType, final int timeout, final byte rfType, final OnSearchCardListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View contentView = getActivity().getLayoutInflater().inflate(
                R.layout.activity_mf_plus_dialog, null);
        final EditText password = contentView.findViewById(R.id.password);
        final EditText key_address = contentView.findViewById(R.id.key_address);

        builder.setTitle("MF plus Card Password Input:");
        builder.setView(contentView);
        builder.setPositiveButton(getString(R.string.set_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                keyMfPlus = password.getText().toString().trim();
                addressMfPlus = StringUtils.convertHexToBytes(key_address.getText().toString().trim());
                hasSetMf = true;
                isMfPlus = true;
                showSearchCardDialog(R.string.title_waiting, R.string.msg_mf_puls_card);
                mCardReadManager.searchCard(cardType, timeout, rfType, listener);
            }
        }).setNegativeButton(getString(R.string.set_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                hasSetMf = false;
                isMfPlus = false;
            }
        }).create().show();
    }

    void closeSearch() {
        Log.i(TAG, "closeSearch");
        isM1 = false;
        isMfPlus = false;
        // stop to detect card
        ifSearch = false;
        mCardReadManager.cancelSearchCard();
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy");
        closeSearch();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mCardReadManager.closeCard();
        super.onDestroy();
    }



    class CardHandler extends Handler implements DialogInterface.OnClickListener, DialogInterface.OnCancelListener {
        WeakReference<Fragment> mFragment;


        CardHandler(Fragment fragment) {
            mFragment = new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            CardFragment fragment = (CardFragment) mFragment.get();
            if (fragment == null || !fragment.isAdded())
                return;
            if (mCardInfoDialog != null) {
                mCardInfoDialog.dismiss();
            }
            if (fragment.mProgressDialog != null) {
                fragment.mProgressDialog.dismiss();
            }
            switch (msg.what) {
                case MSG_CARD_OK:
                    CardInfoEntity cardInfoEntity = (CardInfoEntity) msg.obj;
                    MyApp.cardInfoEntity = cardInfoEntity;
                    mCardInfoDialog = DialogUtils.show(fragment.getActivity(),
                            fragment.getString(R.string.title_card), SDK_Result.obtainCardInfo(fragment.getActivity(), cardInfoEntity),
                            "OK", this, this);
                    break;
                case MSG_CARD_APDU:
                    mCardInfoDialog = DialogUtils.show(fragment.getActivity(),
                            fragment.getString(R.string.title_apdu),
                            SDK_Result.appendMsg("Code", msg.arg1 + "", "APDU send", msg.getData().getString(CardFragment.KEY_APDU), "APDU response", (String) msg.obj),
                            "OK", this, this);
                    break;
                case MSG_RF_CARD_APDU:
                    byte rfCardType = msg.getData().getByte(KEY_RF_CARD_TYPE);
                    String type = handleRfCardType(rfCardType);
                    mCardInfoDialog = DialogUtils.show(fragment.getActivity(), type,
                            SDK_Result.appendMsg("Code", msg.arg1 + "", "Send", msg.getData().getString(CardFragment.KEY_APDU), "Response", (String) msg.obj),
                            "OK", this, this);
                    break;
                case MSG_CARD_M1:
                    mCardInfoDialog = DialogUtils.show(fragment.getActivity(),
                            fragment.getString(R.string.title_card), (String) msg.obj,
                            "OK", this, this);
                    break;

                case MSG_CARD_MF_PLUS:
                    mCardInfoDialog = DialogUtils.show(fragment.getActivity(),
                            fragment.getString(R.string.title_card), (String) msg.obj,
                            "OK", this, this);
                    break;
                default:
                    mCardInfoDialog = DialogUtils.show(fragment.getActivity(),
                            fragment.getString(R.string.title_error),
                            SDK_Result.obtainMsg(fragment.getActivity(), msg.what),
                            "OK", this, this);
                    break;
            }
        }

        private String handleRfCardType(byte rfCardType) {
            String type = "";
            switch (rfCardType) {
                case SdkData.RF_TYPE_A:
                    type = "RF_TYPE_A";
                    break;
                case SdkData.RF_TYPE_B:
                    type = "RF_TYPE_B";
                    break;
                case SdkData.RF_TYPE_MEMORY_A:
                    type = "RF_TYPE_MEMORY_A";
                    break;
                case SdkData.RF_TYPE_FELICA:
                    type = "RF_TYPE_FELICA";
                    break;
                case SdkData.RF_TYPE_MEMORY_B:
                    type = "RF_TYPE_MEMORY_B";
                    break;
            }
            return type;
        }

        @Override
        public void onCancel(DialogInterface dialog) {
            CardFragment fragment = (CardFragment) mFragment.get();
            if (fragment != null && fragment.isAdded()) {
                fragment.ifSearch = false;
                closeSearch();
            }
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            CardFragment fragment = (CardFragment) mFragment.get();
            if (fragment != null && fragment.isAdded()) {
                fragment.ifSearch = false;
                closeSearch();
            }
        }
    }

}
