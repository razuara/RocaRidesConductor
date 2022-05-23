package com.rocasoftware.rocaridesconductor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import io.grpc.Compressor;

public class RegistroExtraActivity extends AppCompatActivity {
    private Button fotoIneButton;
    private CheckBox aceptoCheckBox;
    private Button registrarButton;
    private Spinner sexoSpinner,estadoSpinner,ciudadSpinner;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference managerRef = db.collection("Managers");
    private CollectionReference sexoRef = db.collection("Sexo");
    private CollectionReference estadosRef = db.collection("Estados");

    private FirebaseStorage storage;
    private StorageReference storageReference;

    private Uri ineUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_extra);

        sexoSpinner = findViewById(R.id.sexoSpinner);
        estadoSpinner = findViewById(R.id.estadoSpinner);
        ciudadSpinner = findViewById(R.id.ciudadSpinner);
        fotoIneButton = findViewById(R.id.fotoIneButton);
        aceptoCheckBox = findViewById(R.id.aceptoCheckBox);
        registrarButton = findViewById(R.id.registrarButton);

        registrarButton.setEnabled(false);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        estadoSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                cargarCiudades();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        aceptoCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                {
                    registrarButton.setEnabled(true);
                }
                else
                {
                    registrarButton.setEnabled(false);
                }
            }
        });

        mAuth = FirebaseAuth.getInstance();

        cargarSexo();
        cargarEstados();

        fotoIneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seleccionarFoto();
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

    private void seleccionarFoto()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==1 && resultCode==RESULT_OK && data!=null && data.getData()!=null)
        {
            ineUrl = data.getData();
            fotoIneButton.setText("Foto Seleccionada");
        }
    }

    private void subirFoto(String randomKey)
    {





    }

    private void validate()
    {
        String sexo = sexoSpinner.getSelectedItem().toString();
        String estado = estadoSpinner.getSelectedItem().toString();
        String ciudad= "";
        if(ciudadSpinner.getAdapter().getCount() == 0)
        {
            Toast.makeText(this, "Ciudad no puede estar vacia.", Toast.LENGTH_SHORT).show();
            return;
        }
        else
        {
            ciudad = ciudadSpinner.getSelectedItem().toString();
        }



        registrar(sexo,estado,ciudad);

    }

    private void registrar(String sexo, String estado, String ciudad)
    {
        String randomKey = UUID.randomUUID().toString();
        String idUser = mAuth.getUid().toString();

        Map<String,Object> actualizador = new HashMap<>();
        actualizador.put("sexo",sexo);
        actualizador.put("estado",estado);
        actualizador.put("ciudad",ciudad);
        actualizador.put("urlImagenIne",randomKey);
        actualizador.put("cuentaCompletada","Si");
        actualizador.put("fechaRegistro",getTimeDate());
        actualizador.put("fechaUltimoLogin",getTimeDate());

        if (fotoIneButton.getText().toString().equals("Foto Seleccionada"))
        {
            managerRef.document(idUser).update(actualizador)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {

                            ProgressDialog pd = new ProgressDialog(RegistroExtraActivity.this);
                            pd.setTitle("Subiendo Fotografia");
                            pd.show();

                            StorageReference fotoIne = storageReference.child("documentos/Ine/" + randomKey);

                            //Comprimir imagen antes de subirla
                            try
                            {
                                Bitmap original = MediaStore.Images.Media.getBitmap(getContentResolver(),ineUrl);
                                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                original.compress(Bitmap.CompressFormat.JPEG,50,stream);
                                byte[] imageByte = stream.toByteArray();

                                fotoIne.putBytes(imageByte)
                                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                pd.dismiss();
                                                Toast.makeText(RegistroExtraActivity.this, "Usuario Actualizado Correctamente", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(RegistroExtraActivity.this,PrincipalActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(RegistroExtraActivity.this, "Error al subir foto", Toast.LENGTH_SHORT).show();
                                            }
                                        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                                                double progressPorcent = (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                                                pd.setMessage("Progreso : " + (int) progressPorcent + "%");
                                            }
                                        });

                            }
                            catch (IOException e)
                            {
                                Toast.makeText(RegistroExtraActivity.this, "No se encontro el archivo", Toast.LENGTH_SHORT).show();
                            }



                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(RegistroExtraActivity.this, "Error al Actualizar Usuario", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        else
        {
            Toast.makeText(RegistroExtraActivity.this, "Tienes que seleccionar un Foto", Toast.LENGTH_SHORT).show();
        }

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
}