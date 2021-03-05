package help.com.miadmimedico;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Medicamento extends AppCompatActivity {

    ClaseMedicamento Medicamento = new ClaseMedicamento();
    ClaseUsuario usuario = new ClaseUsuario();

    FirebaseAuth mAuth;
    DatabaseReference baseDatos;

    ArrayList<String> id_medicamento = new ArrayList<>();
    ArrayList<String> datos_medicamento = new ArrayList<>();;

    ImageView imagenMedicamento;
    SearchView buscarMedicamento;
    TextView avisoMedicamentoVacio;
    ListView listaMedicamento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicamento);

        Toolbar toolbar;

        mAuth = FirebaseAuth.getInstance();
        baseDatos = FirebaseDatabase.getInstance().getReference();

        usuario.setIdUsuario(mAuth.getCurrentUser().getUid());

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        buscarMedicamento = (SearchView) findViewById(R.id.buscarMedicamento);
        imagenMedicamento = (ImageView) findViewById(R.id.imagenMedicamento);
        avisoMedicamentoVacio = (TextView) findViewById(R.id.avisoMedicamentoVacio);
        listaMedicamento = (ListView) findViewById(R.id.listaMedicamento);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Medicamento");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Principal.class));
                finish();
            }
        });

        mostrarAvisoMedicamentoVacio();

        listarMedicamento();

    }

    public void mostrarAvisoMedicamentoVacio(){

        baseDatos.child("medicamento").child(usuario.getIdUsuario()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    imagenMedicamento.setVisibility(View.GONE);
                    avisoMedicamentoVacio.setVisibility(View.GONE);
                    buscarMedicamento.setVisibility(View.VISIBLE);
                }
                else{
                    buscarMedicamento.setVisibility(View.GONE);
                    imagenMedicamento.setVisibility(View.VISIBLE);
                    avisoMedicamentoVacio.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public boolean onCreateOptionsMenu (Menu menu){
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    public boolean onOptionsItemSelected (MenuItem menuItem){
        switch (menuItem.getItemId()){
            case R.id.agregar:
                Intent intent = new Intent(Medicamento.this,AgregarMedicamento.class);
                startActivity(intent);
                finish();
                break;
        }
        return true;
    }

    public void listarMedicamento(){
        baseDatos.child("medicamento").child(usuario.getIdUsuario()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                id_medicamento.clear();
                datos_medicamento.clear();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot objSnapshot : dataSnapshot.getChildren()) {
                        Medicamento.setNombreMedicamento((objSnapshot.child("medicamento").getValue().toString()));
                        Medicamento.setViaAdministracion(objSnapshot.child("viaAdministracion").getValue().toString());
                        Medicamento.setCantidadPorcion(objSnapshot.child("cantidadPorcion").getValue().toString());
                        Medicamento.setTipoPorcion(objSnapshot.child("tipoPorcion").getValue().toString());
                        Medicamento.setIntervaloHora(objSnapshot.child("intervaloHora").getValue().toString());
                        Medicamento.setCantidadMedicamento(objSnapshot.child("cantidadMedicamento").getValue().toString());
                        Medicamento.setDias(objSnapshot.child("dias").getValue().toString());

                        id_medicamento.add((objSnapshot.child("idMedicamento").getValue().toString()));
                        datos_medicamento.add("Medicamento: " + Medicamento.getNombreMedicamento() + "\n" + "Vía de administración: "
                                + Medicamento.getViaAdministracion() + "\n" + "Porción: " + Medicamento.getCantidadPorcion()
                                + Medicamento.getTipoPorcion() + "\n" + "Cantidad: " + Medicamento.getCantidadMedicamento()
                                + "\n" + "Cada " + Medicamento.getIntervaloHora() + " horas por " + Medicamento.getDias() + " días");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        }) ;

        listaMedicamento.setAdapter(new AdaptadorMedicamento(this, datos_medicamento, id_medicamento));

        buscarMedicamento.setOnQueryTextListener(new android.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String letra) {
                buscarMedicamento(letra);
                return true;
            }
        });
    }

    public void buscarMedicamento(final String letra){
        baseDatos.child("medicamento").child(usuario.getIdUsuario()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String datosMedicamento = "";
                id_medicamento.clear();
                datos_medicamento.clear();
                if(dataSnapshot.exists()) {
                    for (DataSnapshot objSnapshot : dataSnapshot.getChildren()) {
                        Medicamento.setNombreMedicamento((objSnapshot.child("medicamento").getValue().toString()));
                        Medicamento.setViaAdministracion(objSnapshot.child("viaAdministracion").getValue().toString());
                        Medicamento.setCantidadPorcion(objSnapshot.child("cantidadPorcion").getValue().toString());
                        Medicamento.setTipoPorcion(objSnapshot.child("tipoPorcion").getValue().toString());
                        Medicamento.setIntervaloHora(objSnapshot.child("intervaloHora").getValue().toString());
                        Medicamento.setCantidadMedicamento(objSnapshot.child("cantidadMedicamento").getValue().toString());
                        Medicamento.setDias(objSnapshot.child("dias").getValue().toString());

                        datosMedicamento = Medicamento.getNombreMedicamento() + "\n" + "Vía de administración:"
                                + Medicamento.getViaAdministracion() + "\n" + "Porción:" + Medicamento.getCantidadPorcion()
                                + Medicamento.getTipoPorcion() + "\n" + "Cantidad:" + Medicamento.getCantidadMedicamento()
                                + "\n" + "Cada " + Medicamento.getIntervaloHora() + " horas por " + Medicamento.getDias() + " días";


                        if (datosMedicamento.toLowerCase().contains(letra.toLowerCase())) {
                            id_medicamento.add(objSnapshot.child("idMedicamento").getValue().toString());
                            datos_medicamento.add(datosMedicamento);
                        }
                    }
                    listaMedicamento.setAdapter(new AdaptadorMedicamento(Medicamento.this, datos_medicamento, id_medicamento));
                    listaMedicamento.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Medicamento.setIdMedicamento(id_medicamento.get(position));
                            Intent intent = new Intent(Medicamento.this, EditarMedicamento.class);
                            intent.putExtra("idMedicamento", Medicamento.getIdMedicamento());
                            startActivity(intent);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}



