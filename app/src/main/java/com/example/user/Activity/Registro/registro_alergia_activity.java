package com.example.user.Activity.Registro;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.user.ConexionBD.BD;
import com.example.user.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class registro_alergia_activity extends AppCompatActivity {

    ListView LV_alergenos;
    ArrayAdapter<String> adapter;
    //String [] array_alergenos = {"alergeno1", "alergeno2", "alergeno3", "alergeno4", "alergeno5"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registro_alergia);
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

        Map<String, Integer> alergenoToId = new HashMap<>();

        LV_alergenos = findViewById(R.id.LV_alergenos);
        LV_alergenos.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        //adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice,array_alergenos);
        //LV_alergenos.setAdapter(adapter);


        new Thread(() -> {
            BD bd = new BD(registro_alergia_activity.this); // Ajusta si necesitas contexto
            Map<Integer, String> alergiasMap = bd.getOpcionesAlergias();

            if (alergiasMap != null) {
                List<String> nombresAlergenos = new ArrayList<>();

                for (Map.Entry<Integer, String> entry : alergiasMap.entrySet()) {
                    nombresAlergenos.add(entry.getValue());
                    alergenoToId.put(entry.getValue(), entry.getKey()); // guarda nombre -> id
                }

                runOnUiThread(() -> {
                    adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, nombresAlergenos);
                    LV_alergenos.setAdapter(adapter);

                    if (setalergiasSeleccionadas != null) {
                        for (Integer alergiaId : setalergiasSeleccionadas) {
                            String alergiaNombre = alergiasMap.get(alergiaId);
                            if (alergiaNombre != null) {
                                int position = nombresAlergenos.indexOf(alergiaNombre);
                                LV_alergenos.setItemChecked(position, true);
                            }
                        }
                    }
                });
            } else {
                runOnUiThread(() ->
                        Toast.makeText(this, "Error al obtener los alérgenos", Toast.LENGTH_SHORT).show()
                );
            }
        }).start();

        /*findViewById(R.id.btn_next).setOnClickListener(v -> {
            // Acción para siguiente nombre


        });*/

        findViewById(R.id.btn_back).setOnClickListener(v -> {
            // Acción para atras mail

            Intent intent2 = new Intent(this, registro_medidas_activity.class);
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
            /*startActivity(new Intent(this, registro_medidas_activity.class));
            Toast.makeText(this, "Atras", Toast.LENGTH_SHORT).show();*/
        });

        // Configuración del ListView con soporte para selección múltiple
        /*adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, array_alergenos);
        LV_alergenos.setAdapter(adapter);
        LV_alergenos.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE); // Habilitar selección múltiple*/

        // Configuración del botón para mostrar las opciones seleccionadas
        findViewById(R.id.btn_next).setOnClickListener(v -> {

            // Recoger las alergias seleccionadas
            List<Integer> idsSeleccionados = new ArrayList<>();
            for (int i = 0; i < LV_alergenos.getCount(); i++) {
                if (LV_alergenos.isItemChecked(i)) {
                    String nombre = (String) LV_alergenos.getItemAtPosition(i);
                    Integer id = alergenoToId.get(nombre);
                    if (id != null) {
                        idsSeleccionados.add(id);
                    }
                }
            }
            Intent intent1 = new Intent(this, registro_terminos_activity.class);
            intent1.putExtra("email", setemail);
            intent1.putExtra("pass", setpass);
            intent1.putExtra("nombre", setNombre);
            intent1.putExtra("apellido", setApellido);
            intent1.putExtra("birthday", setBirthday);
            intent1.putExtra("username", setusername);
            intent1.putExtra("sexo", setsexo);
            intent1.putExtra("peso", setpeso);
            intent1.putExtra("talla", settalla);
            intent1.putExtra("cintura", setcintura);
            intent1.putExtra("cadera", setcadera);
            intent1.putExtra("brazo", setbrazo);
            intent1.putIntegerArrayListExtra("alergiasSeleccionadas", (ArrayList<Integer>) idsSeleccionados);
            startActivity(intent1);

            /*StringBuilder selectedItems = new StringBuilder("Selected Items: ");
            for (int i = 0; i < LV_alergenos.getCount(); i++) {
                if (LV_alergenos.isItemChecked(i)) {
                    selectedItems.append(LV_alergenos.getItemAtPosition(i)).append(" ");
                }
            }

            // Mostrar las opciones seleccionadas en un Toast
            Toast.makeText(this, selectedItems.toString(), Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, registro_terminos_activity.class));
            Toast.makeText(this, "Siguiente", Toast.LENGTH_SHORT).show();*/
        });


    }


}