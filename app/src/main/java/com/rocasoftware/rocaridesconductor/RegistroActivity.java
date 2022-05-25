package com.rocasoftware.rocaridesconductor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;

public class RegistroActivity extends AppCompatActivity {

    private EditText nombreEditText,apellidoEditText,telefonoEditText,emailEditText,passwordEditText,repetirpasswordEditText;
    private Button fotoPerfilButton,fotoLicenciaButton,registrarButton;
    private Spinner sexoSpinner,estadoSpinner,ciudadSpinner;


    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


    private CollectionReference managerRef = db.collection("Managers");
    private CollectionReference conductoresRef = db.collection("Conductores");
    private CollectionReference sexoRef = db.collection("Sexo");
    private CollectionReference estadosRef = db.collection("Estados");

    private FirebaseStorage storage;
    private StorageReference storageReference;

    private Uri fotoPerfilUrl,fotoLicenciaUrl;


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

        sexoSpinner = findViewById(R.id.sexoSpinner);
        estadoSpinner = findViewById(R.id.estadoSpinner);
        ciudadSpinner = findViewById(R.id.ciudadSpinner);

        fotoPerfilButton = findViewById(R.id.fotoPerfilButton);
        fotoLicenciaButton = findViewById(R.id.fotoLicenciaButton);

        registrarButton = findViewById(R.id.registrarButton);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();


        mAuth = FirebaseAuth.getInstance();

        cargarSexo();
        cargarEstados();

        estadoSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                cargarCiudades();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        fotoPerfilButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seleccionarFotoPerfil();
            }
        });

        fotoLicenciaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seleccionarFotoLicencia();
            }
        });

        registrarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                validate();
            }
        });
    }

    private void seleccionarFotoPerfil()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,1);
    }

    private void seleccionarFotoLicencia()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==1 && resultCode==RESULT_OK && data!=null && data.getData()!=null)
        {
            fotoPerfilUrl = data.getData();
            fotoPerfilButton.setText("Foto Seleccionada");
        }
        if (requestCode==2 && resultCode==RESULT_OK && data!=null && data.getData()!=null)
        {
            fotoLicenciaUrl = data.getData();
            fotoLicenciaButton.setText("Foto Seleccionada");
        }
    }

    private void validate()
    {
        String nombre = nombreEditText.getText().toString();
        String apellido = apellidoEditText.getText().toString();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String repetirPassword = repetirpasswordEditText.getText().toString().trim();

        String sexo = sexoSpinner.getSelectedItem().toString();
        String estado = estadoSpinner.getSelectedItem().toString();
        String ciudad= "";


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
        if(ciudadSpinner.getAdapter().getCount() == 0)
        {
            Toast.makeText(this, "Ciudad no puede estar vacia.", Toast.LENGTH_SHORT).show();
            return;
        }
        else
        {
            ciudad = ciudadSpinner.getSelectedItem().toString();
        }

        registrar(nombre,apellido,email,password,sexo,estado,ciudad);

    }

    private void registrar(String nombre, String apellido, String email, String password, String sexo, String estado, String ciudad)
    {
        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            String idUser = user.getUid();
                            String randomKeyPerfil = UUID.randomUUID().toString();
                            String randomKeyLicencia = UUID.randomUUID().toString();
                            String idNewUser = mAuth.getUid().toString();


                            String telefono = telefonoEditText.getText().toString().trim();
                            String fechaRegistro = getTimeDate();
                            String fechaUltimoLogin = getTimeDate();
                            String cuentaActivada = "No";
                            String idManager = idUser;
                            String urlImagenFotoPerfil = randomKeyPerfil;
                            String urlImagenFotoLicencia = randomKeyLicencia;

                            ManagerModel manager = new ManagerModel(nombre,apellido,telefono,email,fechaRegistro,fechaUltimoLogin,cuentaActivada,sexo,estado,ciudad,urlImagenFotoPerfil,urlImagenFotoLicencia);
                            ConductorModel conductor = new ConductorModel(nombre,apellido,telefono,email,password,fechaRegistro,fechaUltimoLogin,cuentaActivada,idManager,sexo,estado,ciudad,urlImagenFotoPerfil,urlImagenFotoLicencia);

                            managerRef.document(idNewUser).set(manager)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            conductoresRef.document(idNewUser).set(conductor)
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(RegistroActivity.this, "Error al crear Conductor", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                            if (fotoPerfilButton.getText().toString().equals("Foto Seleccionada") && fotoLicenciaButton.getText().toString().equals("Foto Seleccionada"))
                                            {
                                                ProgressDialog pd = new ProgressDialog(RegistroActivity.this);
                                                pd.setTitle("Creando Usuario");
                                                pd.setMessage("Espere un momento....");
                                                pd.setIndeterminate(true);
                                                pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                                pd.show();


                                                StorageReference fotoPerfil = storageReference.child("documentos/Perfiles/" + randomKeyPerfil);
                                                StorageReference fotoLicencia = storageReference.child("documentos/Licencias/" + randomKeyLicencia);

                                                //Comprimir imagen antes de subirla
                                                try
                                                {
                                                    Bitmap original1 = MediaStore.Images.Media.getBitmap(getContentResolver(),fotoPerfilUrl);
                                                    Bitmap original2 = MediaStore.Images.Media.getBitmap(getContentResolver(),fotoLicenciaUrl);

                                                    ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
                                                    original1.compress(Bitmap.CompressFormat.JPEG,25,stream1);
                                                    byte[] imageByte1 = stream1.toByteArray();

                                                    ByteArrayOutputStream stream2 = new ByteArrayOutputStream();
                                                    original2.compress(Bitmap.CompressFormat.JPEG,25,stream2);
                                                    byte[] imageByte2 = stream2.toByteArray();

                                                    fotoPerfil.putBytes(imageByte1)
                                                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                                @Override
                                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                                    pd.dismiss();
                                                                    Toast.makeText(RegistroActivity.this, "Usuario Actualizado Correctamente", Toast.LENGTH_SHORT).show();
                                                                    Intent intent = new Intent(RegistroActivity.this,PrincipalActivity.class);
                                                                    startActivity(intent);
                                                                    finish();
                                                                }
                                                            }).addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    Toast.makeText(RegistroActivity.this, "Error al subir foto", Toast.LENGTH_SHORT).show();
                                                                }
                                                            });

                                                    fotoLicencia.putBytes(imageByte2);


                                                }
                                                catch (IOException e)
                                                {
                                                    Toast.makeText(RegistroActivity.this, "No se encontro el archivo", Toast.LENGTH_SHORT).show();
                                                }

                                            }
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(RegistroActivity.this, "Problema para agregar el usuario", Toast.LENGTH_SHORT).show();
                                        }
                                    });


                        }
                        else
                        {
                            Toast.makeText(RegistroActivity.this, "Fallo en registrar el usuario Auth", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RegistroActivity.this, "Error al crear conductor", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void cargarSexo()
    {
        List<String> sexoList = new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, sexoList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sexoSpinner.setAdapter(adapter);
        sexoRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String nombre = document.getString("nombre");
                        sexoList.add(nombre);
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void cargarEstados()
    {
        List<String> list = new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        estadoSpinner.setAdapter(adapter);
        estadosRef.orderBy("nombre").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String nombre = document.getString("nombre");
                        list.add(nombre);
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void cargarCiudades()
    {
        String estado = estadoSpinner.getSelectedItem().toString();
        estadosRef.orderBy("nombre").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        String idEstado;
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String nombre = document.getString("nombre");
                                if (nombre.equals(estado))
                                {
                                    idEstado = document.getId();

                                    List<String> list = new ArrayList<>();
                                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, list);
                                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    ciudadSpinner.setAdapter(adapter);
                                    estadosRef.document(idEstado).collection("Ciudades").orderBy("nombre").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                    String nombre = document.getString("nombre");
                                                    list.add(nombre);
                                                }
                                                adapter.notifyDataSetChanged();
                                            }
                                            else
                                            {

                                            }

                                        }
                                    });
                                }
                                else
                                {

                                }
                            }
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