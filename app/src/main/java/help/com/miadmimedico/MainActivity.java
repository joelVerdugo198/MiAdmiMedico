package help.com.miadmimedico;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    //INICIAR SESION USUARIO

    ClaseUsuario usuario = new ClaseUsuario();

    private FirebaseAuth mAuth;
    private ProgressDialog progreso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar;
        Button botonIniciarSesion;
        final EditText campoCorreo, campoContraseña ;
        TextView btnRegistrarIniciarSesion, btnResetearContraseña;

        mAuth = FirebaseAuth.getInstance();
        progreso = new ProgressDialog(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        campoCorreo = (EditText) findViewById(R.id.usuarioIniciarSesion);
        campoContraseña = (EditText) findViewById(R.id.contraseñaIniciarSesion);
        botonIniciarSesion = (Button) findViewById(R.id.botonIniciarSesion);
        btnRegistrarIniciarSesion = (TextView) findViewById(R.id.registrarIniciarSesion);
        btnResetearContraseña = (TextView) findViewById(R.id.contraseñaOlvidada);

        setSupportActionBar(toolbar);

        botonIniciarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usuario.setCorreo(campoCorreo.getText().toString());
                usuario.setContraseña(campoContraseña.getText().toString());
                if (!usuario.getCorreo().isEmpty() && !usuario.getContraseña().isEmpty()){
                    progreso.setMessage("Iniciando sesión ...");
                    progreso.setCanceledOnTouchOutside(false);
                    progreso.show();
                    iniciarSesion();
                }
            }
        });

         btnRegistrarIniciarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Registro.class);
                startActivity(intent);
                finish();
            }
        });

        btnResetearContraseña.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ResetearContrasena.class);
                startActivity(intent);
                finish();
            }
        });

    }

    public void iniciarSesion(){
        mAuth.signInWithEmailAndPassword(usuario.getCorreo(), usuario.getContraseña()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    progreso.dismiss();
                    Intent intent = new Intent(MainActivity.this, Principal .class);
                    startActivity(intent);
                    finish();
                }
                else{
                    progreso.dismiss();
                    Toast.makeText(MainActivity.this, "Verifique su conexión a internet", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
