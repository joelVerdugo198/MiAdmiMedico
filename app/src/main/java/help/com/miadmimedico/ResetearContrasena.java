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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetearContrasena extends AppCompatActivity {

    ClaseUsuario usuario = new ClaseUsuario();

    private FirebaseAuth mAuth;
    private ProgressDialog progreso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resetear_contrasena);

        Toolbar toolbar;
        final EditText campoCorreo;
        Button btnVerificar;

        mAuth = FirebaseAuth.getInstance();
        progreso = new ProgressDialog(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        campoCorreo = (EditText) findViewById(R.id.escribirCorreoVerificar);
        btnVerificar = (Button) findViewById(R.id.btnVerificar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Resetear contraseña");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });

        btnVerificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usuario.setCorreo(campoCorreo.getText().toString());

                if (!usuario.getCorreo().isEmpty()){
                    progreso.setMessage("Enviando mensaje ...");
                    progreso.setCanceledOnTouchOutside(false);
                    progreso.show();
                    resetearContraseña();
                }
                else{
                    Toast.makeText(ResetearContrasena.this,"Campo vacío", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void resetearContraseña(){
        mAuth.setLanguageCode("es");
        //SE MANDA UN CORREO A SU CORREO ELECTRONICO PARA CAMBIAR LA CONTRASEÑA
        // TRIM() ELIMINO POSIBLES ESPCIOS EN BLANCO EN EL CORREO
        mAuth.sendPasswordResetEmail(usuario.getCorreo().trim()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    progreso.dismiss();
                    startActivity( new Intent(ResetearContrasena.this, MainActivity.class));
                    finish();
                }
                else{
                    progreso.dismiss();
                    Toast.makeText(ResetearContrasena.this, "Verifique su conexión a internet", Toast.LENGTH_SHORT).show();
                   // Toast.makeText(ResetearContrasena.this,"Correo no registrado", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
