package sv.edu.catolica.drinkupgrupo06;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.net.ParseException;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;


public class ReminderActivity extends HomeActivity {
    ListView listView;
    FloatingActionButton fab;
    DataBase dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        crearCanalNotificacion();
        EdgeToEdge.enable(this);
        setContentView(R.layout.reminder);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(v.getPaddingLeft(), systemBars.top, v.getPaddingRight(), systemBars.bottom);
            return insets;
        });


        listView = findViewById(R.id.listViewRecordatorios);
        fab = findViewById(R.id.fab);
        dbHelper = new DataBase(this);

        SharedPreferences prefs = getSharedPreferences("usuario", MODE_PRIVATE);
        int usuarioId = prefs.getInt("id", -1);

        if (usuarioId == -1) {
            Toast.makeText(this, R.string.error_usuario_no_identificado_reminder, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (esPrimeraVez(usuarioId)) {
            generarRecordatoriosIniciales(usuarioId);
        }

        cargarRecordatorios(usuarioId);

        validarYReprogramarRecordatorios(usuarioId);

        fab.setOnClickListener(v -> {
            CrearRecordatorio(v);
            Toast.makeText(this, R.string.agregar_recordatorio_reminder, Toast.LENGTH_SHORT).show();
        });

        listView.setOnItemClickListener((adapterView, view, position, id) -> {
            Recordatorio recordatorio = (Recordatorio) adapterView.getItemAtPosition(position);
            mostrarDialogoEditar(recordatorio);
        });


    }

    private boolean esPrimeraVez(int usuarioId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT COUNT(*) FROM recordatorios WHERE usuario_id = ?", new String[]{String.valueOf(usuarioId)});
        boolean primeraVez = true;
        if (c.moveToFirst()) {
            primeraVez = c.getInt(0) == 0;
        }
        c.close();
        return primeraVez;
    }

    private void generarRecordatoriosIniciales(int usuarioId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT objetivo_diario_ml FROM datos_usuario WHERE usuario_id = ?", new String[]{String.valueOf(usuarioId)});

        if (c.moveToFirst()) {
            int objetivo = c.getInt(0);
            int maxPorToma = 300;

            // 1. Calcular tomas necesarias
            int tomasNecesarias = (int) Math.ceil((double) objetivo / maxPorToma);

            // 2. Definir horario activo (puedes hacerlo configurable después)
            int horaInicio = 8; // 8:00 AM
            int horaFin = 22;   // 10:00 PM
            int horasDisponibles = horaFin - horaInicio;

            // 3. Evitar más recordatorios que horas disponibles
            int recordatoriosFinales = Math.min(tomasNecesarias, horasDisponibles);
            int cantidadPorToma = objetivo / recordatoriosFinales;

            // 4. Calcular intervalo entre recordatorios
            int intervalo = horasDisponibles / recordatoriosFinales;

            // 5. Generar los recordatorios
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, horaInicio);
            cal.set(Calendar.MINUTE, 0);

            for (int i = 0; i < recordatoriosFinales; i++) {
                String hora = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(cal.getTime());

                ContentValues values = new ContentValues();
                values.put("usuario_id", usuarioId);
                values.put("hora", hora);
                values.put("cantidad_ml", cantidadPorToma);

                long result = db.insert("recordatorios", null, values);
                if (result != -1) {
                    programarNotificacion(hora, cantidadPorToma, result); // Aquí se programa la notificación, el reult es el ID del recordatorio insertado
                    cal.add(Calendar.HOUR_OF_DAY, intervalo);
                }

            }
        }
        c.close();
    }


    private void cargarRecordatorios(int usuarioId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT id, hora, cantidad_ml FROM recordatorios WHERE usuario_id = ? ORDER BY hora ASC",
                new String[]{String.valueOf(usuarioId)});

        List<Recordatorio> lista = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String horaActualStr = sdf.format(new Date());

        try {
            Date horaActual = sdf.parse(horaActualStr);

            while (c.moveToNext()) {
                int id = c.getInt(0);
                String horaStr = c.getString(1);
                int cantidad = c.getInt(2);

                Date horaRecordatorio = sdf.parse(horaStr);
                String estado = horaRecordatorio.before(horaActual) ? getString(R.string.anterior_reminder) : getString(R.string.pr_ximo__reminder);

                lista.add(new Recordatorio(id, horaStr, estado, cantidad));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            c.close();
        }

        //ArrayAdapter<Recordatorio> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, lista);
        RecordatorioAdapter adapter = new RecordatorioAdapter(this, lista);
        listView.setAdapter(adapter);

    }

    private void programarNotificacion(String horaTexto, int cantidadMl, long idRecordatorio) {
        int idReminder = (int) idRecordatorio; // Asegurarse de que el ID sea un entero
        Calendar calendar = Calendar.getInstance();
        String[] partes = horaTexto.split(":");
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(partes[0]));
        calendar.set(Calendar.MINUTE, Integer.parseInt(partes[1]));
        calendar.set(Calendar.SECOND, 0);

        long tiempo = calendar.getTimeInMillis();
        if (tiempo < System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);  // Si la hora ya pasó hoy, empieza mañana
            tiempo = calendar.getTimeInMillis();
        }

        Intent intent = new Intent(this, NotificationReceiver.class);
        intent.putExtra(getString(R.string.hora_reminder), horaTexto);
        intent.putExtra(getString(R.string.cantidad_reminder), cantidadMl);
        intent.putExtra("idRecordatorio", idReminder); // Pasar el ID del recordatorio

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                idReminder,  //Id del recordatorio extraido de la bd ---- anates era textohora.hash ID único basado en la hora
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        tiempo,
                        pendingIntent
                );
            } else {
                alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        tiempo,
                        pendingIntent
                );
            }
        } catch (SecurityException e) {
            Log.e("AlarmError", "No se pudo programar la alarma", e);
        }
    }

    private void validarYReprogramarRecordatorios(int usuarioId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT id, hora, cantidad_ml FROM recordatorios WHERE usuario_id = ?", new String[]{String.valueOf(usuarioId)});
        while (c.moveToNext()) {
            int id = c.getInt(0);
            String hora = c.getString(1);
            int cantidad = c.getInt(2);

            // Verifica si la alarma está activa
            Intent intent = new Intent(this, NotificationReceiver.class);
            intent.putExtra(getString(R.string.hora_reminder), hora);
            intent.putExtra(getString(R.string.cantidad_reminder), cantidad);
            intent.putExtra("idRecordatorio", id);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    this,
                    id,
                    intent,
                    PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_IMMUTABLE
            );
            if (pendingIntent == null) {
                // No está activa, reprograma
                programarNotificacion(hora, cantidad, id);
            }
        }
        c.close();
    }

    private void crearCanalNotificacion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence nombre = getString(R.string.recordatorios_reminder);
            String descripcion = getString(R.string.notificaciones_para_beber_agua_reminder);
            int importancia = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel canal = new NotificationChannel("recordatorio_channel", nombre, importancia);
            canal.setDescription(descripcion);

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(canal);
        }
    }


    public void CrearRecordatorio(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        view = getLayoutInflater().inflate(R.layout.dialogo_agregar_recordatorio, null);
        builder.setView(view);

        NumberPicker npHora = view.findViewById(R.id.npHora);
        NumberPicker npMinuto = view.findViewById(R.id.npMinuto);
        NumberPicker npCantidad = view.findViewById(R.id.npCantidad);

        // Configurar rangos
        npHora.setMinValue(0);
        npHora.setMaxValue(23);
        npMinuto.setMinValue(0);
        npMinuto.setMaxValue(59);

        npCantidad.setMinValue(50);
        npCantidad.setMaxValue(300);
        npCantidad.setWrapSelectorWheel(false);
        npCantidad.setValue(125); // Valor por defecto

        builder.setPositiveButton(R.string.guardar_reminder, (dialog, which) -> {
            int hora = npHora.getValue();
            int minuto = npMinuto.getValue();
            int cantidad = npCantidad.getValue();

            String horaTexto = String.format(Locale.getDefault(), "%02d:%02d", hora, minuto);
            guardarRecordatorioEnBD(horaTexto, cantidad);
        });

        builder.setNegativeButton(R.string.cancelar_reminder, null);
        builder.show();
    }


    private void guardarRecordatorioEnBD(String horaTexto, int cantidadMl) {
        SharedPreferences prefs = getSharedPreferences("usuario", MODE_PRIVATE);
        int userId = prefs.getInt("id", -1);
        if (userId == -1) return;


        SQLiteDatabase db = new DataBase(this).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("usuario_id", userId);
        values.put("hora", horaTexto);
        values.put("cantidad_ml", cantidadMl);

        long result = db.insert("recordatorios", null, values);

        if (result != -1) {
            Toast.makeText(this, "Recordatorio agregado", Toast.LENGTH_SHORT).show();
            programarNotificacion(horaTexto, cantidadMl, result); // Aquí
            cargarRecordatorios(userId); // Recargar la lista de recordatorios
        } else {
            Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show();
        }
    }

    private void mostrarDialogoEditar(Recordatorio recordatorio) {
        // Inflate el layout personalizado
        View dialogView = getLayoutInflater().inflate(R.layout.dialogo_agregar_recordatorio, null);

        TextView tvTitulo = dialogView.findViewById(R.id.tvTitulo);
        NumberPicker npHora = dialogView.findViewById(R.id.npHora);
        NumberPicker npMinuto = dialogView.findViewById(R.id.npMinuto);
        NumberPicker npCantidad = dialogView.findViewById(R.id.npCantidad);

        // Cambiar título a "Editar Recordatorio"
        tvTitulo.setText(R.string.editar_recordatorio_reminder);

        // Configurar NumberPickers
        npHora.setMinValue(0);
        npHora.setMaxValue(23);

        npMinuto.setMinValue(0);
        npMinuto.setMaxValue(59);

        // Convertir la hora string a enteros
        String[] partesHora = recordatorio.hora.split(":");
        int hora = Integer.parseInt(partesHora[0]);
        int minuto = Integer.parseInt(partesHora[1]);

        npHora.setValue(hora);
        npMinuto.setValue(minuto);

        npCantidad.setMinValue(50);
        npCantidad.setMaxValue(300); // o lo que consideres máximo
        npCantidad.setValue(recordatorio.cantidad);

        new AlertDialog.Builder(this)
                .setView(dialogView)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    int nuevaHora = npHora.getValue();
                    int nuevoMinuto = npMinuto.getValue();

                    // Formatear nueva hora a HH:mm
                    String horaFormateada = String.format("%02d:%02d", nuevaHora, nuevoMinuto);
                    recordatorio.setHora(horaFormateada);
                    recordatorio.setCantidad(npCantidad.getValue());

                    actualizarRecordatorioEnBD(recordatorio.id, horaFormateada, recordatorio.cantidad);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    public void eliminarRecordatorio(int id) {


        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int filasEliminadas = db.delete("recordatorios", "id = ?", new String[]{String.valueOf(id)});

        if (filasEliminadas > 0) {
            Toast.makeText(this, R.string.recordatorio_eliminado_correctamente, Toast.LENGTH_SHORT).show();
            cancelarAlarma(id); // Cancelar la alarma antes de eliminar el recordatorio

            SharedPreferences prefs = getSharedPreferences("usuario", MODE_PRIVATE);
            int usuarioId = prefs.getInt("id", -1);
            cargarRecordatorios(usuarioId); // Recargar la lista
        } else {
            Toast.makeText(this, R.string.no_se_pudo_eliminar_el_recordatorio, Toast.LENGTH_SHORT).show();
        }
    }


    private void cancelarAlarma(int idRecordatorio) {
        Intent intent = new Intent(this, NotificationReceiver.class); // usa tu receiver
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                idRecordatorio, // debe ser el mismo que usaste al programar
                intent,
                PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }



    private void actualizarRecordatorioEnBD(int id, String horaTexto, int cantidadMl) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("hora", horaTexto);
        values.put("cantidad_ml", cantidadMl);

        int result = db.update("recordatorios", values, "id = ?", new String[]{String.valueOf(id)});
        if (result > 0) {
            Toast.makeText(this, R.string.recordatorio_actualizado, Toast.LENGTH_SHORT).show();

            // Reprogramar notificación
            cancelarAlarma(id); // Cancelar la alarma existente
            programarNotificacion(horaTexto, cantidadMl, id);

            // Volver a cargar la lista
            SharedPreferences prefs = getSharedPreferences("usuario", MODE_PRIVATE);
            int userId = prefs.getInt("id", -1);
            cargarRecordatorios(userId);
        } else {
            Toast.makeText(this, R.string.error_al_actualizar, Toast.LENGTH_SHORT).show();
        }
    }






}