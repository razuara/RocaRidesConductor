package com.rocasoftware.rocaridesconductor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference conductorRef = db.collection("Conductores");
    private CollectionReference managerRef = db.collection("Managers");
    private final int DURACION_SPLASH = 3000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Manda llamar splashscreen x 4 segundos
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSplash();

    }

    private void getSplash()
    {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user!=null)
                {
                    String idUser= user.getUid();
                    managerRef.document(idUser).get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if (documentSnapshot.exists())
                                    {
                                        Map<String,Object> actualizador = new HashMap<>();
                                        actualizador.put("fechaUltimoLogin",getTimeDate());

                                        managerRef.document(idUser).update(actualizador);

                                        Intent intent = new Intent(MainActivity.this,PrincipalActivity.class);
                                        intent.putExtra("idUser",idUser);
                                        startActivity(intent);
                                        finish();
                                    }
                                    else
                                    {
                                        conductorRef.document(idUser).get()
                                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                        if (documentSnapshot.exists())
                                                        {
                                                            Map<String,Object> actualizador = new HashMap<>();
                                                            actualizador.put("fechaUltimoLogin",getTimeDate());

                                                            conductorRef.document(idUser).update(actualizador);
                                                            Intent intent = new Intent(MainActivity.this,PrincipalActivity.class);
                                                            intent.putExtra("idUser",idUser);
                                                            startActivity(intent);
                                                            finish();
                                                        }
                                                        else
                                                        {
                                                            FirebaseAuth.getInstance().signOut();
                                                            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                                            startActivity(intent);
                                                            finish();
                                                        }
                                                    }
                                                });
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            });

                }
                else
                {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, DURACION_SPLASH);
    }

    public static String getTimeDate() { // without parameter argument
        try{
            Date netDate = new Date(); // current time from here
            SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
            return sfd.format(netDate);
        } catch(Exception e) {
            return "date";
        }
    }
}