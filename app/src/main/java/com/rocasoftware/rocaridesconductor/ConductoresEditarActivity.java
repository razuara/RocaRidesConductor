package com.rocasoftware.rocaridesconductor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConductoresEditarActivity extends AppCompatActivity {

    private TextView nombreEditText,apellidoEditText,telefonoEditText;
    private Spinner sexoSpinner,estadoSpinner,ciudadSpinner;
    private ImageView fotoPerfilImageView,fotoLicenciaImageView;
    private Button actualizarButton,borrarButton,fotoPerfilButton,fotoLicenciaButton;


    private FirebaseApp defaultApp = FirebaseApp.getInstance();
    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String idUser = user.getUid();

    final private FirebaseApp VerifierApp = FirebaseApp.initializeApp(defaultApp.getApplicationContext(),defaultApp.getOptions(),"Verifier");
    final private FirebaseAuth mAuth2 = FirebaseAuth.getInstance(VerifierApp);

    private CollectionReference contadorRef = db.collection("Contadores");
    private CollectionReference conductorRef = db.collection("Conductores");
    private CollectionReference sexoRef = db.collection("Sexo");
    private CollectionReference estadosRef = db.collection("Estados");


    private FirebaseStorage storage;
    private StorageReference storageReference;

    private Uri fotoPerfilUrl,fotoLicenciaUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conductores_editar);

        mAuth = FirebaseAuth.getInstance();

        nombreEditText = findViewById(R.id.nombreEditText);
        apellidoEditText = findViewById(R.id.apellidoEditText);
        telefonoEditText = findViewById(R.id.telefonoEditText);

        sexoSpinner = findViewById(R.id.sexoSpinner);
        estadoSpinner = findViewById(R.id.estadoSpinner);
        ciudadSpinner = findViewById(R.id.ciudadSpinner);

        fotoPerfilImageView = findViewById(R.id.fotoPerfilImageView);
        fotoLicenciaImageView = findViewById(R.id.fotoLicenciaImageView);

        actualizarButton = findViewById(R.id.actualizarButton);
        borrarButton = findViewById(R.id.borrarButton);
        fotoPerfilButton = findViewById(R.id.fotoPerfilButton);
        fotoLicenciaButton = findViewById(R.id.fotoLicenciaButton);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        cargarinfo();

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

        actualizarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validar();
            }
        });

        borrarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateAlertDialog();
            }
        });
    }

    private void CreateAlertDialog()
    {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setMessage("Seguro que quieres borrar este registro?");
        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String idDocumento = getIntent().getStringExtra("idDocumento");

                conductorRef.document(idDocumento).get()
                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        if (documentSnapshot.exists())
                                        {
                                            ProgressDialog pd3 = new ProgressDialog(ConductoresEditarActivity.this);
                                            pd3.setTitle("Borrando Conductor");
                                            pd3.setMessage("Espere un momento....");
                                            pd3.setIndeterminate(true);
                                            pd3.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                            pd3.show();

                                            ConductorModel conductor = documentSnapshot.toObject(ConductorModel.class);
                                            String urlImagenFotoPerfil = conductor.getUrlImagenFotoPerfil();
                                            String urlImagenFotoLicencia = conductor.getUrlImagenFotoLicencia();
                                            String email = conductor.getEmail();
                                            String password = conductor.getPassword();


                                            StorageReference fotoPerfil = storageReference.child("documentos/Perfiles/" + urlImagenFotoPerfil);
                                            StorageReference fotoLicencia = storageReference.child("documentos/Licencias/" + urlImagenFotoLicencia);

                                            fotoPerfil.delete();
                                            fotoLicencia.delete();

                                            mAuth2.signInWithEmailAndPassword(email, password);
                                            mAuth2.getCurrentUser().delete();



                                            conductorRef.document(idDocumento).delete()
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {
                                                            //contador de conductores
                                                            contador("Conductores","-");
                                                            Handler handler = new Handler();
                                                            handler.postDelayed(new Runnable() {
                                                                public void run() {
                                                                    VerifierApp.delete();
                                                                    Toast.makeText(ConductoresEditarActivity.this, "Registro Borrado Correctamente", Toast.LENGTH_SHORT).show();
                                                                    Intent intent = new Intent(ConductoresEditarActivity.this,ConductoresActivity.class);
                                                                    startActivity(intent);
                                                                    finish();
                                                                }
                                                            }, 3000);   //3 seconds

                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(ConductoresEditarActivity.this, "Error al Borrar Registro", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });

                                        }
                                    }
                                });


            }
        });
        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(ConductoresEditarActivity.this, "Proceso Cancelado", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.create();
        dialog.show();
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

            Uri selectedImage = data.getData();
            InputStream in;
            try {
                in = getContentResolver().openInputStream(selectedImage);
                final Bitmap selected_img = BitmapFactory.decodeStream(in);
                fotoPerfilImageView.setImageBitmap(selected_img);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, "An error occured!", Toast.LENGTH_LONG).show();
            }


        }
        if (requestCode==2 && resultCode==RESULT_OK && data!=null && data.getData()!=null)
        {
            fotoLicenciaUrl = data.getData();
            fotoLicenciaButton.setText("Foto Seleccionada");

            Uri selectedImage = data.getData();
            InputStream in;
            try {
                in = getContentResolver().openInputStream(selectedImage);
                final Bitmap selected_img = BitmapFactory.decodeStream(in);
                fotoLicenciaImageView.setImageBitmap(selected_img);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, "An error occured!", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void validar()
    {
        String nombre = nombreEditText.getText().toString().trim();
        String apellido = apellidoEditText.getText().toString().trim();
        String telefono = telefonoEditText.getText().toString().trim();
        String sexo = sexoSpinner.getSelectedItem().toString();
        String estado = estadoSpinner.getSelectedItem().toString();
        String ciudad = ciudadSpinner.getSelectedItem().toString();


        if (nombre.isEmpty())
        {
            nombreEditText.setError("No puede estar vacio");
            return;
        }
        if (apellido.isEmpty())
        {
            apellidoEditText.setError("No puede estar vacio");
            return;
        }
        if (telefono.isEmpty())
        {
            telefonoEditText.setError("No puede estar vacio");
            return;
        }
        actualizarButton.setClickable(false);
        actualizarButton.setText("Actualizando...");

        actualizar(nombre,apellido,telefono,sexo,estado,ciudad);

    }

    private void actualizar(String nombre, String apellido, String telefono,String sexo,String estado,String ciudad)
    {
        String idDocumento = getIntent().getStringExtra("idDocumento");

        Map<String,Object> note = new HashMap<>();
        note.put("nombre",nombre);
        note.put("apellido",apellido);
        note.put("telefono",telefono);
        note.put("sexo",sexo);
        note.put("estado",estado);
        note.put("ciudad",ciudad);

        conductorRef.document(idDocumento).update(note)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                        if (fotoPerfilButton.getText().toString().equals("Foto Seleccionada"))
                        {
                            ProgressDialog pd = new ProgressDialog(ConductoresEditarActivity.this);
                            pd.setTitle("Actualizando Conductor");
                            pd.setMessage("Espere un momento....");
                            pd.setIndeterminate(true);
                            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                            pd.show();

                            conductorRef.document(idDocumento).get()
                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        if (documentSnapshot.exists())
                                        {
                                            ConductorModel conductor = documentSnapshot.toObject(ConductorModel.class);
                                            String urlImagenFotoPerfil = conductor.getUrlImagenFotoPerfil();

                                            StorageReference fotoPerfil = storageReference.child("documentos/Perfiles/" + urlImagenFotoPerfil);

                                            //Comprimir imagen antes de subirla
                                            try
                                            {
                                                Bitmap original1 = MediaStore.Images.Media.getBitmap(getContentResolver(),fotoPerfilUrl);



                                                ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
                                                original1.compress(Bitmap.CompressFormat.JPEG,25,stream1);
                                                byte[] imageByte1 = stream1.toByteArray();

                                                fotoPerfil.putBytes(imageByte1)
                                                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                            @Override
                                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Toast.makeText(ConductoresEditarActivity.this, "Error al subir foto", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                            }
                                            catch (IOException e)
                                            {
                                                Toast.makeText(ConductoresEditarActivity.this, "No se encontro el archivo", Toast.LENGTH_SHORT).show();
                                            }


                                        }
                                    }
                                });

                        }
                        if (fotoLicenciaButton.getText().toString().equals("Foto Seleccionada"))
                        {
                            ProgressDialog pd2 = new ProgressDialog(ConductoresEditarActivity.this);
                            pd2.setTitle("Actualizando Conductor");
                            pd2.setMessage("Espere un momento....");
                            pd2.setIndeterminate(true);
                            pd2.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                            pd2.show();

                            conductorRef.document(idDocumento).get()
                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            if (documentSnapshot.exists())
                                            {
                                                ConductorModel conductor = documentSnapshot.toObject(ConductorModel.class);
                                                String urlImagenFotoLicencia = conductor.getUrlImagenFotoLicencia();

                                                StorageReference fotoPerfil = storageReference.child("documentos/Licencias/" + urlImagenFotoLicencia);

                                                //Comprimir imagen antes de subirla
                                                try
                                                {
                                                    Bitmap original2 = MediaStore.Images.Media.getBitmap(getContentResolver(),fotoLicenciaUrl);


                                                    ByteArrayOutputStream stream2 = new ByteArrayOutputStream();
                                                    original2.compress(Bitmap.CompressFormat.JPEG,25,stream2);
                                                    byte[] imageByte2 = stream2.toByteArray();

                                                    fotoPerfil.putBytes(imageByte2)
                                                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                                @Override
                                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                                }
                                                            }).addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    Toast.makeText(ConductoresEditarActivity.this, "Error al subir foto", Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                }
                                                catch (IOException e)
                                                {
                                                    Toast.makeText(ConductoresEditarActivity.this, "No se encontro el archivo", Toast.LENGTH_SHORT).show();
                                                }


                                            }
                                        }
                                    });
                        }
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            public void run() {
                                VerifierApp.delete();
                                Toast.makeText(ConductoresEditarActivity.this, "Registro Actualizado Correctamente", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(ConductoresEditarActivity.this,ConductoresActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }, 3000);   //3 seconds

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ConductoresEditarActivity.this, "Error al actualizar conductor", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void cargarinfo()
    {
        String idDocumento = getIntent().getStringExtra("idDocumento");
        conductorRef.document(idDocumento).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists())
                        {
                            ConductorModel conductor = documentSnapshot.toObject(ConductorModel.class);
                            String nombre = conductor.getNombre();
                            String apellido = conductor.getApellido();
                            String telefono = conductor.getTelefono();
                            String sexo = conductor.getSexo();
                            String estado = conductor.getEstado();
                            String ciudad = conductor.getCiudad();
                            String urlImagenFotoPerfil = conductor.getUrlImagenFotoPerfil();
                            String urlImagenFotoLicencia = conductor.getUrlImagenFotoLicencia();

                            nombreEditText.setText(nombre);
                            apellidoEditText.setText(apellido);
                            telefonoEditText.setText(telefono);
                            cargarSexo(sexo);
                            cargarEstados(estado,ciudad);
                            cargarFotoPerfil(urlImagenFotoPerfil);
                            cargarFotoLicencia(urlImagenFotoLicencia);

                        }
                        else
                        {
                            Toast.makeText(ConductoresEditarActivity.this, "No existe el documento", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void cargarFotoLicencia(String urlImagenFotoLicencia)
    {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference photoReference= storageReference.child("documentos/Licencias/"+urlImagenFotoLicencia);

        final long ONE_MEGABYTE = 1024 * 1024;
        photoReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                fotoLicenciaImageView.setImageBitmap(bmp);
            }
        });
    }

    private void cargarFotoPerfil(String urlImagenFotoPerfil)
    {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference photoReference= storageReference.child("documentos/Perfiles/"+urlImagenFotoPerfil);

        final long ONE_MEGABYTE = 1024 * 1024;
        photoReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                fotoPerfilImageView.setImageBitmap(bmp);
            }
        });
    }


    private void cargarSexo(String sexo)
    {
        List<String> sexoList = new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, sexoList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sexoSpinner.setAdapter(adapter);
        sexoRef.orderBy("nombre").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String nombre = document.getString("nombre");
                        sexoList.add(nombre);
                    }
                    adapter.notifyDataSetChanged();
                    int posicion = adapter.getPosition(sexo);
                    sexoSpinner.setSelection(posicion);
                }
            }
        });

    }
    private void cargarEstados(String estado,String ciudad)
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
                    int posicion = adapter.getPosition(estado);
                    estadoSpinner.setSelection(posicion);
                    cargarCiudades(ciudad);
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

    private void cargarCiudades(String ciudad)
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
                                                int posicion = adapter.getPosition(ciudad);
                                                ciudadSpinner.setSelection(posicion);
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

    public void contador(String documento,String operador) {
        contadorRef.document(documento).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()) {
                            int contadorDocumento,contadorFinal;
                            ContadorModel contador = documentSnapshot.toObject(ContadorModel.class);
                            contadorDocumento = contador.getContador();
                            if (operador.equals("+"))
                            {
                                contadorFinal = contadorDocumento+1;
                            }
                            else
                            {
                                contadorFinal = contadorDocumento-1;
                            }
                            Map<String,Object> note = new HashMap<>();
                            note.put("contador",contadorFinal);
                            contadorRef.document(documento).update(note);
                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        VerifierApp.delete();
        Intent intent = new Intent(ConductoresEditarActivity.this,ConductoresActivity.class);
        startActivity(intent);
        finish();
    }
}