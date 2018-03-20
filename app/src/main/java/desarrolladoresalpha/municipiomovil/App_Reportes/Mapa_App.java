package desarrolladoresalpha.municipiomovil.App_Reportes;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import desarrolladoresalpha.municipiomovil.R;

public class Mapa_App extends AppCompatActivity implements OnMapReadyCallback,GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerDragListener {
    private static int PETICION_PERMISO_LOCALIZACION = 101;
    private GoogleMap mMap;
    double lat = 0.0;
    double lng = 0.0;
    String GPS,Direccion;
    private Marker marcador;
    boolean Verificador;
    String Tipo;
    String Ubicacion;
    Marker miMarcador = null;
    ImageView Muestra;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reporte_ubicacion);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Muestra = (ImageView)findViewById(R.id.imgMuestra);
        Intent datos= getIntent();
        Bundle b = datos.getExtras();
        if(b!=null)
        {
            switch (b.get("TIPO").toString()){
                case "1":
                    Tipo = "BACHE";
                    break;
                case "2":
                    Tipo = "ANIMAL";
                    break;
                case "3":
                    Tipo = "ILUMINARIA";
                    break;
                case "4":
                    Tipo = "PODADO";
                    break;
                case "5":
                    Tipo = "AGUA";
                    break;
                case "6":
                    Tipo = "BASURA";
                    break;
            }
        }

        ImageView Regresar = (ImageView)findViewById(R.id.botonRegresar);
        Regresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), Reporte_App.class);
                i.putExtra("TIPO", Tipo);
                startActivity(i);
                overridePendingTransition(R.anim.right_in, R.anim.right_out);
                finish();
            }
        });

        FloatingActionButton Guardar =(FloatingActionButton)findViewById(R.id.botonGuardar);
        Guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RelativeLayout Vista = (RelativeLayout)findViewById(R.id.Layout_Vista);
                if (Vista.getVisibility() == View.VISIBLE){
                    Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),android.R.anim.fade_out);
                    Vista.startAnimation(animation);
                    Vista.setVisibility(View.INVISIBLE);
                }else {
                    if(miMarcador != null) {
                        Ubicacion = miMarcador.getPosition().latitude + "," + miMarcador.getPosition().longitude;
                        Intent i = new Intent(getApplicationContext(), Reporte_App.class);
                        i.putExtra("UBICACION", Ubicacion);
                        i.putExtra("TIPO", Tipo);
                        startActivity(i);
                        overridePendingTransition(R.anim.right_in, R.anim.right_out);
                        finish();
                    }else{
                        Toast.makeText(Mapa_App.this,"Estamos obteniendo su ubicación, espere ...",Toast.LENGTH_SHORT).show();
                        miUbicacion();
                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext(), Reporte_App.class);
        startActivity(i);
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
        finish();
        super.onBackPressed();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.tema_original));
        mMap.getUiSettings().setCompassEnabled(false);
        mMap.setOnMarkerDragListener(this);
        this.miUbicacion();
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener()
        {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Ubicacion = (marker.getPosition().latitude+","+marker.getPosition().longitude);
                return true;
            }
        });
    }

    @Override
    public void onMapClick(LatLng latLng) {

    }

    @Override
    public void onMapLongClick(LatLng latLng) {

    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {

    }

    public void locationStart() {
        LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        final boolean gpsEnabled = mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gpsEnabled) {
            Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(settingsIntent);
        }

    }


    private void miUbicacion() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PETICION_PERMISO_LOCALIZACION);
            return;
        } else {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            ActualizarUbicacion(location);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1200,0,locListener);
        }
    }

    private void ActualizarUbicacion(Location location) {
        if (location != null) {
            lat = location.getLatitude();
            lng = location.getLongitude();
            AgregarMarcador(new LatLng(lat,lng),"USUARIO");
        }
    }

    private void AgregarMarcador(LatLng coordenadas,String cTipo) {
        switch (cTipo){
            case "USUARIO":
                if (marcador != null) marcador.remove();
                marcador = mMap.addMarker(new MarkerOptions()
                        .position(coordenadas)
                        .title(Direccion)
                        .snippet("Ubicación actual")
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.selection_usuario_item)));
                if (Verificador == false) {
                    CameraUpdate MiUbicacion = CameraUpdateFactory.newLatLngZoom(coordenadas, 16);
                    mMap.animateCamera(MiUbicacion);
                    AgregarMarcador(new LatLng(lat,lng),Tipo);
                    Verificador = true;
                }
                break;
            case "BACHE":
                miMarcador = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(lat,lng))
                        .anchor(0.5f,0.5f)
                        .draggable(true)
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.selection_bache_item)));
                onMarkerDragStart(miMarcador);
                Muestra.setImageResource(R.mipmap.selection_bache_item);

                break; //Bache
            case "ILUMINARIA":
                miMarcador = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(lat,lng))
                        .anchor(0.5f,0.5f)
                        .draggable(true)
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.selection_iluminaria_item)));
                onMarkerDragStart(miMarcador);
                Muestra.setImageResource(R.mipmap.selection_iluminaria_item);

                break; //Animal
            case "ANIMAL":
                if (marcador != null) marcador.remove();
                miMarcador = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(lat,lng))
                        .anchor(0.5f,0.5f)
                        .draggable(true)
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.selection_animal_item)));
                onMarkerDragStart(miMarcador);
                Muestra.setImageResource(R.mipmap.selection_animal_item);

                break; //Iluminaria
            case "PODADO":
                miMarcador = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(lat,lng))
                        .anchor(0.5f,0.5f)
                        .draggable(true)
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.selection_podado_item)));
                onMarkerDragStart(miMarcador);
                Muestra.setImageResource(R.mipmap.selection_podado_item);
                break; //Podado
            case "AGUA":
                miMarcador = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(lat,lng))
                        .anchor(0.5f,0.5f)
                        .draggable(true)
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.selection_agua_item)));
                onMarkerDragStart(miMarcador);
                Muestra.setImageResource(R.mipmap.selection_agua_item);
                break; //Agua
            case "BASURA":
                miMarcador = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(lat,lng))
                        .anchor(0.5f,0.5f)
                        .draggable(true)
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.selection_basura_item)));
                onMarkerDragStart(miMarcador);
                Muestra.setImageResource(R.mipmap.selection_basura_item);
                break; //Basura
        }
    }


    LocationListener locListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            ActualizarUbicacion(location);
            setLocation(location);
        }

        public void setLocation(Location loc) {
            if (loc.getLatitude() != 0.0 && loc.getLongitude() != 0.0) {
                try {
                    Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                    List<Address> list = geocoder.getFromLocation(
                            loc.getLatitude(), loc.getLongitude(), 1);
                    if (!list.isEmpty()) {
                        Address DirCalle = list.get(0);
                        Direccion = (DirCalle.getAddressLine(0));
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {
            GPS = ("GPS Activado");
            Mensaje();
        }

        @Override
        public void onProviderDisabled(String s) {
            GPS = ("GPS Desactivado");
            locationStart();
            Mensaje();
        }
    };

    public void Mensaje() {
        Toast toast = Toast.makeText(this, GPS, Toast.LENGTH_LONG);
        toast.show();
    }
}
