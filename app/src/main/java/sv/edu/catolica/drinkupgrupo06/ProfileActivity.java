package sv.edu.catolica.drinkupgrupo06;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ProfileActivity extends HomeActivity {

    private EditText editName, editEmail, editAge, editWeight;
    private Spinner spinnerGender, spinnerActivity, spinnerObjetivo;
    private Button btnEdit, btnSave;
    private SharedPreferences prefs;
    private DataBase dbHelper;
    private int userId;
    private String[] genderOptions, activityOptions;
    private int objetivoAnterior = 0;
    private int[] opcionesObjetivo;
    private String[] opcionesObjetivoStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        editName = findViewById(R.id.edit_name);
        editEmail = findViewById(R.id.edit_email);
        editAge = findViewById(R.id.edit_age);
        editWeight = findViewById(R.id.edit_weight);
        spinnerGender = findViewById(R.id.spinner_gender);
        spinnerActivity = findViewById(R.id.spinner_activity);
        spinnerObjetivo = findViewById(R.id.spinner_objetivo);
        btnEdit = findViewById(R.id.btn_edit);
        btnSave = findViewById(R.id.btn_save);

        genderOptions = getResources().getStringArray(R.array.genero_array);
        activityOptions = getResources().getStringArray(R.array.actividad_array);

        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, genderOptions);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGender.setAdapter(genderAdapter);

        ArrayAdapter<String> activityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, activityOptions);
        activityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerActivity.setAdapter(activityAdapter);

        prefs = getSharedPreferences("usuario", MODE_PRIVATE);
        dbHelper = new DataBase(this);

        userId = prefs.getInt("id", -1);
        if (userId != -1) {
            cargarDatos(userId);
        }

        editEmail.setEnabled(false);
        editName.setEnabled(false);
        editAge.setEnabled(false);
        editWeight.setEnabled(false);
        spinnerGender.setEnabled(false);
        spinnerActivity.setEnabled(false);
        spinnerObjetivo.setEnabled(false);

        btnEdit.setOnClickListener(v -> {
            editName.setEnabled(true);
            editAge.setEnabled(true);
            editWeight.setEnabled(true);
            spinnerGender.setEnabled(true);
            spinnerActivity.setEnabled(true);
            spinnerObjetivo.setEnabled(true);
            btnEdit.setVisibility(View.GONE);
            btnSave.setVisibility(View.VISIBLE);
        });

        btnSave.setOnClickListener(v -> {
            String nombre = editName.getText().toString().trim();
            String edadStr = editAge.getText().toString().trim();
            String pesoStr = editWeight.getText().toString().trim();
            String genero = spinnerGender.getSelectedItem().toString();
            String actividad = spinnerActivity.getSelectedItem().toString();
            int objetivoSeleccionado = opcionesObjetivo[spinnerObjetivo.getSelectedItemPosition()];

            if (nombre.isEmpty() || edadStr.isEmpty() || pesoStr.isEmpty()) {
                Toast.makeText(this, R.string.completa_todos_profile, Toast.LENGTH_SHORT).show();
                return;
            }
            int edad;
            try {
                edad = Integer.parseInt(edadStr);
                if (edad <= 10 || edad > 120) {
                    Toast.makeText(this, R.string.ingresa_una_edad, Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, R.string.edad_debe_ser_profile, Toast.LENGTH_SHORT).show();
                return;
            }

            int peso;
            try {
                peso = Integer.parseInt(pesoStr);
                if (peso <= 30 || peso > 500) {
                    Toast.makeText(this, R.string.ingresa_un_peso, Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, R.string.peso_debe_ser_un_n_mero_v_lido_profile, Toast.LENGTH_SHORT).show();
                return;
            }

            if (userId != -1) {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                db.execSQL("UPDATE usuarios SET nombre=? WHERE id=?", new Object[]{nombre, userId});
                db.execSQL("UPDATE datos_usuario SET edad=?, genero=?, peso=?, actividad_fisica=?, objetivo_diario_ml=? WHERE usuario_id=?",
                        new Object[]{edad, genero, peso, actividad, objetivoSeleccionado, userId});
            }

            prefs.edit()
                    .putString(getString(R.string.nombre_profile), nombre)
                    .putString(getString(R.string.edad_profile), edadStr)
                    .putString(getString(R.string.genero_profile), genero)
                    .putString(getString(R.string.peso_profile), pesoStr)
                    .putString(getString(R.string.actividad_fisica_profile), actividad)
                    .putInt(getString(R.string.objetivo_diario_ml_profile), objetivoSeleccionado)
                    .apply();

            editName.setEnabled(false);
            editAge.setEnabled(false);
            editWeight.setEnabled(false);
            spinnerGender.setEnabled(false);
            spinnerActivity.setEnabled(false);
            spinnerObjetivo.setEnabled(false);
            btnEdit.setVisibility(View.VISIBLE);
            btnSave.setVisibility(View.GONE);

            objetivoAnterior = objetivoSeleccionado;

            Toast.makeText(this, R.string.datos_actualizados_profile, Toast.LENGTH_SHORT).show();
        });
    }

    private void cargarDatos(int userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT u.nombre, u.correo, d.edad, d.genero, d.peso, d.actividad_fisica, d.objetivo_diario_ml " +
                        "FROM usuarios u " +
                        "LEFT JOIN datos_usuario d ON u.id = d.usuario_id " +
                        "WHERE u.id = ?",
                new String[]{String.valueOf(userId)}
        );
        if (cursor.moveToFirst()) {
            editName.setText(cursor.getString(0));
            editEmail.setText(cursor.getString(1));
            editAge.setText(cursor.getString(2));
            editWeight.setText(cursor.getString(4));
            String genero = cursor.getString(3);
            String actividad = cursor.getString(5);
            objetivoAnterior = cursor.getInt(6);

            if (genero != null) {
                int genderPos = java.util.Arrays.asList(genderOptions).indexOf(genero);
                spinnerGender.setSelection(genderPos >= 0 ? genderPos : 0);
            }
            if (actividad != null) {
                int activityPos = java.util.Arrays.asList(activityOptions).indexOf(actividad);
                spinnerActivity.setSelection(activityPos >= 0 ? activityPos : 0);
            }
            cargarOpcionesObjetivo();
            spinnerObjetivo.setEnabled(false);
            btnSave.setVisibility(View.GONE);
            btnEdit.setVisibility(View.VISIBLE);
        }
        cursor.close();
    }

    private void cargarOpcionesObjetivo() {
        int min = 1000, max = 4000, paso = 50;
        int total = ((max - min) / paso) + 1;
        opcionesObjetivo = new int[total];
        opcionesObjetivoStr = new String[total];
        int seleccionado = 0;
        for (int i = 0, ml = min; ml <= max; i++, ml += paso) {
            opcionesObjetivo[i] = ml;
            opcionesObjetivoStr[i] = ml + " ml";
            if (ml == objetivoAnterior) seleccionado = i;
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, opcionesObjetivoStr);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerObjetivo.setAdapter(adapter);
        spinnerObjetivo.setSelection(seleccionado);
    }
}