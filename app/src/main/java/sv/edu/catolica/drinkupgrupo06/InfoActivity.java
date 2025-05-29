package sv.edu.catolica.drinkupgrupo06;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class InfoActivity extends HomeActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.info);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(v.getPaddingLeft(), systemBars.top, v.getPaddingRight(), systemBars.bottom);
            return insets;
        });


    }
    public void tarjeta1Click(View view) {
        openDetailActivity(
                getString(R.string.titulo_card1_largo),
                getString(R.string.la_recomendaci_n_de_beber_8_vasos_de_agua) +
                        getString(R.string.peso_corporal_se_recomienda_35_ml_por_kg) +
                        getString(R.string.nivel_de_actividad_a_ade_500) +
                        getString(R.string.clima_en_climas_c_lidos) +
                        getString(R.string.embarazo_lactancia) +
                        getString(R.string.se_ales_de_buena_hidrataci_n) +
                        getString(R.string.orina_de_color_amarillo_claro) +
                        getString(R.string.piel_el_stica) +
                        getString(R.string.boca_h_meda) +
                        getString(R.string.energ_a_constante) +
                        getString(R.string.consejo_profesional),
                R.drawable.tip1
        );
    }

    // Método para manejar el click en la tarjeta 2
    public void tarjeta2Click(View view) {
        openDetailActivity(
                getString(R.string.titulo_card2_largo),
                getString(R.string.llevar_una_botella) +
                        getString(R.string.beneficios_ambientales) +
                        getString(R.string.reduce_el_uso_de_pl_sticos) +
                        getString(R.string.disminuye_tu_huella_de_carbono) +
                        getString(R.string.beneficios_para_la_salud) +
                        getString(R.string.recordatorio_visual_constante_para_beber) +
                        getString(R.string.permite_medir_tu_consumo_exacto) +
                        getString(R.string.mejor_control_de_temperatura_del_l_quido) +
                        getString(R.string.tipos_de_botellas_recomendadas) +
                        getString(R.string.acero_inoxidable_mantiene_temperatura_12_24_hrs) +
                        getString(R.string.vidrio_borosilicato_libre_de_qu_micos) +
                        getString(R.string.trit_n_livianas_y_resistentes) +
                        getString(R.string.truco_profesional_elige_una_botella),
                R.drawable.tip2
        );
    }

    // Método para manejar el click en la tarjeta 3
    public void tarjeta3Click(View view) {
        openDetailActivity(
                getString(R.string.titulo_card3_largo),
                getString(R.string.las_frutas_con_alto_contenido) +
                        getString(R.string.sand_a_92_agua) +
                        getString(R.string._1_taza_140) +
                        getString(R.string.rica_en_vitamina) +
                        getString(R.string.mel_n_90) +
                        getString(R.string.alto_contenido_en_potasio) +
                        getString(R.string.ayuda_a_regular_presi_n_arterial) +
                        getString(R.string.naranjas_88) +
                        getString(R.string.refuerza_sistema_inmunol_gico) +
                        getString(R.string.ayuda_a_absorber_hierro) +
                        getString(R.string.otras_frutas_hidratantes) +
                        getString(R.string.pi_a_87_agua_bromelina_digestiva) +
                        getString(R.string.pepino_96_agua_t_cnicamente_una_fruta) +
                        getString(R.string.fresas_91_agua_antioxidantes) +
                        getString(R.string.receta_hidratante_mezcla_sand_a_pepino),
                R.drawable.tip6
        );
    }

    // Método para manejar el click en la tarjeta 4
    public void tarjeta4Click(View view) {
        openDetailActivity(
                getString(R.string.titulo_card4_largo),
                getString(R.string.c_mo_afectan_diferentes_bebidas) +
                        getString(R.string.caf_por_taza) +
                        getString(R.string.efecto_diur_tico_leve) +
                        getString(R.string.contrarresta_con_1) +
                        getString(R.string.l_mite_saludable_3_4_tazas) +
                        getString(R.string.alcohol) +
                        getString(R.string.inhibe_la_hormona_antidiur_tica_adh) +
                        getString(R.string.cada_copa_necesita_1_2_vasos_de_agua) +
                        getString(R.string.causa_p_rdida_de_electrolitos) +
                        getString(R.string.bebidas_azucaradas) +
                        getString(R.string.alto_contenido_de_fructosa) +
                        getString(R.string.requieren_agua_para_metabolizarse) +
                        getString(R.string.aumentan_deshidrataci_n_celular) +
                        getString(R.string.alternativas_saludables) +
                        getString(R.string.agua_de_coco_naturalmente_isot_nica) +
                        getString(R.string.t_herbal_sin_cafe_na) +
                        getString(R.string.agua_infusionada_con_frutas) +
                        getString(R.string.dato_clave_el_cuerpo_necesita_3_mol_culas),
                R.drawable.tip4
        );
    }

    private void openDetailActivity(String title, String description, int imageRes) {
        Intent intent = new Intent(this, DetalleActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("description", description);
        intent.putExtra("imageRes", imageRes);
        startActivity(intent);
    }



}