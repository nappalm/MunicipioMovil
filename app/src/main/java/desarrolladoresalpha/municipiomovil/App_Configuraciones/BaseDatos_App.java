package desarrolladoresalpha.municipiomovil.App_Configuraciones;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BaseDatos_App extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "TocumboMovil.db";

    //Datos de localidades :::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    public static final String TABLE_LOCALIDADES = "localidades";
    public static final String COLUMN_IDLOC = "_id";
    public static final String COLUMN_NOMBRELOC = "nombre";
    //Datos de telefonos :::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    public static final String TABLE_TELEFONOS = "telefonos";
    public static final String COLUMN_IDTEL = "_id";
    public static final String COLUMN_DEPARTAMENTO = "departamento";
    public static final String COLUMN_TELEFONO = "telefono";
    //Datos de reportes ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    public static final String TABLE_REPORTES = "reportes";
    public static final String COLUMN_IDREP = "_id";
    public static final String COLUMN_TIPOREP = "tipo";
    public static final String COLUMN_ESTADOREP = "estado";
    //Datos de noticias ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    public static final String TABLE_NOTICIAS = "noticias";
    public static final String COLUMN_IDNOT = "_id";
    public static final String COLUMN_ENCABEZADO = "encabezado";
    public static final String COLUMN_FECHA = "fecha";
    public static final String COLUMN_TEXTO = "texto";
    public static final String COLUMN_AUTOR = "autor";




    public BaseDatos_App(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_LOCALIDADES + " (" +COLUMN_IDLOC + " INTEGER PRIMARY KEY, " +COLUMN_NOMBRELOC + " TEXT)");
        db.execSQL("CREATE TABLE " + TABLE_TELEFONOS + " (" +COLUMN_IDTEL + " INTEGER PRIMARY KEY, " +COLUMN_DEPARTAMENTO + " TEXT, "+COLUMN_TELEFONO+" TEXT)");
        db.execSQL("CREATE TABLE " + TABLE_REPORTES + " (" +COLUMN_IDREP + " INTEGER PRIMARY KEY, " +COLUMN_TIPOREP + " TEXT, "+COLUMN_ESTADOREP+" TEXT)");
        db.execSQL("CREATE TABLE " + TABLE_NOTICIAS + " (" +COLUMN_IDNOT + " INTEGER PRIMARY KEY, " +COLUMN_ENCABEZADO + " TEXT, "+COLUMN_FECHA+" TEXT, "+COLUMN_TEXTO + " TEXT, "+COLUMN_AUTOR+ " TEXT)");


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        if (i > i1) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCALIDADES);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_TELEFONOS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_REPORTES);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTICIAS);

        }
    }

    public Cursor ConsultarLocalidades() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_LOCALIDADES, new String[]{COLUMN_IDLOC,COLUMN_NOMBRELOC},null, null, null, null, COLUMN_NOMBRELOC);
        if (cursor != null) {
            cursor.moveToFirst();
            return cursor;
        } else {
            return null;
        }
    }

    public void RegistrarLocalidades(String Nombre, int ID) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_IDLOC, ID);
        values.put(COLUMN_NOMBRELOC, Nombre);

        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_LOCALIDADES, null, values);
        db.close();
    }

    public void EliminarLocalidades(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_LOCALIDADES);
    }

    public Cursor ConsultarTelefonos() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_TELEFONOS, new String[]{COLUMN_IDTEL,COLUMN_DEPARTAMENTO,COLUMN_TELEFONO},null, null, null, null, COLUMN_DEPARTAMENTO);
        if (cursor != null) {
            cursor.moveToFirst();
            return cursor;
        } else {
            return null;
        }
    }

    public void RegistrarTelefonos(String Departamento,String Telefono) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_DEPARTAMENTO, Departamento);
        values.put(COLUMN_TELEFONO, Telefono);


        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_TELEFONOS, null, values);
        db.close();
    }

    public void EliminarTelefonos(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_TELEFONOS);
    }

    public Cursor ConsultarReportes() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_REPORTES, new String[]{COLUMN_IDREP,COLUMN_TIPOREP,COLUMN_ESTADOREP},null, null, null, null, COLUMN_IDREP);
        if (cursor != null) {
            cursor.moveToFirst();
            return cursor;
        } else {
            return null;
        }
    }

    public void RegistrarReportes(int ID,String Tipo,String Estado) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_IDREP, ID);
        values.put(COLUMN_TIPOREP, Tipo);
        values.put(COLUMN_ESTADOREP, Estado);

        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_REPORTES, null, values);
        db.close();
    }

    public void EliminarReportes(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_REPORTES);
    }


    public Cursor ConsultarNoticias() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NOTICIAS, new String[]{COLUMN_IDNOT,COLUMN_ENCABEZADO,COLUMN_FECHA,COLUMN_TEXTO,COLUMN_AUTOR},null, null, null, null, COLUMN_IDREP);
        if (cursor != null) {
            cursor.moveToFirst();
            return cursor;
        } else {
            return null;
        }
    }

    public void RegistrarNoticias(int Folio,String Encabezado,String Fecha,String Txto,String Autor) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_IDNOT, Folio);
        values.put(COLUMN_ENCABEZADO, Encabezado);
        values.put(COLUMN_FECHA, Fecha);
        values.put(COLUMN_TEXTO, Txto);
        values.put(COLUMN_AUTOR, Autor);


        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_NOTICIAS, null, values);
        db.close();
    }

    public void EliminarNoticias(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NOTICIAS);
    }
}
