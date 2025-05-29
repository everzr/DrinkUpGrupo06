package sv.edu.catolica.drinkupgrupo06;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ResumenDiaActivity extends AppCompatActivity {
    private static final int LIMITE_CARACTERES = 100;

    private TextView txtFecha, txtObjetivo, txtTotalConsumido, txtEstado, txtNota;
    private String fecha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resumen_dia);

        inicializarVistas();
        configurarToolbar();

        fecha = getIntent().getStringExtra("fecha");
        txtFecha.setText(getString(R.string.fecha_resumen_dia) + fecha);

        ImageButton btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> finish());

        cargarDatosResumen();
        mostrarNotaGuardada();

        configurarBotones();
    }

    private void inicializarVistas() {
        txtFecha = findViewById(R.id.txtFecha);
        txtObjetivo = findViewById(R.id.txtObjetivo);
        txtTotalConsumido = findViewById(R.id.txtTotalConsumido);
        txtEstado = findViewById(R.id.txtEstado);
        txtNota = findViewById(R.id.txtNota);
    }

    private void configurarToolbar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }

    private void configurarBotones() {
        FloatingActionButton btnAgregarNota = findViewById(R.id.btnAgregarNota);
        Button btnEditarNota = findViewById(R.id.btnEditarNota);

        btnAgregarNota.setOnClickListener(v -> mostrarDialogoNota());
        btnEditarNota.setOnClickListener(v -> mostrarDialogoEditarNota());
    }

    private void cargarDatosResumen() {
        DataBase dbHelper = new DataBase(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        SharedPreferences preferences = getSharedPreferences("usuario", MODE_PRIVATE);
        int usuarioId = preferences.getInt("id", -1);

        int totalConsumido = obtenerTotalConsumido(db, usuarioId);
        int objetivo = obtenerObjetivoDiario(db, usuarioId);
        db.close();

        String estado = totalConsumido == 0 ? getString(R.string.no_iniciado) :
                totalConsumido >= objetivo ? getString(R.string.completado) : getString(R.string.incompleto);

        txtObjetivo.setText(getString(R.string.objetivo_resumen_dia) + objetivo + " ml");
        txtTotalConsumido.setText(getString(R.string.total_consumido_resumen_dia) + totalConsumido + " ml");
        txtEstado.setText(getString(R.string.estado_resumen_dia) + estado);
    }

    private int obtenerTotalConsumido(SQLiteDatabase db, int usuarioId) {
        Cursor cursor = db.rawQuery(
                "SELECT SUM(cantidad_ml) FROM consumo WHERE usuario_id = ? AND fecha = ?",
                new String[]{String.valueOf(usuarioId), fecha}
        );
        int total = 0;
        if (cursor.moveToFirst()) {
            total = cursor.getInt(0);
        }
        cursor.close();
        return total;
    }

    private int obtenerObjetivoDiario(SQLiteDatabase db, int usuarioId) {
        Cursor cursor = db.rawQuery(
                "SELECT objetivo_diario_ml FROM datos_usuario WHERE usuario_id = ?",
                new String[]{String.valueOf(usuarioId)}
        );
        int objetivo = 0;
        if (cursor.moveToFirst()) {
            objetivo = cursor.getInt(0);
        }
        cursor.close();
        return objetivo;
    }

    private void mostrarDialogoNota() {
        final EditText input = new EditText(this);
        input.setHint(R.string.escribe_tu_nota_aqu);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.agregar_nota)
                .setView(input)
                .setPositiveButton(R.string.guardar, null)
                .setNegativeButton(R.string.cancelar_resumen_dia, null)
                .create();

        dialog.setOnShowListener(dialogInterface -> {
            Button botonGuardar = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            botonGuardar.setOnClickListener(v -> {
                String nuevaNota = input.getText().toString().trim();

                if (nuevaNota.isEmpty()) {
                    input.setError(getString(R.string.la_nota_no_vacia));
                } else if (nuevaNota.length() > LIMITE_CARACTERES) {
                    mostrarAlertaLimite();
                    nuevaNota = nuevaNota.substring(0, LIMITE_CARACTERES);
                    input.setText(nuevaNota);
                    input.setSelection(nuevaNota.length());
                } else {
                    String notaActual = obtenerNotaGuardada();
                    nuevaNota = "• " + nuevaNota;
                    String notaActualizada = notaActual.isEmpty() ? nuevaNota : notaActual + "\n" + nuevaNota;

                    guardarNota(notaActualizada);
                    txtNota.setText(notaActualizada);
                    dialog.dismiss();
                }
            });
        });

        dialog.show();
    }

    private void mostrarDialogoEditarNota() {
        String notaActual = obtenerNotaGuardada();

        final EditText input = new EditText(this);
        input.setText(notaActual);
        input.setSelection(notaActual.length());

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.editar_nota_resumen_dia)
                .setView(input)
                .setPositiveButton(R.string.guardar_resumen_dia, null)
                .setNegativeButton(R.string.cancelar_resumen_dia_edit, null)
                .create();

        dialog.setOnShowListener(dialogInterface -> {
            Button botonGuardar = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            botonGuardar.setOnClickListener(v -> {
                String notaEditada = input.getText().toString().trim();

                if (notaEditada.isEmpty()) {
                    input.setError(getString(R.string.la_nota_no_vacia_edit));
                } else if (notaEditada.length() > LIMITE_CARACTERES) {
                    mostrarAlertaLimite();
                    notaEditada = notaEditada.substring(0, LIMITE_CARACTERES);
                    input.setText(notaEditada);
                    input.setSelection(notaEditada.length());
                } else {
                    guardarNota(notaEditada);
                    txtNota.setText(notaEditada);
                    dialog.dismiss();
                }
            });
        });

        dialog.show();
    }



    private void guardarNota(String nota) {
        SharedPreferences prefs = getSharedPreferences("notas", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("nota_" + fecha, nota);
        editor.apply();
    }

    private void mostrarNotaGuardada() {
        String nota = obtenerNotaGuardada();
        if (!nota.isEmpty()) {
            txtNota.setText(nota);
        }
    }

    private String obtenerNotaGuardada() {
        SharedPreferences prefs = getSharedPreferences("notas", MODE_PRIVATE);
        return prefs.getString("nota_" + fecha, "");
    }

    private void mostrarAlertaLimite() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.l_mite_alcanzado)
                .setMessage(getString(R.string.la_nota_no_puede_exceder_los_r) + LIMITE_CARACTERES + getString(R.string.caracteres))
                .setPositiveButton("OK", null)
                .show();
    }
}
