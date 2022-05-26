package com.rocasoftware.rocaridesconductor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
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

public class VehiculosRegistroActivity extends AppCompatActivity {
    private Spinner marcaSpinner,modeloSpinner;
    private EditText añoEditText,colorEditText,placasEditText;
    private Button fotoVehiculoButton,fotoTarjetaButton,registrarButton;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String idUser = user.getUid();

    private CollectionReference vehiculoRef = db.collection("Vehiculos");
    private CollectionReference marcaRef = db.collection("Marcas");


    private FirebaseStorage storage;
    private StorageReference storageReference;

    private Uri fotoVehiculoUrl,fotoTarjetaUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehiculos_registro);

        marcaSpinner = findViewById(R.id.marcaSpinner);
        modeloSpinner = findViewById(R.id.modeloSpinner);

        añoEditText = findViewById(R.id.añoEditText);
        colorEditText = findViewById(R.id.colorEditText);
        placasEditText = findViewById(R.id.placasEditText);

        fotoVehiculoButton = findViewById(R.id.fotoVehiculoButton);
        fotoTarjetaButton = findViewById(R.id.fotoTarjetaButton);
        registrarButton = findViewById(R.id.registrarButton);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();


        cargarMarcas();


        marcaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                cargarModelos();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        fotoVehiculoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seleccionarFotoVehiculo();
            }
        });

        fotoTarjetaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seleccionarFotoTarjeta();
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

    private void seleccionarFotoVehiculo()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,1);
    }

    private void seleccionarFotoTarjeta()
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
            fotoVehiculoUrl = data.getData();
            fotoVehiculoButton.setText("Foto Seleccionada");
        }
        if (requestCode==2 && resultCode==RESULT_OK && data!=null && data.getData()!=null)
        {
            fotoTarjetaUrl = data.getData();
            fotoTarjetaButton.setText("Foto Seleccionada");
        }
    }

    private void validate()
    {
        String marca = marcaSpinner.getSelectedItem().toString();
        String modelo = "";

        String año = añoEditText.getText().toString().trim();
        String color = colorEditText.getText().toString().trim();
        String placas = placasEditText.getText().toString().trim();

        if (año.isEmpty())
        {
            añoEditText.setError("No puede estar vacio.");
            return;
        }
        if (color.isEmpty())
        {
            colorEditText.setError("No puede estar vacio.");
            return;
        }
        if (placas.isEmpty())
        {
            placasEditText.setError("No puede estar vacio.");
            return;
        }
        if(modeloSpinner.getAdapter().getCount() == 0)
        {
            Toast.makeText(this, "Modelo no puede estar vacia.", Toast.LENGTH_SHORT).show();
            return;
        }
        else
        {
            modelo = modeloSpinner.getSelectedItem().toString();
        }

        registrar(marca,modelo,año,color,placas);

    }

    private void registrar(String marca, String modelo, String año, String color, String placas)
    {
        String randomKeyFotoVehiculo = UUID.randomUUID().toString();
        String randomKeyTarjeta = UUID.randomUUID().toString();

        String urlImagenFotoVehiculo = randomKeyFotoVehiculo;
        String urlImagenFotoTarjeta = randomKeyTarjeta;

        VehiculoModel vehiculo = new VehiculoModel(marca,modelo,año,color,placas,idUser,randomKeyFotoVehiculo,randomKeyTarjeta);
        vehiculoRef.document().set(vehiculo)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        ProgressDialog pd = new ProgressDialog(VehiculosRegistroActivity.this);
                        pd.setTitle("Creando el vehiculo");
                        pd.setMessage("Espere un momento....");
                        pd.setIndeterminate(true);
                        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        pd.show();


                        StorageReference fotoVehiculo = storageReference.child("documentos/Vehiculos/" + randomKeyFotoVehiculo);
                        StorageReference fotoTarjeta = storageReference.child("documentos/Tarjetas/" + randomKeyTarjeta);

                        //Comprimir imagen antes de subirla
                        try
                        {
                            Bitmap original1 = MediaStore.Images.Media.getBitmap(getContentResolver(),fotoVehiculoUrl);
                            Bitmap original2 = MediaStore.Images.Media.getBitmap(getContentResolver(),fotoTarjetaUrl);

                            ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
                            original1.compress(Bitmap.CompressFormat.JPEG,25,stream1);
                            byte[] imageByte1 = stream1.toByteArray();

                            ByteArrayOutputStream stream2 = new ByteArrayOutputStream();
                            original2.compress(Bitmap.CompressFormat.JPEG,25,stream2);
                            byte[] imageByte2 = stream2.toByteArray();

                            fotoVehiculo.putBytes(imageByte1)
                                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            fotoTarjeta.putBytes(imageByte2);
                                            Handler handler = new Handler();
                                            handler.postDelayed(new Runnable() {
                                                public void run() {
                                                    pd.dismiss();
                                                    Toast.makeText(VehiculosRegistroActivity.this, "Usuario Actualizado Correctamente", Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(VehiculosRegistroActivity.this,VehiculosActivity.class);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            }, 3000);   //3 seconds

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(VehiculosRegistroActivity.this, "Error al subir foto", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                        }
                        catch (IOException e)
                        {
                            Toast.makeText(VehiculosRegistroActivity.this, "No se encontro el archivo", Toast.LENGTH_SHORT).show();
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    private void cargarMarcas()
    {
        List<String> list = new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        marcaSpinner.setAdapter(adapter);
        marcaRef.orderBy("nombre").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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

    private void cargarModelos()
    {
        String marca = marcaSpinner.getSelectedItem().toString();
        marcaRef.orderBy("nombre").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        String idMarca;
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String nombre = document.getString("nombre");
                                if (nombre.equals(marca))
                                {
                                    idMarca = document.getId();

                                    List<String> list = new ArrayList<>();
                                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, list);
                                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    modeloSpinner.setAdapter(adapter);
                                    marcaRef.document(idMarca).collection("Modelos").orderBy("nombre").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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
        Intent intent = new Intent(VehiculosRegistroActivity.this,VehiculosActivity.class);
        startActivity(intent);
        finish();
    }
}