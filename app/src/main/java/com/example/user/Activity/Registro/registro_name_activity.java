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

import com.example.user.R;

import java.util.ArrayList;
import java.util.Calendar;

import POJO.Persona;

public class registro_name_activity extends AppCompatActivity {

    EditText ET_bday, ET_nombre, ET_apellido, ET_username;
    POJO.Persona Persona= new Persona();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registro_name);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

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


        ET_bday=findViewById(R.id.ET_bdayNAME);
        ET_nombre = findViewById(R.id.ET_nombre);
        ET_apellido = findViewById(R.id.ET_apellido);
        ET_username = findViewById(R.id.ET_username);

        ET_bday.setText(setBirthday);
        ET_nombre.setText(setNombre);
        ET_apellido.setText(setApellido);
        ET_username.setText(setusername);

        findViewById(R.id.btn_next).setOnClickListener(v -> {
            // Acción para siguiente medidas

            String nombre = ET_nombre.getText().toString();
            String apellido = ET_apellido.getText().toString();
            String username = ET_username.getText().toString();
            String birthday = ET_bday.getText().toString();

            if (nombre.isEmpty() || apellido.isEmpty() ||
                    username.isEmpty() || birthday.isEmpty()) {
                Toast.makeText(this, "Todos los campos deben estar llenos", Toast.LENGTH_SHORT).show();
                return;
            }
            int edad = calcularEdad(birthday);
            if (edad < 16 || edad > 99) {
                Toast.makeText(this, "La edad debe estar entre 17 y 99 años", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent1 = new Intent(this, registro_medidas_activity.class);
            intent1.putExtra("email", setemail);
            intent1.putExtra("pass", setpass);
            intent1.putExtra("nombre", nombre);
            intent1.putExtra("apellido", apellido);
            intent1.putExtra("birthday", birthday);
            intent1.putExtra("username", username);
            intent1.putExtra("sexo", setsexo);
            intent1.putExtra("peso", setpeso);
            intent1.putExtra("talla", settalla);
            intent1.putExtra("cintura", setcintura);
            intent1.putExtra("cadera", setcadera);
            intent1.putExtra("brazo", setbrazo);
            intent1.putIntegerArrayListExtra("alergiasSeleccionadas", (ArrayList<Integer>) setalergiasSeleccionadas);

            startActivity(intent1);

            /*startActivity(new Intent(this, registro_medidas_activity.class));
            Toast.makeText(this, "Siguiente MEDIDAS", Toast.LENGTH_SHORT).show();*/
        });

        findViewById(R.id.btn_back).setOnClickListener(v -> {
            // Acción para atras mail
            Intent intent2 = new Intent(this, registro_mail_activity.class);
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
            /*startActivity(new Intent(this, registro_mail_activity.class));
            Toast.makeText(this, "Atras MEDIDAS", Toast.LENGTH_SHORT).show();*/
        });

        ET_bday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Obtener la fecha actual
                Calendar actual = Calendar.getInstance();
                int dia = actual.get(Calendar.DAY_OF_MONTH);
                int mes = actual.get(Calendar.MONTH);
                int an = actual.get(Calendar.YEAR);

                // Crear el DatePickerDialog con la fecha actual como predeterminada
                DatePickerDialog date = new DatePickerDialog(registro_name_activity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                                // Guardar los valores en el objeto Persona
                                Persona.dia = dayOfMonth;
                                Persona.mes = month + 1; // Ajustar el mes (DatePicker usa índices basados en 0)
                                Persona.year = year;

                                // Formatear la fecha seleccionada
                                String diaFormato = String.format("%02d", Persona.dia);
                                String mesFormato = String.format("%02d", Persona.mes);
                                String fecha = Persona.year + "-" + mesFormato + "-" + diaFormato;
                                ET_bday.setText(fecha); // Mostrar la fecha en el EditText
                            }
                        }, an, mes, dia); // Fecha predeterminada (actual)

                // Establecer el rango de fechas permitidas
                Calendar minDate = Calendar.getInstance();
                Calendar maxDate = Calendar.getInstance();
                maxDate.set(actual.get(Calendar.YEAR) - 18, actual.get(Calendar.MONTH), actual.get(Calendar.DAY_OF_MONTH));// 1 de enero de 1970
                //minDate.set(1970, Calendar.JANUARY, 1); // 1 de enero de 1970
                minDate.set(actual.get(Calendar.YEAR) - 99, actual.get(Calendar.MONTH), actual.get(Calendar.DAY_OF_MONTH));

                date.getDatePicker().setMinDate(minDate.getTimeInMillis());

                date.getDatePicker().setMaxDate(maxDate.getTimeInMillis()); // Fecha máxima = fecha actual

                date.show();
            }
        });

    }

    private int calcularEdad(String fechaNacimiento) {
        // Formato esperado: "yyyy-MM-dd"
        String[] partes = fechaNacimiento.split("-");
        int anio = Integer.parseInt(partes[0]);
        int mes = Integer.parseInt(partes[1]);
        int dia = Integer.parseInt(partes[2]);

        Calendar nacimiento = Calendar.getInstance();
        nacimiento.set(anio, mes - 1, dia);

        Calendar hoy = Calendar.getInstance();

        int edad = hoy.get(Calendar.YEAR) - nacimiento.get(Calendar.YEAR);

        // Verificar si aún no ha cumplido años este año
        if (hoy.get(Calendar.MONTH) < nacimiento.get(Calendar.MONTH) ||
                (hoy.get(Calendar.MONTH) == nacimiento.get(Calendar.MONTH) &&
                        hoy.get(Calendar.DAY_OF_MONTH) < nacimiento.get(Calendar.DAY_OF_MONTH))) {
            edad--;
        }

        return edad;
    }
}