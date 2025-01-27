package POJO;

public class CatalogItem {

    public static final int TYPE_CATEGORY = 0;
    public static final int TYPE_PRODUCT = 1;

    private int type;
    private String title; // Nombre del producto o encabezado de la categoría
    private String description; // Solo para productos
    //private String imageUrl; // Solo para productos


    public CatalogItem(int type, String title, String description) {
        this.type = type;
        this.title = title;
        this.description = description;
        //this.imageUrl = imageUrl;
    }

    public int getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    /*public String getImageUrl() {
        return imageUrl;
    }*/
}
