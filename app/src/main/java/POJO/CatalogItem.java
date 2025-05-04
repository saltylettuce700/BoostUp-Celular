package POJO;

public class CatalogItem {

    public static final int TYPE_CATEGORY = 0;
    public static final int TYPE_PRODUCT = 1;

    private int type;
    private int id;
    private String title; // Nombre del producto o encabezado de la categoría
    private String description; // Solo para productos
    private String tipoProteinaSaborizante;
    private String tipoProducto;
    //private String imageUrl; // Solo para productos


    public CatalogItem(int type, String title, int id, String description, String tipoProteinaSaborizante, String tipoProducto) {
        this.type = type;
        this.id = id;
        this.title = title;
        this.description = description;
        this.tipoProteinaSaborizante = tipoProteinaSaborizante;
        this.tipoProducto = tipoProducto;
        //this.imageUrl = imageUrl;
    }

    public int getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public int getId() {return id;}

    public String getTipoProducto() {
        return tipoProducto;
    }

    public String getDescription() {
        return description;
    }

    /*public String getImageUrl() {
        return imageUrl;
    }*/

    public String getTipoProteinaSaborizante() {return tipoProteinaSaborizante;}
}
