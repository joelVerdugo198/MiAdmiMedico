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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Registro extends AppCompatActivity {

    //REGISTRO USUARIO

    ClaseUsuario usuario = new ClaseUsuario();

    FirebaseAuth mAuth;
    DatabaseReference baseDatos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        Toolbar toolbar;
        Button btnGuardarRegistro;
        final EditText campoNombre, campoApellido, campoTelefono, campoCorreo, campoContraseña;

        mAuth = FirebaseAuth.getInstance();
        baseDatos = FirebaseDatabase.getInstance().getReference();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        btnGuardarRegistro = (Button) findViewById(R.id.botonGuardarUsuario);
        campoNombre = (EditText) findViewById(R.id.escribirNombreUsuario);
        campoApellido = (EditText) findViewById(R.id.escribirApellidoUsuario);
        campoTelefono = (EditText) findViewById(R.id.escribirTelefonoUsuario);
        campoCorreo = (EditText) findViewById(R.id.escribirCorreoUsuario);
        campoContraseña = (EditText) findViewById(R.id.escribirContraseñaUsuario);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Registro");

        btnGuardarRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usuario.setNombre(campoNombre.getText().toString());
                usuario.setApellido(campoApellido.getText().toString());
                usuario.setTelefono(campoTelefono.getText().toString());
                usuario.setCorreo(campoCorreo.getText().toString());
                usuario.setContraseña(campoContraseña.getText().toString());

                //SE COMPRUEBA DE QUE NO ESTEN VACIOS LOS CAMPOS Y CUMPlAN CON ESPECIFICACIONES
                if (!usuario.getNombre().isEmpty() && !usuario.getApellido().isEmpty() && !usuario.getTelefono().isEmpty()
                        && !usuario.getCorreo().isEmpty() && !usuario.getContraseña().isEmpty()) {
                    if (usuario.getContraseña().length() >= 6 && usuario.getTelefono().length() == 10) {
                        Toast.makeText(Registro.this, "Verifique su conexión a internet", Toast.LENGTH_SHORT).show();
                        verificarUsuario(usuario.getCorreo());
                    }
                    else{
                        if (usuario.getTelefono().length() < 10 || usuario.getTelefono().length() > 10) {
                            Toast.makeText(Registro.this, "El número teléfonico debe de tener 10 dígitos ", Toast.LENGTH_SHORT).show();
                        } else if (usuario.getContraseña().length() < 6) {
                            Toast.makeText(Registro.this, "La contraseña debe tener al menos 6 carácteres", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                else {
                    Toast.makeText(Registro.this, "Llenar campos vacíos", Toast.LENGTH_SHORT).show();
                }
            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        });
    }

    public void registrarUsuario() {
        mAuth.createUserWithEmailAndPassword(usuario.getCorreo(), usuario.getContraseña()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
          public void onComplete(@NonNull final Task<AuthResult> task) {
            if(task.isSuccessful()){
                String idUsuario = mAuth.getCurrentUser().getUid();
                Map<String, Object> datoUsuario = new HashMap<>();

                //TRIM() ELIMINO POSIBLES ESPCIOS EN BLANCO EN EL CORREO
                datoUsuario.put("nombre", usuario.getNombre());
                datoUsuario.put("apellido", usuario.getApellido());
                datoUsuario.put("telefono", usuario.getTelefono());
                datoUsuario.put("correo", usuario.getCorreo().trim());
                datoUsuario.put("contrasena", usuario.getContraseña());

                baseDatos.child("usuario").child(idUsuario).setValue(datoUsuario).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task2) {
                        if (task2.isSuccessful()) {
                            Toast.makeText(Registro.this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(Registro.this, MainActivity.class));
                            finish();
                        } else {
                            Toast.makeText(Registro.this, "Error al crear los datos", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
          }
        });
    }

    public void verificarUsuario(final String correoIngresado ){

        final ArrayList<String> correo_verificar = new ArrayList<>();

        baseDatos.child("usuario").child(usuario.getIdUsuario()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot objSnapshot : dataSnapshot.getChildren()){
                        if (objSnapshot.child("correo").getValue().toString().equals(correoIngresado)){
                            correo_verificar.add(objSnapshot.child("correo").getValue().toString());
                        }
                    }
                    if (correo_verificar.size()>0) {
                        Toast.makeText(Registro.this, "El correo ya esta registrado en otra cuenta", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        registrarUsuario();
                    }
                } else {
                    registrarUsuario();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Registro.this, "Error, comprebe su conexión a internet", Toast.LENGTH_SHORT).show();
            }
        });
    }
}



