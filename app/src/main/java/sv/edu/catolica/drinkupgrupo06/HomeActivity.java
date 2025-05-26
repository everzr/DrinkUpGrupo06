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
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity {
    private DataBase dbHelper;
    private TextView tvBienvenida;
    private TextView tvObjetivo, tvConsumo, tvProxRecordatorio;
    private Toolbar toolbar;
   private TextView fechaActual;
    private Menu actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(v.getPaddingLeft(), systemBars.top, v.getPaddingRight(), systemBars.bottom);
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
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.azul_4)));
        }

        tvBienvenida = findViewById(R.id.tvBienvenida);

        SharedPreferences prefs = getSharedPreferences("usuario", MODE_PRIVATE);
        String nombre = prefs.getString("nombre", "Usuario");
        String mensajeBienvenida = getString(R.string.welcome_message, nombre);
        tvBienvenida.setText(mensajeBienvenida);

        tvObjetivo = findViewById(R.id.tvObjetivo);
        tvConsumo = findViewById(R.id.tvConsumo);
        tvProxRecordatorio = findViewById(R.id.tvProxRecordatorio);
        fechaActual = findViewById(R.id.fecha);
        mostrarDatosHome();

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
        actionBar = menu;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (actionBar != null) {
            for (int i = 0; i < actionBar.size(); i++) {
                MenuItem menuItem = actionBar.getItem(i);
                if (menuItem.getIcon() != null) {
                    menuItem.getIcon().setTint(getResources().getColor(R.color.white));
                }
            }
        }

        // Cambia el color solo del ítem seleccionado
        if (item.getIcon() != null) {
            item.getIcon().setTint(getResources().getColor(R.color.grey));
        }

        if (item.getItemId() == R.id.nav_home) {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
            reload(null);
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
            return true;

        }
        return super.onOptionsItemSelected(item);
    }


    public void RegistrarConsumo(View view) {
        Intent intent = new Intent(this, RegistrarConsumoActivity.class);
        startActivityForResult(intent, 1);
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            mostrarDatosHome(); // Recarga los datos
        }
    }

    private void mostrarDatosHome() {
        SharedPreferences prefs = getSharedPreferences("usuario", MODE_PRIVATE);
        int userId = prefs.getInt("id", -1);

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // 1. Objetivo diario
        Cursor c = db.rawQuery("SELECT objetivo_diario_ml FROM datos_usuario WHERE usuario_id = ?", new String[]{String.valueOf(userId)});
        int objetivo = 0;
        if (c.moveToFirst() && !c.isNull(0)) {
            objetivo = c.getInt(0);
        }
        c.close();
        tvObjetivo.setText(objetivo + " ml");

        // 2. Consumo del día
        String hoy = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date());

        Cursor c2 = db.rawQuery("SELECT SUM(cantidad_ml) FROM consumo WHERE usuario_id = ? AND fecha = ?", new String[]{String.valueOf(userId), hoy});
        int consumo = 0;
        if (c2.moveToFirst() && !c2.isNull(0)) {
            consumo = c2.getInt(0);
        }
        c2.close();
        tvConsumo.setText(String.valueOf(consumo));
        //fechaActual.setText(hoy);



        // 3. Próximo recordatorio
        String horaActual = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());

        Cursor c3 = db.rawQuery(
                "SELECT hora FROM recordatorios WHERE usuario_id = ? AND hora > ? ORDER BY hora ASC LIMIT 1",
                new String[]{String.valueOf(userId), horaActual}
        );

        String proximo = "No hay más recordatorios hoy";
        if (c3.moveToFirst() && c3.getString(0) != null) {
            String fechaCompleta = c3.getString(0);
            try {
                Date fecha = new SimpleDateFormat("HH:mm", Locale.getDefault()).parse(fechaCompleta);
                proximo = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(fecha);
            } catch (Exception e) {
                proximo = fechaCompleta; // en caso de error, mostrar la fecha cruda
            }
        }
        c3.close();

        tvProxRecordatorio.setText("Próximo recordatorio: " + proximo);

    }

    public void reload(View view) {
        mostrarDatosHome();
    }
}