package sv.edu.catolica.drinkupgrupo06;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class GraficoActivity extends AppCompatActivity{
    private BarChart barChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grafico);

        barChart = findViewById(R.id.barChart);

        int mes = getIntent().getIntExtra("mes", -1); // mes recibido (0-based)
        int anio = getIntent().getIntExtra("anio", -1);

        if (mes != -1 && anio != -1) {
            cargarDatosConsumo(mes + 1, anio); // mes +1 porque en SQLite es 1-based
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }

    private void cargarDatosConsumo(int mes, int anio) {
        SharedPreferences prefs = getSharedPreferences("usuario", MODE_PRIVATE);
        int usuarioId = prefs.getInt("id", -1);
        if (usuarioId == -1) return;

        DataBase dbHelper = new DataBase(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT fecha, SUM(cantidad_ml) as total FROM consumo " +
                        "WHERE usuario_id = ? AND strftime('%m', fecha) = ? AND strftime('%Y', fecha) = ? " +
                        "GROUP BY fecha",
                new String[]{
                        String.valueOf(usuarioId),
                        String.format("%02d", mes),
                        String.valueOf(anio)
                });

        ArrayList<BarEntry> entradas = new ArrayList<>();
        ArrayList<String> etiquetasDias = new ArrayList<>();

        HashMap<Integer, Integer> mapaDiaCantidad = new HashMap<>();

        while (cursor.moveToNext()) {
            String fecha = cursor.getString(0);
            int cantidad = cursor.getInt(1);
            int dia = Integer.parseInt(fecha.substring(8)); // YYYY-MM-DD → DD

            mapaDiaCantidad.put(dia, cantidad);
        }

        cursor.close();
        db.close();

        // Asegurarse de mostrar todos los días del mes, aunque estén vacíos
        Calendar calendario = Calendar.getInstance();
        calendario.set(anio, mes - 1, 1); // Calendar usa mes 0-based
        int diasEnMes = calendario.getActualMaximum(Calendar.DAY_OF_MONTH);

        for (int dia = 1; dia <= diasEnMes; dia++) {
            int cantidad = mapaDiaCantidad.getOrDefault(dia, 0);
            entradas.add(new BarEntry(dia, cantidad));
            etiquetasDias.add(String.valueOf(dia));
        }

        BarDataSet dataSet = new BarDataSet(entradas, "Consumo diario de agua (ml)");
        dataSet.setColor(Color.parseColor("#2196F3")); // Azul
        dataSet.setValueTextSize(12f);
        dataSet.setValueTextColor(Color.DKGRAY);

        BarData barData = new BarData(dataSet);
        barChart.setData(barData);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(etiquetasDias));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setLabelRotationAngle(-45);

        YAxis yAxis = barChart.getAxisLeft();
        yAxis.setAxisMinimum(0f);
        barChart.getAxisRight().setEnabled(false);

        barChart.getDescription().setEnabled(false);
        barChart.setFitBars(true);
        barChart.animateY(1000);
        barChart.invalidate();
    }
}
