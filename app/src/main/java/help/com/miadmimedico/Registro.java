package help.com.miadmimedico;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bumptech.glide.Glide;
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
import com.kbeanie.multipicker.api.CacheLocation;
import com.kbeanie.multipicker.api.CameraImagePicker;
import com.kbeanie.multipicker.api.ImagePicker;
import com.kbeanie.multipicker.api.Picker;
import com.kbeanie.multipicker.api.callbacks.ImagePickerCallback;
import com.kbeanie.multipicker.api.entity.ChosenImage;

public class Registro extends AppCompatActivity {

    //REGISTRO USUARIO

    ClaseUsuario usuario = new ClaseUsuario();

    FirebaseAuth mAuth;
    DatabaseReference baseDatos;
    ImagePicker imagePicker;
    CameraImagePicker cameraImagePicker;
    Uri fotoPerfilUri;

    String pickerPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        Toolbar toolbar;
        Button btnGuardarRegistro;
        final ImageView campoFotoPerfil;
        final EditText campoNombre, campoApellido, campoTelefono, campoCorreo, campoContraseña;

        mAuth = FirebaseAuth.getInstance();
        baseDatos = FirebaseDatabase.getInstance().getReference();
        imagePicker = new ImagePicker(this);
        cameraImagePicker = new CameraImagePicker(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        btnGuardarRegistro = (Button) findViewById(R.id.botonGuardarUsuario);
        campoFotoPerfil = (ImageView) findViewById(R.id.fotoPerfilRegistro);
        campoNombre = (EditText) findViewById(R.id.escribirNombreUsuario);
        campoApellido = (EditText) findViewById(R.id.escribirApellidoUsuario);
        campoTelefono = (EditText) findViewById(R.id.escribirTelefonoUsuario);
        campoCorreo = (EditText) findViewById(R.id.escribirCorreoUsuario);
        campoContraseña = (EditText) findViewById(R.id.escribirContraseñaUsuario);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Registro");

        Glide.with(Registro.this).load(usuario.getFotoPerfil()).placeholder(R.drawable.iconousuario).into(campoFotoPerfil);

        cameraImagePicker.setCacheLocation(CacheLocation.EXTERNAL_STORAGE_APP_DIR);

        //FOTO DE GALERÍA
        imagePicker.setImagePickerCallback(new ImagePickerCallback() {
            @Override
            public void onImagesChosen(List<ChosenImage> list) {
                if (!list.isEmpty()){
                    String path = list.get(0).getOriginalPath();
                    fotoPerfilUri = Uri.parse(path);
                    campoFotoPerfil.setImageURI(fotoPerfilUri);
                }
            }

            @Override
            public void onError(String s) {
                Toast.makeText(Registro.this,"Error "+s,Toast.LENGTH_SHORT).show();
            }
        });

        //FOTO DE CÁMARA
        cameraImagePicker.setImagePickerCallback(new ImagePickerCallback() {
            @Override
            public void onImagesChosen(List<ChosenImage> list) {
                String path = list.get(0).getOriginalPath();
                fotoPerfilUri = Uri.fromFile(new File(path));
                campoFotoPerfil.setImageURI(fotoPerfilUri);
            }

            @Override
            public void onError(String s) {
                Toast.makeText(Registro.this, "Error: "+s, Toast.LENGTH_SHORT).show();
            }
        });


        //SELECCIONAR FOTO DE CÁMARA O GALERÍA AL OPRIMIR LA IMÁGEN
        campoFotoPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(Registro.this);
                dialog.setTitle("Foto de perfil");

                String[] items = {"GalerÍa","Camara"};

                dialog.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i){
                            case 0:
                                imagePicker.pickImage();
                                break;
                            case 1:
                                pickerPath = cameraImagePicker.pickImage();
                                break;
                        }
                    }
                });

                AlertDialog dialogConstruido = dialog.create();
                dialogConstruido.show();
            }
        });

        btnGuardarRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (fotoPerfilUri!=null) {
                    usuario.subirFoto(fotoPerfilUri, new ClaseUsuario.IDevolverUrlFoto() {
                        @Override
                        public void devolerUrlString(String url) {
                            usuario.setFotoPerfil(url);
                            usuario.setNombre(campoNombre.getText().toString());
                            usuario.setApellido(campoApellido.getText().toString());
                            usuario.setTelefono(campoTelefono.getText().toString());
                            usuario.setCorreo(campoCorreo.getText().toString());
                            usuario.setContraseña(campoContraseña.getText().toString());

                            //SE COMPRUEBA DE QUE NO ESTEN VACIOS LOS CAMPOS Y CUMPlAN CON ESPECIFICACIONES
                            if (!usuario.getNombre().isEmpty() && !usuario.getApellido().isEmpty() && !usuario.getTelefono().isEmpty()
                                    && !usuario.getCorreo().isEmpty() && !usuario.getContraseña().isEmpty()) {
                                if (usuario.getContraseña().length() >= 6 && usuario.getTelefono().length() == 10) {
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
                }
                else{
                    usuario.setNombre(campoNombre.getText().toString());
                    usuario.setApellido(campoApellido.getText().toString());
                    usuario.setTelefono(campoTelefono.getText().toString());
                    usuario.setCorreo(campoCorreo.getText().toString());
                    usuario.setContraseña(campoContraseña.getText().toString());

                    //SE COMPRUEBA DE QUE NO ESTEN VACIOS LOS CAMPOS Y CUMPlAN CON ESPECIFICACIONES
                    if (!usuario.getNombre().isEmpty() && !usuario.getApellido().isEmpty() && !usuario.getTelefono().isEmpty()
                            && !usuario.getCorreo().isEmpty() && !usuario.getContraseña().isEmpty()) {
                        if (usuario.getContraseña().length() >= 6 && usuario.getTelefono().length() == 10) {
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

    //RECIBIR EL RESULTADO DESDE LA CAMARA O GALERÍA
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Picker.PICK_IMAGE_DEVICE && resultCode == RESULT_OK){
            imagePicker.submit(data);
        }else if(requestCode == Picker.PICK_IMAGE_CAMERA && resultCode == RESULT_OK){
        cameraImagePicker.reinitialize(pickerPath);
        cameraImagePicker.submit(data);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // TIENES QUE GUARDAR LA RUTA EN CASO DE QUE SE ELIMINE TU ACTIVIDAD.
        // EN TAL ESCENARIO, DEBERÁ REINICIAR EL CAMERAIMAGENPICKER
        outState.putString("picker_path", pickerPath);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        // DESPUÉS DE LA RECREACIÓN DE LA ACTIVIDAD, DEBE REINICIALIZAR ESTOS
        // DOS VALORES PARA PODER REINICIALIZAR CAMERAIMAGENPICKER
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("picker_path")) {
                pickerPath = savedInstanceState.getString("picker_path");
            }
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    public void registrarUsuario() {
        mAuth.createUserWithEmailAndPassword(usuario.getCorreo(), usuario.getContraseña()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
          public void onComplete(@NonNull final Task<AuthResult> task) {
            if(task.isSuccessful()){
                String idUsuario = mAuth.getCurrentUser().getUid();
                Map<String, Object> datoUsuario = new HashMap<>();

                //TRIM() ELIMINO POSIBLES ESPACIOS EN BLANCO EN EL CORREO
                datoUsuario.put("nombre", usuario.getNombre());
                datoUsuario.put("apellido", usuario.getApellido());
                datoUsuario.put("telefono", usuario.getTelefono());
                datoUsuario.put("correo", usuario.getCorreo().trim());
                datoUsuario.put("contrasena", usuario.getContraseña());
                datoUsuario.put("fotoPerfil", usuario.getFotoPerfil());

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



