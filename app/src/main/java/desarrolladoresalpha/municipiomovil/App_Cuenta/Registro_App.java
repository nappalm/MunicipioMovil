package desarrolladoresalpha.municipiomovil.App_Cuenta;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Vibrator;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
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
import java.util.regex.Pattern;

import desarrolladoresalpha.municipiomovil.Precarga_App;
import desarrolladoresalpha.municipiomovil.R;

public class Registro_App extends AppCompatActivity {
    EditText[] Datos;
    RelativeLayout Cargando;
    //String CodigoGenerado; //Obtiene el codigo de registro
    RequestQueue requestQueue; //Para la utilizacion de Volley
    SharedPreferences Preferencias; //Para guardar informacion en cache
    TextView Verificador;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registro_app);
        requestQueue = Volley.newRequestQueue(getApplicationContext()); //Libreria de Volley
        Cargando =(RelativeLayout)findViewById(R.id.Layout_Cargando);
        Verificador =(TextView)findViewById(R.id.infoVerificador);
        Preferencias = getSharedPreferences("Preferencias", Context.MODE_PRIVATE); //Datos de preferencias para guardar en cache.
        Datos = new EditText[]{findViewById(R.id.editNombre),findViewById(R.id.editApellidos),findViewById(R.id.editCorreo),findViewById(R.id.editTelefono),findViewById(R.id.editClave)};
        FloatingActionButton Registro = (FloatingActionButton)findViewById(R.id.botonRegistro);
        Registro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (VerificarDatos() == 1){
                    //TRUE! significa que los datos estan correctos;
                    if (!validarEmail(Datos[2].getText().toString())){
                        //Varificamos que el correo este en sintaxis correcta
                        Toast.makeText(getApplicationContext(),"Correo invalido",Toast.LENGTH_SHORT).show();
                        Vibrator v = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                        v.vibrate(500);
                    }else{
                        //Correo y demas información correcta, procesamos al envío
                        Registro_App.EnviarRegistro Registro = new Registro_App.EnviarRegistro();
                        Registro.execute();
                    }
                }else if(VerificarDatos() == 0){
                    //Toast.makeText(getApplicationContext(),"Te ha faltado información",Toast.LENGTH_SHORT).show();
                    Verificador.setVisibility(View.VISIBLE);
                    Verificador.setText("Ingresa todos los campos");
                    Vibrator v = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                    v.vibrate(500);
                }else if(VerificarDatos() == 2){
                    Verificador.setVisibility(View.VISIBLE);
                    Verificador.setText("La contraseña debe ser mayor a 6 caracteres");
                    Vibrator v = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                    v.vibrate(500);
                }

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

    private int VerificarDatos(){
        if(Datos[4].getText().length() > 6) { //Filtro clave mayor de 6 caracteres
            int empty = 0;
            for (int i = 0; i < Datos.length; i++) {
                if (Datos[i].getText().toString().isEmpty()) {
                    empty++;
                }
            }
            if (empty == 0) {
                return 1; //Todos llenos
            } else {
                return 0; //Faltan datos por llenar
            }
        }else{
            return 2; //Contraseña minima de 6 caracteres
        }
    }

    private boolean validarEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }

    private class EnviarRegistro extends AsyncTask<Void , Void, Void> {
        public void onPreExecute() {
            Cargando.setVisibility(View.VISIBLE);
        }
        public void onPostExecute(Void unused) {

        }
        @Override
        protected Void doInBackground(Void... params) {
            StringRequest request = new StringRequest(Request.Method.POST, getString(R.string.usuario_registro), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    switch (response){
                        case "exist":
                            //Toast.makeText(getApplicationContext(),"Ya existe una cuenta con ese correo",Toast.LENGTH_SHORT).show();
                            Verificador.setVisibility(View.VISIBLE);
                            Verificador.setText("El correo ya se encuentra en uso");
                            Cargando.setVisibility(View.INVISIBLE);
                            break; //cuenta existente
                        case "code-resend": break; //codigo no generado
                        case "false-save":
                            Toast.makeText(getApplicationContext(),"Se presento un problema, vuelve a intentarlo",Toast.LENGTH_SHORT).show();
                            break; //problema de registro de usuario
                        default:
                            SharedPreferences.Editor editor = Preferencias.edit();
                            editor.putString("Aplicacion","code"); //TODO! aqui debe quedar en "code" no en "check"
                            editor.putString("ID",response);
                            editor.putString("Nombre",Datos[0].getText().toString());
                            editor.putString("Apellidos",Datos[1].getText().toString());
                            editor.putString("Correo",Datos[2].getText().toString());
                            editor.putString("Celular",Datos[3].getText().toString());
                            editor.commit();
                            Intent i = new Intent(getApplicationContext(), Precarga_App.class);
                            startActivity(i);
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            finish();
                            break; //Registro de usuario
                    }
                    Cargando.setVisibility(View.INVISIBLE);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(), "Se ha producido un error de envío", Toast.LENGTH_SHORT).show();
                    Cargando.setVisibility(View.INVISIBLE);
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> parameters = new HashMap<String, String>();
                    parameters.put("correo",Datos[2].getText().toString());
                    parameters.put("nombre", Datos[0].getText().toString());
                    parameters.put("apellidos", Datos[1].getText().toString());
                    parameters.put("celular",Datos[3].getText().toString());
                    parameters.put("clave",Datos[4].getText().toString());

                    return parameters;
                }
            };
            requestQueue.add(request);
            return null;
        }
    }
}
