package sv.edu.catolica.drinkupgrupo06;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class RegisterActivity extends AppCompatActivity {
    EditText etNombre, etCorreo, etContrasena;
    Button btnRegistrar;
    DataBase dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(v.getPaddingLeft(), systemBars.top, v.getPaddingRight(), systemBars.bottom);
            return insets;
        });

        etNombre = findViewById(R.id.etNombre);
        etCorreo = findViewById(R.id.etCorreo);
        etContrasena = findViewById(R.id.etContrasena);
        btnRegistrar = findViewById(R.id.btnRegistrar);

        dbHelper = new DataBase(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }

    public void RegistrarUsuario(View view) {
        String nombre = etNombre.getText().toString().trim();
        String correo = etCorreo.getText().toString().trim();
        String contrasena = etContrasena.getText().toString().trim();

        if (nombre.isEmpty() || correo.isEmpty() || contrasena.isEmpty()) {
            Toast.makeText(this, R.string.completa_todos_los_campos, Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put("nombre", nombre);
        valores.put("correo", correo);
        valores.put("contrasena", contrasena);

        long resultado = db.insert("usuarios", null, valores);

        if (resultado != -1) {
            Toast.makeText(this, R.string.usuario_registrado_correctamente, Toast.LENGTH_SHORT).show();
            finish(); // opcional: regresa al login
        } else {
            Toast.makeText(this, R.string.error_correo_ya_registrado, Toast.LENGTH_SHORT).show();
        }
    }
}