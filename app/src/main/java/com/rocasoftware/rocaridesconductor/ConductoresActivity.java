package com.rocasoftware.rocaridesconductor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class ConductoresActivity extends AppCompatActivity {
    private EditText buscarEditView;
    private RecyclerView recyclerView;
    private Button registroButton;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference sexoRef = db.collection("Conductores");

    private FirestoreRecyclerAdapter adapter;
    int contSexo;
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


        //cargarLista();


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
}