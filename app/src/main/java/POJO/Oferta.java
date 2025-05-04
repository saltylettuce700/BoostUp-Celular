package POJO;

public class Oferta {

    private final int imagenResIdOferta;



    private final String textoPromo;

    public Oferta(int imagenResIdOferta, String textoPromo) {

        this.imagenResIdOferta = imagenResIdOferta;
        this.textoPromo = textoPromo;
    }

    public int getImagenResIdOferta() {
        return imagenResIdOferta;
    }

    public String getTextoPromo() {
        return textoPromo;
    }
}
