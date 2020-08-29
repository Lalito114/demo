package com.szzcs.smartpos.configuracion;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import org.apache.commons.lang3.ObjectUtils;

public class SQLiteBD extends SQLiteOpenHelper {

    private static final String NOMBRE_DB = "configuracionServidor1";
    private static final int VERSION_DB = 1;
    private static final String TBL_EMPRESA = "CREATE TABLE TBL_EMPRESA1(ID TEXT PRIMARY KEY, SIIC TEXT,CORREO TEXT, EMPRESAID TEXT, IP TEXT, NOMBRE TEXT, NUMEROFRANQUICIA TEXT, NUMINTERNO TEXT)" ;


    public SQLiteBD(Context context) {
        super(context, NOMBRE_DB, null, VERSION_DB);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TBL_EMPRESA);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS TBL_EMPRESA1");
        db.execSQL(TBL_EMPRESA);
    }

    public void agregaEmpresa(String id, String siic,String correo, String empresaid, String ip, String nombre, String numerofranquicia, String numinterno){
        SQLiteDatabase base = getWritableDatabase();
        if (base!=null){
            base.execSQL("INSERT INTO TBL_EMPRESA1 VALUES ('"+id+"','"+siic+"','"+correo+"', '"+empresaid+"','"+ip+"','"+nombre+"','"+numerofranquicia+"','"+numinterno+"')");
            base.close();
        }
    }

    public void agregartipoempresa(){

    }
}
