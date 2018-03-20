package desarrolladoresalpha.municipiomovil.App_Cuenta;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import desarrolladoresalpha.municipiomovil.App_Municipio.Localidades_App;
import desarrolladoresalpha.municipiomovil.App_Principal.Inicio_App;
import desarrolladoresalpha.municipiomovil.R;

public class Opciones_App extends AppCompatActivity {
    TextView[] Verificadores;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.opciones_app);
        final SharedPreferences[] Preferencias = {getSharedPreferences("Preferencias", Context.MODE_PRIVATE)}; //Datos de preferencias para guardar en cache.
        Verificadores = new TextView[]{(TextView)findViewById(R.id.checkUsuario),(TextView)findViewById(R.id.checkCorreo),
                (TextView)findViewById(R.id.checkClave),(TextView)findViewById(R.id.checkCelular),(TextView)findViewById(R.id.checkLocalidad)};
        ImageView Regresar = (ImageView)findViewById(R.id.botonRegresar);
        Regresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), Inicio_App.class);
                startActivity(i);
                overridePendingTransition(R.anim.right_in, R.anim.right_out);
                finish();
            }
        });
        Verificadores[0].setText(Preferencias[0].getString("Nombre","")+" "+Preferencias[0].getString("Apellidos",""));
        Verificadores[1].setText(Preferencias[0].getString("Correo",""));
        Verificadores[3].setText(Preferencias[0].getString("Celular",""));
        Verificadores[4].setText(Preferencias[0].getString("Localidad",""));


        LinearLayout[] Menus = new LinearLayout[]{(LinearLayout)findViewById(R.id.botonUsuario),(LinearLayout)findViewById(R.id.botonCorreo),
                (LinearLayout)findViewById(R.id.botonClave),(LinearLayout)findViewById(R.id.botonCelular),(LinearLayout)findViewById(R.id.botonLocalidad)};
        for (int i = 0; i<Menus.length;i++){
            final int finalI = i;
            Menus[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (finalI == 4){
                        Intent x = new Intent(getApplicationContext(), Localidades_App.class);
                        x.putExtra("TIPO", "LOCALIDAD");
                        startActivity(x);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        finish();
                    }else {
                        Intent x = new Intent(getApplicationContext(), Configurar_App.class);
                        x.putExtra("TIPO", String.valueOf(finalI + 1));
                        startActivity(x);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        finish();
                    }
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext(), Inicio_App.class);
        startActivity(i);
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
        finish();
        super.onBackPressed();
    }
}
