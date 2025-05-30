package sv.edu.catolica.drinkupgrupo06;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class InsertUserDataActivity extends AppCompatActivity {
    private EditText editTextEdad, editTextGenero, editTextPeso, editTextActividad;
    private NumberPicker numberPickerEdad, numberPickerPeso;
    private Spinner spinnerGenero, spinnerActividad;
    private Button btnGuardarDatos;
    private DataBase dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.insert_user_data);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left + v.getPaddingLeft(), systemBars.top, systemBars.right + v.getPaddingRight(), systemBars.bottom + v.getPaddingBottom());
            return insets;
        });

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Configurar NumberPicker para la edad
        numberPickerEdad = findViewById(R.id.numberPickerEdad);
        numberPickerEdad.setMinValue(10);
        numberPickerEdad.setMaxValue(120);

        // Configurar NumberPicker para el peso
        numberPickerPeso = findViewById(R.id.numberPickerPeso);
        numberPickerPeso.setMinValue(30);
        numberPickerPeso.setMaxValue(500);

        // Configurar Spinner para el género
        spinnerGenero = findViewById(R.id.spinnerGenero);
        ArrayAdapter<CharSequence> adapterGenero = ArrayAdapter.createFromResource(this,
                R.array.genero_array, android.R.layout.simple_spinner_item);
        adapterGenero.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGenero.setAdapter(adapterGenero);

        // Configurar Spinner para el tipo de actividad física
        spinnerActividad = findViewById(R.id.spinnerActividad);
        ArrayAdapter<CharSequence> adapterActividad = ArrayAdapter.createFromResource(this,
                R.array.actividad_array, android.R.layout.simple_spinner_item);
        adapterActividad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerActividad.setAdapter(adapterActividad);


        btnGuardarDatos = findViewById(R.id.btnGuardarDatos);

        dbHelper = new DataBase(this);

    }


    public void GuardarDatos(View view) {
        int edad = numberPickerEdad.getValue();
        int peso = numberPickerPeso.getValue();
        String genero = spinnerGenero.getSelectedItem().toString();
        String actividad = spinnerActividad.getSelectedItem().toString();

        if (genero.isEmpty() || actividad.isEmpty()) {
            Toast.makeText(this, R.string.por_favor_completa_insert, Toast.LENGTH_SHORT).show();
            return;
        }

        int objetivoDiario = calcularObjetivoDiario(edad, peso, actividad); // Valor sugerido
        final int[] opciones = {
                Math.max(objetivoDiario - 250, 1000), // mínimo de seguridad
                objetivoDiario,
                Math.min(objetivoDiario + 250, 4000)  // máximo recomendado
        };
        String[] opcionesStr = {opciones[0] + " ml", opciones[1] + " ml", opciones[2] + " ml"};

        // Inflar el layout personalizado para el diálogo
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_objetivo_diario, null);
        Spinner spinnerObjetivo = dialogView.findViewById(R.id.spinnerObjetivo);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, opcionesStr);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerObjetivo.setAdapter(adapter);
        spinnerObjetivo.setSelection(1);

        new AlertDialog.Builder(this)
                .setTitle(R.string.objetivo_diario_calculado)
                .setMessage(getString(R.string.tu_objetivo_diario_recomendado_es) + objetivoDiario + getString(R.string.ml_deseas_cambiarlo) +
                        getString(R.string.advertencia_el_objetivo_max))
                .setView(dialogView)
                .setPositiveButton(R.string.aceptar_insert, (dialog, which) -> {
                    int objetivoSeleccionado = opciones[spinnerObjetivo.getSelectedItemPosition()];

                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    ContentValues values = new ContentValues();

                    int usuarioId = getSharedPreferences("usuario", MODE_PRIVATE).getInt("id", -1);
                    if (usuarioId == -1) {
                        Toast.makeText(this, R.string.error_al_obtener_el_usuario_insert, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    values.put("usuario_id", usuarioId);
                    values.put("edad", edad);
                    values.put("genero", genero);
                    values.put("peso", peso);
                    values.put("actividad_fisica", actividad);
                    values.put("objetivo_diario_ml", objetivoSeleccionado);

                    long result = db.insert("datos_usuario", null, values);
                    if (result != -1) {
                        Toast.makeText(this, R.string.datos_guardados_insert, Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, HomeActivity.class));
                        finish();
                    } else {
                        Toast.makeText(this, R.string.error_al_guardar_insert, Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(R.string.cancelar_insert, null)
                .show();
    }

    private int calcularObjetivoDiario(int edad, int peso, String actividad) {
        double objetivo = peso * 35; // Base: 35 ml por kg

        // Ajuste por actividad física
        switch (actividad.toLowerCase()) {
            case "intenso":
                objetivo += 1000; // +1L para entrenamientos duros o trabajos físicos pesados
                break;
            case "moderado":
                objetivo += 700; // +700 ml para ejercicio regular
                break;
            case "ligero":
                objetivo += 400; // +400 ml para caminatas o tareas físicas suaves
                break;
            case "sedentario":
            default:
                break; // Sin incremento
        }

        // Ajuste por edad (si quieres aplicar alguno)
        if (edad > 60) {
            objetivo -= 250; // Ajuste leve para adultos mayores
        }

        // Límite superior para evitar excesos peligrosos
        if (objetivo > 4000) objetivo = 4000;

        return (int)objetivo;
    }
}