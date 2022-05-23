package com.rocasoftware.rocaridesconductor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText,passwordEditText;
    private Button accesoButton;
    private TextView olvidastePasswordTextView,registroTextView,privacidadTextView,terminosCondicionesTextView;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference managerRef = db.collection("Managers");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        accesoButton = findViewById(R.id.accesoButton);
        registroTextView = findViewById(R.id.registroTextView);
        olvidastePasswordTextView = findViewById(R.id.olvidastePasswordTextView);
        privacidadTextView = findViewById(R.id.privacidadTextView);
        terminosCondicionesTextView = findViewById(R.id.terminosCondicionesTextView);

        privacidadTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,PrivacidadActivity.class);
                startActivity(intent);
                finish();
            }
        });


        registroTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,RegistroActivity.class);
                startActivity(intent);
                finish();
            }
        });

        olvidastePasswordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,OlvidasteContraActivity.class);
                startActivity(intent);
                finish();
            }
        });

        accesoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate();
            }
        });
    }

    private void validate() {


        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();


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

        iniciarSesion(email,password);

    }

    private void iniciarSesion(String email, String password)
    {
        accesoButton.setClickable(false);
        accesoButton.setText("Accesando...");
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            String idUser= user.getUid();
                            managerRef.document(idUser).get()
                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        if (documentSnapshot.exists())
                                        {

                                            ManagerModel manager = documentSnapshot.toObject(ManagerModel.class);
                                            String cuentaCompletada = manager.getCuentaCompletada();
                                            if (cuentaCompletada.equals("Si"))
                                            {
                                                Map<String,Object> actualizador = new HashMap<>();
                                                actualizador.put("fechaUltimoLogin",getTimeDate());

                                                managerRef.document(idUser).update(actualizador);

                                                accesoButton.setClickable(true);
                                                accesoButton.setText("Acceso");
                                                Intent intent = new Intent(LoginActivity.this,PrincipalActivity.class);
                                                intent.putExtra("idUser",idUser);
                                                startActivity(intent);
                                                finish();
                                            }
                                            else
                                            {
                                                accesoButton.setClickable(true);
                                                accesoButton.setText("Acceso");
                                                Intent intent = new Intent(LoginActivity.this,RegistroExtraActivity.class);
                                                intent.putExtra("idUser",idUser);
                                                startActivity(intent);
                                                finish();
                                            }
                                        }
                                        else
                                        {
                                            FirebaseAuth.getInstance().signOut();
                                            accesoButton.setClickable(true);
                                            accesoButton.setText("Acceso");
                                            Toast.makeText(LoginActivity.this, "Usuario no es Conductor", Toast.LENGTH_SHORT).show();
                                            //aqui se verifica si no es manager, que sea solo conductor
                                        }
                                    }
                                });

                        }
                        else
                        {
                            accesoButton.setClickable(true);
                            accesoButton.setText("Acceso");
                            Toast.makeText(LoginActivity.this,"Credenciales incorrectas",Toast.LENGTH_LONG).show();
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

}