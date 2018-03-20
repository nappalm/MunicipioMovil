package desarrolladoresalpha.municipiomovil.App_Cuenta;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
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

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

import desarrolladoresalpha.municipiomovil.App_Recuperacion.Recovery_App;
import desarrolladoresalpha.municipiomovil.Precarga_App;
import desarrolladoresalpha.municipiomovil.R;

public class Login_App extends AppCompatActivity {
    RelativeLayout Cargando;
    EditText Usuario,Clave;
    RequestQueue requestQueue; //Para la utilizacion de Volley
    SharedPreferences Preferencias; //Para guardar informacion en cache
    ProgressBar Carga;
    TextView Verificador,Recuperar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_app);
        requestQueue = Volley.newRequestQueue(getApplicationContext()); //Libreria de Volley
        Cargando = (RelativeLayout)findViewById(R.id.Layout_Precarga);
        Usuario = (EditText)findViewById(R.id.editCorreo);
        Clave = (EditText)findViewById(R.id.editClave);
        Carga = (ProgressBar)findViewById(R.id.botonCargando);
        Carga.getIndeterminateDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);
        Verificador =(TextView)findViewById(R.id.infoVerificador);
        Preferencias = getSharedPreferences("Preferencias", Context.MODE_PRIVATE); //Datos de preferencias para guardar en cache.
        Recuperar = (TextView)findViewById(R.id.botonRecovery);
        Recuperar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), Recovery_App.class);
                startActivity(i);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });
        RelativeLayout Ingresar = (RelativeLayout)findViewById(R.id.botonIngresar);
        Ingresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!Usuario.getText().toString().isEmpty() && !Clave.getText().toString().isEmpty()){
                    hideSoftKeyboard();
                    Login_App.VerificarLogin Login = new Login_App.VerificarLogin();
                    Login.execute();
                }else{
                    Vibrator v = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                    v.vibrate(500);
                    Verificador.setVisibility(View.VISIBLE);
                    Verificador.setText("Ingresa tu usuario y contraseña");
                }
            }
        });
        RelativeLayout Registrar = (RelativeLayout)findViewById(R.id.botonRegistrar);
        Registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), Registro_App.class);
                startActivity(i);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });
    }
    private class VerificarLogin extends AsyncTask<Void, Void, Void> {
        public void onPreExecute() {
            Carga.setVisibility(View.VISIBLE); //Mostramos la precarga
        }
        public void onPostExecute(Void unused) {
        }

        @Override
        protected Void doInBackground(Void... params) {
            StringRequest request = new StringRequest(Request.Method.POST, getString(R.string.usuario_login), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if (!response.equals("null")){
                        try {


                            JSONArray jsonArray = new JSONArray(response);
                            //Asignamos datos a las preferencias
                            SharedPreferences.Editor editor = Preferencias.edit();
                            editor.putString("Aplicacion","check");
                            editor.putString("ID",jsonArray.getJSONObject(0).getString("id"));
                            editor.putString("Nombre",jsonArray.getJSONObject(0).getString("nombre"));
                            editor.putString("Apellidos",jsonArray.getJSONObject(0).getString("apellidos"));
                            editor.putString("Celular",jsonArray.getJSONObject(0).getString("celular"));
                            editor.putString("Correo",jsonArray.getJSONObject(0).getString("correo"));
                            editor.putString("Clave",jsonArray.getJSONObject(0).getString("clave"));
                            editor.putString("Localidad",jsonArray.getJSONObject(0).getString("localidad"));
                            editor.commit();

                            //TODO! falta validacion de codigo pero queda excluido por ahora.
                            Intent i = new Intent(getApplicationContext(), Precarga_App.class);
                            startActivity(i);
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else{
                        Carga.setVisibility(View.GONE);
                        Verificador.setVisibility(View.VISIBLE);
                        Verificador.setText("Al parecer los datos son incorrectos");
                        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),android.R.anim.fade_in);
                        Recuperar.startAnimation(animation);
                        Recuperar.setVisibility(View.VISIBLE);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Carga.setVisibility(View.GONE);
                    Toast.makeText(Login_App.this,"Verifique que este conectado a internet",Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> parameters = new HashMap<String, String>();
                    parameters.put("correo", Usuario.getText().toString());
                    parameters.put("clave", Clave.getText().toString());
                    return parameters;
                }
            };
            requestQueue.add(request);
            return null;
        }
    }

    public void hideSoftKeyboard() {
        //Ocultar teclado
        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }
}
