package modelo.entity.enums;

public enum Tecnica {
    SPRAY("Spray / Aerosol"),
    STENCIL("Stencil / Plantilla"),
    MURAL("Mural"),
    GRAFFITI("Graffiti"),
    PASTE_UP("Paste-up / Cartel"),
    INSTALACION("Instalación"),
    ESCULTURA("Escultura urbana"),
    PINTURA("Pintura"),
    MIXTA("Técnica mixta");

    private final String descripcion;

    Tecnica(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    @Override
    public String toString() {
        return descripcion;
    }

    public static Tecnica fromDescripcion(String descripcion) {
        for (Tecnica tecnica : values()) {
            if (tecnica.getDescripcion().equals(descripcion)) {
                return tecnica;
            }
        }
        return null;
    }
}
