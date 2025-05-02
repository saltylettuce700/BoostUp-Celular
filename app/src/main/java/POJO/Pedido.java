package POJO;

public class Pedido {

    private final String id;
    private final String nombreBebida;
    private final String sabor;
    private final String proteina;

    public Pedido(String id, String nombreBebida, String sabor, String proteina) {
        this.id = id;
        this.nombreBebida = nombreBebida;
        this.sabor = sabor;
        this.proteina = proteina;
    }

    public String getSabor() {
        return sabor;
    }

    public String getProteina() {
        return proteina;
    }

    public String getNombreBebida() {
        return nombreBebida;
    }

    public String getId() {return id;}
}
