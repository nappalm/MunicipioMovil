package desarrolladoresalpha.municipiomovil.App_Cuenta;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Vibrator;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import desarrolladoresalpha.municipiomovil.R;

public class Configurar_App extends AppCompatActivity {
    EditText Uno,Dos;
    LinearLayout lUno,lDos;
    TextView tUno,tDos,Titulo;
    String Tipo;
    SharedPreferences[] Preferencias;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.configurar_app);
        Preferencias = new SharedPreferences[]{getSharedPreferences("Preferencias", Context.MODE_PRIVATE)}; //Datos de preferencias para guardar en cache.

        Titulo = (TextView)findViewById(R.id.repTitulo);
        Uno = (EditText) findViewById(R.id.editDatosUno);
        Dos = (EditText)findViewById(R.id.editDatosDos);
        lUno = (LinearLayout)findViewById(R.id.editUno);
        lDos = (LinearLayout)findViewById(R.id.editDos);

        tUno = (TextView)findViewById(R.id.infoEditUno);
        tDos = (TextView)findViewById(R.id.infoEditDos);


        Intent datos= getIntent();
        Bundle b = datos.getExtras();
        if(b!=null)
        {
            Tipo = b.get("TIPO").toString();
            switch (b.get("TIPO").toString()){
                case "1":
                    lDos.setVisibility(View.VISIBLE);
                    tDos.setVisibility(View.VISIBLE);
                    tDos.setText("Apellidos");
                    Titulo.setText("Datos de usuario");
                    Uno.setText(Preferencias[0].getString("Nombre",""));
                    Dos.setText(Preferencias[0].getString("Apellidos",""));

                    break; //Usuario
                case "2":
                    Titulo.setText("Datos de correo");
                    tUno.setText("Correo electronico");
                    Uno.setText(Preferencias[0].getString("Correo",""));

                    break; //Correo
                case "3":
                    Titulo.setText("Clave de acceso");
                    tUno.setText("Ingresa una clave");

                    break; //Clave
                case "4":
                    Titulo.setText("Dispositivo móvil");
                    tUno.setText("Numero de telefono");
                    Uno.setText(Preferencias[0].getString("Celular",""));

                    break; //Celular
                case "5":
                    Titulo.setText("Datos de localidad");
                    tUno.setText("Selecciona una localidad");
                    Uno.setText(Preferencias[0].getString("Localidad",""));
                    break; //Localidad
            }
        }

        ImageView Regresar = (ImageView)findViewById(R.id.botonRegresar);
        Regresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), Opciones_App.class);
                startActivity(i);
                overridePendingTransition(R.anim.right_in, R.anim.right_out);
                finish();
            }
        });

        FloatingActionButton Guardar = (FloatingActionButton)findViewById(R.id.botonGuardar);
        Guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if (Tipo.equals("1")){ //Datos de nombre y apellidos que no esten vacios
                    if (Uno.getText().toString().isEmpty() || Dos.getText().toString().isEmpty()){
                        Vibrator v = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                        v.vibrate(500);
                    }else{
                        new Configurar_App.Insertar(Configurar_App.this).execute();
                    }
               }else{
                    if (Uno.getText().toString().isEmpty()) {
                        Vibrator v = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                        v.vibrate(500);
                    }else{
                        new Configurar_App.Insertar(Configurar_App.this).execute();
                    }
               }
              hideSoftKeyboard();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext(), Opciones_App.class);
        startActivity(i);
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
        finish();
        super.onBackPressed();
    }

    private boolean ModificarDatos(){
        HttpClient httpClient;
        HttpPost httpPost;
        httpClient = new DefaultHttpClient();
        httpPost = new HttpPost(getString(R.string.usuario_modificar));//url del servidor

        try {
            List<NameValuePair> nameValuePairs;
            //empezamos añadir nuestros datos
            nameValuePairs = new ArrayList<NameValuePair>(4);
            nameValuePairs.add(new BasicNameValuePair("usuario",Preferencias[0].getString("ID",""))); //ID de usuario logeado
            nameValuePairs.add(new BasicNameValuePair("tipo",Tipo)); //Tipo de campo
            if (Tipo.equals("1")){ //Si es para nombre se envían dos parametros
                nameValuePairs.add(new BasicNameValuePair("dato1",Uno.getText().toString())); //Nombre
                nameValuePairs.add(new BasicNameValuePair("dato2",Dos.getText().toString())); //Apellido
            }else{
                nameValuePairs.add(new BasicNameValuePair("dato1",Uno.getText().toString())); //Primer dato
            }

            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            String response = httpClient.execute(httpPost, responseHandler);
            return true;

        } catch(UnsupportedEncodingException e){
            e.printStackTrace();
        }catch (ClientProtocolException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }
        return  false;
    }
    class Insertar extends AsyncTask<String,String,String> {
        private Activity context;
        private ProgressDialog dialog;
        Insertar(Activity context){
            this.context=context;
            dialog = new ProgressDialog(context);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
        }
        @Override
        protected void onPreExecute() {
            dialog.setMessage("Modificando información ...");
            dialog.show();
        }
        @Override
        protected void onPostExecute(String s) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }

       protected String doInBackground(String... params) {
            if(ModificarDatos()) {
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ModicarPreferencia(Tipo);
                        Intent  x = new Intent(getApplicationContext(), Opciones_App.class);
                        startActivity(x);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        finish();
                    }
                });
            }else{
                context.runOnUiThread(new Runnable(){
                    @Override
                    public void run() {
                        Toast.makeText(context, "Se ha producido un error de envio.", Toast.LENGTH_LONG).show();
                    }
                });
            }

            return null;
        }
    }
    private void ModicarPreferencia(String Tipo){
        //Modificamos preferencias una ves actualizadas en la BDD
        SharedPreferences Preferencias; //Para guardar informacion en cache
        Preferencias = getSharedPreferences("Preferencias", Context.MODE_PRIVATE); //Datos de preferencias para guardar en cache.
        SharedPreferences.Editor editor = Preferencias.edit();
        switch (Tipo){
            case "1":
                editor.putString("Nombre", Uno.getText().toString());
                editor.putString("Apellidos", Dos.getText().toString());
                break; //Usuario
            case "2":
                editor.putString("Correo", Uno.getText().toString());
                break; //Correo
            case "4":
                editor.putString("Celular", Uno.getText().toString());
                break; //Celular
        }

        editor.commit();
    }

    public void hideSoftKeyboard() {
        //Ocultar teclado
        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }
}
