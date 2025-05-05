package com.example.user.Activity.Registro;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import POJO.Persona;
import com.example.user.R;

import java.util.ArrayList;
import java.util.Calendar;

public class registro_medidas_activity extends AppCompatActivity {

    EditText ET_bday, ET_peso, ET_talla, ET_cintura, ET_cadera, ET_brazo;
    Persona Persona= new Persona();
    RadioButton RB_masculino, RB_femenino;

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

        RB_femenino = findViewById(R.id.btn_f);
        RB_masculino = findViewById(R.id.btn_m);
        ET_peso =findViewById(R.id.ET_peso);
        ET_talla = findViewById(R.id.ET_talla);
        ET_cintura =findViewById(R.id.ET_cintura);
        ET_cadera =findViewById(R.id.ET_cadera);
        ET_brazo =findViewById(R.id.ET_cbrazo);

        //ET_bday=findViewById(R.id.ET_bday);

        Intent intent = getIntent();
        String setemail = intent.getStringExtra("email");
        String setpass =intent.getStringExtra("pass");
        String setNombre = intent.getStringExtra("nombre");
        String setApellido = intent.getStringExtra("apellido");
        String setBirthday = intent.getStringExtra("birthday");
        String setusername = intent.getStringExtra("username");
        String setsexo = intent.getStringExtra("sexo");
        int setpeso = intent.getIntExtra("peso",0);
        int settalla = intent.getIntExtra("talla",0);
        int setcintura = intent.getIntExtra("cintura", 0);
        int setcadera = intent.getIntExtra("cadera", 0);
        int setbrazo = intent.getIntExtra("brazo", 0);
        ArrayList<Integer> setalergiasSeleccionadas = intent.getIntegerArrayListExtra("alergiasSeleccionadas");

        if(setsexo != null){
            if(setsexo.equals("Masculino")){
                RB_masculino.setChecked(true);
                RB_femenino.setChecked(false);
            }else if(setsexo.equals("Femenino")){
                RB_femenino.setChecked(true);
                RB_masculino.setChecked(false);
            } else {
                RB_femenino.setChecked(false);
                RB_masculino.setChecked(false);
            }
        }


        ET_peso.setText(String.valueOf(setpeso));
        ET_talla.setText(String.valueOf(settalla));
        ET_cintura.setText(String.valueOf(setcintura));
        ET_cadera.setText(String.valueOf(setcadera));
        ET_brazo.setText(String.valueOf(setbrazo));



        findViewById(R.id.btn_next).setOnClickListener(v -> {
            // Acción para siguiente medidas
            String sPeso = ET_peso.getText().toString();
            String sTalla = ET_talla.getText().toString();
            String sCintura = ET_cintura.getText().toString();
            String sCadera = ET_cadera.getText().toString();
            String sBrazo = ET_brazo.getText().toString();
            String sexo = "";

            if (RB_femenino.isChecked()) {
                sexo = "Femenino";
            } else if (RB_masculino.isChecked()) {
                sexo = "Masculino";
            }

            if (sPeso.isEmpty() || sTalla.isEmpty() || sCintura.isEmpty() ||
                    sCadera.isEmpty() || sBrazo.isEmpty()) {
                Toast.makeText(this, "Todos los campos deben estar llenos", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!RB_femenino.isChecked() && !RB_masculino.isChecked()) {
                Toast.makeText(this, "Debe seleccionar el sexo", Toast.LENGTH_SHORT).show();
                return;
            }

            int peso = Integer.parseInt(sPeso);
            int talla = Integer.parseInt(sTalla);
            int cintura = Integer.parseInt(sCintura);
            int cadera = Integer.parseInt(sCadera);
            int brazo = Integer.parseInt(sBrazo);


            // Validar rangos
            if (peso < 30 || peso > 200) {
                Toast.makeText(this, "El peso debe estar entre 30 y 200 kg", Toast.LENGTH_SHORT).show();
                return;
            }

            if (talla < 130 || talla > 230) {
                Toast.makeText(this, "La talla debe estar entre 130 y 230 cm", Toast.LENGTH_SHORT).show();
                return;
            }

            if (cintura < 40 || cintura > 150) {
                Toast.makeText(this, "La cintura debe estar entre 40 y 150 cm", Toast.LENGTH_SHORT).show();
                return;
            }

            if (cadera < 40 || cadera > 170) {
                Toast.makeText(this, "La cadera debe estar entre 40 y 170 cm", Toast.LENGTH_SHORT).show();
                return;
            }

            if (brazo < 15 || brazo > 50) {
                Toast.makeText(this, "La circunferencia del brazo debe estar entre 15 y 50 cm", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent1 = new Intent(this, registro_alergia_activity.class);
            intent1.putExtra("email", setemail);
            intent1.putExtra("pass", setpass);
            intent1.putExtra("nombre", setNombre);
            intent1.putExtra("apellido", setApellido);
            intent1.putExtra("birthday", setBirthday);
            intent1.putExtra("username", setusername);
            intent1.putExtra("sexo", sexo);
            intent1.putExtra("peso", peso);
            intent1.putExtra("talla", talla);
            intent1.putExtra("cintura", cintura);
            intent1.putExtra("cadera", cadera);
            intent1.putExtra("brazo", brazo);
            intent1.putIntegerArrayListExtra("alergiasSeleccionadas", (ArrayList<Integer>) setalergiasSeleccionadas);

            startActivity(intent1);

            /*startActivity(new Intent(this, registro_alergia_activity.class));
            Toast.makeText(this, "Siguiente ALERGIA", Toast.LENGTH_SHORT).show();*/
        });

        findViewById(R.id.btn_back).setOnClickListener(v -> {
            // Acción para atras mail
            /*startActivity(new Intent(this, registro_name_activity.class));
            Toast.makeText(this, "Atras NAME", Toast.LENGTH_SHORT).show();*/

            Intent intent2 = new Intent(this, registro_name_activity.class);
            intent2.putExtra("email", setemail);
            intent2.putExtra("pass", setpass);
            intent2.putExtra("nombre", setNombre);
            intent2.putExtra("apellido", setApellido);
            intent2.putExtra("birthday", setBirthday);
            intent2.putExtra("username", setusername);
            intent2.putExtra("sexo", setsexo);
            intent2.putExtra("peso", setpeso);
            intent2.putExtra("talla", settalla);
            intent2.putExtra("cintura", setcintura);
            intent2.putExtra("cadera", setcadera);
            intent2.putExtra("brazo", setbrazo);
            intent2.putIntegerArrayListExtra("alergiasSeleccionadas", (ArrayList<Integer>) setalergiasSeleccionadas);

            startActivity(intent2);
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