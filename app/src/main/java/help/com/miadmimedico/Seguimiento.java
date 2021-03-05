package help.com.miadmimedico;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Seguimiento extends AppCompatActivity {

    ClaseSeguimiento seguimiento = new ClaseSeguimiento();
    ClaseUsuario usuario = new ClaseUsuario();

    FirebaseAuth mAuth;
    DatabaseReference baseDatos;

    ExpandableListView listaSeguimiento;
    AdaptadorSeguimiento adaptadorSeguimiento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seguimiento);

        Toolbar toolbar;

        mAuth = FirebaseAuth.getInstance();
        baseDatos = FirebaseDatabase.getInstance().getReference();
        usuario.setIdUsuario(mAuth.getCurrentUser().getUid());

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        listaSeguimiento = (ExpandableListView) findViewById(R.id.listaSeguimiento);


        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Seguimiento");


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Principal.class));
            }
        });

        cargarDato();

    }

    private void cargarDato(){
        Map<String, ArrayList<String>> lista_medicamento = new HashMap<>();
        Map<String, ArrayList<String>> barra_progreso_medicamento = new HashMap<>();
        ArrayList<String> lista_tipo_seguimiento = new ArrayList<>();
        final ArrayList<String> cardiologo = new ArrayList<>();
        final ArrayList<String> urologo = new ArrayList<>();
        final ArrayList<String> dermatologo = new ArrayList<>();
        final ArrayList<String> oncologo = new ArrayList<>();
        final ArrayList<String> neurologo = new ArrayList<>();
        final ArrayList<String> psiquiatria = new ArrayList<>();
        final ArrayList<String> medicoFamiliar = new ArrayList<>();
        final ArrayList<String> medicamento = new ArrayList<>();

        final ArrayList<String> barra_cardiologo = new ArrayList<>();
        final ArrayList<String> barra_urologo = new ArrayList<>();
        final ArrayList<String> barra_dermatologo = new ArrayList<>();
        final ArrayList<String> barra_oncologo = new ArrayList<>();
        final ArrayList<String> barra_neurologo = new ArrayList<>();
        final ArrayList<String> barra_psiquiatria = new ArrayList<>();
        final ArrayList<String> barra_medicoFamiliar = new ArrayList<>();
        final ArrayList<String> barra_medicamento = new ArrayList<>();

        baseDatos.child("seguimiento").child(usuario.getIdUsuario()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                cardiologo.clear();
                urologo.clear();
                dermatologo.clear();
                 medicamento.clear();
                 oncologo.clear();
                 neurologo.clear();
                 psiquiatria.clear();
                 medicoFamiliar.clear();

                barra_cardiologo.clear();
                barra_urologo.clear();
                barra_dermatologo.clear();
                barra_medicamento.clear();
                barra_oncologo.clear();
                barra_neurologo.clear();
                barra_psiquiatria.clear();
                barra_medicoFamiliar.clear();
                if (dataSnapshot.exists()){
                    for (DataSnapshot objSnapshot : dataSnapshot.getChildren()){
                        seguimiento.setNombreMedicamento(objSnapshot.child("medicamento").getValue().toString());
                        seguimiento.setCantidadTotal(objSnapshot.child("cantidadTotal").getValue().toString());
                        seguimiento.setCantidadTomada(objSnapshot.child("cantidadTomada").getValue().toString());

                        if (objSnapshot.child("tipo").getValue().toString().equals("Cardiólogo")) {
                            cardiologo.add("Medicamento: " + seguimiento.getNombreMedicamento() + "\n" + "Avance: " +
                                    seguimiento.getCantidadTomada() + "/" + seguimiento.getCantidadTotal());
                            barra_cardiologo.add(String.valueOf(Integer.parseInt(seguimiento.getCantidadTomada()) * 100 /
                                    (Integer.parseInt(seguimiento.getCantidadTotal()))));
                        }
                        else if (objSnapshot.child("tipo").getValue().toString().equals("Dermatólogo")){
                            dermatologo.add("Medicamento: " + seguimiento.getNombreMedicamento() + "\n" + "Avance: " +
                                    seguimiento.getCantidadTomada() + "/" + seguimiento.getCantidadTotal());
                            barra_dermatologo.add(String.valueOf(Integer.parseInt(seguimiento.getCantidadTomada()) * 100 /
                                    (Integer.parseInt(seguimiento.getCantidadTotal()))));
                        }
                        else if (objSnapshot.child("tipo").getValue().toString().equals("Urólogo")){
                            urologo.add("Medicamento: " + seguimiento.getNombreMedicamento() + "\n" + "Avance: " +
                                    seguimiento.getCantidadTomada() + "/" + seguimiento.getCantidadTotal());
                            barra_urologo.add(String.valueOf(Integer.parseInt(seguimiento.getCantidadTomada()) * 100 /
                                    (Integer.parseInt(seguimiento.getCantidadTotal()))));
                        }
                        else if (objSnapshot.child("tipo").getValue().toString().equals("Medicamento")){
                            medicamento.add("Medicamento: " + seguimiento.getNombreMedicamento() + "\n" + "Avance: " +
                                    seguimiento.getCantidadTomada() + "/" + seguimiento.getCantidadTotal());
                            barra_medicamento.add(String.valueOf(Integer.parseInt(seguimiento.getCantidadTomada()) * 100 /
                                    (Integer.parseInt(seguimiento.getCantidadTotal()))));
                        }
                        else if (objSnapshot.child("tipo").getValue().toString().equals("Oncólogo")){
                            oncologo.add("Medicamento: " + seguimiento.getNombreMedicamento() + "\n" + "Avance: " +
                                    seguimiento.getCantidadTomada() + "/" + seguimiento.getCantidadTotal());
                            barra_oncologo.add(String.valueOf(Integer.parseInt(seguimiento.getCantidadTomada()) * 100 /
                                    (Integer.parseInt(seguimiento.getCantidadTotal()))));
                        }
                        else if (objSnapshot.child("tipo").getValue().toString().equals("Neurólogo")){
                            neurologo.add("Medicamento: " + seguimiento.getNombreMedicamento() + "\n" + "Avance: " +
                                    seguimiento.getCantidadTomada() + "/" + seguimiento.getCantidadTotal());
                            barra_neurologo.add(String.valueOf(Integer.parseInt(seguimiento.getCantidadTomada()) * 100 /
                                    (Integer.parseInt(seguimiento.getCantidadTotal()))));
                        }
                        else if (objSnapshot.child("tipo").getValue().toString().equals("Psiquiatría")){
                            psiquiatria.add("Medicamento: " + seguimiento.getNombreMedicamento() + "\n" + "Avance: " +
                                    seguimiento.getCantidadTomada() + "/" + seguimiento.getCantidadTotal());
                            barra_psiquiatria.add(String.valueOf(Integer.parseInt(seguimiento.getCantidadTomada()) * 100 /
                                    (Integer.parseInt(seguimiento.getCantidadTotal()))));
                        }
                        else if (objSnapshot.child("tipo").getValue().toString().equals("Médico familiar")){
                            medicoFamiliar.add("Medicamento: " + seguimiento.getNombreMedicamento() + "\n" + "Avance: " +
                                    seguimiento.getCantidadTomada() + "/" + seguimiento.getCantidadTotal());
                            barra_medicoFamiliar.add(String.valueOf(Integer.parseInt(seguimiento.getCantidadTomada()) * 100 /
                                    (Integer.parseInt(seguimiento.getCantidadTotal()))));
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        lista_tipo_seguimiento.add("Medicamento");
        lista_tipo_seguimiento.add("Cardiólogo");
        lista_tipo_seguimiento.add("Urólogo");
        lista_tipo_seguimiento.add("Dermatólogo");
        lista_tipo_seguimiento.add("Oncólogo");
        lista_tipo_seguimiento.add("Neurólogo");
        lista_tipo_seguimiento.add("Psiquiatría");
        lista_tipo_seguimiento.add("Médico familiar");

        lista_medicamento.put(lista_tipo_seguimiento.get(0),medicamento);
        lista_medicamento.put(lista_tipo_seguimiento.get(1),cardiologo);
        lista_medicamento.put(lista_tipo_seguimiento.get(2),urologo);
        lista_medicamento.put(lista_tipo_seguimiento.get(3),dermatologo);
        lista_medicamento.put(lista_tipo_seguimiento.get(4),oncologo);
        lista_medicamento.put(lista_tipo_seguimiento.get(5),neurologo);
        lista_medicamento.put(lista_tipo_seguimiento.get(6),psiquiatria);
        lista_medicamento.put(lista_tipo_seguimiento.get(7),medicoFamiliar);

        barra_progreso_medicamento.put(lista_tipo_seguimiento.get(0),barra_medicamento);
        barra_progreso_medicamento.put(lista_tipo_seguimiento.get(1),barra_cardiologo);
        barra_progreso_medicamento.put(lista_tipo_seguimiento.get(2),barra_urologo);
        barra_progreso_medicamento.put(lista_tipo_seguimiento.get(3),barra_dermatologo);
        barra_progreso_medicamento.put(lista_tipo_seguimiento.get(4),barra_oncologo);
        barra_progreso_medicamento.put(lista_tipo_seguimiento.get(5),barra_neurologo);
        barra_progreso_medicamento.put(lista_tipo_seguimiento.get(6),barra_psiquiatria);
        barra_progreso_medicamento.put(lista_tipo_seguimiento.get(7),barra_medicoFamiliar);


        adaptadorSeguimiento = new AdaptadorSeguimiento(this,lista_tipo_seguimiento,lista_medicamento, barra_progreso_medicamento );
        listaSeguimiento.setAdapter(adaptadorSeguimiento);
    }

}
