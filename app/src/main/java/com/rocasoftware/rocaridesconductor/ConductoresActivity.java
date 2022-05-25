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

public class ConductoresActivity extends AppCompatActivity {
    private EditText buscarEditView;
    private RecyclerView recyclerView;
    private Button registroButton;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference conductorRef = db.collection("Conductores");

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
        setContentView(R.layout.activity_conductores);

        registroButton = findViewById(R.id.registroButton);
        recyclerView = findViewById(R.id.conductoresRecyclerView);
        buscarEditView = findViewById(R.id.buscarEditView);


        cargarLista();


        buscarEditView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //cargarListaBuscar();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        registroButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ConductoresActivity.this,ConductoresRegistroActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void cargarLista()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String idUser = user.getUid();
        Query query = conductorRef.whereEqualTo("idManager",idUser);
        FirestoreRecyclerOptions<ConductorModel> options = new FirestoreRecyclerOptions.Builder<ConductorModel>()
                .setQuery(query,ConductorModel.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<ConductorModel, ConductoresViewHolder>(options) {
            @NonNull
            @Override
            public ConductoresViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.conductor_item,parent,false);
                return new ConductoresViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull ConductoresViewHolder holder, int position, @NonNull ConductorModel model) {
                StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                StorageReference photoReference= storageReference.child("documentos/Perfiles/"+model.urlImagenFotoPerfil);

                final long ONE_MEGABYTE = 1024 * 1024;
                photoReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        holder.fotoPerfilImageView.setImageBitmap(bmp);

                    }
                });

                holder.nombreTextView.setText(model.getNombre() + " " + model.getApellido());
                holder.telefonoTextView.setText(model.getTelefono());
                holder.correoTextView.setText(model.getEmail());

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DocumentSnapshot snapshot = getSnapshots().getSnapshot(holder.getAbsoluteAdapterPosition());
                        String idDocumento = snapshot.getId();
                        Intent intent = new Intent(ConductoresActivity.this, ConductoresEditarActivity.class);
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

    private class ConductoresViewHolder extends RecyclerView.ViewHolder{

        private TextView nombreTextView,telefonoTextView,correoTextView;
        private ImageView fotoPerfilImageView;
        public ConductoresViewHolder(@NonNull View itemView)
        {
            super(itemView);
            nombreTextView = itemView.findViewById(R.id.nombreTextView);
            telefonoTextView = itemView.findViewById(R.id.telefonoTextView);
            correoTextView = itemView.findViewById(R.id.correoTextView);
            fotoPerfilImageView = itemView.findViewById(R.id.fotoPerfilImageView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    conductorRef.get()
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
        Intent intent = new Intent(ConductoresActivity.this,PrincipalActivity.class);
        startActivity(intent);
        finish();
    }

}