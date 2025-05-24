package sv.edu.catolica.drinkupgrupo06;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigation;

    private String perfilActivo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences prefs = getSharedPreferences("perfil_sesion", MODE_PRIVATE);
        perfilActivo = prefs.getString("nombre", null);
        if (perfilActivo == null) {
            Log.e("HomeActivity", "PERFIL ACTIVO ES NULL. REDIRIGIENDO.");
        } else {
            Log.d("HomeActivity", "PERFIL CARGADO: " + perfilActivo);
        }


        if (perfilActivo == null) {
            finish(); // Si no hay sesión, cerramos la actividad
            return;
        }

        Log.d("HomeActivity", "Perfil activo leído: " + perfilActivo);

        bottomNavigation = findViewById(R.id.bottomNavigation);

        // Cargar fragmento por defecto con argumentos
        Fragment fragmentInicio = new HomeFragment();
        Bundle args = new Bundle();
        args.putString("perfil", perfilActivo);
        fragmentInicio.setArguments(args);
        cargarFragmento(fragmentInicio);

        bottomNavigation.setOnItemSelectedListener(item -> {
            Fragment fragment = null;
            Bundle argsFrag = new Bundle();
            argsFrag.putString("perfil", perfilActivo);

            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                fragment = new HomeFragment();
            } else if (itemId == R.id.nav_reminder) {
                fragment = new ReminderFragment();
            } else if (itemId == R.id.nav_history) {
                fragment = new HistoryFragment();
            } else if (itemId == R.id.nav_info) {
                fragment = new InfoFragment();
            }

            if (fragment != null) {
                fragment.setArguments(argsFrag);
                cargarFragmento(fragment);
                return true;
            }

            return false;
        });
    }

    private void cargarFragmento(Fragment fragmento) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragmento)
                .commit();
    }
}