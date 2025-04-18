package com.example.user.Activity.Registro;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import POJO.Persona;
import com.example.user.R;

import java.util.Calendar;

public class registro_medidas_activity extends AppCompatActivity {

    EditText ET_bday;
    Persona Persona= new Persona();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registro_medidas);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //ET_bday=findViewById(R.id.ET_bday);



        findViewById(R.id.btn_next).setOnClickListener(v -> {
            // Acción para siguiente medidas
            startActivity(new Intent(this, registro_alergia_activity.class));
            Toast.makeText(this, "Siguiente ALERGIA", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.btn_back).setOnClickListener(v -> {
            // Acción para atras mail
            startActivity(new Intent(this, registro_name_activity.class));
            Toast.makeText(this, "Atras NAME", Toast.LENGTH_SHORT).show();
        });

        /*ET_bday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Obtener la fecha actual
                Calendar actual = Calendar.getInstance();
                int dia = actual.get(Calendar.DAY_OF_MONTH);
                int mes = actual.get(Calendar.MONTH);
                int an = actual.get(Calendar.YEAR);

                // Crear el DatePickerDialog con la fecha actual como predeterminada
                DatePickerDialog date = new DatePickerDialog(registro_medidas_activity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                                // Guardar los valores en el objeto Persona
                                Persona.dia = dayOfMonth;
                                Persona.mes = month + 1; // Ajustar el mes (DatePicker usa índices basados en 0)
                                Persona.year = year;

                                // Formatear la fecha seleccionada
                                String fecha = Persona.dia + "/" + Persona.mes + "/" + Persona.year;
                                ET_bday.setText(fecha); // Mostrar la fecha en el EditText
                            }
                        }, an, mes, dia); // Fecha predeterminada (actual)

                // Establecer el rango de fechas permitidas
                Calendar minDate = Calendar.getInstance();
                minDate.set(1970, Calendar.JANUARY, 1); // 1 de enero de 1970
                date.getDatePicker().setMinDate(minDate.getTimeInMillis());

                date.getDatePicker().setMaxDate(actual.getTimeInMillis()); // Fecha máxima = fecha actual

                date.show();
            }
        });*/

    }


}