package com.example.user.ConexionBD;

public class Usuario {

    String email;
    String password;
    String username;
    String nombre;
    String apellido;
    String sexo;
    String fec_nacimiento;
    float peso_kg;
    int talla_cm;
    int cintura_cm;
    int cadera_cm;
    int circ_brazo_cm;

    public Usuario(String email, String password, String username, String nombre, String apellido, String sexo,
                   String fec_nacimiento, float peso_kg, int talla_cm, int cintura_cm, int cadera_cm, int circ_brazo_cm) {
        this.email = email;
        this.password = password;
        this.username = username;
        this.nombre = nombre;
        this.apellido = apellido;
        this.sexo = sexo;
        this.fec_nacimiento = fec_nacimiento;
        this.peso_kg = peso_kg;
        this.talla_cm = talla_cm;
        this.cintura_cm = cintura_cm;
        this.cadera_cm = cadera_cm;
        this.circ_brazo_cm = circ_brazo_cm;
    }
}
