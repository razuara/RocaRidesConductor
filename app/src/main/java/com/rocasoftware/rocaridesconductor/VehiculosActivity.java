package com.rocasoftware.rocaridesconductor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class VehiculosActivity extends AppCompatActivity {
    private EditText buscarEditView;
    private RecyclerView recyclerView;
    private Button registroButton;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference vehiculoRef = db.collection("Vehiculos");

    private FirestoreRecyclerAdapter adapter;


    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehiculos);

        registroButton = findViewById(R.id.registroButton);
        recyclerView = findViewById(R.id.vehiculosRecyclerView);
        buscarEditView = findViewById(R.id.buscarEditView);


        cargarLista();


        buscarEditView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                cargarListaBuscar();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        registroButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VehiculosActivity.this,VehiculosRegistroActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    private void cargarListaBuscar()
    {
        String busqueda = buscarEditView.getText().toString().trim();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String idUser = user.getUid();
        Query query;

        if (busqueda.equals(""))
        {
            query = vehiculoRef.orderBy("marca").whereEqualTo("idManager",idUser);
        }
        else
        {
            query = vehiculoRef.orderBy("marca").whereEqualTo("idManager",idUser).startAt(busqueda).endAt(busqueda + "\uf8ff");
        }

        FirestoreRecyclerOptions<VehiculoModel> options = new FirestoreRecyclerOptions.Builder<VehiculoModel>()
                .setQuery(query,VehiculoModel.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<VehiculoModel, VehiculosActivity.VehiculosViewHolder>(options) {
            @NonNull
            @Override
            public VehiculosActivity.VehiculosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.vehiculo_item,parent,false);
                return new VehiculosActivity.VehiculosViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull VehiculosActivity.VehiculosViewHolder holder, int position, @NonNull VehiculoModel model) {
                StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                StorageReference photoReference= storageReference.child("documentos/Vehiculos/"+model.urlImagenFotoVehiculo);

                final long ONE_MEGABYTE = 1024 * 1024;
                photoReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        holder.fotoVehiculoImageView.setImageBitmap(bmp);

                    }
                });

                holder.nombreTextView.setText(model.marca + " " + model.modelo + " " + model.año);
                holder.colorTextView.setText(model.color);
                holder.placasTextView.setText(model.placas);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DocumentSnapshot snapshot = getSnapshots().getSnapshot(holder.getAbsoluteAdapterPosition());
                        String idDocumento = snapshot.getId();
                        Intent intent = new Intent(VehiculosActivity.this, VehiculosEditarActivity.class);
                        intent.putExtra("idDocumento",idDocumento);
                        startActivity(intent);
                        finish();
                    }
                });
            }
        };
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }


    private void cargarLista()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String idUser = user.getUid();
        Query query = vehiculoRef.orderBy("marca").whereEqualTo("idManager",idUser);
        FirestoreRecyclerOptions<VehiculoModel> options = new FirestoreRecyclerOptions.Builder<VehiculoModel>()
                .setQuery(query,VehiculoModel.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<VehiculoModel, VehiculosActivity.VehiculosViewHolder>(options) {
            @NonNull
            @Override
            public VehiculosActivity.VehiculosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.vehiculo_item,parent,false);
                return new VehiculosActivity.VehiculosViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull VehiculosActivity.VehiculosViewHolder holder, int position, @NonNull VehiculoModel model) {
                StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                StorageReference photoReference= storageReference.child("documentos/Vehiculos/"+model.urlImagenFotoVehiculo);

                final long ONE_MEGABYTE = 1024 * 1024;
                photoReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        holder.fotoVehiculoImageView.setImageBitmap(bmp);

                    }
                });

                holder.nombreTextView.setText(model.marca + " " + model.modelo + " " + model.año);
                holder.colorTextView.setText(model.color);
                holder.placasTextView.setText(model.placas);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DocumentSnapshot snapshot = getSnapshots().getSnapshot(holder.getAbsoluteAdapterPosition());
                        String idDocumento = snapshot.getId();
                        Intent intent = new Intent(VehiculosActivity.this, VehiculosEditarActivity.class);
                        intent.putExtra("idDocumento",idDocumento);
                        startActivity(intent);
                        finish();
                    }
                });
            }
        };
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private class VehiculosViewHolder extends RecyclerView.ViewHolder{

        private TextView nombreTextView,colorTextView,placasTextView;
        private ImageView fotoVehiculoImageView;
        public VehiculosViewHolder(@NonNull View itemView)
        {
            super(itemView);
            nombreTextView = itemView.findViewById(R.id.nombreTextView);
            colorTextView = itemView.findViewById(R.id.colorTextView);
            placasTextView = itemView.findViewById(R.id.placasTextView);
            fotoVehiculoImageView = itemView.findViewById(R.id.fotoVehiculoImageView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    vehiculoRef.get()
                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                                }
                            });
                }
            });
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(VehiculosActivity.this,PrincipalActivity.class);
        startActivity(intent);
        finish();
    }
}