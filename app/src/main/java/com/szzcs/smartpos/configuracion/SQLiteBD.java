package com.szzcs.smartpos.configuracion;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.support.annotation.Nullable;
import android.util.Log;

public class SQLiteBD  extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "ConfiguracionEstacion.db";

    public SQLiteBD( Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TBL_CONFIGURACION_ESTACION);
        db.execSQL(TBL_ENCABEZADO_ESTACION);
        db.execSQL(TBL_DATOS_TARJETERO);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_CONFIGURACION_ESTACION);
        db.execSQL(SQL_DELETE_ENCABEZADO);
        db.execSQL(SQL_DELETE_DATOS_TARJETERO);
        onCreate(db);

    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
//--------------------------------------TABLA EDICION Y CONFIGURACION DE LO MTODOS DE LA TABLA NUMERO TARJETERO--------------------------
    public static class DatosTarjetero implements BaseColumns{
        public static final String NOMBRE_TABLA_TARJETERO = "tblnumerotarjetero";
        public static final String DIRECCION_MAC = "direccionmac";
        public static final String PROPIEDAD_CONEXION = "propiedadconexion";
        public static final String ID_TARJETERO = "id";
    }

    private static final String TBL_DATOS_TARJETERO =
            "CREATE TABLE " + DatosTarjetero.NOMBRE_TABLA_TARJETERO + " ("+
                    DatosTarjetero._ID + " TEXT PRIMARY KEY," +
                    DatosTarjetero.DIRECCION_MAC + " TEXT," +
                    DatosTarjetero.PROPIEDAD_CONEXION + " TEXT, "  +
                    DatosTarjetero.ID_TARJETERO + " TEXT )";

    private static final String SQL_DELETE_DATOS_TARJETERO =
            "DROP TABLE IF EXISTS " + DatosTarjetero.NOMBRE_TABLA_TARJETERO;

    public void InsertarDatosNumeroTarjetero(String direccionmac, String propiedadconexion, String id){
        SQLiteDatabase base = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatosTarjetero.DIRECCION_MAC, direccionmac);
        values.put(DatosTarjetero.PROPIEDAD_CONEXION, propiedadconexion);
        values.put(DatosTarjetero.ID_TARJETERO, id);

        long newRowId = base.insert(DatosTarjetero.NOMBRE_TABLA_TARJETERO,null,values);
    }

    public String getDireccionMac(){
        SQLiteDatabase base = getReadableDatabase();
        Cursor cursor = base.rawQuery("SELECT tblnumerotarjetero FROM direccionmac", null);
        cursor.moveToFirst();
        String dato = cursor.getString(0);
        return dato;
    }
    public String getPropiedadConexion(){
        SQLiteDatabase base = getReadableDatabase();
        Cursor cursor = base.rawQuery("SELECT tblnumerotarjetero FROM propiedadconexion", null);
        cursor.moveToFirst();
        String dato = cursor.getString(0);
        return dato;
    }

    public String getITarjtero(){
        SQLiteDatabase base = getReadableDatabase();
        Cursor cursor = base.rawQuery("SELECT tblnumerotarjetero FROM id", null);
        cursor.moveToFirst();
        String dato = cursor.getString(0);
        return dato;
    }


//   -------------------------------TEMINAN LOS METODOS DE LA TABLA DEL NUMERO DEL TAJETERO ----------------------------------------------


    public boolean checkDataBase(String Database_path) {
        SQLiteDatabase checkDB = null;
        try {
            checkDB = SQLiteDatabase.openDatabase(Database_path, null, SQLiteDatabase.OPEN_READONLY);
            checkDB.close();
        } catch (SQLiteException e) {
            Log.e("Error", "No existe la base de datos ");
        }
        return checkDB != null;
    }

    public void InsertarDatosEstacion(String idempresa, String sucursalid,String siic,String correo, String empresaid, String ipestacion, String nombreestacion, String numerofranquicia, String numerointerno, String tipo){
        SQLiteDatabase base = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Datosempresa.IDEMPRESA, idempresa);
        values.put(Datosempresa.SUCURSALID, sucursalid);
        values.put(Datosempresa.SIIC, siic);
        values.put(Datosempresa.CORREO, correo);
        values.put(Datosempresa.EMPRESAID, empresaid);
        values.put(Datosempresa.IPESTACION, ipestacion);
        values.put(Datosempresa.NOMBREESTACION, nombreestacion);
        values.put(Datosempresa.NUMEROFRANQUICIA, numerofranquicia);
        values.put(Datosempresa.NUMEROINTERNO, numerointerno);
        values.put(Datosempresa.TIPO, tipo);

        long newRowId = base.insert(Datosempresa.NOMBRE_TABLA, null, values);
    }

    public String getIdEstacion(){
        SQLiteDatabase base = getReadableDatabase();
        Cursor cursor = base.rawQuery("SELECT idempresa FROM configuracionestacion", null);
        cursor.moveToFirst();
        String idempresa = cursor.getString(0);
        return idempresa;
    }
    public String getNumeroEstacion(){
        SQLiteDatabase base = getReadableDatabase();
        Cursor cursor = base.rawQuery("SELECT numerofranquicia FROM configuracionestacion", null);
        cursor.moveToFirst();
        String idempresa = cursor.getString(0);
        return idempresa;
    }
    public String getSIIC(){
        SQLiteDatabase base = getReadableDatabase();
        Cursor cursor = base.rawQuery("SELECT siic FROM configuracionestacion", null);
        cursor.moveToFirst();
        String tipo = cursor.getString(0);
        return tipo;
    }
    public String getNombreEsatcion (){
        SQLiteDatabase base = getReadableDatabase();
        Cursor cursor = base.rawQuery("SELECT nombreestacion FROM configuracionestacion", null);
        cursor.moveToFirst();
        String nombreestacion = cursor.getString(0);
        return nombreestacion;
    }
    public String getIpEstacion(){
        SQLiteDatabase base = getReadableDatabase();
        Cursor cursor = base.rawQuery("SELECT ipestacion FROM configuracionestacion", null);
        cursor.moveToFirst();
        String ipestacion = cursor.getString(0);
        return ipestacion;
    }
    public String getTipoEstacion(){
        SQLiteDatabase base = getReadableDatabase();
        Cursor cursor = base.rawQuery("SELECT tipo FROM configuracionestacion", null);
        cursor.moveToFirst();
        String tipo = cursor.getString(0);
        return tipo;
    }
    public String getIdSucursal(){
        SQLiteDatabase base = getReadableDatabase();
        Cursor cursor = base.rawQuery("SELECT sucursalid FROM configuracionestacion", null);
        cursor.moveToFirst();
        String idempresa = cursor.getString(0);
        return idempresa;
    }
    public static class Datosempresa implements BaseColumns {
        public static final String NOMBRE_TABLA = "configuracionestacion";
        public static final String IDEMPRESA = "idempresa";
        public static final String SUCURSALID = "sucursalid";
        public static final String SIIC = "siic";
        public static final String CORREO = "correo";
        public static final String EMPRESAID = "empresaid";
        public static final String IPESTACION = "ipestacion";
        public static final String NOMBREESTACION = "nombreestacion";
        public static final String NUMEROFRANQUICIA = "numerofranquicia";
        public static final String NUMEROINTERNO = "numerointerno";
        public static final String TIPO = "tipo";
    }
    private static final String TBL_CONFIGURACION_ESTACION =
            "CREATE TABLE " + Datosempresa.NOMBRE_TABLA + " (" +
                    Datosempresa._ID + " INTEGER PRIMARY KEY," +
                    Datosempresa.IDEMPRESA + " TEXT," +
                    Datosempresa.SUCURSALID + " TEXT," +
                    Datosempresa.SIIC + " TEXT," +
                    Datosempresa.CORREO + " TEXT," +
                    Datosempresa.EMPRESAID + " TEXT," +
                    Datosempresa.IPESTACION + " TEXT," +
                    Datosempresa.NOMBREESTACION + " TEXT," +
                    Datosempresa.NUMEROFRANQUICIA + " TEXT," +
                    Datosempresa.NUMEROINTERNO + " TEXT," +
                    Datosempresa.TIPO + " TEXT)";

    private static final String SQL_DELETE_CONFIGURACION_ESTACION =
            "DROP TABLE IF EXISTS " + Datosempresa.NOMBRE_TABLA;

    public static class Datosencabezado implements BaseColumns{
        public static final String NOMBRE_TABLA_ENCABEZADO = "tblencabezado";
        public static final String EMPRESA_ID = "empresaid";
        public static final String RAZON_SOCIAL = "razonsocial";
        public static final String REGIMEN_FISCAL = "regimenfiscal";
        public static final String CALLE = "calle";
        public static final String NUMERO_EXTERIOR = "numeroexterior";
        public static final String NUMERO_INTERIOR = "numerointerior";
        public static final String COLONIA = "colonia";
        public static final String LOCALIDAD = "localidad";
        public static final String MUNICIPIO = "municipio";
        public static final String ESTADO = "estado";
        public static final String PAIS = "pais";
        public static final String CP = "cp";
        public static final String RFC = "rfc";
    }

    private static final String TBL_ENCABEZADO_ESTACION =
            "CREATE TABLE " + Datosencabezado.NOMBRE_TABLA_ENCABEZADO + " ("+
                    Datosencabezado._ID + " TEXT PRIMARY KEY," +
                    Datosencabezado.EMPRESA_ID + " TEXT," +
                    Datosencabezado.RAZON_SOCIAL + " TEXT," +
                    Datosencabezado.REGIMEN_FISCAL + " TEXT," +
                    Datosencabezado.CALLE + " TEXT," +
                    Datosencabezado.NUMERO_EXTERIOR + " TEXT," +
                    Datosencabezado.NUMERO_INTERIOR + " TEXT," +
                    Datosencabezado.COLONIA + " TEXT," +
                    Datosencabezado.LOCALIDAD + " TEXT," +
                    Datosencabezado.MUNICIPIO + " TEXT," +
                    Datosencabezado.ESTADO + " TEXT," +
                    Datosencabezado.PAIS + " TEXT," +
                    Datosencabezado.CP + " TEXT," +
                    Datosencabezado.RFC + " TEXT)";

    private static final String SQL_DELETE_ENCABEZADO =
            "DROP TABLE IF EXISTS " + Datosencabezado.NOMBRE_TABLA_ENCABEZADO;



    public void InsertarDatosEncabezado(String empresaid, String razonsocial, String regimenfiscal, String calle, String numeroexterior, String numerointerior, String colonia, String localidad, String municipio, String estado, String pais, String cp, String rfc){
        SQLiteDatabase base = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Datosencabezado.EMPRESA_ID,empresaid);
        values.put(Datosencabezado.RAZON_SOCIAL,razonsocial);
        values.put(Datosencabezado.REGIMEN_FISCAL, regimenfiscal);
        values.put(Datosencabezado.CALLE, calle);
        values.put(Datosencabezado.NUMERO_EXTERIOR, numeroexterior);
        values.put(Datosencabezado.NUMERO_INTERIOR,numerointerior);
        values.put(Datosencabezado.COLONIA, colonia);
        values.put(Datosencabezado.LOCALIDAD,localidad);
        values.put(Datosencabezado.MUNICIPIO, municipio);
        values.put(Datosencabezado.ESTADO, estado);
        values.put(Datosencabezado.PAIS, pais);
        values.put(Datosencabezado.CP,cp);
        values.put(Datosencabezado.RFC, rfc);

        long newRowId = base.insert(Datosencabezado.NOMBRE_TABLA_ENCABEZADO, null, values);
    }

    public String getempresaid(){
        SQLiteDatabase base = getReadableDatabase();
        Cursor cursor = base.rawQuery("SELECT empresaid FROM tblencabezado", null);
        cursor.moveToFirst();
        String tipo = cursor.getString(0);
        return tipo;
    }

    public String getRazonSocial(){
        SQLiteDatabase base = getReadableDatabase();
        Cursor cursor = base.rawQuery("SELECT razonsocial FROM tblencabezado", null);
        cursor.moveToFirst();
        String tipo = cursor.getString(0);
        return tipo;
    }
    public String getRegimenFiscal(){
        SQLiteDatabase base = getReadableDatabase();
        Cursor cursor = base.rawQuery("SELECT regimenfiscal FROM tblencabezado", null);
        cursor.moveToFirst();
        String tipo = cursor.getString(0);
        return tipo;
    }
    public String getCalle(){
        SQLiteDatabase base = getReadableDatabase();
        Cursor cursor = base.rawQuery("SELECT calle FROM tblencabezado", null);
        cursor.moveToFirst();
        String tipo = cursor.getString(0);
        return tipo;
    }
    public String getNumeroExterior(){
        SQLiteDatabase base = getReadableDatabase();
        Cursor cursor = base.rawQuery("SELECT numeroexterior FROM tblencabezado", null);
        cursor.moveToFirst();
        String tipo = cursor.getString(0);
        return tipo;
    }
    public String getNumeroInterno(){
        SQLiteDatabase base = getReadableDatabase();
        Cursor cursor = base.rawQuery("SELECT numerointerior FROM tblencabezado", null);
        cursor.moveToFirst();
        String tipo = cursor.getString(0);
        return tipo;
    }
    public String getColonia(){
        SQLiteDatabase base = getReadableDatabase();
        Cursor cursor = base.rawQuery("SELECT colonia FROM tblencabezado", null);
        cursor.moveToFirst();
        String tipo = cursor.getString(0);
        return tipo;
    }
    public String getLocalidad(){
        SQLiteDatabase base = getReadableDatabase();
        Cursor cursor = base.rawQuery("SELECT localidad FROM tblencabezado", null);
        cursor.moveToFirst();
        String tipo = cursor.getString(0);
        return tipo;
    }
    public String getMunicipio(){
        SQLiteDatabase base = getReadableDatabase();
        Cursor cursor = base.rawQuery("SELECT municipio FROM tblencabezado", null);
        cursor.moveToFirst();
        String tipo = cursor.getString(0);
        return tipo;
    }
    public String getEstado(){
        SQLiteDatabase base = getReadableDatabase();
        Cursor cursor = base.rawQuery("SELECT estado FROM tblencabezado", null);
        cursor.moveToFirst();
        String tipo = cursor.getString(0);
        return tipo;
    }
    public String getPais(){
        SQLiteDatabase base = getReadableDatabase();
        Cursor cursor = base.rawQuery("SELECT pais FROM tblencabezado", null);
        cursor.moveToFirst();
        String tipo = cursor.getString(0);
        return tipo;
    }
    public String getCP(){
        SQLiteDatabase base = getReadableDatabase();
        Cursor cursor = base.rawQuery("SELECT cp FROM tblencabezado", null);
        cursor.moveToFirst();
        String tipo = cursor.getString(0);
        return tipo;
    }
    public String getRFC(){
        SQLiteDatabase base = getReadableDatabase();
        Cursor cursor = base.rawQuery("SELECT rfc FROM tblencabezado", null);
        cursor.moveToFirst();
        String tipo = cursor.getString(0);
        return tipo;
    }
}
