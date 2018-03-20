package desarrolladoresalpha.municipiomovil.App_Adaptadores;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import desarrolladoresalpha.municipiomovil.R;

public class Adaptador_Reporte extends SimpleCursorAdapter {
    private Context mContext;
    private Context appContext;
    private int layout;
    private Cursor c;
    private final LayoutInflater inflater;

    public Adaptador_Reporte(Context context, int layout, Cursor c, String[] from, int[] to) {
        super(context, layout, c, from, to);
        this.layout = layout;
        this.mContext = context;
        this.inflater = LayoutInflater.from(context);
        this.c = c;
    }

    @Override
    public View newView (Context context, Cursor cursor, ViewGroup parent) {
        return inflater.inflate(layout, null);
    }



    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        String status =c.getString(c.getColumnIndexOrThrow("estado"));
        String type = c.getString(c.getColumnIndexOrThrow("tipo"));

        TextView Tipo = (TextView) view.findViewById(R.id.repTipo);
        TextView Estado = (TextView) view.findViewById(R.id.repEstado);
        ImageView Reporte = (ImageView)view.findViewById(R.id.imgReporte);

        switch (status){
            case "0":
                Estado.setText("En espera");
                break;
            case "1":
                Estado.setText("Atendiendo");
                break;
            case "2":
                Estado.setText("Terminado");
                break;
        }

        switch (type){
            case "1":
                Tipo.setText("Reporde bache");
                Reporte.setImageResource(R.mipmap.selection_bache_item);
                break;
            case "2":
                Tipo.setText("Animal muerto");
                Reporte.setImageResource(R.mipmap.selection_animal_item);
                break;
            case "3":
                Tipo.setText("Reporte iluminaria");
                Reporte.setImageResource(R.mipmap.selection_iluminaria_item);
                break;
            case "4":
                Tipo.setText("Podado de areas");
                Reporte.setImageResource(R.mipmap.selection_podado_item);
                break;
            case "5":
                Tipo.setText("Reporte de agua");
                Reporte.setImageResource(R.mipmap.selection_agua_item);
                break;
            case "6":
                Tipo.setText("Problemas con recolección");
                Reporte.setImageResource(R.mipmap.selection_basura_item);
                break;
        }
    }
}