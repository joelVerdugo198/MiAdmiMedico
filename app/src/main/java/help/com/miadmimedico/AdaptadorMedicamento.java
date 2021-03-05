package help.com.miadmimedico;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class AdaptadorMedicamento extends BaseAdapter {

    private static LayoutInflater inflater = null;

    Context contexto, context;
    ArrayList<String> dato_medicamento;
    ArrayList<String> id_medicamento;

    public  AdaptadorMedicamento (Context contexto, ArrayList<String> dato_medicamento,ArrayList<String> id_medicamento) {
        this.contexto = contexto;
        this.dato_medicamento = dato_medicamento;
        this.id_medicamento = id_medicamento;

        inflater = (LayoutInflater) contexto.getSystemService(contexto.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final View listaMedicamento = inflater.inflate(R.layout.elemento_lista_medicameto,null);

        TextView datoMedicamento = (TextView) listaMedicamento.findViewById(R.id.medicamento);

        datoMedicamento.setText(dato_medicamento.get(position));


        datoMedicamento.setTag(position);

        datoMedicamento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), EditarMedicamento.class);
                intent.putExtra("idMedicamento", id_medicamento.get(position) );
                contexto.startActivity(intent);
            }
        });

        return listaMedicamento;
    }

    @Override
    public int getCount() {
        return dato_medicamento.size();
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
