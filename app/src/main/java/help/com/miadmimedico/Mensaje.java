package help.com.miadmimedico;
import android.support.annotation.NonNull;
import android.telephony.SmsManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Mensaje {

    ClaseContacto contacto = new ClaseContacto();
    ClaseUsuario usuario = new ClaseUsuario();

    FirebaseAuth mAuth;
    DatabaseReference baseDatos;

    public Mensaje (){

    }

    public void enviarMensajeContacto (final String idContacto){

        mAuth = FirebaseAuth.getInstance();
        baseDatos = FirebaseDatabase.getInstance().getReference();

        usuario.setIdUsuario(mAuth.getCurrentUser().getUid());


        baseDatos.child("contacto").child(usuario.getIdUsuario()).child(idContacto).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                     contacto.setTelefono(dataSnapshot.child("telefono").getValue().toString());
                    baseDatos.child("usuario").child(usuario.getIdUsuario()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()){
                                usuario.setNombre(dataSnapshot.child("nombre").getValue().toString());
                                try {
                                    SmsManager sms = SmsManager.getDefault();
                                    sms.sendTextMessage(contacto.getTelefono(), null,
                                            "MiAdmiMedico. " + usuario.getNombre()  + " no se ha tomado su medicamento",
                                            null, null);

                                }catch (Exception e){

                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

}
