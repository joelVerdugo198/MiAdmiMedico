package help.com.miadmimedico;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.nfc.Tag;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kbeanie.multipicker.api.CacheLocation;
import com.kbeanie.multipicker.api.CameraImagePicker;
import com.kbeanie.multipicker.api.ImagePicker;
import com.kbeanie.multipicker.api.Picker;
import com.kbeanie.multipicker.api.callbacks.ImagePickerCallback;
import com.kbeanie.multipicker.api.entity.ChosenImage;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Usuario extends AppCompatActivity {

    ClaseUsuario Usuario = new ClaseUsuario();

    FirebaseAuth mAuth;
    DatabaseReference baseDatos;

    ImagePicker imagePicker;
    CameraImagePicker cameraImagePicker;
    ImageView campoFotoPerfil;
    Uri fotoPerfilUri;

    String pickerPath;

    EditText campoNombre, campoApellido, campoTelefono, campoCorreo, campoContrasena;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario);

        Toolbar toolbar;
        Button btnActulizarUsuario,btnActualizarContrasena, btnEliminarUsuario;



        mAuth = FirebaseAuth.getInstance();
        baseDatos = FirebaseDatabase.getInstance().getReference();
        Usuario.setIdUsuario(mAuth.getCurrentUser().getUid());
        imagePicker = new ImagePicker(this);
        cameraImagePicker = new CameraImagePicker(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        campoFotoPerfil = (ImageView) findViewById(R.id.fotoPerfilUsuario);
        campoNombre = (EditText) findViewById(R.id.escribirNombreEditarUsuario);
        campoApellido = (EditText) findViewById(R.id.escribirApellidoEditarUsuario);
        campoTelefono = (EditText) findViewById(R.id.escribirTelefonoEditarUsuario);
        btnActulizarUsuario = (Button) findViewById(R.id.botonGuardarEditarUsuario);
        btnActualizarContrasena = (Button) findViewById(R.id.botonActualizarEditarUsuario2);
        btnEliminarUsuario = (Button) findViewById(R.id.botonEliminarEditarUsuario);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Editar perfil");

        mostrarDatosUsuario();

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
                Toast.makeText(Usuario.this,"Error "+s,Toast.LENGTH_SHORT).show();
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
                Toast.makeText(Usuario.this, "Error: "+s, Toast.LENGTH_SHORT).show();
            }
        });


        //SELECCIONAR FOTO DE CÁMARA O GALERÍA AL OPRIMIR LA IMÁGEN
        campoFotoPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(Usuario.this);
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

        btnActulizarUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (fotoPerfilUri!=null) {
                    Usuario.subirFoto(fotoPerfilUri, new ClaseUsuario.IDevolverUrlFoto() {
                        @Override
                        public void devolerUrlString(String url) {
                            Usuario.setFotoPerfil(url);
                            Usuario.setNombre(campoNombre.getText().toString());
                            Usuario.setApellido(campoApellido.getText().toString());
                            Usuario.setTelefono(campoTelefono.getText().toString());

                            //SE COMPRUEBA DE QUE NO ESTEN VACIOS LOS CAMPOS Y CUMPlAN CON ESPECIFICACIONES
                            if (!Usuario.getNombre().isEmpty() || !Usuario.getApellido().isEmpty() ||
                                    !Usuario.getTelefono().isEmpty()) {

                                if (Usuario.getTelefono().length() == 10) {
                                    actualizarUsuario();
                                } else if (Usuario.getTelefono().length() < 10 || Usuario.getTelefono().length() > 10) {
                                    Toast.makeText(Usuario.this, "el número teléfonico debe de tener 10 dígitos", Toast.LENGTH_SHORT).show();
                                }

                            } else {
                                Toast.makeText(Usuario.this, "Llenar campos vacíos", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }
                else{
                    Usuario.setNombre(campoNombre.getText().toString());
                    Usuario.setApellido(campoApellido.getText().toString());
                    Usuario.setTelefono(campoTelefono.getText().toString());

                    //SE COMPRUEBA DE QUE NO ESTEN VACIOS LOS CAMPOS Y CUMPlAN CON ESPECIFICACIONES
                    if (!Usuario.getNombre().isEmpty() || !Usuario.getApellido().isEmpty() ||
                            !Usuario.getTelefono().isEmpty()) {

                        if (Usuario.getTelefono().length() == 10) {
                            actualizarUsuario();
                        } else if (Usuario.getTelefono().length() < 10 || Usuario.getTelefono().length() > 10) {
                            Toast.makeText(Usuario.this, "el número teléfonico debe de tener 10 dígitos", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(Usuario.this, "Llenar campos vacíos", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        btnActualizarContrasena.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                baseDatos.child("usuario").child(Usuario.getIdUsuario()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()) {
                            Usuario.setCorreo(dataSnapshot.child("correo").getValue().toString());
                            mAuth.setLanguageCode("es");
                            //SE MANDA UN CORREO A SU CORREO ELECTRONICO PARA CAMBIAR LA CONTRASEÑA
                            // TRIM() ELIMINO POSIBLES ESPCIOS EN BLANCO EN EL CORREO
                            mAuth.sendPasswordResetEmail(Usuario.getCorreo()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()){
                                        Toast.makeText(Usuario.this, "Se te ha enviado a tu correo electrónico un link donde puedes cambiar tu contraseña", Toast.LENGTH_SHORT).show();
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
        });

        btnEliminarUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mensaje = new AlertDialog.Builder(Usuario.this);
                mensaje.setMessage("¿Está seguro de eliminar su cuenta?" + "\n" + "Ojo: se eliminarán todos los datos relacionados con tu cuenta")
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
                                eliminarUsuario();
                            }
                        });
                AlertDialog titulo = mensaje.create();
                titulo.setTitle("Eliminar");
                titulo.show();

            }

        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Principal.class));
                finish();
            }
        });
    }

    public void mostrarDatosUsuario(){
        baseDatos.child("usuario").child(Usuario.getIdUsuario()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Usuario.setFotoPerfil(dataSnapshot.child("fotoPerfil").getValue().toString());
                    Usuario.setNombre(dataSnapshot.child("nombre").getValue().toString());
                    Usuario.setApellido(dataSnapshot.child("apellido").getValue().toString());
                    Usuario.setTelefono(dataSnapshot.child("telefono").getValue().toString());

                    campoNombre.setText(Usuario.getNombre());
                    campoApellido.setText(Usuario.getApellido());
                    campoTelefono.setText(Usuario.getTelefono());

                    Glide.with(Usuario.this).load(Usuario.getFotoPerfil()).placeholder(R.drawable.iconousuario).into(campoFotoPerfil);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void eliminarUsuario(){
        mAuth.getCurrentUser().delete();
        baseDatos.child("contacto").child(Usuario.getIdUsuario()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    baseDatos.child("contacto").child(Usuario.getIdUsuario()).removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        baseDatos.child("receta").child(Usuario.getIdUsuario()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    baseDatos.child("receta").child(Usuario.getIdUsuario()).removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        baseDatos.child("medicamento").child(Usuario.getIdUsuario()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    baseDatos.child("medicamento").child(Usuario.getIdUsuario()).removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        baseDatos.child("seguimiento").child(Usuario.getIdUsuario()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot objSnapshot : dataSnapshot.getChildren()){

                        Intent intent = new Intent(Usuario.this, Notificacion.class);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(Usuario.this, Integer.parseInt(objSnapshot.child("idAlarma").getValue().toString()),
                                intent, PendingIntent.FLAG_IMMUTABLE);
                        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                        alarmManager.cancel(pendingIntent);

                        Intent intent2 = new Intent(Usuario.this, Notificacion.class);
                        PendingIntent pendingIntent2 = PendingIntent.getBroadcast(Usuario.this, Integer.parseInt(objSnapshot.child("idAlarma").getValue().toString()),
                                intent2, PendingIntent.FLAG_ONE_SHOT);
                        AlarmManager alarmManager2 = (AlarmManager) getSystemService(ALARM_SERVICE);
                        alarmManager2.cancel(pendingIntent2);
                    }
                    baseDatos.child("seguimiento").child(Usuario.getIdUsuario()).removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        baseDatos.child("usuario").child(Usuario.getIdUsuario()).removeValue();
        startActivity(new Intent(Usuario.this, MainActivity.class));
        finish();
    }

    public void actualizarUsuario() {
        final Map<String, Object> datos_usuario = new HashMap<>();

        datos_usuario.put("fotoPerfil", Usuario.getFotoPerfil());
        datos_usuario.put("nombre", Usuario.getNombre());
        datos_usuario.put("apellido", Usuario.getApellido());
        datos_usuario.put("telefono", Usuario.getTelefono());

        baseDatos.child("usuario").child(Usuario.getIdUsuario()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    datos_usuario.put("correo", dataSnapshot.child("correo").getValue().toString());
                    baseDatos.child("usuario").child(Usuario.getIdUsuario()).setValue(datos_usuario).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task2) {
                            if (task2.isSuccessful()) {
                                Toast.makeText(Usuario.this, "Se actualizó con éxito", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(Usuario.this, Principal.class));
                                finish();
                            } else {
                                Toast.makeText(Usuario.this, "Error al actualizar los datos", Toast.LENGTH_SHORT).show();
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
}
