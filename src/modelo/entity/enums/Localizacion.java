package modelo.entity.enums;

public enum Localizacion {
    MADRID("Madrid"),
    BARCELONA("Barcelona"),
    VALENCIA("Valencia"),
    SEVILLA("Sevilla"),
    BILBAO("Bilbao"),
    ZARAGOZA("Zaragoza"),
    MALAGA("Málaga"),
    PARIS("París"),
    LONDRES("Londres"),
    NUEVA_YORK("Nueva York"),
    BERLIN("Berlín"),
    AMSTERDAM("Ámsterdam"),
    LISBOA("Lisboa"),
    ROMA("Roma"),
    OTRA("Otra");

    private final String nombre;

    Localizacion(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    @Override
    public String toString() {
        return nombre;
    }

    public static Localizacion fromNombre(String nombre) {
        for (Localizacion loc : values()) {
            if (loc.getNombre().equalsIgnoreCase(nombre)) {
                return loc;
            }
        }
        return null;
    }
}
