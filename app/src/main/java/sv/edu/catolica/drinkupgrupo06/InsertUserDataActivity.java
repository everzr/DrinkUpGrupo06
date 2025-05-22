package sv.edu.catolica.drinkupgrupo06;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
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
        numberPickerEdad.setMinValue(1);
        numberPickerEdad.setMaxValue(120);

        // Configurar NumberPicker para el peso
        numberPickerPeso = findViewById(R.id.numberPickerPeso);
        numberPickerPeso.setMinValue(30);
        numberPickerPeso.setMaxValue(200);

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

        // Obtener valores de los Spinner
        String genero = spinnerGenero.getSelectedItem().toString();
        String actividad = spinnerActividad.getSelectedItem().toString();

        // Validar que los valores no estén vacíos (aunque no es necesario con NumberPicker y Spinner)
        if (genero.isEmpty() || actividad.isEmpty()) {
            Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        int usuarioId = getSharedPreferences("usuario", MODE_PRIVATE).getInt("id", -1);
        if (usuarioId == -1) {
            Toast.makeText(this, "Error al obtener el usuario", Toast.LENGTH_SHORT).show();
            return;
        }

        values.put("usuario_id", usuarioId);
        values.put("edad", edad);
        values.put("genero", genero);
        values.put("peso", peso);
        values.put("actividad_fisica", actividad);

        long result = db.insert("datos_usuario", null, values);
        if (result != -1) {
            Toast.makeText(this, "Datos guardados correctamente", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        } else {
            Toast.makeText(this, "Error al guardar los datos", Toast.LENGTH_SHORT).show();
        }
    }
}