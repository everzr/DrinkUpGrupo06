package sv.edu.catolica.drinkupgrupo06;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RegistrarConsumoActivity extends AppCompatActivity {

    private int[] diseños = {R.drawable.ic_vaso, R.drawable.ic_jarra, R.drawable.ic_botella, R.drawable.ic_taza};
    private String[] nombresDiseños = {"Vaso", "Jarra", "Botella", "Taza"};
    private int diseñoSeleccionado = 0;

    private FrameLayout designContainer;
    private NumberPicker numberCantidad;
    private ImageView diseñoView;
    View rellenoCeleste;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registrar_consumo);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, v.getPaddingTop(), systemBars.right, systemBars.bottom);
            return insets;
        });


        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        SharedPreferences prefs = getSharedPreferences("usuario", MODE_PRIVATE);
        diseñoSeleccionado = prefs.getInt("diseñoSeleccionado", 0); // 0 es el valor por defecto

        designContainer = findViewById(R.id.design_container);
        diseñoView = new ImageView(this);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        );
        diseñoView.setLayoutParams(params);
        diseñoView.setScaleType(ImageView.ScaleType.FIT_CENTER); // O CENTER_INSIDE
        designContainer.addView(diseñoView);

        rellenoCeleste = findViewById(R.id.rellenoCeleste);
        FrameLayout designContainer = findViewById(R.id.design_container);

        actualizarDiseño();

        findViewById(R.id.btn_settings).setOnClickListener(v -> mostrarDialogoDiseños());

        findViewById(R.id.btn_settings).setOnClickListener(v -> mostrarDialogoDiseños());

        numberCantidad = findViewById(R.id.number_cantidad);
        int[] cantidades = {50, 100, 150, 200, 250, 300, 350, 400, 500};
        String[] cantidadesStr = new String[cantidades.length];
        for (int i = 0; i < cantidades.length; i++) {
            cantidadesStr[i] = String.valueOf(cantidades[i]);
        }
        numberCantidad.setMinValue(0);
        numberCantidad.setMaxValue(cantidades.length - 1);
        numberCantidad.setDisplayedValues(cantidadesStr);
        numberCantidad.setWrapSelectorWheel(false);

        numberCantidad.setOnValueChangedListener((picker, oldVal, newVal) -> {
            actualizarCantidadEnDiseño(cantidades[newVal]);
        });

        findViewById(R.id.btn_registrar).setOnClickListener(v -> {
            registrarConsumoEnBD();
        });

    }


    private void mostrarDialogoDiseños() {
        new AlertDialog.Builder(this)
                .setTitle("Elige un diseño")
                .setSingleChoiceItems(nombresDiseños, diseñoSeleccionado, (dialog, which) -> {
                    diseñoSeleccionado = which;

                    SharedPreferences prefs = getSharedPreferences("usuario", MODE_PRIVATE);
                    prefs.edit().putInt("diseñoSeleccionado", diseñoSeleccionado).apply();

                    actualizarDiseño();
                    dialog.dismiss();
                })
                .show();
    }

    private void actualizarDiseño() {
        diseñoView.setImageResource(diseños[diseñoSeleccionado]);
        // Aquí podrías agregar lógica para cambiar el color o animación según la cantidad
    }

    private void actualizarCantidadEnDiseño(int cantidad) {
        int altura = designContainer.getHeight() - 130;
        int nivel = (int) (cantidad / 500f * altura);
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) rellenoCeleste.getLayoutParams();
        params.height = nivel;
        rellenoCeleste.setLayoutParams(params);
    }

    private void registrarConsumoEnBD() {
        SharedPreferences prefs = getSharedPreferences("usuario", MODE_PRIVATE);
        int usuarioId = prefs.getInt("id", -1);
        if (usuarioId == -1) {
            Toast.makeText(this, "Error: usuario no identificado", Toast.LENGTH_SHORT).show();
            return;
        }

        int[] cantidades = {50, 100, 150, 200, 250, 300, 350, 400, 500};
        int cantidad = cantidades[numberCantidad.getValue()];

        String fecha = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        DataBase dbHelper = new DataBase(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("usuario_id", usuarioId);
        values.put("fecha", fecha);
        values.put("cantidad_ml", cantidad);

        long result = db.insert("consumo", null, values);
        if (result != -1) {
            Toast.makeText(this, "Consumo registrado", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        } else {
            Toast.makeText(this, "Error al registrar consumo", Toast.LENGTH_SHORT).show();
        }

    }
}