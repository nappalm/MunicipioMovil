package desarrolladoresalpha.municipiomovil.App_Principal;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import desarrolladoresalpha.municipiomovil.App_Cuenta.Opciones_App;
import desarrolladoresalpha.municipiomovil.App_Municipio.Municipio_App;
import desarrolladoresalpha.municipiomovil.App_Municipio.Numeros_App;
import desarrolladoresalpha.municipiomovil.App_Municipio.Noticias_App;
import desarrolladoresalpha.municipiomovil.App_Reportes.MisReportes_App;
import desarrolladoresalpha.municipiomovil.App_Reportes.Reporte_App;
import desarrolladoresalpha.municipiomovil.Precarga_App;
import desarrolladoresalpha.municipiomovil.R;

public class Inicio_App extends AppCompatActivity {
    RelativeLayout Menus; //Contenedor de menus de aplicacion
    TextView Usuario,Localidad;
    SharedPreferences Preferencias;
    @Override
    public void onBackPressed() {
        if (Menus.getVisibility() == View.VISIBLE){
            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),android.R.anim.fade_out);
            Menus.startAnimation(animation);
            Menus.setVisibility(View.INVISIBLE);
        }else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inicio_app);
        Preferencias = getSharedPreferences("Preferencias", Context.MODE_PRIVATE); //Datos de preferencias para guardar en cache.

        Menus = (RelativeLayout)findViewById(R.id.Layout_Menu);
        Usuario = (TextView)findViewById(R.id.inicioUsuario);
        Localidad = (TextView)findViewById(R.id.inicioLocalidad);

        Usuario.setText(Preferencias.getString("Nombre","")+" "+Preferencias.getString("Apellidos",""));
        if (Preferencias.getString("Localidad","").isEmpty() || Preferencias.getString("Localidad","").equals("No especificado") ) {
            Localidad.setText(Preferencias.getString("Correo",""));
        }else{
           Localidad.setText(Preferencias.getString("Localidad",""));
        }

        LinearLayout Telefonos = (LinearLayout)findViewById(R.id.botonTelefonos);
        Telefonos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), Numeros_App.class);
                startActivity(i);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });



        LinearLayout Bache = (LinearLayout)findViewById(R.id.botonBache);
        Bache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), Reporte_App.class);
                i.putExtra("TIPO", "BACHE");
                startActivity(i);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });



        LinearLayout Iluminaria = (LinearLayout)findViewById(R.id.botonIluminaria);
        Iluminaria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), Reporte_App.class);
                i.putExtra("TIPO", "ILUMINARIA");
                startActivity(i);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });

        LinearLayout Animal = (LinearLayout)findViewById(R.id.botonAnimal);
        Animal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), Reporte_App.class);
                i.putExtra("TIPO", "ANIMAL");
                startActivity(i);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });

        LinearLayout Podado = (LinearLayout)findViewById(R.id.botonPodado);
        Podado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), Reporte_App.class);
                i.putExtra("TIPO", "PODADO");
                startActivity(i);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });

        LinearLayout Noticias = (LinearLayout)findViewById(R.id.botonNoticias);
        Noticias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), Noticias_App.class);
                startActivity(i);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });

        LinearLayout Agua = (LinearLayout)findViewById(R.id.botonAgua);
        Agua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), Reporte_App.class);
                i.putExtra("TIPO", "AGUA");
                startActivity(i);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });

        LinearLayout Basura = (LinearLayout)findViewById(R.id.botonBasura);
        Basura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), Reporte_App.class);
                i.putExtra("TIPO", "BASURA");
                startActivity(i);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });


        LinearLayout Municipio = (LinearLayout)findViewById(R.id.botonMunicipio);
        Municipio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), Municipio_App.class);
                startActivity(i);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });


        ImageView Menu = (ImageView)findViewById(R.id.botonMenu);
        Menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Menus.setVisibility(View.VISIBLE);
                Menus.setAlpha(0.0f);
                Menus.animate()
                        .translationY(5)
                        .alpha(1.0f)
                        .setListener(null);
            }
        });

        LinearLayout Reportes = (LinearLayout)findViewById(R.id.botonReportes);
        Reportes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), MisReportes_App.class);
                startActivity(i);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });


        LinearLayout Cuenta = (LinearLayout)findViewById(R.id.botonConfigurar);
        Cuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), Opciones_App.class);
                startActivity(i);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });


        LinearLayout Sesion = (LinearLayout)findViewById(R.id.botonSalir);
        Sesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LimpiarPreferencias();
            }
        });

        RelativeLayout OcultarMenu = (RelativeLayout)findViewById(R.id.Layout_Hide);
        OcultarMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),android.R.anim.fade_out);
                Menus.startAnimation(animation);
                Menus.setVisibility(View.INVISIBLE);
            }
        });

    }
    private void LimpiarPreferencias(){
        SharedPreferences.Editor editor = Preferencias.edit();
        editor.putString("Aplicacion","");
        editor.putString("ID","");
        editor.putString("Nombre","");
        editor.putString("Apellidos","");
        editor.putString("Celular","");
        editor.putString("Correo","");
        editor.putString("Clave","");
        editor.putString("Localidad","");
        editor.commit();
        Intent i = new Intent(getApplicationContext(), Precarga_App.class);
        startActivity(i);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }

}
