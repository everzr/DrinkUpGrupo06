package sv.edu.catolica.drinkupgrupo06;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import java.util.List;

public class RecordatorioAdapter extends ArrayAdapter<Recordatorio> {

    public RecordatorioAdapter(Context context, List<Recordatorio> recordatorios) {
        super(context, 0, recordatorios);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Recordatorio recordatorio = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_recordatorio, parent, false);
        }
        Button btnEliminar = convertView.findViewById(R.id.btnEliminar);

        btnEliminar.setOnClickListener(v -> {
            if (getContext() instanceof ReminderActivity) {

                new AlertDialog.Builder(getContext())
                        .setTitle(R.string.eliminar_recoradapter)
                        .setMessage(R.string.deseas_eliminar_este_recordatorio)
                        .setPositiveButton(R.string.si, (dialog, which) -> {
                            ((ReminderActivity) getContext()).eliminarRecordatorio(recordatorio.id);
                        })
                        .setNegativeButton(R.string.cancelar_recoradapter, null)
                        .show();

            }
        });
        TextView tvHora = convertView.findViewById(R.id.tvHora);
        TextView tvEstado = convertView.findViewById(R.id.tvEstado);
        TextView tvCantidad = convertView.findViewById(R.id.tvCantidad);

        tvHora.setText(getContext().getString(R.string.hora_recoradapter) + recordatorio.hora);
        tvEstado.setText(getContext().getString(R.string.estado_recoradapter) + recordatorio.estado);
        tvCantidad.setText(getContext().getString(R.string.cantidad_recoradapter) + recordatorio.cantidad + " ml");

        return convertView;
    }
}
