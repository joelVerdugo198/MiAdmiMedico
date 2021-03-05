
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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class EditarMedicamento extends AppCompatActivity {

    ClaseMedicamento medicamento = new ClaseMedicamento();
    ClaseSeguimiento seguimiento = new ClaseSeguimiento();
    ClaseUsuario usuario = new ClaseUsuario();

    FirebaseAuth mAuth;
    DatabaseReference baseDatos;

    EditText campoNombreMedicamento, campoCantidadPorcion;

    Spinner campoOpcionDia, campoOpcionHora, campoOpcionPorcion,
            campoOpcionCantidad, campoOpcionVia;

    final HashMap<String, Object> datos_seguimiento = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_medicamento);

        Toolbar toolbar;
        Button btnGuardarMedicamento, btnEliminarMedicamento;

        mAuth = FirebaseAuth.getInstance();
        baseDatos = FirebaseDatabase.getInstance().getReference();

        medicamento.setIdMedicamento(getIntent().getStringExtra("idMedicamento"));
        usuario.setIdUsuario(mAuth.getCurrentUser().getUid());

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        campoNombreMedicamento = (EditText) findViewById(R.id.escribirMedicamentoEditarMedicamento);
        campoCantidadPorcion = (EditText) findViewById(R.id.escribirPorcionEditarMedicamento);
        campoOpcionPorcion = (Spinner) findViewById(R.id.tipoPorcionEditarMedicamento);
        campoOpcionCantidad = (Spinner) findViewById(R.id.opcionCantidadEditarMedicamento);
        campoOpcionHora = (Spinner) findViewById(R.id.opcionHoraEditarMedicamento);
        campoOpcionDia = (Spinner) findViewById(R.id.opcionDiaEditarMedicamento);
        campoOpcionVia = (Spinner) findViewById(R.id.opcionViaEditarMedicamento);
        btnGuardarMedicamento = (Button) findViewById(R.id.btnGuardarEditarMedicamento);
        btnEliminarMedicamento = (Button) findViewById(R.id.btnEliminarMedicamento);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Editar medicamento ");

        mostrarDatosMedicamento();

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Medicamento.class));
            }
        });

        btnGuardarMedicamento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                medicamento.setNombreMedicamento(campoNombreMedicamento.getText().toString());
                medicamento.setCantidadPorcion(campoCantidadPorcion.getText().toString());
                medicamento.setTipoPorcion(campoOpcionPorcion.getSelectedItem().toString());
                medicamento.setCantidadMedicamento(campoOpcionCantidad.getSelectedItem().toString());
                medicamento.setIntervaloHora(campoOpcionHora.getSelectedItem().toString());
                medicamento.setDias(campoOpcionDia.getSelectedItem().toString());
                medicamento.setViaAdministracion(campoOpcionVia.getSelectedItem().toString());

                //SE COMPRUEBA DE QUE NO ESTEN VACIOS LOS CAMPOS Y CUMPlAN CON ESPECIFICACIONES
                if(!medicamento.getNombreMedicamento().isEmpty() && !medicamento.getCantidadPorcion().isEmpty() &&
                        !medicamento.getTipoPorcion().equals("") && !medicamento.getCantidadMedicamento().equals("") &&
                        !medicamento.getIntervaloHora().equals("") && !medicamento.getDias().equals("") &&
                        !medicamento.getViaAdministracion().equals("")) {

                    if(medicamento.getCantidadPorcion().length() > 3){
                        Toast.makeText(EditarMedicamento.this, "Verifique la cantidad de la porción", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        actualizarMedicamento();
                    }
                }
                else{
                    Toast.makeText(EditarMedicamento.this, "Llenar campos vacíos", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnEliminarMedicamento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mensaje = new AlertDialog.Builder(EditarMedicamento.this);
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
                                startActivity(new Intent(EditarMedicamento.this,Medicamento.class));
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
        datosSpinner();

        baseDatos.child("medicamento").child(usuario.getIdUsuario()).child(medicamento.getIdMedicamento()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    medicamento.setNombreMedicamento(dataSnapshot.child("medicamento").getValue().toString());
                    medicamento.setCantidadPorcion(dataSnapshot.child("cantidadPorcion").getValue().toString());
                    medicamento.setTipoPorcion(dataSnapshot.child("tipoPorcion").getValue().toString());
                    medicamento.setCantidadMedicamento(dataSnapshot.child("cantidadMedicamento").getValue().toString());
                    medicamento.setIntervaloHora(dataSnapshot.child("intervaloHora").getValue().toString());
                    medicamento.setDias(dataSnapshot.child("dias").getValue().toString());
                    medicamento.setViaAdministracion(dataSnapshot.child("viaAdministracion").getValue().toString());

                    campoNombreMedicamento.setText(medicamento.getNombreMedicamento());
                    campoCantidadPorcion.setText(medicamento.getCantidadPorcion());

                    for (int i = 0; i < campoOpcionPorcion.getAdapter().getCount(); i++) {
                        if (campoOpcionPorcion.getItemAtPosition(i).toString().equals(medicamento.getTipoPorcion())) {
                            campoOpcionPorcion.setSelection(i);
                        }
                    }

                    for (int i = 0; i < campoOpcionCantidad.getAdapter().getCount(); i++) {
                        if (campoOpcionCantidad.getItemAtPosition(i).toString().equals(medicamento.getCantidadMedicamento())) {
                            campoOpcionCantidad.setSelection(i);
                        }
                    }

                    for (int i = 0; i < campoOpcionHora.getAdapter().getCount(); i++) {
                        if (campoOpcionHora.getItemAtPosition(i).toString().equals(medicamento.getIntervaloHora())) {
                            campoOpcionHora.setSelection(i);
                        }
                    }

                    for (int i = 0; i < campoOpcionDia.getAdapter().getCount(); i++) {
                        if (campoOpcionDia.getItemAtPosition(i).toString().equals(medicamento.getDias())) {
                            campoOpcionDia.setSelection(i);
                        }
                    }

                    for (int i = 0; i < campoOpcionVia.getAdapter().getCount(); i++) {
                        if (campoOpcionVia.getItemAtPosition(i).toString().equals(medicamento.getViaAdministracion())) {
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

    public void actualizarMedicamento(){
        final Map<String, Object> datos_medicamento = new HashMap<>();

        datos_medicamento.put("idMedicamento", medicamento.getIdMedicamento());
        datos_medicamento.put("medicamento", medicamento.getNombreMedicamento());
        datos_medicamento.put("cantidadPorcion", medicamento.getCantidadPorcion());
        datos_medicamento.put("tipoPorcion", medicamento.getTipoPorcion());
        datos_medicamento.put("cantidadMedicamento", medicamento.getCantidadMedicamento());
        datos_medicamento.put("intervaloHora", medicamento.getIntervaloHora());
        datos_medicamento.put("dias", medicamento.getDias());
        datos_medicamento.put("viaAdministracion", medicamento.getViaAdministracion());

        baseDatos.child("medicamento").child(usuario.getIdUsuario()).child(medicamento.getIdMedicamento()).
                addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.child("intervaloHora").getValue().toString().equals(medicamento.getIntervaloHora()) ||
                        !dataSnapshot.child("dias").getValue().toString().equals(medicamento.getDias())) {
                    AlertDialog.Builder mensaje = new AlertDialog.Builder(EditarMedicamento.this);
                    mensaje.setMessage("¿Está seguro de querer editar estos datos?" + "\n"
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
                                    baseDatos.child("medicamento").child(usuario.getIdUsuario()).child(medicamento.getIdMedicamento()).
                                            setValue(datos_medicamento).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                startActivity(new Intent(EditarMedicamento.this,Medicamento.class));
                                                finish();
                                            }
                                            else{
                                                Toast.makeText(EditarMedicamento.this, "Error al actualizar los datos", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            });
                    AlertDialog titulo = mensaje.create();
                    titulo.setTitle("Advertencia");
                     titulo.show();
                }
                else{
                    actualizarSeguimiento();
                    baseDatos.child("medicamento").child(usuario.getIdUsuario()).child(medicamento.getIdMedicamento()).
                            setValue(datos_medicamento).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(EditarMedicamento.this, "Se actualizó con éxito", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(EditarMedicamento.this,Medicamento.class));
                                finish();
                            }
                            else{
                                Toast.makeText(EditarMedicamento.this, "Error al actualizar los datos", Toast.LENGTH_SHORT).show();
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

    public void actualizarSeguimiento(){
        baseDatos.child("seguimiento").child(usuario.getIdUsuario()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot objSnapshot : dataSnapshot.getChildren()){
                        if (objSnapshot.child("idMedicamento").getValue().toString().equals(medicamento.getIdMedicamento())) {
                            seguimiento.setIdSeguimiento(objSnapshot.child("idSeguimiento").getValue().toString());
                            seguimiento.setIdAlarma(objSnapshot.child("idAlarma").getValue().toString());
                            seguimiento.setHoraAlarma(objSnapshot.child("horaAlarma").getValue().toString());
                            seguimiento.setCantidadTotal(objSnapshot.child("cantidadTotal").getValue().toString());
                            seguimiento.setCantidadTomada(objSnapshot.child("cantidadTomada").getValue().toString());
                            seguimiento.setAlarmaConfirmda(objSnapshot.child("alarmaConfirmada").getValue().toString());

                            datos_seguimiento.put("idSeguimiento", seguimiento.getIdSeguimiento());
                            datos_seguimiento.put("idMedicamento", medicamento.getIdMedicamento());
                            datos_seguimiento.put("idAlarma", objSnapshot.child("idAlarma").getValue().toString());
                            datos_seguimiento.put("tipo", "Medicamento");
                            datos_seguimiento.put("medicamento", medicamento.getNombreMedicamento());
                            datos_seguimiento.put("cantidadPorcion", medicamento.getCantidadPorcion());
                            datos_seguimiento.put("tipoPorcion", medicamento.getTipoPorcion());
                            datos_seguimiento.put("cantidadMedicamento", medicamento.getCantidadMedicamento());
                            datos_seguimiento.put("viaAdministracion", medicamento.getViaAdministracion());
                            datos_seguimiento.put("intervaloHora", medicamento.getIntervaloHora());
                            datos_seguimiento.put("cantidadTotal", seguimiento.getCantidadTotal());
                            datos_seguimiento.put("cantidadTomada", seguimiento.getCantidadTomada());
                            datos_seguimiento.put("alarmaConfirmada", seguimiento.getAlarmaConfirmda());
                            datos_seguimiento.put("horaAlarma", seguimiento.getHoraAlarma());

                            baseDatos.child("seguimiento").child(usuario.getIdUsuario()).child(seguimiento.getIdSeguimiento()).setValue(datos_seguimiento).
                                    addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                        }
                                    });
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
        baseDatos.child("seguimiento").child(usuario.getIdUsuario()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot objSnapshot : dataSnapshot.getChildren()){
                        if (objSnapshot.child("idMedicamento").getValue().toString().equals(medicamento.getIdMedicamento())){
                            Toast.makeText(EditarMedicamento.this, "Se eliminó con éxito", Toast.LENGTH_SHORT).show();
                            baseDatos.child("seguimiento").child(usuario.getIdUsuario()).child(objSnapshot.child("idSeguimiento")
                                    .getValue().toString()).removeValue();
                            baseDatos.child("medicamento").child(usuario.getIdUsuario()).child(medicamento.getIdMedicamento()).removeValue();
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
                         if (objSnapshot.child("idMedicamento").getValue().toString().equals(medicamento.getIdMedicamento())){
                             seguimiento.setIdAlarma(objSnapshot.child("idAlarma").getValue().toString());
                             Intent intent = new Intent(EditarMedicamento.this, Notificacion.class);
                             PendingIntent pendingIntent = PendingIntent.getBroadcast(EditarMedicamento.this,
                                     Integer.parseInt(seguimiento.getIdAlarma()), intent, PendingIntent.FLAG_ONE_SHOT);
                             AlarmManager alarmManager = (AlarmManager) EditarMedicamento.this.getSystemService(ALARM_SERVICE);
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
                        if (objSnapshot.child("idMedicamento").getValue().toString().equals(medicamento.getIdMedicamento())) {
                            seguimiento.setIdSeguimiento(objSnapshot.child("idSeguimiento").getValue().toString());
                            seguimiento.setIdAlarma(objSnapshot.child("idAlarma").getValue().toString());
                            seguimiento.setHoraAlarma(simpleDateFormat.format(new Date()));

                            //SE ELIMINA LA ALARMA DEL SEGUIMIENTO QUE SE REINICIA
                            Intent intent = new Intent(EditarMedicamento.this, Notificacion.class);
                            PendingIntent pendingIntent = PendingIntent.getBroadcast(EditarMedicamento.this,
                                    Integer.parseInt(seguimiento.getIdAlarma()), intent, PendingIntent.FLAG_ONE_SHOT);
                            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                            alarmManager.cancel(pendingIntent);

                            datos_seguimiento.put("idSeguimiento", seguimiento.getIdSeguimiento());
                            datos_seguimiento.put("idMedicamento", medicamento.getIdMedicamento());
                            datos_seguimiento.put("idAlarma",objSnapshot.child("idAlarma").getValue().toString());
                            datos_seguimiento.put("tipo", "Medicamento");
                            datos_seguimiento.put("medicamento", medicamento.getNombreMedicamento());
                            datos_seguimiento.put("cantidadPorcion", medicamento.getCantidadPorcion());
                            datos_seguimiento.put("tipoPorcion", medicamento.getTipoPorcion());
                            datos_seguimiento.put("cantidadMedicamento", medicamento.getCantidadMedicamento());
                            datos_seguimiento.put("viaAdministracion", medicamento.getViaAdministracion());
                            datos_seguimiento.put("intervaloHora", medicamento.getIntervaloHora());
                            datos_seguimiento.put("cantidadTotal", String.valueOf
                                    ((Integer.parseInt(medicamento.getDias()) * 24) / (Integer.parseInt(medicamento.getIntervaloHora()))));
                            datos_seguimiento.put("cantidadTomada", "1");
                            datos_seguimiento.put("alarmaConfirmada", "");

                            // POSIBLES CASOS DONDE PARA IGUALAR A LOS DIGITOS DE LA HORA
                            if (Integer.parseInt(seguimiento.getHoraAlarma().substring(3, 5)) +
                                    Integer.parseInt(medicamento.getIntervaloHora()) >= 70){
                                if (Integer.parseInt(seguimiento.getHoraAlarma().substring(0, 2)) < 9){
                                    datos_seguimiento.put("horaAlarma", "0" + (Integer.parseInt(seguimiento.getHoraAlarma().substring(0, 2)) + 1)
                                            + seguimiento.getHoraAlarma().substring(2, 3) + (Integer.parseInt(seguimiento.getHoraAlarma().substring(3, 5))
                                            + Integer.parseInt(medicamento.getIntervaloHora()) - 60));
                                }
                                else{
                                    datos_seguimiento.put("horaAlarma", (Integer.parseInt(seguimiento.getHoraAlarma().substring(0, 2)) + 1)
                                            + seguimiento.getHoraAlarma().substring(2, 3) + (Integer.parseInt(seguimiento.getHoraAlarma().substring(3, 5))
                                            + Integer.parseInt(medicamento.getIntervaloHora()) - 60));
                                }
                            }
                            else  if (Integer.parseInt(seguimiento.getHoraAlarma().substring(3, 5)) +
                                    Integer.parseInt(medicamento.getIntervaloHora()) >= 60 ){
                                if (Integer.parseInt(seguimiento.getHoraAlarma().substring(0, 2)) < 9){
                                    datos_seguimiento.put("horaAlarma", "0" + (Integer.parseInt(seguimiento.getHoraAlarma().substring(0, 2)) + 1)
                                            + seguimiento.getHoraAlarma().substring(2, 3) + "0" + (Integer.parseInt(seguimiento.getHoraAlarma().substring(3, 5))
                                            + Integer.parseInt(medicamento.getIntervaloHora()) - 60));
                                }
                                else{
                                    datos_seguimiento.put("horaAlarma", (Integer.parseInt(seguimiento.getHoraAlarma().substring(0, 2)) + 1)
                                            + seguimiento.getHoraAlarma().substring(2, 3) + "0" + (Integer.parseInt(seguimiento.getHoraAlarma().substring(3, 5))
                                            + Integer.parseInt(medicamento.getIntervaloHora()) - 60));
                                }
                            }
                            else if (Integer.parseInt(seguimiento.getHoraAlarma().substring(3, 5)) + Integer.parseInt(medicamento.getIntervaloHora())  <=  9){
                                datos_seguimiento.put("horaAlarma", seguimiento.getHoraAlarma().substring(0, 3) + "0" +
                                        (Integer.parseInt(seguimiento.getHoraAlarma().substring(3, 5)) + Integer.parseInt(medicamento.getIntervaloHora())));
                            }
                            else {
                                datos_seguimiento.put("horaAlarma", seguimiento.getHoraAlarma().substring(0, 3) +
                                        (Integer.parseInt(seguimiento.getHoraAlarma().substring(3, 5)) + Integer.parseInt(medicamento.getIntervaloHora())));
                            }

                            baseDatos.child("seguimiento").child(usuario.getIdUsuario()).child(seguimiento.getIdSeguimiento()).setValue(datos_seguimiento).
                                    addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                }
                            });



                            //SE CREA LA NUEVA ALARMA DEL NUEVO SEGUIMIENTO Y SE ENVIAN LOS DATOS A LA CLASE NOTIFICACION
                            final int idAlarma = (int) System.currentTimeMillis();
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTimeInMillis(System.currentTimeMillis());
                            calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(seguimiento.getHoraAlarma().substring(0, 2)));
                            calendar.set(Calendar.MINUTE, Integer.parseInt(seguimiento.getHoraAlarma().substring(3, 5)));
                            datos_seguimiento.put("idAlarma", String.valueOf(idAlarma));
                            datos_seguimiento.put("alarmaConfirmada", "no confirmado");
                            Intent intent2 = new Intent(EditarMedicamento.this, Notificacion.class);
                            intent2.putExtra("idSeguimiento", seguimiento.getIdSeguimiento());
                            intent2.putExtra("idNotificacion", idAlarma);
                            intent2.putExtra("datosSeguimento", datos_seguimiento);
                            intent2.putExtra("mensaje", "Es hora de tomar tu medicamento");
                            PendingIntent pendingIntent2 = PendingIntent.getBroadcast(EditarMedicamento.this, idAlarma, intent2,
                                    PendingIntent.FLAG_ONE_SHOT);
                            AlarmManager alarmManager2 = (AlarmManager) getSystemService(ALARM_SERVICE);
                            alarmManager2.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent2);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void datosSpinner() {
        ArrayAdapter<CharSequence> adaptadorOpcionDia = ArrayAdapter.createFromResource(this, R.array.dia,
                android.R.layout.simple_spinner_item);
        campoOpcionDia.setAdapter(adaptadorOpcionDia);

        ArrayAdapter<CharSequence> adaptadorOpcionHora = ArrayAdapter.createFromResource(this, R.array.hora,
                android.R.layout.simple_spinner_item);
        campoOpcionHora.setAdapter(adaptadorOpcionHora);

        ArrayAdapter<CharSequence> adaptadorOpcionVia = ArrayAdapter.createFromResource(this, R.array.tipo_via,
                android.R.layout.simple_spinner_item);
        campoOpcionVia.setAdapter(adaptadorOpcionVia);

        ArrayAdapter<CharSequence> adaptadorOpcionPorcion = ArrayAdapter.createFromResource(this, R.array.tipo_porcion,
                android.R.layout.simple_spinner_item);
        campoOpcionPorcion.setAdapter(adaptadorOpcionPorcion);

        ArrayAdapter<CharSequence> adaptadorOpcionCantidad = ArrayAdapter.createFromResource(this, R.array.cantidad,
                android.R.layout.simple_spinner_item);
        campoOpcionCantidad.setAdapter(adaptadorOpcionCantidad);
    }
}
