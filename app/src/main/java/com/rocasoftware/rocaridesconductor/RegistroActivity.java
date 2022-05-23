package com.rocasoftware.rocaridesconductor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public class RegistroActivity extends AppCompatActivity {

    private EditText nombreEditText,apellidoEditText,telefonoEditText,emailEditText,passwordEditText,repetirpasswordEditText;
    private Button registrarButton;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference managerRef = db.collection("Managers");
    private CollectionReference conductorRef = db.collection("Conductores");
    private CollectionReference sexoRef = db.collection("Sexo");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        nombreEditText = findViewById(R.id.nombreEditText);
        apellidoEditText = findViewById(R.id.apellidoEditText);
        telefonoEditText = findViewById(R.id.telefonoEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        repetirpasswordEditText = findViewById(R.id.repetirpasswordEditText);
        registrarButton = findViewById(R.id.registrarButton);


        mAuth = FirebaseAuth.getInstance();

        registrarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                validate();
            }
        });
    }



    private void validate()
    {
        String nombre = nombreEditText.getText().toString();
        String apellido = apellidoEditText.getText().toString();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String repetirPassword = repetirpasswordEditText.getText().toString().trim();

        if(nombre.isEmpty())
        {
            nombreEditText.setError("No puede estar vacio.");
            return;
        }
        if(apellido.isEmpty())
        {
            apellidoEditText.setError("No puede estar vacio.");
            return;
        }
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            emailEditText.setError("Correo Invalido");
            return;
        }
        else
        {
            emailEditText.setError(null);
        }
        if (password.isEmpty() || password.length() < 8)
        {
            passwordEditText.setError("Se necesitan mas de 8 caracteres");
            return;
        }
        else if(!Pattern.compile("[0-9]").matcher(password).find())
        {
            passwordEditText.setError("Al menos un numero");
            return;
        }
        else
        {
            passwordEditText.setError(null);
        }
        if (!repetirPassword.equals(password))
        {
            repetirpasswordEditText.setError("Deben ser iguales");
            return;
        }
        else
        {
            registrar(nombre,apellido,email,password);
        }
    }

    private void registrar(String nombre,String apellido,String email, String password)
    {
        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            String telefono = telefonoEditText.getText().toString().trim();
                            String fechaRegistro = getTimeDate();
                            String fechaUltimoLogin = getTimeDate();
                            String cuentaActivada = "No";
                            String esManager = "Si";
                            String sexo = "";
                            String estado = "";
                            String ciudad = "";

                            String idUser = mAuth.getUid().toString();

                            ManagerModel manager = new ManagerModel(nombre,apellido,telefono,email,fechaRegistro,fechaUltimoLogin,cuentaActivada,sexo,estado,ciudad);
                            managerRef.document(idUser).set(manager);
                            String idManager = idUser;


                            ConductorModel conductor = new ConductorModel(nombre,apellido,telefono,email,fechaRegistro,fechaUltimoLogin,cuentaActivada,esManager,idManager,sexo,estado,ciudad);
                            conductorRef.document(idUser).set(conductor)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            FirebaseAuth.getInstance().signOut();
                                            Toast.makeText(RegistroActivity.this, "Usuario Agregado", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(RegistroActivity.this,LoginActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    });
                        }
                        else
                        {
                            Toast.makeText(RegistroActivity.this, "Fallo en registrarse", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(RegistroActivity.this,LoginActivity.class);
        startActivity(intent);
        finish();
    }
}