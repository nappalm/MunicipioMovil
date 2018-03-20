package desarrolladoresalpha.municipiomovil.App_Recuperacion;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Vibrator;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
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

import desarrolladoresalpha.municipiomovil.Precarga_App;
import desarrolladoresalpha.municipiomovil.R;

public class Keycode_App extends AppCompatActivity {
    EditText Codigo;
    RequestQueue requestQueue; //Para la utilizacion de Volley
    SharedPreferences Preferencias; //Para guardar informacion en cache
    RelativeLayout Cargando;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.keycode_app);
        Cargando = (RelativeLayout)findViewById(R.id.Layout_Cargando);
        Codigo = (EditText)findViewById(R.id.editCodigo);
        requestQueue = Volley.newRequestQueue(getApplicationContext()); //Libreria de Volley
        Preferencias = getSharedPreferences("Preferencias", Context.MODE_PRIVATE); //Datos de preferencias para guardar en cache.

        FloatingActionButton Validar = (FloatingActionButton)findViewById(R.id.botonValidar);
        Validar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!Codigo.getText().toString().isEmpty()){
                    Keycode_App.VerificarCuenta validar = new Keycode_App.VerificarCuenta();
                    validar.execute();
                }else{
                    Toast.makeText(getApplicationContext(),"Ingresa un codigo valido",Toast.LENGTH_SHORT).show();
                    Vibrator v = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                    v.vibrate(500);
                }
            }
        });

        TextView NoCodigo = (TextView)findViewById(R.id.botonReenviar);
        NoCodigo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), Reenviar_App.class);
                startActivity(i);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });

        TextView Titulo = (TextView)findViewById(R.id.infoTitulo);
        Titulo.setText("Hola "+Preferencias.getString("Nombre","")+", ingresa el codigo de validación que enviamos a tu cuenta de correo.");
    }
    private class VerificarCuenta extends AsyncTask<Void, Void, Void> {
        public void onPreExecute() {
            Cargando.setVisibility(View.VISIBLE); //Mostramos la precarga
        }
        public void onPostExecute(Void unused) {
        }

        @Override
        protected Void doInBackground(Void... params) {
            StringRequest request = new StringRequest(Request.Method.POST, getString(R.string.usuario_keycode), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if (!response.equals("null")){
                            SharedPreferences.Editor editor = Preferencias.edit();
                            editor.putString("Aplicacion", "check");
                            editor.commit();

                            Intent i = new Intent(getApplicationContext(), Precarga_App.class);
                            startActivity(i);
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            finish();
                    }else{
                        Cargando.setVisibility(View.INVISIBLE);
                        Toast.makeText(Keycode_App.this,"Ingresa un codigo valido",Toast.LENGTH_SHORT).show();

                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Cargando.setVisibility(View.INVISIBLE);
                    Toast.makeText(Keycode_App.this,"Verifique que este conectado a internet",Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> parameters = new HashMap<String, String>();
                    parameters.put("usuario", Preferencias.getString("ID",""));
                    parameters.put("codigo", Codigo.getText().toString());
                    return parameters;
                }
            };
            requestQueue.add(request);
            return null;
        }
    }

}
