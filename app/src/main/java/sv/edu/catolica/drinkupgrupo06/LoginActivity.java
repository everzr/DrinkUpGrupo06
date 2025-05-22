package sv.edu.catolica.drinkupgrupo06;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
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

public class LoginActivity extends AppCompatActivity {

    private EditText editTextEmail, editTextPassword;
    private Button buttonLogin, buttonRegisterRedirect;

    private DataBase dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.btnLogin);
        buttonRegisterRedirect = findViewById(R.id.btnRegisterRedirect);

        dbHelper = new DataBase(this);

        SharedPreferences prefs = getSharedPreferences("usuario", MODE_PRIVATE);
        if (prefs.getBoolean("isLoggedIn", false)) {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }

    public void IniciarSesion(View view) {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (validarCredenciales(email, password)) {
            Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        } else {
            Toast.makeText(this, "Credenciales incorrectas. Intenta nuevamente.", Toast.LENGTH_SHORT).show();
        }
    }

    public void Registrarse(View view) {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    private boolean validarCredenciales(String correo, String contrasena) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT id, nombre, correo FROM usuarios WHERE correo = ? AND contrasena = ?",
                new String[]{correo, contrasena}
        );

        if (cursor.moveToFirst()) {
            int id = cursor.getInt(0);
            String nombre = cursor.getString(1);
            String correoDb = cursor.getString(2);

            // Guardar en SharedPreferences
            SharedPreferences prefs = getSharedPreferences("usuario", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("isLoggedIn", true);
            editor.putInt("id", id);
            editor.putString("nombre", nombre);
            editor.putString("correo", correoDb);
            editor.apply();

            cursor.close();
            return true;
        }

        cursor.close();
        return false;
    }

}