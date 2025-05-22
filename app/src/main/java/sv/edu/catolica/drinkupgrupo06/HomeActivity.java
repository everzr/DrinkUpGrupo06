package sv.edu.catolica.drinkupgrupo06;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class HomeActivity extends AppCompatActivity {
    private DataBase dbHelper;
    private TextView tvBienvenida;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        dbHelper = new DataBase(this);

        // Verificar si el usuario ha ingresado sus datos
        if (!datosUsuarioCompletos()) {
            // Redirigir a InsertUserDataActivity si no hay datos
            startActivity(new Intent(this, InsertUserDataActivity.class));
            finish();
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.blue)));
        }

        tvBienvenida = findViewById(R.id.tvBienvenida);

        SharedPreferences prefs = getSharedPreferences("usuario", MODE_PRIVATE);
        String nombre = prefs.getString("nombre", "Usuario");
        String mensajeBienvenida = getString(R.string.welcome_message, nombre);
        tvBienvenida.setText(mensajeBienvenida);



        /*        // Configurar el Toolbar
       findViewById(R.id.icon_home).setOnClickListener(v -> {
            Intent intent = new Intent(this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        });

        findViewById(R.id.icon_reminder).setOnClickListener(v -> {
            Intent intent = new Intent(this, ReminderActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        });

        findViewById(R.id.icon_info).setOnClickListener(v -> {
            Intent intent = new Intent(this, InfoActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        });

        findViewById(R.id.icon_history).setOnClickListener(v -> {
            Intent intent = new Intent(this, HistoryActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        });

        findViewById(R.id.icon_profile).setOnClickListener(v -> {
            Intent intent = new Intent(this, ProfileActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        }); */

    }


    public void cerrarSesion(View view) {
        new AlertDialog.Builder(this)
                .setTitle("Confirmación")
                .setMessage("¿Estás seguro de que deseas cerrar sesión?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    SharedPreferences prefs = getSharedPreferences("usuario", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.clear(); // Limpia todos los datos guardados
                    editor.apply();

                    startActivity(new Intent(this, LoginActivity.class));
                    overridePendingTransition(0, 0);
                    finish();
                })
                .setNegativeButton("No", null)
                .show();
    }

    private boolean datosUsuarioCompletos() {
        int usuarioId = getSharedPreferences("usuario", MODE_PRIVATE).getInt("id", -1);
        if (usuarioId == -1) {
            return false; // Usuario no encontrado
        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM datos_usuario WHERE usuario_id = ?", new String[]{String.valueOf(usuarioId)});
        boolean datosCompletos = cursor.moveToFirst(); // Verifica si hay datos
        cursor.close();
        return datosCompletos;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.navbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.nav_home) {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
            finish();

            return true;
        } else if (item.getItemId() == R.id.nav_reminder) {
            Intent intent = new Intent(this, ReminderActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
            finish();
            return true;
        } else if (item.getItemId() == R.id.nav_history) {
            Intent intent = new Intent(this, HistoryActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
            finish();
            return true;
        } else if (item.getItemId() == R.id.nav_info) {
            Intent intent = new Intent(this, InfoActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
            finish();
            return true;
        } else if (item.getItemId() == R.id.nav_profile) {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
            finish();
            return true;
        } else if (item.getItemId() == R.id.nav_logout) {
            cerrarSesion(null);

            return true;

        }
        return super.onOptionsItemSelected(item);
    }



}