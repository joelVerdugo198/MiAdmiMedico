package help.com.miadmimedico;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Map;

public class AdaptadorSeguimiento extends BaseExpandableListAdapter {

    private ArrayList<String> lista_tipo_seguimiento;
    private Map<String, ArrayList<String>> lista_medicamento;
    private Map<String, ArrayList<String>> barra_progreso_medicamento;
    private Context contexto;

    public AdaptadorSeguimiento( Context contexto, ArrayList<String> lista_tipo_seguimiento,
                                 Map<String, ArrayList<String>> lista_medicamento, Map<String, ArrayList<String>> barra_progreso_medicamento) {
        this.lista_tipo_seguimiento = lista_tipo_seguimiento;
        this.lista_medicamento = lista_medicamento;
        this.barra_progreso_medicamento = barra_progreso_medicamento;
        this.contexto = contexto;
    }

    @Override
    public int getGroupCount() {
        return lista_tipo_seguimiento.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return lista_medicamento.get(lista_tipo_seguimiento.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return lista_tipo_seguimiento.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return lista_medicamento.get(lista_tipo_seguimiento.get(groupPosition)).get(childPosition);
    }

    public Object barraProgreso(int groupPosition, int childPosition){
        return barra_progreso_medicamento.get(lista_tipo_seguimiento.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        String tituloTipoSeguimiento = (String) getGroup(groupPosition);
        convertView = LayoutInflater.from(contexto).inflate(R.layout.elemento_tipo_seguimiento, null);


        TextView tipoSeguimiento = (TextView) convertView.findViewById(R.id.nombreTipoSeguimiento);

        tipoSeguimiento.setText(tituloTipoSeguimiento);


        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        String item = (String) getChild(groupPosition, childPosition);
        String progreso = (String) barraProgreso(groupPosition, childPosition);
        convertView = LayoutInflater.from(contexto).inflate(R.layout.elemento_lista_seguimiento, null);

        TextView datoSeguimiento = (TextView) convertView.findViewById(R.id.datoSeguimiento);
        ProgressBar barraProgreso = (ProgressBar) convertView.findViewById(R.id.progressBar);

        datoSeguimiento.setText(item);

        barraProgreso.setProgress(Integer.parseInt(progreso));

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
