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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

public class VehiculosEditarActivity extends AppCompatActivity {
    Spinner marcaSpinner,modeloSpinner;
    EditText añoEditText,colorEditText,placasEditText;
    ImageView fotoVehiculoImageView,fotoTarjetaImageView;
    Button fotoVehiculoButton,fotoTarjetaButton,actualizarButton,borrarButton;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    private CollectionReference vehiculoRef = db.collection("Vehiculos");
    private CollectionReference marcaRef = db.collection("Marcas");


    private FirebaseStorage storage;
    private StorageReference storageReference;

    private Uri fotoVehiculoUrl,fotoTarjetaUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehiculos_editar);

        mAuth = FirebaseAuth.getInstance();

        marcaSpinner = findViewById(R.id.marcaSpinner);
        modeloSpinner = findViewById(R.id.modeloSpinner);

        añoEditText = findViewById(R.id.añoEditText);
        colorEditText = findViewById(R.id.colorEditText);
        placasEditText = findViewById(R.id.placasEditText);

        fotoVehiculoImageView = findViewById(R.id.fotoVehiculoImageView);
        fotoTarjetaImageView = findViewById(R.id.fotoTarjetaImageView);

        fotoVehiculoButton = findViewById(R.id.fotoVehiculoButton);
        fotoTarjetaButton = findViewById(R.id.fotoTarjetaButton);
        actualizarButton = findViewById(R.id.actualizarButton);
        borrarButton = findViewById(R.id.borrarButton);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        cargarinfo();

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

    private void validar()
    {
        String marca = marcaSpinner.getSelectedItem().toString();
        String modelo = modeloSpinner.getSelectedItem().toString();
        String año = añoEditText.getText().toString().trim();
        String color = colorEditText.getText().toString().trim();
        String placas = placasEditText.getText().toString().trim();

        if (año.isEmpty())
        {
            añoEditText.setError("No puede estar vacio");
            return;
        }
        if (color.isEmpty())
        {
            colorEditText.setError("No puede estar vacio");
            return;
        }
        if (placas.isEmpty())
        {
            placasEditText.setError("No puede estar vacio");
            return;
        }
        actualizarButton.setClickable(false);
        actualizarButton.setText("Actualizando...");

        actualizar(marca,modelo,año,color,placas);

    }

    private void actualizar(String marca, String modelo, String año, String color, String placas)
    {
        String idDocumento = getIntent().getStringExtra("idDocumento");

        Map<String,Object> note = new HashMap<>();
        note.put("marca",marca);
        note.put("modelo",modelo);
        note.put("año",año);
        note.put("color",color);
        note.put("placas",placas);

        vehiculoRef.document(idDocumento).update(note)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                        if (fotoVehiculoButton.getText().toString().equals("Foto Seleccionada"))
                        {
                            ProgressDialog pd = new ProgressDialog(VehiculosEditarActivity.this);
                            pd.setTitle("Actualizando Vehiculo");
                            pd.setMessage("Espere un momento....");
                            pd.setIndeterminate(true);
                            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                            pd.show();

                            vehiculoRef.document(idDocumento).get()
                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            if (documentSnapshot.exists())
                                            {
                                                VehiculoModel vehiculo = documentSnapshot.toObject(VehiculoModel.class);
                                                String urlImagenFotoVehiculo = vehiculo.getUrlImagenFotoVehiculo();

                                                StorageReference fotoVehiculo = storageReference.child("documentos/Vehiculos/" + urlImagenFotoVehiculo);

                                                //Comprimir imagen antes de subirla
                                                try
                                                {
                                                    Bitmap original1 = MediaStore.Images.Media.getBitmap(getContentResolver(),fotoVehiculoUrl);



                                                    ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
                                                    original1.compress(Bitmap.CompressFormat.JPEG,25,stream1);
                                                    byte[] imageByte1 = stream1.toByteArray();

                                                    fotoVehiculo.putBytes(imageByte1)
                                                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                                @Override
                                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                                                }
                                                            }).addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    Toast.makeText(VehiculosEditarActivity.this, "Error al subir foto", Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                }
                                                catch (IOException e)
                                                {
                                                    Toast.makeText(VehiculosEditarActivity.this, "No se encontro el archivo", Toast.LENGTH_SHORT).show();
                                                }


                                            }
                                        }
                                    });

                        }
                        if (fotoTarjetaButton.getText().toString().equals("Foto Seleccionada"))
                        {
                            ProgressDialog pd2 = new ProgressDialog(VehiculosEditarActivity.this);
                            pd2.setTitle("Actualizando Conductor");
                            pd2.setMessage("Espere un momento....");
                            pd2.setIndeterminate(true);
                            pd2.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                            pd2.show();

                            vehiculoRef.document(idDocumento).get()
                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            if (documentSnapshot.exists())
                                            {
                                                VehiculoModel vehiculo = documentSnapshot.toObject(VehiculoModel.class);
                                                String urlImagenFotoTarjeta = vehiculo.getUrlImagenFotoTarjeta();

                                                StorageReference fotoTarjeta = storageReference.child("documentos/Tarjetas/" + urlImagenFotoTarjeta);

                                                //Comprimir imagen antes de subirla
                                                try
                                                {
                                                    Bitmap original2 = MediaStore.Images.Media.getBitmap(getContentResolver(),fotoTarjetaUrl);


                                                    ByteArrayOutputStream stream2 = new ByteArrayOutputStream();
                                                    original2.compress(Bitmap.CompressFormat.JPEG,25,stream2);
                                                    byte[] imageByte2 = stream2.toByteArray();

                                                    fotoTarjeta.putBytes(imageByte2)
                                                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                                @Override
                                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                                }
                                                            }).addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    Toast.makeText(VehiculosEditarActivity.this, "Error al subir foto", Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                }
                                                catch (IOException e)
                                                {
                                                    Toast.makeText(VehiculosEditarActivity.this, "No se encontro el archivo", Toast.LENGTH_SHORT).show();
                                                }


                                            }
                                        }
                                    });
                        }
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            public void run() {
                                Toast.makeText(VehiculosEditarActivity.this, "Registro Actualizado Correctamente", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(VehiculosEditarActivity.this,VehiculosActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }, 3000);   //3 seconds

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(VehiculosEditarActivity.this, "Error al actualizar conductor", Toast.LENGTH_SHORT).show();
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

            Uri selectedImage = data.getData();
            InputStream in;
            try {
                in = getContentResolver().openInputStream(selectedImage);
                final Bitmap selected_img = BitmapFactory.decodeStream(in);
                fotoVehiculoImageView.setImageBitmap(selected_img);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, "An error occured!", Toast.LENGTH_LONG).show();
            }


        }
        if (requestCode==2 && resultCode==RESULT_OK && data!=null && data.getData()!=null)
        {
            fotoTarjetaUrl = data.getData();
            fotoTarjetaButton.setText("Foto Seleccionada");

            Uri selectedImage = data.getData();
            InputStream in;
            try {
                in = getContentResolver().openInputStream(selectedImage);
                final Bitmap selected_img = BitmapFactory.decodeStream(in);
                fotoTarjetaImageView.setImageBitmap(selected_img);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, "An error occured!", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void cargarinfo()
    {
        String idDocumento = getIntent().getStringExtra("idDocumento");
        vehiculoRef.document(idDocumento).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists())
                        {
                            VehiculoModel vehiculo = documentSnapshot.toObject(VehiculoModel.class);
                            String marca = vehiculo.getMarca();
                            String modelo = vehiculo.getModelo();
                            String año = vehiculo.getAño();
                            String color = vehiculo.getColor();
                            String placas = vehiculo.getPlacas();
                            String urlImagenFotoVehiculo = vehiculo.getUrlImagenFotoVehiculo();
                            String urlImagenFotoTarjeta = vehiculo.getUrlImagenFotoTarjeta();

                            cargarMarcas(marca,modelo);

                            añoEditText.setText(año);
                            colorEditText.setText(color);
                            placasEditText.setText(placas);

                            cargarFotoVehiculo(urlImagenFotoVehiculo);
                            cargarFotoTarjeta(urlImagenFotoTarjeta);

                        }
                        else
                        {
                            Toast.makeText(VehiculosEditarActivity.this, "No existe el documento", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void cargarFotoVehiculo(String urlImagenFotoVehiculo)
    {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference photoReference= storageReference.child("documentos/Vehiculos/"+urlImagenFotoVehiculo);

        final long ONE_MEGABYTE = 1024 * 1024;
        photoReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                fotoVehiculoImageView.setImageBitmap(bmp);
            }
        });
    }

    private void cargarFotoTarjeta(String urlImagenFotoTarjeta)
    {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference photoReference= storageReference.child("documentos/Tarjetas/"+urlImagenFotoTarjeta);

        final long ONE_MEGABYTE = 1024 * 1024;
        photoReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                fotoTarjetaImageView.setImageBitmap(bmp);
            }
        });
    }

    private void cargarMarcas(String marca,String modelo)
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
                    int posicion = adapter.getPosition(marca);
                    marcaSpinner.setSelection(posicion);
                    cargarModelos(modelo);
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

    private void cargarModelos(String modelo)
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
                                                int posicion = adapter.getPosition(modelo);
                                                modeloSpinner.setSelection(posicion);
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

    private void CreateAlertDialog()
    {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setMessage("Seguro que quieres borrar este registro?");
        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String idDocumento = getIntent().getStringExtra("idDocumento");

                vehiculoRef.document(idDocumento).get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if (documentSnapshot.exists())
                                {
                                    ProgressDialog pd3 = new ProgressDialog(VehiculosEditarActivity.this);
                                    pd3.setTitle("Borrando Vehiculo");
                                    pd3.setMessage("Espere un momento....");
                                    pd3.setIndeterminate(true);
                                    pd3.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                    pd3.show();

                                    VehiculoModel vehiculo = documentSnapshot.toObject(VehiculoModel.class);
                                    String urlImagenFotoVehiculo = vehiculo.getUrlImagenFotoVehiculo();
                                    String urlImagenFotoTarjeta = vehiculo.getUrlImagenFotoTarjeta();



                                    StorageReference fotoVehiculo = storageReference.child("documentos/Vehiculos/" + urlImagenFotoVehiculo);
                                    StorageReference fotoTarjeta = storageReference.child("documentos/Tarjetas/" + urlImagenFotoTarjeta);

                                    fotoVehiculo.delete();
                                    fotoTarjeta.delete();

                                    vehiculoRef.document(idDocumento).delete()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Handler handler = new Handler();
                                                    handler.postDelayed(new Runnable() {
                                                        public void run() {
                                                            Toast.makeText(VehiculosEditarActivity.this, "Registro Borrado Correctamente", Toast.LENGTH_SHORT).show();
                                                            Intent intent = new Intent(VehiculosEditarActivity.this,VehiculosActivity.class);
                                                            startActivity(intent);
                                                            finish();
                                                        }
                                                    }, 3000);   //3 seconds

                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(VehiculosEditarActivity.this, "Error al Borrar Registro", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(VehiculosEditarActivity.this, "Proceso Cancelado", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.create();
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(VehiculosEditarActivity.this,VehiculosActivity.class);
        startActivity(intent);
        finish();
    }
}