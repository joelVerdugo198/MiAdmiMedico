package help.com.miadmimedico;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.widget.Toolbar;
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

public class Contacto extends AppCompatActivity {

    ClaseContacto contacto = new ClaseContacto();
    ClaseUsuario usuario = new ClaseUsuario();

    FirebaseAuth mAuth;
    DatabaseReference baseDatos;

    ArrayList<String> nombre_contacto = new ArrayList<>();
    ArrayList<String> id_contacto = new ArrayList<>();

    ImageView imagenContacto;
    SearchView buscarContacto;
    ListView listaContacto;
    TextView avisoContactoVacio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacto);

        Toolbar toolbar;
        FloatingActionButton btnAgregarContacto;

        mAuth = FirebaseAuth.getInstance();
        baseDatos = FirebaseDatabase.getInstance().getReference();
        usuario.setIdUsuario(mAuth.getCurrentUser().getUid());

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        buscarContacto = (SearchView) findViewById(R.id.buscarContacto);
        imagenContacto = (ImageView) findViewById(R.id.imagenContacto);
        avisoContactoVacio = (TextView) findViewById(R.id.textoContactoVacio);
        listaContacto = (ListView) findViewById(R.id.listaContacto);
        btnAgregarContacto = (FloatingActionButton) findViewById(R.id.botonAgregarContacto);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Contacto");

        mostrarAvisoContactoVacio();

        listarContactos();

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Principal.class));
                finish();
            }
        });


        btnAgregarContacto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent agregarContacto = new Intent(Contacto.this, AgregarContacto.class);
                startActivity(agregarContacto);
                finish();
            }
        });
    }

    public void mostrarAvisoContactoVacio(){

        baseDatos.child("contacto").child(usuario.getIdUsuario()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    buscarContacto.setVisibility(View.VISIBLE);
                    imagenContacto.setVisibility(View.GONE);
                    avisoContactoVacio.setVisibility(View.GONE);
                }
                else{
                    buscarContacto.setVisibility(View.GONE);
                    imagenContacto.setVisibility(View.VISIBLE);
                    avisoContactoVacio.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void listarContactos(){
        baseDatos.child("contacto").child(usuario.getIdUsuario()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                nombre_contacto.clear();
                id_contacto.clear();
                for (DataSnapshot objSnapshot : dataSnapshot.getChildren()){
                    id_contacto.add(objSnapshot.child("idContacto").getValue().toString());
                    contacto.setNombre((objSnapshot.child("nombre").getValue().toString()));
                    contacto.setApellido((objSnapshot.child("apellido").getValue().toString()));
                    nombre_contacto.add(contacto.getNombre() + " " + contacto.getApellido());
                }
                listaContacto.setAdapter(new AdapatadorContacto(Contacto.this, nombre_contacto, id_contacto));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        buscarContacto.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                buscarContacto(s);
                return true;
            }
        });
    }

    public void buscarContacto( final String s) {
        baseDatos.child("contacto").child(usuario.getIdUsuario()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String nombreCompleto = "";
                nombre_contacto.clear();
                id_contacto.clear();
                for (DataSnapshot objSnapshot : dataSnapshot.getChildren()){
                    contacto.setNombre((objSnapshot.child("nombre").getValue().toString()));
                    contacto.setApellido(objSnapshot.child("apellido").getValue().toString());
                    nombreCompleto = contacto.getNombre() + " " + contacto.getApellido();
                    if (nombreCompleto.toLowerCase().contains(s.toLowerCase())){
                        id_contacto.add(objSnapshot.child("idContacto").getValue().toString());
                        nombre_contacto.add(nombreCompleto);
                    }
                }
                listaContacto.setAdapter(new AdapatadorContacto(Contacto.this, nombre_contacto, id_contacto));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
