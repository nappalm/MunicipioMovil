package desarrolladoresalpha.municipiomovil.App_Municipio;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.kosalgeek.asynctask.AsyncResponse;
import com.kosalgeek.asynctask.PostResponseAsyncTask;

import org.json.JSONArray;

import desarrolladoresalpha.municipiomovil.App_Configuraciones.BaseDatos_App;
import desarrolladoresalpha.municipiomovil.App_Principal.Inicio_App;
import desarrolladoresalpha.municipiomovil.R;

public class Numeros_App extends AppCompatActivity implements AsyncResponse {
    private static PostResponseAsyncTask task; //Libreria para descargar datos
    private static BaseDatos_App basededatos; //Base de datos
    SimpleCursorAdapter simpleCursorAdapter; //Adaptador por defecto
    ListView Telefonos;
    private static int PETICION_PERMISO_LLAMADAS = 102;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.numeros_app);
        Telefonos = (ListView) findViewById(R.id.ListTelefonos);
        Telefonos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView Numero = (TextView)view.findViewById(R.id.textTelefono);
                //añadimos permiso para poder hacer llamadas desde el dispositivo
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(Numeros_App.this,
                                new String[]{Manifest.permission.CALL_PHONE},
                                PETICION_PERMISO_LLAMADAS);
                        return;
                    }else{
                        Intent intent = new Intent(Intent.ACTION_CALL);
                        intent.setData(Uri.parse("tel:"+Numero.getText().toString()));
                        startActivity(intent);
                    }
            }
        });
        basededatos = new BaseDatos_App(this, null, null, 1);
        ConsultarTelefonos();
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext(), Inicio_App.class);
        startActivity(i);
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
        finish();
        super.onBackPressed();
    }

    @Override
    public void processFinish(String s) {
        try {
            JSONArray jsonArray = new JSONArray(s);
            basededatos.EliminarTelefonos();
            for (int i = 0; i < jsonArray.length(); i++) {
                basededatos.RegistrarTelefonos(jsonArray.getJSONObject(i).getString("nombre"),jsonArray.getJSONObject(i).getString("telefono"));
            }
            ConsultarTelefonos();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void ConsultarTelefonos() {
        try {
            Cursor cursor = basededatos.ConsultarTelefonos();
            if (cursor == null) {
                Toast.makeText(this, "Se ha presentado un problema al cargar", Toast.LENGTH_LONG).show();
                return;
            }
            if (cursor.getCount() == 0) {
                //Si no se encuentra ningun elemento descargar información de la nube
                task = new PostResponseAsyncTask(this);
                task.execute(getString(R.string.mostrar_telefonos));
            }
            //basededatos.COLUMN_IDLOC,
            String[] columns = new String[]{basededatos.COLUMN_DEPARTAMENTO,basededatos.COLUMN_TELEFONO};
            int[] boundTo = new int[]{R.id.textDepartamento,R.id.textTelefono};
            simpleCursorAdapter = new SimpleCursorAdapter(this, R.layout.item_telefono, cursor, columns, boundTo, 0);
            Telefonos.setAdapter(simpleCursorAdapter);

        } catch (Exception ex) {
            Toast.makeText(this, "Se ha producido un error "+ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

}
