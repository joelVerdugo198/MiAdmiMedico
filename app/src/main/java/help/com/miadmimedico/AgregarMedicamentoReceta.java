package help.com.miadmimedico;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AgregarMedicamentoReceta extends AppCompatActivity {

    ClaseMedicamentoReceta medicamentoReceta = new ClaseMedicamentoReceta();
    ClaseSeguimiento seguimiento = new ClaseSeguimiento();
    ClaseUsuario usuario = new ClaseUsuario();

    FirebaseAuth mAuth;
    DatabaseReference baseDatos;

    Toolbar toolbar;
    EditText campoCedulaMedica, campoNombreMedicamento, campoCantidadPorcion;
    Button btnGuardarReceta;
    Spinner campoTipoReceta, campoOpcionDia, campoOpcionHora, campoOpcionPorcion,
            campoOpcionCantidad, campoOpcionVia, campoContacto1, campoContacto2, campoContacto3;

    List<String> id_contacto = new ArrayList<>();
    HashMap<String, Object> datos_seguimiento = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_medicamento_receta);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();
        baseDatos = FirebaseDatabase.getInstance().getReference();

        usuario.setIdUsuario(mAuth.getCurrentUser().getUid());

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        campoTipoReceta = (Spinner) findViewById(R.id.opcionEditarReceta);
        campoCedulaMedica = (EditText) findViewById(R.id.escribirCedulaEditarReceta);
        campoNombreMedicamento = (EditText) findViewById(R.id.escribirMedicamentoEditarReceta);
        campoOpcionDia = (Spinner) findViewById(R.id.opcionDiaEditarReceta);
        campoOpcionHora = (Spinner) findViewById(R.id.opcionHoraEditarReceta);
        campoOpcionVia = (Spinner) findViewById(R.id.opcionViaEditarReceta);
        campoCantidadPorcion = (EditText) findViewById(R.id.escribirPorcionEditarReceta);
        campoOpcionPorcion = (Spinner) findViewById(R.id.tipoPorcionEditarReceta);
        campoOpcionCantidad = (Spinner) findViewById(R.id.opcionCantidadEditarReceta);
        campoContacto1 = (Spinner) findViewById(R.id.contacto1EditarReceta);
        campoContacto2 = (Spinner) findViewById(R.id.contacto2EditarReceta);
        campoContacto3 = (Spinner) findViewById(R.id.contacto3EditarReceta);
        btnGuardarReceta = (Button) findViewById(R.id.botonGuardarReceta);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Agregar medicamento");

        datosSpinner();
        cargarSpinnerContacto();

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Receta.class));
            }
        });

        btnGuardarReceta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                medicamentoReceta.setTipoReceta(campoTipoReceta.getSelectedItem().toString());
                medicamentoReceta.setCedulaMedica(campoCedulaMedica.getText().toString());
                medicamentoReceta.setNombreMedicamento(campoNombreMedicamento.getText().toString());
                medicamentoReceta.setCantidadPorcion(campoCantidadPorcion.getText().toString());
                medicamentoReceta.setTipoPorcion(campoOpcionPorcion.getSelectedItem().toString());
                medicamentoReceta.setCantidadMedicamento(campoOpcionCantidad.getSelectedItem().toString());
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
                if(!medicamentoReceta.getTipoReceta().equals("") && !medicamentoReceta.getCedulaMedica().isEmpty() &&
                        !medicamentoReceta.getNombreMedicamento().isEmpty() && !medicamentoReceta.getCantidadPorcion().isEmpty() &&
                        !medicamentoReceta.getTipoPorcion().equals("") && !medicamentoReceta.getCantidadMedicamento().equals("") &&
                        !medicamentoReceta.getIntervaloHora().equals("") && !medicamentoReceta.getDias().equals("") &&
                        !medicamentoReceta.getViaAdministracion().equals("")) {

                    if (campoContacto2.getSelectedItemPosition() > 0 && campoContacto3.getSelectedItemPosition() > 0
                    && campoContacto1.getSelectedItemPosition() == 0){
                        Toast.makeText(AgregarMedicamentoReceta.this, "Llenar campo contacto 1", Toast.LENGTH_SHORT).show();
                    }
                    else if (campoContacto1.getSelectedItemPosition() > 0 && campoContacto3.getSelectedItemPosition() > 0
                    && campoContacto2.getSelectedItemPosition() == 0){
                        Toast.makeText(AgregarMedicamentoReceta.this, "Llenar campo contacto 2", Toast.LENGTH_SHORT).show();
                    }
                    else if (campoContacto3.getSelectedItemPosition() > 0 && campoContacto1.getSelectedItemPosition() == 0
                    && campoContacto2.getSelectedItemPosition() == 0){
                        Toast.makeText(AgregarMedicamentoReceta.this, "Llenar campo contacto 1 y 2", Toast.LENGTH_SHORT).show();
                    }
                    else if (campoContacto2.getSelectedItemPosition() > 0 && campoContacto1.getSelectedItemPosition() == 0
                            && campoContacto3.getSelectedItemPosition() == 0){
                        Toast.makeText(AgregarMedicamentoReceta.this, "Llenar campo contacto 1", Toast.LENGTH_SHORT).show();
                    }
                    else if(medicamentoReceta.getCantidadPorcion().length() > 3){
                        Toast.makeText(AgregarMedicamentoReceta.this, "Verifique la cantidad de la porción", Toast.LENGTH_SHORT).show();
                    }

                    else{
                        registrarMedicamento();
                    }
                }
                else{
                    Toast.makeText(AgregarMedicamentoReceta.this, "Llenar los campos vacíos", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void registrarMedicamento(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        int idAlarma = (int) System.currentTimeMillis();

        medicamentoReceta.setIdMedicamento(UUID.randomUUID().toString());
        seguimiento.setIdSeguimiento(UUID.randomUUID().toString());


        Map<String, Object> datos_medicamento = new HashMap<>();

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


        // SE REGISTRA JUNTO CON EL MEDICAMENTO EL SEGUIMIENTO
        seguimiento.setHoraAlarma(simpleDateFormat.format(new Date()));
        seguimiento.setIdAlarma(String.valueOf(idAlarma));
        datos_seguimiento.put("idSeguimiento", seguimiento.getIdSeguimiento());
        datos_seguimiento.put("idMedicamento", medicamentoReceta.getIdMedicamento());
        datos_seguimiento.put("idAlarma", seguimiento.getIdAlarma());
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
        datos_seguimiento.put("alarmaConfirmada", "");
        datos_seguimiento.put("envioSmsContacto", "0");

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

        baseDatos.child("receta").child(usuario.getIdUsuario()).child(medicamentoReceta.getIdMedicamento()).
                setValue(datos_medicamento).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task2) {
                if (task2.isSuccessful()){
                    Toast.makeText(AgregarMedicamentoReceta.this, "Se agregó con éxito", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(AgregarMedicamentoReceta.this, Receta.class));
                    finish();
                }
                else {
                    Toast.makeText(AgregarMedicamentoReceta.this,"Error al crear los datos", Toast.LENGTH_SHORT).show();
                }
            }
        });

        baseDatos.child("seguimiento").child(usuario.getIdUsuario()).child(seguimiento.getIdSeguimiento()).
                setValue(datos_seguimiento).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task2) {
                alarma();
            }
        });

    }

    public void alarma() {
        baseDatos.child("seguimiento").child(usuario.getIdUsuario()).addListenerForSingleValueEvent(new ValueEventListener() {
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
                            seguimiento.setCantidadMedicamento(objSnapshot.child("cantidadMedicamento"). getValue().toString());
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
                            Intent intent = new Intent(AgregarMedicamentoReceta.this, Notificacion.class);
                            intent.putExtra("idSeguimiento", seguimiento.getIdSeguimiento());
                            intent.putExtra("idNotificacion", seguimiento.getIdAlarma());
                            intent.putExtra("datosSeguimento", datos_seguimiento);
                            intent.putExtra("mensaje", "Es hora de tomar tu medicamento");
                            PendingIntent pendingIntent = PendingIntent.getBroadcast(AgregarMedicamentoReceta.this, Integer.parseInt(seguimiento.getIdAlarma()), intent, PendingIntent.FLAG_IMMUTABLE);
                            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                            alarmManager.setRepeating( AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 30 * 1000, pendingIntent);

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
        final List<String> lista_contacto = new ArrayList<>();
        baseDatos.child("contacto").child(usuario.getIdUsuario()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                id_contacto.clear();
                lista_contacto.clear();
                String nombreContacto = "", apellidoContacto = "";
                lista_contacto.add("");
                if(dataSnapshot.exists()) {
                    for (DataSnapshot objSnapshot : dataSnapshot.getChildren()) {
                        id_contacto.add(objSnapshot.child("idContacto").getValue().toString());
                        nombreContacto = (objSnapshot.child("nombre").getValue().toString());
                        apellidoContacto = (objSnapshot.child("apellido").getValue().toString());
                        lista_contacto.add(nombreContacto + " " + apellidoContacto);
                    }
                }
                ArrayAdapter<String> contacto = new ArrayAdapter<>(AgregarMedicamentoReceta.this,android.R.layout.simple_spinner_item,lista_contacto);
                campoContacto1.setAdapter(contacto);
                campoContacto2.setAdapter(contacto);
                campoContacto3.setAdapter(contacto);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void datosSpinner(){
        ArrayAdapter<CharSequence> adaptadorTipoReceta = ArrayAdapter.createFromResource(this,R.array.tipo_receta,
                android.R.layout.simple_spinner_item);
        campoTipoReceta.setAdapter(adaptadorTipoReceta);

        ArrayAdapter<CharSequence> adaptadorOpcionDia = ArrayAdapter.createFromResource(this,R.array.dia,
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
        campoOpcionCantidad.setAdapter(adaptadorOpcionCantidad);
    }

}
