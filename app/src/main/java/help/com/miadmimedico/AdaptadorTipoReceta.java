package help.com.miadmimedico;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdaptadorTipoReceta extends BaseExpandableListAdapter {

    private ArrayList<String> lista_tipo_receta;
    private Map<String, ArrayList<String>> id_medicamento;
    private Map<String, ArrayList<String>> lista_medicamento;
    private Context contexto;

    FirebaseAuth mAuth;
    DatabaseReference baseDatos;


    public AdaptadorTipoReceta(Context contexto, ArrayList<String> lista_tipo_receta, Map<String, ArrayList<String>> lista_medicamento, Map<String, ArrayList<String>> id_medicamento) {
        this.lista_tipo_receta = lista_tipo_receta;
        this.lista_medicamento = lista_medicamento;
        this.id_medicamento = id_medicamento;
        this.contexto = contexto;
    }

    public Object idMedicamento(int groupPosition, int childPosition) {
        return id_medicamento.get(lista_tipo_receta.get(groupPosition)).get(childPosition);
    }

    @Override
    public int getGroupCount() {
        return lista_tipo_receta.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return lista_medicamento.get(lista_tipo_receta.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return lista_tipo_receta.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return lista_medicamento.get(lista_tipo_receta.get(groupPosition)).get(childPosition);
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

        String tituloTipoReceta = (String) getGroup(groupPosition);
        convertView = LayoutInflater.from(contexto).inflate(R.layout.elemento_tipo_receta, null);

        TextView tipoReceta = (TextView) convertView.findViewById(R.id.nombreTipoReceta);

        tipoReceta.setText(tituloTipoReceta);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        mAuth = FirebaseAuth.getInstance();
        baseDatos = FirebaseDatabase.getInstance().getReference();

        final String idUsuario = mAuth.getCurrentUser().getUid();
        String item = (String) getChild(groupPosition, childPosition);
        final String idMedicamento = (String) idMedicamento(groupPosition, childPosition);

        convertView = LayoutInflater.from(contexto).inflate(R.layout.elemento_lista_medicamento_receta, null);

        final TextView datoReceta = (TextView) convertView.findViewById(R.id.medicamentoReceta);
        datoReceta.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);


        datoReceta.setText(item);

        datoReceta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), EditarMedicamentoReceta.class);
                intent.putExtra("idMedicamento", idMedicamento );
                contexto.startActivity(intent);
            }
        });

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}