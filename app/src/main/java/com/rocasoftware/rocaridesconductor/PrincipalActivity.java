package com.rocasoftware.rocaridesconductor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class PrincipalActivity extends AppCompatActivity {
    TextView nombreCompletoTextView;
    CardView conductoresCardView,logoutCardView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        nombreCompletoTextView = findViewById(R.id.nombreCompletoTextView);
        conductoresCardView = findViewById(R.id.conductoresCardView);
        logoutCardView = findViewById(R.id.logoutCardView);

        conductoresCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PrincipalActivity.this,ConductoresActivity.class);
                startActivity(intent);
                finish();
            }
        });
        logoutCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(PrincipalActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(PrincipalActivity.this,LoginActivity.class);
        startActivity(intent);
        finish();
    }
}