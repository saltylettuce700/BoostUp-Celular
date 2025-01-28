package POJO;

public class BebidaDestacada {
    private final String nombre;
    private final String sabor;
    private final int imagenResId;

    public BebidaDestacada(String nombre, String sabor, int imagenResId) {
        this.nombre = nombre;
        this.sabor = sabor;
        this.imagenResId = imagenResId;
    }

    public String getNombre() {
        return nombre;
    }

    public String getSabor() {
        return sabor;
    }

    public int getImagenResId() {
        return imagenResId;
    }
}
