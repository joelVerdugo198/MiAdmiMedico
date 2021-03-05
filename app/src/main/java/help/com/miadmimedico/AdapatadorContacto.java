package help.com.miadmimedico;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class AdapatadorContacto extends BaseAdapter {

    private static LayoutInflater inflater = null;

    Context contexto;
    ArrayList<String> nombre_contacto;
    ArrayList<String> id_contacto;

    public  AdapatadorContacto (Context contexto, ArrayList<String> nombre_contacto, ArrayList<String> id_contacto) {
        this.contexto = contexto;
        this.nombre_contacto = nombre_contacto;
        this.id_contacto = id_contacto;

        inflater = (LayoutInflater) contexto.getSystemService(contexto.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final View listaContacto = inflater.inflate(R.layout.elemento_lista,null);
        TextView nombreContacto = (TextView) listaContacto.findViewById(R.id.nombreContacto);

        nombreContacto.setText(nombre_contacto.get(position));

        nombreContacto.setTag(position);

        nombreContacto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(contexto, EditarContacto.class);
                intent.putExtra("idContacto", id_contacto.get(position) );
                contexto.startActivity(intent);
            }
        });


        return listaContacto;
    }

    @Override
    public int getCount() {
        return nombre_contacto.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


}
