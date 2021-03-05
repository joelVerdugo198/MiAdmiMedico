package help.com.miadmimedico;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditarContacto extends AppCompatActivity {

    ClaseContacto contacto = new ClaseContacto();
    ClaseSeguimiento seguimiento = new ClaseSeguimiento();
    ClaseUsuario usuario = new ClaseUsuario();

    FirebaseAuth mAuth;
    DatabaseReference baseDatos;

    EditText campoNombre,campoApellido, campoTelefono;

    private boolean eliminar = true, verificar_telefono = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_contacto);

        Toolbar toolbar;
        Button btnActualizarContacto;
        TextView btnEliminarContacto;

        mAuth = FirebaseAuth.getInstance();
        baseDatos = FirebaseDatabase.getInstance().getReference();
        usuario.setIdUsuario(mAuth.getCurrentUser().getUid());

        campoNombre = (EditText) findViewById(R.id.escribirNombreEditarContacto);
        campoApellido = (EditText) findViewById(R.id.escribirApellidoEditarContacto);
        campoTelefono = (EditText) findViewById(R.id.escribirtelefonoEditarContacto);
        btnActualizarContacto = (Button) findViewById(R.id.botonGuardarEditarContacto);
        btnEliminarContacto = (TextView) findViewById(R.id.botonEliminarEditarContacto);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Editar contacto");

        contacto.setIdContacto(getIntent().getStringExtra("idContacto"));

        mostrarDatosContacto();

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Contacto.class));
                finish();
            }
        });

        btnActualizarContacto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contacto.setNombre(campoNombre.getText().toString());
                contacto.setApellido(campoApellido.getText().toString());
                contacto.setTelefono(campoTelefono.getText().toString());

                //SE COMPRUEBA DE QUE NO ESTEN VACIOS LOS CAMPOS Y CUMPlAN CON ESPECIFICACIONES
                if (!contacto.getNombre().isEmpty() && !contacto.getApellido().isEmpty() && !contacto.getTelefono().isEmpty()) {
                    if (contacto.getTelefono().length() == 10) {
                        verificarTelefono(contacto.getTelefono());
                    } else {
                        Toast.makeText(EditarContacto.this, "El número teléfonico debe de tener 10 dígitos", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(EditarContacto.this, "Llenar campos vacíos", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnEliminarContacto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mensaje = new AlertDialog.Builder(EditarContacto.this);
                mensaje.setMessage("¿Está seguro de eliminar este contacto?")
                        .setCancelable(false)
                        .setPositiveButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .setNegativeButton("Si", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                eliminarContacto();
                            }
                        });
                AlertDialog titulo = mensaje.create();
                titulo.setTitle("Eliminar");
                titulo.show();
            }
        });
    }

    public void mostrarDatosContacto(){
        baseDatos.child("contacto").child(usuario.getIdUsuario()).child(contacto.getIdContacto()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    contacto.setNombre(dataSnapshot.child("nombre").getValue().toString());
                    contacto.setApellido(dataSnapshot.child("apellido").getValue().toString());
                    contacto.setTelefono(dataSnapshot.child("telefono").getValue().toString());

                    campoNombre.setText(contacto.getNombre());
                    campoApellido.setText(contacto.getApellido());
                    campoTelefono.setText(contacto.getTelefono());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void verificarTelefono(final String telefono){
        baseDatos.child("contacto").child(usuario.getIdUsuario()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot objSnapshot : dataSnapshot.getChildren()){
                    if (objSnapshot.child("telefono").getValue().toString().equals(telefono)
                            && !objSnapshot.child("idContacto").getValue().toString().equals(contacto.getIdContacto())){
                        verificar_telefono = true;
                    }
                }
                if (verificar_telefono == true){
                    Toast.makeText(EditarContacto.this, "El número de teléfono ya está registrado en otro contacto", Toast.LENGTH_SHORT).show();
                }
                else{
                    actualizarContacto();
                }
                verificar_telefono = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void actualizarContacto(){
        Map<String, Object> datos_contacto = new HashMap<>();
        datos_contacto.put("idContacto", contacto.getIdContacto());
        datos_contacto.put("nombre", contacto.getNombre());
        datos_contacto.put("apellido", contacto.getApellido());
        datos_contacto.put("telefono", contacto.getTelefono());

        baseDatos.child("contacto").child(usuario.getIdUsuario()).child(contacto.getIdContacto()).setValue(datos_contacto).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task2) {
                if (task2.isSuccessful()) {
                    Toast.makeText(EditarContacto.this, "Se actualizó con éxito", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(EditarContacto.this,Contacto.class));
                    finish();
                } else {
                    Toast.makeText(EditarContacto.this, "Error al actualizar los datos", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void eliminarContacto(){
        baseDatos.child("receta").child(usuario.getIdUsuario()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 if (dataSnapshot.exists()){
                     for (DataSnapshot objSnapshot : dataSnapshot.getChildren()){
                         if (objSnapshot.child("contacto1").getValue().toString().equals(contacto.getIdContacto())
                         || objSnapshot.child("contacto2").getValue().toString().equals(contacto.getIdContacto())
                         || objSnapshot.child("contacto3").getValue().toString().equals(contacto.getIdContacto())){
                             seguimiento.setNombreMedicamento(objSnapshot.child("medicamento").getValue().toString());
                             seguimiento.setTipo(objSnapshot.child("tipoReceta").getValue().toString());
                             eliminar = false;
                         }
                     }
                     if (eliminar==false) {
                         Toast.makeText(EditarContacto.this, "No se puede eliminar ya que está asociado al "
                                 + seguimiento.getNombreMedicamento() + " de la receta " + seguimiento.getTipo(), Toast.LENGTH_LONG).show();
                     }
                 }else if (eliminar==true){
                     Toast.makeText(EditarContacto.this, "Se eliminó con éxito", Toast.LENGTH_SHORT).show();
                     baseDatos.child("contacto").child(usuario.getIdUsuario()).child(contacto.getIdContacto()).removeValue();
                     startActivity(new Intent(EditarContacto.this,Contacto.class));
                     finish();
                 }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
