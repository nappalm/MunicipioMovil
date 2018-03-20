package desarrolladoresalpha.municipiomovil.App_Municipio;

import android.content.Intent;
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
import android.widget.SimpleCursorAdapter;
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

import desarrolladoresalpha.municipiomovil.App_Configuraciones.BaseDatos_App;
import desarrolladoresalpha.municipiomovil.App_Principal.Inicio_App;
import desarrolladoresalpha.municipiomovil.R;

public class Noticias_App extends AppCompatActivity {
    ListView Noticias;
    RequestQueue requestQueue; //Para la utilizacion de Volley
    private static BaseDatos_App basededatos;
    SwipeRefreshLayout swipeRefreshLayout;
    RelativeLayout Cargando,Ninguno;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.noticias_app);
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

        Cargando = (RelativeLayout)findViewById(R.id.Layout_Cargando);
        Ninguno = (RelativeLayout)findViewById(R.id.Layout_Ninguno);
        requestQueue = Volley.newRequestQueue(getApplicationContext()); //Libreria de Volley
        basededatos = new BaseDatos_App(this, null, null, 1);
        Noticias = (ListView)findViewById(R.id.ListNoticias);

        Noticias.setDivider(null);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeLayout);
        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        Noticias_App.DescargarNoticias Listado = new Noticias_App.DescargarNoticias();
                        Listado.execute();
                    }
                });

        Noticias_App.DescargarNoticias Listado = new Noticias_App.DescargarNoticias();
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

    public void ConsultarNoticias() {
        try {
            Cursor cursor = basededatos.ConsultarNoticias();
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
                    basededatos.COLUMN_ENCABEZADO,
                    basededatos.COLUMN_FECHA,
                    basededatos.COLUMN_TEXTO,
                    basededatos.COLUMN_AUTOR,

            };

            int[] boundTo = new int[]{
                    R.id.not_Emcabezado,
                    R.id.not_Fecha,
                    R.id.not_Texto,
                    R.id.not_Autor
            };

            SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,R.layout.item_noticia,cursor,columns,boundTo);
            Noticias.setAdapter(adapter);

        } catch (Exception ex) {
            Toast.makeText(this, "Se ha producido un error "+ex, Toast.LENGTH_LONG).show();
        }
    }
    private class DescargarNoticias extends AsyncTask<Void, Void, Void> {
        public void onPreExecute() {
            swipeRefreshLayout.setRefreshing(false);
            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in);
            Cargando.startAnimation(animation);
            Cargando.setVisibility(View.VISIBLE);
        }

        public void onPostExecute(Void unused) {
        }


        @Override
        protected Void doInBackground(Void... params) {
            StringRequest request = new StringRequest(Request.Method.POST, getString(R.string.municipio_noticias), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if (!response.equals("null")){
                        try {
                            basededatos.EliminarNoticias();
                            JSONArray jsonArray = new JSONArray(response);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                basededatos.RegistrarNoticias(
                                        jsonArray.getJSONObject(i).getInt("folio"),
                                        jsonArray.getJSONObject(i).getString("encabezado"),
                                        jsonArray.getJSONObject(i).getString("fecha"),
                                        jsonArray.getJSONObject(i).getString("texto"),
                                        jsonArray.getJSONObject(i).getString("autor"));
                            }
                            ConsultarNoticias();
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
                }
            })

            {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String,String> params = new HashMap<String, String>();
                    params.put("Content-Type","application/x-www-form-urlencoded");

                    return params;
                }


                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> parameters = new HashMap<String, String>();
                    return parameters;
                }
            };
            requestQueue.add(request);
            return null;
        }
    }
}



