package POJO;

public class CatalogItem {

    public static final int TYPE_CATEGORY = 0;
    public static final int TYPE_PRODUCT = 1;

    private int type;
    private int id;
    private String title; // Nombre del producto o encabezado de la categoría
    private String description; // Solo para productos
    private String tipoProteinaSaborizante;
    private int imageResId;
    // Solo para productos


    public CatalogItem(int type, String title, int id, String description, String tipoProteinaSaborizante,  int imageResId) {
        this.type = type;
        this.id = id;
        this.title = title;
        this.description = description;
        this.tipoProteinaSaborizante = tipoProteinaSaborizante;
        this.imageResId = imageResId;
    }

    public int getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public int getId() {return id;}

    public String getDescription() {
        return description;
    }

    public int getImageResId() {
        return imageResId;
    }

    public String getTipoProteinaSaborizante() {return tipoProteinaSaborizante;}
}
