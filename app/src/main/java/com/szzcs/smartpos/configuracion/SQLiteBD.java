package com.szzcs.smartpos.configuracion;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteBD extends SQLiteOpenHelper {

    private static final String NOMBRE_DB = "configuracionServidor";
    private static final int VERSION_DB = 1;
    private static final String TABLA_RECURSOS = "CREATE TABLE TBL_CONFIGURACION" +
            "(IDIP INT PRIMARY KEY, DIREECION TEXT, NUMEROESTACION TEXT)";

    private static final String TABLA_TIPOEMPRESA = "CREATE TABLE TBL_TIPOEMPRESA (IDTIPO INT PRIMARY KEY, DESCRIPCION_TIPO TEXT, CPRIMARIO TEXT, CSECUNDARIO TEXT, CTERCERO TEXT) ";

    public SQLiteBD(Context context) {
        super(context, NOMBRE_DB, null, VERSION_DB);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLA_RECURSOS);
        db.execSQL(TABLA_TIPOEMPRESA);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

    }

    public void agregarip(int idip, String direccion, String numeroestacion){
        SQLiteDatabase base = getWritableDatabase();
        if (base!=null){
            base.execSQL("INSERT INTO TBL_CONFIGURACION VALUES ('"+idip+"','"+direccion+"', '"+numeroestacion+"')");
            base.close();
        }
    }

    public void agregartipoempresa(){

    }
}
