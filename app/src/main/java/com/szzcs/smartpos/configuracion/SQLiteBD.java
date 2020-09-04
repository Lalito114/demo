package com.szzcs.smartpos.configuracion;


import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.support.annotation.Nullable;

public class SQLiteBD  extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "ConfiguracionEstacion.db";

    public SQLiteBD( Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TBL_CONFIGURACION_ESTACION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_CONFIGURACION_ESTACION);
        onCreate(db);

    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void InsertarDatosEstacion(String idempresa, String siic,String correo, String empresaid, String ipestacion, String nombreestacion, String numerofranquicia, String numerointerno){
        SQLiteDatabase base = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Datosempresa.IDEMPRESA, idempresa);
        values.put(Datosempresa.SIIC, siic);
        values.put(Datosempresa.CORREO, correo);
        values.put(Datosempresa.EMPRESAID, empresaid);
        values.put(Datosempresa.IPESTACION, ipestacion);
        values.put(Datosempresa.NOMBREESTACION, nombreestacion);
        values.put(Datosempresa.NUMEROFRANQUICIA, numerofranquicia);
        values.put(Datosempresa.NUMEROINTERNO, numerointerno);

        long newRowId = base.insert(Datosempresa.NOMBRE_TABLA, null, values);

    }

    String getIdEsatcion(){
        String IdEstacion;
        
    }
    public static class Datosempresa implements BaseColumns {
        public static final String NOMBRE_TABLA = "configuracionestacion";
        public static final String IDEMPRESA = "idempresa";
        public static final String SIIC = "siic";
        public static final String CORREO = "correo";
        public static final String EMPRESAID = "empresaid";
        public static final String IPESTACION = "ipestacion";
        public static final String NOMBREESTACION = "nombreestacion";
        public static final String NUMEROFRANQUICIA = "numerofranquicia";
        public static final String NUMEROINTERNO = "numerointerno";
    }

    private static final String TBL_CONFIGURACION_ESTACION =
            "CREATE TABLE " + Datosempresa.NOMBRE_TABLA + " (" +
                    Datosempresa._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    Datosempresa.IDEMPRESA + " TEXT," +
                    Datosempresa.SIIC + " TEXT," +
                    Datosempresa.CORREO + " TEXT," +
                    Datosempresa.EMPRESAID + " TEXT," +
                    Datosempresa.IPESTACION + " TEXT," +
                    Datosempresa.NOMBREESTACION + " TEXT," +
                    Datosempresa.NUMEROFRANQUICIA + " TEXT," +
                    Datosempresa.NUMEROINTERNO + " TEXT)";

    private static final String SQL_DELETE_CONFIGURACION_ESTACION =
            "DROP TABLE IF EXISTS " + Datosempresa.NOMBRE_TABLA;

}
