package sv.edu.catolica.drinkupgrupo06;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBase extends SQLiteOpenHelper {

    private static final String DB_NAME = "drinkup.db";
    private static final int DB_VERSION = 1;

    public DataBase(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE usuarios (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nombre TEXT NOT NULL," +
                "correo TEXT UNIQUE NOT NULL," +
                "contrasena TEXT NOT NULL)");

        db.execSQL("CREATE TABLE datos_usuario (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "usuario_id INTEGER NOT NULL," +
                "edad INTEGER," +
                "genero TEXT," +
                "peso REAL," +
                "actividad_fisica TEXT," +
                "objetivo_diario_ml INTEGER," +
                "FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE)");

        db.execSQL("CREATE TABLE consumo (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "usuario_id INTEGER NOT NULL," +
                "fecha TEXT NOT NULL," +
                "cantidad_ml INTEGER NOT NULL," +
                "FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE)");

        db.execSQL("CREATE TABLE recordatorios (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "usuario_id INTEGER NOT NULL," +
                "hora TEXT NOT NULL," +
                "cantidad_ml INTEGER NOT NULL," +
                "FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE)");

        db.execSQL("CREATE TABLE tips (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "titulo TEXT NOT NULL," +
                "descripcion TEXT," +
                "imagen TEXT)");

        db.execSQL("CREATE TABLE notas (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "usuario_id INTEGER NOT NULL," +
                "fecha TEXT NOT NULL," +
                "nota TEXT," +
                "FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Borra y recrea si se actualiza versión (puedes usar ALTER para producción)
        db.execSQL("DROP TABLE IF EXISTS notas");
        db.execSQL("DROP TABLE IF EXISTS tips");
        db.execSQL("DROP TABLE IF EXISTS recordatorios");
        db.execSQL("DROP TABLE IF EXISTS consumo");
        db.execSQL("DROP TABLE IF EXISTS datos_usuario");
        db.execSQL("DROP TABLE IF EXISTS usuarios");
        onCreate(db);
    }
}
