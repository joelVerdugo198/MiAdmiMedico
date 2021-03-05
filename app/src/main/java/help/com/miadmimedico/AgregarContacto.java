package help.com.miadmimedico;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AgregarContacto extends AppCompatActivity {

    ClaseContacto contacto = new ClaseContacto();
    ClaseUsuario usuario = new ClaseUsuario();

    FirebaseAuth mAuth;
    DatabaseReference baseDatos;

    EditText campoNombre, campoApellido, campoTelefono;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_contacto);

        Toolbar toolbar;
        Button btnGuardarContacto;

        mAuth = FirebaseAuth.getInstance();
        baseDatos = FirebaseDatabase.getInstance().getReference();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        btnGuardarContacto = (Button) findViewById(R.id.botonGuardarAgregarContacto);

        campoNombre = (EditText) findViewById(R.id.escribirNombreAgregarContacto);
        campoApellido = (EditText) findViewById(R.id.escribirApellidoAgregarContacto);
        campoTelefono = (EditText) findViewById(R.id.escribirtelefonoAgregarContacto);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Agregar contacto");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Contacto.class));
            }
        });

        btnGuardarContacto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contacto.setNombre(campoNombre.getText().toString());
                contacto.setApellido(campoApellido.getText().toString());
                contacto.setTelefono(campoTelefono.getText().toString());

                //SE COMPRUEBA DE QUE NO ESTEN VACIOS LOS CAMPOS Y CUMPlAN CON ESPECIFICACIONES
                if (!contacto.getNombre().isEmpty() && !contacto.getApellido().isEmpty()
                        && !contacto.getTelefono().isEmpty()) {
                    if (contacto.getTelefono().length() == 10) {
                        agregarContacto();
                    } else {
                        Toast.makeText(AgregarContacto.this, "El número teléfonico debe de tener 10 dígitos", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AgregarContacto.this, "Llenar campos vacíos", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void agregarContacto(){

        final ArrayList<String> verificar_telefono = new ArrayList<>();
        usuario.setIdUsuario(mAuth.getCurrentUser().getUid());
        contacto.setIdContacto(UUID.randomUUID().toString());
        final Map<String, Object> datos_contacto = new HashMap<>();

        datos_contacto.put("idContacto", contacto.getIdContacto());
        datos_contacto.put("nombre", contacto.getNombre());
        datos_contacto.put("apellido", contacto.getApellido());
        datos_contacto.put("telefono", contacto.getTelefono());

        baseDatos.child("contacto").child(usuario.getIdUsuario()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    verificar_telefono.clear();
                    for (DataSnapshot objSnapshot : dataSnapshot.getChildren()) {
                        if (objSnapshot.child("telefono").getValue().toString().equals(contacto.getTelefono())) {
                            verificar_telefono.add(objSnapshot.child("telefono").getValue().toString());
                        }
                    }
                    if (verificar_telefono.size()>0) {
                        Toast.makeText(AgregarContacto.this, "El número de teléfono ya esta registrado en otro contacto", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        baseDatos.child("contacto").child(usuario.getIdUsuario()).child(contacto.getIdContacto()).setValue(datos_contacto).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task2) {
                                if (task2.isSuccessful()) {
                                    Toast.makeText(AgregarContacto.this, "Se agregó con éxito", Toast.LENGTH_SHORT).show();

                                    startActivity(new Intent(AgregarContacto.this, Contacto.class));
                                    finish();
                                } else {
                                    Toast.makeText(AgregarContacto.this, "Error al crear los datos", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
                else{
                    baseDatos.child("contacto").child(usuario.getIdUsuario()).child(contacto.getIdContacto()).setValue(datos_contacto).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task2) {
                            if (task2.isSuccessful()) {
                                Toast.makeText(AgregarContacto.this, "Se agregó con éxito", Toast.LENGTH_SHORT).show();

                                startActivity(new Intent(AgregarContacto.this, Contacto.class));
                                finish();
                            } else {
                                Toast.makeText(AgregarContacto.this, "Error al crear los datos", Toast.LENGTH_SHORT).show();
                            }
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
