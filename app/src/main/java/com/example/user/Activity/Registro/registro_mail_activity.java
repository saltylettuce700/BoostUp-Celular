package com.example.user.Activity.Registro;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.user.ConexionBD.BD;
import com.example.user.R;

import java.util.ArrayList;

public class registro_mail_activity extends AppCompatActivity {
    EditText correoET, passET, confirmacionPassET;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registro_mail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        correoET = findViewById(R.id.ET_newcorreo);
        passET = findViewById(R.id.ET_newpassword);
        confirmacionPassET = findViewById(R.id.ET_passconfirmation);

        Intent intent1 = getIntent();
        String setEmail = intent1.getStringExtra("email");
        String setPass = intent1.getStringExtra("pass");
        String setNombre = intent1.getStringExtra("nombre");
        String setApellido = intent1.getStringExtra("apellido");
        String setBirthday = intent1.getStringExtra("birthday");
        String setusername = intent1.getStringExtra("username");
        String setsexo = intent1.getStringExtra("sexo");
        int setpeso = intent1.getIntExtra("peso",0);
        int settalla = intent1.getIntExtra("talla",0);
        int setcintura = intent1.getIntExtra("cintura", 0);
        int setcadera = intent1.getIntExtra("cadera", 0);
        int setbrazo = intent1.getIntExtra("brazo", 0);
        ArrayList<Integer> setalergiasSeleccionadas = intent1.getIntegerArrayListExtra("alergiasSeleccionadas");


        correoET.setText(setEmail);
        passET.setText(setPass);
        confirmacionPassET.setText(setPass);

        findViewById(R.id.btn_next).setOnClickListener(v -> {
            // Acción para siguiente nombre

            String email = correoET.getText().toString();
            String password = passET.getText().toString();
            String confpass = confirmacionPassET.getText().toString();

            if (password.isEmpty() || confpass.isEmpty() || email.isEmpty()) {
                Toast.makeText(this, "Campos vacíos, favor de llenarlos", Toast.LENGTH_SHORT).show();
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "El formato del correo no es válido", Toast.LENGTH_SHORT).show();
            }else{
                BD bd = new BD(this);

                bd.comprobarEmailExiste(email, new BD.BooleanCallback() {
                    @Override
                    public void onSuccess(boolean existe) {
                        runOnUiThread(()->{
                            if (existe){
                                Toast.makeText(registro_mail_activity.this, "Correo ya registrado", Toast.LENGTH_SHORT).show();
                            }else{
                                if (validarPassword(password)){
                                    if (confirmarPassword(password, confpass)){
                                        Intent intent = new Intent(registro_mail_activity.this, registro_name_activity.class);
                                        intent.putExtra("email", email);
                                        intent.putExtra("pass", password);
                                        intent.putExtra("nombre", setNombre);
                                        intent.putExtra("apellido", setApellido);
                                        intent.putExtra("birthday", setBirthday);
                                        intent.putExtra("username", setusername);
                                        intent.putExtra("sexo", setsexo);
                                        intent.putExtra("peso", setpeso);
                                        intent.putExtra("talla", settalla);
                                        intent.putExtra("cintura", setcintura);
                                        intent.putExtra("cadera", setcadera);
                                        intent.putExtra("brazo", setbrazo);
                                        intent.putIntegerArrayListExtra("alergiasSeleccionadas", (ArrayList<Integer>) setalergiasSeleccionadas);

                                        startActivity(intent);
                                    }
                                }
                            }
                        });
                    }

                    @Override
                    public void onFailure() {
                        runOnUiThread(() ->
                                Toast.makeText(registro_mail_activity.this, "Error de conexión", Toast.LENGTH_SHORT).show()
                        );
                    }
                });


            }

           /* startActivity(new Intent(this, registro_name_activity.class));
            Toast.makeText(this, "Siguiente nombre", Toast.LENGTH_SHORT).show();*/
        });
    }

    public void irRegistroNameActivity(){

    }

    public boolean validarPassword(String pass){
        boolean tieneMayuscula = !pass.equals(pass.toLowerCase());
        boolean tieneNumero = pass.matches(".*\\d.*");
        boolean tieneLongitudMinima = pass.length() >= 8;

        if (!tieneMayuscula){
            Toast.makeText(this, "Debe tener minimo una mayuscula", Toast.LENGTH_SHORT).show();
        } else if (!tieneNumero) {
            Toast.makeText(this, "Debe tener minimo un numero", Toast.LENGTH_SHORT).show();
        } else if (!tieneLongitudMinima) {
            Toast.makeText(this, "Debe tener minimo 8 caracteres", Toast.LENGTH_SHORT).show();
        }

        return tieneMayuscula && tieneNumero && tieneLongitudMinima;
    }

    public boolean confirmarPassword(String pass, String confPass){
        if (!pass.equals(confPass)){
            Toast.makeText(this, "Confirmación de contraseña incorrecta", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }

    }
}