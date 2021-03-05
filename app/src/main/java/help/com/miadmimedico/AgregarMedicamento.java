package help.com.miadmimedico;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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
import java.util.UUID;

public class AgregarMedicamento extends AppCompatActivity {

    ClaseMedicamento medicamento = new ClaseMedicamento();
    ClaseSeguimiento seguimiento = new ClaseSeguimiento();
    ClaseUsuario usuario = new ClaseUsuario();

    FirebaseAuth mAuth;
    DatabaseReference baseDatos;

    EditText campoNombreMedicamento, campoCantidadPorcion;
    Spinner campoOpcionDia, campoOpcionHora, campoOpcionPorcion,
            campoOpcionCantidad, campoOpcionVia;

    HashMap<String, Object> datos_seguimiento = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_medicamento);

        Toolbar toolbar;
        Button btnGuardarMedicamento;

        mAuth = FirebaseAuth.getInstance();
        baseDatos = FirebaseDatabase.getInstance().getReference();
        usuario.setIdUsuario(mAuth.getCurrentUser().getUid());

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        campoNombreMedicamento = (EditText) findViewById(R.id.escribirMedicamentoAgregarMedicamento);
        campoCantidadPorcion = (EditText) findViewById(R.id.escribirPorcionAgregarMedicamento);
        campoOpcionDia = (Spinner) findViewById(R.id.opcionDiaAgregarMedicamento);
        campoOpcionHora = (Spinner) findViewById(R.id.opcionHoraAgregarMedicamento);
        campoOpcionVia = (Spinner) findViewById(R.id.opcionViaAgregarMedicamento);
        campoOpcionPorcion = (Spinner) findViewById(R.id.tipoPorcionAgregarMedicamento);
        campoOpcionCantidad = (Spinner) findViewById(R.id.opcionCantidadAgregarMedicamento);
        btnGuardarMedicamento = (Button) findViewById(R.id.botonAgregarMedicamento);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Agregar medicamento ");

        datosSpinner();

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Medicamento.class));
                finish();
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
                        Toast.makeText(AgregarMedicamento.this, "Verifique la cantidad de la porción", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        registrarMedicamento();
                    }
                }else{
                    Toast.makeText(AgregarMedicamento .this, "Llenar campos vacíos", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void registrarMedicamento(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        int idAlarma = (int) System.currentTimeMillis();

        medicamento.setIdMedicamento(UUID.randomUUID().toString());
        seguimiento.setIdSeguimiento(UUID.randomUUID().toString());

        Map<String, Object> datos_medicamento = new HashMap<>();

        datos_medicamento.put("idMedicamento", medicamento.getIdMedicamento());
        datos_medicamento.put("medicamento", medicamento.getNombreMedicamento());
        datos_medicamento.put("cantidadPorcion", medicamento.getCantidadPorcion());
        datos_medicamento.put("tipoPorcion", medicamento.getTipoPorcion());
        datos_medicamento.put("cantidadMedicamento", medicamento.getCantidadMedicamento());
        datos_medicamento.put("intervaloHora", medicamento.getIntervaloHora());
        datos_medicamento.put("dias", medicamento.getDias());
        datos_medicamento.put("viaAdministracion", medicamento.getViaAdministracion());

        seguimiento.setHoraAlarma(simpleDateFormat.format(new Date()));
        seguimiento.setIdAlarma(String.valueOf(idAlarma));
        datos_seguimiento.put("idSeguimiento", seguimiento.getIdSeguimiento());
        datos_seguimiento.put("idMedicamento", medicamento.getIdMedicamento());
        datos_seguimiento.put("idAlarma", seguimiento.getIdAlarma());
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
        else if (Integer.parseInt(seguimiento.getHoraAlarma().substring(3, 5)) + Integer.parseInt(medicamento.getIntervaloHora())  <= 9){
            datos_seguimiento.put("horaAlarma", seguimiento.getHoraAlarma().substring(0, 3) + "0" +
                    (Integer.parseInt(seguimiento.getHoraAlarma().substring(3, 5)) + Integer.parseInt(medicamento.getIntervaloHora())));
        }
        else {
            datos_seguimiento.put("horaAlarma", seguimiento.getHoraAlarma().substring(0, 3) +
                    (Integer.parseInt(seguimiento.getHoraAlarma().substring(3, 5)) + Integer.parseInt(medicamento.getIntervaloHora())));
        }

        baseDatos.child("medicamento").child(usuario.getIdUsuario()).child(medicamento.getIdMedicamento()).setValue(datos_medicamento).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task2) {
                if (task2.isSuccessful()){
                    Toast.makeText(AgregarMedicamento.this, "Se agregó con éxito", Toast.LENGTH_SHORT).show();

                    startActivity(new Intent(AgregarMedicamento.this, Medicamento.class));
                    finish();
                }
                else {
                    Toast.makeText(AgregarMedicamento.this,"Error al crear los datos", Toast.LENGTH_SHORT).show();
                }
            }
        });

        baseDatos.child("seguimiento").child(usuario.getIdUsuario()).child(seguimiento.getIdSeguimiento()).setValue(datos_seguimiento).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task2) {
                if (task2.isSuccessful()){
                    alarma();
                }

            }
        });

    }

    public void alarma() {
        baseDatos.child("seguimiento").child(usuario.getIdUsuario()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot objSnapshot : dataSnapshot.getChildren()) {
                        if (objSnapshot.child("alarmaConfirmada").getValue().toString().equals("")) {
                            seguimiento.setIdSeguimiento(objSnapshot.child("idSeguimiento").getValue().toString());
                            seguimiento.setIdMedicamento(objSnapshot.child("idMedicamento").getValue().toString());
                            seguimiento.setIdAlarma(objSnapshot.child("idAlarma").getValue().toString());
                            seguimiento.setTipo(objSnapshot.child("tipo").getValue().toString());
                            seguimiento.setNombreMedicamento(objSnapshot.child("medicamento").getValue().toString());
                            seguimiento.setCantidadPorcion(objSnapshot.child("cantidadPorcion").getValue().toString());
                            seguimiento.setTipoPorcion(objSnapshot.child("tipoPorcion").getValue().toString());
                            seguimiento.setCantidadMedicamento(objSnapshot.child("cantidadMedicamento").getValue().toString());
                            seguimiento.setViaAdministracion(objSnapshot.child("viaAdministracion").getValue().toString());
                            seguimiento.setIntervaloHora(objSnapshot.child("intervaloHora").getValue().toString());
                            seguimiento.setCantidadTotal(objSnapshot.child("cantidadTotal").getValue().toString());
                            seguimiento.setCantidadTomada(objSnapshot.child("cantidadTomada").getValue().toString());
                            seguimiento.setHoraAlarma(objSnapshot.child("horaAlarma").getValue().toString());

                            datos_seguimiento.put("idSeguimiento", seguimiento.getIdSeguimiento());
                            datos_seguimiento.put("idMedicamento", seguimiento.getIdMedicamento());
                            datos_seguimiento.put("idAlarma", seguimiento.getIdAlarma());
                            datos_seguimiento.put("tipo", seguimiento.getTipo());
                            datos_seguimiento.put("medicamento", seguimiento.getNombreMedicamento());
                            datos_seguimiento.put("cantidadPorcion",seguimiento.getCantidadPorcion());
                            datos_seguimiento.put("tipoPorcion",seguimiento.getTipoPorcion());
                            datos_seguimiento.put("cantidadMedicamento",seguimiento.getCantidadMedicamento());
                            datos_seguimiento.put("viaAdministracion",seguimiento.getViaAdministracion());
                            datos_seguimiento.put("intervaloHora", seguimiento.getIntervaloHora());
                            datos_seguimiento.put("cantidadTotal", seguimiento.getCantidadTotal());
                            datos_seguimiento.put("cantidadTomada", seguimiento.getCantidadTomada());
                            datos_seguimiento.put("alarmaConfirmada", "no confirmado");

                            // SE CREA LA ALARMA Y SE ENVIA LA INFORMACION A LA CLASE NOTIFICACION
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTimeInMillis(System.currentTimeMillis());
                            calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(seguimiento.getHoraAlarma().substring(0, 2)));
                            calendar.set(Calendar.MINUTE, Integer.parseInt(seguimiento.getHoraAlarma().substring(3, 5)));
                            Intent intent = new Intent(AgregarMedicamento.this, Notificacion.class);
                            intent.putExtra("idSeguimiento", seguimiento.getIdSeguimiento());
                            intent.putExtra("idNotificacion", seguimiento.getIdAlarma());
                            intent.putExtra("datosSeguimento", datos_seguimiento);
                            intent.putExtra("mensaje", "Es hora de tomar tu medicamento");
                            PendingIntent pendingIntent = PendingIntent.getBroadcast(AgregarMedicamento.this, Integer.parseInt(seguimiento.getIdAlarma()),
                                    intent, PendingIntent.FLAG_ONE_SHOT);
                            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                            alarmManager.set( AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(), pendingIntent);

                        }


                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void datosSpinner() {

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
