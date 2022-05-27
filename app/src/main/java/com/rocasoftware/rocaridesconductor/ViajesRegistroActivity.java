package com.rocasoftware.rocaridesconductor;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.type.DateTime;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ViajesRegistroActivity extends AppCompatActivity {
    EditText tituloEditText,fechaSalidaEditText,horaSalidaEditText;
    Button registrarButton;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String idUser = user.getUid();

    private CollectionReference viajeRef = db.collection("Viajes");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viajes_registro);

        tituloEditText = findViewById(R.id.tituloEditText);
        fechaSalidaEditText = findViewById(R.id.fechaSalidaEditText);
        horaSalidaEditText = findViewById(R.id.horaSalidaEditText);
        registrarButton = findViewById(R.id.registrarButton);

        fechaSalidaEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateDialog();
            }
        });

        horaSalidaEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showHourDialog();
            }
        });

        registrarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String fechaSalida = fechaSalidaEditText.getText().toString();
                String horaSalida = horaSalidaEditText.getText().toString();


                Map<String,Object> note = new HashMap<>();
                note.put("fechaHora",getTimeStamp(fechaSalida,horaSalida));


                viajeRef.document("cjOb0Gazdgd8ALr06Cfr").update(note);
            }
        });


    }



    public Date getTimeStamp(String fecha,String hora) { // without parameter argument
        String fechahora = fecha + "T" + hora+"Z";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        try{
            Date date = format.parse(fechahora);
            return date ;
        } catch(Exception e) {
            return new Date();
        }
    }

    private void showHourDialog() {
        Calendar calendar = Calendar.getInstance();
        int hora = calendar.get(Calendar.HOUR);
        int minuto = calendar.get(Calendar.HOUR);

        TimePickerDialog td = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Calendar calendar1 = Calendar.getInstance();
                calendar1.set(Calendar.HOUR,hourOfDay);
                calendar1.set(Calendar.MINUTE,minute);

                CharSequence dateCharSequence = DateFormat.format("HH:mm:ss", calendar1);
                horaSalidaEditText.setText(dateCharSequence);
            }
        },hora,minuto,false);
        td.show();
    }

    private void showDateDialog() {
        Calendar calendar = Calendar.getInstance();

        int año = calendar.get(Calendar.YEAR);
        int mes = calendar.get(Calendar.MONTH);;
        int dia = calendar.get(Calendar.DATE);;

        DatePickerDialog dp = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                Calendar calendar1 = Calendar.getInstance();
                calendar1.set(Calendar.YEAR,year);
                calendar1.set(Calendar.MONTH,month);
                calendar1.set(Calendar.DATE,dayOfMonth);

                CharSequence dateCharSequence = DateFormat.format("yyyy-MM-dd", calendar1);
                fechaSalidaEditText.setText(dateCharSequence);
            }
        },año,mes,dia);
        dp.show();
    }

}