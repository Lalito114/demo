package com.szzcs.smartpos.configuracion;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteBD extends SQLiteOpenHelper {

    private static final String NOMBRE_DB = "CONFIGURACIONESTACION1";
    private static final int VERSION_DB = 1;
    private static final String TBL_DATOSESTACION = "CREATE TABLE TBL_DATOSESTACION1(IDREGISTRO INT PRIMARY KEY AUTOINCREMENT, IDESTACION TEXT, SIIC TEXT,CORREO TEXT, EMPRESAID TEXT, IP TEXT, NOMBRE TEXT, NUMEROFRANQUICIA TEXT, NUMINTERNO TEXT)";


    public SQLiteBD(Context context) {
        super(context, NOMBRE_DB, null, VERSION_DB);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TBL_DATOSESTACION);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

    }

    public void DatosEstacion(String id, String siic,String correo, String empresaid, String ip, String nombre, String numerofranquicia, String numinterno){
        SQLiteDatabase base = getWritableDatabase();
        if (base!=null){
            base.execSQL("INSERT INTO TBL_DATOSESTACION1 VALUES (''"+id+"','"+siic+"','"+correo+"', '"+empresaid+"','"+ip+"','"+nombre+"','"+numerofranquicia+"','"+numinterno+"')");
            base.close();
        }
    }

    public String getIdEStacion(){
        SQLiteDatabase base = getWritableDatabase();
        Cursor c = base.rawQuery("SELECT IDESTACION FROM TBL_DATOSESTACION", null);
        String IdEstacion  = c.getString(c.getColumnIndex("IDESTACION"));
        return IdEstacion;
    }

}
