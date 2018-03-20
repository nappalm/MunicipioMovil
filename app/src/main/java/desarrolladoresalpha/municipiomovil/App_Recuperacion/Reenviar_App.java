package desarrolladoresalpha.municipiomovil.App_Recuperacion;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

import desarrolladoresalpha.municipiomovil.R;

public class Reenviar_App extends AppCompatActivity {
    SharedPreferences Preferencias; //Para guardar informacion en cache
    RequestQueue requestQueue; //Para la utilizacion de Volley
    EditText Correo;
    ProgressBar Carga;
    String CorreoGeneral;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reenviar_app);
        requestQueue = Volley.newRequestQueue(getApplicationContext()); //Libreria de Volley
        Preferencias = getSharedPreferences("Preferencias", Context.MODE_PRIVATE); //Datos de preferencias para guardar en cache.
        Correo = (EditText)findViewById(R.id.editReenviar);
        Carga = (ProgressBar)findViewById(R.id.botonCargando);
        TextView EmailInfo = (TextView)findViewById(R.id.infoEmail);
        EmailInfo.setText("Te hemos reenviado la activación a tu correo: "+Preferencias.getString("Correo",""));
        CorreoGeneral = Preferencias.getString("Correo","");
        //Reenviamos el codigo al iniciar el activity!
        Reenviar_App.ReenviarCodigo reenviarCodigo = new Reenviar_App.ReenviarCodigo();
        reenviarCodigo.execute();

        final RelativeLayout Reenviar = (RelativeLayout)findViewById(R.id.botonReenviar);
        Reenviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Correo.getText().toString().isEmpty()){
                    CorreoGeneral = Correo.getText().toString();
                    Reenviar_App.ReenviarCodigo reenviarCodigo = new Reenviar_App.ReenviarCodigo();
                    reenviarCodigo.execute();
                }else{
                    Vibrator v = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                    v.vibrate(500);
                }
            }
        });

        TextView Mostrar = (TextView)findViewById(R.id.botonVReenviar);
        Mostrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinearLayout Reenvio = (LinearLayout)findViewById(R.id.Layout_Reenviar);
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),android.R.anim.fade_in);
                Reenvio.startAnimation(animation);
                Reenvio.setVisibility(View.VISIBLE);
            }
        });

        LinearLayout Reintentar = (LinearLayout)findViewById(R.id.botonReintentar);
        Reintentar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), Keycode_App.class);
                startActivity(i);
                overridePendingTransition(R.anim.right_in, R.anim.right_out);
                finish();
            }
        });
    }

    private class ReenviarCodigo extends AsyncTask<Void, Void, Void> {
        public void onPreExecute() {
            if (!Preferencias.getString("Correo","").toString().equals(CorreoGeneral)) {
                Carga.setVisibility(View.VISIBLE); //Mostramos la precarga
            }
        }
        public void onPostExecute(Void unused) {
        }

        @Override
        protected Void doInBackground(Void... params) {
            StringRequest request = new StringRequest(Request.Method.POST, getString(R.string.usuario_keyreenviar), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if (!Preferencias.getString("Correo","").toString().equals(CorreoGeneral)){
                        SharedPreferences.Editor editor = Preferencias.edit();
                        editor.putString("Correo",Correo.getText().toString());
                        editor.commit();
                        Intent i = new Intent(getApplicationContext(), Keycode_App.class);
                        startActivity(i);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        finish();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Carga.setVisibility(View.GONE);
                    Toast.makeText(Reenviar_App.this,"Verifique que este conectado a internet",Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> parameters = new HashMap<String, String>();
                    parameters.put("correo", CorreoGeneral);
                    parameters.put("usuario",Preferencias.getString("ID",""));
                    return parameters;
                }
            };
            requestQueue.add(request);
            return null;
        }
    }

}
