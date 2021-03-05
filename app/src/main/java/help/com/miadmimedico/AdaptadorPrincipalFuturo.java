package help.com.miadmimedico;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class AdaptadorPrincipalFuturo extends BaseAdapter {

    private static LayoutInflater inflater = null;

    Context contexto;
    ArrayList<String> dato_alarma_futura;
    ArrayList<String> hora_alarma_futura;

    public  AdaptadorPrincipalFuturo (Context contexto, ArrayList<String> dato_alarma_futura,
                                      ArrayList<String> hora_alarma_futura)
    {
        this.contexto = contexto;
        this.dato_alarma_futura = dato_alarma_futura;
        this.hora_alarma_futura = hora_alarma_futura;

        inflater = (LayoutInflater) contexto.getSystemService(contexto.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View listaAlarmaFutura = inflater.inflate(R.layout.elemento_lista_alarma_futura,null);

        TextView datosMedicamentoAlarmaFutura = (TextView) listaAlarmaFutura.findViewById(R.id.datosMedicamentoAlarmaFutura);
        TextView horaMedicamentoAlarmaFutura = (TextView) listaAlarmaFutura.findViewById(R.id.horaMedicamentoAlarmaFutura);

        datosMedicamentoAlarmaFutura.setText(dato_alarma_futura.get(position));
        horaMedicamentoAlarmaFutura.setText(hora_alarma_futura.get(position));

        return listaAlarmaFutura;
    }

    @Override
    public int getCount() {
        return dato_alarma_futura.size();
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
