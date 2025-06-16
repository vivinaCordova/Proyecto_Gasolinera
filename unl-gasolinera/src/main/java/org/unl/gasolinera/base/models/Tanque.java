package org.unl.gasolinera.base.models;

public class Tanque {
    private Integer id;
    private float capacidad;
    private float capacidadTotal;
    private float capacidadMinima;
    private TipoCombustibleEnum tipo;


    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public float getCapacidad() {
        return this.capacidad;
    }
    private String codigo;

    public String getCodigo() {
        return this.codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }


    public void setCapacidad(float capacidad) {
        this.capacidad = capacidad;
    }

    public float getCapacidadTotal() {
        return this.capacidadTotal;
    }

    public void setCapacidadTotal(float capacidadTotal) {
        this.capacidadTotal = capacidadTotal;
    }

    public float getCapacidadMinima() {
        return this.capacidadMinima;
    }

    public void setCapacidadMinima(float capacidadMinima) {
        this.capacidadMinima = capacidadMinima;
    }

    public TipoCombustibleEnum getTipo() {
        return this.tipo;
    }

    public void setTipo(TipoCombustibleEnum tipo) {
        this.tipo = tipo;
    }
    
}
