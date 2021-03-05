package help.com.miadmimedico;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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
import java.util.Date;
import java.util.HashMap;

import static android.content.Context.ALARM_SERVICE;
import static java.nio.file.Paths.get;

public class AdaptadorPrincipal extends BaseAdapter {

    private static LayoutInflater inflater = null;

    ClaseSeguimiento seguimiento = new ClaseSeguimiento();
    ClaseUsuario usuario = new ClaseUsuario();

    Context contexto;
    ArrayList<String> dato_alarma;
    ArrayList<String> hora_alarma;
    ArrayList<String> id_seguimiento;

    public  AdaptadorPrincipal (Context contexto, ArrayList<String> dato_alarma,
                                ArrayList<String> hora_alarma, ArrayList<String> id_seguimiento)
    {
        this.contexto = contexto;
        this.dato_alarma= dato_alarma;
        this.hora_alarma = hora_alarma;
        this.id_seguimiento= id_seguimiento;

        inflater = (LayoutInflater) contexto.getSystemService(contexto.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final View listaAlarma = inflater.inflate(R.layout.elemento_lista_alarma,null);

        FirebaseAuth mAuth;
        final DatabaseReference baseDatos;
        mAuth = FirebaseAuth.getInstance();
        final MediaPlayer tono;
        tono = MediaPlayer.create(contexto, R.raw.tono);
        baseDatos = FirebaseDatabase.getInstance().getReference();
        usuario.setIdUsuario(mAuth.getCurrentUser().getUid());

        final int idAlarma = (int) System.currentTimeMillis();
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        final HashMap<String, Object> datos_seguimiento = new HashMap<>();

        TextView datosMedicamentoAlarma = (TextView) listaAlarma.findViewById(R.id.datosMedicamentoAlarmaConfirmar);
        TextView horaMedicamentoAlarma = (TextView) listaAlarma.findViewById(R.id.horaaMedicamentoAlarmaConfirmar);
        ImageView btnConfirmarAlarma = (ImageView) listaAlarma.findViewById(R.id.botonConfirmarAlarma);

        datosMedicamentoAlarma.setText(dato_alarma.get(position));
        horaMedicamentoAlarma.setText(hora_alarma.get(position));
        btnConfirmarAlarma.setTag(position);

        btnConfirmarAlarma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seguimiento.setHoraAlarma(simpleDateFormat.format(new Date()));
                tono.pause();
                baseDatos.child("seguimiento").child(usuario.getIdUsuario()).child(id_seguimiento.get(position)).addListenerForSingleValueEvent( new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.exists()) {
                            seguimiento.setIdSeguimiento(dataSnapshot.child("idSeguimiento").getValue().toString());
                            seguimiento.setIdMedicamento(dataSnapshot.child("idMedicamento").getValue().toString());
                            seguimiento.setIdAlarma(dataSnapshot.child("idAlarma").getValue().toString());
                            seguimiento.setTipo(dataSnapshot.child("tipo").getValue().toString());
                            seguimiento.setNombreMedicamento(dataSnapshot.child("medicamento").getValue().toString());
                            seguimiento.setCantidadPorcion(dataSnapshot.child("cantidadPorcion").getValue().toString());
                            seguimiento.setTipoPorcion(dataSnapshot.child("tipoPorcion").getValue().toString());
                            seguimiento.setCantidadMedicamento(dataSnapshot.child("cantidadMedicamento").getValue().toString());
                            seguimiento.setViaAdministracion(dataSnapshot.child("viaAdministracion").getValue().toString());
                            seguimiento.setIntervaloHora(dataSnapshot.child("intervaloHora").getValue().toString());
                            seguimiento.setCantidadTotal(dataSnapshot.child("cantidadTotal").getValue().toString());
                            seguimiento.setCantidadTomada(String.valueOf(Integer.parseInt(dataSnapshot.child("cantidadTomada").getValue().toString()) + 1));
                            seguimiento.setHoraAlarma(simpleDateFormat.format(new Date()));

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
                            datos_seguimiento.put("alarmaConfirmada", "confirmada");

                            // POSIBLES CASOS DONDE PARA IGUALAR A LOS DIGITOS DE LA HORA
                            if (Integer.parseInt(seguimiento.getHoraAlarma().substring(3, 5)) +
                                    Integer.parseInt(seguimiento.getIntervaloHora()) >= 70){
                                if (Integer.parseInt(seguimiento.getHoraAlarma().substring(0, 2)) < 9){
                                    datos_seguimiento.put("horaAlarma", "0" + (Integer.parseInt(seguimiento.getHoraAlarma().substring(0, 2)) + 1)
                                            + seguimiento.getHoraAlarma().substring(2, 3) + (Integer.parseInt(seguimiento.getHoraAlarma().substring(3, 5))
                                            + Integer.parseInt(seguimiento.getIntervaloHora()) - 60));
                                }
                                else{
                                    datos_seguimiento.put("horaAlarma", (Integer.parseInt(seguimiento.getHoraAlarma().substring(0, 2)) + 1)
                                            + seguimiento.getHoraAlarma().substring(2, 3) + (Integer.parseInt(seguimiento.getHoraAlarma().substring(3, 5))
                                            + Integer.parseInt(seguimiento.getIntervaloHora()) - 60));
                                }
                            }
                            else  if (Integer.parseInt(seguimiento.getHoraAlarma().substring(3, 5)) +
                                    Integer.parseInt(seguimiento.getIntervaloHora()) >= 60 ){
                                if (Integer.parseInt(seguimiento.getHoraAlarma().substring(0, 2)) < 9){
                                    datos_seguimiento.put("horaAlarma", "0" + (Integer.parseInt(seguimiento.getHoraAlarma().substring(0, 2)) + 1)
                                            + seguimiento.getHoraAlarma().substring(2, 3) + "0" + (Integer.parseInt(seguimiento.getHoraAlarma().substring(3, 5))
                                            + Integer.parseInt(seguimiento.getIntervaloHora()) - 60));
                                }
                                else{
                                    datos_seguimiento.put("horaAlarma", (Integer.parseInt(seguimiento.getHoraAlarma().substring(0, 2)) + 1)
                                            + seguimiento.getHoraAlarma().substring(2, 3) + "0" + (Integer.parseInt(seguimiento.getHoraAlarma().substring(3, 5))
                                            + Integer.parseInt(seguimiento.getIntervaloHora()) - 60));
                                }
                            }
                            else if (Integer.parseInt(seguimiento.getHoraAlarma().substring(3, 5)) + Integer.parseInt(seguimiento.getIntervaloHora())  <= 9){
                                datos_seguimiento.put("horaAlarma", seguimiento.getHoraAlarma().substring(0, 3) + "0" +
                                        (Integer.parseInt(seguimiento.getHoraAlarma().substring(3, 5)) + Integer.parseInt(seguimiento.getIntervaloHora())));
                            }
                            else {
                                datos_seguimiento.put("horaAlarma", seguimiento.getHoraAlarma().substring(0, 3) +
                                        (Integer.parseInt(seguimiento.getHoraAlarma().substring(3, 5)) + Integer.parseInt(seguimiento.getIntervaloHora())));
                            }

                            if (!seguimiento.getTipo().equals("Medicamento")) {
                                //SE REINICIA EL CONTADOR DE SMS (SOLO APLICA ESTE DATO PARA MEDICAMENTOS DE RECETA)
                                datos_seguimiento.put("envioSmsContacto", "0");

                                //SE ELIMINA LA ALARMA QUE SE CONFIRMO PARA EVITAR QUE SIGA TRABAJANDO
                                Intent intent = new Intent(contexto, Notificacion.class);
                                PendingIntent pendingIntent = PendingIntent.getBroadcast(contexto, Integer.parseInt(seguimiento.getIdAlarma()), intent, PendingIntent.FLAG_IMMUTABLE);
                                AlarmManager alarmManager = (AlarmManager) contexto.getSystemService(ALARM_SERVICE);
                                alarmManager.cancel(pendingIntent);

                                //SE ACTUALIZAN LOS DATOS DE LA BASE DE DATOS
                                baseDatos.child("seguimiento").child(usuario.getIdUsuario()).child(id_seguimiento.get(position)).setValue(datos_seguimiento).
                                        addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    //SE CREA LA NUEVA ALARMA CON LOS DATOS ACTUALIZADOS Y SE ENVIAN LOS DATOS A LA CLASE NOTIFICACION
                                                    datos_seguimiento.put("idAlarma", idAlarma);
                                                    datos_seguimiento.put("alarmaConfirmada", "no confirmado");
                                                    Intent intent = new Intent(contexto, Notificacion.class);
                                                    intent.putExtra("idSeguimiento", seguimiento.getIdSeguimiento());
                                                    intent.putExtra("idNotificacion", seguimiento.getIdAlarma());
                                                    intent.putExtra("datosSeguimento", datos_seguimiento);
                                                    intent.putExtra("mensaje", "Es hora de tomar tu medicamento");
                                                    PendingIntent pendingIntent = PendingIntent.getBroadcast(contexto, idAlarma, intent, PendingIntent.FLAG_IMMUTABLE);
                                                    AlarmManager alarmManager = (AlarmManager) contexto.getSystemService(ALARM_SERVICE);
                                                    alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() +
                                                            Integer.parseInt(seguimiento.getIntervaloHora()) * 60 * 1000, 30 * 1000, pendingIntent);
                                                }
                                            }
                                        });

                            }
                            else if (seguimiento.getTipo().equals("Medicamento")){
                                //SE ACTUALIZAN LOS DATOS DE LA BASE DE DATOS
                                baseDatos.child("seguimiento").child(usuario.getIdUsuario()).child(id_seguimiento.get(position)).setValue(datos_seguimiento).
                                        addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                            }
                                        });

                                //SE CREA UNA NUEVA ALARMA Y SE ENVÍA LA INFORMARMACION A LA CLASE DE NOTIFICACION
                                datos_seguimiento.put("idAlarma", idAlarma);
                                datos_seguimiento.put("alarmaConfirmada", "no confirmado");
                                Intent intent = new Intent(contexto, Notificacion.class);
                                intent.putExtra("idSeguimiento", seguimiento.getIdSeguimiento());
                                intent.putExtra("idNotificacion", seguimiento.getIdAlarma());
                                intent.putExtra("datosSeguimento", datos_seguimiento);
                                intent.putExtra("mensaje", "Es hora de tomar tu medicamento");
                                PendingIntent pendingIntent = PendingIntent.getBroadcast(contexto, Integer.parseInt(seguimiento.getIdAlarma()), intent, PendingIntent.FLAG_ONE_SHOT);
                                AlarmManager alarmManager = (AlarmManager) contexto.getSystemService(ALARM_SERVICE);
                                alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() +
                                        Integer.parseInt(seguimiento.getIntervaloHora()) * 60 * 1000, pendingIntent);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });


        return listaAlarma;
    }
    @Override
    public int getCount() {
        return dato_alarma.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

}
