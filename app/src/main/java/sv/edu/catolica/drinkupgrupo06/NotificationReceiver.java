package sv.edu.catolica.drinkupgrupo06;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import java.util.Calendar;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String hora = intent.getStringExtra("hora");
        int cantidad = intent.getIntExtra("cantidad", 0);
        int idRecordatorio = intent.getIntExtra("idRecordatorio", -1);

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String canalId = "recordatorios_canal";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel canal = new NotificationChannel(
                    canalId,
                    "Canal de Recordatorios",
                    NotificationManager.IMPORTANCE_HIGH
            );
            canal.setDescription("Recordatorios para beber agua");
            manager.createNotificationChannel(canal);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, canalId)
                .setSmallIcon(R.drawable.logoapp)
                .setContentTitle("¡Hora de beber agua!")
                .setContentText("Ingiere " + cantidad + " ml. Programado para las " + hora)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.logoapp))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        manager.notify((int) System.currentTimeMillis(), builder.build());

        // Reprogramar la alarma para el día siguiente
        if (hora != null && idRecordatorio != -1) {
            String[] partes = hora.split(":");
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(partes[0]));
            calendar.set(Calendar.MINUTE, Integer.parseInt(partes[1]));
            calendar.set(Calendar.SECOND, 0);
            calendar.add(Calendar.DAY_OF_YEAR, 1);

            Intent newIntent = new Intent(context, NotificationReceiver.class);
            newIntent.putExtra("hora", hora);
            newIntent.putExtra("cantidad", cantidad);
            newIntent.putExtra("idRecordatorio", idRecordatorio);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context,
                    idRecordatorio,
                    newIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            try {
                if (alarmManager != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        alarmManager.setExactAndAllowWhileIdle(
                                AlarmManager.RTC_WAKEUP,
                                calendar.getTimeInMillis(),
                                pendingIntent
                        );
                    } else {
                        alarmManager.setExact(
                                AlarmManager.RTC_WAKEUP,
                                calendar.getTimeInMillis(),
                                pendingIntent
                        );
                    }
                }
            } catch (SecurityException e) {
                Log.e("AlarmError", "No se pudo programar la alarma", e);
            }

        }
    }
}