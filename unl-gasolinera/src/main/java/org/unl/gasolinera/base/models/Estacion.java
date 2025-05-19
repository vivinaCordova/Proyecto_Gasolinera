package org.unl.gasolinera.base.models;

public class Estacion {
    private Integer id;
    private String codigo;
    private EstadoUsoEnum estadoE;

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCodigo() {
        return this.codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public EstadoUsoEnum getEstadoE() {
        return this.estadoE;
    }

    public void setEstadoE(EstadoUsoEnum estadoE) {
        this.estadoE = estadoE;
    }
}
