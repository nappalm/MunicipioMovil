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
import android.widget.ImageView;
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

import desarrolladoresalpha.municipiomovil.App_Cuenta.Login_App;
import desarrolladoresalpha.municipiomovil.R;

public class Recovery_App extends AppCompatActivity {
    SharedPreferences Preferencias; //Para guardar informacion en cache
    RequestQueue requestQueue; //Para la utilizacion de Volley
    EditText Correo,Clave,Codigo;
    String Usuario;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recovery_app);
        requestQueue = Volley.newRequestQueue(getApplicationContext()); //Libreria de Volley
        Preferencias = getSharedPreferences("Preferencias", Context.MODE_PRIVATE); //Datos de preferencias para guardar en cache.
        Correo = (EditText)findViewById(R.id.editCorreo);
        Clave = (EditText)findViewById(R.id.editClave);
        Codigo = (EditText)findViewById(R.id.editCodigo);


        FloatingActionButton Recuperar = (FloatingActionButton)findViewById(R.id.botonRecuperar);
        Recuperar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!Correo.getText().toString().isEmpty()){
                    Recovery_App.RecuperarCuenta recuperar = new Recovery_App.RecuperarCuenta();
                    recuperar.execute();
                }
            }
        });

        FloatingActionButton Verificar = (FloatingActionButton)findViewById(R.id.botonVerificar);
        Verificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!Codigo.getText().toString().isEmpty() && !Clave.getText().toString().isEmpty()){
                    Recovery_App.VerificarCodigo verificar = new Recovery_App.VerificarCodigo();
                    verificar.execute();
                }else{
                    Vibrator v = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                    v.vibrate(500);
                }
            }
        });

        ImageView Regresar = (ImageView)findViewById(R.id.botonRegresar);
        Regresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), Login_App.class);
                startActivity(i);
                overridePendingTransition(R.anim.right_in, R.anim.right_out);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext(), Login_App.class);
        startActivity(i);
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
        finish();
        super.onBackPressed();
    }

    private class RecuperarCuenta extends AsyncTask<Void, Void, Void> {
        public void onPreExecute() {

        }
        public void onPostExecute(Void unused) {
        }

        @Override
        protected Void doInBackground(Void... params) {
            StringRequest request = new StringRequest(Request.Method.POST, getString(R.string.usuario_recuperar), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Toast.makeText(Recovery_App.this,"Se ha envíado tu código",Toast.LENGTH_SHORT).show();
                    Usuario = response;
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(Recovery_App.this,"Verifique que este conectado a internet",Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> parameters = new HashMap<String, String>();
                    parameters.put("correo", Correo.getText().toString());
                    return parameters;
                }
            };
            requestQueue.add(request);
            return null;
        }
    }
    private class VerificarCodigo extends AsyncTask<Void, Void, Void> {
        public void onPreExecute() {

        }
        public void onPostExecute(Void unused) {
        }

        @Override
        protected Void doInBackground(Void... params) {
            StringRequest request = new StringRequest(Request.Method.POST, getString(R.string.usuario_keycode), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if (response.equals("pass-change")){
                        Toast.makeText(Recovery_App.this,"Ingresa con tu nueva clave",Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(getApplicationContext(), Login_App.class);
                        startActivity(i);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        finish();
                    }else{
                        Toast.makeText(getApplicationContext(),"Al parecer el codigo no coincide",Toast.LENGTH_SHORT).show();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(Recovery_App.this,"Verifique que este conectado a internet",Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> parameters = new HashMap<String, String>();
                    parameters.put("usuario", Usuario);
                    parameters.put("codigo", Codigo.getText().toString());
                    parameters.put("clave", Clave.getText().toString());
                    return parameters;
                }
            };
            requestQueue.add(request);
            return null;
        }
    }


}
