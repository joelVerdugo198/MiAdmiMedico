package help.com.miadmimedico;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.Map;


public class Notificacion extends BroadcastReceiver {

    ClaseSeguimiento seguimiento = new ClaseSeguimiento();
    ClaseMedicamentoReceta medicamentoReceta = new ClaseMedicamentoReceta();
    ClaseContacto contacto = new ClaseContacto();
    ClaseUsuario usuario = new ClaseUsuario();
    Mensaje mensaje = new Mensaje();

    FirebaseAuth mAuth;
    DatabaseReference baseDatos;

    MediaPlayer tono;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onReceive(final Context context, Intent intent) {

        tono = MediaPlayer.create(context, R.raw.tono);
        final String idSeguimiento = intent.getStringExtra("idSeguimiento");
        final String idNotificacion = intent.getStringExtra("idNotificacion");
        final String mensajeSms = intent.getStringExtra("mensaje");
        final Map<String, Object> datosSeguimiento = (Map<String, Object>) intent.getSerializableExtra("datosSeguimento");

        mAuth = FirebaseAuth.getInstance();
        baseDatos = FirebaseDatabase.getInstance().getReference();


        Intent intent2 = new Intent(context, Principal.class);
        final PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent2, 0);
        final NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        final Notification.Builder builder = new Notification.Builder(context);


        if (idNotificacion!=null) {
            usuario.setIdUsuario(mAuth.getCurrentUser().getUid());
            baseDatos.child("seguimiento").child(usuario.getIdUsuario()).child(idSeguimiento).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        seguimiento.setTipo(dataSnapshot.child("tipo").getValue().toString());
                        if (dataSnapshot.child("alarmaConfirmada").getValue().toString().equals("no confirmado")
                                && !seguimiento.getTipo().equals("Medicamento")
                                && Integer.parseInt(dataSnapshot.child("envioSmsContacto").getValue().toString()) < 3) {
                            seguimiento.setIdMedicamento(dataSnapshot.child("idMedicamento").getValue().toString());
                            seguimiento.setEnvioSmsContacto(dataSnapshot.child("envioSmsContacto").getValue().toString());

                            baseDatos.child("receta").child(usuario.getIdUsuario()).child(seguimiento.getIdMedicamento()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        if (!dataSnapshot.child("contacto1").getValue().toString().equals("")) {
                                            medicamentoReceta.setContacto1(dataSnapshot.child("contacto1").getValue().toString());
                                            medicamentoReceta.setContacto2(dataSnapshot.child("contacto2").getValue().toString());
                                            medicamentoReceta.setContacto3(dataSnapshot.child("contacto3").getValue().toString());
                                            if (medicamentoReceta.getContacto2().isEmpty() && medicamentoReceta.getContacto3().isEmpty()) {
                                                if (seguimiento.getEnvioSmsContacto().equals("0")) {
                                                    mensaje.enviarMensajeContacto(dataSnapshot.child("contacto1").getValue().toString());
                                                    contacto.setIdContacto(dataSnapshot.child("contacto1").getValue().toString());
                                                    datosSeguimiento.put("envioSmsContacto", "1");
                                                } else if (seguimiento.getEnvioSmsContacto().equals("1")) {
                                                    mensaje.enviarMensajeContacto(dataSnapshot.child("contacto1").getValue().toString());
                                                    datosSeguimiento.put("envioSmsContacto", "2");
                                                    contacto.setIdContacto(dataSnapshot.child("contacto1").getValue().toString());

                                                } else if (seguimiento.getEnvioSmsContacto().equals("2")) {
                                                    mensaje.enviarMensajeContacto(dataSnapshot.child("contacto1").getValue().toString());
                                                    contacto.setIdContacto(dataSnapshot.child("contacto1").getValue().toString());
                                                    datosSeguimiento.put("envioSmsContacto", "3");
                                                }
                                            } else if (medicamentoReceta.getContacto3().isEmpty()) {
                                                if (seguimiento.getEnvioSmsContacto().equals("0")) {
                                                    mensaje.enviarMensajeContacto(dataSnapshot.child("contacto1").getValue().toString());
                                                    contacto.setIdContacto(dataSnapshot.child("contacto1").getValue().toString());
                                                    datosSeguimiento.put("envioSmsContacto", "1");
                                                } else if (seguimiento.getEnvioSmsContacto().equals("1")) {
                                                    mensaje.enviarMensajeContacto(dataSnapshot.child("contacto2").getValue().toString());
                                                    contacto.setIdContacto(dataSnapshot.child("contacto2").getValue().toString());
                                                    datosSeguimiento.put("envioSmsContacto", "2");

                                                } else if (seguimiento.getEnvioSmsContacto().equals("2")) {
                                                    mensaje.enviarMensajeContacto(dataSnapshot.child("contacto1").getValue().toString());
                                                    contacto.setIdContacto(dataSnapshot.child("contacto1").getValue().toString());
                                                    datosSeguimiento.put("envioSmsContacto", "3");
                                                }
                                            } else {
                                                if (seguimiento.getEnvioSmsContacto().equals("0")) {
                                                    mensaje.enviarMensajeContacto(dataSnapshot.child("contacto1").getValue().toString());
                                                    contacto.setIdContacto(dataSnapshot.child("contacto1").getValue().toString());
                                                    datosSeguimiento.put("envioSmsContacto", "1");
                                                } else if (seguimiento.getEnvioSmsContacto().equals("1")) {
                                                    mensaje.enviarMensajeContacto(dataSnapshot.child("contacto2").getValue().toString());
                                                    contacto.setIdContacto(dataSnapshot.child("contacto2").getValue().toString());
                                                    datosSeguimiento.put("envioSmsContacto", "2");

                                                } else if (seguimiento.getEnvioSmsContacto().equals("2")) {
                                                    mensaje.enviarMensajeContacto(dataSnapshot.child("contacto3").getValue().toString());
                                                    contacto.setIdContacto(dataSnapshot.child("contacto3").getValue().toString());
                                                    datosSeguimiento.put("envioSmsContacto", "3");
                                                }


                                            }
                                            baseDatos.child("contacto").child(usuario.getIdUsuario()).child(contacto.getIdContacto()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    if (dataSnapshot.exists()) {
                                                        contacto.setNombre(dataSnapshot.child("nombre").getValue().toString());
                                                        contacto.setApellido(dataSnapshot.child("apellido").getValue().toString());
                                                        final Notification.Builder builder = new Notification.Builder(context);
                                                        builder.setSmallIcon(R.drawable.iconomedicamento)
                                                                .setContentTitle("Alarma no confirmada")
                                                                .setContentText("Se le ha enviado un sms a " + contacto.getNombre() + " " + contacto.getApellido())
                                                                .setWhen(System.currentTimeMillis())
                                                                .setAutoCancel(true)
                                                                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                                                                .setContentIntent(pendingIntent);
                                                        builder.setSound(Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.tono));
                                                        //tono.start();
                                                        notificationManager.notify(Integer.parseInt(idNotificacion), builder.build());
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });


                                        } else {

                                            datosSeguimiento.put("envioSmsContacto", Integer.parseInt(seguimiento.getEnvioSmsContacto()) + 1);
                                            final Notification.Builder builder = new Notification.Builder(context);
                                            builder.setSmallIcon(R.drawable.iconomedicamento)
                                                    .setContentTitle("Alarma no confirmada")
                                                    .setContentText("No te haz tomado tu medicamento")
                                                    .setWhen(System.currentTimeMillis())
                                                    .setAutoCancel(true)
                                                    .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                                                    .setContentIntent(pendingIntent);
                                            builder.setSound(Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.tono));
                                            //tono.start();
                                            notificationManager.notify(Integer.parseInt(idNotificacion), builder.build());
                                        }

                                        datosSeguimiento.put("alarmaConfirmada", "no confirmado");

                                        baseDatos.child("seguimiento").child(usuario.getIdUsuario()).child(idSeguimiento).setValue(datosSeguimiento).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                            }
                                        });


                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });


                        } else {
                            datosSeguimiento.put("alarmaConfirmada", "no confirmado");

                            baseDatos.child("seguimiento").child(usuario.getIdUsuario()).child(idSeguimiento).setValue(datosSeguimiento).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                }
                            });

                            final Notification.Builder builder = new Notification.Builder(context);
                            builder.setSmallIcon(R.drawable.iconomedicamento)
                                    .setContentTitle("Alarma")
                                    .setContentText(mensajeSms)
                                    .setWhen(System.currentTimeMillis())
                                    .setAutoCancel(true)
                                    .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                                    .setContentIntent(pendingIntent);

                            builder.setSound(Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.tono));
                            // tono.start();
                            notificationManager.notify(Integer.parseInt(idNotificacion), builder.build());


                        }

                        /*if (dataSnapshot.child("alarmaConfirmada").getValue().toString().equals("confirmada")) {
                            tono.pause();
                        }*/

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }


    }

}
