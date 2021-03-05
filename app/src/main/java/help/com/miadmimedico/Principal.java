package help.com.miadmimedico;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class Principal extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    ClaseSeguimiento seguimiento = new ClaseSeguimiento();
    ClaseUsuario usuario = new ClaseUsuario();

    FirebaseAuth mAuth;
    DatabaseReference baseDatos;

    ListView listaAlarma, listaAlarmaFutura;
    TextView actividadAlarma, alarmaConfirmarVacio, alarmaFuturaVacio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        mAuth = FirebaseAuth.getInstance();
        baseDatos = FirebaseDatabase.getInstance().getReference();

        usuario.setIdUsuario(mAuth.getCurrentUser().getUid());

        listaAlarma = (ListView) findViewById(R.id.listaAlarma);
        alarmaConfirmarVacio = (TextView) findViewById(R.id.textoAlarmaVacio);
        actividadAlarma = (TextView) findViewById(R.id.actividadAlarma);
        alarmaFuturaVacio = (TextView) findViewById(R.id.textoAlarmaFuturaVacio);
        listaAlarmaFutura = (ListView) findViewById(R.id.listaFuturaAlarma);

        mostrarAvisoNoAlarma();

        permisoSms();

        mostrarAlarma();

        estadoSeguimiento();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.principal, menu);
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.perfilMenu) {
            Intent perfil = new Intent(Principal.this, Usuario.class);
            startActivity(perfil);
            finish();
        } else if (id == R.id.contactoMenu) {
            Intent contacto = new Intent(Principal.this, Contacto.class);
            startActivity(contacto);
            finish();
        } else if (id == R.id.recetaMenu) {
            Intent receta = new Intent(Principal.this, Receta.class);
            startActivity(receta);
            finish();
        } else if (id == R.id.medicamentoMenu) {
            Intent medicamento = new Intent(Principal.this, Medicamento.class);
            startActivity(medicamento);
            finish();
        } else if (id == R.id.seguimientoMenu) {
            Intent seguimiento = new Intent(Principal.this, Seguimiento.class);
            startActivity(seguimiento);
            finish();
        } else if (id == R.id.cerrarSesionMenu) {
            Intent cerrar = new Intent(Principal.this, MainActivity.class);
            startActivity(cerrar);
            finish();
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void mostrarAvisoNoAlarma(){

        final ArrayList<String> alarmaVacia = new ArrayList<>();
        final ArrayList<String> alarmaFuturaVacia = new ArrayList<>();

        baseDatos.child("seguimiento").child(usuario.getIdUsuario()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    actividadAlarma.setText("");
                    alarmaFuturaVacia.clear();
                    alarmaVacia.clear();
                    for (DataSnapshot objSnapshot : dataSnapshot.getChildren()){
                        if (objSnapshot.child("alarmaConfirmada").getValue().toString().equals("no confirmado")) {
                            alarmaVacia.add(objSnapshot.child("alarmaConfirmada").getValue().toString());
                        }
                        else if(objSnapshot.child("alarmaConfirmada").getValue().toString().equals("confirmada")
                        || objSnapshot.child("alarmaConfirmada").getValue().toString().equals("")){
                            alarmaFuturaVacia.add(objSnapshot.child("alarmaConfirmada").getValue().toString());
                        }
                    }
                    if (alarmaVacia.size()>0){
                        alarmaConfirmarVacio.setVisibility(View.GONE);
                    }
                    else if (alarmaVacia.size()<=0){
                        alarmaConfirmarVacio.setVisibility(View.VISIBLE);
                    }

                    if (alarmaFuturaVacia.size()>0){
                        alarmaFuturaVacio.setVisibility(View.GONE);
                    }
                    else if (alarmaFuturaVacia.size()<=0){
                        alarmaFuturaVacio.setVisibility(View.VISIBLE);
                    }

                }
                else{
                    actividadAlarma.setText("No hay alarmas");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void mostrarAlarma(){
        final ArrayList<String> datos_alarma_futura = new ArrayList<>();
        final ArrayList<String> datos_alarma = new ArrayList<>();
        final ArrayList<String> hora_alarma_futura = new ArrayList<>();
        final ArrayList<String> hora_alarma = new ArrayList<>();
        final ArrayList<String> id_seguimiento = new ArrayList<>();

        baseDatos.child("seguimiento").child(usuario.getIdUsuario()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                datos_alarma_futura.clear();
                hora_alarma.clear();
                hora_alarma_futura.clear();
                datos_alarma.clear();
                id_seguimiento.clear();
                if (dataSnapshot.exists()){
                    for (DataSnapshot objSnapshot : dataSnapshot.getChildren()){
                        seguimiento.setNombreMedicamento(objSnapshot.child("medicamento").getValue().toString());
                        seguimiento.setCantidadPorcion(objSnapshot.child("cantidadPorcion").getValue().toString());
                        seguimiento.setTipoPorcion(objSnapshot.child("tipoPorcion").getValue().toString());
                        seguimiento.setCantidadMedicamento(objSnapshot.child("cantidadMedicamento").getValue().toString());
                        seguimiento.setViaAdministracion(objSnapshot.child("viaAdministracion").getValue().toString());
                        seguimiento.setTipo(objSnapshot.child("tipo").getValue().toString());

                        //MUESTRA LAS ALARMAS A CONFIRMAR
                        if (objSnapshot.child("alarmaConfirmada").getValue().toString().equals("no confirmado")){
                            id_seguimiento.add(objSnapshot.child("idSeguimiento").getValue().toString());
                            hora_alarma.add(objSnapshot.child("horaAlarma").getValue().toString());
                            datos_alarma.add("Tipo: " + seguimiento.getTipo()  + "\n" +
                                    "Medicamento: " + seguimiento.getNombreMedicamento() + "\n" +
                                    "Porción: " + seguimiento.getCantidadPorcion() + seguimiento.getTipoPorcion() + "\n" +
                                    "Cantidad: " + seguimiento.getCantidadMedicamento() +"\n" +
                                    "Vía de administración: " + "\n" + seguimiento.getViaAdministracion());
                        }
                        // MUESTRA LAS ALARMAS FUTURAS A SONAR
                         else{
                           hora_alarma_futura.add(objSnapshot.child("horaAlarma").getValue().toString());
                            datos_alarma_futura.add("Tipo: " + seguimiento.getTipo()  + "\n" +
                                    "Medicamento: " + seguimiento.getNombreMedicamento() + "\n" +
                                    "Porción: " + seguimiento.getCantidadPorcion() + seguimiento.getTipoPorcion() + "\n" +
                                    "Cantidad: " + seguimiento.getCantidadMedicamento() +"\n" +
                                    "Vía de administración: " + "\n" + seguimiento.getViaAdministracion());
                         }
                    }
                    ordenarAlarmasConfirmar(hora_alarma, datos_alarma, id_seguimiento);
                    ordenarAlarmasFuturas( hora_alarma_futura, datos_alarma_futura);
                    listaAlarma.setAdapter(new AdaptadorPrincipal(Principal.this, datos_alarma, hora_alarma, id_seguimiento));
                    listaAlarmaFutura.setAdapter(new AdaptadorPrincipalFuturo(Principal.this, datos_alarma_futura, hora_alarma_futura));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void ordenarAlarmasFuturas(final ArrayList<String> hora_alarma_futura, final ArrayList<String> datos_alarma_futura ){

        String auxHora = "", auxDato = "";

        if (hora_alarma_futura.size() > 0) {
            for (int i = 0; i < hora_alarma_futura.size(); i++) {
                for (int j = 0; j < hora_alarma_futura.size() - i - 1; j++){
                    if ((Integer.parseInt(hora_alarma_futura.get(j).substring(0, 2)) >  Integer.parseInt(hora_alarma_futura.get(j + 1).substring(0, 2)))
                    || (Integer.parseInt(hora_alarma_futura.get(j).substring(0, 2)) ==  Integer.parseInt(hora_alarma_futura.get(j + 1).substring(0, 2))
                            && Integer.parseInt(hora_alarma_futura.get(j).substring(3, 5)) >  Integer.parseInt(hora_alarma_futura.get(j + 1).substring(3, 5)))){
                        auxHora = hora_alarma_futura.get(j + 1);
                        auxDato = datos_alarma_futura. get(j + 1);
                        hora_alarma_futura.set(j + 1, hora_alarma_futura.get(j));
                        datos_alarma_futura.set(j + 1, datos_alarma_futura.get(j));
                        hora_alarma_futura.set(j, auxHora);
                        datos_alarma_futura.set(j, auxDato);
                    }
                }

            }
        }
    }

    public void  ordenarAlarmasConfirmar(final ArrayList<String> hora_alarma, final ArrayList<String> datos_alarma,  final ArrayList<String> id_seguimiento){
        String auxHora = "", auxDato = "", auxIdSeguimiento = "";

        if (hora_alarma.size() > 0) {
            for (int i = 0; i < hora_alarma.size(); i++) {
                for (int j = 0; j < hora_alarma.size() - i - 1; j++){
                    auxHora = hora_alarma.get(j + 1);
                    auxDato = datos_alarma.get(j + 1);
                    auxIdSeguimiento = id_seguimiento.get(j + 1);
                    hora_alarma.set(j + 1, hora_alarma.get(j));
                    datos_alarma.set(j + 1, datos_alarma.get(j));
                    id_seguimiento.set(j + 1, id_seguimiento.get(j));
                    hora_alarma.set(j, auxHora);
                    datos_alarma.set(j, auxDato);
                    id_seguimiento.set(j, auxIdSeguimiento);
                }
            }
        }
    }


    public void estadoSeguimiento(){
        final HashMap<String, Object> datos_seguimiento = new HashMap<>();

        baseDatos.child("seguimiento").child(usuario.getIdUsuario()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    datos_seguimiento.clear();
                    for (DataSnapshot objSnapshot : dataSnapshot.getChildren()) {
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
                        seguimiento.setAlarmaConfirmda(objSnapshot.child("alarmaConfirmada").getValue().toString());
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
                        datos_seguimiento.put("alarmaConfirmada", seguimiento.getAlarmaConfirmda());
                        datos_seguimiento.put("horaAlarma", seguimiento.getHoraAlarma());

                        //SE ELIMINA EL MEDICAMENTO Y SU SEFUIMIENTO CUANDO LLEGUA AL TOTAL DE APLICACIONES DEL MEDICAMENTO
                        if (objSnapshot.child("alarmaConfirmada").getValue().toString().equals("confirmada") &&
                                Integer.parseInt(objSnapshot.child("cantidadTomada").getValue().toString()) ==
                                        Integer.parseInt(objSnapshot.child("cantidadTotal").getValue().toString())) {
                            if (objSnapshot.child("tipo").getValue().toString().equals("Medicamento")) {
                                 baseDatos.child("medicamento").child(usuario.getIdUsuario()).child(objSnapshot.child("idMedicamento").getValue().toString()).removeValue();
                            } else {
                                 baseDatos.child("receta").child(usuario.getIdUsuario()).child(objSnapshot.child("idMedicamento").getValue().toString()).removeValue();
                            }
                            baseDatos.child("seguimiento").child(usuario.getIdUsuario()).child(objSnapshot.child("idSeguimiento").getValue().toString()).removeValue();
                        }
                        //SE CANCELA LA ALARMA DE UN MEDICAMENTO DE RECETA YA QUE LLEGUA ENVIAR 3 SMS PARA QUE NO ESTE SONANDO CADA 30 SEGUNDOS
                        else if (!objSnapshot.child("tipo").getValue().toString().equals("Medicamento")){
                            if (objSnapshot.child("envioSmsContacto").getValue().toString()!=null) {
                                if (objSnapshot.child("alarmaConfirmada").getValue().toString().equals("no confirmado")
                                        && Integer.parseInt(objSnapshot.child("envioSmsContacto").getValue().toString()) > 2) {
                                    Intent intent2 = new Intent(Principal.this, Notificacion.class);
                                    PendingIntent pendingIntent2 = PendingIntent.getBroadcast(Principal.this, Integer.parseInt(seguimiento.getIdAlarma()), intent2, PendingIntent.FLAG_IMMUTABLE);
                                    AlarmManager alarmManager2 = (AlarmManager) getSystemService(ALARM_SERVICE);
                                    alarmManager2.cancel(pendingIntent2);
                                }
                            }
                        }

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void permisoSms(){
        //SOLICITAR PERMISO PARA MANDAR SMS UNA VEZ QUE INICIE SESIÓN
        if (ActivityCompat.checkSelfPermission(Principal.this,
                Manifest.permission.SEND_SMS)!= PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(Principal.this,Manifest.permission.SEND_SMS)!=PackageManager.PERMISSION_GRANTED)
        {ActivityCompat.requestPermissions(Principal.this,new String[]
                {Manifest.permission.SEND_SMS,}, 1000);
        }else{

        };
    }

}
