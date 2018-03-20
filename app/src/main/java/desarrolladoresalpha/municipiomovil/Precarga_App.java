package desarrolladoresalpha.municipiomovil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ProgressBar;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.util.Timer;
import java.util.TimerTask;

import desarrolladoresalpha.municipiomovil.App_Cuenta.Login_App;
import desarrolladoresalpha.municipiomovil.App_Principal.Inicio_App;
import desarrolladoresalpha.municipiomovil.App_Recuperacion.Keycode_App;

public class Precarga_App extends AppCompatActivity {
    private static final long SPLASH_SCREEN_DELAY = 3000;
    SharedPreferences Preferencias;
    SharedPreferences.Editor editor;
    RequestQueue requestQueue; //Para la utilizacion de Volley

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.precarga_app);
        Preferencias = Preferencias = getSharedPreferences("Preferencias", Context.MODE_PRIVATE); //Datos de preferencias para guardar en cache.
        editor = Preferencias.edit();
        requestQueue = Volley.newRequestQueue(getApplicationContext()); //Libreria de Volley

        ProgressBar Carga = (ProgressBar)findViewById(R.id.proLoading);
        Carga.getIndeterminateDrawable().setColorFilter(Color.LTGRAY, PorterDuff.Mode.MULTIPLY);
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                CargarPreferencias();
            }
        };

        Timer timer = new Timer();
        timer.schedule(task, SPLASH_SCREEN_DELAY);
    }


    //Acceso a preferencias para guardar datos en cache.
    public void CargarPreferencias(){
        String Aplicacion = (Preferencias.getString("Aplicacion","")); //Verificamos que sea correcto.
        Intent ventana = null;
        if (Aplicacion.isEmpty()){
            ventana = new Intent(getApplicationContext(), Login_App.class);
        }else if (Aplicacion.equals("check") && !Preferencias.getString("ID","").isEmpty()){
            ventana = new Intent(getApplicationContext(), Inicio_App.class);
        }else if (Aplicacion.equals("code")){
            ventana = new Intent(getApplicationContext(), Keycode_App.class);
        }/*else if(Preferencias.getString("ID","").isEmpty() && !Preferencias.getString("Correo","").isEmpty()){
            //Descargaos el ID del correo
            Precarga_App.VerificarUsuario verificar = new Precarga_App.VerificarUsuario();
            verificar.execute();
        }*/
        if (ventana != null) {
            startActivity(ventana);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        }
   }

    private void LimpiarPreferencias(){
        editor.putString("Aplicacion","");
        editor.putString("ID","");
        editor.putString("Nombre","");
        editor.putString("Apellidos","");
        editor.putString("Celular","");
        editor.putString("Correo","");
        editor.putString("Clave","");
        editor.putString("Localidad","");
        editor.commit();
        Intent i = new Intent(getApplicationContext(), Login_App.class);
        startActivity(i);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }
/*
    private class VerificarUsuario extends AsyncTask<Void, Void, Void> {
        public void onPreExecute() {
        }
        public void onPostExecute(Void unused) {
        }

        @Override
        protected Void doInBackground(Void... params) {
            StringRequest request = new StringRequest(Request.Method.POST, getString(R.string.mostrar_validacion), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if (!response.equals("null")){
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            //Asignamos datos a las preferencias
                            editor.putString("ID",jsonArray.getJSONObject(0).getString("ID"));
                            editor.commit();
                            Intent i = new Intent(getApplicationContext(), Precarga_App.class);
                            startActivity(i);
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else{
                        LimpiarPreferencias();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    LimpiarPreferencias();
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> parameters = new HashMap<String, String>();
                    parameters.put("correo", Preferencias.getString("Correo",""));
                    return parameters;
                }
            };
            requestQueue.add(request);
            return null;
        }
    }
*/

}
