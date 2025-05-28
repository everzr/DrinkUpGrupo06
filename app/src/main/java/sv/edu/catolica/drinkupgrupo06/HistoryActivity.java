package sv.edu.catolica.drinkupgrupo06;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HistoryActivity extends HomeActivity {
    private MaterialCalendarView calendarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.historial);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        calendarView = findViewById(R.id.calendar_view);

        Button btnGenerarGrafico = findViewById(R.id.btnGenerarGrafico);
        btnGenerarGrafico.setOnClickListener(v -> {
            int mes = calendarView.getCurrentDate().getMonth(); // Mes actual (0-based)
            int anio = calendarView.getCurrentDate().getYear(); // Año actual

            Intent intent = new Intent(HistoryActivity.this, GraficoActivity.class);
            intent.putExtra("mes", mes);
            intent.putExtra("anio", anio);
            startActivity(intent);
        });

        calendarView.setOnDateChangedListener((widget, date, selected) -> {
            String fechaSeleccionada = String.format("%04d-%02d-%02d",
                    date.getYear(), date.getMonth() + 1, date.getDay());

            mostrarResumenDelDia(fechaSeleccionada);
        });



        SharedPreferences prefs = getSharedPreferences("usuario", MODE_PRIVATE);
        int usuarioId = prefs.getInt("id", -1);
        if (usuarioId == -1) return;

        DataBase dbHelper = new DataBase(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        int objetivoDiario = 0;
        Cursor cursorObjetivo = db.rawQuery(
                "SELECT objetivo_diario_ml FROM datos_usuario WHERE usuario_id = ?",
                new String[]{String.valueOf(usuarioId)}
        );
        if (cursorObjetivo.moveToFirst()) {
            objetivoDiario = cursorObjetivo.getInt(0);
        }
        cursorObjetivo.close();

        Cursor cursor = db.rawQuery(
                "SELECT fecha, SUM(cantidad_ml) FROM consumo WHERE usuario_id = ? GROUP BY fecha",
                new String[]{String.valueOf(usuarioId)}
        );

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        while (cursor.moveToNext()) {
            String fechaStr = cursor.getString(0);
            int cantidad = cursor.getInt(1);

            try {
                Date fecha = sdf.parse(fechaStr);
                int color = (cantidad >= objetivoDiario)
                        ? Color.parseColor("#2196F3")  // Azul
                        : Color.parseColor("#FFA500"); // Anaranjado

                calendarView.addDecorator(new DayDecorator(fecha, color));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        cursor.close();
        db.close();
    }

    private void mostrarResumenDelDia(String fecha) {
        Intent intent = new Intent(this, ResumenDiaActivity.class);
        intent.putExtra("fecha", fecha);
        startActivity(intent);
    }
}
