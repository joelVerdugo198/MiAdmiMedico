 package help.com.miadmimedico;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


 public class EditarMedicamentoReceta extends AppCompatActivity {

     ClaseMedicamentoReceta medicamentoReceta = new ClaseMedicamentoReceta();
     ClaseSeguimiento seguimiento = new ClaseSeguimiento();
     ClaseUsuario usuario = new ClaseUsuario();

     FirebaseAuth mAuth;
     DatabaseReference baseDatos;

     EditText campoCedulaMedica, campoNombreMedicamento, campoCantidadPorcion;
     Spinner campoTipoReceta ,campoOpcionDia, campoOpcionHora, campoOpcionPorcion,
             campoCantidad, campoOpcionVia, campoContacto1, campoContacto2, campoContacto3;

     List<String> id_contacto = new ArrayList<>();
     List<String> lista_contacto = new ArrayList<>();
     final HashMap<String, Object> datos_seguimiento = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_medicamento_receta);

        Toolbar toolbar;
        Button btnGuardarMedicamentoReceta, btnEliminarMedicamentoReceta;


        mAuth = FirebaseAuth.getInstance();
        baseDatos = FirebaseDatabase.getInstance().getReference();

        medicamentoReceta.setIdMedicamento(getIntent().getStringExtra("idMedicamento"));
        usuario.setIdUsuario(mAuth.getCurrentUser().getUid());

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        campoTipoReceta = (Spinner) findViewById(R.id.opcionEditarReceta);
        campoCedulaMedica = (EditText) findViewById(R.id.escribirCedulaEditarReceta);
        campoNombreMedicamento = (EditText) findViewById(R.id.escribirMedicamentoEditarReceta);
        campoCantidadPorcion = (EditText) findViewById(R.id.escribirPorcionEditarReceta);
        campoOpcionDia = (Spinner) findViewById(R.id.opcionDiaEditarReceta);
        campoOpcionHora = (Spinner) findViewById(R.id.opcionHoraEditarReceta);
        campoOpcionVia = (Spinner) findViewById(R.id.opcionViaEditarReceta);
        campoOpcionPorcion = (Spinner) findViewById(R.id.tipoPorcionEditarReceta);
        campoCantidad = (Spinner) findViewById(R.id.opcionCantidadEditarReceta);
        campoContacto1 = (Spinner) findViewById(R.id.contacto1EditarReceta);
        campoContacto2 = (Spinner) findViewById(R.id.contacto2EditarReceta);
        campoContacto3 = (Spinner) findViewById(R.id.contacto3EditarReceta);
        btnGuardarMedicamentoReceta = (Button) findViewById(R.id.botonEditarMedicamentoReceta);
        btnEliminarMedicamentoReceta = (Button) findViewById(R.id.btnEliminarMedicamentoReceta);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Editar medicamento ");

        mostrarDatosMedicamento();

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Receta.class));
            }
        });

        btnGuardarMedicamentoReceta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                medicamentoReceta.setTipoPorcion(campoTipoReceta.getSelectedItem().toString());
                medicamentoReceta.setCedulaMedica(campoCedulaMedica.getText().toString());
                medicamentoReceta.setNombreMedicamento(campoNombreMedicamento.getText().toString());
                medicamentoReceta.setCantidadPorcion(campoCantidadPorcion.getText().toString());
                medicamentoReceta.setTipoPorcion(campoOpcionPorcion.getSelectedItem().toString());
                medicamentoReceta.setCantidadMedicamento(campoCantidad.getSelectedItem().toString());
                medicamentoReceta.setIntervaloHora(campoOpcionHora.getSelectedItem().toString());
                medicamentoReceta.setDias(campoOpcionDia.getSelectedItem().toString());
                medicamentoReceta.setViaAdministracion(campoOpcionVia.getSelectedItem().toString());

                // SE CONDICIONA LOS POSIBLES CASOS DE SELECCIONAR UN CONTACTO
                if (campoContacto1.getSelectedItemPosition() > 0 && campoContacto2.getSelectedItemPosition() > 0
                        && campoContacto3.getSelectedItemPosition() > 0) {
                    medicamentoReceta.setContacto1(id_contacto.get(campoContacto1.getSelectedItemPosition() - 1));
                    medicamentoReceta.setContacto2(id_contacto.get(campoContacto2.getSelectedItemPosition() - 1));
                    medicamentoReceta.setContacto3(id_contacto.get(campoContacto3.getSelectedItemPosition() - 1));
                }
                else if (campoContacto1.getSelectedItemPosition() > 0 && campoContacto2.getSelectedItemPosition() > 0){
                    medicamentoReceta.setContacto1(id_contacto.get(campoContacto1.getSelectedItemPosition() - 1));
                    medicamentoReceta.setContacto2(id_contacto.get(campoContacto2.getSelectedItemPosition() - 1));
                }
                else if (campoContacto1.getSelectedItemPosition() > 0){
                    medicamentoReceta.setContacto1(id_contacto.get(campoContacto1.getSelectedItemPosition() - 1));
                }

                //SE COMPRUEBA DE QUE NO ESTEN VACIOS LOS CAMPOS Y CUMPlAN CON ESPECIFICACIONES
                if (!medicamentoReceta.getTipoReceta().equals("") && !medicamentoReceta.getCedulaMedica().isEmpty() &&
                        !medicamentoReceta.getNombreMedicamento().isEmpty() && !medicamentoReceta.getCantidadPorcion().isEmpty() &&
                        !medicamentoReceta.getTipoPorcion().equals("") && !medicamentoReceta.getCantidadMedicamento().equals("") &&
                        !medicamentoReceta.getIntervaloHora().equals("") && !medicamentoReceta.getDias().equals("") &&
                        !medicamentoReceta.getViaAdministracion().equals("")){

                    if (campoContacto2.getSelectedItemPosition() > 0 && campoContacto3.getSelectedItemPosition() > 0
                            && campoContacto1.getSelectedItemPosition() == 0){
                        Toast.makeText(EditarMedicamentoReceta.this, "Llenar campo contacto 1", Toast.LENGTH_SHORT).show();
                    }
                    else if (campoContacto1.getSelectedItemPosition() > 0 && campoContacto3.getSelectedItemPosition() > 0
                            && campoContacto2.getSelectedItemPosition() == 0){
                        Toast.makeText(EditarMedicamentoReceta.this, "Llenar campo contacto 2", Toast.LENGTH_SHORT).show();
                    }
                    else if (campoContacto3.getSelectedItemPosition() > 0 && campoContacto1.getSelectedItemPosition() == 0
                            && campoContacto2.getSelectedItemPosition() == 0){
                        Toast.makeText(EditarMedicamentoReceta.this, "Llenar campo contacto 1 y 2", Toast.LENGTH_SHORT).show();
                    }
                    else if (campoContacto2.getSelectedItemPosition() > 0 && campoContacto1.getSelectedItemPosition() == 0
                            && campoContacto3.getSelectedItemPosition() == 0){
                        Toast.makeText(EditarMedicamentoReceta.this, "Llenar campo contacto 1", Toast.LENGTH_SHORT).show();
                    }
                    else if(medicamentoReceta.getCantidadPorcion().length() > 3){
                        Toast.makeText(EditarMedicamentoReceta.this, "Verifique la cantidad de la porción", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        actualizarDatosMedicamento();
                    }

                }
                else{
                    Toast.makeText(EditarMedicamentoReceta.this, "Llenar campos vacíos", Toast.LENGTH_SHORT).show();
                }

            }
        });

        btnEliminarMedicamentoReceta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mensaje = new AlertDialog.Builder(EditarMedicamentoReceta.this);
                mensaje.setMessage("¿Está seguro de eliminar el medicamento?")
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
                                eliminarMedicamento();
                                startActivity(new Intent(EditarMedicamentoReceta.this,Receta.class));
                                finish();
                            }
                        });
                AlertDialog titulo = mensaje.create();
                titulo.setTitle("Eliminar");
                titulo.show();
            }
        });

    }

     public void mostrarDatosMedicamento(){
         cargarSpinnerContacto();
         datosSpinner();

         baseDatos.child("receta").child(usuario.getIdUsuario()).child(medicamentoReceta.getIdMedicamento()).
                 addListenerForSingleValueEvent(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 if(dataSnapshot.exists()){
                     medicamentoReceta.setTipoReceta(dataSnapshot.child("tipoReceta").getValue().toString());
                     medicamentoReceta.setCedulaMedica(dataSnapshot.child("cedulaMedica").getValue().toString());
                     medicamentoReceta.setNombreMedicamento(dataSnapshot.child("medicamento").getValue().toString());
                     medicamentoReceta.setCantidadPorcion(dataSnapshot.child("cantidadPorcion").getValue().toString());
                     medicamentoReceta.setTipoPorcion(dataSnapshot.child("tipoPorcion").getValue().toString());
                     medicamentoReceta.setCantidadMedicamento(dataSnapshot.child("cantidadMedicamento").getValue().toString());
                     medicamentoReceta.setIntervaloHora(dataSnapshot.child("intervaloHora").getValue().toString());
                     medicamentoReceta.setDias(dataSnapshot.child("dias").getValue().toString());
                     medicamentoReceta.setViaAdministracion(dataSnapshot.child("viaAdministracion").getValue().toString());
                     medicamentoReceta.setContacto1(dataSnapshot.child("contacto1").getValue().toString());
                     medicamentoReceta.setContacto2(dataSnapshot.child("contacto2").getValue().toString());
                     medicamentoReceta.setContacto3(dataSnapshot.child("contacto3").getValue().toString());

                     baseDatos.child("contacto").child(usuario.getIdUsuario()).addValueEventListener(new ValueEventListener() {
                         @Override
                         public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                             if (dataSnapshot.exists()){
                                 for (DataSnapshot objSnapshot : dataSnapshot.getChildren()){
                                     if (objSnapshot.child("idContacto").getValue().toString().equals(medicamentoReceta.getContacto1())){
                                         medicamentoReceta.setContacto1(objSnapshot.child("nombre").getValue().toString() + " " +
                                                 objSnapshot.child("apellido").getValue().toString() );
                                     }
                                     else if (objSnapshot.child("idContacto").getValue().toString().equals(medicamentoReceta.getContacto2())){
                                         medicamentoReceta.setContacto2(objSnapshot.child("nombre").getValue().toString() + " " +
                                                 objSnapshot.child("apellido").getValue().toString() );
                                     }
                                     else if (objSnapshot.child("idContacto").getValue().toString().equals(medicamentoReceta.getContacto3())){
                                         medicamentoReceta.setContacto3(objSnapshot.child("nombre").getValue().toString() + " " +
                                                 objSnapshot.child("apellido").getValue().toString() );
                                     }
                                 }
                                 for (int i = 0; i < lista_contacto.size(); i++){
                                     if (lista_contacto.get(i).equals(medicamentoReceta.getContacto1())){
                                         campoContacto1.setSelection(i);
                                     }
                                     else if(lista_contacto.get(i).equals(medicamentoReceta.getContacto2())){
                                         campoContacto2.setSelection(i);
                                     }
                                     else if(lista_contacto.get(i).equals(medicamentoReceta.getContacto3())){
                                         campoContacto3.setSelection(i);
                                     }
                                 }
                             }
                         }

                         @Override
                         public void onCancelled(@NonNull DatabaseError databaseError) {

                         }
                     });

                     campoCedulaMedica.setText(medicamentoReceta.getCedulaMedica());
                     campoNombreMedicamento.setText(medicamentoReceta.getNombreMedicamento());
                     campoCantidadPorcion.setText(medicamentoReceta.getCantidadPorcion());

                     for (int i = 0; i < campoTipoReceta.getAdapter().getCount(); i++) {
                         if (campoTipoReceta.getItemAtPosition(i).toString().equals(medicamentoReceta.getTipoReceta())) {
                             campoTipoReceta.setSelection(i);
                         }
                     }

                     for (int i = 0; i < campoOpcionPorcion.getAdapter().getCount(); i++) {
                         if (campoOpcionPorcion.getItemAtPosition(i).toString().equals(medicamentoReceta.getTipoPorcion())) {
                             campoOpcionPorcion.setSelection(i);
                         }
                     }

                     for (int i = 0; i < campoCantidad.getAdapter().getCount(); i++) {
                         if (campoCantidad.getItemAtPosition(i).toString().equals(medicamentoReceta.getCantidadMedicamento())) {
                             campoCantidad.setSelection(i);
                         }
                     }

                     for (int i = 0; i < campoOpcionHora.getAdapter().getCount(); i++) {
                         if (campoOpcionHora.getItemAtPosition(i).toString().equals(medicamentoReceta.getIntervaloHora())) {
                             campoOpcionHora.setSelection(i);
                         }
                     }

                     for (int i = 0; i < campoOpcionDia.getAdapter().getCount(); i++) {
                         if (campoOpcionDia.getItemAtPosition(i).toString().equals(medicamentoReceta.getDias())) {
                             campoOpcionDia.setSelection(i);
                         }
                     }

                     for (int i = 0; i < campoOpcionVia.getAdapter().getCount(); i++) {
                         if (campoOpcionVia.getItemAtPosition(i).toString().equals(medicamentoReceta.getViaAdministracion())) {
                             campoOpcionVia.setSelection(i);
                         }
                     }
                 }
             }

             @Override
             public void onCancelled(@NonNull DatabaseError databaseError) {

             }
         });
     }

    public void actualizarDatosMedicamento(){
        final Map<String, Object> datos_medicamento = new HashMap<>();

        datos_medicamento.put("idMedicamento", medicamentoReceta.getIdMedicamento());
        datos_medicamento.put("tipoReceta", medicamentoReceta.getTipoReceta());
        datos_medicamento.put("cedulaMedica", medicamentoReceta.getCedulaMedica());
        datos_medicamento.put("medicamento", medicamentoReceta.getNombreMedicamento());
        datos_medicamento.put("cantidadPorcion", medicamentoReceta.getCantidadPorcion());
        datos_medicamento.put("tipoPorcion", medicamentoReceta.getTipoPorcion());
        datos_medicamento.put("cantidadMedicamento", medicamentoReceta.getCantidadMedicamento());
        datos_medicamento.put("intervaloHora", medicamentoReceta.getIntervaloHora());
        datos_medicamento.put("dias", medicamentoReceta.getDias());
        datos_medicamento.put("viaAdministracion", medicamentoReceta.getViaAdministracion());
        datos_medicamento.put("contacto1", medicamentoReceta.getContacto1());
        datos_medicamento.put("contacto2", medicamentoReceta.getContacto2());
        datos_medicamento.put("contacto3", medicamentoReceta.getContacto3());

        baseDatos.child("receta").child(usuario.getIdUsuario()).child(medicamentoReceta.getIdMedicamento()).
                addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.child("intervaloHora").getValue().toString().equals(medicamentoReceta.getIntervaloHora()) ||
                        !dataSnapshot.child("dias").getValue().toString().equals(medicamentoReceta.getDias())){
                    AlertDialog.Builder mensaje2 = new AlertDialog.Builder(EditarMedicamentoReceta.this);
                    mensaje2.setMessage("¿Está seguro de querer editar estos datos?" + "\n"
                            + "Ojo: si cambias los días o horas se reiniciará el seguimiento de este medicamento")
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
                                    reiniciarSeguimiento();
                                    baseDatos.child("receta").child(usuario.getIdUsuario()).child(medicamentoReceta.getIdMedicamento()).
                                            setValue(datos_medicamento).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                Toast.makeText(EditarMedicamentoReceta.this, "Se actualizó con éxito", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(EditarMedicamentoReceta.this,Receta.class);
                                                startActivity(intent);
                                            }
                                            else{
                                                Toast.makeText(EditarMedicamentoReceta.this, "Error al actualizar los datos", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            });
                    AlertDialog titulo2 = mensaje2.create();
                    titulo2.setTitle("Advertencia");
                    titulo2.show();
                }
                else{
                    actualizarDatosSeguimiento();
                    baseDatos.child("receta").child(usuario.getIdUsuario()).child(medicamentoReceta.getIdMedicamento()).
                            setValue(datos_medicamento).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(EditarMedicamentoReceta.this, "Se actualizó con éxito", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(EditarMedicamentoReceta.this,Receta.class);
                                startActivity(intent);
                            }
                            else{
                                Toast.makeText(EditarMedicamentoReceta.this, "Error al actualizar los datos", Toast.LENGTH_SHORT).show();
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

     public void actualizarDatosSeguimiento(){
         baseDatos.child("seguimiento").child(usuario.getIdUsuario()).addListenerForSingleValueEvent(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 if (dataSnapshot.exists()){
                     for (DataSnapshot objSnapshot : dataSnapshot.getChildren()){
                         if (objSnapshot.child("idMedicamento").getValue().toString().equals(medicamentoReceta.getIdMedicamento())) {
                             seguimiento.setIdSeguimiento(objSnapshot.child("idSeguimiento").getValue().toString());
                             seguimiento.setIdAlarma(objSnapshot.child("idAlarma").getValue().toString());
                             seguimiento.setHoraAlarma(objSnapshot.child("horaAlarma").getValue().toString());
                             seguimiento.setCantidadTotal(objSnapshot.child("cantidadTotal").getValue().toString());
                             seguimiento.setCantidadTomada(objSnapshot.child("cantidadTomada").getValue().toString());
                             seguimiento.setAlarmaConfirmda(objSnapshot.child("alarmaConfirmada").getValue().toString());
                             seguimiento.setEnvioSmsContacto(objSnapshot.child("envioSmsContacto").getValue().toString());

                             datos_seguimiento.put("idSeguimiento", seguimiento.getIdSeguimiento());
                             datos_seguimiento.put("idMedicamento", medicamentoReceta.getIdMedicamento());
                             datos_seguimiento.put("idAlarma", objSnapshot.child("idAlarma").getValue().toString());
                             datos_seguimiento.put("tipo", medicamentoReceta.getTipoReceta());
                             datos_seguimiento.put("medicamento", medicamentoReceta.getNombreMedicamento());
                             datos_seguimiento.put("cantidadPorcion", medicamentoReceta.getCantidadPorcion());
                             datos_seguimiento.put("tipoPorcion", medicamentoReceta.getTipoPorcion());
                             datos_seguimiento.put("cantidadMedicamento", medicamentoReceta.getCantidadMedicamento());
                             datos_seguimiento.put("viaAdministracion", medicamentoReceta.getViaAdministracion());
                             datos_seguimiento.put("intervaloHora", medicamentoReceta.getIntervaloHora());
                             datos_seguimiento.put("cantidadTotal", seguimiento.getCantidadTotal());
                             datos_seguimiento.put("cantidadTomada", seguimiento.getCantidadTomada());
                             datos_seguimiento.put("alarmaConfirmada", seguimiento.getAlarmaConfirmda());
                             datos_seguimiento.put("horaAlarma", seguimiento.getHoraAlarma());
                             datos_seguimiento.put("envioSmsContacto", seguimiento.getEnvioSmsContacto());


                             baseDatos.child("seguimiento").child(usuario.getIdUsuario()).child(seguimiento.getIdSeguimiento()).setValue(datos_seguimiento).
                                     addOnCompleteListener(new OnCompleteListener<Void>() {
                                         @Override
                                         public void onComplete(@NonNull Task<Void> task) {
                                         }
                                     });

                             //SE ELIMINA LA ALARMA PROGRAMADA, YA QUE ALMACENA LOS DATOS DE SEGUIMIENTO Y SON INMUTABLES
                             Intent intent = new Intent(EditarMedicamentoReceta.this, Notificacion.class);
                             PendingIntent pendingIntent = PendingIntent.getBroadcast(EditarMedicamentoReceta.this, Integer.parseInt(seguimiento.getIdAlarma()), intent, PendingIntent.FLAG_IMMUTABLE);
                             AlarmManager alarmManager = (AlarmManager) EditarMedicamentoReceta.this.getSystemService(ALARM_SERVICE);
                             alarmManager.cancel(pendingIntent);

                             //SE CREA LA NUEVA ALARMA CON LOS DATOS ACTUALIZADOS Y SE ENVIAN LOS DATOS A LA CLASE NOTIFICACION
                             final int idAlarma = (int) System.currentTimeMillis();
                             Calendar calendar = Calendar.getInstance();
                             calendar.setTimeInMillis(System.currentTimeMillis());
                             calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(seguimiento.getHoraAlarma().substring(0, 2)));
                             calendar.set(Calendar.MINUTE, Integer.parseInt(seguimiento.getHoraAlarma().substring(3, 5)));
                             datos_seguimiento.put("idAlarma", String.valueOf(idAlarma));
                             datos_seguimiento.put("alarmaConfirmada", "no confirmado");
                             Intent intent2 = new Intent(EditarMedicamentoReceta.this, Notificacion.class);
                             intent2.putExtra("idSeguimiento", seguimiento.getIdSeguimiento());
                             intent2.putExtra("idNotificacion", seguimiento.getIdAlarma());
                             intent2.putExtra("datosSeguimento", datos_seguimiento);
                             intent2.putExtra("mensaje", "Es hora de tomar tu medicamento");
                             PendingIntent pendingIntent2 = PendingIntent.getBroadcast(EditarMedicamentoReceta.this, idAlarma ,intent2, PendingIntent.FLAG_IMMUTABLE);
                             AlarmManager alarmManager2 = (AlarmManager) getSystemService(ALARM_SERVICE);
                             alarmManager2.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() ,30 * 1000, pendingIntent2);
                         }
                     }
                 }
             }

             @Override
             public void onCancelled(@NonNull DatabaseError databaseError) {

             }
         });
     }

     public void eliminarMedicamento(){
         baseDatos.child("seguimiento").child(usuario.getIdUsuario()).addListenerForSingleValueEvent(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 if (dataSnapshot.exists()){
                     for (DataSnapshot objSnapshot : dataSnapshot.getChildren()){
                         if (objSnapshot.child("idMedicamento").getValue().toString().equals(medicamentoReceta.getIdMedicamento())){
                             Toast.makeText(EditarMedicamentoReceta.this, "Se eliminó con éxito", Toast.LENGTH_SHORT).show();
                             baseDatos.child("seguimiento").child(usuario.getIdUsuario()).child(objSnapshot.child("idSeguimiento").getValue().toString()).removeValue();
                             baseDatos.child("receta").child(usuario.getIdUsuario()).child(medicamentoReceta.getIdMedicamento()).removeValue();
                             eliminarAlarma();
                         }
                     }
                 }
             }

             @Override
             public void onCancelled(@NonNull DatabaseError databaseError) {

             }
         });

     }

     public void eliminarAlarma (){
         baseDatos.child("seguimiento").child(usuario.getIdUsuario()).addListenerForSingleValueEvent(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 if (dataSnapshot.exists()){
                     for (DataSnapshot objSnapshot : dataSnapshot.getChildren()){
                         if (objSnapshot.child("idMedicamento").getValue().toString().equals(medicamentoReceta.getIdMedicamento())){
                             seguimiento.setIdAlarma(objSnapshot.child("idAlarma").getValue().toString());
                             Intent intent = new Intent(EditarMedicamentoReceta.this, Notificacion.class);
                             PendingIntent pendingIntent = PendingIntent.getBroadcast(EditarMedicamentoReceta.this,
                                     Integer.parseInt(seguimiento.getIdAlarma()), intent, PendingIntent.FLAG_ONE_SHOT);
                             AlarmManager alarmManager = (AlarmManager) EditarMedicamentoReceta.this.getSystemService(ALARM_SERVICE);
                             alarmManager.cancel(pendingIntent);
                         }
                     }
                 }
             }

             @Override
             public void onCancelled(@NonNull DatabaseError databaseError) {

             }
         });
     }

     public void reiniciarSeguimiento(){
         final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");

         baseDatos.child("seguimiento").child(usuario.getIdUsuario()).addListenerForSingleValueEvent(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 if (dataSnapshot.exists()){
                     for (DataSnapshot objSnapshot : dataSnapshot.getChildren()){
                         if (objSnapshot.child("idMedicamento").getValue().toString().equals(medicamentoReceta.getIdMedicamento())) {
                             seguimiento.setIdSeguimiento(objSnapshot.child("idSeguimiento").getValue().toString());
                             seguimiento.setIdAlarma(objSnapshot.child("idAlarma").getValue().toString());
                             seguimiento.setHoraAlarma(simpleDateFormat.format(new Date()));
                             datos_seguimiento.put("idSeguimiento", seguimiento.getIdSeguimiento());
                             datos_seguimiento.put("idMedicamento", medicamentoReceta.getIdMedicamento());
                             datos_seguimiento.put("idAlarma", objSnapshot.child("idAlarma").getValue().toString());
                             datos_seguimiento.put("tipo", medicamentoReceta.getTipoReceta());
                             datos_seguimiento.put("medicamento", medicamentoReceta.getNombreMedicamento());
                             datos_seguimiento.put("cantidadPorcion", medicamentoReceta.getCantidadPorcion());
                             datos_seguimiento.put("tipoPorcion", medicamentoReceta.getTipoPorcion());
                             datos_seguimiento.put("cantidadMedicamento", medicamentoReceta.getCantidadMedicamento());
                             datos_seguimiento.put("viaAdministracion", medicamentoReceta.getViaAdministracion());
                             datos_seguimiento.put("intervaloHora", medicamentoReceta.getIntervaloHora());
                             datos_seguimiento.put("cantidadTotal", String.valueOf
                                     ((Integer.parseInt(medicamentoReceta.getDias()) * 24) / (Integer.parseInt(medicamentoReceta.getIntervaloHora()))));
                             datos_seguimiento.put("cantidadTomada", "1");
                             datos_seguimiento.put("alarmaConfirmada","");
                             datos_seguimiento.put("envioSmsContacto","0");

                             // POSIBLES CASOS DONDE PARA IGUALAR A LOS DIGITOS DE LA HORA
                             if (Integer.parseInt(seguimiento.getHoraAlarma().substring(3, 5)) +
                                     Integer.parseInt(medicamentoReceta.getIntervaloHora()) >= 70){
                                 if (Integer.parseInt(seguimiento.getHoraAlarma().substring(0, 2)) < 9){
                                     datos_seguimiento.put("horaAlarma", "0" + (Integer.parseInt(seguimiento.getHoraAlarma().substring(0, 2)) + 1)
                                             + seguimiento.getHoraAlarma().substring(2, 3) + (Integer.parseInt(seguimiento.getHoraAlarma().substring(3, 5))
                                             + Integer.parseInt(medicamentoReceta.getIntervaloHora()) - 60));
                                 }
                                 else{
                                     datos_seguimiento.put("horaAlarma", (Integer.parseInt(seguimiento.getHoraAlarma().substring(0, 2)) + 1)
                                             + seguimiento.getHoraAlarma().substring(2, 3) + (Integer.parseInt(seguimiento.getHoraAlarma().substring(3, 5))
                                             + Integer.parseInt(medicamentoReceta.getIntervaloHora()) - 60));
                                 }
                             }
                             else  if (Integer.parseInt(seguimiento.getHoraAlarma().substring(3, 5)) +
                                     Integer.parseInt(medicamentoReceta.getIntervaloHora()) >= 60 ){
                                 if (Integer.parseInt(seguimiento.getHoraAlarma().substring(0, 2)) < 9){
                                     datos_seguimiento.put("horaAlarma", "0" + (Integer.parseInt(seguimiento.getHoraAlarma().substring(0, 2)) + 1)
                                             + seguimiento.getHoraAlarma().substring(2, 3) + "0" + (Integer.parseInt(seguimiento.getHoraAlarma().substring(3, 5))
                                             + Integer.parseInt(medicamentoReceta.getIntervaloHora()) - 60));
                                 }
                                 else{
                                     datos_seguimiento.put("horaAlarma", (Integer.parseInt(seguimiento.getHoraAlarma().substring(0, 2)) + 1)
                                             + seguimiento.getHoraAlarma().substring(2, 3) + "0" + (Integer.parseInt(seguimiento.getHoraAlarma().substring(3, 5))
                                             + Integer.parseInt(medicamentoReceta.getIntervaloHora()) - 60));
                                 }
                             }
                             else if (Integer.parseInt(seguimiento.getHoraAlarma().substring(3, 5)) + Integer.parseInt(medicamentoReceta.getIntervaloHora())  <= 9){
                                 datos_seguimiento.put("horaAlarma", seguimiento.getHoraAlarma().substring(0, 3) + "0" +
                                         (Integer.parseInt(seguimiento.getHoraAlarma().substring(3, 5)) + Integer.parseInt(medicamentoReceta.getIntervaloHora())));
                             }
                             else {
                                 datos_seguimiento.put("horaAlarma", seguimiento.getHoraAlarma().substring(0, 3) +
                                         (Integer.parseInt(seguimiento.getHoraAlarma().substring(3, 5)) + Integer.parseInt(medicamentoReceta.getIntervaloHora())));
                             }

                             baseDatos.child("seguimiento").child(usuario.getIdUsuario()).child(seguimiento.getIdSeguimiento()).setValue(datos_seguimiento).addOnCompleteListener(new OnCompleteListener<Void>() {
                                 @Override
                                 public void onComplete(@NonNull Task<Void> task) {
                                 }
                             });

                             //SE ELIMINA LA ALARMA DEL SEGUIMIENTO QUE SE REINICIA
                             Intent intent = new Intent(EditarMedicamentoReceta.this, Notificacion.class);
                             PendingIntent pendingIntent = PendingIntent.getBroadcast(EditarMedicamentoReceta.this, Integer.parseInt(seguimiento.getIdAlarma()), intent, PendingIntent.FLAG_IMMUTABLE);
                             AlarmManager alarmManager = (AlarmManager) EditarMedicamentoReceta.this.getSystemService(ALARM_SERVICE);
                             alarmManager.cancel(pendingIntent);

                            //SE CREA LA NUEVA ALARMA DEL NUEVO SEGUIMIENTO Y SE ENVIAN LOS DATOS A LA CLASE NOTIFICACION
                             final int idAlarma = (int) System.currentTimeMillis();
                             datos_seguimiento.put("idAlarma", String.valueOf(idAlarma));
                             datos_seguimiento.put("alarmaConfirmada", "no confirmado");
                             Intent intent2 = new Intent(EditarMedicamentoReceta.this, Notificacion.class);
                             intent2.putExtra("idSeguimiento", seguimiento.getIdSeguimiento());
                             intent2.putExtra("idNotificacion", seguimiento.getIdAlarma());
                             intent2.putExtra("datosSeguimento", datos_seguimiento);
                             intent2.putExtra("mensaje", "Es hora de tomar tu medicamento");
                             PendingIntent pendingIntent2 = PendingIntent.getBroadcast(EditarMedicamentoReceta.this, idAlarma ,intent2, PendingIntent.FLAG_IMMUTABLE);
                             AlarmManager alarmManager2 = (AlarmManager) getSystemService(ALARM_SERVICE);
                             alarmManager2.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() +
                                     Integer.parseInt(medicamentoReceta.getIntervaloHora()) * 60 * 1000,30 * 1000, pendingIntent2);
                         }
                     }
                 }
             }

             @Override
             public void onCancelled(@NonNull DatabaseError databaseError) {

             }
         });

     }

     public void cargarSpinnerContacto(){
         baseDatos.child("contacto").child(usuario.getIdUsuario()).addListenerForSingleValueEvent( new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 lista_contacto.clear();
                 String nombreContacto = "", apellidoContacto;
                 lista_contacto.add("");
                 if(dataSnapshot.exists()) {
                     for (DataSnapshot objSnapshot : dataSnapshot.getChildren()) {
                         id_contacto.add(objSnapshot.child("idContacto").getValue().toString());
                         nombreContacto = (objSnapshot.child("nombre").getValue().toString());
                         apellidoContacto = (objSnapshot.child("apellido").getValue().toString());
                         lista_contacto.add(nombreContacto + " " + apellidoContacto);
                     }
                 }

                 ArrayAdapter<String> contacto = new ArrayAdapter<>(EditarMedicamentoReceta.this,android.R.layout.simple_spinner_item,lista_contacto);
                 campoContacto1.setAdapter(contacto);
                 campoContacto2.setAdapter(contacto);
                 campoContacto3.setAdapter(contacto);
             }

             @Override
             public void onCancelled(@NonNull DatabaseError databaseError) {

             }
         });
     }

     public void datosSpinner(){

         ArrayAdapter<CharSequence> adaptadorTipoReceta = ArrayAdapter.createFromResource(this,R.array.tipo_receta,
                 android.R.layout.simple_spinner_item);
         campoTipoReceta.setAdapter(adaptadorTipoReceta);

         ArrayAdapter<CharSequence> adaptadorOpcionDia = ArrayAdapter.createFromResource(EditarMedicamentoReceta.this,R.array.dia,
                 android.R.layout.simple_spinner_item);
         campoOpcionDia.setAdapter(adaptadorOpcionDia);


         ArrayAdapter<CharSequence> adaptadorOpcionHora = ArrayAdapter.createFromResource(this,R.array.hora,
                 android.R.layout.simple_spinner_item);
         campoOpcionHora.setAdapter(adaptadorOpcionHora);

         ArrayAdapter<CharSequence> adaptadorOpcionVia = ArrayAdapter.createFromResource(this,R.array.tipo_via,
                 android.R.layout.simple_spinner_item);
         campoOpcionVia.setAdapter(adaptadorOpcionVia);

         ArrayAdapter<CharSequence> adaptadorOpcionPorcion = ArrayAdapter.createFromResource(this,R.array.tipo_porcion,
                 android.R.layout.simple_spinner_item);
         campoOpcionPorcion.setAdapter(adaptadorOpcionPorcion);

         ArrayAdapter<CharSequence> adaptadorOpcionCantidad = ArrayAdapter.createFromResource(this,R.array.cantidad,
                 android.R.layout.simple_spinner_item);
         campoCantidad.setAdapter(adaptadorOpcionCantidad);
     }
 }
