package sv.edu.catolica.drinkupgrupo06;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.splashscreen);


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Redirigir al Login después de 2 segundos
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashScreen.this, LoginActivity.class);
            startActivity(intent);
            finish(); // evita que el usuario regrese al splash
        }, 2000);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }
}