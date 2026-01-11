package modelo.entity.enums;

public enum Pais {
    ESPAÑA("España"),
    FRANCIA("Francia"),
    REINO_UNIDO("Reino Unido"),
    ALEMANIA("Alemania"),
    ITALIA("Italia"),
    PORTUGAL("Portugal"),
    ESTADOS_UNIDOS("Estados Unidos"),
    MEXICO("México"),
    ARGENTINA("Argentina"),
    BRASIL("Brasil"),
    COLOMBIA("Colombia"),
    CHILE("Chile"),
    JAPON("Japón"),
    CHINA("China"),
    AUSTRALIA("Australia"),
    CANADA("Canadá"),
    HOLANDA("Holanda"),
    BELGICA("Bélgica"),
    SUECIA("Suecia"),
    OTRO("Otro");

    private final String nombre;

    Pais(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    @Override
    public String toString() {
        return nombre;
    }

    public static Pais fromNombre(String nombre) {
        for (Pais pais : values()) {
            if (pais.getNombre().equalsIgnoreCase(nombre)) {
                return pais;
            }
        }
        return null;
    }
}
