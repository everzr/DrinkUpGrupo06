package sv.edu.catolica.drinkupgrupo06;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String hora = intent.getStringExtra("hora");
        int cantidad = intent.getIntExtra("cantidad", 0);

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
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("¡Hora de beber agua!")
                .setContentText("Ingiere " + cantidad + " ml. Programado para las " + hora)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        manager.notify((int) System.currentTimeMillis(), builder.build());
    }
}
