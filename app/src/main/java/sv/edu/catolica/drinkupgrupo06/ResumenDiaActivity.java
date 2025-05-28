package sv.edu.catolica.drinkupgrupo06;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ResumenDiaActivity extends AppCompatActivity {
    private TextView txtFecha, txtObjetivo, txtTotalConsumido, txtEstado, txtNota;
    private String fecha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resumen_dia);

        txtFecha = findViewById(R.id.txtFecha);
        txtObjetivo = findViewById(R.id.txtObjetivo);
        txtTotalConsumido = findViewById(R.id.txtTotalConsumido);
        txtEstado = findViewById(R.id.txtEstado);
        txtNota = findViewById(R.id.txtNota);
        FloatingActionButton btnAgregarNota = findViewById(R.id.btnAgregarNota);

        fecha = getIntent().getStringExtra("fecha");
        txtFecha.setText("Fecha: " + fecha);

        cargarDatosResumen();
        mostrarNotaGuardada();

        btnAgregarNota.setOnClickListener(v -> mostrarDialogoNota());

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }

    private void cargarDatosResumen() {
        DataBase dbHelper = new DataBase(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        SharedPreferences preferences = getSharedPreferences("usuario", MODE_PRIVATE);
        int usuarioId = preferences.getInt("id", -1);

        Cursor cursor = db.rawQuery("SELECT SUM(cantidad_ml) FROM consumo WHERE usuario_id = ? AND fecha = ?",
                new String[]{String.valueOf(usuarioId), fecha});

        int totalConsumido = 0;
        if (cursor.moveToFirst()) {
            totalConsumido = cursor.getInt(0);
        }
        cursor.close();

        int objetivo = 0;
        Cursor cursorObjetivo = db.rawQuery("SELECT objetivo_diario_ml FROM datos_usuario WHERE usuario_id = ?",
                new String[]{String.valueOf(usuarioId)});
        if (cursorObjetivo.moveToFirst()) {
            objetivo = cursorObjetivo.getInt(0);
        }
        cursorObjetivo.close();
        db.close();

        String estado = totalConsumido == 0 ? "No iniciado" :
                totalConsumido >= objetivo ? "Completado" : "Incompleto";

        txtObjetivo.setText("Objetivo: " + objetivo + " ml");
        txtTotalConsumido.setText("Total consumido: " + totalConsumido + " ml");
        txtEstado.setText("Estado: " + estado);
    }

    private void mostrarDialogoNota() {
        final EditText input = new EditText(this);
        input.setHint("Escribe tu nota aquí");

        new AlertDialog.Builder(this)
                .setTitle("Agregar nota")
                .setView(input)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    String nuevaNota = input.getText().toString().trim();
                    if (!nuevaNota.isEmpty()) {
                        SharedPreferences prefs = getSharedPreferences("notas", MODE_PRIVATE);
                        String notaAnterior = prefs.getString("nota_" + fecha, "");

                        // Formatear nueva nota con viñeta
                        nuevaNota = "• " + nuevaNota;

                        // Acumular nota
                        String notaActualizada = notaAnterior.isEmpty()
                                ? nuevaNota
                                : notaAnterior + "\n" + nuevaNota;

                        guardarNota(notaActualizada);
                        txtNota.setText(notaActualizada);
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }



    private void guardarNota(String nota) {
        SharedPreferences prefs = getSharedPreferences("notas", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("nota_" + fecha, nota);
        editor.apply();
    }

    private void mostrarNotaGuardada() {
        SharedPreferences prefs = getSharedPreferences("notas", MODE_PRIVATE);
        String notaGuardada = prefs.getString("nota_" + fecha, "");
        if (!notaGuardada.isEmpty()) {
            txtNota.setText(notaGuardada);
        }
    }

}
