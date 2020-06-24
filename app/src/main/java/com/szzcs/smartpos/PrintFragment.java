package com.szzcs.smartpos;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.BarcodeFormat;
import com.szzcs.smartpos.utils.DialogUtils;
import com.zcs.sdk.DriverManager;
import com.zcs.sdk.Printer;
import com.zcs.sdk.SdkResult;
import com.zcs.sdk.print.PrnAlignTypeEnum;
import com.zcs.sdk.print.PrnFontSizeTypeEnum;
import com.zcs.sdk.print.PrnSpeedTypeEnum;
import com.zcs.sdk.print.PrnStrFormat;
import com.zcs.sdk.print.PrnTextFont;
import com.zcs.sdk.print.PrnTextStyle;
import com.zcs.sdk.util.LogUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.content.Intent.getIntent;

/**
 * Created by yyzz on 2018/5/25.
 */

public class PrintFragment extends PreferenceFragment {
    private static final String TAG = "PrintFragment";
    private DriverManager mDriverManager = MyApp.sDriverManager;
    private Printer mPrinter;
    private boolean mPrintStatus = false;
    private Bitmap mBitmapDef;

    public static final String PRINT_TEXT = "Esta máquina POS inteligente tiene una impresora y está basada en la aplicación de la plataforma Android. Integra sistemas de cajero y ECR caros. La demanda de nuevos pagos de escaneo de código se está volviendo cada vez más importante. El dispositivo inteligente de impresora Android de pantalla grande tiene una aplicación de gestión de marketing comercial incorporada. , Acepte el pago del pedido del cliente, satisfaga bien las necesidades anteriores; al mismo tiempo, los requisitos portátiles, con la implementación del sistema de mensajería de nombre real, utilizado en la industria de mensajería para escanear rápidamente el código de barras para ingresar. Excelente mano de obra y excelente calidad son las mejores opciones en el mercado.";
    public static final String QR_TEXT = "https://www.baidu.com";
    public static final String BAR_TEXT = "6922711079066";
    Intent estacion;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Instancia la vista de
        //addPreferencesFromResource(R.xml.pref_print);
    }

    @Override
    public void onResume() {
        super.onResume();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    File file = new File(Environment.getExternalStorageDirectory() + "/fonts/simsun.ttf");
                    if (file.exists()) {
                    } else {
                        AssetManager mAssetManger = getActivity().getAssets();
                        // String[] fileNames = mAssetManger.list("fonts");// 获取assets目录下的所有文件及有文件的目录名
                        InputStream in = mAssetManger.open("fonts/simsun.ttf");
                        saveFile(in, "simsun.ttf");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    File file = new File(Environment.getExternalStorageDirectory() + "/fonts/heiti.ttf");
                    if (file.exists()) {
                    } else {
                        AssetManager mAssetManger = getActivity().getAssets();
                        // String[] fileNames = mAssetManger.list("fonts");// 获取assets目录下的所有文件及有文件的目录名
                        InputStream in = mAssetManger.open("fonts/heiti.ttf");
                        saveFile(in, "heiti.ttf");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {

                    File file = new File(Environment.getExternalStorageDirectory() + "/fonts/fangzhengyouyuan.ttf");
                    if (file.exists()) {
                    } else {
                        AssetManager mAssetManger = getActivity().getAssets();
                        // String[] fileNames = mAssetManger.list("fonts");// 获取assets目录下的所有文件及有文件的目录名
                        InputStream in = mAssetManger.open("fonts/fangzhengyouyuan.ttf");
                        saveFile(in, "fangzhengyouyuan.ttf");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    int fontsStyle = 0;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mDriverManager = MyApp.sDriverManager;
        mPrinter = mDriverManager.getPrinter();
        int printerStatus = mPrinter.getPrinterStatus();
        Log.d(TAG, "getPrinterStatus: " + printerStatus);
        if (printerStatus != SdkResult.SDK_OK) {
            mPrintStatus = true;
        } else {
            mPrintStatus = false;
        }



//        findPreference(getString(R.string.key_print_text)).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
//            @Override
//            public boolean onPreferenceChange(final Preference preference, final Object newValue) {
//
//                ListPreference listPreference = (ListPreference) preference;
//                final int index = listPreference.findIndexOfValue((String) newValue);
//                final CharSequence[] entries = listPreference.getEntries();
//                if (entries[index].equals("宋体") || entries[index].equals("Song Typeface")) {
//                    LogUtils.error("打印宋体");
//                    fontsStyle = 0;
//                    try {
//                        File file = new File(Environment.getExternalStorageDirectory() + "/fonts/simsun.ttf");
//                        if (file.exists()) {
//                        } else {
//                            AssetManager mAssetManger = getActivity().getAssets();
//                            // String[] fileNames = mAssetManger.list("fonts");// 获取assets目录下的所有文件及有文件的目录名
//                            InputStream in = mAssetManger.open("fonts/simsun.ttf");
//                            saveFile(in, "simsun.ttf");
//                        }
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                } else if (entries[index].equals("默认字体") || entries[index].equals("Default Typeface")) {
//                    LogUtils.error("打印默认字体");
//                    fontsStyle = 1;
//
//
//                } else if (entries[index].equals("幼圆体") || entries[index].equals("Rounded Fonts")) {
//                    LogUtils.error("打印圆幼体");
//                    fontsStyle = 2;
//                    try {
//
//                        File file = new File(Environment.getExternalStorageDirectory() + "/fonts/fangzhengyouyuan.ttf");
//                        if (file.exists()) {
//                        } else {
//                            AssetManager mAssetManger = getActivity().getAssets();
//
//                            InputStream in = mAssetManger.open("fonts/fangzhengyouyuan.ttf");
//                            saveFile(in, "fangzhengyouyuan.ttf");
//                        }
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//                printMatrixText();
//                return true;
//            }
//        });
        printMatrixText();

    }


    public void printMatrixText() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                AssetManager asm = getActivity().getAssets();
                InputStream inputStream = null;
                try {
                    inputStream = asm.open("copo.png");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Drawable d = Drawable.createFromStream(inputStream, null);
                Bitmap bitmap = ((BitmapDrawable) d).getBitmap();

                int printStatus = mPrinter.getPrinterStatus();
                if (printStatus == SdkResult.SDK_PRN_STATUS_PAPEROUT) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            DialogUtils.show(getActivity(), getString(R.string.printer_out_of_paper));

                        }
                    });
                } else {


                    mPrinter.setPrintAppendBitmap(bitmap, Layout.Alignment.ALIGN_CENTER);
                    PrnStrFormat format = new PrnStrFormat();
                    format.setTextSize(30);
                    format.setAli(Layout.Alignment.ALIGN_CENTER);
                    format.setStyle(PrnTextStyle.BOLD);
                    if (fontsStyle == 0) {
                        format.setFont(PrnTextFont.CUSTOM);
                        format.setPath(Environment.getExternalStorageDirectory() + "/fonts/simsun.ttf");
                    } else if (fontsStyle == 1) {
                        format.setFont(PrnTextFont.DEFAULT);
                        //  format.setPath(Environment.getExternalStorageDirectory()+"/fonts/heiti.ttf");
                    } else {
                        format.setFont(PrnTextFont.CUSTOM);
                        format.setPath(Environment.getExternalStorageDirectory() + "/fonts/fangzhengyouyuan.ttf");
                    }

                    format.setTextSize(20);
                    format.setStyle(PrnTextStyle.ITALIC);
                    format.setAli(Layout.Alignment.ALIGN_CENTER);
                    //------------------------------Encabezado------------------------------
                    mPrinter.setPrintAppendString(" ", format);
                    String texto = getArguments().getString("noestacion");
                    mPrinter.setPrintAppendString(getResources().getString(R.string.pos_sales_slip)+texto,format);

                    //Nombre de la Esatcion de Servicio
                    String nombre = getArguments().getString("nombreestacion");
                    mPrinter.setPrintAppendString(nombre, format);

                    //RFC de la estacion de Servicio y SIIC
                    String rfc = getArguments().getString("razonsocial");
                    String siic = getArguments().getString("datos");
                    mPrinter.setPrintAppendString(getResources().getString(R.string.merchant_no)+rfc+"  "+getResources().getString(R.string.terminal_name)+siic, format);

                    //Regimen fiscal
                    String regimen = getArguments().getString("regimenfiscal");
                    mPrinter.setPrintAppendString(regimen, format);

                    //-------Datos Direccion--------------------
                    //calle de la empresa
                    String calle = getArguments().getString("calle");
                    String exterior = getArguments().getString("exterior");
                    String colonia = getArguments().getString("colonia");
                    String localidad = getArguments().getString("localidad");
                    String municipio = getArguments().getString("municipio");
                    String estado = getArguments().getString("estado");
                    String cp = getArguments().getString("cp");
                    String pais = getArguments().getString("pais");
                    mPrinter.setPrintAppendString(calle+" "+ exterior + " " + colonia + " " + localidad + " " + municipio
                            + " " + estado + " " + cp + " " + pais, format);
                    mPrinter.setPrintAppendString(" ", format);
                    mPrinter.setPrintAppendString(" ", format);

                    //------------------Aqui termina el encabezado
                    format.setAli(Layout.Alignment.ALIGN_CENTER);
                    format.setTextSize(20);
                    format.setStyle(PrnTextStyle.NORMAL);
                    format.setAli(Layout.Alignment.ALIGN_CENTER);
                    format.setStyle(PrnTextStyle.NORMAL);



                    //-------------------------------------------- Inicia el anticuerpo del ticket
                    format.setAli(Layout.Alignment.ALIGN_CENTER);
                    format.setStyle(PrnTextStyle.NORMAL);
                    format.setTextSize(20);
                    mPrinter.setPrintAppendString("ORIGINAL", format);
                    //--------Numero de Recibo
                    String recibo = getArguments().getString("norecibo");
                    mPrinter.setPrintAppendString("RECIBO: " + recibo, format);

                    //-- numero de rastreo
                    String rastreo = getArguments().getString("norastreo");
                    mPrinter.setPrintAppendString("No. Rastreo: " + rastreo, format);


                    //Forma de Pago
                    String formapago = getArguments().getString("formadepago");
                    String nombreforma = null;
                    int efectivo = 1;
                    int ban = Integer.parseInt(formapago);
                    if (efectivo == ban){
                        nombreforma = "EFECTIVO";
                    }else {
                        int vales = 2;
                        if (vales == ban){
                            nombreforma = "VALES";
                        }else {
                            int american = 3;
                            if (american == ban){
                                nombreforma = "AMERICAN EXPRESS";
                            }else{
                                int gascard = 4;
                                if (gascard == ban){
                                    nombreforma = "GAS CARD AMEX";
                                }else{
                                    int visa = 5;
                                    if (visa == ban){
                                        nombreforma = "VISA/MASTERCARD";
                                    }else{
                                        int electronic = 6;
                                        if (electronic == ban){
                                            nombreforma = "VALE ELECTRONICO";
                                        }else{
                                            int credito = 7;
                                            if (credito == ban){
                                                nombreforma = "CREDITO ES";
                                            }else{
                                                int mobile = 10;
                                                if (mobile == ban){
                                                    nombreforma = "CORPOMOBILE";
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    mPrinter.setPrintAppendString("FORMA DE PAGO:" + nombreforma, format);

                    //Datos de tiempo de venta
                    Date date = new Date();
                    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    DateFormat hourFormat = new SimpleDateFormat("HH:mm:ss");
                    mPrinter.setPrintAppendString(getResources().getString(R.string.exe_date) + dateFormat.format(date)+ "  HORA:" + hourFormat.format(date), format);

                    //Numero de terminqal, posicion de cargar y datos del vendedor
                    String posicion = getArguments().getString("posicion");
                    String idusuario = getArguments().getString("idusuario");
                    mPrinter.setPrintAppendString("TERM: "+"PC: "+ posicion+" DESP: "+idusuario+" VEND: " + idusuario, format);
                    mPrinter.setPrintAppendString(" ", format);
                    //----------------------------------------------------------------------------------------------------

                    //-----------------------------Inician los datos de las ventas realizadas
                    format.setTextSize(20);
                    format.setAli(Layout.Alignment.ALIGN_NORMAL);
                    format.setStyle(PrnTextStyle.NORMAL);
                    mPrinter.setPrintAppendString("  CANT  II   DESC   PRECIO   IMPORTE", format);
                    mPrinter.setPrintAppendString("- - - - - - - - - - - - - - - - - - - ", format);
                    String cantidad = getArguments().getString("cantidad");
                    String numero = getArguments().getString("numero");
                    String descrip = getArguments().getString("descrip");
                    String precio = getArguments().getString("precio");
                    String importe = getArguments().getString("impor");

                    format.setAli(Layout.Alignment.ALIGN_NORMAL);
                    mPrinter.setPrintAppendString(cantidad, format);
                    //-- -------------------costos finales

                    format.setAli(Layout.Alignment.ALIGN_NORMAL);
                    format.setStyle(PrnTextStyle.NORMAL);
                    format.setTextSize(20);
                    String subtotal = getArguments().getString("subtotal");
                    String iva = getArguments().getString("iva");
                    String total = getArguments().getString("total");
                    String totaltexto = getArguments().getString("totaltexto");
                    mPrinter.setPrintAppendString("                  SUBTOTAL:   "+ subtotal, format);
                    mPrinter.setPrintAppendString("                       IVA:   "+ iva, format);
                    mPrinter.setPrintAppendString("                     TOTAL:   "+ total, format);
                    mPrinter.setPrintAppendString("    ", format);
                    format.setAli(Layout.Alignment.ALIGN_OPPOSITE);
                    mPrinter.setPrintAppendString(totaltexto, format);
                    mPrinter.setPrintAppendString("- - - - -- -- - - - - - - - - ", format);

                    mPrinter.setPrintAppendString( " ", format);
                    String mensaje = getArguments().getString("mensaje");
                    mPrinter.setPrintAppendString(mensaje + " ", format);
                    mPrinter.setPrintAppendString( " ", format);
                    mPrinter.setPrintAppendString( " ", format);
                    mPrinter.setPrintAppendString( " ", format);
                    mPrinter.setPrintAppendString( " ", format);
                    format.setStyle(PrnTextStyle.NORMAL);
                    printStatus = mPrinter.setPrintStart();
                    if (printStatus == SdkResult.SDK_PRN_STATUS_PAPEROUT) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                DialogUtils.show(getActivity(), getString(R.string.printer_out_of_paper));

                            }
                        });
                    }
                    if (ban == 3){
                        segundometodo();
                    }
                    enviarPrincipal();
                }
            }
        }).start();
    }

    private void segundometodo() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                AssetManager asm = getActivity().getAssets();
                InputStream inputStream = null;
                try {
                    inputStream = asm.open("copo.png");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Drawable d = Drawable.createFromStream(inputStream, null);
                Bitmap bitmap = ((BitmapDrawable) d).getBitmap();

                int printStatus = mPrinter.getPrinterStatus();
                if (printStatus == SdkResult.SDK_PRN_STATUS_PAPEROUT) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            DialogUtils.show(getActivity(), getString(R.string.printer_out_of_paper));

                        }
                    });
                } else {


                    mPrinter.setPrintAppendBitmap(bitmap, Layout.Alignment.ALIGN_CENTER);
                    PrnStrFormat format = new PrnStrFormat();
                    format.setTextSize(30);
                    format.setAli(Layout.Alignment.ALIGN_CENTER);
                    format.setStyle(PrnTextStyle.BOLD);
                    if (fontsStyle == 0) {
                        format.setFont(PrnTextFont.CUSTOM);
                        format.setPath(Environment.getExternalStorageDirectory() + "/fonts/simsun.ttf");
                    } else if (fontsStyle == 1) {
                        format.setFont(PrnTextFont.DEFAULT);
                        //  format.setPath(Environment.getExternalStorageDirectory()+"/fonts/heiti.ttf");
                    } else {
                        format.setFont(PrnTextFont.CUSTOM);
                        format.setPath(Environment.getExternalStorageDirectory() + "/fonts/fangzhengyouyuan.ttf");
                    }

                    format.setTextSize(20);
                    format.setStyle(PrnTextStyle.ITALIC);
                    format.setAli(Layout.Alignment.ALIGN_CENTER);
                    //------------------------------Encabezado------------------------------
                    mPrinter.setPrintAppendString(" ", format);
                    String texto = getArguments().getString("noestacion");
                    mPrinter.setPrintAppendString(getResources().getString(R.string.pos_sales_slip)+texto,format);

                    //Nombre de la Esatcion de Servicio
                    String nombre = getArguments().getString("nombreestacion");
                    mPrinter.setPrintAppendString(nombre, format);

                    //RFC de la estacion de Servicio y SIIC
                    String rfc = getArguments().getString("razonsocial");
                    String siic = getArguments().getString("datos");
                    mPrinter.setPrintAppendString(getResources().getString(R.string.merchant_no)+rfc+"  "+getResources().getString(R.string.terminal_name)+siic, format);

                    //Regimen fiscal
                    String regimen = getArguments().getString("regimenfiscal");
                    mPrinter.setPrintAppendString(regimen, format);

                    //-------Datos Direccion--------------------
                    //calle de la empresa
                    String calle = getArguments().getString("calle");
                    String exterior = getArguments().getString("exterior");
                    String colonia = getArguments().getString("colonia");
                    String localidad = getArguments().getString("localidad");
                    String municipio = getArguments().getString("municipio");
                    String estado = getArguments().getString("estado");
                    String cp = getArguments().getString("cp");
                    String pais = getArguments().getString("pais");
                    mPrinter.setPrintAppendString(calle+" "+ exterior + " " + colonia + " " + localidad + " " + municipio
                            + " " + estado + " " + cp + " " + pais, format);
                    mPrinter.setPrintAppendString(" ", format);
                    mPrinter.setPrintAppendString(" ", format);

                    //------------------Aqui termina el encabezado
                    format.setAli(Layout.Alignment.ALIGN_CENTER);
                    format.setTextSize(20);
                    format.setStyle(PrnTextStyle.NORMAL);
                    format.setAli(Layout.Alignment.ALIGN_CENTER);
                    format.setStyle(PrnTextStyle.NORMAL);



                    //-------------------------------------------- Inicia el anticuerpo del ticket
                    format.setAli(Layout.Alignment.ALIGN_CENTER);
                    format.setStyle(PrnTextStyle.NORMAL);
                    format.setTextSize(20);
                    mPrinter.setPrintAppendString("COPIA", format);
                    //--------Numero de Recibo
                    String recibo = getArguments().getString("norecibo");
                    mPrinter.setPrintAppendString("RECIBO: " + recibo, format);

                    //-- numero de rastreo
                    String rastreo = getArguments().getString("norastreo");
                    mPrinter.setPrintAppendString("No. Rastreo: " + rastreo, format);


                    //Forma de Pago
                    String formapago = getArguments().getString("formadepago");
                    String nombreforma = null;
                    int efectivo = 1;
                    int ban = Integer.parseInt(formapago);
                    if (efectivo == ban){
                        nombreforma = "EFECTIVO";
                    }else {
                        int vales = 2;
                        if (vales == ban){
                            nombreforma = "VALES";
                        }else {
                            int american = 3;
                            if (american == ban){
                                nombreforma = "AMERICAN EXPRESS";
                            }else{
                                int gascard = 4;
                                if (gascard == ban){
                                    nombreforma = "GAS CARD AMEX";
                                }else{
                                    int visa = 5;
                                    if (visa == ban){
                                        nombreforma = "VISA/MASTERCARD";
                                    }else{
                                        int electronic = 6;
                                        if (electronic == ban){
                                            nombreforma = "VALE ELECTRONICO";
                                        }else{
                                            int credito = 7;
                                            if (credito == ban){
                                                nombreforma = "CREDITO ES";
                                            }else{
                                                int mobile = 10;
                                                if (mobile == ban){
                                                    nombreforma = "CORPOMOBILE";
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    mPrinter.setPrintAppendString("FORMA DE PAGO:" + nombreforma, format);

                    //Datos de tiempo de venta
                    Date date = new Date();
                    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    DateFormat hourFormat = new SimpleDateFormat("HH:mm:ss");
                    mPrinter.setPrintAppendString(getResources().getString(R.string.exe_date) + dateFormat.format(date)+ "  HORA:" + hourFormat.format(date), format);

                    //Numero de terminqal, posicion de cargar y datos del vendedor
                    String posicion = getArguments().getString("posicion");
                    String idusuario = getArguments().getString("idusuario");
                    mPrinter.setPrintAppendString("TERM: "+"PC: "+ posicion+" DESP: "+idusuario+" VEND: " + idusuario, format);
                    mPrinter.setPrintAppendString(" ", format);
                    //----------------------------------------------------------------------------------------------------

                    //-----------------------------Inician los datos de las ventas realizadas
                    format.setTextSize(20);
                    format.setAli(Layout.Alignment.ALIGN_NORMAL);
                    format.setStyle(PrnTextStyle.NORMAL);
                    mPrinter.setPrintAppendString("  CANT  II   DESC   PRECIO   IMPORTE", format);
                    mPrinter.setPrintAppendString("- - - - - - - - - - - - - - - - - - - ", format);
                    String cantidad = getArguments().getString("cantidad");
                    String numero = getArguments().getString("numero");
                    String descrip = getArguments().getString("descrip");
                    String precio = getArguments().getString("precio");
                    String importe = getArguments().getString("impor");

                    format.setAli(Layout.Alignment.ALIGN_NORMAL);
                    mPrinter.setPrintAppendString(cantidad, format);
                    //-- -------------------costos finales

                    format.setAli(Layout.Alignment.ALIGN_NORMAL);
                    format.setStyle(PrnTextStyle.NORMAL);
                    format.setTextSize(20);
                    String subtotal = getArguments().getString("subtotal");
                    String iva = getArguments().getString("iva");
                    String total = getArguments().getString("total");
                    String totaltexto = getArguments().getString("totaltexto");
                    mPrinter.setPrintAppendString("                  SUBTOTAL:   "+ subtotal, format);
                    mPrinter.setPrintAppendString("                       IVA:   "+ iva, format);
                    mPrinter.setPrintAppendString("                     TOTAL:   "+ total, format);
                    mPrinter.setPrintAppendString("    ", format);
                    format.setAli(Layout.Alignment.ALIGN_OPPOSITE);
                    mPrinter.setPrintAppendString(totaltexto, format);
                    mPrinter.setPrintAppendString("- - - - -- -- - - - - - - - - ", format);

                    mPrinter.setPrintAppendString( " ", format);
                    String mensaje = getArguments().getString("mensaje");
                    mPrinter.setPrintAppendString(mensaje + " ", format);
                    mPrinter.setPrintAppendString( " ", format);
                    mPrinter.setPrintAppendString( " ", format);
                    mPrinter.setPrintAppendString( " ", format);
                    mPrinter.setPrintAppendString( " ", format);
                    format.setStyle(PrnTextStyle.NORMAL);
                    printStatus = mPrinter.setPrintStart();
                    if (printStatus == SdkResult.SDK_PRN_STATUS_PAPEROUT) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                DialogUtils.show(getActivity(), getString(R.string.printer_out_of_paper));

                            }
                        });
                    }
                    enviarPrincipal();
                }
            }
        }).start();

    }

    private void enviarPrincipal() {
        Intent i = new Intent(getActivity(),Munu_Principal.class);
        startActivity(i);

    }

    public static void saveFile(InputStream inputStream, String fileName) {
        //Log.e(TAG, "保存图片");
        File appDir = new File(Environment.getExternalStorageDirectory(), "fonts");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        File file = new File(appDir, fileName);

        try {
            FileOutputStream fos = new FileOutputStream(file);
            byte[] bs = new byte[1024];
            // 读取到的数据长度
            int len;
            // 开始读取
            while ((len = inputStream.read(bs)) != -1) {
                fos.write(bs, 0, len);
            }

            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mBitmapDef != null) {
            mBitmapDef.recycle();
        }
    }

    public void datosEncabezado(String response) {
        Toast.makeText(getActivity(),response, Toast.LENGTH_LONG).show();
    }
}
