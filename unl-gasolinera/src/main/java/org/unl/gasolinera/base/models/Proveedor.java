package org.unl.gasolinera.base.models;

public class Proveedor {
    private Integer id;
    private String nombre;
    private String correoElectronico;
    private Integer idPrecioEstablecido;

    public Integer getIdPrecioEstablecido() {
        return this.idPrecioEstablecido;
    }

    public void setIdPrecioEstablecido(Integer idPrecioEstablecido) {
        this.idPrecioEstablecido = idPrecioEstablecido;
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public String getNombre() {
        return this.nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreoElectronico() {
        return this.correoElectronico;
    }

    public void setCorreoElectronico(String correoElectronico) {
        this.correoElectronico = correoElectronico;
    }

}
