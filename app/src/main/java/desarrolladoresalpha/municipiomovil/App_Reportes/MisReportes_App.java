package desarrolladoresalpha.municipiomovil.App_Reportes;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
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

import desarrolladoresalpha.municipiomovil.App_Adaptadores.Adaptador_Reporte;
import desarrolladoresalpha.municipiomovil.App_Configuraciones.BaseDatos_App;
import desarrolladoresalpha.municipiomovil.App_Principal.Inicio_App;
import desarrolladoresalpha.municipiomovil.R;

public class MisReportes_App extends AppCompatActivity {
    Adaptador_Reporte customAdapter;
    ListView Reportes;
    RequestQueue requestQueue; //Para la utilizacion de Volley
    private static BaseDatos_App basededatos;
    SharedPreferences Preferencias; //Para guardar informacion en cache
    SwipeRefreshLayout swipeRefreshLayout;
    RelativeLayout Cargando,Ninguno;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.misreportes_app);
        Cargando = (RelativeLayout)findViewById(R.id.Layout_Cargando);
        Ninguno = (RelativeLayout)findViewById(R.id.Layout_Ninguno);
        Preferencias = getSharedPreferences("Preferencias", Context.MODE_PRIVATE); //Datos de preferencias para guardar en cache.
        requestQueue = Volley.newRequestQueue(getApplicationContext()); //Libreria de Volley
        basededatos = new BaseDatos_App(this, null, null, 1);
        Reportes = (ListView)findViewById(R.id.ListReportes);

        Reportes.setDivider(null);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeLayout);
        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        MisReportes_App.DescargarReportes Listado = new MisReportes_App.DescargarReportes();
                        Listado.execute();
                    }
                });

        ImageView Regresar = (ImageView)findViewById(R.id.botonRegresar);
        Regresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), Inicio_App.class);
                startActivity(i);
                overridePendingTransition(R.anim.right_in, R.anim.right_out);
                finish();
            }
        });

        //ConsultarReportes();
        MisReportes_App.DescargarReportes Listado = new MisReportes_App.DescargarReportes();
        Listado.execute();
    }
    @Override
    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext(), Inicio_App.class);
        startActivity(i);
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
        finish();
        super.onBackPressed();
    }
    public void ConsultarReportes() {
        try {
            Cursor cursor = basededatos.ConsultarReportes();
            if (cursor == null) {
                Toast.makeText(this, "Se ha presentado un problema al cargar", Toast.LENGTH_LONG).show();
                return;
            }

            if (cursor.getCount() == 0) {
                Ninguno.setVisibility(View.VISIBLE);
            }else{
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),android.R.anim.fade_out);
                Ninguno.setVisibility(View.INVISIBLE);
                Cargando.startAnimation(animation);
                Cargando.setVisibility(View.INVISIBLE);
            }

            String[] columns = new String[]{
                    basededatos.COLUMN_IDREP,
                    basededatos.COLUMN_TIPOREP,
                    basededatos.COLUMN_ESTADOREP
            };

            int[] boundTo = new int[]{
                    R.id.repTipo,
                    R.id.repEstado,
            };

            customAdapter = new Adaptador_Reporte(this, R.layout.item_reporte, cursor, columns, boundTo);
            Reportes.setAdapter(customAdapter);

        } catch (Exception ex) {
            Toast.makeText(this, "Se ha producido un error "+ex, Toast.LENGTH_LONG).show();
        }
    }
    private class DescargarReportes extends AsyncTask<Void, Void, Void> {
        public void onPreExecute() {
            swipeRefreshLayout.setRefreshing(false);
            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),android.R.anim.fade_in);
            Cargando.startAnimation(animation);
            Cargando.setVisibility(View.VISIBLE);
        }
        public void onPostExecute(Void unused) {
        }

        @Override
        protected Void doInBackground(Void... params) {
            StringRequest request = new StringRequest(Request.Method.POST, getString(R.string.mostrar_reportes), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if (!response.equals("null")){
                        try {
                            basededatos.EliminarReportes();
                            JSONArray jsonArray = new JSONArray(response);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                basededatos.RegistrarReportes(
                                        jsonArray.getJSONObject(i).getInt("ID"),
                                        jsonArray.getJSONObject(i).getString("tipo"),
                                        jsonArray.getJSONObject(i).getString("status"));
                            }
                            ConsultarReportes();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else{
                        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),android.R.anim.fade_out);
                        Cargando.startAnimation(animation);
                        Cargando.setVisibility(View.INVISIBLE);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),android.R.anim.fade_out);
                    Cargando.startAnimation(animation);
                    Cargando.setVisibility(View.INVISIBLE);
                    Toast.makeText(MisReportes_App.this,"Verifique que este conectado a internet",Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> parameters = new HashMap<String, String>();
                    parameters.put("usuario", Preferencias.getString("ID",""));
                    return parameters;
                }
            };
            requestQueue.add(request);
            return null;
        }
    }

}
