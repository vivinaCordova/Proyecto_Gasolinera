package org.unl.gasolinera.base.models;

public class Cuenta {
    
    private Integer id;
    private String correo;
    private String clave;
    private Integer id_persona;
    private EstadoEnum estado;

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCorreo() {
        return this.correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getClave() {
        return this.clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public Integer getId_persona() {
        return this.id_persona;
    }

    public void setId_persona(Integer id_persona) {
        this.id_persona = id_persona;
    }

    public EstadoEnum getEstado() {
        return this.estado;
    }

    public void setEstado(EstadoEnum estado) {
        this.estado = estado;
    }


    

}