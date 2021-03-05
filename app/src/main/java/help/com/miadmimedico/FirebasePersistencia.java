package help.com.miadmimedico;

import com.google.firebase.database.FirebaseDatabase;

public class FirebasePersistencia extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //DETECTA LOS CAMBIOS HECHOS SIN INTERNET Y LOS AGREGA A LA BASE DE DATOS CUANDO HAY INTERNET
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
