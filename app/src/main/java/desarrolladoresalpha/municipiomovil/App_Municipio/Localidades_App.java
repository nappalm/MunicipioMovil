package desarrolladoresalpha.municipiomovil.App_Municipio;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.kosalgeek.asynctask.AsyncResponse;
import com.kosalgeek.asynctask.PostResponseAsyncTask;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import desarrolladoresalpha.municipiomovil.App_Configuraciones.BaseDatos_App;
import desarrolladoresalpha.municipiomovil.App_Principal.Inicio_App;
import desarrolladoresalpha.municipiomovil.App_Cuenta.Opciones_App;
import desarrolladoresalpha.municipiomovil.Precarga_App;
import desarrolladoresalpha.municipiomovil.R;

public class Localidades_App extends AppCompatActivity implements AsyncResponse{
    private static PostResponseAsyncTask task; //Libreria para descargar datos
    private static BaseDatos_App basededatos; //Base de datos
    SimpleCursorAdapter simpleCursorAdapter; //Adaptador por defecto
    SharedPreferences Preferencias; //Para guardar informacion en cache
    TextView Localidad; //Obtener el nombre de la localidad seleccionada
    ListView Localidades;
    SwipeRefreshLayout swipeRefreshLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.localidades_app);
        Preferencias = getSharedPreferences("Preferencias", Context.MODE_PRIVATE); //Datos de preferencias para guardar en cache.
        Localidades = (ListView)findViewById(R.id.ListLocalidades);
        Localidades.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Localidad = (TextView) view.findViewById(R.id.textTelefono);
                SharedPreferences.Editor editor = Preferencias.edit();
                editor.putString("Localidad", Localidad.getText().toString());
                editor.commit();
                Intent x = null;
                Intent datos= getIntent();
                Bundle b = datos.getExtras();
                if(b!=null)
                {
                    x = new Intent(getApplicationContext(), Opciones_App.class);
                }else if(b.get("TIPO").toString().equals("LOCALIDAD")){
                    x = new Intent(getApplicationContext(), Precarga_App.class);
                }else if(b.get("TIPO").toString().equals("INICIO")){
                    x = new Intent(getApplicationContext(), Inicio_App.class);
                }
                    new Localidades_App.Insertar(Localidades_App.this).execute();
            }
        });
        basededatos = new BaseDatos_App(this, null, null, 1);
        ConsultarLocalidades(); //Consultamos informacion a la base de datos


        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeLayout);
        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        task = new PostResponseAsyncTask(Localidades_App.this);
                        task.execute(getString(R.string.mostrar_localidades));
                    }
                });

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
    }



    @Override
    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext(), Opciones_App.class);
        startActivity(i);
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
        finish();
        super.onBackPressed();
    }

    @Override
    public void processFinish(String s) {
        try {
            JSONArray jsonArray = new JSONArray(s);
            basededatos.EliminarLocalidades();
            for (int i = 0; i < jsonArray.length(); i++) {
                basededatos.RegistrarLocalidades(jsonArray.getJSONObject(i).getString("nombre"),jsonArray.getJSONObject(i).getInt("ID"));
                Log.d("localidad: ",jsonArray.getJSONObject(i).getString("nombre"));
            }
            ConsultarLocalidades();
        } catch (Exception ex) {

            ex.printStackTrace();
        }
        swipeRefreshLayout.setRefreshing(false);
    }

    public void ConsultarLocalidades() {
        try {
            Cursor cursor = basededatos.ConsultarLocalidades();
            if (cursor == null) {
                Toast.makeText(this, "Se ha presentado un problema al cargar", Toast.LENGTH_LONG).show();
                return;
            }
            if (cursor.getCount() == 0) {
                //Si no se encuentra ningun elemento descargar información de la nube
                task = new PostResponseAsyncTask(this);
                task.execute(getString(R.string.mostrar_localidades));
            }
            //basededatos.COLUMN_IDLOC,
            String[] columns = new String[]{basededatos.COLUMN_NOMBRELOC};
            int[] boundTo = new int[]{R.id.textTelefono};
            simpleCursorAdapter = new SimpleCursorAdapter(this, R.layout.item_localidad, cursor, columns, boundTo, 0);
            Localidades.setAdapter(simpleCursorAdapter);

        } catch (Exception ex) {
            Toast.makeText(this, "Se ha producido un error "+ex.getMessage(), Toast.LENGTH_LONG).show();
        }
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
            nameValuePairs.add(new BasicNameValuePair("usuario",Preferencias.getString("ID",""))); //ID de usuario logeado
            nameValuePairs.add(new BasicNameValuePair("tipo","5")); //Tipo de campo localidad
            nameValuePairs.add(new BasicNameValuePair("dato1",Localidad.getText().toString())); //Primer dato

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
}
