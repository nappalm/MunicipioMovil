package desarrolladoresalpha.municipiomovil.App_Reportes;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import desarrolladoresalpha.municipiomovil.App_Principal.Inicio_App;
import desarrolladoresalpha.municipiomovil.R;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class Reporte_App extends AppCompatActivity{
    private static final int RESULT_LOAD_IMAGE = 1;
    EditText repDescripcion;
    SharedPreferences Preferencias;
    SimpleDateFormat Fecha = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat Hora = new SimpleDateFormat("hh:mm:ss");
    TextView[] verificadores;
    ImageView Fotografia;
    int Tipo;
    String Ubicacion;
    Bitmap Imagen;
    String base64;



    //Seleccion de fotos o captura

    private static String APP_DIRECTORY = "MyPictureApp/";
    private static String MEDIA_DIRECTORY = APP_DIRECTORY + "PictureApp";

    private final int MY_PERMISSIONS = 100;
    private final int PHOTO_CODE = 200;
    private final int SELECT_PICTURE = 300;
    private String mPath;
    RelativeLayout Layout_Reporte;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reporte_app);

        Fotografia = new ImageView(this);
        Preferencias = getSharedPreferences("Preferencias",Context.MODE_PRIVATE); //Datos de preferencias para guardar en cache.
        verificadores = new TextView[]{(TextView)findViewById(R.id.checkDescripcion),(TextView)findViewById(R.id.checkFoto),(TextView)findViewById(R.id.checkUbicacion)};
        Layout_Reporte = (RelativeLayout)findViewById(R.id.Layout_Reporte);

        Intent datos= getIntent();
        Bundle b = datos.getExtras();
        if(b!=null)
        {
            TextView Titulo = (TextView)findViewById(R.id.repTitulo);
            switch (b.get("TIPO").toString()){
                case "BACHE":
                        Titulo.setText("Reportar un bache");
                        Tipo = 1;
                    break;
                case "ANIMAL":
                        Titulo.setText("Animal muerto");
                        Tipo = 2;
                    break;
                case "ILUMINARIA":
                        Titulo.setText("Reportar iluminaria");
                        Tipo = 3;
                    break;
                case "PODADO":
                        Titulo.setText("Podar areas verdes");
                        Tipo = 4;
                break;
                case "AGUA":
                        Titulo.setText("Servicios de agua");
                        Tipo = 5;
                    break;
                case "BASURA":
                        Titulo.setText("Reportar recolección");
                        Tipo = 6;
                    break;
            }
            if (b.get("UBICACION")!= null) {
                Ubicacion = b.get("UBICACION").toString();
                verificadores[2].setText("Listo");
            }
        }

        repDescripcion = (EditText)findViewById(R.id.descripcionReporte);
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


        final RelativeLayout Layout_Descripcion = (RelativeLayout)findViewById(R.id.Layout_Descripcion);
        final LinearLayout Descripcion = (LinearLayout)findViewById(R.id.botonDescripcion);
        Descripcion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),android.R.anim.fade_in);
                Layout_Descripcion.startAnimation(animation);
                Layout_Descripcion.setVisibility(View.VISIBLE);
            }
        });

        FloatingActionButton CDescripcion = (FloatingActionButton)findViewById(R.id.cerrarDescripcion);
        CDescripcion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!repDescripcion.getText().toString().isEmpty()){
                        verificadores[0].setText("Listo");
                }else{
                    verificadores[0].setText("Sin descripción");
                }
                hideSoftKeyboard();
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),android.R.anim.fade_out);
                Layout_Descripcion.startAnimation(animation);
                Layout_Descripcion.setVisibility(View.INVISIBLE);
            }
        });

        RelativeLayout Enviar = (RelativeLayout)findViewById(R.id.botonEnviar);
        Enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!verificadores[0].getText().toString().equals("Sin descripción") && !verificadores[1].getText().toString().equals("Seleccionar foto") && !verificadores[2].getText().toString().equals("Seleccionar"))
                {
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    Imagen.compress(Bitmap.CompressFormat.JPEG,25,byteArrayOutputStream);
                    base64 = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
                    new Insertar(Reporte_App.this).execute();
                }else{
                    Toast.makeText(getApplicationContext(),"Al parecer falta ingresar información",Toast.LENGTH_SHORT).show();
                    Vibrator v = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                    v.vibrate(500);
                }
            }
        });

        LinearLayout Galeria = (LinearLayout)findViewById(R.id.botonFoto);
        Galeria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mayRequestStoragePermission()){
                    seleccionarMedio();
                }
            }
        });

        LinearLayout Geolocalizar = (LinearLayout)findViewById(R.id.botonUbicacion);
        Geolocalizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getApplicationContext(), Mapa_App.class);
                i.putExtra("TIPO", Tipo);
                startActivity(i);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });

    }

    private boolean mayRequestStoragePermission() {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return true;

        if((checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) &&
                (checkSelfPermission(CAMERA) == PackageManager.PERMISSION_GRANTED))
            return true;

        if((shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE)) || (shouldShowRequestPermissionRationale(CAMERA))){
            Snackbar.make(Layout_Reporte, "Los permisos son necesarios para poder usar la aplicación",
                    Snackbar.LENGTH_INDEFINITE).setAction(android.R.string.ok, new View.OnClickListener() {
                @TargetApi(Build.VERSION_CODES.M)
                @Override
                public void onClick(View v) {
                    requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, CAMERA}, MY_PERMISSIONS);
                }
            });
        }else{
            requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, CAMERA}, MY_PERMISSIONS);
        }

        return false;
    }

    private void openCamera() {
        File file = new File(Environment.getExternalStorageDirectory(), MEDIA_DIRECTORY);
        boolean isDirectoryCreated = file.exists();

        if(!isDirectoryCreated)
            isDirectoryCreated = file.mkdirs();

        if(isDirectoryCreated){
            Long timestamp = System.currentTimeMillis() / 1000;
            String imageName = timestamp.toString() + ".jpg";
            mPath = Environment.getExternalStorageDirectory() + File.separator + MEDIA_DIRECTORY
                    + File.separator + imageName;


            File newFile = new File(mPath);

            //TODO agregamos esta linea! para nougat!
            String authorities = getApplicationContext().getPackageName() + ".fileprovider";
            Uri imageUri = FileProvider.getUriForFile(this,authorities,newFile);


            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            //intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(newFile));
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(intent, PHOTO_CODE);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("file_path", mPath);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mPath = savedInstanceState.getString("file_path");
    }

    private void seleccionarMedio() {
        LayoutInflater inflater = LayoutInflater.from(this);
        //vista que contiene el diseño
        View login_layout = inflater.inflate(R.layout.vista_foto,null);
        //botones de seleccion de tipo de captura
        LinearLayout Galeria = (LinearLayout)login_layout.findViewById(R.id.botonGaleria);
        LinearLayout Tomar = (LinearLayout)login_layout.findViewById(R.id.botonTomar);
        //Creacion de AlertDialog
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Selecciona una foto");
        alertDialog.setMessage("Puedes tomar una foto o cargar una antes previamente tomada");
        alertDialog.setView(login_layout);

        //Botones dentro del alertDialog
        final AlertDialog show = alertDialog.show();
        Galeria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(intent.createChooser(intent, "Selecciona app de imagen"), SELECT_PICTURE);
                //Cerrar dialogo para evitar que se mantenga una ves tomada la foto
                show.dismiss();

            }
        });

        Tomar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCamera();
                show.dismiss();
            }
        });

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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        BitmapFactory.Options opts = new BitmapFactory.Options ();
        opts.inSampleSize = 2;   // for 1/2 the image to be loaded

        if(resultCode == RESULT_OK){
            switch (requestCode){
                case PHOTO_CODE:
                    MediaScannerConnection.scanFile(this,
                            new String[]{mPath}, null,
                            new MediaScannerConnection.OnScanCompletedListener() {
                                @Override
                                public void onScanCompleted(String path, Uri uri) {
                                    Log.i("ExternalStorage", "Scanned " + path + ":");
                                    Log.i("ExternalStorage", "-> Uri = " + uri);
                                }
                            });

                    Bitmap bitmap = BitmapFactory.decodeFile(mPath);
                    Fotografia.setImageBitmap(bitmap);
                    Imagen =((BitmapDrawable) Fotografia.getDrawable()).getBitmap();
                    verificadores[1].setText("Listo (Capturada)");
                    break;
                case SELECT_PICTURE:
                    Uri path = data.getData();
                    Fotografia.setImageURI(path);
                    Imagen =((BitmapDrawable) Fotografia.getDrawable()).getBitmap();
                    verificadores[1].setText("Listo (De Galeria)");
                    break;
            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == MY_PERMISSIONS){
            if(grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(Reporte_App.this, "Permisos aceptados", Toast.LENGTH_SHORT).show();
            }
        }else{
            showExplanation();
        }
    }

    private void showExplanation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Reporte_App.this);
        builder.setTitle("Permisos denegados");
        builder.setMessage("Para usar las funciones de la app necesitas aceptar los permisos");
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });

        builder.show();
    }

    //Envio de informacion por HTTP a la nube
    private boolean Reporte_Nuevo(){
        HttpClient httpClient;
        HttpPost httpPost;
        httpClient = new DefaultHttpClient();
        httpPost = new HttpPost(getString(R.string.registrar_reporte));//url del servidor

        try {
            List<NameValuePair> nameValuePairs;
            //empezamos añadir nuestros datos
            nameValuePairs = new ArrayList<NameValuePair>(4);
            nameValuePairs.add(new BasicNameValuePair("usuario",Preferencias.getString("ID",""))); //ID de usuario logeado
            nameValuePairs.add(new BasicNameValuePair("fecha",Fecha.format(new Date()))); //Fecha de envio
            nameValuePairs.add(new BasicNameValuePair("hora",Hora.format(new Date()))); //Hora de envio
            nameValuePairs.add(new BasicNameValuePair("tipo",String.valueOf(Tipo))); //Bache,Iluminaria,....
            Log.d(">>>",String.valueOf(Tipo));
            nameValuePairs.add(new BasicNameValuePair("localizacion",Ubicacion)); //Coordenadas GPS
            nameValuePairs.add(new BasicNameValuePair("descripcion",repDescripcion.getText().toString())); //Descripcion del usuario
            nameValuePairs.add(new BasicNameValuePair("imagen",base64)); //Imagen de reporte


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
            dialog.setMessage("Enviando información ...");
            dialog.show();
        }
        @Override
        protected void onPostExecute(String s) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
        protected String doInBackground(String... params) {
            if(Reporte_Nuevo()) {
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "Se ha guardado con exito", Toast.LENGTH_LONG).show();
                        Intent i = new Intent(getApplicationContext(), Inicio_App.class);
                        startActivity(i);
                        overridePendingTransition(R.anim.right_in, R.anim.right_out);
                        finish();
                        /*verificadores[0].setText("Sin descripción");
                        verificadores[1].setText("Seleccionar foto");
                        verificadores[2].setText("Seleccionar");*/

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

    public void hideSoftKeyboard() {
        //Ocultar teclado
        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

}
