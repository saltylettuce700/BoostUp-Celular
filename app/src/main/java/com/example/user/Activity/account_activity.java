package com.example.user.Activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.user.Activity.Registro.registro_terminos_activity;
import com.example.user.R;

public class account_activity extends AppCompatActivity {

    Button call_button;
    TextView telefono;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_account);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });



        findViewById(R.id.idioma_section).setOnClickListener(v -> {
            // Acción para idioma (crear un dialog con 2 radios entre ingles y espanol)
            //startActivity(new Intent(this, InventarioActivity.class));
            Toast.makeText(this, "IDIOMA", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.progreso_section).setOnClickListener(v -> {
            // Acción para progreso
            //startActivity(new Intent(this, ReportarFalloActivity.class));
            Toast.makeText(this, "PROGRESO", Toast.LENGTH_SHORT).show();

        });

        findViewById(R.id.terminos_section).setOnClickListener(v -> {
            //terminos y condiciones solo leer activity
            startActivity(new Intent(this, registro_terminos_activity.class));
            Toast.makeText(this, "TERMINOS Y COND", Toast.LENGTH_SHORT).show();
        });

        // Botón "Restablecer"
        findViewById(R.id.ayuda_cliente_section).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCallDialog();
            }
        });

        findViewById(R.id.cerrar_sesion_section).setOnClickListener(v -> {
            // Acción para Cerrar Sesión
            startActivity(new Intent(this, login_activity.class));
            Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show();
        });

        //Botones de arriba

        findViewById(R.id.perfil_section).setOnClickListener(v -> {
            startActivity(new Intent(this, datos_perfil_activity.class));
            Toast.makeText(this, "IR A PERFIL", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.historial_section).setOnClickListener(v -> {
            // Acción para Cerrar Sesión
            startActivity(new Intent(this, historial_activity.class));
            Toast.makeText(this, "HISTORIAL", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.pago_section).setOnClickListener(v -> {
            // Acción para Cerrar Sesión
            //startActivity(new Intent(this, registro_terminos_activity.class));
            Toast.makeText(this, "ABRIR METODOS DE PAGO", Toast.LENGTH_SHORT).show();
        });
    }

    private void showCallDialog() {

        Dialog dialog = new Dialog(this, R.style.BlurBackgroundDialog);

        // Inflar el diseño personalizado
        View dialogView = LayoutInflater.from(this).inflate(R.layout.custom_dialog_call, null);
        dialog.setContentView(dialogView);

        // Configurar botones
        Button btn_call = dialogView.findViewById(R.id.btn_call);
        Button btnCancel = dialogView.findViewById(R.id.btn_cancel);
        TextView telefono = dialogView.findViewById(R.id.TV_numero);

        btn_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Acción de continuar
                dialog.dismiss();
                Toast.makeText(account_activity.this, "llamando", Toast.LENGTH_SHORT).show();

                Intent llamar = new Intent((Intent.ACTION_CALL));

                llamar.setData(Uri.parse("tel:" + telefono.getText().toString()));
                if (ActivityCompat.checkSelfPermission(account_activity.this, Manifest.permission.CALL_PHONE)
                        != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(account_activity.this, new String[] {Manifest.permission.CALL_PHONE},10);
                    return;
                }
                startActivity(llamar);

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(account_activity.this, "Cancelar llamar", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        // Mostrar el diálogo
        dialog.show();


    }
}