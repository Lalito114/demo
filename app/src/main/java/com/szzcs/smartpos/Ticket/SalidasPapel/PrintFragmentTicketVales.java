package com.szzcs.smartpos.Ticket.SalidasPapel;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.util.Log;
import android.view.View;

import com.szzcs.smartpos.Munu_Principal;
import com.szzcs.smartpos.MyApp;
import com.szzcs.smartpos.R;
import com.szzcs.smartpos.configuracion.SQLiteBD;
import com.szzcs.smartpos.utils.DialogUtils;
import com.zcs.sdk.DriverManager;
import com.zcs.sdk.Printer;
import com.zcs.sdk.SdkResult;
import com.zcs.sdk.print.PrnStrFormat;
import com.zcs.sdk.print.PrnTextFont;
import com.zcs.sdk.print.PrnTextStyle;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class PrintFragmentTicketVales extends PreferenceFragment {
    private static final String TAG = "PrintFragment";
    private DriverManager mDriverManager = MyApp.sDriverManager;
    private Printer mPrinter;
    private boolean mPrintStatus = false;
    private Bitmap mBitmapDef;


    public static final String PRINT_TEXT = "Esta máquina POS inteligente tiene una impresora y está basada en la aplicación de la plataforma Android. Integra sistemas de cajero y ECR caros. La demanda de nuevos pagos de escaneo de código se está volviendo cada vez más importante. El dispositivo inteligente de impresora Android de pantalla grande tiene una aplicación de gestión de marketing comercial incorporada. , Acepte el pago del pedido del cliente, satisfaga bien las necesidades anteriores; al mismo tiempo, los requisitos portátiles, con la implementación del sistema de mensajería de nombre real, utilizado en la industria de mensajería para escanear rápidamente el código de barras para ingresar. Excelente mano de obra y excelente calidad son las mejores opciones en el mercado.";
    public static final String QR_TEXT = "0000-1057-760";
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
        ImprimirticketNormalVales();



    }

    public void ImprimirticketNormalVales() {
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
                    //------------------------------Encabezado Datos fiscales------------------------------
                    SQLiteBD data = new SQLiteBD(getActivity());
                    //Asignamos la hora y fecha de la impresion del ticket, alinenadolos del lado izquierdo del ticket
                    format.setTextSize(23);
                    //Tamaño del tipo de letra
                    //Formato o tipo de letra de molde
                    format.setStyle(PrnTextStyle.NORMAL);
                    //Alineacion del texto
                    format.setAli(Layout.Alignment.ALIGN_OPPOSITE);
                    //Asignamos dos saltos de linea
                    mPrinter.setPrintAppendString("", format);
                    //Asignamos la primera linea de texto que es para la fecha y hora de la impresion del ticket
                    //Clase donde obtenemos el dia y lo hora del sistema
                    Date date = new Date();
                    //Formato para el dia, mes y año
                    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    //Formato para la hora, minutos y segundos
                    DateFormat hourFormat = new SimpleDateFormat("HH:mm:ss");
                    mPrinter.setPrintAppendString(dateFormat.format(date)+ " " + hourFormat.format(date), format);

                    // Nombre de la Estacion y Numero de la Estacion
                    format.setTextSize(23);
                    format.setStyle(PrnTextStyle.NORMAL);
                    format.setAli(Layout.Alignment.ALIGN_CENTER);
                    mPrinter.setPrintAppendString("", format);
                    mPrinter.setPrintAppendString("Est: "+data.getNumeroEstacion(), format);
                    mPrinter.setPrintAppendString(data.getNombreEsatcion(), format);

                    //Datos obre el regimen y domicilio Fiscal
                    mPrinter.setPrintAppendString(data.getCalle() + ", " + data.getNumeroExterior()+ ", " + data.getNumeroInterno() +", " + data.getColonia() +
                            data.getLocalidad() +  ", " + data.getMunicipio() + ", " + data.getEstado() + ", " + data.getPais() + ", CP:" + data.getCP(), format);
                    mPrinter.setPrintAppendString(" ",format);
                    mPrinter.setPrintAppendString("RFC: "+data.getRFC()+" SIIC: " + data.getSIIC(),format);
                    mPrinter.setPrintAppendString("" ,format);
                    mPrinter.setPrintAppendString(data.getRegimenFiscal() ,format);

                    mPrinter.setPrintAppendString("",format);

                    format.setTextSize(25);
                    format.setStyle(PrnTextStyle.NORMAL);
                    format.setAli(Layout.Alignment.ALIGN_CENTER);
                    mPrinter.setPrintAppendString("ORIGINAL",format);

                    format.setTextSize(23);
                    format.setStyle(PrnTextStyle.NORMAL);
                    format.setAli(Layout.Alignment.ALIGN_CENTER);


                    String numerorecibo = getArguments().getString("numerorecibo");
                    String numerotransaccion = getArguments().getString("numerotransaccion");
                    mPrinter.setPrintAppendString("No. Rec: " + numerorecibo + "  No. Trans: " + numerotransaccion,format);

                    String numeroRastreo = getArguments().getString("numerorastreo");
//                    mPrinter.setPrintAppendString("No. Rastreo: " + numeroRastreo,format);

                    String posicion = getArguments().getString("posicion");
                    String despachador = getArguments().getString("despachador");
                    String vendedor = getArguments().getString("vendedor");
                    mPrinter.setPrintAppendString("PC: " + posicion,format);
                    mPrinter.setPrintAppendString("Le atendio: " + vendedor,format);
                    format.setTextSize(23);
                    format.setStyle(PrnTextStyle.BOLD);
                    format.setAli(Layout.Alignment.ALIGN_CENTER);
                    String formapago = getArguments().getString("nombrepago");
                    mPrinter.setPrintAppendString("PAGO: " + formapago ,format);
                    format.setTextSize(23);
                    format.setStyle(PrnTextStyle.NORMAL);
                    format.setAli(Layout.Alignment.ALIGN_NORMAL);
                    mPrinter.setPrintAppendString(" CANT    DESC    PRECIO    IMPORTE", format);
                    mPrinter.setPrintAppendString("- - - - - - - - - - - - - - - - -", format);
                    format.setTextSize(23);
                    format.setStyle(PrnTextStyle.NORMAL);
                    format.setAli(Layout.Alignment.ALIGN_CENTER);
                    String productos = getArguments().getString("productos");
                    mPrinter.setPrintAppendString(productos,format);
                    mPrinter.setPrintAppendString("- - - - - - - - - - - - - - - - -", format);

                    String subtotal = getArguments().getString("subtotal");
                    String iva = getArguments().getString("iva");
                    String total = getArguments().getString("total");
                    String totaltexto = getArguments().getString("totaltexto");
                    mPrinter.setPrintAppendString("               SUBTOTAL:"+ subtotal, format);
                    mPrinter.setPrintAppendString("                    IVA:"+ iva, format);
                    mPrinter.setPrintAppendString("                  TOTAL:"+ total, format);
                    mPrinter.setPrintAppendString("    ", format);
                    format.setAli(Layout.Alignment.ALIGN_OPPOSITE);
                    format.setTextSize(23);
                    format.setStyle(PrnTextStyle.NORMAL);
                    format.setAli(Layout.Alignment.ALIGN_CENTER);
                    mPrinter.setPrintAppendString("("+totaltexto+")", format);


                    format.setTextSize(80);
                    format.setStyle(PrnTextStyle.NORMAL);
                    format.setAli(Layout.Alignment.ALIGN_CENTER);

                    mPrinter.setPrintAppendString("$ " + total,format);

//                    mPrinter.setPrintAppendQRCode(numeroRastreo, 200, 200, Layout.Alignment.ALIGN_CENTER);

                    format.setTextSize(23);
                    format.setStyle(PrnTextStyle.NORMAL);
                    format.setAli(Layout.Alignment.ALIGN_CENTER);
//                    String mensaje = getArguments().getString("mensaje");
//                    mPrinter.setPrintAppendString(mensaje ,format);

                    mPrinter.setPrintAppendString("" ,format);
                    mPrinter.setPrintAppendString("" ,format);
                    mPrinter.setPrintAppendString("" ,format);
                    mPrinter.setPrintAppendString("" ,format);

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
        Intent i = new Intent(getActivity(), Munu_Principal.class);
        startActivity(i);
        getActivity().finish();
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

}

